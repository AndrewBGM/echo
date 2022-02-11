package io.github.andrewbgm.echo

import kotlin.math.*

private class Projection<N>(
  val key: String?,
  val type: Tag.Type<*>,
  var props: Props,
  val parent: Projection<N>? = null,
  val node: N? = null,
) {
  var children = emptyList<Projection<N>>()
  var hooks = emptyList<Hook>()

  val innerNode: N?
    get() = node ?: children.firstNotNullOfOrNull { innerNode }

  val outerNode: N?
    get() = node ?: parent?.outerNode
}

private const val ROOT_TAG_KEY = "ROOT"

private object RootTagType : Tag.Type<Props>

class Reconciler<in T : Tag.Type<P>, in P : Props, N : Any>(
  private val projector: Projector<T, P, N>,
) {
  private val projectionByRoot = mutableMapOf<N, Projection<N>>()

  fun createRoot(
    node: N,
  ): N {
    require(projectionByRoot[node] == null)

    projectionByRoot[node] = Projection(
      key = ROOT_TAG_KEY,
      type = RootTagType,
      props = Props(),
      node = node,
    )

    return node
  }

  fun updateRoot(
    node: N,
    tag: Tag,
  ) {
    val projection = requireNotNull(projectionByRoot[node])

    Echo.update {
      projection.children = reconcileChildren(projection, listOf(tag))
    }
  }

  private fun reconcileChildren(
    parent: Projection<N>,
    children: List<Tag?>,
  ): List<Projection<N>> {
    val newProjections = mutableListOf<Projection<N>>()
    val count = max(parent.children.size, children.size)
    for (i in 0..count) {
      val previousProjection = parent.children.getOrNull(i)
      val nextTag = children.getOrNull(i)

      if (previousProjection == null && nextTag != null) {
        newProjections += appendProjection(parent, nextTag)
      }

      if (previousProjection != null && nextTag == null) {
        removeProjection(previousProjection)
      }

      if (previousProjection != null && nextTag != null) {
        newProjections += if (previousProjection.type == nextTag.type) {
          updateProjection(previousProjection, nextTag)
          previousProjection
        } else replaceProjection(previousProjection, nextTag)
      }
    }

    return newProjections.toList()
  }

  private fun appendProjection(
    parent: Projection<N>,
    tag: Tag,
  ): Projection<N> {
    val newProjection = createProjection(parent, tag)
    if (newProjection.node != null) {
      val parentNode = requireNotNull(parent.outerNode)
      projector.appendChild(parentNode, newProjection.node)
    }

    return newProjection
  }

  private fun replaceProjection(
    projection: Projection<N>,
    tag: Tag,
  ): Projection<N> {
    val newProjection = insertProjection(projection, tag)
    removeProjection(projection)
    return newProjection
  }

  private fun insertProjection(
    beforeProjection: Projection<N>,
    tag: Tag,
  ): Projection<N> {
    val parent = requireNotNull(beforeProjection.parent)
    val newProjection = createProjection(parent, tag)
    if (newProjection.node != null) {
      val parentNode = requireNotNull(parent.outerNode)
      val beforeChildNode = requireNotNull(beforeProjection.innerNode)
      projector.insertChild(parentNode, newProjection.node, beforeChildNode)
    }

    return newProjection
  }

  private fun createProjection(
    parent: Projection<N>,
    tag: Tag,
  ): Projection<N> = when (tag.type) {
    is FragmentTagType -> createFragmentProjection(parent, tag)
    is ViewTagType<*> -> createViewProjection(parent, tag)
    else -> createHostProjection(parent, tag)
  }

  private fun createFragmentProjection(
    parent: Projection<N>,
    tag: Tag,
  ): Projection<N> = buildProjection(
    parent = parent,
    tag = tag,
    children = tag.props.children,
  )

  private fun createHostProjection(
    parent: Projection<N>,
    tag: Tag,
  ): Projection<N> {
    val node = projector.createNode(tag.type as T, tag.props as P)

    return buildProjection(
      parent = parent,
      tag = tag,
      children = tag.props.children,
      node = node,
    )
  }

  private fun createViewProjection(
    parent: Projection<N>,
    tag: Tag,
  ): Projection<N> {
    var child: Tag? = null
    val hooks = Echo.withHooks {
      val type = tag.type as ViewTagType<Props>
      child = type.view.render(tag.props)
    }

    return buildProjection(
      parent = parent,
      tag = tag,
      children = listOf(child),
      hooks = hooks,
    )
  }

  private fun updateProjection(
    projection: Projection<N>,
    tag: Tag,
  ) = when (tag.type) {
    is FragmentTagType -> updateFragmentProjection(projection, tag)
    is ViewTagType<*> -> updateViewProjection(projection, tag)
    else -> updateHostProjection(projection, tag)
  }

  private fun updateFragmentProjection(
    projection: Projection<N>,
    tag: Tag,
  ) {
    projection.props = tag.props
    projection.children = reconcileChildren(projection, tag.props.children)
  }

  private fun updateHostProjection(
    projection: Projection<N>,
    tag: Tag,
  ) {
    if (projection.props != tag.props) {
      val node = requireNotNull(projection.node)
      projector.updateNode(node, projection.props as P, tag.props as P)
    }

    projection.props = tag.props
    projection.children = reconcileChildren(projection, tag.props.children)
  }

  private fun updateViewProjection(
    projection: Projection<N>,
    tag: Tag,
  ) {
    var child: Tag? = null
    val hooks = Echo.withHooks(projection.hooks) {
      val type = tag.type as ViewTagType<Props>
      child = type.view.render(tag.props)
    }

    projection.props = tag.props
    projection.children = reconcileChildren(projection, listOf(child))
    projection.hooks = hooks
  }

  private fun removeProjection(
    projection: Projection<N>,
  ) = when (projection.type) {
    is FragmentTagType -> removeFragmentProjection(projection)
    is ViewTagType<*> -> removeViewProjection(projection)
    else -> removeHostProjection(projection)
  }

  private fun removeFragmentProjection(
    projection: Projection<N>,
  ) {
    projection.children.forEach(::removeProjection)
  }

  private fun removeHostProjection(
    projection: Projection<N>,
  ) {
    val parentNode = requireNotNull(projection.parent?.outerNode)
    val node = requireNotNull(projection.node)

    projection.children.forEach(::removeProjection)
    projector.removeChild(parentNode, node)
  }

  private fun removeViewProjection(
    projection: Projection<N>,
  ) {
    projection.children.forEach(::removeProjection)

    Echo.deferCallback {
      projection.hooks
        .filterIsInstance<EffectHook>()
        .mapNotNull { it.cleanup }
        .forEach { it() }
    }
  }

  private fun buildProjection(
    parent: Projection<N>,
    tag: Tag,
    children: List<Tag?>,
    node: N? = null,
    hooks: List<Hook> = emptyList(),
  ): Projection<N> {
    val projection = Projection(
      key = tag.key,
      type = tag.type,
      props = tag.props,
      parent = parent,
      node = node,
    )

    projection.children = reconcileChildren(projection, children)
    projection.hooks = hooks

    return projection
  }
}

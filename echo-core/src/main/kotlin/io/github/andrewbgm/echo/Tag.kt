package io.github.andrewbgm.echo

typealias TagBody = Tag.Children.() -> Unit

data class Tag(
  val key: String?,
  val type: Type<*>,
  val props: Props,
) {
  @DslMarker
  internal annotation class ChildrenMarker

  @ChildrenMarker
  class Children {
    companion object {
      internal fun <P : Props> wrap(
        props: P,
        body: TagBody?,
      ): P = props.also {
        val children = Children().apply { body?.invoke(this) }
        Props.setChildren(it, children.items.toList())
      }
    }

    private val items = mutableListOf<Tag>()

    operator fun Tag?.unaryPlus() {
      this?.let { items += it }
    }

    operator fun List<Tag?>?.unaryPlus() {
      this?.let { items += it.filterNotNull() }
    }
  }

  interface Type<in P : Props>
}

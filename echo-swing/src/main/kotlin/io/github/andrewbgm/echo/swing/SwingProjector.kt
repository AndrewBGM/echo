package io.github.andrewbgm.echo.swing

import io.github.andrewbgm.echo.*
import javax.swing.*

internal object RootNode : SwingNode<Unit, SwingProps> {
  override val ref = Unit

  override fun appendChild(
    child: Any,
  ) {
    require(child is JFrame)
    child.pack()
    child.setLocationRelativeTo(null)
    child.isVisible = true
  }

  override fun removeChild(
    child: Any,
  ) {
    require(child is JFrame)
    child.dispose()
  }

  override fun insertChild(
    child: Any,
    beforeChild: Any,
  ) {
    require(child is JFrame)
    require(beforeChild is JFrame)

    if (!child.isVisible) {
      appendChild(child)
    }
  }
}

internal object SwingProjector :
  Projector<SwingTagType<SwingProps, SwingNode<*, SwingProps>>, SwingProps, SwingNode<*, SwingProps>> {
  override fun createNode(
    type: SwingTagType<SwingProps, SwingNode<*, SwingProps>>,
    props: SwingProps,
  ): SwingNode<*, SwingProps> = type.createNode(props)

  override fun updateNode(
    node: SwingNode<*, SwingProps>,
    previousProps: SwingProps?,
    nextProps: SwingProps,
  ) = node.update(previousProps, nextProps)

  override fun appendChild(
    parent: SwingNode<*, SwingProps>,
    child: SwingNode<*, SwingProps>,
  ) = parent.appendChild(child.ref)

  override fun removeChild(
    parent: SwingNode<*, SwingProps>,
    child: SwingNode<*, SwingProps>,
  ) = parent.removeChild(child.ref)

  override fun insertChild(
    parent: SwingNode<*, SwingProps>,
    child: SwingNode<*, SwingProps>,
    beforeChild: SwingNode<*, SwingProps>,
  ) = parent.insertChild(child.ref, beforeChild.ref)
}

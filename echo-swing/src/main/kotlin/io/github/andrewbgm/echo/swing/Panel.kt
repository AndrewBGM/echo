package io.github.andrewbgm.echo.swing

import io.github.andrewbgm.echo.*
import java.awt.*
import javax.swing.*

private class PanelProps : SwingProps()

private class PanelNode(
  override val ref: JPanel,
) : SwingNode<JPanel, PanelProps> {
  override fun appendChild(
    child: Any,
  ) {
    require(child is Component)
    ref.add(child)
  }

  override fun removeChild(
    child: Any,
  ) {
    require(child is Component)
    ref.remove(child)
    ref.revalidate()
    ref.repaint()
  }

  override fun insertChild(
    child: Any,
    beforeChild: Any,
  ) {
    require(child is Component)
    require(beforeChild is Component)

    if (ref.components.contains(child)) {
      ref.remove(child)
      ref.revalidate()
      ref.repaint()
    }

    val idx = ref.components.indexOf(beforeChild)
    ref.add(child, idx)
  }
}

private object PanelTagType : SwingTagType<PanelProps, PanelNode> {
  override fun createNode(
    props: PanelProps,
  ): PanelNode = PanelNode(JPanel()).apply {
    update(null, props)
  }
}

fun panel(
  key: String? = null,
  body: TagBody? = null,
): Tag = Echo.createTag(key, PanelTagType, PanelProps(), body)

package io.github.andrewbgm.echo.swing

import io.github.andrewbgm.echo.*
import javax.swing.*

private class MenuBarProps : SwingProps()

private class MenuBarNode(
  override val ref: JMenuBar,
) : SwingNode<JMenuBar, MenuBarProps> {
  override fun appendChild(
    child: Any,
  ) {
    require(child is JMenu)
    ref.add(child)
  }

  override fun removeChild(
    child: Any,
  ) {
    require(child is JMenu)
    ref.remove(child)
  }

  override fun insertChild(
    child: Any,
    beforeChild: Any,
  ) {
    require(child is JMenu)
    require(beforeChild is JMenu)

    if (ref.components.contains(child)) {
      ref.remove(child)
    }

    val idx = ref.components.indexOf(beforeChild)
    ref.add(child, idx)
  }
}

private object MenuBarTagType : SwingTagType<MenuBarProps, MenuBarNode> {
  override fun createNode(
    props: MenuBarProps,
  ): MenuBarNode = MenuBarNode(JMenuBar()).apply {
    update(null, props)
  }
}

fun menuBar(
  key: String? = null,
  body: TagBody? = null,
): Tag = Echo.createTag(key, MenuBarTagType, MenuBarProps(), body)

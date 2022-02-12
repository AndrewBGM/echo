package io.github.andrewbgm.echo.swing

import io.github.andrewbgm.echo.*
import java.util.*
import javax.swing.*

typealias MenuItemOnAction = () -> Unit

private val onActionCallbacks = WeakHashMap<JMenuItem, MenuItemOnAction?>()

private data class MenuItemProps(
  val text: String?,
  val onAction: MenuItemOnAction?,
) : SwingProps()

private class MenuItemNode(
  override val ref: JMenuItem,
) : SwingNode<JMenuItem, MenuItemProps> {
  override fun update(
    previousProps: MenuItemProps?,
    nextProps: MenuItemProps,
  ) = with(ref) {
    text = nextProps.text

    onActionCallbacks[this] = nextProps.onAction
  }
}

private object MenuItemTagType : SwingTagType<MenuItemProps, MenuItemNode> {
  override fun createNode(
    props: MenuItemProps,
  ): MenuItemNode = MenuItemNode(JMenuItem()).apply {
    ref.addActionListener {
      Echo.batchUpdates {
        val callback = onActionCallbacks[ref]
        callback?.invoke()
      }
    }

    update(null, props)
  }
}

fun menuItem(
  key: String? = null,
  text: String? = null,
  onAction: MenuItemOnAction? = null,
): Tag = Echo.createTag(key, MenuItemTagType, MenuItemProps(text, onAction))

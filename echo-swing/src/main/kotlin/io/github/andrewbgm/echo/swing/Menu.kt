package io.github.andrewbgm.echo.swing

import io.github.andrewbgm.echo.*
import java.util.*
import javax.swing.*

typealias MenuOnAction = () -> Unit

private val onActionCallbacks = WeakHashMap<JMenu, MenuOnAction?>()

private data class MenuProps(
  val text: String?,
  val onAction: MenuOnAction?,
) : SwingProps()

private class MenuNode(
  override val ref: JMenu,
) : SwingNode<JMenu, MenuProps> {
  override fun update(
    previousProps: MenuProps?,
    nextProps: MenuProps,
  ) = with(ref) {
    text = nextProps.text

    onActionCallbacks[this] = nextProps.onAction
  }

  override fun appendChild(
    child: Any,
  ) {
    require(child is JMenuItem)
    ref.add(child)
  }

  override fun removeChild(
    child: Any,
  ) {
    require(child is JMenuItem)
    ref.remove(child)
  }

  override fun insertChild(
    child: Any,
    beforeChild: Any,
  ) {
    require(child is JMenuItem)
    require(beforeChild is JMenuItem)

    if (ref.menuComponents.contains(child)) {
      ref.remove(child)
    }

    val idx = ref.menuComponents.indexOf(beforeChild)
    ref.insert(child, idx)
  }
}

private object MenuTagType : SwingTagType<MenuProps, MenuNode> {
  override fun createNode(
    props: MenuProps,
  ): MenuNode = MenuNode(JMenu()).apply {
    ref.addActionListener {
      Echo.batchUpdates {
        val callback = onActionCallbacks[ref]
        callback?.invoke()
      }
    }

    update(null, props)
  }
}

fun menu(
  key: String? = null,
  text: String? = null,
  onAction: MenuOnAction? = null,
  body: TagBody? = null,
): Tag = Echo.createTag(key, MenuTagType, MenuProps(text, onAction), body)

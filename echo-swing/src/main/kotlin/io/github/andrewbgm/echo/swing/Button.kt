package io.github.andrewbgm.echo.swing

import io.github.andrewbgm.echo.*
import java.util.*
import javax.swing.*

typealias ButtonOnAction = () -> Unit

private val onActionCallbacks = WeakHashMap<JButton, ButtonOnAction?>()

private data class ButtonProps(
  val text: String?,
  val onAction: ButtonOnAction?,
) : SwingProps()

private class ButtonNode(
  override val ref: JButton,
) : SwingNode<JButton, ButtonProps> {
  override fun update(
    previousProps: ButtonProps?,
    nextProps: ButtonProps,
  ) = with(ref) {
    text = nextProps.text

    onActionCallbacks[this] = nextProps.onAction
  }
}

private object ButtonTagType : SwingTagType<ButtonProps, ButtonNode> {
  override fun createNode(
    props: ButtonProps,
  ): ButtonNode = ButtonNode(JButton()).apply {
    ref.addActionListener {
      Echo.batchUpdates {
        val callback = onActionCallbacks[ref]
        callback?.invoke()
      }
    }

    update(null, props)
  }
}

fun button(
  key: String? = null,
  text: String? = null,
  onAction: ButtonOnAction? = null,
): Tag = Echo.createTag(key, ButtonTagType, ButtonProps(text, onAction))

package io.github.andrewbgm.echo.swing

import io.github.andrewbgm.echo.*
import java.util.*
import javax.swing.*
import javax.swing.event.*

typealias TextFieldOnChange = (String) -> Unit

private val onChangeCallbacks = WeakHashMap<JTextField, TextFieldOnChange?>()

private data class TextFieldProps(
  val initialValue: String?,
  val columns: Int,
  val onChange: TextFieldOnChange?,
) : SwingProps()

private class TextFieldNode(
  override val ref: JTextField,
) : SwingNode<JTextField, TextFieldProps> {
  override fun update(
    previousProps: TextFieldProps?,
    nextProps: TextFieldProps,
  ) = with(ref) {
    columns = nextProps.columns

    onChangeCallbacks[this] = nextProps.onChange
  }
}

private object TextFieldTagType : SwingTagType<TextFieldProps, TextFieldNode> {
  override fun createNode(
    props: TextFieldProps,
  ): TextFieldNode = TextFieldNode(JTextField()).apply {
    ref.text = props.initialValue
    ref.document.addDocumentListener(object : DocumentListener {
      override fun changedUpdate(
        e: DocumentEvent,
      ) = handleUpdate(e)

      override fun insertUpdate(
        e: DocumentEvent,
      ) = handleUpdate(e)

      override fun removeUpdate(
        e: DocumentEvent,
      ) = handleUpdate(e)

      private fun handleUpdate(
        e: DocumentEvent,
      ) = Echo.batchUpdates {
        val newText = e.document.getText(0, e.document.length)
        val callback = onChangeCallbacks[ref]
        callback?.invoke(newText)
      }
    })

    update(null, props)
  }
}

fun textField(
  key: String? = null,
  initialValue: String? = null,
  columns: Int = 0,
  onChange: TextFieldOnChange? = null,
): Tag = Echo.createTag(key, TextFieldTagType, TextFieldProps(initialValue, columns, onChange))

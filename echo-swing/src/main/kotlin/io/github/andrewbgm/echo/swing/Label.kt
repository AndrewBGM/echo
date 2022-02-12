package io.github.andrewbgm.echo.swing

import io.github.andrewbgm.echo.*
import javax.swing.*

private data class LabelProps(
  val text: String?,
) : SwingProps()

private class LabelNode(
  override val ref: JLabel,
) : SwingNode<JLabel, LabelProps> {
  override fun update(
    previousProps: LabelProps?,
    nextProps: LabelProps,
  ) = with(ref) {
    text = nextProps.text
  }
}

private object LabelTagType : SwingTagType<LabelProps, LabelNode> {
  override fun createNode(
    props: LabelProps,
  ): LabelNode = LabelNode(JLabel()).apply {
    update(null, props)
  }
}

fun label(
  key: String? = null,
  text: String? = null,
): Tag = Echo.createTag(key, LabelTagType, LabelProps(text))

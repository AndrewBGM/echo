package io.github.andrewbgm.echo.swing

import io.github.andrewbgm.echo.*
import java.awt.*
import java.awt.event.*
import java.util.*
import javax.swing.*

typealias FrameOnClose = () -> Unit

private val onCloseCallbacks = WeakHashMap<JFrame, FrameOnClose?>()

private data class FrameProps(
  val title: String?,
  val onClose: FrameOnClose?,
) : SwingProps()

private class FrameNode(
  override val ref: JFrame,
) : SwingNode<JFrame, FrameProps> {
  override fun update(
    previousProps: FrameProps?,
    nextProps: FrameProps,
  ) = with(ref) {
    title = nextProps.title

    onCloseCallbacks[this] = nextProps.onClose
  }

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

private object FrameTagType : SwingTagType<FrameProps, FrameNode> {
  override fun createNode(
    props: FrameProps,
  ): FrameNode = FrameNode(JFrame()).apply {
    ref.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
    ref.addWindowListener(object : WindowAdapter() {
      override fun windowClosing(
        e: WindowEvent?,
      ) = Echo.batchUpdates {
        val callback = onCloseCallbacks[ref]
        callback?.invoke()
      }
    })

    update(null, props)
  }
}

fun frame(
  key: String? = null,
  title: String? = null,
  onClose: FrameOnClose? = null,
  body: TagBody? = null,
): Tag = Echo.createTag(key, FrameTagType, FrameProps(title, onClose), body)

package io.github.andrewbgm.echo.swing.example

import io.github.andrewbgm.echo.*
import io.github.andrewbgm.echo.swing.*
import io.github.andrewbgm.echo.swing.example.views.*

object SwingExample : View<Props> {
  operator fun invoke(
    key: String? = null,
  ): Tag = View.createTag(key, SwingExample)

  override fun render(
    props: Props,
  ): Tag? {
    val (isClosed, setIsClosed) = Echo.useState { false }

    fun handleClose() = setIsClosed { true }

    if (isClosed) {
      return null
    }

    return App(onClose = ::handleClose)
  }
}

fun main() = EchoSwing.startApplication(SwingExample())

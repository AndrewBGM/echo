package io.github.andrewbgm.echo.swing.example.views

import io.github.andrewbgm.echo.*
import io.github.andrewbgm.echo.swing.*

data class AppProps(
  val onClose: () -> Unit,
) : Props()

object App : View<AppProps> {
  operator fun invoke(
    key: String? = null,
    onClose: () -> Unit,
  ): Tag = View.createTag(key, App, AppProps(onClose))

  override fun render(
    props: AppProps,
  ): Tag {
    val (onClose) = props

    return frame(title = "Swing Example", onClose = onClose) {
      +panel {
        +Counter()
      }
    }
  }
}

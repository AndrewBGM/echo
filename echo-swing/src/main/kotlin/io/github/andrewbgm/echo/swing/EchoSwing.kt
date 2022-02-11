package io.github.andrewbgm.echo.swing

import io.github.andrewbgm.echo.*
import javax.swing.*

object EchoSwing {
  private val reconciler = Reconciler(SwingProjector)

  fun startApplication(
    tag: Tag,
  ) {
    SwingUtilities.invokeLater {
      reconciler.updateRoot(reconciler.createRoot(RootNode), tag)
    }
  }
}

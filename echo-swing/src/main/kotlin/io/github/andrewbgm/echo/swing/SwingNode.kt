package io.github.andrewbgm.echo.swing

interface SwingNode<out T : Any, in P : SwingProps> {
  val ref: T

  fun update(
    previousProps: P?,
    nextProps: P,
  ) {
    // NOOP
  }

  fun appendChild(
    child: Any,
  ) {
    // NOOP
  }

  fun removeChild(
    child: Any,
  ) {
    // NOOP
  }

  fun insertChild(
    child: Any,
    beforeChild: Any,
  ) {
    // NOOP
  }
}

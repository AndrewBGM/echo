package io.github.andrewbgm.echo.swing

import io.github.andrewbgm.echo.*

interface SwingTagType<in P : SwingProps, out N : SwingNode<*, P>> : Tag.Type<P> {
  fun createNode(
    props: P,
  ): N
}

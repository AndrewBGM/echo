package io.github.andrewbgm.echo

interface Projector<in T : Tag.Type<P>, in P : Props, N : Any> {
  fun createNode(
    type: T,
    props: P,
  ): N

  fun updateNode(
    node: N,
    previousProps: P?,
    nextProps: P,
  )

  fun appendChild(
    parent: N,
    child: N,
  )

  fun removeChild(
    parent: N,
    child: N,
  )

  fun insertChild(
    parent: N,
    child: N,
    beforeChild: N,
  )
}

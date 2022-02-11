package io.github.andrewbgm.echo

open class Props {
  companion object {
    internal fun setChildren(
      props: Props,
      children: List<Tag>,
    ) {
      props._children = children
    }
  }

  private var _children = emptyList<Tag>()
  val children: List<Tag>
    get() = _children
}

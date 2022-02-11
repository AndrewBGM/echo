package io.github.andrewbgm.echo

internal data class ViewTagType<in P : Props>(
  val view: View<P>,
) : Tag.Type<P>

interface View<in P : Props> {
  companion object {
    fun createTag(
      key: String?,
      view: View<Props>,
      body: TagBody? = null,
    ): Tag = createTag(key, view, Props(), body)

    fun <P : Props> createTag(
      key: String?,
      view: View<P>,
      props: P,
      body: TagBody? = null,
    ): Tag = Echo.createTag(key, ViewTagType(view), props, body)
  }

  fun render(
    props: P,
  ): Tag?
}

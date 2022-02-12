package io.github.andrewbgm.echo

import kotlin.test.*

internal class ViewTest {
  object TestView : View<Props> {
    override fun render(
      props: Props,
    ): Tag? = null
  }

  @Test
  fun `Should create a valid tag`() {
    val key = "TEST"
    val tag = View.createTag(key, TestView)

    assertEquals(key, tag.key)
    assertIs<ViewTagType<Props>>(tag.type)
  }
}

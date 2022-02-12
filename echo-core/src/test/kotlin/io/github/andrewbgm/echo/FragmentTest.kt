package io.github.andrewbgm.echo

import kotlin.test.*

internal class FragmentTest {
  @Test
  fun `Should create a valid tag`() {
    val key = "TEST"
    val tag = fragment(key = key)

    assertEquals(key, tag.key)
    assertIs<FragmentTagType>(tag.type)
  }
}

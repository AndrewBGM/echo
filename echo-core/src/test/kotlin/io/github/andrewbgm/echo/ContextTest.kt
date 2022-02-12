package io.github.andrewbgm.echo

import kotlin.test.*

internal class ContextTest {
  private val testContext = Echo.createContext("")

  @Test
  fun `Should create a valid tag`() {
    val key = "TEST"
    val tag = testContext(key = key, value = "test")

    assertEquals(key, tag.key)
    assertIs<ContextTagType<String>>(tag.type)
    assertIs<ContextProps<String>>(tag.props)
  }
}

package io.github.andrewbgm.echo

import kotlin.test.*

internal class HooksTest {
  @Test
  fun `Should update the context value`() {
    val initialValue = ""
    val hook = ContextHook(initialValue)

    assertEquals(initialValue, hook.value)

    val newValue = "TEST"
    hook.invoke(newValue)
    assertEquals(newValue, hook.value)
  }
}

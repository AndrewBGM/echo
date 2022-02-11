package io.github.andrewbgm.echo.swing.example.views

import io.github.andrewbgm.echo.*
import io.github.andrewbgm.echo.swing.*

data class CounterProps(
  val initialCount: Int,
) : Props()

object Counter : View<CounterProps> {
  operator fun invoke(
    key: String? = null,
    initialCount: Int = 0,
  ): Tag = View.createTag(key, Counter, CounterProps(initialCount))

  override fun render(
    props: CounterProps,
  ): Tag {
    val (initialCount) = props

    val (count, setCount) = Echo.useState { initialCount }

    fun handleClick() = setCount { it + 1 }

    return button(text = "Count: $count", onAction = ::handleClick)
  }
}

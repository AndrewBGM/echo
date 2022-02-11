package io.github.andrewbgm.echo

internal data class ContextProps<T>(
  val value: T,
) : Props()

internal data class ContextTagType<T>(
  val context: Context<T>,
) : Tag.Type<ContextProps<T>>

class Context<T> {
  operator fun invoke(
    key: String? = null,
    value: T,
    body: TagBody? = null,
  ): Tag = Echo.createTag(key, ContextTagType(this), ContextProps(value), body)
}

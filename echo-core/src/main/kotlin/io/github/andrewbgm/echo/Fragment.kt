package io.github.andrewbgm.echo

internal object FragmentTagType : Tag.Type<Props>

fun fragment(
  key: String? = null,
  body: TagBody? = null,
): Tag = Echo.createTag(key, FragmentTagType, body)

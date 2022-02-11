package io.github.andrewbgm.echo

typealias EffectBody = Effect.() -> Unit
typealias EffectCleanupCallback = () -> Unit

interface Effect {
  fun cleanup(
    callback: EffectCleanupCallback,
  )
}

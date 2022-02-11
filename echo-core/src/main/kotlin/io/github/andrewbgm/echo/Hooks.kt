package io.github.andrewbgm.echo

typealias HookBuilder<H> = (H?) -> H

interface Hook

internal class EffectHook(
  var dependencies: Array<*>,
  body: EffectBody,
) : Effect, Hook {
  init {
    Echo.deferCallback {
      body()
    }
  }

  private var _cleanup: EffectCleanupCallback? = null
  val cleanup: EffectCleanupCallback?
    get() = _cleanup

  operator fun invoke(
    newDependencies: Array<*>,
    body: EffectBody,
  ): EffectHook = apply {
    if (!dependencies.contentEquals(newDependencies)) {
      dependencies = newDependencies

      Echo.deferCallback {
        cleanup?.invoke()
        body()
      }
    }
  }

  override fun cleanup(
    callback: EffectCleanupCallback,
  ) {
    _cleanup = callback
  }
}

typealias ReducerInitializer<S> = () -> S
typealias ReducerFunction<S, A> = (S, A) -> S
typealias ReducerDispatch<A> = (A) -> Unit

internal class ReducerHook<S, A>(
  private val reduce: ReducerFunction<S, A>,
  init: ReducerInitializer<S>,
) : Hook {
  var state = init()

  fun dispatch(
    action: A,
  ) = Echo.scheduleUpdate {
    state = reduce(state, action)
  }
}

typealias StateInitializer<T> = ReducerInitializer<T>
typealias StateUpdaterAction<T> = (T) -> T
typealias StateUpdater<T> = ReducerDispatch<StateUpdaterAction<T>>

internal fun <T> stateReducer(
  state: T,
  action: StateUpdaterAction<T>,
): T = action(state)

typealias MemoInitializer<T> = () -> T

internal class MemoHook<T>(
  private var dependencies: Array<*>,
  var value: T,
) : Hook {
  operator fun invoke(
    newDependencies: Array<*>,
    init: MemoInitializer<T>,
  ): MemoHook<T> = apply {
    if (!dependencies.contentEquals(newDependencies)) {
      dependencies = newDependencies
      value = init()
    }
  }
}

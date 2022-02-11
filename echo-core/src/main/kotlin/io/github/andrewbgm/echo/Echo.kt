package io.github.andrewbgm.echo

object Echo {
  private var shouldBatchUpdates = false
  private val deferredCallbacks = mutableListOf<() -> Unit>()
  private val scheduledUpdates = mutableListOf<() -> Unit>()
  private var updater: (() -> Unit)? = null

  private var areHooksAccessible = false
  private var hookIndex = 0
  private var oldHooks = emptyList<Hook>()
  private val newHooks = mutableListOf<Hook>()

  private val valueByContext = mutableMapOf<Context<out Any?>, Any?>()

  fun <T> createContext(
    defaultValue: T,
  ): Context<T> {
    val context = Context<T>()
    valueByContext[context] = defaultValue
    return context
  }

  fun createTag(
    key: String?,
    type: Tag.Type<Props>,
    body: TagBody? = null,
  ): Tag = createTag(key, type, Props(), body)

  fun <P : Props> createTag(
    key: String?,
    type: Tag.Type<P>,
    props: P,
    body: TagBody? = null,
  ): Tag = Tag(key, type, Tag.Children.wrap(props, body))

  fun <H : Hook> useHook(
    builder: HookBuilder<H>,
  ): H {
    require(areHooksAccessible)

    val oldHook = oldHooks.getOrNull(hookIndex)
    val newHook = builder(oldHook as H?)

    hookIndex++
    newHooks += newHook

    return newHook
  }

  fun <T> useContext(
    context: Context<T>,
  ): T {
    val hook = useHook<ContextHook<T>> {
      val value = valueByContext[context] as T
      it?.invoke(value) ?: ContextHook(value)
    }

    return hook.value
  }

  fun useEffect(
    vararg dependencies: Array<*>,
    body: EffectBody,
  ) {
    useHook<EffectHook> {
      it?.invoke(dependencies, body) ?: EffectHook(dependencies, body)
    }
  }

  fun <S, A> useReducer(
    reduce: ReducerFunction<S, A>,
    init: ReducerInitializer<S>,
  ): Pair<S, ReducerDispatch<A>> {
    val hook = useHook<ReducerHook<S, A>> {
      it ?: ReducerHook(reduce, init)
    }

    return Pair(hook.state, hook::dispatch)
  }

  fun <T> useState(
    init: StateInitializer<T>,
  ): Pair<T, StateUpdater<T>> = useReducer(::stateReducer, init)

  fun <T> useRef(
    initialValue: T,
  ): Ref<T> {
    val (ref) = useState { Ref(initialValue) }

    return ref
  }

  fun <T> useMemo(
    vararg dependencies: Any?,
    init: MemoInitializer<T>,
  ): T {
    val hook = useHook<MemoHook<T>> {
      it?.invoke(dependencies, init) ?: MemoHook(dependencies, init())
    }

    return hook.value
  }

  fun scheduleUpdate(
    callback: () -> Unit,
  ) {
    scheduledUpdates += callback

    resolveUpdates()
  }

  fun batchUpdates(
    callback: () -> Unit,
  ) {
    shouldBatchUpdates.let {
      shouldBatchUpdates = true
      callback()
      shouldBatchUpdates = it
    }

    resolveUpdates()
  }

  fun deferCallback(
    callback: () -> Unit,
  ) {
    deferredCallbacks += callback
  }

  internal fun update(
    callback: () -> Unit,
  ) = scheduleUpdate {
    updater = callback
  }

  internal fun <T, R> withContext(
    context: Context<T>,
    value: T,
    callback: () -> R,
  ): R {
    return valueByContext[context].let {
      valueByContext[context] = value
      val result = callback()
      valueByContext[context] = it
      result
    }
  }

  internal fun withHooks(
    previousHooks: List<Hook> = emptyList(),
    callback: () -> Unit,
  ): List<Hook> {
    hookIndex = 0
    oldHooks = previousHooks
    newHooks.clear()

    areHooksAccessible = true
    callback()
    areHooksAccessible = false

    return newHooks.toList()
  }

  private fun resolveUpdates() {
    if (shouldBatchUpdates) {
      return
    }

    while (scheduledUpdates.isNotEmpty()) {
      deferredCallbacks.clear()
      while (scheduledUpdates.isNotEmpty()) {
        scheduledUpdates.removeFirst().invoke()
      }

      updater?.invoke()

      while (deferredCallbacks.isNotEmpty()) {
        deferredCallbacks.removeFirst().invoke()
      }
    }
  }
}

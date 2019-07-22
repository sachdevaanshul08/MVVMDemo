package com.demo.repository.network.paging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

/**
 *  Thread pools for the application.
 *
 */
@Singleton
open class AppExecutors(private val diskIO: Executor) {

    @Inject
    constructor() : this(
        Executors.newSingleThreadExecutor()
    )

    fun diskIOExecutor(): Executor {
        return diskIO
    }

    fun diskIO(task: NewTask, scope: CoroutineScope?) {
        scope?.launch {
            performIoOperations(task)
        }
    }

    private suspend fun performIoOperations(task: NewTask) {
        withContext(Dispatchers.IO) {
            task.executeTask()
        }
    }

    private suspend fun mainThread(task: NewTask) {
        withContext(Dispatchers.Main) {
            task.executeTask()
        }
    }


}

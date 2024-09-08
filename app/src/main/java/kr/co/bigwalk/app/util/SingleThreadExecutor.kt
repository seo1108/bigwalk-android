package kr.co.bigwalk.app.util

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SingleThreadExecutor: Executor {

    private val executorService : ExecutorService = Executors.newSingleThreadExecutor()

    override fun execute(command: Runnable?) {
        executorService.execute(command)
    }

}
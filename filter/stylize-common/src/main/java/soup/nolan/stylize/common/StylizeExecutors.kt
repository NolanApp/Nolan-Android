package soup.nolan.stylize.common

import java.util.concurrent.Executor
import java.util.concurrent.Executors

object StylizeExecutors {

    val serialExecutor: Executor by lazy {
        Executors.newSingleThreadExecutor()
    }
}

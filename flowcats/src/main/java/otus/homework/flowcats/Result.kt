package otus.homework.flowcats

sealed class Result {
    data class Success<T : Any>(val data: T) : Result()
    data class Error(val exception: Throwable) : Result()
}

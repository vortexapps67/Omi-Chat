package com.popchat.util

sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()

    companion object {
        fun <T> success(value: T): Result<T> = Success(value)
        fun <T> failure(exception: Exception): Result<T> = Failure(exception)
    }

    fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(value)
        return this
    }

    fun onFailure(action: (Exception) -> Unit): Result<T> {
        if (this is Failure) action(exception)
        return this
    }

    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }

    fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Success -> transform(value)
        is Failure -> this
    }

    fun getOrElse(default: T): T = when (this) {
        is Success -> value
        is Failure -> default
    }

    fun getOrThrow(): T = when (this) {
        is Success -> value
        is Failure -> throw exception
    }

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure
}
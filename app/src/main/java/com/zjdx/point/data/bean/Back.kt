package com.zjdx.point.data.bean

import java.lang.Exception


/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Back<out T : Any> {

    data class Success<out T : Any>(val data: T) : Back<T>()
    data class Error(val error: SubmitBackModel) : Back<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$error]"
        }
    }
}
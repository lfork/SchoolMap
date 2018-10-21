package com.lfork.amaptest.data

/**
 *
 * Created by 98620 on 2018/8/30.
 */
interface Callback<T> {
    fun succeed(data: T)
    fun failed(log: String)
}
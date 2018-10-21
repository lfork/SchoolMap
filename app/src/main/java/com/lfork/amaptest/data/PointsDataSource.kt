package com.lfork.amaptest.data

/**
 *
 * Created by 98620 on 2018/10/19.
 */
interface PointsDataSource{
    fun getPoints(callBack: Callback<ArrayList<Point>>)
}
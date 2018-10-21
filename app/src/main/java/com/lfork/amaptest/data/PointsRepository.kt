package com.lfork.amaptest.data

import com.lfork.amaptest.data.remote.PointsRemoteDataSource

/**
 *
 * Created by 98620 on 2018/10/19.
 */
object PointsRepository:PointsDataSource  {
    override fun getPoints(callBack: Callback<ArrayList<Point>>) {
        remoteDataSource.getPoints(callBack)
    }

    private val remoteDataSource = PointsRemoteDataSource()

}
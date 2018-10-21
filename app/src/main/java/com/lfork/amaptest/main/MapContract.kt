package com.lfork.amaptest.main

import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.WalkRouteResult
import com.lfork.amaptest.common.BaseView
import com.lfork.amaptest.common.BasePresenter
import com.lfork.amaptest.data.Point


/**
 *
 * Created by 98620 on 2018/10/19.
 */
interface MapContract {
    interface View : BaseView<Presenter> {

        fun showDialog(msg: String)

        fun dismissDialog()

        fun showToast(msg: String)

        fun insertPoints(points: ArrayList<Point>)

        fun clearPoint(point: Point)

        fun clearMap()

        fun showCurrentLocation()


        fun clearRouteSearchResult()

        fun showRouteSearchResult(mWalkRouteResult: WalkRouteResult)
    }

    interface Presenter : BasePresenter {

        val isDataMissing: Boolean

        fun startRouteSearch()

        fun startLocate()

        fun setLocateMode(mode:Int)


        fun startRouteSearch(mStartPoint: LatLonPoint, mEndPoint: LatLonPoint, routeType: Int, mode: Int)
    }
}
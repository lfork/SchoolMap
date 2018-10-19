package com.lfork.amaptest.main

import android.content.Context
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.AMapException
import com.amap.api.services.route.*
import com.lfork.amaptest.R
import com.lfork.amaptest.data.RoutePlanner.ROUTE_TYPE_BUS
import com.lfork.amaptest.data.RoutePlanner.ROUTE_TYPE_CROSSTOWN
import com.lfork.amaptest.data.RoutePlanner.ROUTE_TYPE_DRIVE
import com.lfork.amaptest.data.RoutePlanner.ROUTE_TYPE_WALK
import com.lfork.amaptest.util.ToastUtil

/**
 *
 * Created by 98620 on 2018/10/19.
 */
class MapPresenter(var context: Context?, var view : MapContract.View) :MapContract.Presenter, RouteSearch.OnRouteSearchListener{
    override fun startRouteSearch() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    init {
        view.presenter = this
    }

    override val isDataMissing: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    private lateinit var mWalkRouteResult: WalkRouteResult


    override fun start() {
        getCustomPoints()
    }

    private fun getCustomPoints(){
//        view.insertPoint()
    }

    override fun startRouteSearch(  mStartPoint :LatLonPoint, mEndPoint :LatLonPoint,routeType:Int, mode:Int) {
        val mRouteSearch = RouteSearch(context)
        mRouteSearch.setRouteSearchListener(this)


        view.showDialog("")
        val fromAndTo = RouteSearch.FromAndTo(mStartPoint, mEndPoint)

        if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
//            val query = RouteSearch.BusRouteQuery(fromAndTo, mode,
//                    mCurrentCityName, 0)// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
//            mRouteSearch.calculateBusRouteAsyn(query)// 异步路径规划公交模式查询
        } else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            val query = RouteSearch.DriveRouteQuery(fromAndTo, mode,
                    null, null, "")// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query)// 异步路径规划驾车模式查询
        } else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            val query = RouteSearch.WalkRouteQuery(fromAndTo)
            mRouteSearch.calculateWalkRouteAsyn(query)// 异步路径规划步行模式查询
        } else if (routeType == ROUTE_TYPE_CROSSTOWN) {
//            val fromAndTo_bus = RouteSearch.FromAndTo(
//                    mStartPoint_bus, mEndPoint_bus)
//            val query = RouteSearch.BusRouteQuery(fromAndTo_bus, mode,
//                    "呼和浩特市", 0)// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
//            query.cityd = "农安县"
//            mRouteSearch.calculateBusRouteAsyn(query)// 异步路径规划公交模式查询


        }

    }

    override fun startLocate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLocateMode(mode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {
        Log.d("导航结束", "????")
    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
        Log.d("导航结束", "????")
    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
        Log.d("导航结束", "????")
    }

    override fun onWalkRouteSearched(result: WalkRouteResult?, errorCode: Int) {
        view.dismissDialog()
        Log.d("导航结束", "????")
        view.clearMap()
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size > 0) {
                    view.showRouteSearchResult(result)
                } else if (result.getPaths() == null) {
                    ToastUtil.show(context, R.string.no_result)
                }

            } else {
                ToastUtil.show(context, R.string.no_result)
            }
        } else {
            ToastUtil.showerror(context, errorCode)
        }
    }
}
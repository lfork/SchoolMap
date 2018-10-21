package com.lfork.amaptest.data.remote

import com.lfork.amaptest.data.Callback
import com.lfork.amaptest.data.Point
import com.lfork.amaptest.data.PointsDataSource

/**
 *
 * Created by 98620 on 2018/10/21.
 * *
 */
class PointsRemoteDataSource : PointsDataSource {
    override fun getPoints(callBack: Callback<ArrayList<Point>>) {


        //学校的"矩形"范围 经度 30.5785~30.5803  纬度103.9847~103.9876
        val points = ArrayList<Point>()

        val description = """
                秘书科：028-85966399
                行政科：028-85966367(航空港)
                028-84833333(龙泉校区)
                信息科：028-85966464
                档案室：028-85967300
                总值班室：85966502 85966503（传真)
                """

        points.add(Point(zoomLevel = 1F, name = "$description 1", lat = 30.578534042349501, lng = 103.989714429171996, imageUrl = "aaa"))
        points.add(Point(zoomLevel = 1F, name = "$description 2", lat = 30.578834042349501, lng = 103.989114429171996, imageUrl = "aaa"))
        points.add(Point(zoomLevel = 1F, name = "$description 3", lat = 30.579134042349501, lng = 103.98914429171996, imageUrl = "aaa"))
        points.add(Point(zoomLevel = 1F, name = "$description 4", lat = 30.5794434042349501, lng = 103.99014429171996, imageUrl = "aaa"))
        points.add(Point(zoomLevel = 1F, name = "$description 5", lat = 30.579734042349501, lng = 103.99214429171996, imageUrl = "aaa"))

        points.add(Point(zoomLevel = 1F, name = "$description 6", lat = 30.580034042349501, lng = 103.99014429171996, imageUrl = "aaa"))
        points.add(Point(zoomLevel = 1F, name = "$description 7", lat = 30.580334042349501, lng = 103.9904429171996, imageUrl = "aaa"))
        points.add(Point(zoomLevel = 1F, name = "$description 8", lat = 30.580834042349501, lng = 103.99129171996, imageUrl = "aaa"))
        points.add(Point(zoomLevel = 1F, name = "$description 9", lat = 30.581134042349501, lng = 103.9914429171996, imageUrl = "aaa"))
        points.add(Point(zoomLevel = 1F, name = "$description 10", lat = 30.580634042349501, lng = 103.99129171996, imageUrl = "aaa"))

        callBack.succeed(points)
    }

}
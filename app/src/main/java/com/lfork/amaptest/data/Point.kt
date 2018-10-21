package com.lfork.amaptest.data

import com.amap.api.maps.model.LatLng

/**
 *
 * Created by 98620 on 2018/9/28.
 *
 * 学校的矩形范围 经度 30.5785~30.5803  纬度103.9847~103.9876
 */
data class Point(val zoomLevel:Float, val name:String, val lat:Double, val lng :Double, val imageUrl:String) {

    var position: LatLng = LatLng(lat,lng)

    var imgLocal :String="???"
}
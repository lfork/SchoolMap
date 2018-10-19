package com.lfork.amaptest.data

import com.amap.api.maps.model.LatLng

/**
 *
 * Created by 98620 on 2018/9/28.
 */
data class Point(val zoomLevel:Float, val name:String, val lat:Double, val lng :Double, val imageUrl:String) {
    var position: LatLng = LatLng(30.581340423495053,103.99014429171996)
    var imgLocal :String="???"
}
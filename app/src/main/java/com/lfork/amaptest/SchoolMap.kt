package com.lfork.amaptest

import android.content.Context

import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.CHINESE
import com.amap.api.maps.AMap.ENGLISH
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.services.route.RouteSearch.WalkRouteQuery
import com.amap.api.services.route.RouteSearch.DriveRouteQuery
import com.amap.api.services.route.RouteSearch.BusRouteQuery
import com.amap.api.services.route.RouteSearch
import com.lfork.amaptest.util.ToastUtil


/**
 *
 * @author 98620
 * @date 2018/9/17
 */
object SchoolMap {
    private val TAG = "Test"
    //声明AMapLocationClient类对象

    fun setLocationListener(context: Context, listener: AMapLocationListener) {
        //初始化定位
        var mLocationClient: AMapLocationClient? = null
        mLocationClient = AMapLocationClient(context)


        //设置定位回调监听
        mLocationClient.setLocationListener(listener)

        //启动定位
        mLocationClient.startLocation()
    }

}

fun AMap.setLocationStyle() {
    val myLocationStyle: MyLocationStyle
    myLocationStyle = MyLocationStyle()//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
    myLocationStyle.interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
    setMyLocationStyle(myLocationStyle)//设置定位蓝点的Style
    //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
    setMyLocationEnabled(true)// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
}

fun AMap.setLocationButtonStyle(){
    uiSettings.isMyLocationButtonEnabled = true
}


/**
 * 设置地图底图语言，目前支持中文底图和英文底图
 *
 * @param language AMap.CHINESE 表示中文，即"zh_cn", AMap.ENGLISH 表示英文，即"en"
 * @since 5.5.0
 */
fun AMap.setEnglishMap(){
    setMapLanguage(ENGLISH)
}

/**
 * 设置地图底图语言，目前支持中文底图和英文底图
 *
 * @param language AMap.CHINESE 表示中文，即"zh_cn", AMap.ENGLISH 表示英文，即"en"
 * @since 5.5.0
 */
fun AMap.setChineseMap(){
    setMapLanguage(CHINESE)
}



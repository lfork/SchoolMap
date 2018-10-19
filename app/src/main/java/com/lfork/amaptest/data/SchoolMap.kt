package com.lfork.amaptest.data

import android.content.Context

import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.CHINESE
import com.amap.api.maps.AMap.ENGLISH
import com.amap.api.maps.AMapOptions
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.lfork.amaptest.R
import com.lfork.amaptest.util.Constants


/**
 *
 * @author 98620
 * @date 2018/9/17
 */

fun AMap.setLocationListener(context: Context, listener: AMapLocationListener) {
    //初始化定位
    val mLocationClient = AMapLocationClient(context)
    //设置定位回调监听
    mLocationClient.setLocationListener(listener)
    //启动定位
    mLocationClient.startLocation()
}

fun AMap.setLocationStyle() {
    val myLocationStyle: MyLocationStyle
    myLocationStyle = MyLocationStyle()//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER) //连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
    myLocationStyle.interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
    setMyLocationStyle(myLocationStyle)//设置定位蓝点的Style
    //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
    setMyLocationEnabled(true)// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
}

fun AMap.setUI() {
    uiSettings.isMyLocationButtonEnabled = true
    uiSettings.isZoomControlsEnabled = false
}


/**
 * 设置地图底图语言，目前支持中文底图和英文底图
 *
 * @param language AMap.CHINESE 表示中文，即"zh_cn", AMap.ENGLISH 表示英文，即"en"
 * @since 5.5.0
 */
fun AMap.setEnglishMap() {
    setMapLanguage(ENGLISH)
}

/**
 * 设置地图底图语言，目前支持中文底图和英文底图
 *
 * @param language AMap.CHINESE 表示中文，即"zh_cn", AMap.ENGLISH 表示英文，即"en"
 * @since 5.5.0
 */
fun AMap.setChineseMap() {
    setMapLanguage(CHINESE)
}

const val LEVEL_DEFAULT = 16F
/**
 * 设置默认地图大小
 */
fun AMap.setDefaultMap() {
    val cuitCamera = CameraPosition.Builder()
            // 16 倍放大
            .target(Constants.CUIT).zoom(16F).bearing(0F).tilt(30F).build()
    val aOptions = AMapOptions()
    aOptions.zoomGesturesEnabled(false)// 禁止通过手势缩放地图
    aOptions.scrollGesturesEnabled(false)// 禁止通过手势移动地图
    aOptions.tiltGesturesEnabled(false)// 禁止通过手势倾斜地图
    aOptions.camera(cuitCamera)
    val cameraUpdate = CameraUpdateFactory.newCameraPosition(aOptions.camera)
    moveCamera(cameraUpdate)
}


/**
 * 在地图上设置marker
 */
var customPointsInfo = ArrayList<MarkerOptions>(0);
var customPoints = ArrayList<Marker>(0);

var marker: Marker? = null
var endPoint: LatLng? = null
fun AMap.setMarker(position: LatLng): Boolean {
    endPoint = position
    marker?.remove()
    val markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_end))
            .position(position)
            .draggable(true)
    marker = addMarker(markerOption)
    return marker == null
}


fun AMap.addCustomPosition(point: Point) {
    val markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.headmaster))
            .position(point.position)
            .draggable(true)
            .title(point.name)
    customPointsInfo.add(markerOption)
    customPoints.add(addMarker(markerOption))
}


/**
 * 隐藏自定义点位
 */
fun AMap.hidePoints() {
    customPoints.forEach {
        it.remove()
    }

    customPoints.clear()
}

fun AMap.showPoints() {
    if (customPoints.size > 0) {
        return
    }
    customPointsInfo.forEach {
        customPoints.add(addMarker(it))
    }
}




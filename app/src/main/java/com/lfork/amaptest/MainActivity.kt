package com.lfork.amaptest

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.MapView
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.lfork.amaptest.route.RouteActivity


class MainActivity : AppCompatActivity(), AMapLocationListener, RouteSearch.OnRouteSearchListener {

    private var mStartPoint = LatLonPoint(39.942295, 116.335891)//起点，116.335891,39.942295
    private val mEndPoint = LatLonPoint(39.995576, 116.481288)//终点，116.481288,39.995576

    //异步获取定位结果2
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onLocationChanged(posittion: AMapLocation?) {
        runOnUiThread {
            val df = SimpleDateFormat("HH:mm:ss")
            val address = posittion!!.address
            val time = df.format(Date())
            mStartPoint = LatLonPoint(posittion.latitude, posittion.longitude)

            textView.text = "$address " +
                    "\ntime: $time" +
                    "\n起点:" + mStartPoint+
                    "\n终点:" + mEndPoint
        }
    }

    private lateinit var aMap: AMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions()
        val mapView = findViewById<View>(R.id.map) as MapView
        mapView.onCreate(savedInstanceState)// 此方法必须重写

        initSchoolMap(mapView)

    }

    /**
     *
     */
    private fun initSchoolMap(mapView: MapView) {
        aMap = mapView.map
        aMap.setLocationStyle()
        aMap.setUI()
        aMap.setEnglishMap()
        aMap.setDefaultMap()
        aMap.setOnMapClickListener{
            aMap.setMarker(it)
        }
        btn_language.setOnClickListener {
            btn_language.text = when (btn_language.text) {
                resources.getString(R.string.english) -> {
                    aMap.setEnglishMap()
                    resources.getString(R.string.zh_map)
                }
                resources.getString(R.string.zh_map) -> {
                    aMap.setChineseMap()
                    resources.getString(R.string.english)
                }
                else -> {
                    resources.getString(R.string.english)
                }
            }
        }

        btn_route_search.setOnClickListener {
            val intent = Intent(this, RouteActivity::class.java)
            startActivity(intent)
        }
    }


    private fun requestPermissions() {
        val permissionList = ArrayList<String>()

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE)
        }

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!permissionList.isEmpty()) {
            val permissions = permissionList.toTypedArray()
            ActivityCompat.requestPermissions(this@MainActivity, permissions, 1)
        } else {
            SchoolMap.setLocationListener(applicationContext, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty()) {
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                }
                SchoolMap.setLocationListener(applicationContext, this)
            } else {
                Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private var progDialog: ProgressDialog? = null;

    /**
     * 显示进度框
     */
    private fun showProgressDialog() {
        if (progDialog == null) {
            progDialog = ProgressDialog(this)

        }
        progDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progDialog!!.setIndeterminate(false)
        progDialog!!.setCancelable(true)
        progDialog!!.setMessage("正在搜索")
        progDialog!!.show()

    }

    /**
     * 隐藏进度框
     */
    private fun dissmissProgressDialog() {
        progDialog?.dismiss()
    }


    /**
     * 开始搜索路径规划方案
     */
    fun AMap.searchRouteResult(routeType: Int, mode: Int, mContext: Context) {
//        val mStartPoint: LatLonPoint? = null
//        val mEndPoint: LatLonPoint? = null
//        if (mStartPoint == null) {
//            ToastUtil.show(mContext, "起点未设置")
//            return
//        }
//
//        if (mEndPoint == null) {
//            ToastUtil.show(mContext, "终点未设置")
//        }
//        showProgressDialog()
//        val fromAndTo = RouteSearch.FromAndTo(
//                mStartPoint, mEndPoint)
//        if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
//            val query = RouteSearch.BusRouteQuery(fromAndTo, mode,
//                    mCurrentCityName, 0)// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
//            mRouteSearch.calculateBusRouteAsyn(query)// 异步路径规划公交模式查询
//        } else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
//            val query = RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "")// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
//            mRouteSearch.calculateDriveRouteAsyn(query)// 异步路径规划驾车模式查询
//        } else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
//            val query = RouteSearch.WalkRouteQuery(fromAndTo, mode)
//            mRouteSearch.calculateWalkRouteAsyn(query)// 异步路径规划步行模式查询
//        }
    }

    override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {
    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
    }

    override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
    }

}
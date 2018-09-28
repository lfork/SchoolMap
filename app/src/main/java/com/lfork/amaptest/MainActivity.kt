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
import android.util.Log
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.lfork.amaptest.overlay.WalkRouteOverlay
import com.lfork.amaptest.route.RouteActivity
import com.lfork.amaptest.util.AMapUtil
import com.lfork.amaptest.util.ToastUtil


class MainActivity : AppCompatActivity(), AMapLocationListener, RouteSearch.OnRouteSearchListener {

    private var mStartPoint = LatLonPoint(39.942295, 116.335891)//起点，116.335891,39.942295
    private var mEndPoint = LatLonPoint(39.995576, 116.501288)//终点，116.481288,39.995576
    private lateinit var mRouteSearch:RouteSearch

    private val ROUTE_TYPE_BUS = 1
    private val ROUTE_TYPE_DRIVE = 2
    private val ROUTE_TYPE_WALK = 3
    private val ROUTE_TYPE_CROSSTOWN = 4

    private lateinit var mWalkRouteResult: WalkRouteResult

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
                    "\n起点:" + mStartPoint +
                    "\n终点:" + mEndPoint
        }
    }

    private lateinit var aMap: AMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapView = findViewById<View>(R.id.map) as MapView
        mapView.onCreate(savedInstanceState)// 此方法必须重写
        initSchoolMap(mapView)
        requestPermissions()
        setCustomPoint()
    }

    private fun setCustomPoint() {
        aMap.addCustomPosition(MyPoint())
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
        aMap.setOnMapClickListener {
            aMap.setMarker(it)
        }
        buttonInit()
        mRouteSearch = RouteSearch(this)
        mRouteSearch.setRouteSearchListener(this)
    }

    private fun buttonInit() {
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

        btn_walk_search.setOnClickListener {
           startRouteSearch(ROUTE_TYPE_WALK,RouteSearch.WALK_DEFAULT)
//            showProgressDialog()
//            Thread {
//                Thread.sleep(1000)
//                runOnUiThread {
//                    dissmissProgressDialog()
//                }
//            }.start()
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
            aMap.setLocationListener(applicationContext, this)
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
                aMap.setLocationListener(applicationContext, this)
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
    private fun startRouteSearch(routeType:Int, mode:Int) {
        if (endPoint == null) {
            ToastUtil.show(this, "终点未设置")
            return
        }
        mEndPoint = LatLonPoint(endPoint?.latitude!!, endPoint?.longitude!!)
        showProgressDialog()
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
        dissmissProgressDialog()
        Log.d("导航结束", "????")
        aMap.clear()// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size > 0) {
                    mWalkRouteResult = result
                    val walkPath = mWalkRouteResult.getPaths()
                            .get(0) ?: return
                    val walkRouteOverlay = WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos())
                    walkRouteOverlay.removeFromMap()
                    walkRouteOverlay.addToMap()
                    walkRouteOverlay.zoomToSpan()
//                    mBottomLayout.setVisibility(View.VISIBLE)
                    val dis = walkPath.getDistance().toInt()
                    val dur = walkPath.getDuration().toInt()
                    val des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")"
//                    mRotueTimeDes.setText(des)
//                    mRouteDetailDes.setVisibility(View.GONE)
//                    mBottomLayout.setOnClickListener(View.OnClickListener {
//                        val intent = Intent(mContext,
//                                WalkRouteDetailActivity::class.java)
//                        intent.putExtra("walk_path", walkPath)
//                        intent.putExtra("walk_result",
//                                mWalkRouteResult)
//                        startActivity(intent)
//                    })
                } else if (result.getPaths() == null) {
                    ToastUtil.show(this, R.string.no_result)
                }

            } else {
                ToastUtil.show(this, R.string.no_result)
            }
        } else {
            ToastUtil.showerror(this.applicationContext, errorCode)
        }
    }

}
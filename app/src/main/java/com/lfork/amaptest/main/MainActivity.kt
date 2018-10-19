package com.lfork.amaptest.main

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
import kotlinx.android.synthetic.main.main_act.*
import java.text.SimpleDateFormat
import java.util.*
import android.app.ProgressDialog
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.amap.api.maps.model.CameraPosition
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.lfork.amaptest.*
import com.lfork.amaptest.data.*
import com.lfork.amaptest.data.RoutePlanner.ROUTE_TYPE_WALK
import com.lfork.amaptest.overlay.WalkRouteOverlay
import com.lfork.amaptest.route.RouteActivity
import com.lfork.amaptest.util.*
import kotlinx.android.synthetic.main.main_dashboard_include.*


class MainActivity : AppCompatActivity(), AMapLocationListener, MapContract.View, AMap.OnCameraChangeListener {

    private var mCurrentPosition = LatLonPoint(39.942295, 116.335891)//起点，116.335891,39.942295

    private var mEndPoint = LatLonPoint(39.995576, 116.501288)//终点，116.481288,39.995576

    override var presenter: MapContract.Presenter? = null

    private var progDialog: ProgressDialog? = null;


    /**
     * core map ui object
     */
    private lateinit var aMap: AMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_act)
        val mapView = findViewById<View>(R.id.map) as MapView
        mapView.onCreate(savedInstanceState)// 此方法必须重写

        initSchoolMapUI(mapView)
        buttonInit()
        supportActionBar?.hide()
        MapPresenter(this, this)
        setCustomPoint()
        requestPermissions()
    }

    private fun setCustomPoint() {
        insertPoint(Point(zoomLevel = 1, name = "校长办公室", lat = 30.581340423495053, lng = 103.99014429171996, imageUrl = "aaa"))

        insertPoint(Point(zoomLevel = 1, name = "校长办公室", lat = 30.581340423495053, lng = 103.99014429171996, imageUrl = "aaa"))

    }

    private fun initSchoolMapUI(mapView: MapView) {
        aMap = mapView.map
        aMap.setLocationStyle()
        aMap.setUI()
        aMap.setEnglishMap()
        aMap.setDefaultMap()
        aMap.setOnCameraChangeListener(this)
        aMap.setOnMapClickListener { aMap.setMarker(it) }


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
            startRouteSearch(ROUTE_TYPE_WALK, RouteSearch.WALK_DEFAULT)
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


    /**
     * 显示进度框
     */
    private fun showProgressDialog() {
        if (progDialog == null) {
            progDialog = ProgressDialog(this)

        }
        progDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progDialog!!.isIndeterminate = false
        progDialog!!.setCancelable(true)
        progDialog!!.setMessage("正在搜索")
        progDialog!!.show()

    }

    /**
     * 隐藏进度框
     */
    private fun dismissProgressDialog() {
        progDialog?.dismiss()
    }

    /**
     * 开始搜索路径规划方案
     */
    private fun startRouteSearch(routeType: Int, mode: Int) {
        if (endPoint == null) {
            ToastUtil.show(this, "终点未设置")
            return
        }
        presenter?.startRouteSearch(mCurrentPosition, LatLonPoint(endPoint?.latitude!!, endPoint?.longitude!!), routeType, mode)
    }

    /**
     *   异步获取定位结果
     */
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onLocationChanged(posittion: AMapLocation?) {
        runOnUiThread {
            val df = SimpleDateFormat("HH:mm:ss")
            val address = posittion!!.address
            val time = df.format(Date())
            mCurrentPosition = LatLonPoint(posittion.latitude, posittion.longitude)

            textView.text = "$address " +
                    "\ntime: $time" +
                    "\n起点:" + mCurrentPosition +
                    "\n终点:" + mEndPoint

            Log.e("Test", textView.text.toString())
        }
    }

    override fun clearRouteSearchResult() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showRouteSearchResult(mWalkRouteResult: WalkRouteResult) {
        val walkPath = mWalkRouteResult.paths[0] ?: return
        val walkRouteOverlay = WalkRouteOverlay(
                this, aMap, walkPath,
                mWalkRouteResult.startPos,
                mWalkRouteResult.targetPos)
        walkRouteOverlay.removeFromMap()
        walkRouteOverlay.addToMap()
        walkRouteOverlay.zoomToSpan()
    }


    override fun showDialog(msg: String) {
        if (TextUtils.isEmpty(msg)) {
            showProgressDialog()
        }
    }

    override fun dismissDialog() {
        dismissProgressDialog()
    }

    override fun showToast(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insertPoint(point: Point) {
        aMap.addCustomPosition(point)
    }

    override fun clearPoint(point: Point) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showCurrentLocation() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun clearMap() {
        aMap.clear()// 清理地图上的所有覆盖物
    }

    override fun onCameraChangeFinish(p0: CameraPosition?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCameraChange(cameraPosition: CameraPosition?) {
        if (cameraPosition != null) {
            val level = cameraPosition.zoom;

            if (level < LEVEL_DEFAULT) {
                aMap.hidePoints()
            } else {
                aMap.showPoints()
            }
        }

    }


}
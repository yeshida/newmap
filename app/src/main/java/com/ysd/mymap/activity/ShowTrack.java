package com.ysd.mymap.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.ysd.mymap.R;
import com.ysd.mymap.db.LocalDbAdapter;
import com.ysd.mymap.db.TrackDbAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/1/25.
 */
public class ShowTrack extends Activity {
    private static final String TAG = "ShowTrack";
    //定义菜单需要的常量
    private static final int MENU_NEW = Menu.FIRST + 1;
    private static final int MENU_CON = MENU_NEW + 1;
    private static final int MENU_DEL = MENU_CON + 1;
    private static final int MENU_MAIN = MENU_DEL + 1;
    private TrackDbAdapter mDbHelper;
    private LocalDbAdapter mlcDbHelper;

    private Button mZin;
    private Button mZout;
    private Button mPanN;
    private Button mPanE;
    private Button mPanW;
    private Button mPanS;
    private Button mGps;
    private Button mSat;
    private Button mTraffic;
    private Button mStreetView;
    private String mDefCaption = "";
    private MapView mMapView;
    private BaiduMap baiduMap;
    private LocationManager locationManager;
    private String provider;
    private int track_id;
    private Long rowId;
    private boolean isFirstLocate = true;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.show_track);
        mMapView = (MapView) findViewById(R.id.mv);
        //获取所有位置提供器
        baiduMap= mMapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setTrafficEnabled(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) { //网络提供器
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (provider.contains(LocationManager.GPS_PROVIDER)) { //GPS提供器
            provider = LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(ShowTrack.this, "No location provider to use",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        while(location  == null)
        {
            locationManager.requestLocationUpdates("gps", 60000, 1, locationListener);
        }

        if (location != null) {
            navigateTo(location);
        }
        locationManager.requestLocationUpdates(provider,5000,1,locationListener);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    private void navigateTo(Location location) {
        if (isFirstLocate) {

            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(ll);//设置圆心坐标
            circleOptions.fillColor(0XFFfaa755);//圆的填充颜色
            circleOptions.radius(10);//设置半径
            circleOptions.stroke(new Stroke(5, 0xAA00FF00));//设置边框
            baiduMap.addOverlay(circleOptions);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuider = new MyLocationData.Builder();
        locationBuider.latitude(location.getLatitude());
        locationBuider.longitude(location.getLongitude());
        MyLocationData locationData = locationBuider.build();
        baiduMap.setMyLocationData(locationData);
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(ll);//设置圆心坐标
        circleOptions.fillColor(0XFFfaa755);//圆的填充颜色
        circleOptions.radius(10);//设置半径
        circleOptions.stroke(new Stroke(5, 0xAA00FF00));//设置边框
        baiduMap.addOverlay(circleOptions);
    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                navigateTo(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}

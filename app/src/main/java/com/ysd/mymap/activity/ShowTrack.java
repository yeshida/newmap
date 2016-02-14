package com.ysd.mymap.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
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
    private Button search;
    private CheckBox mTraffic;
    private CheckBox isCompass;
    private CheckBox isOverLook;
    private CheckBox isRotate;
    private CheckBox isScroll;
    private CheckBox isZoom;
    private Button normalview,sat;
    private String mDefCaption = "";
    private MapView mMapView;
    private BaiduMap baiduMap;
    private LocationManager locationManager;
    private String provider;
    private int track_id;
    private Long rowId;
    private boolean isFirstLocate = true;
    private LocationClient locationClient;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.show_track);
        mMapView = (MapView) findViewById(R.id.mv);
        normalview = (Button) findViewById(R.id.normalview);
        sat = (Button) findViewById(R.id.sat);
        mTraffic = (CheckBox) findViewById(R.id.traffic);

        isOverLook = (CheckBox) findViewById(R.id.isOverLook);
        isRotate = (CheckBox) findViewById(R.id.isRotate);
        isScroll = (CheckBox) findViewById(R.id.isScroll);
        isZoom = (CheckBox) findViewById(R.id.isZoom);
        search = (Button) findViewById(R.id.search);

        isOverLook.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        isRotate.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        isScroll.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        isZoom.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        //获取所有位置提供器
        baiduMap= mMapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mTraffic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //开启实时交通图，可以看到交通拥堵情况
                baiduMap.setTrafficEnabled(isChecked);
            }
        });
        sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //卫星地图
                baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            }
        });
        normalview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //普通图
                baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ShowTrack.this, Search.class);
                startActivity(intent);
            }
        });
        // 开启定位图层
/*        baiduMap.setMyLocationEnabled(true);
        BDLocation bdLocation = new BDLocation();
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setCoorType("bd09ll");
        locationClientOption.setOpenGps(true);
        locationClientOption.setScanSpan(1000);
         locationClient = new LocationClient(getApplicationContext(),locationClientOption);
        locationClient.registerLocationListener(new MyLiechtensteiner());
        locationClient.start();*/


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
        mMapView.onResume();
        super.onResume();
    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        locationClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
    private void navigateTo(Location location) {
        if (isFirstLocate) {
           LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            //以动画的方式更新地图的状态
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_geo);
            OverlayOptions option = new MarkerOptions()
                    .position(ll)
                    .icon(mCurrentMarker).zIndex(9).draggable(true);
            baiduMap.addOverlay(option);
            baiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
                public void onMarkerDrag(Marker marker) {
                    //拖拽中
                }

                public void onMarkerDragEnd(Marker marker) {
                  LatLng ll=  marker.getPosition();
                    GeoCoder geoCoder = GeoCoder.newInstance();
                    geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                        // 地理编码查询结果回调函数  地址->坐标
                        @Override
                        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                        }
                        // 反地理编码查询结果回调函数 坐标->地址
                        @Override
                        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                Toast.makeText(ShowTrack.this, "未找到对应的地址", Toast.LENGTH_SHORT).show();
                            } else {
                                //显示具体区县街道门牌的名称
                                ReverseGeoCodeResult.AddressComponent addressComponent = result.getAddressDetail();
                                Toast.makeText(ShowTrack.this,"位置："+addressComponent.district+""+addressComponent.street+""+addressComponent.streetNumber,Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    ReverseGeoCodeOption option = new ReverseGeoCodeOption().location(ll);
                    geoCoder.reverseGeoCode(option);
                    //拖拽结束
                }

                public void onMarkerDragStart(Marker marker) {
                    //开始拖拽
                }
            });
            isFirstLocate = false;
        }
        //开启定位图层
        baiduMap.setMyLocationEnabled(true);
        //定位当前位置
        MyLocationData.Builder locationBuider = new MyLocationData.Builder();
        locationBuider.latitude(location.getLatitude());
        locationBuider.longitude(location.getLongitude());
        MyLocationData locationData = locationBuider.build();
        baiduMap.setMyLocationData(locationData);
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_geo);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,mCurrentMarker);
        baiduMap.setMyLocationConfigeration(config);
        baiduMap.setMyLocationEnabled(false);
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

   class MyLiechtensteiner implements BDLocationListener {

       @Override
       public void onReceiveLocation(BDLocation bdLocation) {
           Log.i("ysd", "====================================");
           Log.i("ysd", "====================================");
           Log.i("ysd", "====================================");
           Log.i("ysd", "====================================");
           Log.i("ysd", "====================================");
           Log.i("ysd", "====================================");
           if (bdLocation == null || mMapView == null)
               return;
           MyLocationData data = new MyLocationData.Builder().accuracy(bdLocation.getRadius()).direction(100).latitude(bdLocation.getLatitude()).longitude(bdLocation.getLongitude()).build();
           baiduMap.setMyLocationData(data);
           if (isFirstLocate) {
               isFirstLocate = false;
               LatLng ll = new LatLng(bdLocation.getLatitude(),
                       bdLocation.getLongitude());
               MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
               baiduMap.animateMapStatus(u);

           }
       }

       @Override
       public void onReceivePoi(BDLocation bdLocation) {
           if (bdLocation == null || mMapView == null)
               return;
       }
   }


    class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String name = buttonView.getText().toString();
            UiSettings uiSettings = baiduMap.getUiSettings();
            if ("是否使用指南针".equals(name)){
                Log.i("======ysd=======","使用指南针");
                uiSettings.setCompassEnabled(isChecked);
            }else if ("是否允许俯视".equals(name)) {
                uiSettings.setOverlookingGesturesEnabled(isChecked);
            }else if ("是否允许旋转".equals(isChecked)) {
                uiSettings.setRotateGesturesEnabled(isChecked);
            }else if ("是否允许拖拽".equals(name)) {
                uiSettings.setScrollGesturesEnabled(isChecked);
            }else if ("是否允许缩放".equals(name)) {
                uiSettings.setZoomGesturesEnabled(isChecked);
            }
            Log.i("======ysd=======", buttonView.getText().toString());
        }
    }
}

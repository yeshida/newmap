package com.ysd.mymap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.ysd.mymap.R;

import java.util.List;

/**
 * Created by Administrator on 2016/2/11.
 */
public class Search extends Activity{
    private MapView mapView;
    private BaiduMap baiduMap;
    private Button button;
    private EditText editText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.seach);
        mapView = (MapView) findViewById(R.id.mv2);
        button = (Button) findViewById(R.id.search);
        editText = (EditText) findViewById(R.id.search_view);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        search();
    }
        /*
        * 搜索功能
        * */
    private void search() {
        PoiSearch poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
             List<PoiInfo> poiInfos = poiResult.getAllPoi();
                PoiInfo poiInfo =  poiInfos.get(1);
               LatLng latLng = poiInfo.location;

                //开启定位图层
                baiduMap.setMyLocationEnabled(true);
                //定位当前位置
                MyLocationData.Builder locationBuider = new MyLocationData.Builder();
                locationBuider.latitude(latLng.latitude);
                locationBuider.longitude(latLng.longitude);
                MyLocationData locationData = locationBuider.build();
                baiduMap.setMyLocationData(locationData);
                BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_geo);
                MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,mCurrentMarker);
                baiduMap.setMyLocationConfigeration(config);
                baiduMap.setMyLocationEnabled(false);

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }
        });
        poiSearch.searchInCity(new PoiCitySearchOption().city("北京"));
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }



}

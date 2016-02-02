package com.ysd.mymap.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
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
    private LocationManager lm;
    private String provider;
    private int track_id;
    private Long rowId;
    private boolean isFirstLocate = true;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.show_track);
        findViews();
        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有空的位置提供器
        List<String> provideerList = lm.getAllProviders();
        if (provideerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (provideerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {


            //当没有可用的位置提供器时，弹出toast提示用户
            Toast.makeText(this, "NO location provider to use", Toast.LENGTH_SHORT).show();
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
        Location location = lm.getLastKnownLocation(provider);
        if (location != null) {
            navigateTo(location);
        }
        // centerOnGPSPosition();
        //  revArgs();
        //  paintLocates();
        // startTrackService();
    }

    private void navigateTo(Location location) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuider = new MyLocationData.Builder();
        locationBuider.latitude(location.getLatitude());
        locationBuider.longitude(location.getLongitude());
        MyLocationData locationData = locationBuider.build();
        baiduMap.setMyLocationData(locationData);
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

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mMapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        if (lm != null) {
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
            lm.removeUpdates(locationListener);
        }
      //  stopTrackService();
    }
    @Override
    public void finish() {
        super.finish();
    }

    private void startTrackService() {
        Intent i = new Intent("com.iceskysl.iTracks.START_TRACK_SERVICE");
        i.putExtra(LocalDbAdapter.TRACKID, track_id);
        startActivity(i);
    }

    private void stopTrackService() {
        stopService(new Intent("om.iceskysl.iTracks.START_TRACK_SERVICE"));
    }

    /*private void paintLocates() {
        mlcDbHelper = new LocalDbAdapter(this);
        try {
            mlcDbHelper.open();
            Cursor mLocatesCursor = mlcDbHelper.getTrackAllLocates(track_id);
            startManagingCursor(mLocatesCursor);
            Resources resources = getResources();
            // Overlay overlays = new MyLocationOverlay(ShowTrack,mLocatesCursor);代码少了一段
            // mMapView.getOverlays().add(overlays);
            mlcDbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void revArgs() {
        Log.d(TAG, "revArgs");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString(TrackDbAdapter.NAME);
            rowId = extras.getLong(TrackDbAdapter.KEY_ROWID);
            track_id = rowId.intValue();
            Log.d(TAG, "rowId=" + rowId);
            if (name != null) {
                setTitle(name);
            }
        }
    }
*/


    private void findViews() {
        Log.d(TAG, "find views");
        mMapView = (MapView) findViewById(R.id.mv);
       // mc = mMapView.getController();
        SharedPreferences settings = getSharedPreferences(Setting.SETTING_INFOS, 0);
        String setting_gps = settings.getString(Setting.SETTING_MAP, "10");
      //  mc.setZoom(Integer.parseInt(setting_gps));

        //set up the button for "Zoom In"
     /*   mZin = (Button) findViewById(R.id.zin);
        mZin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZoomIn();
            }
        });
        //set up the button for "Zoom out"
        mZout = (Button) findViewById(R.id.zout);
        mZout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZoomOut();
            }
        });
        //set up the button for "pan North"
        mPanN = (Button) findViewById(R.id.pann);
        mPanN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panNorth();
            }
        });
        //set up the button for "pan east"
        mPanE = (Button) findViewById(R.id.pane);
        mPanE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panEast();
            }
        });
        //set p the button for "pan west"
        mPanW = (Button) findViewById(R.id.panw);
        mPanW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panWest();
            }
        });
        //set p the button for "pan south"
        mPanS = (Button) findViewById(R.id.pans);
        mPanS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panSouth();
            }
        });
        //set p the button for "GPS"
        mGps = (Button) findViewById(R.id.gps);
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerOnGPSPosition();
            }
        });
        //set up the button for "Satelite toggle"
        mSat = (Button) findViewById(R.id.sat);
        mSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSatellite();
            }
        });
        //set up the button for "traffic toggle"
        mTraffic = (Button) findViewById(R.id.traffic);
        mTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTraffic();
            }
        });
        //set up the button for "street toggle"
        mStreetView = (Button) findViewById(R.id.streetview);
        mStreetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStreetView();
            }
        });
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
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
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);*/
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onkeydown");
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            panWest();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            panEast();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            panNorth();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            panSouth();
            return true;
        }
        return false;
    }

    private void panSouth() {
       // GeoPoint pt = new GeoPoint(mMapView.getMapCenter().getLatitudeE6() - mMapView.getLatitudeSpan() / 4, mMapView.getMapCenter().getLongitudeE6());
      //  mc.setCenter(pt);
    }

    private void panWest() {
       // GeoPoint pt = new GeoPoint(mMapView.getMapCenter().getLatitudeE6(), mMapView.getMapCenter().getLongitudeE6() - mMapView.getLatitudeSpan() / 4);
        //mc.setCenter(pt);
    }

    private void panEast() {
      //  GeoPoint pt = new GeoPoint(mMapView.getMapCenter().getLatitudeE6(), mMapView.getMapCenter().getLongitudeE6() + mMapView.getLatitudeSpan() / 4);
      //  mc.setCenter(pt);
    }

    private void panNorth() {
      //  GeoPoint pt = new GeoPoint(mMapView.getMapCenter().getLatitudeE6() + mMapView.getLatitudeSpan() / 4, mMapView.getMapCenter().getLongitudeE6());
      //  mc.setCenter(pt);
    }

   /* private void ZoomOut() {
        mc.zoomIn();
    }

    private void ZoomIn() {
        mc.zoomOut();
    }
*/
    private void toggleStreetView() {
       // mMapView.setSatellite(false);
     //   mMapView.setStreetView(true);
    //    mMapView.setTraffic(false);
    }

    private void toggleTraffic() {
       // mMapView.setSatellite(false);
     //   mMapView.setStreetView(false);
      //  mMapView.setTraffic(true);
    }

    private void toggleSatellite() {
     //   mMapView.setSatellite(true);
       // mMapView.setStreetView(false);
       // mMapView.setTraffic(false);
    }
/*

    private void centerOnGPSPosition() {
        Log.d(TAG, "centerongpsposition");
        String context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(context);
        // 设置位置服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 取得效果最好的位置服务
        String provider = locationManager.getBestProvider(criteria, true);
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
        locationManager.requestLocationUpdates(provider, 1000, 0, locationListener);
        Location loc=locationManager.getLastKnownLocation(provider);
        while(loc==null){
            locationManager.requestLocationUpdates(provider,1000,0,locationListener);
        }
        mDefPoint = new GeoPoint((int) (loc.getLatitude() * 1000000), (int) (loc.getLongitude() * 1000000));
        mDefCaption ="Im Here.";
        mc.animateTo(mDefPoint);
        mc.setCenter(mDefPoint);
        //show Overlay on map
        MyOverlay mo = new MyOverlay();
        mo.onTap(mDefPoint, mMapView);
        mMapView.getOverlays().add(mo);
    }
//this is used draw an overlay on the map
    private class MyOverlay extends Overlay{

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean b) {
        Log.d(TAG, "MyOverlay::darw..mDefCation=" + mDefCaption);
        super.draw(canvas, mapView, b);
        if (mDefCaption.length() == 0) {
            return;
        }
        Paint p = new Paint();
        int[] scoords = new int[2];
        int sz=5;
        //convert to screen coords
        Point myScreenCoords = new Point();
        mMapView.getProjection().toPixels(mDefPoint, myScreenCoords);
        scoords[0]=myScreenCoords.x;
        scoords[1]=myScreenCoords.y;
        p.setTextSize(14);
        p.setAntiAlias(true);

        int sw = (int) (p.measureText(mDefCaption) + 0.5f);
        int sh=25;
        int sx = scoords[0]-sw/2-5;
        int sy = scoords[1]-sh-sz/2-5;
        RectF rec = new RectF(sx, sy, sx + sw + 10, sy + sh);

        p.setStyle(Paint.Style.FILL);
        p.setARGB(128, 255, 0, 0);

        canvas.drawRoundRect(rec, 5, 5, p);

        p.setStyle(Paint.Style.STROKE);
        p.setARGB(255, 255, 255, 255);
        canvas.drawRoundRect(rec, 5, 5, p);

        canvas.drawText(mDefCaption, sx + 5, sy + sh - 8, p);
        //draw piint body and outer ring

        p.setStyle(Paint.Style.FILL);
        p.setARGB(88, 255, 0, 0);
        p.setStrokeWidth(1);
        RectF spot = new RectF(scoords[0] - sz, scoords[1] + sz, scoords[0]+sz,scoords[1]-sz);
        canvas.drawOval(spot,p);
        p.setARGB(255, 255, 0, 0);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(scoords[0],scoords[1],sz,p);
    }
}

    protected class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            Log.d(TAG, "mylocatioListener::onLocationChanged..");
            if (loc != null) {
                Toast.makeText(getBaseContext(),"Location changed:Lat"+loc.getLatitude()+"Lng:"+loc.getLongitude(),Toast.LENGTH_SHORT).show();
                //set up the overlay controller
                mDefPoint = new GeoPoint((int)(loc.getLatitude() * 1000000),(int)(loc.getLongitude()*1000000));
                mc.animateTo(mDefPoint);
                mc.setCenter(mDefPoint);
                //show on the map.
                mDefCaption = "Lat:" + loc.getLatitude() + ",Lng:" + loc.getLongitude();
                MyOverlay mo = new MyOverlay();
                mo.onTap(mDefPoint, mMapView);
                mMapView.getOverlays().add(mo);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getBaseContext(), "ProviderDisable", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(), "onProviderEnabled,provider:"+provider, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }
    //初始化菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_CON, 0, "继续").setAlphabeticShortcut('C');
        menu.add(0, MENU_DEL, 0, "").setAlphabeticShortcut('D');
        menu.add(0, MENU_NEW, 0, "新建").setAlphabeticShortcut('N');
        menu.add(0, MENU_MAIN, 0, "主页").setAlphabeticShortcut('M');
        return true;
    }
    //当一个菜单被选中的时候调用

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case MENU_NEW:
                intent.setClass(ShowTrack.this, NewTrack.class);
                startActivity(intent);
                return true;
            case MENU_CON:
                //TODO:继续跟踪选择的记录
                startTrackService();
                return true;
            case MENU_DEL:
                mDbHelper = new TrackDbAdapter(this);
                try {
                    mDbHelper.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (mDbHelper.deleteTrack(rowId)) {
                    mDbHelper.close();
                    intent.setClass(ShowTrack.this, ITracks.class);
                    startActivity(intent);
                } else {
                    mDbHelper.close();
                }
                return true;
            case MENU_MAIN:
                intent.setClass(ShowTrack.this, ITracks.class);
                startActivity(intent);
                break;
        }
        return true;
    }
*/

}

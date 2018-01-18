package murmur.transmission;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;

import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.sql.Time;

public class MainActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private TextureMapView mMapView;// 百度地图控件
    private BaiduMap mBaiduMap;  // 百度地图对象
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private MyLocationConfiguration.LocationMode mCurrentMode= MyLocationConfiguration.LocationMode.FOLLOWING;
    private Button bt1 ;
    private Button bt2 ;
    private Button bt3 ;
    private String loc;
    private String shijian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        StartDialog dialog = new StartDialog(this);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Toast.makeText(MainActivity.this, "应用开始页面关闭", Toast.LENGTH_SHORT).show();
            }
        });
        init();
        initLocation();
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        mLocationClient.start();
        mLocationClient.requestLocation();
        Log.d("baidulocation", "baidulocationshow");
        dbHelper = new MyDatabaseHelper(getApplicationContext());


    }

    protected void initLocation() {
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，设置定位模式，默认高精度
//LocationMode.Hight_Accuracy：高精度；
//LocationMode. Battery_Saving：低功耗；
//LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
//可选，设置返回经纬度坐标类型，默认gcj02
//gcj02：国测局坐标；
//bd09ll：百度经纬度坐标；
//bd09：百度墨卡托坐标；
//海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(1000);
//可选，设置发起定位请求的间隔，int类型，单位ms
//如果设置为0，则代表单次定位，即仅定位一次，默认为0
//如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
//可选，设置是否使用gps，默认false
//使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
//可选，定位SDK内部是一个service，并放到了独立进程。
//设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
//可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
//可选，7.2版本新增能力
//如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(true);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

    /**
     * 初始化方法
     */
    private void init() {
        mMapView = (TextureMapView) findViewById(R.id.mTexturemap);
//普通地图
        Log.d("baidulocation", "baidulocationinit");
        bt1 = (Button) findViewById(R.id.bt1);
        bt2 = (Button) findViewById(R.id.bt2);
        bt3 = (Button) findViewById(R.id.bt3);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Data.class);
                startActivity(intent);

            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,shoujijian.class);
                startActivity(intent);

            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,yizhan.class);
                startActivity(intent);

            }
        });
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
    }
    public void show(BDLocation location){
// 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();





// 设置定位数据
        mBaiduMap.setMyLocationData(locData);

// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_geo);
        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
        mBaiduMap.setMyLocationConfiguration(config);
    }
    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            show(location);
            if (location == null) {
                return;
            }
            Log.d("hlhlocation", "onReceiveLocation: location"+location);
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                    sb.append("time : ");
                    /**
                     * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                     * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                     */
                    sb.append(location.getTime());
                    shijian=location.getTime();
                    sb.append("\nlocType : ");// 定位类型
                    sb.append(location.getLocType());
                    sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                    sb.append(location.getLocTypeDescription());
                    sb.append("\nlatitude : ");// 纬度
                    sb.append(location.getLatitude());
                    sb.append("\nlontitude : ");// 经度
                    sb.append(location.getLongitude());
                BdMapUtils.reverseGeoParse(location.getLongitude(), location.getLatitude(), new OnGetGeoCoderResultListener() {
                        @Override
                        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                        }

                        @Override
                        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                // 没有检测到结果
                                Log.d("经纬度转换地址","抱歉，未能找到结果!");
                            }else{////得到结果后处理方法
                                Log.d("经纬度转换地址",result.getAddress());
                                loc=result.getAddress();
                                SQLiteDatabase db =dbHelper.getWritableDatabase();
                                ContentValues cv=new ContentValues();
                                cv.put("ID","741741");
                                cv.put("TIME", shijian);
                                cv.put("LOCATION",loc);
                                db.insert("Location",null,cv);
                                db.disableWriteAheadLogging();
                            }
                        }

                    });

                    sb.append("\nradius : ");// 半径
                    sb.append(location.getRadius());
                    sb.append("\nlocationdescribe: ");
                    sb.append(location.getLocationDescribe());// 位置语义化信息
//                    sb.append("\nPoi: ");// POI信息
//                    if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
//                        for (int i = 0; i < location.getPoiList().size(); i++) {
//                            Poi poi = (Poi) location.getPoiList().get(i);
//                            sb.append(poi.getName() + ";");
//                        }
//                    }
                    if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                        sb.append("\nspeed : ");
                        sb.append(location.getSpeed());// 速度 单位：km/h
                        sb.append("\nsatellite : ");
                        sb.append(location.getSatelliteNumber());// 卫星数目
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 海拔高度 单位：米
                        sb.append("\ngps status : ");
                        sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                        sb.append("\ndescribe : ");
                        sb.append("gps定位成功");
                    } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                        // 运营商信息
                        if (location.hasAltitude()) {// *****如果有海拔高度*****
                            sb.append("\nheight : ");
                            sb.append(location.getAltitude());// 单位：米
                        }
                        sb.append("\noperationers : ");// 运营商信息
                        sb.append(location.getOperators());
                        sb.append("\ndescribe : ");
                        sb.append("网络定位成功");
                    } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                        sb.append("\ndescribe : ");
                        sb.append("离线定位成功，离线定位结果也是有效的");
                    } else if (location.getLocType() == BDLocation.TypeServerError) {
                        sb.append("\ndescribe : ");
                        sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                    } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                        sb.append("\ndescribe : ");
                        sb.append("网络不同导致定位失败，请检查网络是否通畅");
                    } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                        sb.append("\ndescribe : ");
                        sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    }
                    Log.d("hlhlocal", "onReceiveLocation: localpostion" + sb.toString());
                }
            }

        }

    @Override
    protected void onStop() {
        super.onStop();
        //停止定位
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
        mMapView=null;
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        db.close();
    }

}

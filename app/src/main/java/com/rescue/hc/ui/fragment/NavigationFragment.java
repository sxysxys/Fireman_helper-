package com.rescue.hc.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.StringUtils;
import com.rescue.hc.R;
import com.rescue.hc.bean.ReccoData;
import com.rescue.hc.event.EventMessage;
import com.rescue.hc.lib.component.EventTag;
import com.rescue.hc.lib.util.ConvertUtils;
import com.rescue.hc.lib.util.GpsUtil;
import com.rescue.hc.lib.util.LngLonUtil;
import com.rescue.hc.lib.util.ToastUtils;
import com.rescue.hc.ui.activity.MainActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import io.reactivex.functions.Consumer;


@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class NavigationFragment extends BaseFragment implements SensorEventListener2 {

	private Unbinder unbinder;

	@BindView(R.id.bmapView)
	MapView mMapView;//地图控件

	@BindView(R.id.btn_model)
	Button mBtnModel;
	@BindView(R.id.cb_satellite)
	CheckBox mCbSatellite;
	@BindView(R.id.cb_road_condition)
	CheckBox mRoadCondition;
	@BindView(R.id.rl_navigation)
	RelativeLayout mRlNavigation;
	@BindView(R.id.empty_gps_layout)
	View mEmptyGpsLayout;

	@BindView(R.id.btn_test)
	Button btnTest;

	@BindView(R.id.btn_change)
	Button btnChange;

	private BaiduMap mAMap;//AMap地图对象
	private LocationClient mLocationClient = null;
	private LocationClientOption mLocationOption = null;
	private boolean isFirstLoc = true;
	private MyLocationConfiguration mMyLocationStyle;//定位蓝点
	//private UiSettings mUiSettings;//控件设置

	private static final String TAG = NavigationFragment.class.getName();

	private static final int REQUEST_CODE_LOCATION_SETTINGS = 0;
	private static final int WHAT_GPS_CHANGE = 0;
	private static final int WHAT_SHOW_ALL = 1;

	// 是否跳转到了系统设置页面
	private boolean isOpenSetting = false;
	private final MyHandler mHandler = new MyHandler(this);

	// 位置管理器
	private LocationManager mLocationManager;

	// 传感器相关
	private SensorManager mSensorManager;
	// 地磁传感器
	private Sensor mMagneticSensor;
	// 加速度传感器
	private Sensor mAccelerometerSensor;

	private float[] mValues, mAccelerometerValues, mMagneticFieldValues;
	private float[] mR;
	private List<Marker> mMarkers = new ArrayList<>();
	private int mCurrentDirection;
	private double mLastX;

	MyLocationListener locationListener=null;
	private LinearLayout mBdLinearLayout=null;
	private InfoWindow mInfoWindow=null;

	String[] permissions = new String[]{
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.ACCESS_FINE_LOCATION
	};

	private void requestPermission() {
		RxPermissions rxPermissions = new RxPermissions(this);
		rxPermissions
				.request(permissions)
				.subscribe(new Consumer<Boolean>() {
					@Override
					public void accept(Boolean aBoolean) throws Exception {
						if (!aBoolean) {
							com.blankj.utilcode.util.ToastUtils.showShort("权限禁止");
						}
					}
				});
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_navigation, null);
		mBdLinearLayout=(LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.info_window_display, null);
		unbinder = ButterKnife.bind(this, view);
		mMapView.onCreate(getActivity(),savedInstanceState);
		requestPermission();
		initView();
		initMap();
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		startNavigate();
		//test();
	}

	private void initView() {

		initData();
	}

	private void initData() {
		mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		initSensor();
		//test();
	}

	// 初始化传感器
	private void initSensor() {

		mValues = new float[3]; // 保存最终的结果
		mAccelerometerValues = new float[3]; // 保存地磁传感器的值
		mMagneticFieldValues = new float[3]; // 保存加速度传感器的值
		mR = new float[9];

		// 获取SensorManager
		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		// 获取Sensor
		if (mSensorManager != null) {
			mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}

	}


	private void initMap() {
		mMapView.setLogoPosition(LogoPosition.logoPostionRightBottom);
		mMapView.showScaleControl(true);
		mMapView.showZoomControls(true);

		mAMap = mMapView.getMap();
		//控件手势
		mAMap.setIndoorEnable(true);//开启室内地图

		mAMap.getUiSettings().setZoomGesturesEnabled(true);//缩放
		mAMap.getUiSettings().setCompassEnabled(true);//指南针
		mAMap.getUiSettings().setScrollGesturesEnabled(true);
		mAMap.getUiSettings().setRotateGesturesEnabled(false);
		mAMap.setMaxAndMinZoomLevel(8,24);

		mAMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {

//				ReccoData reccoData=new ReccoData();
//				reccoData.setDevEui("00000002");
//				reccoData.setReccoId("0002");
//				reccoData.setLatitude(marker.getPosition().latitude);
//				reccoData.setLongitude(marker.getPosition().longitude);
//				reccoData.setAlarmState((byte) 3);
//				reccoData.setGesture((byte) 3);
//				reccoData.setTemperature(25.0f);
//				createInfoWindow(mBdLinearLayout,reccoData);
//				InfoWindow infoWindow = marker.getInfoWindow();
//				if(infoWindow!=null)
//				{
//					infoWindow.setBitmapDescriptor(BitmapDescriptorFactory.fromView(mBdLinearLayout));
//					marker.showInfoWindow(infoWindow);
//				}



				ReccoData reccoData=(ReccoData) marker.getExtraInfo().getSerializable("reccoData");
				if(reccoData!=null)
				{
					marker.showInfoWindow(marker.getInfoWindow());
					return true;
				}
				return false;
			}
		});

		mAMap.setMyLocationEnabled(true);
		initLocation();
	}

	private void initLocation() {
		mLocationClient = new LocationClient(this.getActivity());
		mLocationOption = getDefaultOption();
		mLocationClient.setLocOption(mLocationOption);
		locationListener=new MyLocationListener();
		mLocationClient.registerLocationListener(locationListener);
		mLocationClient.start();
	}

	private LocationClientOption getDefaultOption() {
		LocationClientOption mOption = new LocationClientOption();
		mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
		mOption.setScanSpan(2000);//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
		mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
		mOption.setNeedDeviceDirect(true);//可选，设置是否需要设备方向结果
		mOption.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		mOption.setIsNeedLocationDescribe(false);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		mOption.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		mOption.setOpenGps(true);//可选，默认false，设置是否开启Gps定位
		mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
		return mOption;
	}

	private final ContentObserver mGpsMonitor = new ContentObserver(null) {
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			boolean enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			// 判断GPS是否打开了
			if (isOpenSetting) {
				return;
			}
			Message message = new Message();
			message.what = WHAT_GPS_CHANGE;
			message.obj = enabled;
			mHandler.sendMessage(message);
		}
	};


	@Override
	public void onFlushCompleted(Sensor sensor) {

	}

	static class MyHandler extends Handler {
		private final WeakReference<NavigationFragment> navigationFragmentWeakReference;

		MyHandler(NavigationFragment navigationFragment) {
			navigationFragmentWeakReference = new WeakReference<>(navigationFragment);
		}

		@Override
		public void handleMessage(Message msg) {
			NavigationFragment fragment = navigationFragmentWeakReference.get();
			if (fragment != null) {
				switch (msg.what) {
					case WHAT_GPS_CHANGE:
						boolean isGpsOpen = (boolean) msg.obj;
						if (isGpsOpen) {
							fragment.showMap();
						} else {
							fragment.hideMap();
						}
						break;
					default:
						break;
				}
			}
		}
	}


	@Subscribe(threadMode = ThreadMode.MainThread)
	public void onEventMainThread(EventMessage message) throws InterruptedException {
		//ToastUtils.normal(this,String.valueOf(message.getCode()),Toast.LENGTH_SHORT);
		switch (message.getCode()) {
			case EventTag.GET_SP_DATA:
				// TODO: 2020/03/26 需要修改
				ReccoData reccoData = (ReccoData) message.getData();
				if (reccoData != null) {
					double latitude = reccoData.getLatitude();
					double longitude = reccoData.getLongitude();
//					LatLng latLng = new LatLng(latitude, longitude);
//					CoordinateConverter converter = new CoordinateConverter();
//
//					converter.from(CoordinateConverter.CoordType.GPS);
//					converter.coord(latLng);
//
//					latLng = converter.convert();

					//LatLng latLng =PositionUtil.gps84_To_Gcj02(latitude,longitude);
					//latLng=PositionUtil.gcj02_To_Bd09(latLng.latitude,latLng.longitude);
					double[] temp=LngLonUtil.gps84_To_bd09(latitude,longitude);

					LatLng latLng = new LatLng(temp[0], temp[1]);
					if (latitude > 0 && longitude > 0) {
						Marker oldmarker = isExist(reccoData.getDevEui());
						if (oldmarker != null) {
							Bundle bundle=new Bundle();
							bundle.putSerializable("reccoData",reccoData);
							oldmarker.setExtraInfo(bundle);
							oldmarker.setPosition(latLng);
							oldmarker.setVisible(true);
							InfoWindow infoWindow= oldmarker.getInfoWindow();
							if(infoWindow!=null)
							{
								createInfoWindow(mBdLinearLayout,reccoData);
								infoWindow.setBitmapDescriptor(BitmapDescriptorFactory.fromView(mBdLinearLayout));
								oldmarker.showInfoWindow(infoWindow);
							}

							MapStatus mapStatus = new MapStatus.Builder()
									.target(latLng).build();
							//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
							MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
							mAMap.animateMapStatus(mMapStatusUpdate);
							return;
						}

						String name = "";
						if (getActivity() != null) {
							name = ((MainActivity) getActivity()).getOnLineFiremanName(reccoData.getDevEui());
						}
						Marker marker = generateMarker(latLng, reccoData, name);
						createInfoWindow(mBdLinearLayout,reccoData);
						InfoWindow infoWindow=new InfoWindow(mBdLinearLayout,latLng,-100);
						marker.showInfoWindow(infoWindow);
//						mAMap.showInfoWindow(infoWindow);
//						marker.setVisible(true);
						mMarkers.add(marker);
						MapStatus mapStatus = new MapStatus.Builder()
								.target(latLng).build();
						//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
						MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
						mAMap.animateMapStatus(mMapStatusUpdate);
					}
				}

				break;
			default:
				break;
		}

	}

	private Marker isExist(String id) throws InterruptedException {
		if (mMarkers != null && mMarkers.size() > 0) {
			for (int i = 0; i < mMarkers.size(); i++) {

				Marker marker = mMarkers.get(i);
				ReccoData reccoData = (ReccoData) marker.getExtraInfo().getSerializable("reccoData");
				if(reccoData!=null)
				{
					if (reccoData.getDevEui().equals(id)) {
						marker.setVisible(false);
						return marker;
					}
				}

			}
		}
		return null;
	}
	private boolean bManualPosition=false;
	@OnClick({R.id.btn_model, R.id.btn_test,R.id.btn_change})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_model:
				bManualPosition=true;
				requestLocation();
				break;
			case R.id.btn_test:
				ReccoData reccoData = new ReccoData();//104.054702,30.596344
				reccoData.setDevEui("00000001");
				reccoData.setReccoId("0001");
				reccoData.setLatitude(30.596344);
				reccoData.setLongitude(104.054702);
				reccoData.setAlarmState((byte) 2);
				reccoData.setGesture((byte) 2);
				reccoData.setTemperature(18.0f);

				LatLng latLng = new LatLng(reccoData.getLatitude(), reccoData.getLongitude());
//				CoordinateConverter converter = new CoordinateConverter();
//				converter.from(CoordinateConverter.CoordType.GPS);
//				converter.coord(latLng);
//				latLng = converter.convert();

				//LogUtils.d(TAG,String.valueOf(latLng.latitude)+String.valueOf(latLng.longitude));

				Marker marker = generateMarker(latLng,reccoData,"李阳");
				createInfoWindow(mBdLinearLayout,reccoData);
				marker.setVisible(true);
				mInfoWindow=new InfoWindow(mBdLinearLayout,latLng,-100);
				marker.showInfoWindow(mInfoWindow);
//				mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
//                int m=marker.getIcons().size();
//                BitmapDescriptor bitmapDescriptor = marker.getIcons().get(0);
//				if (marker.getObject() instanceof DeviceData) {
//					mMarkers.add(marker);
//				}
				break;
			case R.id.btn_change:
				if(mInfoWindow!=null)
				{
					mAMap.hideInfoWindow();
				}
				break;
			default:
				break;
		}
	}

	private void requestLocation()
	{
		if(mLocationClient != null && mLocationClient.isStarted()) {
			Log.d(TAG, "requestLocation.");
			//请求定位，异步返回，结果在locationListener中获取.
			mLocationClient.requestLocation();
		}else if (mLocationClient != null && !mLocationClient.isStarted()) {
			Log.d(TAG, "locationClient is started : " + mLocationClient.isStarted());
			//定位没有开启 则开启定位，结果在locationListener中获取.
			mLocationClient.start();
		}else {
			Log.e(TAG,"request location error!!!");
		}
	}
	private Bitmap getViewBitmap(View addViewContent) {

		addViewContent.setDrawingCacheEnabled(true);

		addViewContent.measure(
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		addViewContent.layout(0, 0,
				addViewContent.getMeasuredWidth(),
				addViewContent.getMeasuredHeight());

		addViewContent.buildDrawingCache();
		Bitmap cacheBitmap = addViewContent.getDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

		return bitmap;
	}

	private Marker generateMarker(LatLng latLng,ReccoData reccoData,String name)
	{
		View inflate = LayoutInflater.from(getContext()).inflate(R.layout.marker_style_layout, null);
		if(inflate!=null)
		{
			TextView tv_serial_number = (TextView) inflate.findViewById(R.id.tv_serial_number);
			TextView tv_name = (TextView) inflate.findViewById(R.id.tv_title);
			tv_serial_number.setText(reccoData.getDevEui());
			if(StringUtils.isEmpty(name))
			{
				tv_name.setText("");
			}
			else
			{
				tv_name.setText(name);
			}
			BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(getViewBitmap(inflate));
			Bundle bundle = new Bundle();
			bundle.putSerializable("reccoData", reccoData);
			OverlayOptions oo = new MarkerOptions().position(latLng).icon(markerIcon).draggable(false).zIndex(9).extraInfo(bundle);
			return (Marker) mAMap.addOverlay(oo);
		}

		return null;
	}

	private void createInfoWindow(LinearLayout linearLayout,ReccoData reccoData)
	{
		InfoWindowHolder holder = null;
//		holder.tv_info_serial_number = (TextView) linearLayout.findViewById(R.id.tv_info_serial_number);
//		holder.tv_info_name = (TextView) linearLayout.findViewById(R.id.tv_info_name);
//		holder.tv_info_gesture = (TextView) linearLayout.findViewById(R.id.tv_info_gesture);
//		holder.tv_info_latitude = (TextView) linearLayout.findViewById(R.id.tv_info_latitude);
//		holder.tv_info_longitude = (TextView) linearLayout.findViewById(R.id.tv_info_longitude);
//		holder.tv_info_temperature = (TextView) linearLayout.findViewById(R.id.tv_info_temperature);
//		holder.tv_info_alarm_state = (TextView) linearLayout.findViewById(R.id.tv_info_alarm_state);
		if (linearLayout.getTag() == null) {
			holder = new InfoWindowHolder();
			holder.tv_info_serial_number = (TextView) linearLayout.findViewById(R.id.tv_info_serial_number);
			holder.tv_info_name = (TextView) linearLayout.findViewById(R.id.tv_info_name);
			holder.tv_info_gesture = (TextView) linearLayout.findViewById(R.id.tv_info_gesture);
			holder.tv_info_latitude = (TextView) linearLayout.findViewById(R.id.tv_info_latitude);
			holder.tv_info_longitude = (TextView) linearLayout.findViewById(R.id.tv_info_longitude);
			holder.tv_info_temperature = (TextView) linearLayout.findViewById(R.id.tv_info_temperature);
			holder.tv_info_alarm_state=(TextView) linearLayout.findViewById(R.id.tv_info_alarm_state);
			linearLayout.setTag(holder);
		}
		holder = (InfoWindowHolder) linearLayout.getTag();

		holder.tv_info_serial_number.setText(reccoData.getDevEui());

		String name = "";
		if (getActivity() != null) {
			name = ((MainActivity) getActivity()).getOnLineFiremanName(reccoData.getDevEui());
			holder.tv_info_name.setText(name);
		}

		holder.tv_info_gesture.setText(ConvertUtils.getReccoGesture(reccoData.getGesture()));
		holder.tv_info_latitude.setText(String.format("%.6f",reccoData.getLatitude()));
		holder.tv_info_longitude.setText(String.format("%.6f",reccoData.getLongitude()));
		holder.tv_info_temperature.setText(String.valueOf(reccoData.getTemperature()));
		byte alarmState=reccoData.getAlarmState();

		byte bit=GetBit(alarmState,7);
		if(bit==1)
		{
			holder.tv_info_alarm_state.setText(ConvertUtils.getReccoAlarmState(5));
		}
		else
		{
			holder.tv_info_alarm_state.setText(ConvertUtils.getReccoAlarmState((alarmState & 0x70) >> 4));
		}
		//holder.tv_info_alarm_state.setText(ConvertUtils.getReccoAlarmState(reccoData.getAlarmState()));
	}


	private byte GetBit(byte b,int index)
	{
		return (byte)(((b & (1 << index)) >0)?1:0);
	}

	// 显示地图
	private void showMap() {
		mEmptyGpsLayout.setVisibility(View.GONE);
		mRlNavigation.setVisibility(View.VISIBLE);
	}

	// 隐藏地图
	private void hideMap() {
		mEmptyGpsLayout.setVisibility(View.VISIBLE);
		mRlNavigation.setVisibility(View.GONE);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {

			isOpenSetting = false;

			if (GpsUtil.isOpen(getActivity())) {
				showMap();
			} else {
				hideMap();
				ToastUtils.error("打开GPS失败,不能使用定位功能!", true);
			}
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			mAccelerometerValues = event.values;
		}

		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			mMagneticFieldValues = event.values;
		}

		SensorManager.getRotationMatrix(mR, null, mAccelerometerValues, mMagneticFieldValues);
		SensorManager.getOrientation(mR, mValues);

		double x = Math.toDegrees(mValues[0]);

		if (Math.abs(x - mLastX) > 1.0) {
			// 将传感器的方向赋予百度地图方向
			mCurrentDirection = (int) x;
		}

		mLastX = x;
	}




	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onStart() {
		getActivity().getContentResolver().registerContentObserver(Settings.Secure.getUriFor(Settings.System.LOCATION_PROVIDERS_ALLOWED), false, mGpsMonitor);
		super.onStart();
	}

	@Override
	public void onDestroy() {

		if (mLocationClient != null) {
			mLocationClient.stop();
			mAMap.setMyLocationEnabled(false);
			mLocationClient.unRegisterLocationListener(locationListener);
			mLocationClient = null;
		}

		if (mMapView != null) {
			mMapView.onDestroy();
			mMapView = null;
		}

		if (unbinder != null) {
			unbinder.unbind();
		}
		if (mSensorManager != null) {
			mSensorManager = null;
		}
		if(mHandler!=null)
		{
			mHandler.removeCallbacksAndMessages(null);
		}

		super.onDestroy();

	}

	@Override
	public void onStop() {
		mSensorManager.unregisterListener(this);
		getActivity().getContentResolver().unregisterContentObserver(mGpsMonitor);
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		mSensorManager.registerListener(this, mAccelerometerSensor, Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mMagneticSensor, Sensor.TYPE_MAGNETIC_FIELD);
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	public void startNavigate() {

		// 定位的动态权限设置，否则会定位到几内亚
		// 获取到权限，初始化地图（是否显示地图，根据GPS是否打开判断）
		//initMap();

		// 判断GPS是否打开了
		if (!GpsUtil.isOpen(getActivity())) {
			new MaterialDialog.Builder(getActivity())
					.content("需要打开GPS才可以使用定位功能，确定跳转到系统设置打开GPS")
					.positiveText(R.string.confirm)
					.positiveColorRes(R.color.positive_color)
					//					.negativeText(R.string.cancel)
					//					.negativeColorRes(R.color.negative_color)
					.buttonRippleColorRes(R.color.button_ripple_color)
					.onPositive((dialog, which) -> {

						isOpenSetting = true;
						//跳转到设置页面让用户自己手动开启
						Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
						dialog.cancel();
					}).show();
		} else {
			showMap();
		}
	}


	@OnCheckedChanged({R.id.cb_satellite, R.id.cb_road_condition})
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.cb_satellite:
				if (isChecked) {
					// 设置为卫星地图
					mAMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
				} else {
					// 设置为普通2D地图
					mAMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
				}
				break;
			case R.id.cb_road_condition:
				// 开启或关闭路况
				mAMap.setTrafficEnabled(isChecked);
				break;
		}
	}


	public class MyLocationListener extends BDAbstractLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			//mapView 销毁后不在处理新接收的位置
			if (location == null || mMapView == null){
				return;
			}

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(location.getDirection()).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mAMap.setMyLocationData(locData);

			// 第一次定位时，将地图位置移动到当前位置
			if (isFirstLoc)
			{
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatus.Builder builder=new MapStatus.Builder();
				builder.zoom(18.0f);
				mAMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);

				mAMap.animateMapStatus(u);
			}

			if(bManualPosition)
			{
				bManualPosition=false;
				LatLng latLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
				mAMap.animateMapStatus(mapStatusUpdate);
			}


		}
	}

	public class InfoWindowHolder {
		public TextView tv_info_serial_number;
		public TextView tv_info_name;
		public TextView tv_info_gesture,tv_info_latitude;
		public TextView tv_info_longitude,tv_info_temperature,tv_info_alarm_state;
	}


}



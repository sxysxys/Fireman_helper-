package com.rescue.hc.ui.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.rescue.hc.BuildConfig;
import com.rescue.hc.R;
import com.rescue.hc.app.AppApplication;
import com.rescue.hc.bean.command.Fireman;
import com.rescue.hc.bean.util.ToastInfo;
import com.rescue.hc.event.EventMessage;
import com.rescue.hc.lib.component.EventTag;
import com.rescue.hc.lib.util.ToastUtils;
import com.rescue.hc.nfc.NfcNdef;
import com.rescue.hc.presenter.main.MainContract;
import com.rescue.hc.presenter.main.MainPresenter;
import com.rescue.hc.ui.dialog.MessageDialog;
import com.rescue.hc.ui.fragment.GroupInfoFragment;
import com.rescue.hc.ui.fragment.HomeFragment;
import com.rescue.hc.ui.fragment.NavigationFragment;
import com.rescue.hc.ui.fragment.RecvDataFragment;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

import static com.rescue.hc.lib.component.EventTag.INIT_USB_PORT;
import static com.rescue.hc.lib.component.EventTag.SET_NFC_CANCEL;
import static com.rescue.hc.lib.component.EventTag.SET_NFC_SUBMIT;
import static com.rescue.hc.lib.component.EventTag.TOAST_INFO_STATUS;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/10/29
 * @descibe none
 * </pre>
 */
public class MainActivity extends BaseActivity implements MainContract.View {
	private Fragment homeFragment, navigationFragment,recvDataFragment,groupInfoFragment;
	private boolean isWriteNfc = false;
	private String nfcWriteInfo = "";
	private MainPresenter mainPresenter;
	private Intent onHomeIntent;
	private final String TAG = MainActivity.class.getSimpleName();
	@Override
	public void setLayoutId() {

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			View decorView = getWindow().getDecorView();
//			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//			getWindow().setStatusBarColor(Color.TRANSPARENT);
//		}


		// 屏幕保持不暗不关闭
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		initUsbReceiver();
		//findUsb();
	}

	@Override
	public void initView() {

		RxPermissions rxPermissions = new RxPermissions(this);
		rxPermissions
				.request(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})
				.subscribe(new Consumer<Boolean>() {
					@Override
					public void accept(Boolean aBoolean) throws Exception {
						if (!aBoolean) {
							ToastUtils.warning("请允许权限", false);
							finish();
						} else {
							if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
								// TODO: Consider calling
								//    ActivityCompat#requestPermissions
								// here to request the missing permissions, and then overriding
								//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
								//                                          int[] grantResults)
								// to handle the case where the user grants the permission. See the documentation
								// for ActivityCompat#requestPermissions for more details.
								return;
							}

						}
					}
				});



		homeFragment = new HomeFragment();
		navigationFragment = new NavigationFragment();
		recvDataFragment=new RecvDataFragment();
		groupInfoFragment=new GroupInfoFragment();

		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_contain, homeFragment, HomeFragment.class.getSimpleName()).show(homeFragment)
				.add(R.id.fragment_contain, navigationFragment, NavigationFragment.class.getSimpleName()).hide(navigationFragment)
				.add(R.id.fragment_contain, recvDataFragment, RecvDataFragment.class.getSimpleName()).hide(recvDataFragment)
				.add(R.id.fragment_contain,groupInfoFragment,GroupInfoFragment.class.getSimpleName()).hide(groupInfoFragment)
				.commit();
		getSupportFragmentManager().beginTransaction().show(homeFragment).commit();

	}

	private BroadcastReceiver mUsbReceiver;

	private UsbManager mUsbManager;
	private UsbSerialPort mSerialPort;
	private List<UsbSerialPort> mEntries = new ArrayList<UsbSerialPort>();
	private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";

	private void initUsbReceiver()
	{

		mUsbReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals(INTENT_ACTION_GRANT_USB)) {
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						// TODO: 2020/03/25
						if(mEntries!=null && mEntries.size()>0)
						{
							mSerialPort=mEntries.get(0);
							//CommunicationService.sPort=mSerialPort;
							EventMessage message=new EventMessage(mSerialPort,INIT_USB_PORT);
							EventBus.getDefault().post(message);
							Log.d(TAG,mSerialPort.getSerial());
						}

					} else {
						Toast.makeText(context, "USB permission denied", Toast.LENGTH_SHORT).show();
					}
				}
			}
		};


	}

	private void findUsb()
	{
		final List<UsbSerialDriver> drivers =
				UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);

		for (final UsbSerialDriver driver : drivers) {
			final List<UsbSerialPort> ports = driver.getPorts();
			Log.d(TAG, String.format("+ %s: %s port%s",
					driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
			mEntries.addAll(ports);
		}
	}

	private static final int MESSAGE_REFRESH = 101;
	private static final int MESSAGE_PORT = 102;
	private static final long REFRESH_TIMEOUT_MILLIS = 3000;
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_REFRESH:
					refreshDeviceList();
					//mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);

					break;
				case MESSAGE_PORT:
					if(mEntries!=null  && mEntries.size()>0)
					{
						mSerialPort=mEntries.get(0);
						UsbDevice device = mSerialPort.getDriver().getDevice();
						if (!mUsbManager.hasPermission(device)) {
							PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
							mUsbManager.requestPermission(device, usbPermissionIntent);

						} else {
							EventMessage message=new EventMessage(mSerialPort,INIT_USB_PORT);
							EventBus.getDefault().post(message);
							//Log.d(TAG,mSerialPort.getSerial());
						}
					}
					break;
				default:
					super.handleMessage(msg);
					break;
			}
		}

	};




	private void refreshDeviceList() {
		new AsyncTask<Void, Void, List<UsbSerialPort>>() {
			@Override
			protected List<UsbSerialPort> doInBackground(Void... params) {
				Log.d(TAG, "Refreshing device list ...");
				SystemClock.sleep(1000);

				final List<UsbSerialDriver> drivers =
						UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);

				final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
				for (final UsbSerialDriver driver : drivers) {
					final List<UsbSerialPort> ports = driver.getPorts();
					Log.d(TAG, String.format("+ %s: %s port%s",
							driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
					result.addAll(ports);
				}

				return result;
			}

			@Override
			protected void onPostExecute(List<UsbSerialPort> result) {
			    if(result!=null && result.size()>0)
                {
                    mEntries.clear();
                    mEntries.addAll(result);
                    mHandler.sendEmptyMessage(MESSAGE_PORT);
                    Log.d(TAG, "Done refreshing, " + mEntries.size() + " entries found.");
                }
			    else
                {
                    mHandler.sendEmptyMessage(MESSAGE_REFRESH);
                }

			}

		}.execute((Void) null);
	}

	@Override
	public void initData(Intent intent) {
		mainPresenter = new MainPresenter(this, this);
		String aa = getIntent().getStringExtra("webNotification");
		/**
		 * 在登陆界面接受到通知 并从打开任务界面  需要处理
		 */
		if (!TextUtils.isEmpty(aa)) {
			if (aa.equals(EventTag.NOTIFICATION)) {
				Timber.d("initData");
				onHomeIntent = intent;
			}
		}

		mHandler.sendEmptyMessage(MESSAGE_REFRESH);

		//EventBus.getDefault().post(new EventMessage("", EventTag.LOGIN_FINISH));
	}

	@OnClick({R.id.img_home, R.id.img_navigation,R.id.img_history,R.id.img_group})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.img_home:
				getSupportFragmentManager().beginTransaction().hide(navigationFragment).hide(recvDataFragment).hide(groupInfoFragment).show(homeFragment).commit();
				break;
			case R.id.img_navigation:
				getSupportFragmentManager().beginTransaction().hide(homeFragment).hide(recvDataFragment).hide(groupInfoFragment).show(navigationFragment).commit();
				break;
			case R.id.img_history:
				getSupportFragmentManager().beginTransaction().hide(homeFragment).hide(navigationFragment).hide(groupInfoFragment).show(recvDataFragment).commit();
				break;
			case R.id.img_group:
				getSupportFragmentManager().beginTransaction().hide(homeFragment).hide(navigationFragment).hide(recvDataFragment).show(groupInfoFragment).commit();
				break;
			default:
				break;
		}
	}

	public void switchFragment() {
		getSupportFragmentManager().beginTransaction().hide(homeFragment).show(navigationFragment).commit();
	}

	public List<String> getOnLineFireman() {
		List<String> list = new ArrayList<>();
		HomeFragment homeFragment1 = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HomeFragment.class.getSimpleName());
		if (homeFragment1 != null) {
			return homeFragment1.getFiremanReccoVoList();
		}
		return list;
	}

	public String getOnLineFiremanName(String watchId) {
		HomeFragment homeFragment1 = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HomeFragment.class.getSimpleName());
		if (homeFragment1 != null) {
			return homeFragment1.getFiremanWatchName(watchId);
		}
		return "";
	}

	@Override
	public void onReceiveEvent(EventMessage event) {
		switch (event.getCode()) {
			case SET_NFC_SUBMIT:
				Fireman fireman = (Fireman) event.getData();
				nfcWriteInfo = fireman.toString();
				isWriteNfc = true;
				break;
			case SET_NFC_CANCEL:
				nfcWriteInfo = "";
				isWriteNfc = (boolean) event.getData();
				break;
			case TOAST_INFO_STATUS:
				ToastInfo toastInfo = (ToastInfo) event.getData();
				if (toastInfo == null) {
					return;
				}
				if (toastInfo.getType() == 1) {
					ToastUtils.success(toastInfo.getMsg(), toastInfo.isWithIcon());
				} else if (toastInfo.getType() == 2) {
					ToastUtils.error(toastInfo.getMsg(), toastInfo.isWithIcon());
				} else if (toastInfo.getType() == 3) {
					ToastUtils.info(toastInfo.getMsg(), toastInfo.isWithIcon());
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (onHomeIntent != null) {
			onHomeIntent = null;
		}
		registerReceiver(mUsbReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));
	}



	@Override
	protected void onNewIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		/**
		 * 在主界面接受通知
		 */
		if (TextUtils.isEmpty(intent.getAction())) {
			Timber.d("onNewIntent");
			String aa = intent.getStringExtra("webNotification");
			if (aa.equals(EventTag.NOTIFICATION)) {
				onHomeIntent = intent;
				return;
			}
		}
		if (isWriteNfc) {
			isWriteNfc = false;
			if (TextUtils.isEmpty(nfcWriteInfo)) {
				showSetNfcMeg();
				ToastUtils.warning("输入不能为空", true);
				return;
			}
			Fireman fireman = NfcNdef.getFiremanInfo(NfcNdef.readNfcTag(intent));
			if (fireman == null) {
				showSetNfcMeg();
				ToastUtils.warning("验证失败", true);
				return;
			}
			if (!fireman.getSerialNumber().equals(nfcWriteInfo.split(",")[0])) {
				showSetNfcMeg();
				ToastUtils.warning("编号不匹配", true);
				return;
			}
			writeNfc(intent);
		} else {
			Fireman fireman = NfcNdef.getFiremanInfo(NfcNdef.readNfcTag(intent));
			if (fireman == null) {
				return;
			}
			EventBus.getDefault().post(new EventMessage(fireman, EventTag.NFC_INFO));
		}
	}

	private void showSetNfcMeg() {
		EventBus.getDefault().post(new EventMessage("", EventTag.SET_NFC_STATUS));
	}

	private void writeNfc(Intent intent) {
		if (NfcNdef.writeNfcTag(intent, nfcWriteInfo)) {
			showSetNfcMeg();
			ToastUtils.success("写入成功", true);
			EventBus.getDefault().post(new EventMessage(getFireman(), EventTag.NFC_UPDATE_FIREMAN));
		} else {
			showSetNfcMeg();
			ToastUtils.error("写入失败", true);
		}
	}

	private Fireman getFireman() {
		String[] info = nfcWriteInfo.split(",");
		if (info.length != 10) {
			return null;
		}
		Fireman fireman1 = new Fireman();
		fireman1.setSerialNumber(info[0]);
		fireman1.setName(info[1]);
		fireman1.setGender(info[2]);
		fireman1.setBirth(info[3]);
		fireman1.setHeight(info[4]);
		fireman1.setWeight(info[5]);
		fireman1.setBloodType(info[6]);
		fireman1.setEnlistTime(info[7]);
		fireman1.setGrade(info[8]);
		fireman1.setPosition(info[9]);
		return fireman1;
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void onBackPressed() {
		new MessageDialog(this, R.style.baseDialog, (dialog, confirm, fireman, id) -> {
			if (confirm) {
				startCleanLeakActivity();
				unregisterReceiver(mUsbReceiver);
				mUsbReceiver=null;
				mHandler.removeMessages(MESSAGE_REFRESH);
				mHandler.removeMessages(MESSAGE_PORT);

				//AppApplication.getApp().stopService();
			}
			dialog.dismiss();
		}).setContent("是否退出监控系统？")
				.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
//		unregisterReceiver(mUsbReceiver);
//		mUsbReceiver=null;
//        mHandler.removeMessages(MESSAGE_REFRESH);
//        mHandler.removeMessages(MESSAGE_PORT);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mainPresenter != null) {
			mainPresenter = null;
		}
//		unregisterReceiver(mUsbReceiver);
//		mUsbReceiver=null;
//		mHandler.removeMessages(MESSAGE_REFRESH);
//		mHandler.removeMessages(MESSAGE_PORT);
		AppApplication.getApp().stopService();
	}

	@Override
	public void unbindSuccess() {
		dismissWaitingDlg();
		startCleanLeakActivity();
	}

	@Override
	public void unbindFailed() {
		dismissWaitingDlg();
		startCleanLeakActivity();
	}

	private void startCleanLeakActivity() {
		startActivity(new Intent(this, CleanLeakActivity.class));
		finish();

	}


}

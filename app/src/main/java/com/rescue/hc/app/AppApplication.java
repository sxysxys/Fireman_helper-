package com.rescue.hc.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.rescue.hc.BuildConfig;
import com.rescue.hc.dao.DaoManager;
import com.rescue.hc.lib.log.ConstTagTree;
import com.rescue.hc.lib.util.audio.AudioUtils;
import com.rescue.hc.service.InitializeService;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import androidx.multidex.MultiDex;
import timber.log.Timber;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/10/29
 * @descibe none
 * </pre>
 */
public class AppApplication extends Application {
    private final static String ACTION_USB_OTG_OFF = "usb.switch.otg.off";
    private final static String ACTION_USB_OTG_ON = "usb.switch.otg.on";
	private static AppApplication mApplication = null;
	private DaoManager mDaoManager = null;
	private static RefWatcher refWatcher;
	private List<Activity> activityList = new ArrayList<>();

	public static AppApplication getApp() {
		return mApplication;
	}

	public DaoManager getDaoManager() {
		if (mDaoManager == null) {
			mDaoManager = DaoManager.getInstance(mApplication);
		}
		return mDaoManager;
	}


	@Override
	public void onCreate() {
		super.onCreate();

        Intent intent = new Intent(ACTION_USB_OTG_ON);
        sendBroadcast(intent);

		if (BuildConfig.DEBUG) {
			Timber.plant(new ConstTagTree().setTag("DebugTag"));
		} else {
			Timber.plant(new ConstTagTree().setTag("DebugTag"));
//			Timber.plant(new CrashReportingTree());
		}

		//在使用SDK各组件之前初始化context信息，传入ApplicationContext
		SDKInitializer.initialize(this);
		//自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
		//包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
		SDKInitializer.setCoordType(CoordType.BD09LL);

		if (LeakCanary.isInAnalyzerProcess(this)) {
			return;
		}
		refWatcher = LeakCanary.install(this);
		mApplication = this;
		//NetClient.init(UrlConstant.BASE_URL);
		InitializeService.start(this);
		MultiDex.install(this);

	}

	public static RefWatcher getRefWatcher() {
		return refWatcher;
	}

	public void stopService() {

        Intent intent = new Intent(ACTION_USB_OTG_OFF);
        sendBroadcast(intent);
		finishAllActivity();
		if (mDaoManager != null) {
			mDaoManager.closeDataBase();
			mDaoManager = null;
			Timber.d("closeDataBase");
		}
		AudioUtils.getInstance().destroy();
		Timber.d("System");
		//强制退出 否则内存中残留app 下次进入不会调用onCreate();
		System.exit(0);
	}

	public static boolean isServiceRunning(Context context, String serviceName) {
		if (("").equals(serviceName) || serviceName == null) {
			return false;
		}
		ActivityManager myManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName()
					.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}

	private static class CrashReportingTree extends Timber.Tree {
		@Override
		protected void log(int priority, String tag, String message, Throwable t) {

		}
	}

	public void addActivity(Activity activity) {
		if (!activityList.contains(activity)) {
			activityList.add(activity);
		}
	}

	public void removeActivity(Activity activity) {
		if (activityList.contains(activity)) {
			activityList.remove(activity);
		}
	}

	public void finishAllActivity() {
		for (Activity activity : activityList) {
			if (!activity.isFinishing() || !activity.isDestroyed()) {
				activity.finish();
			}
		}
	}

}

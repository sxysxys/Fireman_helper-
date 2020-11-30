/***************************************************************************************************
 * 单位：北京红云融通技术有限公司
 * 日期：2017-09-08
 * 版本：1.0.0
 * 版权：All rights reserved.
 **************************************************************************************************/
package com.rescue.hc.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.rescue.hc.BuildConfig;
import com.rescue.hc.lib.log.CrashHandler;
import com.rescue.hc.lib.util.audio.AudioUtils;

import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * 描述：应用Application初始化service
 * 类名：InitializeService
 * 作者：gtzha
 * 日期：2017-09-08
 */
public class InitializeService extends IntentService {

	private static final String ACTION_INIT_WHEN_APP_CREATE =
			BuildConfig.APPLICATION_ID + ".service.action.INIT";

	public InitializeService() {
		super("InitializeService");
	}

	public static void start(Context context) {
		Intent intent = new Intent(context, InitializeService.class);
		intent.setAction(ACTION_INIT_WHEN_APP_CREATE);
		intent.setPackage(context.getPackageName());
		context.startService(intent);
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		if (intent != null) {
			final String action = intent.getAction();
			if (ACTION_INIT_WHEN_APP_CREATE.equals(action)) {
				performInit();
			}
		}
	}

	private void performInit() {
		Timber.d("performInit");
		// 注册未捕获异常收集器
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
		AudioUtils.getInstance().initTTs(getApplicationContext());
		//Intent intent = new Intent(this, NettyRemoteService.class);
		//startService(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Timber.d("onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Timber.d("onDestroy");
	}
}

package com.personal.framework.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.core.app.NotificationManagerCompat;

/**
 * Created by januswong on 2018/1/25.
 */

public class CommonUtil {

	private static long lastClickTime;

	/**
	 * @return
	 * @author: liuzb
	 * @Title: isFastDoubleClick
	 * @Description: 防止按钮快速点击导致多次处理
	 * @date: 2014-3-13 下午5:31:47
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 600) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 手机号码检查
	 */
	public static boolean phonenumberCheck(String phoneNumber) {

		// 表达式对象
		Pattern p = Pattern.compile("^(1)[0-9]{10}$");

		// 创建 Matcher 对象
		Matcher m = p.matcher(phoneNumber);

		return m.matches();
	}

	/**
	 * 密码检查
	 */
	public static boolean passwordCheck(String password) {
		return password.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$");
	}

	/**
	 * EditText获取焦点并显示软键盘
	 */
	public static void showSoftInputFromWindow(Activity activity, EditText editText) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(editText, 0);
	}

	public static Point getDeviceSize(Context context) {
		Point deviceSize = new Point(0, 0);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			((WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay().getSize(deviceSize);
		} else {
			Display display = ((WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();
			deviceSize.x = display.getWidth();
			deviceSize.y = display.getHeight();
			display = null;
		}
		return deviceSize;
	}

	/**
	 * 隐藏界面输入软键盘
	 *
	 * @param activity
	 */
	public static void hideSoftInputFromWindow(final Activity activity) {
		// activity.runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {

			View view = activity.getCurrentFocus();
			if (view != null) {
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		}
		// }
		// });
	}

	/**
	 * Description: dp 转换 px
	 *
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dp2px(Context context, float dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	/**
	 * 获取VersionCode
	 *
	 * @param ctx
	 * @return
	 */
	public static int getLocalVersionCode(Context ctx) {
		int localVersion = 0;
		try {
			PackageInfo packageInfo = ctx.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(ctx.getPackageName(), 0);
			localVersion = packageInfo.versionCode;
			LogUtil.d("TAG", "本软件的版本号。。" + localVersion);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return localVersion;
	}

	/**
	 * 获取软件版本号
	 *
	 * @param ctx
	 * @return
	 */
	public static String getLocalVersionName(Context ctx) {
		String versionName = "";
		try {
			PackageInfo packageInfo = ctx.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(ctx.getPackageName(), 0);
			versionName = packageInfo.versionName;
			LogUtil.d("TAG", "本软件的版本号。。" + versionName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 获取当前应用程序的包名
	 *
	 * @param context 上下文对象
	 * @return 返回包名
	 */
	public static String getAppProcessName(Context context) {
		//当前应用pid
		int pid = android.os.Process.myPid();
		//任务管理类
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//遍历所有应用
		List<ActivityManager.RunningAppProcessInfo> infos = manager != null ? manager.getRunningAppProcesses() : null;
		if (infos == null) {
			return "";
		}
		for (ActivityManager.RunningAppProcessInfo info : infos) {
			//得到当前应用 返回包名
			if (info.pid == pid) {
				return info.processName;
			}
		}
		return "";
	}

	/**
	 * 判断应用是否在运行
	 *
	 * @param context
	 * @return
	 */
	public static boolean isRun(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		String myPkgName = "com.bosy.watch";
		//100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表系统后台有此进程在运行
		for (ActivityManager.RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(myPkgName) || info.baseActivity.getPackageName().equals(myPkgName)) {
				isAppRunning = true;
				break;
			}
		}
		return isAppRunning;
	}

	/**
	 * Created by chitty on 2017/8/3.
	 * 获取通知栏权限是否开启
	 */
	private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
	private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

	public static boolean isNotificationEnabled(Context context) {
		if (Build.VERSION.SDK_INT >= 24) {
			return NotificationManagerCompat.from(context).areNotificationsEnabled();
		} else if (Build.VERSION.SDK_INT >= 19) {
			AppOpsManager appOps =
					(AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
			ApplicationInfo appInfo = context.getApplicationInfo();
			String pkg = context.getApplicationContext().getPackageName();
			int uid = appInfo.uid;
			try {
				Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
				Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE,
						Integer.TYPE, String.class);
				Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
				int value = (int) opPostNotificationValue.get(Integer.class);
				return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg)
						== AppOpsManager.MODE_ALLOWED);
			} catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException
					| InvocationTargetException | IllegalAccessException | RuntimeException e) {
				return true;
			}
		} else {
			return true;
		}
	}

	/**
	 * 跳转到权限设置界面
	 */
	public static Intent openSettingNotice(Context context) {
		Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
		if (appIntent != null) {
			//context.startActivity(appIntent);
			return appIntent;
		}
		appIntent = context.getPackageManager().getLaunchIntentForPackage("com.oppo.safe");
		if (appIntent != null) {
			//context.startActivity(appIntent);
			return appIntent;
		}
		Intent intent = new Intent();
		String packageName = context.getPackageName();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
			intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
			intent.putExtra("app_package", context.getPackageName());
			intent.putExtra("app_uid", context.getApplicationInfo().uid);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.fromParts("package", context.getPackageName(), null));
		} else {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
			intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
		}
		//context.startActivity(intent);
		return intent;
	}

	/**
	 * InputMethodManager 导致的内存泄露及解决方案
	 * 这是Android输入法的一个bug，在15<=API<=23中都存在
	 * @param destContext
	 */
	public static void fixInputMethodManagerLeak(Context destContext) {
		if (destContext == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm == null) {
			return;
		}
		String [] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
		Field f = null;
		Object objGet = null;
		for (int i = 0;i < arr.length;i ++) {
			String param = arr[i];
			try{
				f = imm.getClass().getDeclaredField(param);
				if (!f.isAccessible()) {
					f.setAccessible(true);
				} // author: sodino mail:sodino@qq.com
				objGet = f.get(imm);
				if (objGet != null && objGet instanceof View) {
					View vGet = (View) objGet;
					if (vGet.getContext() == destContext) {
						// 被InputMethodManager持有引用的context是想要目标销毁的
						f.set(imm, null);
						// 置空，破坏掉path to gc节点
					} else {
						// 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
						break;
					}
				}
			}catch(Throwable t){
				t.printStackTrace();
			}
		}
	}

	/**
	 * @param slotId slotId为卡槽Id，它的值为 0、1；
	 *               获取IMEI号
	 * @return
	 */
	public static String getIMEI(Context context, int slotId) {
		try {
			TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			Method method = null;
			if (manager != null) {
				method = manager.getClass().getMethod("getImei", int.class);
				return (String) method.invoke(manager, slotId);
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

}

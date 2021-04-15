package com.rescue.hc.lib.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Created by szc
 * @date on 2018/04/23
 */
@SuppressLint("SimpleDateFormat")
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static String TAG = "MyCrash";
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static CrashHandler instance;
    private Context mContext;

    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<>();

    // 用于格式化日期,作为日志文件名的一部分
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
//		autoClear(5);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理，这里出现了问题
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
//		if (handleException(ex) && mDefaultHandler != null) {
//			// 如果用户没有处理则让系统默认的异常处理器来处理
////			mDefaultHandler.uncaughtException(thread, ex);
//		} else {
////			try {
////				Thread.sleep(3000);
////			} catch (InterruptedException e) {
////				e.printStackTrace();
////			}
////          如果使用如下方法退出程序，将会发生日志打印三次的BUG
////			// 退出程序
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(1);
//		}
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息; 否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        Log.e("ss", ex.toString());
        try {
            // 使用Toast来显示异常信息
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, "很抱歉,程序出现异常",
                            Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();
            // 收集设备参数信息
            collectDeviceInfo(mContext);
            // 保存日志文件
            saveCrashInfoFile(ex);
            SystemClock.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName + "";
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     * @throws Exception
     */
    private String saveCrashInfoFile(Throwable ex) throws Exception {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new Date());
        sb.append("\r\n").append(date).append("\n");
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.flush();
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        sb.append("...................................................end.......................................................");

        try {
            String time;
            time = formatter.format(new Date());
            String fileName = time + "-" + "crash-" + mContext.getPackageName() + ".log";
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "crash" + File.separator;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName, true);
                fos.write("\r\n".getBytes());
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

}

/***************************************************************************************************
 * 单位：北京红云融通技术有限公司
 * 日期：2016-12-09
 * 版本：1.0.0
 * 版权：All rights reserved.
 **************************************************************************************************/
package com.rescue.hc.lib.util;

import android.os.Environment;

import com.rescue.hc.lib.component.Constant;

import java.io.File;

import timber.log.Timber;

/**
 * 描述：应用目录辅助类
 * 类名：AppDirectoryHelper
 * 作者：gtzha
 * 日期：2016-12-09
 */
public class AppDirectoryHelper {

    /**
     * base目录
     */
    public static final String BASE_DIR = Environment.getExternalStorageDirectory().toString() + File.separator
            + Constant.APPLICATION_ID + File.separator;

    /**
     * copy数据库目录
     */
    public static final String COPY_SQLITE_FOLDER = "sqlite";
    /**
     * 应用日志目录
     */
    public static final String LOG_FOLDER = "appLog";
    /**
     * dump目录
     */
    public static final String DUMP_FOLDER = "dump";
    /**
     * eventlogcat日志目录
     */
    public static final String EVENTLOGCAT_FOLDER = "eventlogcat";
    /**
     * InternalConnect日志目录
     */
    public static final String INTERNAL_CONNECT_FOLDER = "InternalConnect";
    /**
     * sdkLog日志目录
     */
    public static final String SDK_LOG_FOLDER = "sdkLog";
    /**
     * x1playerLog日志目录
     */
    public static final String X1PLAYER_LOG_FOLDER = "x1playerLog";
    public static final String X1PLAYER_LOG_FOLDER_LOG = "log";
    /**
     * 拍摄视频目录
     */
    public static final String RECORD_VIDEO_FOLDER = "recordVideos";
    /**
     * 录音文件目录
     */
    public static final String RECORD_AUDIO_FOLDER = "recordAudios";
    /**
     * apk下载目录
     */
    public static final String DOWNLOADAPK_FOLDER = "downloadApk";

    // 压缩文件保存的文件夹
    public static String FILE_COMPRESS_DIR = "compress";
    // 调用系统相机拍照保存的路径
    public static String FILE_TAKE_PHOTO_DIR = "takePhoto";

    /**
     * 拍摄头像图片目录
     */
    public static String HEAD_ICON_FOLDER = "headIcon";
    /**
     * 拍摄背景图片目录
     */
    public static String BACK_GROUND_FOLDER = "bgFolder";
    /**
     * 应用分享默认图片目录
     */
    public static final String SHARE_PIC_FOLDER = "sharePicFolder";
    /**
     * 应用文件下载目录
     */
    public static final String DOWNLOAD_FILE_FOLDER = "downloadFolder";

    /**
     * 资讯图片->查看->保存目录
     */
    public static final String INFOR_PIC_FOLDER="inforPicFolder";

    public static String getDir(String folderName) {
        String path = BASE_DIR + folderName + "/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    public static void init() {
        // 以butelSign文件区分是否首次使用此应用目录
        // 存在此应用目录，且没有butelSign文件的场合，清空应用目录（原yunzhi杨帆目录）
        try {
            File baseDir = new File(BASE_DIR);
            File butelSignFile = new File(baseDir, "butelSign");
            if (!baseDir.exists()) {
                baseDir.mkdirs();
                butelSignFile.createNewFile();
            } else {
                if (!butelSignFile.exists()) {

                    delete(baseDir);
                    baseDir.mkdirs();
                    butelSignFile.createNewFile();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }


    }


    public static void delete(File file) {
        if (file.isDirectory() && "sdkLog".equals(file.getName())) {
            // sdk日志删除不掉，会导致异常
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }
}

package com.rescue.hc.dao;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/11/09
 * @descibe none
 * </pre>
 */
public class DaoManager {
	/**
	 * 数据库名称
	 */
	private static final String DB_NAME = "HjqHc.db";
	private volatile static DaoManager mDaoManager;
	/**
	 * 多线程访问
	 */
	private static DaoMaster.DevOpenHelper mHelper;
	private static DaoMaster mDaoMaster;
	private static DaoSession mDaoSession;
	private Context context;


	private DaoManager(Context context) {
		this.context = context;
	}


	/**
	 * 使用单例模式获得操作数据库的对象
	 */
	public static DaoManager getInstance(Context context) {
		DaoManager instance = null;
		if (mDaoManager == null) {
			synchronized (DaoManager.class) {
				instance = new DaoManager(context.getApplicationContext());
				mDaoManager = instance;
			}
		}
		return mDaoManager;
	}


	/**
	 * 判断数据库是否存在，如果不存在则创建
	 */
	private DaoMaster getDaoMaster() {
		if (null == mDaoMaster) {
			mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
			mDaoMaster = new DaoMaster(mHelper.getWritableDatabase());
		}
		return mDaoMaster;
	}

	/**
	 * 完成对数据库的增删查找
	 */
	public DaoSession getDaoSession() {
		if (null == mDaoSession) {
			if (null == mDaoMaster) {
				mDaoMaster = getDaoMaster();
			}
			mDaoSession = mDaoMaster.newSession();
		}
		return mDaoSession;
	}

	/**
	 * 设置debug模式开启或关闭，默认关闭
	 */
	public void setDebug(boolean flag) {
		QueryBuilder.LOG_SQL = flag;
		QueryBuilder.LOG_VALUES = flag;
	}

	/**
	 * 关闭数据库
	 */
	public void closeDataBase() {
		closeHelper();
		closeDaoSession();
		if (mDaoManager != null) {
			mDaoManager = null;
		}
		mHelper = null;
	}

	private void closeDaoSession() {
		if (null != mDaoSession) {
			mDaoSession.clear();
			mDaoSession = null;
		}
	}

	private void closeHelper() {
		if (mHelper != null) {
			mHelper.close();
			mHelper = null;
		}
	}

}

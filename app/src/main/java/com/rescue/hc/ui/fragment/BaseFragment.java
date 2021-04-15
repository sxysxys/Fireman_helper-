package com.rescue.hc.ui.fragment;

import android.os.Bundle;

import com.rescue.hc.app.AppApplication;
import com.rescue.hc.event.EventMessage;
import com.rescue.hc.ui.dialog.CommonWaitDialog;
import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle2.components.support.RxFragment;

import androidx.annotation.Nullable;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import timber.log.Timber;

/**
 * Created by Administrator on 2018/1/22.
 */

public class BaseFragment extends RxFragment {
	private CommonWaitDialog waitDialog;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
			Timber.d("shen123:注册成功:%s", this.getClass().getName());
		}
	}


	@Override
	public void onDestroy() {
		Timber.d("shen123:正在退出:%s", this.getClass().getName());
		super.onDestroy();
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
		//检测fragment中的内存泄露
		RefWatcher refWatcher = AppApplication.getRefWatcher();
		refWatcher.watch(this);
	}

	@Subscribe(threadMode = ThreadMode.MainThread)
	public void onReceiveEvent(EventMessage event) {

	}

	protected void showWaitingDlg(String msg) {
		if (waitDialog == null) {
			waitDialog = new CommonWaitDialog(getActivity(), msg);

		}
		if (!waitDialog.isShowing()) {
			waitDialog.show();
		}

	}

	protected void showWaitingDlg(String msg, CommonWaitDialog.IWaitDialogCallBack callBack) {
		if (waitDialog == null) {
			waitDialog = new CommonWaitDialog(getActivity(), msg);
		}
		waitDialog.setCallBack(callBack);
		if (!waitDialog.isShowing()) {
			waitDialog.show();
		}
	}

	protected void dismissWaitingDlg() {
		if (waitDialog != null && waitDialog.isShowing()) {
			waitDialog.dismiss();
		}
	}
}

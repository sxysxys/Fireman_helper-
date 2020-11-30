package com.rescue.hc.ui.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.rescue.hc.app.AppApplication;
import com.rescue.hc.event.EventMessage;
import com.rescue.hc.ui.dialog.CommonWaitDialog;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * @author szc
 * @date 2018/10/29
 * @describe 添加描述
 */
public abstract class BaseActivity extends RxAppCompatActivity {
	private CommonWaitDialog waitDialog;
	private Unbinder mUnbinder;
	protected boolean isForeground;
	protected NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;

	/**
	 * onCreate->onStart->onResume->onPause->onStop->onDestroy
	 */
	@Override
	protected void onResume() {
		super.onResume();
		isForeground = true;
		//设置处理优于所有其他NFC的处理
		if (mNfcAdapter != null) {
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		isForeground = false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		//此处adapter需要重新获取，否则无法获取message
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		//一旦截获NFC消息，就会通过PendingIntent调用窗口
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mNfcAdapter != null) {
			mNfcAdapter.disableForegroundDispatch(this);
		}
	}

	protected void initEventBus() {
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
	}

	@Subscribe(threadMode = ThreadMode.MainThread)
	public void onReceiveEvent(EventMessage event) {

	}

	protected void unbindtEventBus() {
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setLayoutId();
		AppApplication.getApp().addActivity(this);
		mUnbinder = ButterKnife.bind(this);
		initEventBus();
		initView();
		initData(getIntent());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
			mUnbinder.unbind();
		}
		AppApplication.getApp().removeActivity(this);
		this.mUnbinder = null;
		unbindtEventBus();
		if (mNfcAdapter != null) {
			mNfcAdapter = null;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				close();
				return true;
			default:
				break;

		}
		return super.onOptionsItemSelected(item);
	}

	public void close() {
		finish();
	}


	protected void showWaitingDlg(String msg) {
		if (waitDialog == null) {
			waitDialog = new CommonWaitDialog(this, msg);

		}
		if (!waitDialog.isShowing()) {
			waitDialog.show();
		}

	}

	protected void showWaitingDlg(String msg, CommonWaitDialog.IWaitDialogCallBack callBack) {
		if (waitDialog == null) {
			waitDialog = new CommonWaitDialog(this, msg);
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


	public abstract void setLayoutId();

	public abstract void initView();

	public abstract void initData(Intent intent);

}

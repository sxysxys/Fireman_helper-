package com.rescue.hc.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.rescue.hc.R;

public class CommonWaitDialog {

	private View aniView;

	private ImageView btn;

	private RotateAnimation mRotateAnimation;

	private Dialog dialog = null;

	private TextView tvShowContent;

	private Activity mActivity;

	public boolean isDissmissForbidden() {
		return isDissmissForbidden;
	}

	public void setDissmissForbidden(boolean dissmissForbidden) {
		isDissmissForbidden = dissmissForbidden;
	}

	private boolean isDissmissForbidden = false;//物理返回，禁止关闭dialog

	public CommonWaitDialog(Activity activity, String alertMsg) {
		super();

		this.mActivity = activity;

		aniView = LayoutInflater.from(mActivity).inflate(
				R.layout.wait_dialog_layout, null);
		btn = (ImageView) aniView.findViewById(R.id.btn_animation);
		tvShowContent = (TextView) aniView.findViewById(R.id.tv_animation);
		tvShowContent.setText(alertMsg);
		mRotateAnimation = new RotateAnimation(0.0F, 360.0F, 1, 0.5F, 1, 0.5F);
		mRotateAnimation.setFillAfter(false);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		mRotateAnimation.setDuration(800);
		mRotateAnimation.setInterpolator(new LinearInterpolator());

		dialog = new Dialog(mActivity, R.style.transparentFrameWindowStyle);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		dialog.addContentView(aniView, params);
		dialog.setCancelable(false);
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
								 KeyEvent event) {
				switch (keyCode) {
					case KeyEvent.KEYCODE_BACK:
						if (callBack != null) {
							callBack.cancelRequest();
						}
						if (!isDissmissForbidden) {
							dismiss();
						}
						break;

					default:
						break;
				}
				return false;
			}
		});
	}

	public void show() {
		if (dialog != null && mActivity != null && !mActivity.isFinishing()) {

			dialog.show();

			btn.post(new Runnable() {

				@Override
				public void run() {

					btn.startAnimation(mRotateAnimation);

				}
			});
		}
	}

	public boolean isShowing() {
		if (dialog != null) {
			return dialog.isShowing();
		}
		return false;
	}

	public void dismiss() {

		if (dialog != null && dialog.isShowing()) {
			btn.clearAnimation();
			dialog.dismiss();
//            dialog = null;
		}
	}


	private IWaitDialogCallBack callBack;

	public void setCallBack(IWaitDialogCallBack callBack) {
		this.callBack = callBack;
	}

	public interface IWaitDialogCallBack {
		void cancelRequest();
	}
}

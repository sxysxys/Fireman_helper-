package com.rescue.hc.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.github.ybq.android.spinkit.style.Circle;
import com.personal.framework.utils.CommonUtil;
import com.rescue.hc.R;
import com.rescue.hc.app.AppApplication;
import com.rescue.hc.bean.model.LoginInfoModel;
import com.rescue.hc.event.EventMessage;
import com.rescue.hc.lib.util.ToastUtils;
import com.rescue.hc.presenter.login.LoginContract;
import com.tbruyelle.rxpermissions2.RxPermissions;

import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

import static com.rescue.hc.lib.component.EventTag.LOGIN_FINISH;

/**
 * @author szc
 * @date 2018/10/29
 * @describe 添加描述
 */
public class LoginActivity extends BaseActivity implements LoginContract.View {

	@BindView(R.id.et_username)
	EditText etUsername;
	@BindView(R.id.input_layout_name)
	LinearLayout inputLayoutName;
	@BindView(R.id.et_password)
	EditText etPassword;
	@BindView(R.id.input_layout)
	View mInputLayout;
	@BindView(R.id.cb_pwd_visibility)
	CheckBox cbPwdVisibility;
	@BindView(R.id.input_layout_psw)
	LinearLayout inputLayoutPsw;
	@BindView(R.id.cb_remember)
	CheckBox cbRemember;
	@BindView(R.id.ll_login_setting)
	LinearLayout llLoginSetting;
	@BindView(R.id.btn_login)
	Button btnLogin;
	@BindView(R.id.pb_loading)
	ProgressBar mProgressBar;
	@BindView(R.id.rl_loading)
	RelativeLayout rlLoading;
	//private LoginPresenter loginPresenter;
	String strName = "admin";
	String strPassword = "123";


	@Override
	public void setLayoutId() {
		setContentView(R.layout.activity_login);
	}

	@Override
	public void initView() {
		//loginPresenter = new LoginPresenter(this, this);
		RxPermissions rxPermissions = new RxPermissions(this);
		rxPermissions
				.request(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})
				.subscribe(new Consumer<Boolean>() {
					@Override
					public void accept(Boolean aBoolean) throws Exception {
						if (!aBoolean) {
							ToastUtils.warning("请允许权限", false);
							exitLogin();
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
	}


	@Override
	public void initData(Intent intent) {
		initProgressBar();
		setTextNameAndPassword();
//		AppApplication.getDaoManager().getDaoSession().deleteAll(TrainMissionWithTypeName.class);
//		TrainMissionWithTypeName trainMissionWithTypeName = new TrainMissionWithTypeName();
//		trainMissionWithTypeName.setTrainMissionId(1);
//		trainMissionWithTypeName.setIsDone(false);
//
//		AppApplication.getDaoManager().getDaoSession().insert(trainMissionWithTypeName);
//		TrainMissionWithTypeName trainMissionWithTypeName1 = new TrainMissionWithTypeName();
//		trainMissionWithTypeName1.setTrainMissionId(2);
//		trainMissionWithTypeName1.setIsDone(true);
//		AppApplication.getDaoManager().getDaoSession().insert(trainMissionWithTypeName1);
//		List<TrainMissionWithTypeName> list = AppApplication.getDaoManager().getDaoSession().loadAll(TrainMissionWithTypeName.class);
//		KLog.d(list.size());
	}


	private void initProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
		Circle circle = new Circle();
		circle.setColor(Color.parseColor("#D50000"));
		mProgressBar.setIndeterminateDrawable(circle);
	}

	@OnClick(R.id.btn_login)
	public void onViewClicked() {
		String username = etUsername.getText().toString();
		String password = etPassword.getText().toString();
//
////		 先判断网络是否可用
//		if (!NetworkUtils.isConnected()) {
//			ToastUtils.normal("未连接网络");
//			return;
//		}

//
//		if ("".equals(username)) {
//			ToastUtils.normal("请输入用户名!");
//			return;
//		}
//
//		if ("".equals(password)) {
//			ToastUtils.normal("请输入密码!");
//			return;
//		}

//		if (!strName.equals(username) || !strPassword.equals(password)) {
//			ToastUtils.error("用户名或密码错误", true);
//		} else {
//			if (cbRemember.isChecked()) {
//				LoginInfoModel.setCbmember(cbRemember.isChecked());
//			}
//			startActivity(new Intent(this, MainActivity.class));
//			finish();
//		}

		if (cbRemember.isChecked()) {
			LoginInfoModel.setCbmember(cbRemember.isChecked());
		}
		startActivity(new Intent(this, MainActivity.class));
		finish();


	}

	@OnCheckedChanged({R.id.cb_pwd_visibility, R.id.cb_remember})
	public void OnCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.cb_pwd_visibility:
				if (isChecked) {
					etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					// 密码不可见
					etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
				break;
			case R.id.cb_remember:

				break;
			default:
				break;
		}
	}

	@Override
	public void onReceiveEvent(EventMessage event) {
		super.onReceiveEvent(event);
		switch (event.getCode()) {
			case LOGIN_FINISH:
				if (!this.isDestroyed()) {
					finish();
					Timber.d("LOGIN_FINISH");
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 将本地保存的用户数据设置到输入框中
	 */
	private void setTextNameAndPassword() {
		if (LoginInfoModel.isRememberPassword()) {
			cbRemember.setChecked(true);
			etUsername.setText(strName);
			etPassword.setText(strPassword);
		}
	}


	@Override
	public void loginSuccess(Object o) {
		Timber.d(o.toString());
		if (cbRemember.isChecked()) {
			LoginInfoModel.saveUserName(etUsername.getText().toString());
			LoginInfoModel.savePassword(etPassword.getText().toString());
			LoginInfoModel.setCbmember(cbRemember.isChecked());
		}
		//LoginInfoModel.saveUserId(Integer.valueOf(o.toString()));
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	@Override
	public void loginFailed() {
		dismissWaitingDlg();
		ToastUtils.error(getString(R.string.login_fail), true);
	}

	@Override
	public void onBackPressed() {
		exitLogin();
	}

	private void exitLogin() {
		finish();
		AppApplication.getApp().stopService();
	}

	@Override
	protected void onDestroy() {
		CommonUtil.fixInputMethodManagerLeak(LoginActivity.this);
		super.onDestroy();
//		if (loginPresenter != null) {
//			loginPresenter = null;
//		}
	}
}

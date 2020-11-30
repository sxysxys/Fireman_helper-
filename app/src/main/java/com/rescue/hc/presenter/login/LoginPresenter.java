package com.rescue.hc.presenter.login;

import com.personal.framework.http.NetClient;
import com.rescue.hc.http.ApiService;
import com.rescue.hc.http.NetworkObserver;
import com.rescue.hc.lib.component.SpTag;
import com.rescue.hc.presenter.BasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/10/29
 * @descibe none
 * </pre>
 */
public class LoginPresenter extends BasePresenter implements LoginContract.Presenter {
	private LoginContract.View mView;

	public LoginPresenter(LoginContract.View mView, LifecycleProvider<ActivityEvent> provider) {
		super(provider);
		this.mView = mView;
	}

	@Override
	public void login(String name, String password) {
		Map<String, String> map = new HashMap<>();
		map.put(SpTag.USER_NAME, name);
		map.put(SpTag.PASSWORD, password);
		NetClient.getRetrofit().create(ApiService.class)
				.login(map)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.compose(getProvider().bindUntilEvent(ActivityEvent.DESTROY))
				.subscribe(new NetworkObserver<Object>() {
					@Override
					public void onSuccess(Object o) {
						super.onSuccess(o);
						mView.loginSuccess(o);
					}

					@Override
					public void onFailure(Throwable e) {
						super.onFailure(e);
						mView.loginFailed();
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}
				});
	}

	@Override
	public void subscribe() {

	}

	@Override
	public void unsubscribe() {

	}
}

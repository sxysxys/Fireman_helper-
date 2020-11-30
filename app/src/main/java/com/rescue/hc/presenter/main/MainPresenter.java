package com.rescue.hc.presenter.main;

import com.personal.framework.http.NetClient;
import com.rescue.hc.bean.model.LoginInfoModel;
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
 * @date on 2018/11/09
 * @descibe none
 * </pre>
 */
public class MainPresenter extends BasePresenter implements MainContract.Presenter {
	private MainContract.View mView;

	public MainPresenter(MainContract.View mView, LifecycleProvider<ActivityEvent> provider) {
		super(provider);
		this.mView = mView;
	}

	@Override
	public void unbindPad() {
		Map<String, String> map = new HashMap<>();
		map.put(SpTag.WATCH_ID, "all");
		map.put(SpTag.IMEI, LoginInfoModel.getIMEI());
		NetClient.getRetrofit().create(ApiService.class)
				.unbindPad(map)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.compose(getProvider().bindUntilEvent(ActivityEvent.DESTROY))
				.subscribe(new NetworkObserver<Object>() {
					@Override
					public void onSuccess(Object o) {
						super.onSuccess(o);
						mView.unbindSuccess();
					}

					@Override
					public void onFailure(Throwable e) {
						super.onFailure(e);
						mView.unbindFailed();
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

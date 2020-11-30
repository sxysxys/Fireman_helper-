package com.rescue.hc.presenter.home;

import com.rescue.hc.presenter.BaseFragmentPresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/10/29
 * @descibe none
 * </pre>
 */
public class HomePresenter extends BaseFragmentPresenter implements HomeContract.Presenter {
	private HomeContract.View mView;

	public HomePresenter(HomeContract.View mView, LifecycleProvider<FragmentEvent> provider) {
		super(provider);
		this.mView = mView;
	}

	@Override
	public void delayTipShow() {
		Observable.timer(1500, TimeUnit.MILLISECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.compose(getProvider().bindUntilEvent(FragmentEvent.DESTROY))
				.subscribe(aLong -> mView.hideTip());
	}

//	@Override
//	public void bindPad(String watchIds) {
//		Map<String, String> map = new HashMap<>();
//		map.put(SpTag.WATCH_ID, watchIds);
//		map.put(SpTag.IMEI, LoginInfoModel.getIMEI());
//		NetClient.getRetrofit().create(ApiService.class)
//				.bindPad(map)
//				.subscribeOn(Schedulers.io())
//				.observeOn(AndroidSchedulers.mainThread())
//				.compose(getProvider().bindUntilEvent(FragmentEvent.DESTROY))
//				.subscribe(new NetworkObserver<Object>() {
//
//					@Override
//					public void onSuccess(Object o) {
//						super.onSuccess(o);
//						mView.bindSuccess(o.toString());
//					}
//
//					@Override
//					public void onFailure(Throwable e) {
//						super.onFailure(e);
//						mView.bindFailed();
//					}
//
//					@Override
//					public void onFinish() {
//						super.onFinish();
//					}
//				});
//	}
//
//	@Override
//	public void unbindPad(String watchIds) {
//		Map<String, String> map = new HashMap<>();
//		map.put(SpTag.WATCH_ID, watchIds);
//		map.put(SpTag.IMEI, LoginInfoModel.getIMEI());
//		NetClient.getRetrofit().create(ApiService.class)
//				.unbindPad(map)
//				.subscribeOn(Schedulers.io())
//				.observeOn(AndroidSchedulers.mainThread())
//				.compose(getProvider().bindUntilEvent(FragmentEvent.DESTROY))
//				.subscribe(new NetworkObserver<Object>() {
//					@Override
//					public void onSuccess(Object o) {
//						super.onSuccess(o);
//						mView.unbindSuccess(o.toString());
//					}
//
//					@Override
//					public void onFailure(Throwable e) {
//						super.onFailure(e);
//						mView.unbindFailed();
//					}
//
//					@Override
//					public void onFinish() {
//						super.onFinish();
//					}
//				});
//	}

	@Override
	public void subscribe() {

	}

	@Override
	public void unsubscribe() {

	}
}

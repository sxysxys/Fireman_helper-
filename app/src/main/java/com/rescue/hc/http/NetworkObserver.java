package com.rescue.hc.http;

import com.personal.framework.http.module.NetException;
import com.rescue.hc.lib.util.ToastUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018/1/22.
 */

public class NetworkObserver<T> implements Observer<T> {
	@Override
	public void onSubscribe(Disposable d) {

	}

	@Override
	public void onNext(T t) {
		onSuccess(t);
	}

	@Override
	public void onError(Throwable e) {
		onFailure(e);
		onFinish();
	}

	@Override
	public void onComplete() {
		onFinish();
	}

	public void onSuccess(T t) {

	}

	public void onFailure(Throwable e) {
		e.printStackTrace();
		if (e instanceof NetException) {
			NetException exception = (NetException) e;
			ToastUtils.error(e.getMessage(), false);
		}
	}

	public void onFinish() {

	}
}

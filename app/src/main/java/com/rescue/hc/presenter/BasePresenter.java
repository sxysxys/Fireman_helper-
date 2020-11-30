package com.rescue.hc.presenter;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Administrator on 2018/1/22.
 */

public class BasePresenter {
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
	private LifecycleProvider<ActivityEvent> provider;

	public BasePresenter(LifecycleProvider<ActivityEvent> provider) {
		this.provider = provider;
	}

	public LifecycleProvider<ActivityEvent> getProvider() {
		return provider;
	}
}

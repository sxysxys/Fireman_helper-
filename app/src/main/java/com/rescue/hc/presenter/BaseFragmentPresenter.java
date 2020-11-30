package com.rescue.hc.presenter;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.disposables.CompositeDisposable;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/10/29
 * @descibe none
 * </pre>
 */
public class BaseFragmentPresenter {
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
	private LifecycleProvider<FragmentEvent> provider;

	public BaseFragmentPresenter(LifecycleProvider<FragmentEvent> provider) {
		this.provider = provider;
	}

	public LifecycleProvider<FragmentEvent> getProvider() {
		return provider;
	}
}

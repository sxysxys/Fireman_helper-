package com.rescue.hc.http;


import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * @author szc
 * @date 2018/11/08
 * @describe 添加描述
 */
public interface ApiService {
	@POST(UrlConstant.login)
	Observable<Object> login(@QueryMap Map<String, String> params);

	@POST(UrlConstant.bindPad)
	Observable<Object> bindPad(@QueryMap Map<String, String> params);

	@POST(UrlConstant.unbindPad)
	Observable<Object> unbindPad(@QueryMap Map<String, String> params);



	@POST(UrlConstant.uploadTask)
	Observable<Object> uploadTask(@QueryMap Map<String, String> params);
}

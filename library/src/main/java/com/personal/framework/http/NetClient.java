package com.personal.framework.http;


import com.personal.framework.http.converter.CustomGsonConverterFactory;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


/**
 * 网络请求基类，可以设置自己的okhttpclient或者使用默认的
 */
public class NetClient {

    private static String sUrl;

    private static OkHttpClient sHttpClient;

    /**
     * 初始化okhttp client，
     *
     * @param url baseUrl
     */
    public static void init(final String url) {
        sUrl = url;
    }

    public static Retrofit getRetrofit() {
        return getRetrofit(sUrl);
    }

    private static Retrofit.Builder getDefaultBuilder(final String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(CustomGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    /**
     * 获取retrofit对象
     *
     * @param url
     * @return
     */
    public static Retrofit getRetrofit(final String url) {
        if (null == sHttpClient) {
            return getDefaultBuilder(url).
                    client(getDefaultOkhttpBuilder().build()).
                    build();
        } else {
            return getDefaultBuilder(url)
                    .client(sHttpClient)
                    .build();

        }
    }

    public static OkHttpClient.Builder getDefaultOkhttpBuilder() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(interceptor);
    }

    public static Retrofit getRetrofit(OkHttpClient client) {
        return getDefaultBuilder(sUrl)
                .client(client)
                .build();
    }

    /**
     * 设置http client
     *
     * @param sHttpClient
     */
    public static void setHttpClient(@NonNull OkHttpClient sHttpClient) {
        NetClient.sHttpClient = sHttpClient;
    }
}

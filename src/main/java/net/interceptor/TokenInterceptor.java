package net.interceptor;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by stassikorskyi on 10.12.15.
 */
public class TokenInterceptor implements Interceptor {

    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl url = originalRequest.httpUrl().newBuilder().addQueryParameter("access_token", "64d21810c8ca4b33abde28a17f1e0012fb318eb4").build();
        originalRequest = originalRequest.newBuilder().url(url).build();
        return chain.proceed(originalRequest);
    }
}

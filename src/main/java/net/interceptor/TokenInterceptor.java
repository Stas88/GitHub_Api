package net.interceptor;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.ConnectException;

/**
 * Created by stassikorskyi on 10.12.15.
 */
public class TokenInterceptor implements Interceptor {

    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl url = originalRequest.httpUrl().newBuilder().addQueryParameter("access_token", "9d2f8ed3e1d06f92c413aeb3595c77a852705612").build();
        originalRequest = originalRequest.newBuilder().url(url).build();
        Response response = null;
        try {
             response = chain.proceed(originalRequest);
        } catch (ConnectException e) {
            e.printStackTrace();
            response = getResponse(chain, originalRequest);
        }
        return response;
    }

    private Response getResponse(Chain chain, Request originalRequest) throws IOException {
        Response response;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        try {
            response = chain.proceed(originalRequest);
        } catch (ConnectException e1) {
            response = getResponse(chain, originalRequest);
        }
        return response;
    }

}

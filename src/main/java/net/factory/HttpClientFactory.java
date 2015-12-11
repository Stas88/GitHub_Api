package net.factory;

import com.squareup.okhttp.OkHttpClient;

/**
 * Created by stassikorskyi on 26.11.15.
 */
public class HttpClientFactory {

    static OkHttpClient client;
    static HttpClientFactory instance;

    public static OkHttpClient getClient(CLIENT_TYPE type) {
        switch (type) {
            case DEFAULT:
                client = SimpleOkHttpClient.getInstance();
                break;
            case WITH_CACHE:
                client = OkHttpClientWithCache.getInstance();
                break;
        }
        return client;
    }

}

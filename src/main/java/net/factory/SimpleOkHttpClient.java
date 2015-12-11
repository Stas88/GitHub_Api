package net.factory;

import com.squareup.okhttp.OkHttpClient;

/**
 * Created by stassikorskyi on 26.11.15.
 */
public class SimpleOkHttpClient extends OkHttpClient {

    public static SimpleOkHttpClient instance;

    private SimpleOkHttpClient() {
        construct();
    }

    private void construct() {
    }


    public static SimpleOkHttpClient getInstance() {
        if (instance == null) {
            instance = new SimpleOkHttpClient();
        }
        return instance;
    }
}

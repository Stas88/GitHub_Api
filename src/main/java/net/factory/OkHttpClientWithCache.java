package net.factory;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

/**
 * Created by stassikorskyi on 26.11.15.
 */
public class OkHttpClientWithCache extends OkHttpClient {

    int cacheSize = 10485760;
    static OkHttpClientWithCache instance;

    private OkHttpClientWithCache() {
        construct();
    }

    private void construct() {
        Cache cache = new Cache(new File("maindir"), (long) cacheSize);
        setCache(cache);
    }

    public static OkHttpClientWithCache getInstance() {
        if (instance == null) {
            instance = new OkHttpClientWithCache();
        }
        return instance;
    }

}

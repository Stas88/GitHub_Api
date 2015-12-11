package net;

import com.google.gson.Gson;
import com.squareup.okhttp.*;
import net.factory.CLIENT_TYPE;
import net.factory.HttpClientFactory;

import java.io.IOException;

/**
 * Created by stassikorskyi on 01.12.15.
 */
public class DeifferentApiPlaying {

    private static final Gson gson = new Gson();
    private static OkHttpClient client;
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final String LINK_REL_NEXT_PROPERTY = "next";

    public DeifferentApiPlaying() {
        client = HttpClientFactory.getClient(CLIENT_TYPE.DEFAULT);
    }

    static String runGet(String url) throws IOException {
        Request request = (new Request.Builder()).url(url).build();
        System.out.println(request);
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    public static void runGoogleSearch() throws Exception {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https").host("google.com").addQueryParameter("q", "polar bears").build();
        Request request = (new Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            System.out.println(response.body().string());
        }
    }
}

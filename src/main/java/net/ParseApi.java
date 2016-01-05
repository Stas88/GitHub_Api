package net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.*;
import net.factory.CLIENT_TYPE;
import net.factory.HttpClientFactory;
import net.model.Repo;

import java.io.IOException;

/**
 * Created by stassikorskyi on 01.12.15.
 */
public class ParseApi {

    static {
        client = HttpClientFactory.getClient(CLIENT_TYPE.DEFAULT);
    }

    static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static OkHttpClient client;
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final String LINK_REL_NEXT_PROPERTY = "next";


    public String postPlayerToParse() throws IOException {
        String url = "https://api.parse.com//1/classes/GameScore";
        String postBody = "\"score\" : 1337, \"playerName\" : \"Sean Plott\", \"cheatMode\": False";
        Request request = (new Request.Builder()).url(url).post(RequestBody.create(MEDIA_TYPE_MARKDOWN,
                postBody)).header("X-Parse-Application-Id", "OMgZ9RCf2aZyCanEbK2CWPEqsHp0TtnfIbU032ba")
                .header("X-Parse-REST-API-Key", "9VFnUm2uep7bXBaS4LI20LdZDCnLk1jr0WQjJSdQ")
                .header("Content-Type", "application/json").build();
        Response response = client.newCall(request).execute();
        System.out.println("Code: " + response.code());
        System.out.println("Body: " + response.body());
        System.out.println("CacheResponse: " + response.cacheResponse());
        System.out.println("Handshake: " + response.handshake());
        System.out.println("Headers: " + response.headers());
        System.out.println("MEssage: " + response.message());
        System.out.println("Request: " + response.request());
        System.out.println("Protocol: " + response.protocol());
        System.out.println("isRedirect: " + response.isRedirect());
        return response.body().string();
    }

    public static void postRepoToParse(Repo repo, String searchSymbols) throws IOException {

        String url;
        if(searchSymbols == null || ("").equals(searchSymbols)) {
            url = "https://api.parse.com//1/classes/all_repos";
        } else {
            url = "https://api.parse.com//1/classes/" + searchSymbols.replace(" ", "_");
        }
        String postBody = gson.toJson(repo);
        System.out.println("post to Parse..");
        System.out.println("postBody: " + postBody);

        Request request = (new Request.Builder()).url(url).post(RequestBody.create(MEDIA_TYPE_MARKDOWN,
                postBody)).header("X-Parse-Application-Id", "OMgZ9RCf2aZyCanEbK2CWPEqsHp0TtnfIbU032ba")
                .header("X-Parse-REST-API-Key", "9VFnUm2uep7bXBaS4LI20LdZDCnLk1jr0WQjJSdQ")
                .header("Content-Type", "application/json").build();
        //Response response = client.newCall(request).execute();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            public void onFailure(Request request, IOException e) {
                System.out.println("request failed o execute: " + request.urlString());
            }

            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if(response.code() == 422) {
                        System.out.println("Finished with 1000 results");
                    }
                    throw new IOException("Unexpected code " + response);
                }
                System.out.println(response.body().string());

            }
        });

//        System.out.println("Code: " + response.code());
//        System.out.println("Body: " + response.body());
//        System.out.println("CacheResponse: " + response.cacheResponse());
//        System.out.println("Handshake: " + response.handshake());
//        System.out.println("Headers: " + response.headers());
//        System.out.println("MEssage: " + response.message());
//        System.out.println("Request: " + response.request());
//        System.out.println("Protocol: " + response.protocol());
//        System.out.println("isRedirect: " + response.isRedirect());
//        return response.body().string();
    }

}

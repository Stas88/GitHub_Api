package net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.squareup.okhttp.*;
import net.factory.CLIENT_TYPE;
import net.factory.HttpClientFactory;
import net.model.*;
import net.util.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GitHubApiPlaying {

    private static final Gson gson = new Gson();
    private static OkHttpClient client;
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final String LINK_REL_NEXT_PROPERTY = "next";
    public static final int TIME_BETWEEN_REQUESTS = 10 * 1000;

    public static final int NO_STORING_METHOD = 0;
    public static final int SAVE_TO_PARSE_STORING_METHOD = 1;

    public GitHubApiPlaying() {
        client = HttpClientFactory.getClient(CLIENT_TYPE.DEFAULT);
        //client.interceptors().add(new TokenInterceptor());
    }


    public void runUserSearch(String searchQuery) throws Exception {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addEncodedPathSegment("search")
                .addPathSegment("users")
                .addQueryParameter("q", searchQuery)
                .build();
        String s = "{\"type\":\"User\",\"site_admin\":false}";

        System.out.println("url: " + url.toString());
        Request request = (new Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        String textFromResponse = response.body().string();
        Users users = gson.fromJson(textFromResponse, Users.class);
        System.out.println(users);
        for (User user : users.items) {
            System.out.println("login: " + user.login);
        }
        System.out.println("total_count: " + users.total_count);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            System.out.println(response.body().string());
        }
    }

    public void runRepoSearch(String searchQuery, String language) throws Exception {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addEncodedPathSegment("search")
                .addPathSegment("repositories")
                .addQueryParameter("q", searchQuery.concat("+language:" + language))
                .build();
        String s = "{\"type\":\"User\",\"site_admin\":false}";

        System.out.println("url: " + url.toString());
        Request request = (new Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        String textFromResponse = response.body().string();
        Repos repos = gson.fromJson(textFromResponse, Repos.class);
        System.out.println(textFromResponse);
        boolean posted = false;
        for (Repo repo : repos.items) {
            Util.printRepo(repo);
//        if(!posted) {
//            ParseApi.postRepoToParse(repo);
//            posted  = true;
//        }
        }
        System.out.println("X-RateLimit-Limit: " + response.header("X-RateLimit-Limit"));
        System.out.println("X-RateLimit-Remaining: " + response.header("X-RateLimit-Remaining"));
        System.out.println("total_count: " + repos.total_count);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            System.out.println(response.body().string());
        }
    }

    public void runSearchWithPagination(String searchQuery, String language, int storingMethod) throws Exception {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addEncodedPathSegment("search")
                .addPathSegment("repositories")
                .addQueryParameter("q", searchQuery)
                .addEncodedQueryParameter("page", String.valueOf(1))
                .build();
        String s = "{\"type\":\"User\",\"site_admin\":false}";

        System.out.println("url: " + url.toString());
        Request request = (new Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        String textFromResponse = response.body().string();
        Repos repos = gson.fromJson(textFromResponse, Repos.class);
        for (Repo repo : repos.items) {
            Util.printRepo(repo);
            switch (storingMethod) {
                case NO_STORING_METHOD:
                    break;
                case SAVE_TO_PARSE_STORING_METHOD:
                    ParseApi.postRepoToParse(repo);
                    break;
            }
        }
        System.out.println("X-RateLimit-Limit: " + response.header("X-RateLimit-Limit"));
        System.out.println("X-RateLimit-Remaining: " + response.header("X-RateLimit-Remaining"));
        Thread.sleep(TIME_BETWEEN_REQUESTS);
        for (int i = Integer.valueOf(response.header("X-RateLimit-Remaining")), j = 2; i > 0; i--, j++) {
            Response response1 = (runSearchRequest(searchQuery, language, j));
            String textFromResponse2 = response1.body().string();
            Repos repos2 = gson.fromJson(textFromResponse2, Repos.class);
            for (Repo repo : repos2.items) {
                Util.printRepo(repo);
                switch (storingMethod) {
                    case NO_STORING_METHOD:
                        break;
                    case SAVE_TO_PARSE_STORING_METHOD:
                        ParseApi.postRepoToParse(repo);
                        break;
                }
            }
            HashMap<String, String> rels = Util.getRelValueFromResponse(response1);
            HttpUrl urlTest = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                    .host("api.github.com")
                    .addEncodedPathSegment("search")
                    .addEncodedPathSegment("repositories")
                    .addEncodedQueryParameter("q", searchQuery)
                    .addEncodedQueryParameter("page", String.valueOf(1 + j))
                    .build();
//           System.out.println("j: " + j);
//           System.out.println("urlTest: " + urlTest);
            System.out.println("X-RateLimit-Remaining: " + response.header("X-RateLimit-Remaining"));
            System.out.println("X-RateLimit-Limit: " + response.header("X-RateLimit-Limit"));
            String rellsNExt = rels.get(urlTest.toString());
            System.out.println("rellsNext: " + rellsNExt);
            if (!(rellsNExt).equals(LINK_REL_NEXT_PROPERTY)) {
                System.out.println("!rellsNExt.equals(LINK_REL_NEXT_PROPERTY)");
                return;
            }
            Thread.sleep(TIME_BETWEEN_REQUESTS);
        }
        System.out.println("total_count: " + repos.total_count);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            System.out.println(response.body().string());
        }
    }

    public void runGetReposCommits() throws Exception {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addEncodedPathSegment("search")
                .addPathSegment("repositories")
                .addQueryParameter("q", "API+language:" + "java")
                .build();
        String s = "{\"type\":\"User\",\"site_admin\":false}";

        System.out.println("url: " + url.toString());
        Request request = (new Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        String textFromResponse = response.body().string();
        Repos repos = gson.fromJson(textFromResponse, Repos.class);
        System.out.println(textFromResponse);
        for (Repo repo : repos.items) {
            //System.out.println("login: " + user.full_name);
            System.out.println("description: " + repo.description);
            System.out.println("    has wiki: " + repo.has_wiki);
            System.out.println("    has pages: " + repo.has_pages);
            System.out.println("    html url: " + repo.html_url);
            System.out.println("    owner login: " + repo.owner.login);

            HttpUrl url1 = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                    .host("api.github.com")
                    .addPathSegment("repos")
                    .addPathSegment(repo.owner.login)
                    .addPathSegment(repo.name)
                    .addPathSegment("commits")
                    .build();

            System.out.println("url1: " + url1.toString());
            Request request1 = (new Request.Builder()).url(url1).build();
            Response response1 = client.newCall(request1).execute();
            String textFromResponse1 = response1.body().string();
            System.out.println("textFromResponse1: " + textFromResponse1);
            break;
            //System.out.println("watchers: " + user.watchers);
        }
        System.out.println("total_count: " + repos.total_count);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            System.out.println(response.body().string());
        }
    }

    public void runGetReposCommitsComments() throws Exception {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addEncodedPathSegment("search")
                .addPathSegment("repositories")
                .addQueryParameter("q", "API+language:" + "java")
                .build();
        String s = "{\"type\":\"User\",\"site_admin\":false}";

        System.out.println("url: " + url.toString());
        Request request = (new Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        String textFromResponse = response.body().string();
        Repos repos = gson.fromJson(textFromResponse, Repos.class);
        System.out.println(textFromResponse);
        for (Repo repo : repos.items) {
            //System.out.println("login: " + user.full_name);
            System.out.println("description: " + repo.description);
            System.out.println("    has wiki: " + repo.has_wiki);
            System.out.println("    has pages: " + repo.has_pages);
            System.out.println("    html url: " + repo.html_url);
            System.out.println("    owner login: " + repo.owner.login);

            HttpUrl url1 = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                    .host("api.github.com")
                    .addPathSegment("repos")
                    .addPathSegment(repo.owner.login)
                    .addPathSegment(repo.name)
                    .addPathSegment("comments")
                    .build();

            System.out.println("url1: " + url1.toString());
            Request request1 = (new Request.Builder()).url(url1).build();
            Response response1 = client.newCall(request1).execute();
            String textFromResponse1 = response1.body().string();
            System.out.println("textFromResponse1: " + textFromResponse1);

            break;
            //System.out.println("watchers: " + user.watchers);
        }
        System.out.println("total_count: " + repos.total_count);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            System.out.println(response.body().string());
        }
    }

    public void runGetReposPages() throws Exception {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addEncodedPathSegment("search")
                .addPathSegment("repositories")
                .addQueryParameter("q", "page+language:" + "java")
                .build();
        String s = "{\"type\":\"User\",\"site_admin\":false}";

        System.out.println("url: " + url.toString());
        Request request = (new Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        String textFromResponse = response.body().string();
        Repos repos = gson.fromJson(textFromResponse, Repos.class);
        System.out.println(textFromResponse);
        for (Repo repo : repos.items) {
            //System.out.println("login: " + user.full_name);
            System.out.println("description: " + repo.description);
            System.out.println("    has wiki: " + repo.has_wiki);
            System.out.println("    has pages: " + repo.has_pages);
            System.out.println("    html url: " + repo.html_url);
            System.out.println("    owner login: " + repo.owner.login);
            if (repo.has_pages) {
                HttpUrl url1 = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                        .host("api.github.com")
                        .addPathSegment("repos")
                        .addPathSegment(repo.owner.login)
                        .addPathSegment(repo.name)
                        .addPathSegment("pages")
                        .build();

                System.out.println("url1: " + url1.toString());
                Request request1 = (new Request.Builder()).url(url1).build();
                Response response1 = client.newCall(request1).execute();
                String textFromResponse1 = response1.body().string();
                System.out.println("textFromResponse1: " + textFromResponse1);

                break;
            }
            //System.out.println("watchers: " + user.watchers);
        }
        System.out.println("total_count: " + repos.total_count);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            System.out.println(response.body().string());
        }
    }

    public void runGetAllUsers() throws Exception {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addEncodedPathSegment("users")
                .build();

        System.out.println("url: " + url.toString());
        Request request = (new Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        String textFromResponse = response.body().string();
        User[] users = gson.fromJson(textFromResponse, User[].class);
        System.out.println(textFromResponse);
        for (User user : users) {
            System.out.println("login: " + user.login);
            System.out.println("repos_url: " + user.repos_url);

            //System.out.println("watchers: " + user.watchers);
        }

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            System.out.println(response.body().string());
        }
    }

    public void runGetAllGists() throws Exception {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addEncodedPathSegment("gists")
                .build();

        System.out.println("url: " + url.toString());
        Request request = (new Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        String textFromResponse = response.body().string();
        Gist[] gists = gson.fromJson(textFromResponse, Gist[].class);
        System.out.println(textFromResponse);
        Util.printGists(gists);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            System.out.println(response.body().string());
        }
    }

    public void runGetAllGistsWithPagination() throws Exception {
        int page = 1;
        Response response = runGistsRequest(page);
        String textFromResponse = response.body().string();
        System.out.println("url: " + response.request().urlString());
        System.out.println("textFromResponse: " + textFromResponse);
        Gist[] gists = gson.fromJson(textFromResponse, Gist[].class);
        System.out.println(textFromResponse);
        Util.printGists(gists);
        Thread.sleep(TIME_BETWEEN_REQUESTS);
        for (int i = Integer.valueOf(response.header("X-RateLimit-Remaining")), j = 2; i > 0; i--, j++) {
            Response response1 = (runGistsRequest(j));
            HashMap<String, String> rels = Util.getRelValueFromResponse(response1);
            String textFromResponse1 = response1.body().string();
            Gist[] gists1 = gson.fromJson(textFromResponse1, Gist[].class);
            System.out.println(textFromResponse1);
            Util.printGists(gists1);
            HttpUrl urlTest = (new HttpUrl.Builder()).scheme("https")
                    .host("api.github.com")
                    .addEncodedPathSegment("gists")
                    .addEncodedQueryParameter("page", String.valueOf(j + 1))
                    .build();
            String rellsNExt = rels.get(urlTest.toString());
            System.out.println("rellsNext: " + rellsNExt);
            if (!(rellsNExt).equals(LINK_REL_NEXT_PROPERTY)) {
                System.out.println("!rellsNExt.equals(LINK_REL_NEXT_PROPERTY)");
                return;
            }
            Thread.sleep(TIME_BETWEEN_REQUESTS);
        }
    }

    private Response runGistsRequest(int page) throws IOException {
        HttpUrl url1 = (new HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addEncodedPathSegment("gists")
                .addEncodedQueryParameter("page", String.valueOf(page))
                .build();
        System.out.println("url1: " + url1.toString());
        Request request1 = (new Request.Builder()).url(url1).build();
        Response response1 = client.newCall(request1).execute();
        //String textFromResponse1 = response1.body().string();
        //Gist[] gists1 = gson.fromJson(textFromResponse1, Gist[].class);
        //System.out.println(textFromResponse1);
        //System.out.println("Gists size1:"  + gists1.length);
        //printGists(gists1);
        System.out.println("X-RateLimit-Remaining: " + response1.header("X-RateLimit-Remaining"));
        if (!response1.isSuccessful()) {
            throw new IOException("Unexpected code " + response1);
        } else {
            //System.out.println(response1.body().string());
        }
        return response1;
    }

    private Response runSearchRequest(String searchQuery, String language, int page) throws IOException {
        HttpUrl url1 = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addEncodedPathSegment("search")
                .addPathSegment("repositories")
                .addQueryParameter("q", searchQuery/*.concat("+language:" + language)*/)
                .addEncodedQueryParameter("page", String.valueOf(page))
                .build();
        System.out.println("url1: " + url1.toString());
        Request request1 = (new Request.Builder()).url(url1).build();
        Response response1 = client.newCall(request1).execute();
        //String textFromResponse1 = response1.body().string();
        //Gist[] gists1 = gson.fromJson(textFromResponse1, Gist[].class);
        //System.out.println(textFromResponse1);
        //System.out.println("Gists size1:"  + gists1.length);
        //printGists(gists1);
        System.out.println("X-RateLimit-Remaining: " + response1.header("X-RateLimit-Remaining"));
        if (!response1.isSuccessful()) {
            throw new IOException("Unexpected code " + response1);
        } else {
            //System.out.println(response1.body().string());
        }
        return response1;
    }

    public void runGetAllOrgs() throws Exception {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addEncodedPathSegment("organizations")
                .build();

        System.out.println("url: " + url.toString());
        Request request = (new Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        String textFromResponse = response.body().string();
        Organisation[] users = gson.fromJson(textFromResponse, Organisation[].class);
        System.out.println(textFromResponse);
        for (Organisation org : users) {
            //System.out.println("login: " + user.full_name);
            System.out.println("login: " + org.login);
            System.out.println("description: " + org.description);
            System.out.println("url: " + org.url);
        }
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            System.out.println(response.body().string());
        }
    }

    public void runEmojis() throws Exception {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https").host("api.github.com").addEncodedPathSegment("emojis").build();
        System.out.println("url: " + url.toString());
        Request request = (new Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            JsonParser parser = new JsonParser();
            JsonObject o = (JsonObject) parser.parse(response.body().string());
            for (Map.Entry entry : o.entrySet()) {
                System.out.println(entry);
            }
        }
    }

    public void runRepoReadme() throws IOException {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addPathSegment("repos")
                .addPathSegment("OpenF2")
                .addPathSegment("F2")
                .addPathSegment("readme")
                .build();

        System.out.println("url1: " + url.toString());
        Request request1 = (new Request.Builder()).url(url).build();
        Response response1 = client.newCall(request1).execute();
        String textFromResponse1 = response1.body().string();
        System.out.println("textFromResponse1: " + textFromResponse1);
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(textFromResponse1);
        JsonPrimitive jsoPrimitive = object.getAsJsonPrimitive("download_url");
        System.out.println(jsoPrimitive.getAsString());
        Request request = (new Request.Builder()).url(jsoPrimitive.getAsString()).build();
        Response response = client.newCall(request).execute();
        String textFromResponse = response.body().string();
        System.out.println(textFromResponse);
        if (!response1.isSuccessful()) {
            throw new IOException("Unexpected code " + response1);
        } else {
            System.out.println(response1.body().string());
        }
    }

    public void authorizeGitHub() throws IOException {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addPathSegment("authorizations")
                        //.addQueryParameter("access_token", "64d21810c8ca4b33abde28a17f1e0012fb318eb4")
                .build();

        RequestBody postBody = new MultipartBuilder()
                .addFormDataPart("note", "Testing Api")
                .addFormDataPart("client_id", "a930d168ba1f8a3e82f4")
                .addFormDataPart("client_secret", "f294185856498e98f700de0df30e647bf6dde8d6")
                .build();

        Request request = (new Request.Builder()).url(url).post(postBody).build();
        System.out.println("Request auth: " + request.toString());

        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

    /**
     * Requires authenication
     * https://api.github.com/?access_token=OAUTH-TOKEN
     * 64d21810c8ca4b33abde28a17f1e0012fb318eb4
     */
    public void getAuthorizationsGitHub() throws IOException {
        HttpUrl url = (new com.squareup.okhttp.HttpUrl.Builder()).scheme("https")
                .host("api.github.com")
                .addPathSegment("authorizations")
                        //.addQueryParameter("access_token", "64d21810c8ca4b33abde28a17f1e0012fb318eb4")
                .build();

        System.out.println("url: " + url.toString());
        Request request = (new Request.Builder()).url(url).build();
        Response response = client.newCall(request).execute();
        String textFromResponse = response.body().string();
        System.out.println("Authorization responce: " + textFromResponse);
//        Request request = (new Request.Builder()).url(url).put(RequestBody.create(MEDIA_TYPE_MARKDOWN,
//                postBody)).header("X-Parse-Application-Id", "OMgZ9RCf2aZyCanEbK2CWPEqsHp0TtnfIbU032ba")
//                .header("X-Parse-REST-API-Key", "9VFnUm2uep7bXBaS4LI20LdZDCnLk1jr0WQjJSdQ")
//                .header("Content-Type", "application/json").build();
//        Response response = client.newCall(request).execute();
//        System.out.println(response.body().string());
    }
}

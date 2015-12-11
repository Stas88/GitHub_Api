package net.util;

import com.squareup.okhttp.Response;
import net.model.Gist;
import net.model.Repo;
import net.model.Repos;

import java.util.HashMap;

/**
 * Created by stassikorskyi on 30.11.15.
 */
public class Util {

    public static void printGists(Gist[] gists1) {
        for (Gist gist : gists1) {
            System.out.println("repos_url: " + gist.html_url);
            System.out.println("description: " + gist.description);
        }
    }

    public static void printRepos(Repos repos) {
        for (Repo repo : repos.items) {
            //System.out.println("login: " + user.full_name);
            System.out.println("description: " + repo.description);
            System.out.println("    has wiki: " + repo.has_wiki);
            System.out.println("    has pages: " + repo.has_pages);
            System.out.println("    html url: " + repo.html_url);
            System.out.println("    owner login: " + repo.owner.login);
            //System.out.println("watchers: " + user.watchers);
        }
    }

    public static void printRepo(Repo repo) {
        System.out.println("description: " + repo.description);
        System.out.println("    has wiki: " + repo.has_wiki);
        System.out.println("    has pages: " + repo.has_pages);
        System.out.println("    html url: " + repo.html_url);
        System.out.println("    owner login: " + repo.owner.login);
    }


    public static HashMap getRelValueFromResponse(Response response) {
        String url = response.request().urlString();
        String linkHeaerToParse = response.header("Link");
        String rel = linkHeaerToParse.substring(url.toString().length() + 4, url.toString().length() + 3 + 11);
        System.out.println("Link: " + response.header("Link"));
        String[] arrayItems = (linkHeaerToParse.split(","));
        HashMap linkHeaderItems = new HashMap<String, String>();
        for (String item : arrayItems) {
            String link = item.substring(item.indexOf('<') + 1, item.indexOf('>'));
            String relValue = item.substring(item.indexOf('\"') + 1, item.length() - 1);
//            System.out.println("key: " + link);
//            System.out.println("value: " + relValue);
            linkHeaderItems.put(link, relValue);
        }

        return linkHeaderItems;
    }
}

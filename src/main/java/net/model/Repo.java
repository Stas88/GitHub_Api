package net.model;

import com.google.gson.annotations.Expose;

/**
 * Created by stassikorskyi on 26.11.15.
 */
public class Repo {

    @Expose
    public String full_name;

    @Expose
    public String description;
    public int watchers;
    public int forks;
    public boolean has_wiki;
    public boolean has_pages;

    @Expose
    public int stargazers_count;

    @Expose
    public String html_url;
    public String url;

    public String name;

    public User owner;

}

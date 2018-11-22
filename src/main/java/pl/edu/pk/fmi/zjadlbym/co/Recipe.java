package pl.edu.pk.fmi.zjadlbym.co;

import java.net.URL;

public class Recipe {
    private String name;
    private String url;

    public Recipe(String name, URL url) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Provided name is invalid");
        }

        if(url == null) {
            throw new IllegalArgumentException("Provided URL is invalid");
        }

        this.name = name;
        this.url = url.toString();
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}

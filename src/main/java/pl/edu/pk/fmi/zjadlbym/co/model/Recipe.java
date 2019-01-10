package pl.edu.pk.fmi.zjadlbym.co.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recipe implements Serializable {
    @JsonProperty("title")
    private String name = "Missing Recipe Name";

    @JsonProperty("href")
    private String url = "Missing URL";

    @JsonProperty("ingredients")
    private String ingredients;

    private List<String> ingredientsNames;

    @JsonProperty("thumbnail")
    private String imageUrl = "https://aka.weightwatchers.ca/images/4105/dynamic/foodandrecipes/2013/06/us_img_recipe_default_600x600.jpg";

    public Recipe() {
        ingredientsNames = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url.trim();
    }

    public String getIngredients() {
        return ingredients.trim();
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
        createIngredientsList(ingredients);
    }

    public String getImageUrl() {
        return imageUrl.trim();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl.trim();
    }

    public List<String> getIngredientsNames() {
        return ingredientsNames;
    }

    private void createIngredientsList(String ingredientsNames) {
        this.ingredientsNames = Arrays.asList(ingredientsNames.split(",\\s"));
    }
}

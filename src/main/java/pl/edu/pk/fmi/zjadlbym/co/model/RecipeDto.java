package pl.edu.pk.fmi.zjadlbym.co.model;

public class RecipeDto
{
    private String name;
    private String url;
    private String thumbnail;

    public RecipeDto(Recipe recipe)
    {
        name = recipe.getName();
        url = recipe.getUrl();
        thumbnail = recipe.getImageUrl();
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }

    public String getThumbnail()
    {
        return thumbnail;
    }
}

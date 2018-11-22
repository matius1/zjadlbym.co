package pl.edu.pk.fmi.zjadlbym.co.config;

import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.pk.fmi.zjadlbym.co.Recipe;

import java.net.MalformedURLException;
import java.net.URL;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/przepis")
public class RestController {

    @RequestMapping("/get")
    public Recipe[] przepisyGet() throws MalformedURLException {
        Recipe[] recipes = new Recipe[] {
                new Recipe(
                        "Spaghetti",
                        new URL("https://www.jamieoliver.com/recipes/pasta-recipes/classic-tomato-spaghetti/")),
                new Recipe(
                        "Scrambled eggs",
                        new URL("https://www.jamieoliver.com/news-and-features/features/how-to-make-perfect-scrambled-eggs/"))
        };

        return recipes;
    }

}

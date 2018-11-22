package pl.edu.pk.fmi.zjadlbym.co.config;

import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.pk.fmi.zjadlbym.co.RecipeDto;

import java.net.MalformedURLException;
import java.net.URL;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/przepis")
public class RestController {

    @RequestMapping("/get")
    public RecipeDto[] przepisyGet() throws MalformedURLException {
        RecipeDto[] recipeDtos = new RecipeDto[] {
                new RecipeDto(
                        "Spaghetti",
                        new URL("https://www.jamieoliver.com/recipeDtos/pasta-recipeDtos/classic-tomato-spaghetti/")),
                new RecipeDto(
                        "Scrambled eggs",
                        new URL("https://www.jamieoliver.com/news-and-features/features/how-to-make-perfect-scrambled-eggs/"))
        };

        return recipeDtos;
    }

}

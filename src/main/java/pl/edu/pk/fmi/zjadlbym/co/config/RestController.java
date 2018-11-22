package pl.edu.pk.fmi.zjadlbym.co.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import pl.edu.pk.fmi.zjadlbym.co.model.RecipeDto;
import pl.edu.pk.fmi.zjadlbym.co.model.RecipesHolder;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/przepis")
public class RestController
{
    private static final String RECIPE_URL = "http://www.recipepuppy.com/api/?i=";

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    public RestController()
    {
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();
    }

    @RequestMapping("/get")
    public ResponseEntity<RecipeDto[]> przepisyGet(String ingredients)
    {
        ResponseEntity<String> response = restTemplate.getForEntity(RECIPE_URL + ingredients, String.class);

        if (OK.equals(response.getStatusCode()))
        {
            try
            {
                RecipesHolder recipesHolder = objectMapper.readValue(response.getBody(), RecipesHolder.class);
                RecipeDto[] dtos = GetRecipeDtos(recipesHolder);
                return new ResponseEntity<>(dtos, HttpStatus.OK);
            }
            catch (IOException e)
            {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private RecipeDto[] GetRecipeDtos(RecipesHolder recipesHolder)
    {
        return recipesHolder
                .getRecipes()
                .stream()
                .limit(5)
                .map(RecipeDto::new)
                .toArray(RecipeDto[]::new);
    }
}

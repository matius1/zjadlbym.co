package pl.edu.pk.fmi.zjadlbym.co.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import pl.edu.pk.fmi.zjadlbym.co.model.Recipe;
import pl.edu.pk.fmi.zjadlbym.co.model.RecipesHolder;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/przepis")
public class RestController
{
    private static final String RECIPE_URL = "http://www.recipepuppy.com/api/?i=";
    private static final String EMPTY_JSON = "{}";
    private static final String ERROR_MESSAGE = "Retrieving recipes failed";

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private Gson json;

    public RestController()
    {
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();
        json = new Gson();
    }

    @RequestMapping("/get")
    public String przepisyGet()
    {
        String ingredientsToFind = "onions,garlic";
        ResponseEntity<String> response = restTemplate.getForEntity(RECIPE_URL + ingredientsToFind, String.class);

        if (OK.equals(response.getStatusCode()))
        {
            try
            {
                RecipesHolder recipesHolder = objectMapper.readValue(response.getBody(), RecipesHolder.class);
                return getResultingRecipesNamesAsJson(recipesHolder);
            }
            catch (IOException e)
            {
                return ERROR_MESSAGE;
            }
        }

        return EMPTY_JSON;
    }

    private String getResultingRecipesNamesAsJson(RecipesHolder recipesHolder)
    {
        List<String> resultingRecipesNames = recipesHolder.getRecipes().stream()
                .limit(5)
                .map(Recipe::getName)
                .collect(toList());
        return json.toJson(resultingRecipesNames);
    }
}

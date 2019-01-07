package pl.edu.pk.fmi.zjadlbym.co.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import pl.edu.pk.fmi.zjadlbym.co.model.Recipe;
import org.springframework.web.util.WebUtils;
import org.springframework.web.util.WebUtils;
import pl.edu.pk.fmi.zjadlbym.co.model.RecipeDto;
import pl.edu.pk.fmi.zjadlbym.co.model.RecipesHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.*;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/przepis")
public class RestController
{
    private Logger logger = LoggerFactory.getLogger(RestController.class);

    private static final String RECIPE_URL = "http://www.recipepuppy.com/api/?i=";
    private static final String PAGE = "&p=";
    private static final String COMMA = ",";
    private final String cookieName = "zjadlbym.co";

    private Map<String, List<Map<Instant, String>>> sessions = new HashMap<>();

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/history")
    public List historyGet(HttpServletRequest request)
    {
        Cookie cookie = WebUtils.getCookie(request, cookieName);

        String sessionID = cookie != null ? cookie.getValue() : "";

        List<Map<Instant, String>> history = sessions.getOrDefault(sessionID, Collections.emptyList());
        logger.info("Getting history for : {} - {}", sessionID, history);

        return history;
    }

    @RequestMapping("/get")
    public ResponseEntity<RecipeDto[]> przepisyGet(String ingredients, HttpServletRequest request)
    {
        logger.info("Request: {}", ingredients);

        HttpHeaders sessionHeaders = manageSessions(ingredients, request);

        //TODO: sebastianpolanski - to be changed for values taken from user
        int numberOfRecipesToShow = 5;
        int maxNoOfMissingIngredients = 5;
        String ingredientsToExclude = "onion,garlic";

        String ingredientsToSearch =
                toLowerCase(ingredients) + COMMA + getIngredientsToExclude(toLowerCase(ingredientsToExclude));

        try
        {
            List<Recipe> recipesToShow =
                    new ArrayList<>(getRecipesToShow(ingredientsToSearch, ingredients,
                            maxNoOfMissingIngredients, numberOfRecipesToShow));

            RecipeDto[] res = getRecipeDtos(recipesToShow, numberOfRecipesToShow);

            if (res.length > 0)
            {
                return ResponseEntity.ok().headers(sessionHeaders).body(res);
            }

            return ResponseEntity.noContent().headers(sessionHeaders).build();

        }
        catch (IOException e)
        {
            return ResponseEntity.badRequest().headers(sessionHeaders).build();
        }
    }

    private String toLowerCase(String text)
    {
        List<String> textAsList = asList(text.split(COMMA));
        textAsList.replaceAll(String::toLowerCase);
        return String.join(COMMA, textAsList);
    }

    private List<Recipe> getRecipesToShow(String ingredientsToSearch, String ownedIngredients,
                                  int maxNoOfMissingIngredients, int numberOfRecipesToShow) throws IOException
    {
        List<Recipe> recipesToShow = new ArrayList<>();
        int recipesPage = 1;
        RecipesHolder response;

        do
        {
            response = getRecipesFromProvider(ingredientsToSearch, recipesPage++);
            recipesToShow.addAll(filterRecipesWithMissingIngredients(response, ownedIngredients,
                    maxNoOfMissingIngredients));
        }
        while (recipesToShow.size() < numberOfRecipesToShow && !response.getRecipes().isEmpty());

        return recipesToShow;
    }

    private RecipesHolder getRecipesFromProvider(String ingredients, int page) throws IOException
    {
        ResponseEntity<String> response =
                restTemplate.getForEntity(RECIPE_URL + ingredients + PAGE + page, String.class);

        if (OK.equals(response.getStatusCode()))
        {
            return objectMapper.readValue(response.getBody(), RecipesHolder.class);
        }

        return new RecipesHolder();
    }

    private String getIngredientsToExclude(String ingredients)
    {
        List<String> ingredientsToExclude = asList(ingredients.split(COMMA));
        ingredientsToExclude.replaceAll(i -> "-" + i);
        return String.join(COMMA, ingredientsToExclude);
    }

    private List<Recipe> filterRecipesWithMissingIngredients(RecipesHolder recipes, String ingredients,
                                                             int maxNoOfMissingIngredients)
    {
        List<String> ownedIngredients = new ArrayList<>(asList(ingredients.split(COMMA)));

        return recipes.getRecipes()
                .stream()
                .filter(r -> hasValidNumberOfMissingIngredients(r, ownedIngredients, maxNoOfMissingIngredients))
                .collect(toList());
    }

    private boolean hasValidNumberOfMissingIngredients(Recipe recipe, List<String> ownedIngredients,
                                                       int maxNoOfMissingIngredients)
    {
        List<String> recipeIngredients = new ArrayList<>(recipe.getIngredientsNames());
        recipeIngredients.removeAll(ownedIngredients);
        return recipeIngredients.size() <= maxNoOfMissingIngredients;
    }

    private RecipeDto[] getRecipeDtos(List<Recipe> recipes, int numberOfShownRecipes)
    {
        return recipes
                .stream()
                .limit(numberOfShownRecipes)
                .map(RecipeDto::new)
                .toArray(RecipeDto[]::new);
    }

    private HttpHeaders manageSessions(String ingredients, HttpServletRequest request)
    {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        String sessionID = cookie != null ? cookie.getValue() : "";
        HttpHeaders returnCookie = new HttpHeaders();

        if (sessionID.isEmpty())
        {
            String newID = String.valueOf(UUID.randomUUID());
            addToHistory(ingredients, newID);
            returnCookie.add("Set-Cookie", cookieName + "=" + newID);

        }
        else
        {
            addToHistory(ingredients, sessionID);
            returnCookie.add("Set-Cookie", cookieName + "=" + sessionID);
        }

        return returnCookie;
    }

    private void addToHistory(String ingredients, String sessionID)
    {
        Map<Instant, String> record = Collections.singletonMap(Instant.now(), ingredients);
        List<Map<Instant, String>> history = sessions.getOrDefault(sessionID, new ArrayList<>());
        history.add(record);

        logger.info("Add to session cache : {} - {}", sessionID, record);

        sessions.put(sessionID, history);
    }
}

package pl.edu.pk.fmi.zjadlbym.co.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.WebUtils;
import pl.edu.pk.fmi.zjadlbym.co.model.RecipeDto;
import pl.edu.pk.fmi.zjadlbym.co.model.RecipesHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.springframework.http.HttpStatus.OK;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/przepis")
public class RestController {
    String cookieName = "zjadlbym.co";

    private Map<String, List<Map<Instant, String>>> sessions = new HashMap<>();

    private static final String RECIPE_URL = "http://www.recipepuppy.com/api/?i=";
    private Logger logger = LoggerFactory.getLogger(RestController.class);

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    public RestController() {
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();
    }

    @RequestMapping("/history")
    public List historyGet(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);

        String sessionID = cookie != null ? cookie.getValue() : "";

        List<Map<Instant, String>> history = sessions.getOrDefault(sessionID, Collections.emptyList());
        logger.info("Getting history for : {} - {}", sessionID, history);

        return history;
    }

    @RequestMapping("/get")
    public ResponseEntity<RecipeDto[]> przepisyGet(String ingredients, HttpServletRequest request) {
        logger.info("Request: {}", ingredients);

        Cookie cookie = WebUtils.getCookie(request, cookieName);
        String sessionID = cookie != null ? cookie.getValue() : "";
        HttpHeaders sessionHeaders = manageSessions(ingredients, sessionID);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(RECIPE_URL + ingredients, String.class);

        if (OK.equals(responseEntity.getStatusCode())) {
            try {
                RecipesHolder recipesHolder = objectMapper.readValue(responseEntity.getBody(), RecipesHolder.class);
                RecipeDto[] dtos = GetRecipeDtos(recipesHolder);

                return new ResponseEntity<>(dtos, HttpStatus.OK);

            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private HttpHeaders manageSessions(String ingredients, String cookie) {
        if (cookie.isEmpty()) {
            double id = new Random().nextDouble();
            String newCookie = String.valueOf(id);
            addToHistory(ingredients, cookie, newCookie);
        } else {
            addToHistory(ingredients, cookie, cookie);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", cookieName + "=" + "value");
        return headers;
    }

    private void addToHistory(String ingredients, String cookie, String newCookie) {
        Map<Instant, String> record = Collections.singletonMap(Instant.now(), ingredients);
        List<Map<Instant, String>> history = sessions.getOrDefault(cookie, new ArrayList<>());
        history.add(record);
        logger.info("Add to session cache : {} - {}", newCookie, record);

        sessions.put(newCookie, history);
    }

    private RecipeDto[] GetRecipeDtos(RecipesHolder recipesHolder) {
        return recipesHolder
                .getRecipes()
                .stream()
                .limit(5)
                .map(RecipeDto::new)
                .toArray(RecipeDto[]::new);
    }
}

package pl.edu.pk.fmi.zjadlbym.co.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.WebUtils;
import pl.edu.pk.fmi.zjadlbym.co.model.RecipeDto;
import pl.edu.pk.fmi.zjadlbym.co.model.RecipesHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.springframework.http.HttpStatus.OK;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/przepis")
public class RestController {
    private Logger logger = LoggerFactory.getLogger(RestController.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate restTemplate = new RestTemplate();

    private static final String RECIPE_URL = "http://www.recipepuppy.com/api/?i=";

    private final String cookieName = "zjadlbym.co";
    private Map<String, List<Map<Instant, String>>> sessions = new HashMap<>();

    @RequestMapping("/history")
    public List historyGet(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);

        String sessionID = cookie != null ? cookie.getValue() : "";

        List<Map<Instant, String>> history = sessions.getOrDefault(sessionID, Collections.emptyList());
        logger.info("Getting history for : {} - {}", sessionID, history);

        return history;
    }

    @RequestMapping("/get")
    public ResponseEntity przepisyGet(String ingredients, HttpServletRequest request) {
        logger.info("Request: {}", ingredients);

        HttpHeaders sessionHeaders = manageSessions(ingredients, request);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(RECIPE_URL + ingredients, String.class);

        if (OK.equals(responseEntity.getStatusCode())) {
            try {
                RecipesHolder recipesHolder = objectMapper.readValue(responseEntity.getBody(), RecipesHolder.class);
                RecipeDto[] dtos = GetRecipeDtos(recipesHolder);

                return ResponseEntity.ok().headers(sessionHeaders).body(dtos);

            } catch (IOException e) {
                return ResponseEntity.badRequest().headers(sessionHeaders).build();
            }
        }

        return ResponseEntity.noContent().headers(sessionHeaders).build();
    }

    private HttpHeaders manageSessions(String ingredients, HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        String sessionID = cookie != null ? cookie.getValue() : "";
        HttpHeaders returnCookie = new HttpHeaders();

        if (sessionID.isEmpty()) {
            String newID = String.valueOf(UUID.randomUUID());
            addToHistory(ingredients, newID);
            returnCookie.add("Set-Cookie", cookieName + "=" + newID);

        } else {
            addToHistory(ingredients, sessionID);
            returnCookie.add("Set-Cookie", cookieName + "=" + sessionID);
        }

        return returnCookie;
    }

    private void addToHistory(String ingredients, String sessionID) {
        Map<Instant, String> record = Collections.singletonMap(Instant.now(), ingredients);
        List<Map<Instant, String>> history = sessions.getOrDefault(sessionID, new ArrayList<>());
        history.add(record);

        logger.info("Add to session cache : {} - {}", sessionID, record);

        sessions.put(sessionID, history);
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

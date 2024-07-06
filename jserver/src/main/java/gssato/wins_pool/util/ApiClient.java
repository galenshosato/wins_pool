package gssato.wins_pool.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

public class ApiClient {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public ApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    //TODO Look into RestTemplate and how to pull the data from the ESPN website
    // Can probably do some methods that update the winner of a match as well, but that might be too ambitious right now
    // Focus on getting team information first
}

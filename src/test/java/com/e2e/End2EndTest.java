package com.e2e;

import com.app.ScoringApp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@SpringBootTest(
        classes = ScoringApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = {
        "github.token=",
        "github.base-url=https://api.github.com"
})
class End2EndTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate http;

    @Autowired
    RestTemplate restTemplate;

    ObjectMapper om = new ObjectMapper();

    @Test
    void popularRepositories_endToEnd_OK() throws Exception {
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate)
                .ignoreExpectOrder(true)
                .build();

        String body = """
            {
              "total_count": 2,
              "incomplete_results": false,
              "items": [
                {
                  "full_name": "o/a",
                  "html_url": "https://gh/a",
                  "language": "Java",
                  "stargazers_count": 123,
                  "forks_count": 7,
                  "pushed_at": "2025-01-01T00:00:00Z",
                  "archived": false
                },
                {
                  "full_name": "o/b",
                  "html_url": "https://gh/b",
                  "language": "Java",
                  "stargazers_count": 45,
                  "forks_count": 3,
                  "pushed_at": "2024-06-01T00:00:00Z",
                  "archived": false
                }
              ]
            }
        """;
        server.expect(once(), requestTo(containsString("/search/repositories")))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        String url = "http://localhost:" + port + "/api/repos/popular?created_from=2024-01-01&language=Java&limit=2";
        ResponseEntity<String> resp = http.getForEntity(url, String.class);

        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isNotNull();

        JsonNode arr = om.readTree(resp.getBody());
        assertThat(arr.isArray()).isTrue();
        assertThat(arr.size()).isEqualTo(2);

        JsonNode first = arr.get(0);
        assertThat(first.get("fullName").asText()).isEqualTo("o/a");
        assertThat(first.get("url").asText()).isEqualTo("https://gh/a");
        assertThat(first.get("language").asText()).isEqualTo("Java");
        assertThat(first.get("stars").asInt()).isEqualTo(123);
        assertThat(first.get("forks").asInt()).isEqualTo(7);
        assertThat(first.get("pushedAt").asText()).isEqualTo("2025-01-01T00:00:00Z");
        assertThat(first.has("score")).isTrue(); // score is computed dynamically

        server.verify();
    }
}

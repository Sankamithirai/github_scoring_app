package com.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GitHubClientTest {

    @Test
    void searchRepos_parsesResponseIntoMapList() {
        RestTemplate rt = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(rt);

        String body = """
        {
          "items": [
            {
              "full_name": "o/r1",
              "html_url": "https://gh/r1",
              "language": "Java",
              "stargazers_count": 10,
              "forks_count": 2,
              "pushed_at": "2025-10-20T00:00:00Z",
              "archived": false
            },
            {
              "full_name": "o/r2",
              "html_url": "https://gh/r2",
              "language": "Java",
              "stargazers_count": 5,
              "forks_count": 1,
              "pushed_at": "2025-09-01T00:00:00Z",
              "archived": false
            }
          ]
        }
        """;

        server.expect(once(), requestTo(containsString("/search/repositories")))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        GitHubClient client = new GitHubClient(rt, "http://example");

        List<Map<String, Object>> repos = client.searchRepos("2024-01-01", "Java", 5);

        assertEquals(2, repos.size());
        assertEquals("o/r1", repos.get(0).get("full_name"));
        assertEquals("https://gh/r1", repos.get(0).get("html_url"));
        assertEquals(false, repos.get(0).get("archived"));

        server.verify();
    }
}

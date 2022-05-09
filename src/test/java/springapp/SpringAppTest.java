package springapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringAppTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    void test() {
        EntityExchangeResult<String> result = webTestClient.get().uri("/").exchange().expectBody(String.class).returnResult();
        if (!Objects.equals(result.getResponseBody(), "hi")) {
            throw new AssertionError("expected 'hi', got: " + result.getResponseBody());
        }
    }
}

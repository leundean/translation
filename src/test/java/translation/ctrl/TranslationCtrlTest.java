package translation.ctrl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import translation.model.Translation;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class TranslationCtrlTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testThatTranslationIsCreatedAndRetrieved() {
        String key = "open";
        Map<String, String> tls = new HashMap<>();
        tls.put("se", "öppna");
        tls.put("de", "öffnen");
        Translation translation = new Translation(key, tls);

        webTestClient.post().uri("/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(translation), Translation.class)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.get().uri("/se/" + key)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.key").isEqualTo(key)
                .jsonPath("$.tl").isEqualTo("öppna");

        // Delete to clean up DB
        webTestClient.delete().uri("/delete/" + key)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testThatBadSearchesGiveHttpResponseNoContent() {
        String key = "open";
        Map<String, String> tls = new HashMap<>();
        tls.put("se", "öppna");
        tls.put("de", "öffnen");
        Translation translation = new Translation(key, tls);

        webTestClient.post().uri("/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(translation), Translation.class)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.get().uri("/se/key-that-is-not-present")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get().uri("/lang-that-is-not-present/" + key)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isNoContent();


        // Delete to clean up DB
        webTestClient.delete().uri("/delete/" + key)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk();

    }
}
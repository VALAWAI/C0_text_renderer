/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/
package eu.valawai.c0_text_renderer.api.v1.texts;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.valawai.c0_text_renderer.messages.TextPayload;
import eu.valawai.c0_text_renderer.services.ReceivedText;
import eu.valawai.c0_text_renderer.services.ReceivedTextStoreService;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;
import net.datafaker.Faker;

/**
 * Test the API to manage the texts.
 * 
 * @see TextsResources
 * 
 * @author VALAWAI
 */
@QuarkusTest
public class TextsResourcesTest {

    /**
     * Random values generator.
     */
    private static final Faker faker = new Faker();

    /**
     * The service to manage the stored received texts.
     */
    @Inject
    ReceivedTextStoreService storageService;

    /**
     * Purge the store before each test.
     */
    @BeforeEach
    public void setUp() {

        this.storageService.purge()
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted();
    }

    /**
     * Check that getting a page returns an empty page when no texts exist.
     */
    @Test
    public void shouldGetEmptyPageWhenNoTexts() {

        final var page = given().when().get("/v1/texts").then()
                .statusCode(Status.OK.getStatusCode())
                .extract().as(ReceivedTextPage.class);

        assertThat(page, notNullValue());
        assertThat(page.total, is(0L));
        assertThat(page.receivedTexts, nullValue());
    }

    /**
     * Check that getting a page uses default pagination parameters (offset=0, limit=10).
     */
    @Test
    public void shouldGetPageWithDefaultPagination() {

        final int totalTexts = 15;
        for (int i = 0; i < totalTexts; i++) {
            this.insertRandomPayload();
        }

        final var page = given().when().get("/v1/texts").then()
                .statusCode(Status.OK.getStatusCode())
                .extract().as(ReceivedTextPage.class);

        assertThat(page, notNullValue());
        assertThat(page.total, is((long) totalTexts));
        assertThat(page.receivedTexts, notNullValue());
        assertThat(page.receivedTexts, hasSize(10));
    }

    /**
     * Check that getting a page respects custom offset and limit parameters.
     */
    @Test
    public void shouldGetPageWithOffsetAndLimit() {

        final int totalTexts = 5;
        final List<ReceivedText> inserted = new ArrayList<>();
        for (int i = 0; i < totalTexts; i++) {
            inserted.add(this.insertRandomPayload());
        }

        final int offset = 1;
        final int limit = 2;
        final var page = given().queryParam("offset", offset).queryParam("limit", limit)
                .when().get("/v1/texts").then()
                .statusCode(Status.OK.getStatusCode())
                .extract().as(ReceivedTextPage.class);

        assertThat(page, notNullValue());
        assertThat(page.total, is((long) totalTexts));
        assertThat(page.receivedTexts, notNullValue());
        assertThat(page.receivedTexts, hasSize(limit));
        assertThat(page.receivedTexts.get(0).id, is(inserted.get(1).id));
        assertThat(page.receivedTexts.get(1).id, is(inserted.get(2).id));
    }

    /**
     * Check that getting a page returns 400 when offset is negative.
     */
    @Test
    public void shouldReturnBadRequestWhenNegativeOffset() {

        given().queryParam("offset", -1).when().get("/v1/texts").then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Check that getting a page returns 400 when limit is less than 1.
     */
    @Test
    public void shouldReturnBadRequestWhenLimitBelowOne() {

        given().queryParam("limit", 0).when().get("/v1/texts").then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Check that retrieving a stored received text by its identifier succeeds.
     */
    @Test
    public void shouldGetReceivedTextById() {

        final var inserted = this.insertRandomPayload();

        final var retrieved = given().pathParam("id", inserted.id)
                .when().get("/v1/texts/{id}").then()
                .statusCode(Status.OK.getStatusCode())
                .extract().as(ReceivedText.class);

        assertThat(retrieved, notNullValue());
        assertThat(retrieved, equalTo(inserted));
    }

    /**
     * Check that retrieving a non-existent received text returns 404 NOT FOUND.
     */
    @Test
    public void shouldNotGetUndefinedReceivedText() {

        final long undefinedId = 99999L;

        given().pathParam("id", undefinedId)
                .when().get("/v1/texts/{id}").then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Build a random valid {@link TextPayload}.
     */
    private TextPayload nextPayload() {

        final var payload = new TextPayload();
        payload.mime_type = "text/plain";
        payload.title = faker.lorem().word();
        payload.content = faker.lorem().sentence();
        return payload;
    }

    /**
     * Insert a random payload and return the resulting {@link ReceivedText}.
     */
    private ReceivedText insertRandomPayload() {

        return this.storageService.insert(this.nextPayload())
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted()
                .getItem();
    }

}

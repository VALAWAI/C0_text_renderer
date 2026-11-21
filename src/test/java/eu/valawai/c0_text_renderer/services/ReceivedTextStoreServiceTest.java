/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c0_text_renderer.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.valawai.c0_text_renderer.messages.TextPayload;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import jakarta.inject.Inject;
import net.datafaker.Faker;

/**
 * Unit test for {@link ReceivedTextStoreService}
 *
 * @see ReceivedTextStoreService
 */
@QuarkusTest
public class ReceivedTextStoreServiceTest {

  /**
   * Random values generator.
   */
  private static final Faker faker = new Faker();

  /**
   * The service to test.
   */
  @Inject
  ReceivedTextStoreService service;

  /**
   * Purge the store before each test so they run independently.
   */
  @BeforeEach
  public void setUp() {

    this.service.purge()
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted();
  }

  // -------------------------------------------------------------------------
  // purge
  // -------------------------------------------------------------------------

  /**
   * Check that purge removes all stored texts.
   */
  @Test
  public void shouldPurgeClearAllTexts() {

    insertRandomPayload();
    insertRandomPayload();

    this.service.purge()
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted();

    final var page = this.service.getReceivedTextPage(0, 100)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(page.total, is(0L));
    assertThat(page.receivedTexts, nullValue());
  }

  // -------------------------------------------------------------------------
  // insert
  // -------------------------------------------------------------------------

  /**
   * Check that inserting a payload with all fields returns a populated ReceivedText.
   */
  @Test
  public void shouldInsertWithAllFields() {

    final var payload = nextPayload();

    final var result = this.service.insert(payload)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(result, notNullValue());
    assertThat(result.id, greaterThan(0L));
    assertThat(result.mimeType, is(payload.mime_type));
    assertThat(result.title, is(payload.title));
    assertThat(result.content, is(payload.content));
  }

  /**
   * Check that inserting a payload without title generates a default title.
   */
  @Test
  public void shouldInsertWithDefaultTitleWhenTitleIsNull() {

    final var payload = nextPayload();
    payload.title = null;

    final var result = this.service.insert(payload)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(result, notNullValue());
    assertThat(result.title, notNullValue());
    assertThat(result.title, containsString(String.valueOf(result.id)));
  }

  /**
   * Check that inserting a payload with an empty title generates a default title.
   */
  @Test
  public void shouldInsertWithDefaultTitleWhenTitleIsEmpty() {

    final var payload = nextPayload();
    payload.title = "";

    final var result = this.service.insert(payload)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(result, notNullValue());
    assertThat(result.title, notNullValue());
    assertThat(result.title, containsString(String.valueOf(result.id)));
  }

  /**
   * Check that each inserted text gets an incremental id.
   */
  @Test
  public void shouldInsertAssignIncrementalIds() {

    final var first = this.service.insert(nextPayload())
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    final var second = this.service.insert(nextPayload())
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(first, notNullValue());
    assertThat(second, notNullValue());
    assertThat(second.id, greaterThan(first.id));
  }

  // -------------------------------------------------------------------------
  // get
  // -------------------------------------------------------------------------

  /**
   * Check that an inserted text can be retrieved by id.
   */
  @Test
  public void shouldGetExistingText() {

    final var inserted = insertRandomPayload();

    final var fetched = this.service.get(inserted.id)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(fetched, notNullValue());
    assertThat(fetched.id, is(inserted.id));
    assertThat(fetched.mimeType, is(inserted.mimeType));
    assertThat(fetched.title, is(inserted.title));
    assertThat(fetched.content, is(inserted.content));
  }

  /**
   * Check that getting a non-existing id fails with an IllegalArgumentException.
   */
  @Test
  public void shouldGetFailForUnknownId() {

    final var failure = this.service.get(Long.MAX_VALUE)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitFailure()
        .getFailure();

    assertThat(failure, instanceOf(IllegalArgumentException.class));
  }

  // -------------------------------------------------------------------------
  // getLastReceivedTextId
  // -------------------------------------------------------------------------

  /**
   * Check that the last id grows with every insert.
   */
  @Test
  public void shouldGetLastReceivedTextIdIncreasesAfterInsert() {

    final var idBefore = this.service.getLastReceivedTextId()
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    insertRandomPayload();

    final var idAfter = this.service.getLastReceivedTextId()
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(idAfter, greaterThan(idBefore));
  }

  // -------------------------------------------------------------------------
  // getReceivedTextPage
  // -------------------------------------------------------------------------

  /**
   * Check that an empty store returns a page with total 0 and no texts.
   */
  @Test
  public void shouldGetEmptyPageWhenNoTexts() {

    final var page = this.service.getReceivedTextPage(0, 10)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(page, notNullValue());
    assertThat(page.total, is(0L));
    assertThat(page.receivedTexts, nullValue());
  }

  /**
   * Check that the total reflects all inserted texts.
   */
  @Test
  public void shouldGetPageWithCorrectTotal() {

    final int count = faker.number().numberBetween(2, 6);
    for (int i = 0; i < count; i++) {
      insertRandomPayload();
    }

    final var page = this.service.getReceivedTextPage(0, 100)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(page, notNullValue());
    assertThat(page.total, is((long) count));
    assertThat(page.receivedTexts, notNullValue());
    assertThat(page.receivedTexts, hasSize(count));
  }

  /**
   * Check that limit is respected.
   */
  @Test
  public void shouldGetPageRespectLimit() {

    final int count = 5;
    for (int i = 0; i < count; i++) {
      insertRandomPayload();
    }

    final int limit = 2;
    final var page = this.service.getReceivedTextPage(0, limit)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(page, notNullValue());
    assertThat(page.total, is((long) count));
    assertThat(page.receivedTexts, notNullValue());
    assertThat(page.receivedTexts, hasSize(limit));
  }

  /**
   * Check that offset skips the right number of elements.
   */
  @Test
  public void shouldGetPageRespectOffset() {

    final int count = 5;
    for (int i = 0; i < count; i++) {
      insertRandomPayload();
    }

    final int offset = 3;
    final var page = this.service.getReceivedTextPage(offset, 100)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(page, notNullValue());
    assertThat(page.total, is((long) count));
    assertThat(page.receivedTexts, notNullValue());
    assertThat(page.receivedTexts, hasSize(count - offset));
  }

  /**
   * Check that an offset beyond the total returns no texts.
   */
  @Test
  public void shouldGetPageReturnNoTextsWhenOffsetExceedsTotal() {

    insertRandomPayload();

    final var page = this.service.getReceivedTextPage(100, 10)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(page, notNullValue());
    assertThat(page.total, is(1L));
    assertThat(page.receivedTexts, nullValue());
  }

  /**
   * Check that the page is ordered by id ascending.
   */
  @Test
  public void shouldGetPageOrderedById() {

    final int count = faker.number().numberBetween(2, 6);
    for (int i = 0; i < count; i++) {
      insertRandomPayload();
    }

    final var page = this.service.getReceivedTextPage(0, 100)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();

    assertThat(page.receivedTexts, notNullValue());
    final var items = page.receivedTexts;
    for (int i = 1; i < items.size(); i++) {
      assertThat("Texts should be ordered by id ascending",
          items.get(i).id, greaterThan(items.get(i - 1).id));
    }
  }

  // -------------------------------------------------------------------------
  // helpers
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

    return this.service.insert(nextPayload())
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertCompleted()
        .getItem();
  }

}

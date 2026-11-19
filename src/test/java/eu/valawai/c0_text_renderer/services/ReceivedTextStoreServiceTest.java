/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c0_text_renderer.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.valawai.c0_text_renderer.messages.TextPayload;
import io.quarkus.test.junit.QuarkusTest;
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
    this.service.purge().await().indefinitely();
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

    this.service.purge().await().indefinitely();

    final var page = this.service.getReceivedTextPage(0, 100).await().indefinitely();
    assertEquals(0, page.total);
    assertNull(page.receivedTexts);
  }

  // -------------------------------------------------------------------------
  // insert
  // -------------------------------------------------------------------------

  /**
   * Check that inserting a payload with all fields returns a populated
   * ReceivedText.
   */
  @Test
  public void shouldInsertWithAllFields() {

    final var payload = nextPayload();
    final var result = this.service.insert(payload).await().indefinitely();

    assertNotNull(result);
    assertTrue(result.id > 0);
    assertEquals(payload.mime_type, result.mimeType);
    assertEquals(payload.title, result.title);
    assertEquals(payload.content, result.content);
  }

  /**
   * Check that inserting a payload without title generates a default title.
   */
  @Test
  public void shouldInsertWithDefaultTitleWhenTitleIsNull() {

    final var payload = nextPayload();
    payload.title = null;

    final var result = this.service.insert(payload).await().indefinitely();

    assertNotNull(result);
    assertNotNull(result.title);
    assertTrue(result.title.contains(String.valueOf(result.id)),
        "Generated title should contain the id but was: " + result.title);
  }

  /**
   * Check that inserting a payload with an empty title generates a default title.
   */
  @Test
  public void shouldInsertWithDefaultTitleWhenTitleIsEmpty() {

    final var payload = nextPayload();
    payload.title = "";

    final var result = this.service.insert(payload).await().indefinitely();

    assertNotNull(result);
    assertNotNull(result.title);
    assertTrue(result.title.contains(String.valueOf(result.id)),
        "Generated title should contain the id but was: " + result.title);
  }

  /**
   * Check that each inserted text gets an incremental id.
   */
  @Test
  public void shouldInsertAssignIncrementalIds() {

    final var first = this.service.insert(nextPayload()).await().indefinitely();
    final var second = this.service.insert(nextPayload()).await().indefinitely();

    assertNotNull(first);
    assertNotNull(second);
    assertTrue(second.id > first.id);
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
    final var fetched = this.service.get(inserted.id).await().indefinitely();

    assertNotNull(fetched);
    assertEquals(inserted.id, fetched.id);
    assertEquals(inserted.mimeType, fetched.mimeType);
    assertEquals(inserted.title, fetched.title);
    assertEquals(inserted.content, fetched.content);
  }

  /**
   * Check that getting a non-existing id fails.
   */
  @Test
  public void shouldGetFailForUnknownId() {

    final var uni = this.service.get(Long.MAX_VALUE);
    try {
      uni.await().indefinitely();
      assertTrue(false, "Expected a failure for an unknown id");
    } catch (final Exception expected) {
      // expected
    }
  }

  // -------------------------------------------------------------------------
  // getLastReceivedTextId
  // -------------------------------------------------------------------------

  /**
   * Check that the last id grows with every insert.
   */
  @Test
  public void shouldGetLastReceivedTextIdIncreasesAfterInsert() {

    final var idBefore = this.service.getLastReceivedTextId().await().indefinitely();
    insertRandomPayload();
    final var idAfter = this.service.getLastReceivedTextId().await().indefinitely();

    assertTrue(idAfter > idBefore);
  }

  // -------------------------------------------------------------------------
  // getReceivedTextPage
  // -------------------------------------------------------------------------

  /**
   * Check that an empty store returns a page with total 0 and no texts.
   */
  @Test
  public void shouldGetEmptyPageWhenNoTexts() {

    final var page = this.service.getReceivedTextPage(0, 10).await().indefinitely();

    assertNotNull(page);
    assertEquals(0, page.total);
    assertNull(page.receivedTexts);
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

    final var page = this.service.getReceivedTextPage(0, 100).await().indefinitely();

    assertNotNull(page);
    assertEquals(count, page.total);
    assertNotNull(page.receivedTexts);
    assertEquals(count, page.receivedTexts.size());
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
    final var page = this.service.getReceivedTextPage(0, limit).await().indefinitely();

    assertNotNull(page);
    assertEquals(count, page.total);
    assertNotNull(page.receivedTexts);
    assertEquals(limit, page.receivedTexts.size());
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
    final var page = this.service.getReceivedTextPage(offset, 100).await().indefinitely();

    assertNotNull(page);
    assertEquals(count, page.total);
    assertNotNull(page.receivedTexts);
    assertEquals(count - offset, page.receivedTexts.size());
  }

  /**
   * Check that an offset beyond the total returns no texts.
   */
  @Test
  public void shouldGetPageReturnNoTextsWhenOffsetExceedsTotal() {

    insertRandomPayload();

    final var page = this.service.getReceivedTextPage(100, 10).await().indefinitely();

    assertNotNull(page);
    assertEquals(1, page.total);
    assertNull(page.receivedTexts);
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

    final var page = this.service.getReceivedTextPage(0, 100).await().indefinitely();

    assertNotNull(page.receivedTexts);
    for (int i = 1; i < page.receivedTexts.size(); i++) {
      assertTrue(page.receivedTexts.get(i).id > page.receivedTexts.get(i - 1).id,
          "Texts should be ordered by id ascending");
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
    return this.service.insert(nextPayload()).await().indefinitely();
  }

}

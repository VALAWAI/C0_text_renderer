/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c0_text_renderer.messages;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.valawai.c0_text_renderer.messages.mov.LogService;
import eu.valawai.c0_text_renderer.services.ReceivedTextStoreService;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import net.datafaker.Faker;

/**
 * Unit tests for {@link TextManager}.
 *
 * @see TextManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class TextManagerTest {

	/**
	 * Random values generator.
	 */
	private static final Faker faker = new Faker();

	/**
	 * The manager to test.
	 */
	@Inject
	TextManager manager;

	/**
	 * The service to generate log messages.
	 */
	@Inject
	LogService log;

	/**
	 * The store service to verify side-effects.
	 */
	@Inject
	ReceivedTextStoreService storeService;

	/**
	 * The validator used for validation assertions.
	 */
	@Inject
	Validator validator;

	/**
	 * Purge the stored texts before each test.
	 */
	@BeforeEach
	public void setUp() {

		this.storeService.purge()
				.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitItem()
				.assertCompleted();
	}

	// -------------------------------------------------------------------------
	// receivedText - Success scenarios
	// -------------------------------------------------------------------------

	/**
	 * Check that a valid text/plain message is acknowledged and stored.
	 */
	@Test
	public void shouldAcknowledgeAndStoreValidPlainText() {

		final var title = faker.lorem().sentence();
		final var content = faker.lorem().paragraph();
		final var json = new JsonObject()
				.put("mime_type", "text/plain")
				.put("title", title)
				.put("content", content);

		final var acked = new AtomicBoolean(false);
		final var nacked = new AtomicReference<Throwable>();
		final var msg = createMessage(json, acked, nacked);

		this.manager.receivedText(msg).toCompletableFuture().join();

		assertTrue(acked.get(), "Message should have been acknowledged");
		assertThat(nacked.get(), nullValue());

		final var page = this.storeService.getReceivedTextPage(0, 10)
				.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitItem()
				.assertCompleted()
				.getItem();

		assertThat(page.total, is(1L));
		assertThat(page.receivedTexts, notNullValue());
		final var stored = page.receivedTexts.get(0);
		assertThat(stored.mimeType, is("text/plain"));
		assertThat(stored.title, is(title));
		assertThat(stored.content, is(content));
	}

	/**
	 * Check that a valid text/html message is acknowledged and stored.
	 */
	@Test
	public void shouldAcknowledgeAndStoreValidHtmlText() {

		final var json = new JsonObject()
				.put("mime_type", "text/html")
				.put("title", "HTML Title")
				.put("content", "<h1>Hello</h1>");

		final var acked = new AtomicBoolean(false);
		final var nacked = new AtomicReference<Throwable>();
		final var msg = createMessage(json, acked, nacked);

		this.manager.receivedText(msg).toCompletableFuture().join();

		assertTrue(acked.get(), "Message should have been acknowledged");
		assertThat(nacked.get(), nullValue());

		final var page = this.storeService.getReceivedTextPage(0, 10)
				.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitItem()
				.getItem();

		assertThat(page.total, is(1L));
		assertThat(page.receivedTexts.get(0).mimeType, is("text/html"));
	}

	/**
	 * Check that a valid text/markdown message is acknowledged and stored.
	 */
	@Test
	public void shouldAcknowledgeAndStoreValidMarkdownText() {

		final var json = new JsonObject()
				.put("mime_type", "text/markdown")
				.put("title", "Markdown Title")
				.put("content", "# Hello Markdown");

		final var acked = new AtomicBoolean(false);
		final var nacked = new AtomicReference<Throwable>();
		final var msg = createMessage(json, acked, nacked);

		this.manager.receivedText(msg).toCompletableFuture().join();

		assertTrue(acked.get(), "Message should have been acknowledged");
		assertThat(nacked.get(), nullValue());

		final var page = this.storeService.getReceivedTextPage(0, 10)
				.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitItem()
				.getItem();

		assertThat(page.total, is(1L));
		assertThat(page.receivedTexts.get(0).mimeType, is("text/markdown"));
	}

	/**
	 * Check that a message with no title receives a default title and is stored.
	 */
	@Test
	public void shouldAcknowledgeAndStoreTextWithoutTitle() {

		final var json = new JsonObject()
				.put("mime_type", "text/plain")
				.put("content", "Content without explicit title");

		final var acked = new AtomicBoolean(false);
		final var nacked = new AtomicReference<Throwable>();
		final var msg = createMessage(json, acked, nacked);

		this.manager.receivedText(msg).toCompletableFuture().join();

		assertTrue(acked.get(), "Message should have been acknowledged");
		assertThat(nacked.get(), nullValue());

		final var page = this.storeService.getReceivedTextPage(0, 10)
				.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitItem()
				.getItem();

		assertThat(page.total, is(1L));
		assertThat(page.receivedTexts.get(0).title, containsString("Text"));
	}

	// -------------------------------------------------------------------------
	// receivedText - Failure / Nack scenarios
	// -------------------------------------------------------------------------

	/**
	 * Check that a message with an invalid MIME type is rejected (nacked).
	 */
	@Test
	public void shouldNackWhenMimeTypeIsInvalid() {

		final var json = new JsonObject()
				.put("mime_type", "application/json")
				.put("content", "Some content");

		final var acked = new AtomicBoolean(false);
		final var nacked = new AtomicReference<Throwable>();
		final var msg = createMessage(json, acked, nacked);

		this.manager.receivedText(msg).toCompletableFuture().join();

		assertFalse(acked.get(), "Message should NOT have been acknowledged");
		assertThat(nacked.get(), notNullValue());
		assertThat(nacked.get(), instanceOf(ConstraintViolationException.class));

		final var page = this.storeService.getReceivedTextPage(0, 10)
				.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitItem()
				.getItem();

		assertThat(page.total, is(0L));
	}

	/**
	 * Check that a message with empty content is rejected (nacked).
	 */
	@Test
	public void shouldNackWhenContentIsEmpty() {

		final var json = new JsonObject()
				.put("mime_type", "text/plain")
				.put("content", "");

		final var acked = new AtomicBoolean(false);
		final var nacked = new AtomicReference<Throwable>();
		final var msg = createMessage(json, acked, nacked);

		this.manager.receivedText(msg).toCompletableFuture().join();

		assertFalse(acked.get(), "Message should NOT have been acknowledged");
		assertThat(nacked.get(), notNullValue());
		assertThat(nacked.get(), instanceOf(ConstraintViolationException.class));

		final var page = this.storeService.getReceivedTextPage(0, 10)
				.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitItem()
				.getItem();

		assertThat(page.total, is(0L));
	}

	/**
	 * Check that a message missing content is rejected (nacked).
	 */
	@Test
	public void shouldNackWhenContentIsMissing() {

		final var json = new JsonObject()
				.put("mime_type", "text/plain");

		final var acked = new AtomicBoolean(false);
		final var nacked = new AtomicReference<Throwable>();
		final var msg = createMessage(json, acked, nacked);

		this.manager.receivedText(msg).toCompletableFuture().join();

		assertFalse(acked.get());
		assertThat(nacked.get(), notNullValue());
		assertThat(nacked.get(), instanceOf(ConstraintViolationException.class));
	}

	/**
	 * Check that a message missing mime_type is rejected (nacked).
	 */
	@Test
	public void shouldNackWhenMimeTypeIsMissing() {

		final var json = new JsonObject()
				.put("content", "Valid content");

		final var acked = new AtomicBoolean(false);
		final var nacked = new AtomicReference<Throwable>();
		final var msg = createMessage(json, acked, nacked);

		this.manager.receivedText(msg).toCompletableFuture().join();

		assertFalse(acked.get());
		assertThat(nacked.get(), notNullValue());
		assertThat(nacked.get(), instanceOf(ConstraintViolationException.class));
	}

	/**
	 * Check that an empty JSON message is rejected (nacked).
	 */
	@Test
	public void shouldNackWhenPayloadIsEmptyJson() {

		final var json = new JsonObject();

		final var acked = new AtomicBoolean(false);
		final var nacked = new AtomicReference<Throwable>();
		final var msg = createMessage(json, acked, nacked);

		this.manager.receivedText(msg).toCompletableFuture().join();

		assertFalse(acked.get());
		assertThat(nacked.get(), notNullValue());
	}

	/**
	 * Check that a message with null payload is rejected (nacked).
	 */
	@Test
	public void shouldNackWhenMessagePayloadIsNull() {

		final var acked = new AtomicBoolean(false);
		final var nacked = new AtomicReference<Throwable>();
		final Message<JsonObject> msg = Message.of(
				null,
				() -> {
					acked.set(true);
					return CompletableFuture.completedFuture(null);
				},
				error -> {
					nacked.set(error);
					return CompletableFuture.completedFuture(null);
				});

		this.manager.receivedText(msg).toCompletableFuture().join();

		assertFalse(acked.get());
		assertThat(nacked.get(), notNullValue());
	}

	/**
	 * Check that if the storage service fails, the message is nacked.
	 */
	@Test
	public void shouldNackWhenStorageFails() {

		final var expectedError = new RuntimeException("Simulated store error");
		final var failingStore = new ReceivedTextStoreService() {
			@Override
			public Uni<eu.valawai.c0_text_renderer.services.ReceivedText> insert(TextPayload payload) {
				return Uni.createFrom().failure(expectedError);
			}
		};

		final var customManager = new TextManager();
		customManager.log = this.log;
		customManager.validator = this.validator;
		customManager.storeService = failingStore;

		final var json = new JsonObject()
				.put("mime_type", "text/plain")
				.put("content", "Some content");

		final var acked = new AtomicBoolean(false);
		final var nacked = new AtomicReference<Throwable>();
		final var msg = createMessage(json, acked, nacked);

		customManager.receivedText(msg).toCompletableFuture().join();

		assertFalse(acked.get());
		assertThat(nacked.get(), notNullValue());
		assertThat(nacked.get().getMessage(), is(expectedError.getMessage()));
	}

	// -------------------------------------------------------------------------
	// toValiedTextPayload tests
	// -------------------------------------------------------------------------

	/**
	 * Check that toValiedTextPayload returns the parsed payload for valid input.
	 */
	@Test
	public void shouldExtractValidTextPayload() {

		final var json = new JsonObject()
				.put("mime_type", "text/plain")
				.put("title", "Test Title")
				.put("content", "Content");

		final var msg = Message.of(json);

		final var payload = this.manager.toValiedTextPayload(msg)
				.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitItem()
				.assertCompleted()
				.getItem();

		assertThat(payload, notNullValue());
		assertThat(payload.mime_type, is("text/plain"));
		assertThat(payload.title, is("Test Title"));
		assertThat(payload.content, is("Content"));
	}

	/**
	 * Check that toValiedTextPayload fails with ConstraintViolationException for invalid input.
	 */
	@Test
	public void shouldFailToExtractInvalidPayload() {

		final var json = new JsonObject()
				.put("mime_type", "invalid/mime")
				.put("content", "");

		final var msg = Message.of(json);

		this.manager.toValiedTextPayload(msg)
				.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitFailure()
				.assertFailedWith(ConstraintViolationException.class);
	}

	/**
	 * Check that toValiedTextPayload fails when payload mapping throws an exception.
	 */
	@Test
	public void shouldFailToExtractMalformedPayload() {

		final Message<JsonObject> msg = Message.of(null);

		this.manager.toValiedTextPayload(msg)
				.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitFailure();
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	/**
	 * Create a reactive message wrapping the specified JSON payload with ack/nack hooks.
	 */
	private Message<JsonObject> createMessage(
			final JsonObject payload,
			final AtomicBoolean acked,
			final AtomicReference<Throwable> nacked) {

		return Message.of(
				payload,
				() -> {
					acked.set(true);
					return CompletableFuture.completedFuture(null);
				},
				error -> {
					nacked.set(error);
					return CompletableFuture.completedFuture(null);
				});
	}

}

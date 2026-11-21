/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by the GNU General Public License v3.0
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c0_text_renderer.messages;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import eu.valawai.c0_text_renderer.messages.mov.LogService;
import eu.valawai.c0_text_renderer.services.ReceivedTextStoreService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

/**
 * The component that manages a text for rendering.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ApplicationScoped
public class TextManager {

	/**
	 * The service to generate log messages.
	 */
	@Inject
	LogService log;

	/**
	 * The component to validate a {@code Payload}.
	 */
	@Inject
	Validator validator;

	/**
	 * The service to store the received text.
	 */
	@Inject
	ReceivedTextStoreService storeService;

	/**
	 * Called when a new text has been received.
	 *
	 * @param msg with the text to store.
	 *
	 * @return the result if the message process.
	 */
	@Incoming("received_text")
	public CompletionStage<Void> receivedText(Message<JsonObject> msg) {

		return this.toValiedTextPayload(msg)
				.chain(payload -> this.storeService.insert(payload).map(_ -> (Throwable) null)).onFailure()
				.recoverWithItem(error -> error).subscribeAsCompletionStage().thenCompose(error -> {

					if (error != null) {

						this.log.errorWithPayload(msg.getPayload(),
								"Unexpected text message, because {0}", error.getMessage());
						return msg.nack(error);

					} else {

						this.log.debugWithPayload(msg.getPayload(), "Text received");
						return msg.ack();
					}

				});

	}

	/**
	 * Obtain a valid text payload from a message.
	 *
	 * @param msg top get the text payload.
	 *
	 * @return the text payload.
	 */
	protected Uni<TextPayload> toValiedTextPayload(Message<JsonObject> msg) {

		try {

			final var payload = msg.getPayload().mapTo(TextPayload.class);
			final var violations = this.validator.validate(payload);
			if (!violations.isEmpty()) {

				return Uni.createFrom().failure(new ConstraintViolationException(violations));

			} else {

				return Uni.createFrom().item(payload);
			}

		} catch (final Throwable error) {

			return Uni.createFrom().failure(error);
		}
	}

}

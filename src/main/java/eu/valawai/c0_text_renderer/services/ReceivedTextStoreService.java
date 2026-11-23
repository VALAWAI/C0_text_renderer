/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c0_text_renderer.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import eu.valawai.c0_text_renderer.api.v1.texts.ReceivedTextPage;
import eu.valawai.c0_text_renderer.messages.TextPayload;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * The sevice used to mantain the content of the text fields in memory.
 * 
 * @author VALAWAI
 */
@ApplicationScoped
public class ReceivedTextStoreService {

  /**
   * The identifier of teh last stored received text.
   */
  private long lastReceivedTextId = 0;

  /**
   * The list of text fields that are managed by this service.
   */
  private final List<ReceivedText> texts = Collections.synchronizedList(new ArrayList<>());

  /**
   * The pattern used to create a title for a received text when no title is
   * provided.
   * The pattern is a format string that can be used with {@link String#format()}
   * and it is expected to have one argument, which is the identifier of the
   * received text.
   */
  @ConfigProperty(name = "c0.text_renderer.title_pattern", defaultValue = "Text %d")
  private String titlePattern;

  /**
   * Remove all the stored received texts.
   */
  public Uni<Void> purge() {

    return Uni.createFrom().item(() -> {
      synchronized (texts) {
        texts.clear();
      }
      return null;
    });
  }

  /**
   * Insert a new received text.
   * 
   * @param payload The received text payload.
   * 
   * @return the received text with the assigned identifier.
   */
  public Uni<ReceivedText> insert(final TextPayload payload) {

    ReceivedText receivedText = new ReceivedText();
    receivedText.id = ++lastReceivedTextId;
    receivedText.mimeType = payload.mime_type;
    receivedText.title = payload.title;
    if (receivedText.title == null || receivedText.title.isEmpty()) {
      receivedText.title = String.format(titlePattern, receivedText.id);
    }
    receivedText.content = payload.content;
    receivedText.receivedAt = System.currentTimeMillis();
    if (texts.add(receivedText)) {
      Collections.sort(texts, (a, b) -> Long.compare(a.id, b.id));
      return Uni.createFrom().item(receivedText);
    } else {
      return Uni.createFrom().failure(new Exception("Error adding received text"));
    }
  }

  /**
   * Get a received text by its identifier.
   * 
   * @param id The identifier of the received text.
   * @return the received text with the assigned identifier.
   */
  public Uni<ReceivedText> get(final long id) {

    synchronized (this.texts) {
      for (ReceivedText receivedText : texts) {
        if (receivedText.id == id) {
          return Uni.createFrom().item(receivedText);
        }
      }
    }
    return Uni.createFrom().failure(() -> new IllegalArgumentException("Received text not found: " + id));
  }

  /**
   * Delete a received text by its identifier.
   * 
   * @param id The identifier of the received text.
   * @return {@code true} if deleted, {@code false} if not found.
   */
  public Uni<Boolean> delete(final long id) {

    return Uni.createFrom().item(() -> {
      synchronized (this.texts) {
        return this.texts.removeIf(text -> text.id == id);
      }
    });
  }

  /**
   * Return the last received id.
   */
  public Uni<Long> getLastReceivedTextId() {

    return Uni.createFrom().item(lastReceivedTextId);
  }

  /**
   * Return a page with received texts ordered by id.
   */
  public Uni<ReceivedTextPage> getReceivedTextPage(int offset, int limit) {

    return Uni.createFrom().item(() -> {

      synchronized (texts) {
        var page = new ReceivedTextPage();
        page.total = this.texts.size();
        if (offset < page.total) {
          int endIndex = (int) Math.min(offset + limit, page.total);
          page.receivedTexts = this.texts.subList(offset, endIndex);
        }

        return page;
      }
    });
  }

}

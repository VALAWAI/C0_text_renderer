/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c0_text_renderer.services;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.c0_text_renderer.models.ReflectionModel;

/**
 * Contain the information of a text that is received to render.
 * 
 * @author VALAWAI
 */
@Schema(description = "The information of a text that is received to render.")
public class ReceivedText extends ReflectionModel {

  /**
   * Identifier of the received text.
   */
  @Schema(description = "Identifier of the received text.", example = "1", readOnly = true)
  public long id;

  /**
   * The mime type of the received text.
   */
  @Schema(description = "The MIME type of the received text.", example = "text/plain")
  public String mimeType;

  /**
   * The title of the received text.
   */
  @Schema(description = "The title of the received text.", example = "Text 1")
  public String title;

  /**
   * The content of the received text.
   */
  @Schema(description = "The content of the received text.", example = "Example message content")
  public String content;

  /**
   * The epoch time in seconds when the text was received.
   */
  @Schema(description = "The epoch time in seconds when the text was received.", example = "1700000000")
  public long receivedAt;

}

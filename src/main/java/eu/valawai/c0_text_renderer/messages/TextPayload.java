/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c0_text_renderer.messages;

import com.fasterxml.jackson.annotation.JsonRootName;

import jakarta.validation.constraints.*;

/**
 * Provides the content to be rendered as text.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@JsonRootName("text_payload")
public class TextPayload extends Payload {

	/**
	 * The mime type of the text.
	 */
	@NotBlank
	@Pattern(regexp = "text/(plain|html|markdown)", message = "must be text/plain, text/html, or text/markdown")
	public String mime_type;

	/**
	 * The content of the text.
	 */
	@NotEmpty
	public String content;

	/**
	 * The title of the text.
	 */
	public String title;

}
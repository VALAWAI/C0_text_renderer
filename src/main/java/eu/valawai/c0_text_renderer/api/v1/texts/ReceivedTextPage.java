/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by the GNU General Public License v3.0
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/
package eu.valawai.c0_text_renderer.api.v1.texts;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.c0_text_renderer.api.Model;
import eu.valawai.c0_text_renderer.services.ReceivedText;

/**
 * Return a page of received text.
 * 
 * @author VALAWAI
 */
@Schema(description = "A page of received messages.")
public class ReceivedTextPage extends Model {

  /**
   * The total of received messages that match the requested page.
   */
  @Schema(description = "The total number of received messages that match the requested page.", example = "42")
  public long total;

  /**
   * The received messages.
   */
  @Schema(description = "The received messages in the current page.")
  public List<ReceivedText> receivedTexts;

}

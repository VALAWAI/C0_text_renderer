/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c0_text_renderer.services;

import eu.valawai.c0_text_renderer.models.ReflectionModel;

/**
 * Contain sthe information of a text that is received to render.
 * 
 * @author VALAWAI
 */
public class ReceivedText extends ReflectionModel {

  /**
   * Identifier of the received text.
   */
  public long id;

  /**
   * The mime type of the received text.
   */
  public String mimeType;

  /**
   * The title of the received text.
   */
  public String title;

  /**
   * The content of the received text.
   */
  public String content;

  /**
   * The epoch time in seconds when the text was received.
   */
  public long receivedAt;

}

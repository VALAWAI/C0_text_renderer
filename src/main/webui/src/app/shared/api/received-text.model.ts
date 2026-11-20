/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

/**
 * Contain the information of a text that is received to render.
 *
 * @author VALAWAI
 */
export class ReceivedText {

	/**
	 * The identifier of the message.
	 */
	public id: number = 0;

	/**
	 * The mime type of the message.
	 */
	public mimeType: 'text/plain' | 'text/markdown' | 'text/html' | '' = 'text/plain';

	/**
	 * The title of the text.
	 */
	public title: string | null = null;

	/**
	 * The content of the text.
	 */
	public content: string = '';

	/**
	 * The epoch time in seconds when the text was received.
	 */
	public receivedAt: number = 0;

}

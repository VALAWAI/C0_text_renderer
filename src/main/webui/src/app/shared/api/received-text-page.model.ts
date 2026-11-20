/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ReceivedText } from "./received-text.model";

/**
 * A page with some received text.
 *
 * @author VALAWAI
 */
export class ReceivedTextPage {

	/**
	 * The total of received messages that match the requested page.
	 */
	public total: number = 0;

	/**
	 * The received messages.
	 */
	public receivedTexts: ReceivedText[] = [];


}

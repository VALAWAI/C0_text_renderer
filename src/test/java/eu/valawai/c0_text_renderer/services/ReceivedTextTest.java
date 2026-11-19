/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c0_text_renderer.services;

import eu.valawai.c0_text_renderer.models.ReflectionModelTestCase;

/**
 * Unit tests for {@link ReceivedText}.
 * 
 * @see ReceivedText
 */
public class ReceivedTextTest extends ReflectionModelTestCase<ReceivedText> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ReceivedText createEmptyModel() {

        return new ReceivedText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillIn(ReceivedText model) {

        model.id = faker.number().randomNumber();
        model.mimeType = "text/plain";
        model.title = faker.lorem().word();
        model.content = faker.lorem().word();
        model.receivedAt = faker.number().randomNumber();
    }

}

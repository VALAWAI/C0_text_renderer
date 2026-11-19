/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by the GNU General Public License v3.0
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/
package eu.valawai.c0_text_renderer.api.v1.texts;

import java.util.ArrayList;

import eu.valawai.c0_text_renderer.api.ModelTestCase;
import eu.valawai.c0_text_renderer.services.ReceivedTextTest;

/**
 * Tests for {@link ReceivedTextPage}
 * 
 * @see ReceivedTextPage
 */
public class ReceivedTextPageTest extends ModelTestCase<ReceivedTextPage> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ReceivedTextPage createEmptyModel() {

        return new ReceivedTextPage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillIn(ReceivedTextPage model) {

        model.total = faker.number().randomNumber();
        var max = faker.number().numberBetween(0, 10);
        if (max > 0) {

            model.receivedTexts = new ArrayList<>();
            var builder = new ReceivedTextTest();
            for (int i = 0; i < max; i++) {

                var item = builder.nextModel();
                model.receivedTexts.add(item);
            }
        }
    }

}

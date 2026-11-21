/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by the GNU General Public License v3.0
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/
package eu.valawai.c0_text_renderer.messages;

/**
 * Test the {@link TextPayload} class.
 * 
 * @see TextPayload
 * @author VALAWAI
 */
public class TextPayloadTest extends PayloadTestCase<TextPayload> {

    /**
     * {@inheritDoc}
     */
    @Override
    public TextPayload createEmptyModel() {
        
        return new TextPayload();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillIn(TextPayload model) {

        model.mime_type = "text/plain";
        model.title = this.faker.lorem().sentence();
        model.content = this.faker.lorem().paragraph();
    }

}

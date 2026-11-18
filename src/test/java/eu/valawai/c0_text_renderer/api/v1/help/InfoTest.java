/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c0_text_renderer.api.v1.help;

import eu.valawai.c0_text_renderer.models.ReflectionModelTestCase;

/**
 * Test the {@link Info}.
 * 
 * @see Info
 */
public class InfoTest extends ReflectionModelTestCase<Info> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Info createEmptyModel() {
        return new Info();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillIn(Info model) {
        model.version = faker.app().version();
        model.profile = faker.options().option("production", "development", "testing");
    }

}

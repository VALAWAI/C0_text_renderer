/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by the GNU General Public License v3.0
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c0_text_renderer.api;

import eu.valawai.c0_text_renderer.models.ReflectionModelTestCase;

/**
 * Generic test over the calsses tha extends the
 * {@link ModelTestCase}
 * 
 * @param <T> type of model to test
 */
public abstract class ModelTestCase<T extends Model> extends ReflectionModelTestCase<T> {

}

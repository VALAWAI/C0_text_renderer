/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c0_text_renderer.api.v1.help;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link HelpResource}.
 *
 * @see HelpResource
 *
 * @author VALAWAI
 */
@QuarkusTest
public class HelpResourceTest {

	/**
	 * Should not found an undefined experiment.
	 */
	@Test
	public void shouldGetInformationOfTheWebServices() {

		final var config = ConfigProvider.getConfig();
		final var model = new Info();
		model.version = config.getOptionalValue("quarkus.application.version", String.class).orElse("1.0");
		model.profile = config.getOptionalValue("quarkus.profile", String.class).orElse("test");
		var info = given().when().get("/v1/help/info").then().statusCode(Status.OK.getStatusCode()).extract()
				.body().as(Info.class);
		assertThat(info, is(model));
	}
}

/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by the GNU General Public License v3.0
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/
package eu.valawai.c0_text_renderer.api.v1.texts;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import eu.valawai.c0_text_renderer.services.ReceivedText;
import eu.valawai.c0_text_renderer.services.ReceivedTextStoreService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * API resources to manage the received resources.
 * 
 * @author VALAWAI
 */
@Path("/v1/texts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Texts", description = "The services to manage the received texts")
public class TextsResources {

    /**
     * The service to manage the stored received messages.
     */
    @Inject
    ReceivedTextStoreService storageService;

    /**
     * Get a page of received messages.
     * 
     * @param offset The offset of the page.
     * @param limit  The limit of the page.
     * @return A page of received messages.
     */
    @GET
    @Operation(summary = "Get a page of received messages", description = "Get a page of received messages.")
    @APIResponse(responseCode = "200", description = "The page with the received messages.", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ReceivedTextPage.class)))
    @APIResponse(responseCode = "400", description = "When a parameter is not valid.")
    public Uni<Response> getPage(
            @QueryParam("offset") @DefaultValue("0") @Parameter(in = ParameterIn.QUERY, description = "The index of the first received message to retrieve", example = "0", schema = @Schema(defaultValue = "0", minimum = "0")) @Min(0) int offset,
            @QueryParam("limit") @DefaultValue("10") @Parameter(in = ParameterIn.QUERY, description = "The maximum number of received messages to retrieve", example = "10", schema = @Schema(defaultValue = "10", minimum = "1")) @Min(1) int limit) {

        return storageService.getReceivedTextPage(offset, limit).map(page -> Response.ok(page).build());
    }

    /**
     * Get a received message.
     * 
     * @param id The id of the message.
     * @return The received message.
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Get a received message", description = "Get a received message by its identifier.")
    @APIResponse(responseCode = "200", description = "The received message associated to the identifier.", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ReceivedText.class)))
    @APIResponse(responseCode = "404", description = "If no received message is found with the specified identifier.")
    public Uni<Response> get(
            @PathParam("id") @Parameter(in = ParameterIn.PATH, description = "The identifier of the message to retrieve", example = "1", required = true) long id) {

        return storageService.get(id).map(text -> Response.ok(text).build())
                .onFailure().recoverWithItem(_ -> Response.status(Status.NOT_FOUND).build());
    }

    /**
     * Delete a received message.
     * 
     * @param id The id of the message.
     * @return No content if deleted, 404 if not found.
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a received message", description = "Delete a received message by its identifier.")
    @APIResponse(responseCode = "204", description = "If the message has been deleted.")
    @APIResponse(responseCode = "404", description = "If no received message is found with the specified identifier.")
    public Uni<Response> delete(
            @PathParam("id") @Parameter(in = ParameterIn.PATH, description = "The identifier of the message to delete", example = "1", required = true) long id) {

        return storageService.delete(id).map(deleted -> {
            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Status.NOT_FOUND).build();
            }
        });
    }

}

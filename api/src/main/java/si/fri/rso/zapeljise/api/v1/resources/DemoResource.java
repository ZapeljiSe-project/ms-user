package si.fri.rso.zapeljise.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.fri.rso.zapeljise.msuser.lib.TownDemo;
import si.fri.rso.zapeljise.msuser.services.beans.UserDataBean;
import si.fri.rso.zapeljise.msuser.services.config.RestProperties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log
@ApplicationScoped
@Path("/demo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Demo")
public class DemoResource {
    private Logger log = Logger.getLogger(DemoResource.class.getName());

    @Inject
    private RestProperties restProperties;

    @Inject
    private UserDataBean userDataBean; // Contains normal code as well as code for the fault tolerance demos.

    @POST
    @Path("break")
    public Response makeUnhealthy() {

        restProperties.setBroken(true);

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("repair")
    public Response makeHealty() {

        restProperties.setBroken(false);

        return Response.status(Response.Status.OK).build();
    }

    @Operation(description = "Get all Slovenian towns - working demo.", summary = "Get all towns - working demo.")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of all available towns.",
                    content = @Content(schema = @Schema(implementation = TownDemo.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list.")}
            )})
    @GET
    @Path("town/works")
    public Response demoTown() {
        log.log(Level.INFO, "Entering GET towns demo method...");

        try {
            List<TownDemo> towns = userDataBean.getTownsDemo();

            log.log(Level.INFO, "Exit GET towns method.");
            return Response.status(Response.Status.OK).entity(towns).build();
        } catch (Exception e) {
            log.log(Level.WARNING, "Error fetching towns.", e);
            log.log(Level.INFO, "Exit GET towns method.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Get all Slovenian towns - without fallback demo.", summary = "Get all towns - without fallback demo.")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of all available towns.",
                    content = @Content(schema = @Schema(implementation = TownDemo.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list.")}
            )})
    @GET
    @Path("fallback/off")
    public Response demoTownWithoutFallback() {
        log.log(Level.INFO, "Entering GET towns without fallback demo method...");

        try {
            List<TownDemo> towns = userDataBean.getTownsDemoWithoutFallback();

            log.log(Level.INFO, "Exit GET towns method.");
            return Response.status(Response.Status.OK).entity(towns).build();
        } catch (Exception e) {
            log.log(Level.WARNING, "Error fetching towns.", e);
            log.log(Level.INFO, "Exit GET towns method.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Get all Slovenian towns - with fallback demo.", summary = "Get all towns - with fallback demo.")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of all available towns.",
                    content = @Content(schema = @Schema(implementation = TownDemo.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list.")}
            )})
    @GET
    @Path("fallback/on")
    public Response demoTownWithFallback() {
        log.log(Level.INFO, "Entering GET towns with fallback demo method...");

        try {
            List<TownDemo> towns = userDataBean.getTownsDemoWithFallback();

            log.log(Level.INFO, "Exit GET towns method.");
            return Response.status(Response.Status.OK).entity(towns).build();
        } catch (Exception e) {
            log.log(Level.WARNING, "Error fetching towns.", e);
            log.log(Level.INFO, "Exit GET towns method.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
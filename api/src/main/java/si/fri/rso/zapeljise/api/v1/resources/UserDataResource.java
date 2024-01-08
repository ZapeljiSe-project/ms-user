package si.fri.rso.zapeljise.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.fri.rso.zapeljise.msuser.lib.UserData;
import si.fri.rso.zapeljise.msuser.services.beans.UserDataBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log
@ApplicationScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users")
public class UserDataResource {
    private Logger log = Logger.getLogger(UserDataResource.class.getName());

    @Inject
    private UserDataBean userDataBean;

    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get data of all users.", summary = "Get all users.")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of data of all users.",
                    content = @Content(schema = @Schema(implementation = UserData.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list.")}
            )})
    @GET
    public Response getUserData() {
        log.log(Level.INFO, "Entering 'GET users' endpoint...");
        List<UserData> userData = userDataBean.getUserDataFilter(uriInfo);

        log.log(Level.INFO, "Exit 'GET users' endpoint.");
        return Response.status(Response.Status.OK).entity(userData).build();
    }

    @Operation(description = "Get data for a single user.", summary = "Get data for a specific user.")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "User data.",
                    content = @Content(
                            schema = @Schema(implementation = UserData.class))
            )})
    @GET
    @Path("/{userDataId}")
    public Response getUserData(@Parameter(description = "User data ID.", required = true)
                                     @PathParam("userDataId") Integer userDataId) {
        log.log(Level.INFO, "Entering 'GET users/id' endpoint...");
        UserData userData = userDataBean.getUserData(userDataId);

        if (userData == null) {
            log.log(Level.WARNING, "Exit 'GET users/id' endpoint - User not found.");
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        log.log(Level.INFO, "Exit 'GET users/id' endpoint.");
        return Response.status(Response.Status.OK).entity(userData).build();
    }

    @Operation(description = "Add new user data.", summary = "Register user.")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "User successfully registered."
            ),
            @APIResponse(responseCode = "405", description = "Validation error.")
    })
    @POST
    @Path("/register")
    public Response registerUser(@RequestBody(
            description = "DTO object with user data.",
            required = true, content = @Content(
            schema = @Schema(implementation = UserData.class))) UserData userData) {
        log.log(Level.INFO, "Entering 'POST users/register' endpoint...");

        if ((userData.getUsername() == null || userData.getPassword() == null)) {
            log.log(Level.WARNING, "Exit 'POST users/register' endpoint - BAD REQUEST.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        userData = userDataBean.createUserData(userData);

        log.log(Level.INFO, "Exit 'POST users/register' endpoint.");
        return Response.status(Response.Status.OK).entity(userData).build();
    }

    @Operation(description = "Check user data.", summary = "Login user.")
    @APIResponses({
            @APIResponse(responseCode = "202", description = "User login successes."),
            @APIResponse(responseCode = "405", description = "Validation error."),
            @APIResponse(responseCode = "400", description = "Bad request."),
            @APIResponse(responseCode = "406", description = "Wrong username or password.")
    })
    @POST
    @Path("/login")
    public Response loginUser(@RequestBody(
            description = "DTO object with user data.",
            required = true, content = @Content(
            schema = @Schema(implementation = UserData.class))) UserData userData) {
        log.log(Level.INFO, "Entering 'POST users/login' endpoint...");

        if ((userData.getUsername() == null || userData.getPassword() == null)) {
            log.log(Level.WARNING, "Exit 'POST users/login' endpoint - BAD REQUEST.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        UserData existingUserData = userDataBean.checkUserData(userData);

        if (existingUserData == null) {
            log.log(Level.INFO, "Exit 'POST users/register' endpoint - User with specified username not found.");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        if (!Objects.equals(existingUserData.getPassword(), userData.getPassword())) {
            log.log(Level.INFO, "Exit 'POST users/register' endpoint - Wrong password.");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        log.log(Level.INFO, "Exit 'POST users/register' endpoint.");
        return Response.status(Response.Status.ACCEPTED).build();
    }

    @Operation(description = "Update data for a user (only name and phone).", summary = "Update user.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "User data successfully updated."
            )
    })
    @PUT
    @Path("{userDataId}")
    public Response putUserData(@Parameter(description = "User ID.", required = true)
                                     @PathParam("userDataId") Integer userDataId,
                                     @RequestBody(
                                             description = "DTO object with user data.",
                                             required = true, content = @Content(
                                             schema = @Schema(implementation = UserData.class)))
                                     UserData userData){
        log.log(Level.INFO, "Entering 'PUT user/id' endpoint...");

        userData = userDataBean.putUserData(userDataId, userData);

        log.log(Level.INFO, "Exit 'PUT user/id' endpoint.");

        if (userData == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).build();
    }

    @Operation(description = "Delete data for a user.", summary = "Delete user.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "User successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @DELETE
    @Path("{userDataId}")
    public Response deleteUserData(@Parameter(description = "User ID.", required = true)
                                        @PathParam("userDataId") Integer userDataId){
        log.log(Level.INFO, "Entering 'DELETE user/id' endpoint...");

        boolean deleted = userDataBean.deleteUserData(userDataId);

        log.log(Level.INFO, "Exit 'DELETE user/id' endpoint.");

        if (deleted) {
            return Response.status(Response.Status.OK).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
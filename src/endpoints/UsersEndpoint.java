package endpoints; /**
 * Created by mortenlaursen on 09/10/2016.
 */

import com.google.gson.Gson;
import controllers.TokenController;
import controllers.UserController;
import database.DBConnector;
import model.User;
import model.UserLogin;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

// implements IEndpoints The Java class will be hosted at the URI path "/users"
@Path("/user")
public class UsersEndpoint  {
    UserController controller = new UserController();
    TokenController tokenController = new TokenController();

    public UsersEndpoint() {
    }

    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("application/json")
    public Response get(@HeaderParam("authorization") String authToken) throws SQLException {

        User user = tokenController.getUserFromTokens(authToken);

        if (user != null){
            if (controller.getUsers() != null) {
                return Response
                        .status(200)
                        .entity(new Gson().toJson(controller.getUsers()))
                        .build();
            } else {
                return Response
                        //error response
                        .status(400)
                        .entity("{\"message\":\"failed\"}")
                        .build();
            }
        }else return Response.status(400).entity("{\"message\":\"failed\"}").build();


    }

    @Path("/{id}")
    @Produces("application/json")
    @GET
    public Response get(@HeaderParam("authorization") String authToken, @PathParam("id") int userId) throws SQLException {

        User user = tokenController.getUserFromTokens(authToken);

        if (user != null){
            if (controller.getUser(userId)!=null) {
                return Response
                        .status(200)
                        .entity(new Gson().toJson(controller.getUser(userId)))
                        .build();
            }
            return Response
                    .status(400)
                    .entity("{\"message\":\"failed\"}")
                    .build();
        }else return Response.status(400).entity("{\"message\":\"failed\"}").build();

    }

    @PUT
    @Path("/{Id}")
    @Produces("application/json")
    public Response edit(@HeaderParam("authorization") String authToken, @PathParam("Id") int id, String data) throws SQLException {

        User user = tokenController.getUserFromTokens(authToken);

        if (user != null){
            if (controller.getUser(id) != null) {
                if (controller.editUser(id, data)) {
                    return Response
                            .status(200)
                            .entity("{\"message\":\"Success! User edited\"}")
                            .build();
                } else {
                    return Response
                            .status(400)
                            .entity("{\"message\":\"failed\"}")
                            .build();
                }
            } else {
                return Response
                        .status(400)
                        .entity("{\"message\":\"failed. No such user\"}")
                        .build();
            }

        }else return Response.status(400).entity("{\"message\":\"failed\"}").build();


    }

    @POST
    @Produces("application/json")
    public Response create(String data) throws Exception {
        if (controller.addUser(data)) {
            //demo to check if it returns this on post.
            return Response
                    .status(200)
                    .entity(new Gson().toJson(controller.getUsers()))
                    .build();
        }
        else return Response.status(400).entity("{\"message\":\"failed\"}").build();
    }

    @Path("/{id}")
    @DELETE
    public Response delete (@HeaderParam("authorization") String authToken, @PathParam("id") int userId) throws SQLException {

        User user = tokenController.getUserFromTokens(authToken);

        if (user != null){
            if(controller.deleteUser(userId)) {
                return Response.status(200).entity("{\"message\":\"Success! User deleted\"}").build();
            }
            else return Response.status(400).entity("{\"message\":\"failed\"}").build();
        }else return Response.status(400).entity("{\"message\":\"failed\"}").build();


    }

    @POST
    @Path("/login")
    @Produces("application/json")
    public Response login(String data) throws SQLException {

        UserLogin userLogin = new Gson().fromJson(data, UserLogin.class);

        String token = tokenController.authenticate(userLogin.getUsername(), userLogin.getPassword());

        if (token != null) {
            //demo to check if it returns this on post.
            return Response
                .status(200)
                .entity(new Gson().toJson(token))
                .build();
        } else return Response
            .status(401)
            .build();
    }

    @POST
    @Path("/logout")
    public Response logout (String data) throws SQLException {

        if(tokenController.deleteToken(data)) {
            return Response
                    .status(200)
                    .entity("Success!")
                    .build();

        } else return Response
                    .status(400)
                    .entity("Failure")
                    .build();
    }

}


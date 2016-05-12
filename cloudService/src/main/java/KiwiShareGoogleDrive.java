/*package kiwishare;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/kiwigdrive")
public class KiwiShareGoogleDrive implements IKiwiShare {

  @GET
  @Path("/log={username},pass={password}")
  @Override
  public Response authentificate(@PathParam("username") String username, @PathParam("password") String password) {

    String output = "Trying to connect with : " + username + ":" + password + "\n";
    return Response.status(200).entity(output).build();
  }

}
*/

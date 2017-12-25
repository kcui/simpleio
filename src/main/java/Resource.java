import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/")
public class Resource {
  @GET
  @Path("/hello")
  public String hello() {
    return "Hello";
  }

  @GET
  @Path("/query")
  public String query(@QueryParam("message") String message) {
    return "You passed " + message;
  }

  @POST
  @Path("/post")
  public String postBody(String message) {
    return "You posted " + message;
  }

  @POST
  @Path("/param")
  public String postParam(@FormParam("message") String message) {
    return "You posted " + message;
  }
}

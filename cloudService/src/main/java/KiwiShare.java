package kiwishare;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.PrintWriter;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.Header;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import java.text.ParseException;
import org.json.*;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.multipart.FormDataParam;

@Path("/kiwishare")
public class KiwiShare implements IKiwiShare {
  private KiwiShareDropbox _dropbox = null; //TODO change MAP<String, IKiwiShare>
  public KiwiShare() {
    _dropbox = KiwiShareDropbox.getInstance();
  }

  @GET
  @Path("authurl")
  public Response getAuthUrl(@QueryParam("type") String type) {
    if(type.equals("dropbox")) {
      return _dropbox.getAuthUrl();
    }
    Map<String, String> jsonContent = new HashMap();
    jsonContent.put("err", "unknown type");
    JSONObject result = new JSONObject(jsonContent);
    return Response.status(200).entity(result.toString()).build();
  }

  @GET
  @Path("/callbackDropbox")
  @Override
  public Response authentificateDropbox(@QueryParam("code") String code, @QueryParam("error") String error) {
    return _dropbox.authentificate(code, error);
  }

  @GET
  @Path("/files")
  @Override
  public Response getFileInfo(@QueryParam("path") String file) {
    //TODO multi Instance
    return _dropbox.getFileInfo(file);
  }

  //@Consumes(MediaType.MULTIPART_FORM_DATA)
  @PUT
  @Path("/put")
  @Override
  public Response sendFile(@FormParam("content") String toUpload, @QueryParam("path") String destination) {
    //TODO multi Instance
    return Response.status(200).entity(toUpload).build();
    //return _dropbox.sendFile(toUpload, destination);
  }


  @GET
  @Path("/info")
  @Override
  public Response getSpaceInfo() {
      //TODO multi Instance
      return _dropbox.getSpaceInfo();
  }

  @GET
  @Path("/mkdir")
  @Override
  public Response mkdir(@QueryParam("path") String folder) {
      //TODO multi Instance
      return _dropbox.mkdir(folder);
  }

  @GET
  @Path("/rm")
  @Override
  public Response removeFile(@QueryParam("path") String file) {
      //TODO multi Instance
      return _dropbox.removeFile(file);
  }

  @GET
  @Path("/mv")
  @Override
  public Response moveFile(@QueryParam("from") String from, @QueryParam("to") String to) {
      //TODO multi Instance
      return _dropbox.moveFile(from, to);
  }

  @GET
  @Path("/share")
  @Override
  public Response shareFile(@QueryParam("path") String file) {
      //TODO multi Instance
      return _dropbox.shareFile(file);
  }
}

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
  private KiwiShareDropbox     _dropbox = null;
  private KiwiShareGoogleDrive _drive = null;  //TODO change MAP<String, IKiwiShare>

  public KiwiShare() {
    _dropbox = KiwiShareDropbox.getInstance();
    _drive = KiwiShareGoogleDrive.getInstance();
  }

  @GET
  @Path("authurl")
  public Response getAuthUrl(@QueryParam("type") String type) {
    if(type.equals("dropbox")) {
      return Response.status(200).entity(_dropbox.getAuthUrl().toString()).build();
    }
    else if(type.equals("drive")) {
      return Response.status(200).entity(_drive.getAuthUrl().toString()).build();
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
    return Response.status(200).entity(_dropbox.authentificate(code, error).toString()).build();
  }

  @GET
  @Path("/callbackDrive")
  @Override
  public Response authentificateDrive(@QueryParam("code") String code, @QueryParam("error") String error) {
    return Response.status(200).entity(_drive.authentificate(code, error).toString()).build();
  }

  @GET
  @Path("/get")
  @Override
  public Response getFileInfo(@QueryParam("path") String file) {
    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.getFileInfo(file));
    result.put("drive", _drive.getFileInfo(file));
    return Response.status(200).entity(result.toString()).build();
  }

  //@Consumes(MediaType.MULTIPART_FORM_DATA)
  @PUT
  @Path("/put")
  @Override
  public Response sendFile(@FormParam("content") String toUpload, @QueryParam("path") String destination) {
    //TODO multi Instance
    return Response.status(200).entity(toUpload).build();
    /*

    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.getFileInfo(file));
    result.put("drive", _drive.getFileInfo(file));
    return Response.status(200).entity(result.toString()).build();
    */
    //return _dropbox.sendFile(toUpload, destination);
  }


  @GET
  @Path("/info")
  @Override
  public Response getSpaceInfo() {
    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.getSpaceInfo());
    result.put("drive", _drive.getSpaceInfo());
    return Response.status(200).entity(result.toString()).build();
  }

  @GET
  @Path("/mkdir")
  @Override
  public Response mkdir(@QueryParam("path") String folder) {
    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.mkdir(folder));
    result.put("drive", _drive.mkdir(folder));
    return Response.status(200).entity(result.toString()).build();
  }

  @GET
  @Path("/rm")
  @Override
  public Response removeFile(@QueryParam("path") String file) {
    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.removeFile(file));
    result.put("drive", _drive.removeFile(file));
    return Response.status(200).entity(result.toString()).build();
  }

  @GET
  @Path("/mv")
  @Override
  public Response moveFile(@QueryParam("from") String from, @QueryParam("to") String to) {
    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.moveFile(from, to));
    result.put("drive", _drive.moveFile(from, to));
    return Response.status(200).entity(result.toString()).build();
  }

  @GET
  @Path("/share")
  @Override
  public Response shareFile(@QueryParam("path") String file) {
    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.shareFile(file));
    result.put("drive", _drive.shareFile(file));
    return Response.status(200).entity(result.toString()).build();
  }
}

package kiwishare;

import com.sun.jersey.multipart.FormDataParam;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/kiwishare")
public class KiwiShare implements IKiwiShare {
  private KiwiShareDropbox     _dropbox = null;
  //TODO change MAP<String, IKiwiShare>
  private KiwiShareGoogleDrive _drive = null;

  /**
   * Init services.
   **/
  public KiwiShare() {
    _dropbox = KiwiShareDropbox.getInstance();
    _drive = KiwiShareGoogleDrive.getInstance();
  }

  @GET
  @Path("authurl")
  public Response getAuthUrl() {
    //TODO foreach services.
    JSONArray responses = new JSONArray();
    JSONObject dropboxResponse = _dropbox.getAuthUrl();
    dropboxResponse.put("service", "dropbox");
    responses.put(dropboxResponse);
    JSONObject driveResponse = _drive.getAuthUrl();
    driveResponse.put("service", "drive");
    responses.put(driveResponse);

    JSONObject result = new JSONObject();
    result.put("urls", responses);
    return Response.status(KiwiUtils.getOkStatus())
    .entity(result.toString()).build();
  }

  @GET
  @Path("/callbackDropbox")
  @Override
  public Response authentificateDropbox(@QueryParam("code") final String code,
                                      @QueryParam("error") final String error) {
    return Response.status(KiwiUtils.getOkStatus())
           .entity(_dropbox.authentificate(code, error).toString()).build();
  }

  @GET
  @Path("/callbackDrive")
  @Override
  public Response authentificateDrive(@QueryParam("code") final String code,
                                      @QueryParam("error") final String error) {
    return Response.status(KiwiUtils.getOkStatus())
          .entity(_drive.authentificate(code, error).toString()).build();
  }

  @GET
  @Path("/get")
  @Override
  public Response getFileInfo(@QueryParam("path") final String file) {
    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.getFileInfo(file));
    result.put("drive", _drive.getFileInfo(file));
    return Response.status(KiwiUtils.getOkStatus()).entity(result.toString())
           .build();
  }

  @POST
  @Path("/put")
  @Override
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response sendFile(@FormDataParam("file") final InputStream file,
                           @QueryParam("path") final String destination) {
    JSONObject result = new JSONObject();
    try {
      //Clone InputStream (if not, we can't send to multiples services)
      byte[] byteArray = IOUtils.toByteArray(file);
      InputStream inputDrop = new ByteArrayInputStream(byteArray);
      InputStream inputDrive = new ByteArrayInputStream(byteArray);
      result.put("dropbox", _dropbox.sendFile(inputDrop, destination));
      result.put("drive", _drive.sendFile(inputDrive, destination));
    } catch (Exception e) {
      result.put("err", e.getMessage());
    }
    return Response.status(KiwiUtils.getOkStatus()).entity(result.toString())
           .build();
  }


  @GET
  @Path("/info")
  @Override
  public Response getSpaceInfo() {
    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.getSpaceInfo());
    result.put("drive", _drive.getSpaceInfo());
    return Response.status(KiwiUtils.getOkStatus()).entity(result.toString())
           .build();
  }

  @GET
  @Path("/mkdir")
  @Override
  public Response mkdir(@QueryParam("path") final String folder) {
    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.mkdir(folder));
    result.put("drive", _drive.mkdir(folder));
    return Response.status(KiwiUtils.getOkStatus()).entity(result.toString())
          .build();
  }

  @GET
  @Path("/rm")
  @Override
  public Response removeFile(@QueryParam("path") final String file) {
    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.removeFile(file));
    result.put("drive", _drive.removeFile(file));
    return Response.status(KiwiUtils.getOkStatus()).entity(result.toString())
           .build();
  }

  @GET
  @Path("/mv")
  @Override
  public Response moveFile(@QueryParam("from") final String from,
                           @QueryParam("to") final String to) {
    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.moveFile(from, to));
    result.put("drive", _drive.moveFile(from, to));
    return Response.status(KiwiUtils.getOkStatus()).entity(result.toString())
          .build();
  }

  @GET
  @Path("/share")
  @Override
  public Response shareFile(@QueryParam("path") final String file) {
    JSONObject result = new JSONObject();
    result.put("dropbox", _dropbox.shareFile(file));
    result.put("drive", _drive.shareFile(file));
    return Response.status(KiwiUtils.getOkStatus()).entity(result.toString())
           .build();
  }


  @GET
  @Path("/tree")
  @Override
  public Response tree(@QueryParam("merge") final String merge) {
    JSONObject result = new JSONObject();
    JSONArray files = _dropbox.tree().getJSONArray("files");
    JSONArray temp = _drive.tree().getJSONArray("files");
    if (merge != null && (merge.equals("true") || merge.equals("1"))) {
      //Merge the 2 JSONArray objects
      List<String> alreadyAdded = new ArrayList<String>();
      for (int i = 0; i < files.length(); ++i) {
        alreadyAdded.add(files.getJSONObject(i).getString("path"));
      }
      for (int i = 0; i < temp.length(); i++) {
        String pathToAdd = temp.getJSONObject(i).getString("path");
        if (!alreadyAdded.contains(pathToAdd)) {
          files.put(temp.getJSONObject(i));
          alreadyAdded.add(pathToAdd);
        }
      }
      result.put("files", files);
    } else {
      result.put("dropbox", files);
      result.put("drive", temp);
    }
    return Response.status(KiwiUtils.getOkStatus()).entity(result.toString())
           .build();
  }
}

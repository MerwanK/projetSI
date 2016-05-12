package kiwishare;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import java.util.Map;
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

@Path("/kiwidropbox")
public class KiwiShareDropbox implements IKiwiShare {

  private String _key = null;
  private String _secret = null;
  private String _callbackUrl = null;

  public KiwiShareDropbox() {
    JSONObject obj = new JSONObject(readFile("config"));
    _key = obj.getString("app_key");
    _secret = obj.getString("app_secret");
    _callbackUrl = obj.getString("callback_url");
  }

  public String readFile(String path) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(path));
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append(System.lineSeparator());
        line = br.readLine();
      }
      String everything = sb.toString();
      return everything;
    } catch(Exception e) {
      return "";
    }
  }

  @GET
  @Path("/authurl")
  @Override
  public Response getAuthUrl() {
    String url = "https://www.dropbox.com/1/oauth2/authorize?response_type=code&" +
    "client_id=" + _key +
    "&redirect_uri=" + _callbackUrl;
    return Response.status(200).entity(url).build();

  }

  @GET
  @Path("/callback")
  @Override
  public Response authentificate(@QueryParam("code") String code, @QueryParam("error") String error) {

    String output = "Trying to connect with : " + code + ":" + error + "\n";

    if (error != null) {
      return Response.status(200).entity(error).build();
    }
    String accessToken = null;
    String body = null;
    try {

      // get the access token by post to Google
      body = post("https://api.dropboxapi.com/1/oauth2/token", ImmutableMap.<String,String>builder()
      .put("code", code)
      .put("client_id", _key)
      .put("client_secret", _secret)
      .put("redirect_uri", "http://localhost:8080/kiwidropbox/callback")
      .put("grant_type", "authorization_code").build());

      JSONObject obj = new JSONObject(body);

      // google tokens expire after an hour, but since we requested offline access we can get a new token without user involvement via the refresh token
      accessToken = obj.getString("access_token");
    } catch (Exception e) {
      accessToken = "Unable to parse json " + body;
    }
    return Response.status(200).entity(accessToken).build();

  }


  // makes a GET request to url and returns body as a string
  public String get(String url) throws ClientProtocolException, IOException {
    return execute(new HttpGet(url));
  }

  // makes a POST request to url with form parameters and returns body as a string
  public String post(String url, Map<String,String> formParameters) throws ClientProtocolException, IOException {
    HttpPost request = new HttpPost(url);

    List <NameValuePair> nvps = new ArrayList <NameValuePair>();

    for (String key : formParameters.keySet()) {
      nvps.add(new BasicNameValuePair(key, formParameters.get(key)));
    }

    request.setEntity(new UrlEncodedFormEntity(nvps));

    return execute(request);
  }

  // makes request and checks response code for 200
  private String execute(HttpRequestBase request) throws ClientProtocolException, IOException {
    DefaultHttpClient httpClient = new DefaultHttpClient();
    HttpResponse response = httpClient.execute(request);

    HttpEntity entity = response.getEntity();
    String body = EntityUtils.toString(entity);

    if (response.getStatusLine().getStatusCode() != 200) {
      throw new RuntimeException("Expected 200 but got " + response.getStatusLine().getStatusCode() + ", with body " + body);
    }

    return body;
  }


  @GET
  @Path("/put")
  @Override
  //TODO put content file
  public Response sendFile(@QueryParam("content") String toUpload, @QueryParam("path") String destination, @QueryParam("token") String token) {
    String url = "https://content.dropboxapi.com/1/files_put/auto/" + destination + "?param=val&access_token=" + token;
    DefaultHttpClient httpClient = new DefaultHttpClient();
    StringBuilder result = new StringBuilder();
    try {
      HttpPut putRequest = new HttpPut(url);
      StringEntity input;
      try {
        input = new StringEntity(toUpload);
      } catch (Exception e) {
        return Response.status(200).entity(e.getMessage()).build();
      }
      putRequest.setEntity(input);
      HttpResponse response = httpClient.execute(putRequest);
      if (response.getStatusLine().getStatusCode() != 200) {
        return Response.status(200).entity("err:"+ response.getStatusLine().getStatusCode()).build();
      }
      BufferedReader br = new BufferedReader(new InputStreamReader(
      (response.getEntity().getContent())));
      String output;
      while ((output = br.readLine()) != null) {
        result.append(output);
      }
    } catch (Exception e) {
      return Response.status(200).entity(e.getMessage()).build();
    }
    return Response.status(200).entity(result.toString()).build();
  }


  @GET
  @Path("/info")
  @Override
  public Response getSpaceInfo(@QueryParam("token") String token) {
    String json=null;
    try {
      json = get(new StringBuilder("https://api.dropboxapi.com/1/account/info?access_token=").append(token)
      .toString());
    } catch (Exception e) {
      String url = new StringBuilder("https://api.dropboxapi.com/1/account/info?access_token=").append(token)
      .toString();
      json = "Unable to parse json " + json + "<br>token:" + token + "<br>" + url;
    }
    return Response.status(200).entity(json).build();
  }


  @GET
  @Path("/files")
  @Override
  public Response getFileInfo(@QueryParam("path") String file, @QueryParam("token") String token) {
    //TODO better path
    String json=null;
    try {
      String url = new StringBuilder("https://content.dropboxapi.com/1/files/auto/"+file+"?access_token=").append(token)      .toString();
      HttpGet request = new HttpGet(url);
      DefaultHttpClient httpClient = new DefaultHttpClient();
      HttpResponse response = httpClient.execute(request);
      for(Header h: response.getAllHeaders()) {
        json = h.getName() + "-" + h.getValue() + "<br>";
      }
    } catch (Exception e) {
      String url = new StringBuilder("https://content.dropboxapi.com/1/files/auto/"+file+"?access_token=").append(token)
      .toString();
      json = "Unable to parse json " + json + "<br>token:" + token + "<br>" + url;
    }
    return Response.status(200).entity(json).build();
  }


  @GET
  @Path("/mkdir")
  @Override
  public Response mkdir(@QueryParam("path") String folder, @QueryParam("token") String token) {
    //TODO better path
    String json=null;
    try {
      json = get(new StringBuilder("https://api.dropboxapi.com/1/fileops/create_folder?access_token=").append(token)
      .append("&root=auto")
      .append("&path=").append(folder)
      .toString());
    } catch (Exception e) {
      String url = new StringBuilder("https://api.dropboxapi.com/1/fileops/create_folder?access_token=").append(token)
      .append("&root=auto")
      .append("&path=").append(folder)
      .toString();
      json = "Unable to parse json " + json + "<br>token:" + token + "<br>" + url;
    }
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path("/rm")
  @Override
  public Response removeFile(@QueryParam("path") String file, @QueryParam("token") String token) {
    //TODO better path
    String json=null;
    try {
      json = get(new StringBuilder("https://api.dropbox.com/1/fileops/delete?access_token=").append(token)
      .append("&root=auto")
      .append("&path=").append(file)
      .toString());
    } catch (Exception e) {
      String url = new StringBuilder("https://api.dropbox.com/1/fileops/delete?access_token=").append(token)
      .append("&root=auto")
      .append("&path=").append(file)
      .toString();
      json = "Unable to parse json " + json + "<br>token:" + token + "<br>" + url;
    }
    return Response.status(200).entity(json).build();
  }


  @GET
  @Path("/mv")
  @Override
  public Response moveFile(@QueryParam("from") String from, @QueryParam("to") String to, @QueryParam("token") String token) {
    //TODO better path
    String json=null;
    try {
      json = post("https://api.dropboxapi.com/1/fileops/move", ImmutableMap.<String,String>builder()
      .put("root", "auto")
      .put("from_path", from)
      .put("to_path", to)
      .put("access_token", token).build());//TODO token here  ?
    } catch (Exception e) {
      json = "Unable to parse json " + json + "<br>token:" + token;
    }
    return Response.status(200).entity(json).build();
  }
}

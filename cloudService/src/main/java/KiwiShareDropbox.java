package kiwishare;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;

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

public class KiwiShareDropbox {//implements IKiwiShare {

  private static volatile KiwiShareDropbox _instance = null;
  private String _key = null;
  private String _secret = null;
  private String _callbackUrl = null;
  private String _token;

  private KiwiShareDropbox() {
    JSONObject obj = new JSONObject(KiwiUtils.readFile("config"));
    _key = obj.getString("app_key");
    _secret = obj.getString("app_secret");
    _callbackUrl = obj.getString("callback_url");
  }

  public static KiwiShareDropbox getInstance() {
    if(_instance == null) {
      synchronized (KiwiShareDropbox.class) {
        _instance =  new KiwiShareDropbox();
      }
    }
    return _instance;
  }

  public Response getAuthUrl() {
    Map<String, String> jsonContent = new HashMap();

    if(_key == null)
    {
      jsonContent.put("err", "incorrect config (app_key)");
    } else if(_secret == null) {
      jsonContent.put("err", "incorrect config (app_secret)");
    } else if(_callbackUrl == null) {
      jsonContent.put("err", "incorrect config (callback_url)");
    } else {
      String url = "https://www.dropbox.com/1/oauth2/authorize?response_type=code&" +
      "client_id=" + _key +
      "&redirect_uri=" + _callbackUrl;
      jsonContent.put("url", url);
    }

    JSONObject result = new JSONObject(jsonContent);
    return Response.status(200).entity(result.toString()).build();
  }

  public Response authentificate(String code, String error) {
    Map<String, String> jsonContent = new HashMap();

    String body = null;
    String accessToken = null;
    if (error != null) {
      jsonContent.put("err", error);
    }
    else {
      try {

        body = KiwiUtils.post("https://api.dropboxapi.com/1/oauth2/token", ImmutableMap.<String,String>builder()
        .put("code", code)
        .put("client_id", _key)
        .put("client_secret", _secret)
        .put("redirect_uri", _callbackUrl)
        .put("grant_type", "authorization_code").build());

        JSONObject obj = new JSONObject(body);
        accessToken = obj.getString("access_token");

        _token = accessToken;
        jsonContent.put("token", accessToken);
      } catch (Exception e) {
        jsonContent.put("err", "Unable to parse json " + body);
      }
    }

    JSONObject result = new JSONObject(jsonContent);
    return Response.status(200).entity(result.toString()).build();
  }

  public Response getFileInfo(String file) {
    //TODO try multi level path.
    String json=null;
    try {
      String url = new StringBuilder("https://content.dropboxapi.com/1/files/auto/"+file+"?access_token=").append(_token)      .toString();
      HttpGet request = new HttpGet(url);
      DefaultHttpClient httpClient = new DefaultHttpClient();
      HttpResponse response = httpClient.execute(request);
      for(Header h: response.getAllHeaders()) {
        json = h.getName() + "-" + h.getValue() + "<br>";
      }
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json);
      json = new JSONObject(jsonContent).toString();
    }
    return Response.status(200).entity(json).build();
  }

  public Response sendFile(String toUpload, String destination) {
    String url = "https://content.dropboxapi.com/1/files_put/auto/" + destination + "?param=val&access_token=" + _token;
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
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", e.getMessage() );
      return Response.status(200).entity(new JSONObject(jsonContent).toString()).build();
    }
    return Response.status(200).entity(result.toString()).build();
  }


  public Response getSpaceInfo() {
    String json=null;
    try {
      json = KiwiUtils.get(new StringBuilder("https://api.dropboxapi.com/1/account/info?access_token=").append(_token)
      .toString());
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json);
      json = new JSONObject(jsonContent).toString();
    }
    return Response.status(200).entity(json).build();
  }

  public Response mkdir(String folder) {
    //TODO better path
    String json=null;
    try {
      json = KiwiUtils.get(new StringBuilder("https://api.dropboxapi.com/1/fileops/create_folder?access_token=").append(_token)
      .append("&root=auto")
      .append("&path=").append(folder)
      .toString());
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json );
      json = new JSONObject(jsonContent).toString();
    }
    return Response.status(200).entity(json).build();
  }

  public Response removeFile(String file) {
    //TODO better path
    String json=null;
    try {
      json = KiwiUtils.get(new StringBuilder("https://api.dropbox.com/1/fileops/delete?access_token=").append(_token)
      .append("&root=auto")
      .append("&path=").append(file)
      .toString());
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json );
      json = new JSONObject(jsonContent).toString();
    }
    return Response.status(200).entity(json).build();
  }

  public Response moveFile(String from, String to) {
    //TODO better path
    String json=null;
    try {
      json = KiwiUtils.post("https://api.dropboxapi.com/1/fileops/move", ImmutableMap.<String,String>builder()
      .put("root", "auto")
      .put("from_path", from)
      .put("to_path", to)
      .put("access_token", _token).build());//TODO token here  ?
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json );
      json = new JSONObject(jsonContent).toString();
    }
    return Response.status(200).entity(json).build();
  }
}

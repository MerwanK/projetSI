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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.PrintWriter;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpEntity;
import org.apache.http.entity.FileEntity;
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

public class KiwiShareDropbox implements IServiceEndpoint {

  private static volatile KiwiShareDropbox _instance = null;
  private String _key = null;
  private String _secret = null;
  private String _callbackUrl = null;
  private String _token;

  private KiwiShareDropbox() {
    JSONObject obj = new JSONObject(KiwiUtils.readFile("dropbox.config"));
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

  public JSONObject getAuthUrl() {
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

    return new JSONObject(jsonContent);
  }

  public JSONObject authentificate(String code, String error) {
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

    return new JSONObject(jsonContent);
  }

  public JSONObject getFileInfo(String file) {
    JSONObject result = null;
    try {
      String url = new StringBuilder("https://content.dropboxapi.com/1/files/auto/"+file+"?access_token=").append(_token)      .toString();
      HttpGet request = new HttpGet(url);
      DefaultHttpClient httpClient = new DefaultHttpClient();
      HttpResponse response = httpClient.execute(request);

      HttpEntity entity = response.getEntity();
      String downloadUrl = this.shareFile(file).getString("url");

      result = new JSONObject(response.getFirstHeader("x-dropbox-metadata").getValue());
      result.put("download_url", downloadUrl);
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json ");
      return new JSONObject(jsonContent);
    }
    return result;
  }

  //TODO check img ? else content type
  public JSONObject sendFile(InputStream toUpload, String destination) {
    String url = "https://content.dropboxapi.com/1/files_put/auto/" + destination + "?param=val&access_token=" + _token;
    DefaultHttpClient httpClient = new DefaultHttpClient();
    StringBuilder result = new StringBuilder();
    try {
      HttpPut putRequest = new HttpPut(url);
      FileEntity input;
      try {
        File file = new File(destination); //TODO remove ?
        OutputStream outputStream = new FileOutputStream(file);
        IOUtils.copy(toUpload, outputStream);
        outputStream.close();
        input = new FileEntity(file);
        file.delete();
      } catch (Exception e) {
        Map<String, String> jsonContent = new HashMap();
        jsonContent.put("err", e.getMessage());
        return new JSONObject(jsonContent);
      }
      putRequest.setEntity(input);
      HttpResponse response = httpClient.execute(putRequest);
      if (response.getStatusLine().getStatusCode() != 200) {
        Map<String, String> jsonContent = new HashMap();
        jsonContent.put("err", new Integer(response.getStatusLine().getStatusCode()).toString());
        return new JSONObject(jsonContent);
      }
      BufferedReader br = new BufferedReader(new InputStreamReader(
      (response.getEntity().getContent())));
      String output;
      while ((output = br.readLine()) != null) {
        result.append(output);
      }
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", e.getMessage());
      return new JSONObject(jsonContent);
    }

    Map<String, String> jsonContent = new HashMap();
    jsonContent.put("send", result.toString());
    return new JSONObject(jsonContent);
  }


  public JSONObject getSpaceInfo() {
    String json=null;
    try {
      json = KiwiUtils.get(new StringBuilder("https://api.dropboxapi.com/1/account/info?access_token=").append(_token)
      .toString());
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json);
      return new JSONObject(jsonContent);
    }
    return new JSONObject(json);
  }

  public JSONObject mkdir(String folder) {
    String json=null;
    try {
      json = KiwiUtils.get(new StringBuilder("https://api.dropboxapi.com/1/fileops/create_folder?access_token=").append(_token)
      .append("&root=auto")
      .append("&path=").append(folder)
      .toString());
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json );
      return new JSONObject(jsonContent);
    }
    return new JSONObject(json);
  }

  public JSONObject removeFile(String file) {
    //TODO don't fail
    String json=null;
    try {
      json = KiwiUtils.get(new StringBuilder("https://api.dropbox.com/1/fileops/delete?access_token=").append(_token)
      .append("&root=auto")
      .append("&path=").append(file)
      .toString());
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json );
      return new JSONObject(jsonContent);
    }
    return new JSONObject(json);
  }

  public JSONObject moveFile(String from, String to) {
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
      return new JSONObject(jsonContent);
    }
    return new JSONObject(json);
  }

  public JSONObject shareFile(String file) {
    String json=null;
    try {
      json = KiwiUtils.get(new StringBuilder("https://api.dropboxapi.com/1/shares/auto/").append(file)
      .append("?access_token=").append(_token)
      .toString());
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json );
      return new JSONObject(jsonContent);
    }
    return new JSONObject(json);
  }

  private void mkPath(String currentPath, String url, JSONArray currentArray) {
    try {
      String json = KiwiUtils.get(url);
      JSONObject responseObj = new JSONObject(json);

      if(responseObj.getBoolean("is_dir")) {
        JSONArray sub = responseObj.getJSONArray("contents");
        for(int i = 0; i < sub.length(); ++i) {
          JSONObject file = sub.getJSONObject(i);
          JSONObject fileToAdd = new JSONObject();
          String nxtPath = file.getString("path");
          fileToAdd.put("path", nxtPath.substring(1));
          if(file.getBoolean("is_dir"))
          {
            mkPath(nxtPath,
            new StringBuilder("https://api.dropboxapi.com/1/metadata/auto").append(nxtPath)
            .append("?access_token=").append(_token)
            .toString()
            ,currentArray);
          }
          currentArray.put(fileToAdd);
        }
      }
    } catch (Exception e) { e.getMessage(); }
  }

  public JSONObject tree() {
    //TODO beautiful tree
    String json=null;
    JSONObject result = new JSONObject();
    try {
      JSONArray path = new JSONArray();
      mkPath("",
      new StringBuilder("https://api.dropboxapi.com/1/metadata/auto/")
      .append("?access_token=").append(_token)
      .toString(),
      path
      );
      result.put("files", path);
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json );
      return new JSONObject(jsonContent);
    }
    return result;
  }
}

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
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.PrintWriter;
import org.apache.commons.io.IOUtils;
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

public class KiwiShareGoogleDrive implements IServiceEndpoint {

  private static volatile KiwiShareGoogleDrive _instance = null;
  private String _key = null;
  private String _secret = null;
  private String _callbackUrl = null;
  private String _token;
  private Map<String, GoogleFile> _fileToId = null;

  private KiwiShareGoogleDrive() {
    JSONObject obj = new JSONObject(KiwiUtils.readFile("drive.config"));
    _key = obj.getString("app_key");
    _secret = obj.getString("app_secret");
    _callbackUrl = obj.getString("callback_url");
    this._fileToId = new HashMap<String, GoogleFile>();
  }

  public static KiwiShareGoogleDrive getInstance() {
    if(_instance == null) {
      synchronized (KiwiShareGoogleDrive.class) {
        _instance =  new KiwiShareGoogleDrive();
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
      String url = new StringBuilder("https://accounts.google.com/o/oauth2/v2/auth?response_type=code&scope=https://www.googleapis.com/auth/drive&")
      .append("client_id=").append(_key)
      .append("&redirect_uri=").append(_callbackUrl)
      .toString();
      jsonContent.put("url", url);
    }

    JSONObject result = new JSONObject(jsonContent);
    return result;
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

        body = KiwiUtils.post("https://www.googleapis.com/oauth2/v4/token", ImmutableMap.<String,String>builder()
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
    return result;
  }

  public JSONObject getFileInfo(String file) {
    //TODO check path & parents
    this.synchronize();
    String filename = this._fileToId.get(file).getId();
    Map<String, String> jsonContent = new HashMap();
    String json=null;
    try {
      String url = new StringBuilder("https://www.googleapis.com/drive/v2/files/"+filename+"?access_token=").append(_token)      .toString();
      HttpGet request = new HttpGet(url);
      DefaultHttpClient httpClient = new DefaultHttpClient();
      HttpResponse response = httpClient.execute(request);

      HttpEntity entity = response.getEntity();
      json = EntityUtils.toString(entity);
    } catch (Exception e) {
      jsonContent.put("err", "Unable to parse json " + json + " for filename:" + filename);
      return new JSONObject(jsonContent);
    }
    return new JSONObject(json);
  }




  public JSONObject sendFile(InputStream toUpload, String destination) {
    //TODO : post
    Map<String, String> jsonContent = new HashMap();
    jsonContent.put("err", "TODO");
    return new JSONObject(jsonContent);
    /*String url = "https://www.googleapis.com/upload/drive/v2/files" + destination + "?param=val&access_token=" + _token;
    DefaultHttpClient httpClient = new DefaultHttpClient();
    StringBuilder result = new StringBuilder();
    try {
    HttpPut putRequest = new HttpPut(url);
    StringEntity input;
    try {
    input = new StringEntity(toUpload);
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
return new JSONObject(jsonContent);*/
}


public JSONObject getSpaceInfo() {
  String json=null;
  try {
    json = KiwiUtils.get(new StringBuilder("https://www.googleapis.com/drive/v2/about?access_token=").append(_token)
    .toString());
  } catch (Exception e) {
    Map<String, String> jsonContent = new HashMap();
    jsonContent.put("err", "Unable to parse json " + json);
    return new JSONObject(jsonContent);
  }
  return new JSONObject(json);
}

public JSONObject mkdir(String folder) {
  //TODO parents + path
  String json=null;
  try {
    JSONObject fileDesc = new JSONObject();
    fileDesc.put("title", folder);
    fileDesc.put("mimeType", "application/vnd.google-apps.folder");
    json = KiwiUtils.post("https://www.googleapis.com/drive/v2/files?access_token=" + _token, fileDesc);
  } catch (Exception e) {
    Map<String, String> jsonContent = new HashMap();
    jsonContent.put("err", "Unable to parse json " + json );
    return new JSONObject(jsonContent);
  }
  return new JSONObject(json);
}

public JSONObject removeFile(String file) {
  //TODO path + clean err
  try {
    this.synchronize();
    String idToDelete = _fileToId.get(file).getId();
    String json=null;
    json = KiwiUtils.delete(new StringBuilder("https://www.googleapis.com/drive/v2/files/").append(idToDelete)
    .append("?access_token=").append(_token)
    .toString());
  } catch (Exception e) {
    Map<String, String> jsonContent = new HashMap();
    jsonContent.put("err", "Unable to parse json " + json );
    return new JSONObject(jsonContent);
  }
  return new JSONObject("{}");
}

public JSONObject moveFile(String from, String to) {
  //TODO
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
  //TODO
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

public JSONObject tree() {
  //TODO generate tree
  String json=null;
  try {
    json = KiwiUtils.get(new StringBuilder("https://www.googleapis.com/drive/v2/files")
    .append("?access_token=").append(_token)
    .append("&spaces=drive")
    .toString());
  } catch (Exception e) {
    Map<String, String> jsonContent = new HashMap();
    jsonContent.put("err", "Unable to parse json " + json );
    return new JSONObject(jsonContent);
  }
  return new JSONObject(json);
}

//TODO: more clever sync
private void synchronize() {
  JSONObject content = tree();
  JSONArray items = content.getJSONArray("items");
  this._fileToId = new HashMap<String, GoogleFile>();
  for(int i = 0; i < items.length(); ++i) {
    JSONObject item = items.getJSONObject(i);
    String id = item.getString("id");
    String title = item.getString("title");
    List<GoogleFolder> parents = new ArrayList<GoogleFolder>();
    JSONArray parentsArray = item.getJSONArray("parents");
    for(int j = 0; j < parentsArray.length(); ++j) {
      JSONObject parent = parentsArray.getJSONObject(j);
      parents.add(new GoogleFolder(parent.getString("id"), parent.getBoolean("isRoot")));
    }
    this._fileToId.put(title, new GoogleFile(id, title, parents));
  }
}


private class GoogleFile {
  private String _id = null;
  private String _title = null;
  private List<GoogleFolder> _parents = null;

  public GoogleFile(String id, String title, List<GoogleFolder> parents) {
    this._id = id;
    this._title = title;
    this._parents = parents;
  }

  public String getId() { return this._id; }
  public String getTitle() { return this._title; }
  public List<GoogleFolder> getParents() { return this._parents; }
}


private class GoogleFolder {
  private String _id = null;
  private boolean _isRoot = false;

  public GoogleFolder(String id, boolean root) {
    this._id = id;
    this._isRoot = root;
  }

  public String getId() { return this._id; }

  public boolean isRoot() { return _isRoot; }
}
}

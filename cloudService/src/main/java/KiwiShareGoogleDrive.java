package kiwishare;

import com.google.common.collect.ImmutableMap;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class KiwiShareGoogleDrive implements IServiceEndpoint {

  //Singleton to save token
  private static volatile KiwiShareGoogleDrive _instance = null;
  private String _key = null;
  private String _secret = null;
  private String _callbackUrl = null;
  private String _token;
  //To transform an ID to a File with some infos
  private Map<String, GoogleFile> _IdToFile = null;
  //To transform a path to an ID
  private Map<String, String> _pathToId = null;

  private KiwiShareGoogleDrive() {
    JSONObject obj = new JSONObject(KiwiUtils.readFile("drive.config"));
    _key = obj.getString("app_key");
    _secret = obj.getString("app_secret");
    _callbackUrl = obj.getString("callback_url");
    this._IdToFile = new HashMap<String, GoogleFile>();
    this._pathToId = new HashMap<String, String>();
  }

  public static KiwiShareGoogleDrive getInstance() {
    if (_instance == null) {
      synchronized (KiwiShareGoogleDrive.class) {
        _instance =  new KiwiShareGoogleDrive();
      }
    }
    return _instance;
  }

  /**
  * @return the secret key of kiwishare
  **/
  public final String getSecret() { return this._secret; }

  /**
  * @return the token of kiwishare
  **/
  public final String getToken() { return this._token; }

  public JSONObject getAuthUrl() {
    Map<String, String> jsonContent = new HashMap();

    if (_key == null) {
      jsonContent.put("err", "incorrect config (app_key)");
    } else if (_secret == null) {
      jsonContent.put("err", "incorrect config (app_secret)");
    } else if (_callbackUrl == null) {
      jsonContent.put("err", "incorrect config (callback_url)");
    } else {
      String url = new StringBuilder(
      "https://accounts.google.com/o/oauth2/v2/auth"
      + "?response_type=code&scope=https://www.googleapis.com/auth/drive&")
      .append("client_id=").append(_key)
      .append("&redirect_uri=").append(_callbackUrl)
      .toString();
      jsonContent.put("url", url);
    }

    JSONObject result = new JSONObject(jsonContent);
    return result;
  }

  public JSONObject authentificate(final String code, final String error) {
    Map<String, String> jsonContent = new HashMap();

    String body = null;
    String accessToken = null;
    if (error != null) {
      jsonContent.put("err", error);
    } else {
      try {

        body = KiwiUtils.post("https://www.googleapis.com/oauth2/v4/token",
        ImmutableMap.<String, String>builder()
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

  public JSONObject getFileInfo(final String file) {
    this.synchronize();
    String filename = this._pathToId.get(file);
    Map<String, String> jsonContent = new HashMap();
    String json = null;
    try {
      String url =
      new StringBuilder("https://www.googleapis.com/drive/v2/files/"
      + filename + "?access_token=").append(_token).toString();
      HttpGet request = new HttpGet(url);
      DefaultHttpClient httpClient = new DefaultHttpClient();
      HttpResponse response = httpClient.execute(request);

      HttpEntity entity = response.getEntity();
      json = EntityUtils.toString(entity);
    } catch (Exception e) {
      jsonContent.put("err", "Unable to parse json " + json
      + " for filename:" + filename);
      return new JSONObject(jsonContent);
    }
    return new JSONObject(json);
  }

  public JSONObject sendFile(final InputStream toUpload,
  final String destination) {
    String json = null;
    try {
      this.synchronize();
      String parent = "";
      String title = "";

      Map<String, String> infoPath = getInfoFromPath(destination);
      //Create a file desc JSONObject
      JSONObject fileDesc = createJSONdesc(infoPath.get("filename"),
      infoPath.get("directParent"), null);
      json = KiwiUtils.postMultipart(
      "https://www.googleapis.com/upload/drive/v2/files?uploadType=multipart"
      + "&access_token=" + _token, fileDesc,
      toUpload);
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json);
      return new JSONObject(jsonContent);
    }
    return new JSONObject(json);
  }


  public JSONObject getSpaceInfo() {
    String json = null;
    try {
      json = KiwiUtils.get(
      new StringBuilder("https://www.googleapis.com/drive/v2/about")
      .append("?access_token=" + _token)
      .toString());
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json);
      return new JSONObject(jsonContent);
    }
    return new JSONObject(json);
  }

  public JSONObject mkdir(final String folder) {
    String json = null;
    try {
      this.synchronize();

      Map<String, String> infoPath = getInfoFromPath(folder);
      JSONObject fileDesc = createJSONdesc(infoPath.get("filename"),
      infoPath.get("directParent"), "application/vnd.google-apps.folder");
      json = KiwiUtils.post(
      "https://www.googleapis.com/drive/v2/files?access_token=" + _token,
      fileDesc);
    } catch (RuntimeException e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err",
      "Can't create dir " + folder + ". Already exists?");
      return new JSONObject(jsonContent);
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "service not connected?");
      return new JSONObject(jsonContent);
    }
    return new JSONObject(json);
  }

  public JSONObject removeFile(final String file) {
    String json = null;
    try {
      this.synchronize();
      String idToDelete = this._pathToId.get(file);
      json = KiwiUtils.delete(
      new StringBuilder("https://www.googleapis.com/drive/v2/files/")
      .append(idToDelete)
      .append("?access_token=").append(_token)
      .toString());
    } catch (RuntimeException e) {
      //Ok the file is deleted (Yeap the API of google drive is...)
      return new JSONObject("{}");
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "service not connected?");
      return new JSONObject(jsonContent);
    }
    return new JSONObject("{}");
  }

  public JSONObject moveFile(final String from, final String to) {
    String json = null;
    try {
      this.synchronize();
      String id = this._pathToId.get(from);
      String actualParent = getInfoFromPath(from).get("directParent");

      Map<String, String> infoPath = getInfoFromPath(to);
      String newParent = infoPath.get("directParent");
      String newTitle = infoPath.get("filename");

      JSONObject fileDesc = createJSONdesc(newTitle, null, null);
      json = KiwiUtils.put("https://www.googleapis.com/drive/v2/files/" + id
      + "?access_token=" + _token
      + "&addParents=" + newParent
      + "&removeParents=" + actualParent,
      fileDesc
      );
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json);
      return new JSONObject(jsonContent);
    }
    return new JSONObject(json);
  }

  public JSONObject shareFile(final String file) {
    JSONObject result = new JSONObject();
    String json = null;
    try {
      this.synchronize();
      String idToShare = this._pathToId.get(file);
      JSONObject permDesc = new JSONObject();
      permDesc.put("role", "owner");
      permDesc.put("type", "anyone");
      json = KiwiUtils.post(
      new StringBuilder("https://www.googleapis.com/drive/v2/files/")
      .append(idToShare)
      .append("/permissions?access_token=").append(_token)
      .toString(), permDesc);
    } catch (RuntimeException ignore) {
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Unable to parse json " + json + e.getMessage());
      return new JSONObject(jsonContent);
    }
    try {
      String id = this._pathToId.get(file);
      result.put("link", _IdToFile.get(id).getLink());
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "File not found: " + file);
      return new JSONObject(jsonContent);
    }
    return result;
  }

  public JSONObject tree() {
    JSONObject result = new JSONObject();
    try {
      this.synchronize();
      JSONArray files = new JSONArray();
      for (String key : this._pathToId.keySet()) {
        if (!key.equals("/")) {
          JSONObject file = new JSONObject();
          file.put("path", key);
          files.put(file);
        }
      }
      result.put("files", files);
    } catch (Exception e) {
      Map<String, String> jsonContent = new HashMap();
      jsonContent.put("err", "Can't build tree");
      return new JSONObject(jsonContent);
    }
    return result;
  }

  /**
  * Split path into (filename, directParent, parentPath).
  * @param pathToFile the file to inspect
  * @return a map with (filename, directParent, parentPath) infos
  **/
  private Map<String, String> getInfoFromPath(final String pathToFile) {
    Map<String, String> result = new HashMap<String, String>();
    String[] path = pathToFile.split("/");
    if (path.length == 1) {
      result.put("filename", pathToFile);
      result.put("directParent", this._pathToId.get("/"));
      result.put("parentPath", "/");
    } else {
      String pathP = "";
      for (int i = 0; i <= path.length - 2; ++i) {
        if (i != 0) {
          pathP += "/";
        }
        pathP += path[i];
      }
      result.put("filename", path[path.length - 1]);
      result.put("directParent", this._pathToId.get(pathP));
      result.put("parentPath", pathP);
    }
    return result;
  }

  /**
  * Create a file description JSON for google drive.
  * @param title the name of the file
  * @param idParent the id of the parent
  * @param mimeType the mimeType of the file
  * @return a JSON which describes the file
  **/
  private JSONObject createJSONdesc(final String title,
  final String idParent,
  final String mimeType) {
    JSONObject fileDesc = new JSONObject();
    fileDesc.put("title", title);
    if (mimeType != null) {
      fileDesc.put("mimeType", mimeType);
    }
    if (idParent != null) {
      JSONArray parents = new JSONArray();
      JSONObject pObj = new JSONObject();
      pObj.put("id", idParent);
      parents.put(pObj);
      fileDesc.put("parents", parents);
    }
    return fileDesc;
  }

  //TODO: more clever sync
  private void synchronize() {
    try {
      JSONObject content = new JSONObject(KiwiUtils.get(
      new StringBuilder("https://www.googleapis.com/drive/v2/files")
      .append("?access_token=").append(_token)
      .append("&spaces=drive")
      .toString()));

      JSONArray items = content.getJSONArray("items");
      this._IdToFile = new HashMap<String, GoogleFile>();
      this._pathToId = new HashMap<String, String>();
      //Build IdToFile
      for (int i = 0; i < items.length(); ++i) {
        JSONObject item = items.getJSONObject(i);
        String id = item.getString("id");
        String title = item.getString("title");
        String link = item.getString("alternateLink");
        List<GoogleFolder> parents = new ArrayList<GoogleFolder>();
        JSONArray parentsArray = item.getJSONArray("parents");
        for (int j = 0; j < parentsArray.length(); ++j) {
          JSONObject parent = parentsArray.getJSONObject(j);
          parents.add(new GoogleFolder(parent.getString("id"),
          parent.getBoolean("isRoot")));
          if (parent.getBoolean("isRoot")) {
            this._pathToId.put("/", parent.getString("id"));
          }
        }
        this._IdToFile.put(id, new GoogleFile(id, title, link, parents));
      }

      //Build pathToId
      for (String key : this._IdToFile.keySet()) {
        GoogleFile gf = this._IdToFile.get(key);
        String finalPath = gf.getTitle();
        String id = gf.getId();
        //TODO multi-parent ?
        GoogleFolder parent = gf.getParents().get(0);
        while (!parent.isRoot()) {
          GoogleFile parentFile = this._IdToFile.get(parent.getId());
          finalPath = parentFile.getTitle() + "/" + finalPath;
          parent = parentFile.getParents().get(0);
        }
        this._pathToId.put(finalPath, id);
      }
    } catch (Exception e) {
      e.getMessage();
    }
  }

  /**
  * This class is used to describe a file.
  **/
  private class GoogleFile {
    private String _id = null;
    private String _title = null;
    private String _link = null;
    private List<GoogleFolder> _parents = null;

    public GoogleFile(final String id, final String title,
    final String link, final List<GoogleFolder> parents) {
      this._id = id;
      this._title = title;
      this._link = link;
      this._parents = parents;
    }

    public String getId() { return this._id; }
    public String getTitle() { return this._title; }
    public String getLink() { return this._link; }
    public List<GoogleFolder> getParents() { return this._parents; }
  }

  /**
  * This class is used to describe a directory.
  **/
  private class GoogleFolder {
    private String _id = null;
    private boolean _isRoot = false;

    public GoogleFolder(final String id, final boolean root) {
      this._id = id;
      this._isRoot = root;
    }

    public String getId() { return this._id; }
    public boolean isRoot() { return _isRoot; }
  }
}

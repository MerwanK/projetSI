package kiwishare;

import org.json.*;

public interface IServiceEndpoint {

  public JSONObject getAuthUrl();

  public JSONObject authentificate(String code, String error);

  public JSONObject getFileInfo(String file);

  public JSONObject sendFile(String toUpload, String destination);

  public JSONObject getSpaceInfo();

  public JSONObject mkdir(String folder);

  public JSONObject removeFile(String file);

  public JSONObject moveFile(String from, String to);

  public JSONObject shareFile(String file);

  public JSONObject tree();

}

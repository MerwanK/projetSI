package kiwishare;

import java.io.InputStream;
import org.json.JSONObject;

/**
* This interface describes methods for a service
**/
public interface IServiceEndpoint {

  /**
  * Return the url for OAuth2 authentification {"name":"SERVICENAME", "url","URL"}
  */
  public JSONObject getAuthUrl();

  /**
  * Callback action
  */
  public JSONObject authentificate(String code, String error);

  /**
  * @return informations on the file
  **/
  public JSONObject getFileInfo(String file);

  /**
  * Upload a file
  **/
  public JSONObject sendFile(InputStream toUpload, String destination);

  /**
  * @return informations on the space
  **/
  public JSONObject getSpaceInfo();

  /**
  * Create a directory
  **/
  public JSONObject mkdir(String folder);

  /**
  * Delete a file
  **/
  public JSONObject removeFile(String file);

  /**
  * Move a file
  **/
  public JSONObject moveFile(String from, String to);

  /**
  * Get the link for sharing
  **/
  public JSONObject shareFile(String file);

  /**
  * Get the tree of a service ([{"path":"PATH"}])
  **/
  public JSONObject tree();

}

package kiwishare;

import java.io.InputStream;
import org.json.JSONObject;

/**
* This interface describes methods for a service.
**/
interface IServiceEndpoint {

  /**
  * @return the url for OAuth2 {"name":"SERVICENAME", "url","URL"}.
  */
  JSONObject getAuthUrl();

  /**
  * Callback action.
  * @param code - the code used for callback action
  * @param error - if an error occured
  * @return the token
  */
  JSONObject authentificate(String code, String error);

  /**
  * @param file - the path to the file
  * @return informations on the file
  **/
  JSONObject getFileInfo(String file);

  /**
  * Upload a file.
  * @param toUpload - the stream of the file to upload
  * @param destination - the final destination
  * @return informations on the file
  **/
  JSONObject sendFile(InputStream toUpload, String destination);

  /**
  * @return informations on the space
  **/
  JSONObject getSpaceInfo();

  /**
  * Create a directory.
  * @param folder - the path of the folder
  * @return informations on the file
  **/
  JSONObject mkdir(String folder);

  /**
  * Delete a file.
  * @param file - the path to the file
  * @return informations on the file
  **/
  JSONObject removeFile(String file);

  /**
  * Move a file.
  * @param from - the original path
  * @param to - the target path
  * @return informations on the file
  **/
  JSONObject moveFile(String from, String to);

  /**
  * Get the link for sharing.
  * @param file - the path to the file
  * @return the url link to the file
  **/
  JSONObject shareFile(String file);

  /**
  * @return Get the tree of a service ([{"path":"PATH"}]).
  **/
  JSONObject tree();

}

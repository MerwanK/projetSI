package kiwishare;

import javax.ws.rs.core.Response;

public interface IKiwiShare {

  /**
  * Get OAuth2 url
  * @return {"err":"ERROR"} if error else {"url":"URL"}
  */
  public Response getAuthUrl(String type);

  /**
  * Authentificate (return a token)
  * @param code: the code for OAuth given at the authorization
  * @param error: if an error occcurs previously
  * @return {"err":"ERROR"} if error else {"token":"TOKEN"}
  */
  public Response authentificateDropbox(String code, String error);

  /**
  * Get info from a file
  */
  public Response getFileInfo(String file, String token);

  /**
  * Send a file
  */
  public Response sendFile(String toUpload, String destination, String token);

  /**
  * Share a file
  */
  //public Response shareFile(String file, String otherUser);//TODO

  /**
  * Get free space
  */
  public Response getSpaceInfo(String token);

  /**
  * Move a file
  * Can also be used to rename a file
  */
  public Response moveFile(String from, String to, String token);

  /**
  * Remove a file
  */
  public Response removeFile(String file, String token);

  /**
  * Make directory
  */
  public Response mkdir(String folder, String token);

}

package kiwishare;

import javax.ws.rs.core.Response;

public interface IKiwiShare {

  public Response getAuthUrl();

  /**
  * Authentificate
  */
  public Response authentificate(String code, String error);

  /**
  * Send a file
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

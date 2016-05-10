package net.mail.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import java.util.ArrayList;

@WebService(name = "Drive", targetNamespace = "http://david.bromberg.fr/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface Drive {

  /**
   * Authentificate
   */
  @WebMethod
  public void authentificate(String username, String password);

  /**
  * Send a file
  */
  @WebMethod
  public Map<String, String> getFileInfo(String file);

  /**
  * Send a file
  */
  @WebMethod
  public void sendFile(String file);

  /**
  * Share a file
  */
  @WebMethod
  public void shareFile(String file, String otherUser);

  /**
  * Get free space
  */
  @WebMethod
  public Map<String, String> getSpaceInfo();

  /**
  * Move a file
  * Can also be used to rename a file
  */
  @WebMethod
  public void moveFile(String from, String to);

  /**
  * Remove a file
  */
  @WebMethod
  public void removeFile(String file);

  /**
  * Synchronize folder
  */
  @WebMethod
  public void syncFolder(String folder);

  /**
  * Make directory
  */
  @WebMethod
  public void mkdir(String folder);
}

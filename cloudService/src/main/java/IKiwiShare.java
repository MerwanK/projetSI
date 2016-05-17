package kiwishare;

import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Describes methods for the webservice.
 **/
interface IKiwiShare {

  /**
  * Get OAuth2 url.
  * @return [({"service":"NAME", "url":"URL"})*]
  */
  Response getAuthUrl();

  /**
  * Authentificate (return a token).
  * @param code the code for OAuth given at the authorization
  * @param error if an error occcurs previously
  * @return {"err":"ERROR"} if error else {"token":"TOKEN"}
  */
  Response authentificateDropbox(String code, String error);

  /**
  * Authentificate (return a token).
  * @param code the code for OAuth given at the authorization
  * @param error if an error occcurs previously
  * @return {"err":"ERROR"} if error else {"token":"TOKEN"}
  */
  Response authentificateDrive(String code, String error);

  /**
  * Get info from a file.
  * @param file - the path to the file
  * @return a json with the informations
  */
  Response getFileInfo(String file);

  /**
  * Send a file.
  * @param file - the path to the file
  * @param destination - the final path
  * @return a json with the informations
  */
  Response sendFile(InputStream file, String destination);

  /**
  * Share a file.
  * @param file - the path to the file
  * @return a json with the informations
  */
  Response shareFile(String file);

  /**
  * Get free space.
  * @return a json with the informations
  */
  Response getSpaceInfo();

  /**
  * Move a file.
  * Can also be used to rename a file
  * @param from - the original path
  * @param to - the final path
  * @return a json with the informations
  */
  Response moveFile(String from, String to);

  /**
  * Remove a file.
  * @param file - the final path
  * @return a json with the informations
  */
  Response removeFile(String file);

  /**
  * Make directory.
  * @param folder - the final path
  * @return a json with the informations
  */
  Response mkdir(String folder);

  /**
  * list files in directory.
  * @param merge if "true" or "1", all path from services are merged.
  * @return a json with the informations
  */
  Response tree(String merge);

}

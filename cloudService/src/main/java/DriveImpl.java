package net.cloudservice;

import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.InputStreamReader;


import com.dropbox.core.*;

import javax.jws.WebParam;
import javax.jws.WebService;

import java.io.FileInputStream;
import java.io.FileOutputStream;

@WebService(endpointInterface = "net.cloudservice.Drive", serviceName = "MyOwnDriveAgent")
public class DriveImpl {

  private DbxClient _dropboxClient;

  /**
  * Authentificate
  */
  public void authentificate(String username, String password) {

    //DROPBOX
    // Get your app key and secret from the Dropbox developers website.
    try {
      final String APP_KEY = "INSERT_APP_KEY";
      final String APP_SECRET = "INSERT_APP_SECRET";

      DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

      DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
      Locale.getDefault().toString());
      DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

      // Have the user sign in and authorize your app.
      String authorizeUrl = webAuth.start();
      System.out.println("1. Go to: " + authorizeUrl);
      System.out.println("2. Click \"Allow\" (you might have to log in first)");
      System.out.println("3. Copy the authorization code.");
      String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

      // This will fail if the user enters an invalid authorization code.
      DbxAuthFinish authFinish = webAuth.finish(code);
      String accessToken = authFinish.accessToken;

      _dropboxClient = new DbxClient(config, accessToken);

      System.out.println("Linked account: " + _dropboxClient.getAccountInfo().displayName);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }

  }

  /**
  * Send a file
  */
  public Map<String, String> getFileInfo(String file) {
    return null;
  }

  /**
  * Send a file
  */
  public void sendFile(FileInputStream toUpload, String destination) {
    /*TODO try {
    DbxEntry.File uploadedFile = _dropboxClient.uploadFile(destination,
    DbxWriteMode.add(), toUpload.length(), toUpload);
    System.out.println("Uploaded: " + uploadedFile.toString());//TODO log
  } finally {
  toUpload.close();
}*/
}

/**
* Get a file
*/
public FileOutputStream getFile(String file) {
  FileOutputStream outputStream = null;
  try {
    outputStream = new FileOutputStream(file);
    DbxEntry.File downloadedFile = _dropboxClient.getFile(file, null,
    outputStream);
    System.out.println("Metadata: " + downloadedFile.toString());//TODO log
    outputStream.close();
  } catch (Exception ex) {
    System.err.println(ex.getMessage());
  }
  return outputStream;
}

/**
* Share a file
*/
public void shareFile(String file, String otherUser) {

}

/**
* Get free space
*/
public Map<String, String> getSpaceInfo() {
  return null;
}

/**
* Move a file
* Can also be used to rename a file
*/
public void moveFile(String from, String to) {

}

/**
* Remove a file
*/
public void removeFile(String file) {

}

/**
* Synchronize folder
*/
public void syncFolder(String folder) {

}

/**
* Make directory
*/
public void mkdir(String folder) {

}

}

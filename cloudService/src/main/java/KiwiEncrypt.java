package kiwishare;

import com.google.common.collect.ImmutableMap;
import com.sun.jersey.multipart.FormDataParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.*;

@Path("/kiwiencrypt")
public class KiwiEncrypt {

  @POST
  @Path("/encrypt")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  /**
  * Encrypt a file
  * @param file the file to encrypt
  * @return a response with the encrypted file in the body
  **/
  public Response encrypt(@FormDataParam("file") InputStream file) {
    String result = "";
    //Create a random file to encrypt (server-side)
    String name = UUID.randomUUID().toString();
    try {
      File targetFile = new File(name);
      FileUtils.copyInputStreamToFile(file, targetFile);

      //GnuPG command to execute
      String command = "";
      String pubKey = KiwiUtils.getInstance().getGpgKey();
      String pass = KiwiUtils.getInstance().getGpgSecret();
      if(pubKey != null) //use default key
      command = "gpg --recipient " + pubKey + " --encrypt --armor --sign --passphrase " + pass + " " + name;
      else
      command = "gpg --default-recipient-self --encrypt --armor --sign --passphrase " + pass + " " + name;
      //Encrypt All The Things!
      Process p;
      p = Runtime.getRuntime().exec(command);
      p.waitFor();

      //Get result
      BufferedReader reader = new BufferedReader(new FileReader(name + ".asc"));
      String line = "";
      while ((line = reader.readLine())!= null) {
        result += line + "\n";
      }

      //Remove useless files
      File encryptedFile = new File(name+".asc");
      targetFile.delete();
      encryptedFile.delete();
    } catch(Exception e) {
      return Response.status(200).entity(e.getMessage()).build();
    }
    return Response.status(200).entity(result).build();
  }


  @POST
  @Path("/decrypt")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  /**
  * Decrypt a file
  * @param file the file to decrypt
  * @return a response with the decrypted file in the body
  **/
  public Response decrypt(@FormDataParam("file") InputStream file) {
    Response ok = null;
    //Create a file to decrypt (server-side)
    String name = UUID.randomUUID().toString();
    try {
      File targetFile = new File(name);
      FileUtils.copyInputStreamToFile(file, targetFile);
      //The GnuPG command to execute
      String command = "gpg --batch --decrypt --passphrase " + KiwiUtils.getInstance().getGpgSecret() + " --output " + name + ".dec " + name;
      //Decrypt file
      Process p;
      p = Runtime.getRuntime().exec(command);
      p.waitFor();
      //Create result response
      ok = Response.ok(new FileInputStream(name+".dec")).status(200).build();
      //Remove useless files
      File decryptedFile = new File(name+".dec");
      targetFile.delete();
      decryptedFile.delete();
    } catch(Exception e) {
      return Response.status(200).entity(e.getMessage()).build();
    }
    return ok;
  }
}

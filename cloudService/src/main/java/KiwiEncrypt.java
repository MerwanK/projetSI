package kiwishare;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import java.util.Map;
import java.io.FileReader;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.PrintWriter;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.Header;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import java.text.ParseException;
import org.json.*;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.multipart.FormDataParam;

@Path("/kiwiencrypt")
public class KiwiEncrypt {

  @POST
  @Path("/encrypt")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response encrypt(@FormDataParam("file") InputStream file, @QueryParam("name") String name) {
    String result = "";
    try {
      Process p;

      File targetFile = new File(name);

      FileUtils.copyInputStreamToFile(file, targetFile);
      String command = "gpg --default-recipient-self --encrypt --armor --sign --passphrase " + KiwiUtils.getInstance().getGpgSecret() + " " + name;
      p = Runtime.getRuntime().exec(command);
      p.waitFor();

      BufferedReader reader = new BufferedReader(new FileReader(name + ".asc"));

      String line = "";
      while ((line = reader.readLine())!= null) {
      result += line + "\n";
    }

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
//TODO remove name param
public Response decrypt(@FormDataParam("file") InputStream file, @QueryParam("name") String name) {
  Response ok = null;
  try {
    Process p;

    File targetFile = new File(name);

    FileUtils.copyInputStreamToFile(file, targetFile);
    String command = "gpg --batch --decrypt --passphrase " + KiwiUtils.getInstance().getGpgSecret() + " --output " + name + ".dec " + name;
    p = Runtime.getRuntime().exec(command);
    p.waitFor();
/*
    BufferedReader reader = new BufferedReader(new FileReader(name + ".dec"));

    String line = "";
    while ((line = reader.readLine())!= null) {
      result += line + "\n";
    }*/
    ok = Response.ok(new FileInputStream(name+".dec")).status(200).build();

    File decryptedFile = new File(name+".dec");
    targetFile.delete();
    decryptedFile.delete();

  } catch(Exception e) {
    return Response.status(200).entity(e.getMessage()).build();
  }
  return ok;
}
}

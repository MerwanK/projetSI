package kiwishare;

import org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.io.IOUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.PrintWriter;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.Header;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import java.text.ParseException;
import org.json.*;
import com.google.common.collect.ImmutableMap;

public class KiwiUtils {

  private static volatile KiwiUtils _instance = null;
  private String _gpgKey = null;
  private String _gpgSecret = null;
  private String _token;

  private KiwiUtils() {
    JSONObject obj = new JSONObject(KiwiUtils.readFile("gpg.config"));
    _gpgKey = obj.getString("gpg_key");
    _gpgSecret = obj.getString("gpg_pass");
  }

  public static KiwiUtils getInstance() {
    if(_instance == null) {
      synchronized (KiwiUtils.class) {
        _instance =  new KiwiUtils();
      }
    }
    return _instance;
  }

  public String getGpgKey() { return _gpgKey; }
  public String getGpgSecret() { return _gpgSecret; }

  public static String readFile(String path) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(path));
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append(System.lineSeparator());
        line = br.readLine();
      }
      String everything = sb.toString();
      return everything;
    } catch(Exception e) {
      return "";
    }
  }

  // makes a GET request to url and returns body as a string
  public static String get(String url) throws ClientProtocolException, IOException {
    return execute(new HttpGet(url));
  }

  // makes a DELETE request to url and returns body as a string
  public static String delete(String url) throws ClientProtocolException, IOException {
    return execute(new HttpDelete(url));
  }

  // makes a POST request to url with form parameters and returns body as a string
  public static String post(String url, Map<String,String> formParameters) throws ClientProtocolException, IOException {
    HttpPost request = new HttpPost(url);
    List <NameValuePair> nvps = new ArrayList <NameValuePair>();

    for (String key : formParameters.keySet()) {
      nvps.add(new BasicNameValuePair(key, formParameters.get(key)));
    }

    request.setEntity(new UrlEncodedFormEntity(nvps));

    return execute(request);
  }

  // makes a POST request to url with form parameters and returns body as a string
  public static String put(String url, JSONObject content) throws ClientProtocolException, IOException {
    HttpPut request = new HttpPut(url);
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json; charset=UTF-8");
    HttpEntity ent = new StringEntity(content.toString());
    request.setEntity(ent);
    return execute(request);
  }

  public static String post(String url, Map<String,String> formParameters, String body) throws ClientProtocolException, IOException {
    HttpPost request = new HttpPost(url);

    for (String key : formParameters.keySet()) {
      request.setHeader(key, formParameters.get(key));
    }

    HttpEntity ent = new StringEntity(body);
    request.setEntity(ent);

    return execute(request);
  }


  public static String postMultipart(String url, JSONObject fileDesc, InputStream body) throws ClientProtocolException, IOException {
    HttpPost request = new HttpPost(url);

    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addTextBody("file", fileDesc.toString(), ContentType.APPLICATION_JSON);
    builder.addBinaryBody("file", body);
    HttpEntity multipart = builder.build();

    request.setEntity(multipart);

    return execute(request);
  }

  // makes a POST request to url with form parameters and returns body as a string
  public static String post(String url, JSONObject content) throws ClientProtocolException, IOException {
    HttpPost request = new HttpPost(url);
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json; charset=UTF-8");
    HttpEntity ent = new StringEntity(content.toString());
    request.setEntity(ent);
    return execute(request);
  }

  // makes request and checks response code for 200
  public static String execute(HttpRequestBase request) throws ClientProtocolException, IOException {
    DefaultHttpClient httpClient = new DefaultHttpClient();
    HttpResponse response = httpClient.execute(request);

    HttpEntity entity = response.getEntity();
    String body = EntityUtils.toString(entity);

    if (response.getStatusLine().getStatusCode() != 200) {
      throw new RuntimeException("Expected 200 but got " + response.getStatusLine().getStatusCode() + ", with body " + body);
    }

    return body;
  }

}

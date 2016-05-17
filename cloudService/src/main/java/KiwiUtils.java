package kiwishare;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class KiwiUtils {

  //Singleton to save parameters
  private static volatile KiwiUtils _instance = null;
  private String _gpgKey = null;
  private String _gpgSecret = null;
  private String _token;

  /**
  * Init GnuPG parameters.
  **/
  private KiwiUtils() {
    JSONObject obj = new JSONObject(KiwiUtils.readFile("gpg.config"));
    _gpgKey = obj.getString("gpg_key");
    _gpgSecret = obj.getString("gpg_pass");
  }

  public static KiwiUtils getInstance() {
    if (_instance == null) {
      synchronized (KiwiUtils.class) {
        _instance =  new KiwiUtils();
      }
    }
    return _instance;
  }

  public String getGpgKey() { return _gpgKey; }
  public String getGpgSecret() { return _gpgSecret; }

  /**
  * Get the content of a file.
  * @param path the path of the file
  * @return the content of the file
  **/
  public static String readFile(final String path) {
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
    } catch (Exception e) {
      return "";
    }
  }

  /**
  * makes a GET request to url and returns body as a string.
  * @throws IOException IOException
  * @param url the url
  * @return the result of the execution of the GET request
  **/
  public static String get(final String url)
  throws IOException {
    return execute(new HttpGet(url));
  }

  /**
  * makes a DELETE request to url and returns body as a string.
  * @throws IOException IOException
  * @param url the url
  * @return the result of the execution of the DELETE request
  **/
  public static String delete(final String url)
  throws IOException {
    return execute(new HttpDelete(url));
  }

  /**
  * makes a PUT request to url with a body and returns body as a string.
  * @throws IOException IOException
  * @param url the url
  * @param content the json object for the body
  * @return the result of the execution of the PUT request
  **/
  public static String put(final String url,
  final JSONObject content)
  throws IOException {
    HttpPut request = new HttpPut(url);
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json; charset=UTF-8");
    HttpEntity ent = new StringEntity(content.toString());
    request.setEntity(ent);
    return execute(request);
  }

  /**
  * makes a POST request to url with form parameters.
  * @throws IOException IOException
  * @param url the url
  * @param formParameters the headers
  * @return the result of the execution of the POST request
  **/
  public static String post(final String url,
  final Map<String, String> formParameters)
  throws IOException {
    HttpPost request = new HttpPost(url);
    List <NameValuePair> nvps = new ArrayList <NameValuePair>();

    for (String key : formParameters.keySet()) {
      nvps.add(new BasicNameValuePair(key, formParameters.get(key)));
    }

    request.setEntity(new UrlEncodedFormEntity(nvps));

    return execute(request);
  }

  /**
  * makes a POST request to url with form parameters and body.
  * @throws IOException IOException
  * @param url the url
  * @param formParameters - the headers
  * @param body - the body of the request
  * @return the result of the execution of the POST request
  **/
  public static String post(final String url,
  final Map<String, String> formParameters,
  final String body)
  throws IOException {
    HttpPost request = new HttpPost(url);

    for (String key : formParameters.keySet()) {
      request.setHeader(key, formParameters.get(key));
    }

    HttpEntity ent = new StringEntity(body);
    request.setEntity(ent);

    return execute(request);
  }

  /**
  * makes a POST request with a file in the body.
  * @throws IOException IOException
  * @param url the url
  * @param fileDesc - the description of the file
  * @param body - the body of the request
  * @return the result of the execution of the POST request
  **/
  public static String postMultipart(final String url,
  final JSONObject fileDesc,
  final InputStream body)
  throws IOException {
    HttpPost request = new HttpPost(url);

    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addTextBody("file", fileDesc.toString(),
    ContentType.APPLICATION_JSON);
    builder.addBinaryBody("file", body);
    HttpEntity multipart = builder.build();

    request.setEntity(multipart);

    return execute(request);
  }

  /**
  * makes a POST request to url with form parameters.
  * @throws IOException IOException
  * @param url the url
  * @param content - the body of the request
  * @return the result of the execution of the POST request
  **/
  public static String post(final String url, final JSONObject content)
  throws IOException {
    HttpPost request = new HttpPost(url);
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json; charset=UTF-8");
    HttpEntity ent = new StringEntity(content.toString());
    request.setEntity(ent);
    return execute(request);
  }

  /**
  * makes request and checks response code for KiwiUtils.getOkStatus().
  * @throws IOException IOException
  * @param request - the request to execute
  * @return the result of the execution of the POST request
  **/
  public static String execute(final HttpRequestBase request)
  throws IOException {
    DefaultHttpClient httpClient = new DefaultHttpClient();
    HttpResponse response = httpClient.execute(request);

    HttpEntity entity = response.getEntity();
    String body = EntityUtils.toString(entity);

    if (response.getStatusLine().getStatusCode() != KiwiUtils.getOkStatus()) {
      throw new RuntimeException(body);
    }

    return body;
  }

  /**
  * @return KiwiUtils.getOkStatus() for Ok
  **/
  public static int getOkStatus() { return 200; }

  /**
  * Encode in HmacSHA256.
  * @param key the key for encoding
  * @param data the data to encode
  * @return the string encoded
  **/
  public static String HmacSHA256(String key, String data) {
    String result = "";
    try {
      Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
      SecretKeySpec secret_key = new SecretKeySpec(
      key.getBytes("UTF-8"), "HmacSHA256");
      sha256_HMAC.init(secret_key);
      result = Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    } catch (Exception e) {
      return "err: " + e.getMessage();
    }
    return result;
  }

}

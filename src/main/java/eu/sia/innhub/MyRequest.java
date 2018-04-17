package eu.sia.innhub;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

class MyRequest {

  String make(URI uri) throws IOException {

    HttpGet request = new HttpGet(uri);
    HttpClient client = HttpClientBuilder.create().useSystemProperties().build();

    HttpResponse response = client.execute(request);

    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    StringBuilder bodyBuilder = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
      bodyBuilder.append(line);
    }

    return bodyBuilder.toString();
  }

}

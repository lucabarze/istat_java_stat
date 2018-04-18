package eu.sia.innhub;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

class MyRequest {

  String make(URI uri) throws IOException {
    final Config conf = ConfigFactory.load().getConfig("proxy");


    HttpGet request = new HttpGet(uri);
    HttpClientBuilder clientBuilder =
      HttpClientBuilder
        .create();

    if (conf.getBoolean("useProxy")) {

      clientBuilder
        .setProxy(new HttpHost(conf.getString("address"), conf.getInt(" port"), conf.getString("protocol")));

      if (conf.getBoolean("useAuth")) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
          new AuthScope(conf.getString(" address"), conf.getInt(" port")),
          new NTCredentials(conf.getString("user"), conf.getString("password"), "", conf.getString("domain"))
        );

        clientBuilder
          .setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy())
          .setDefaultCredentialsProvider(credsProvider);
      }

    }


    HttpResponse response = clientBuilder.build().execute(request);

    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    StringBuilder bodyBuilder = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
      bodyBuilder.append(line);
    }

    return bodyBuilder.toString();
  }

}

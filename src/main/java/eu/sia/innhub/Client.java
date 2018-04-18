package eu.sia.innhub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import no.ssb.jsonstat.JsonStatModule;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;


public class Client {

  public static void main(String[] args) {

    final Config conf = createConfig(args);

    try {

      MyRequest request = new MyRequest();
      String body = request.make(createUri(conf));

      ObjectMapper mapper = createMapper();
      Map.Entry<String, JsonNode> dataNode = mapper.readTree(body).fields().next();

      DatasetManipulator datasetManipulator = new DatasetManipulator(mapper);

      DatabaseConnector databaseConnector = new DatabaseConnector();
      databaseConnector.writeIstatEntries(datasetManipulator.getIstatEntries(dataNode), conf.getString("table"));

    } catch (URISyntaxException | IOException | SQLException | ParseException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    System.exit(0);

  }

  private static ObjectMapper createMapper() {
    return new ObjectMapper()
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule())
      .registerModule(new GuavaModule())
      .registerModule(new JsonStatModule());
  }

  private static Config createConfig(String[] args) {
    Config conf = ConfigFactory.load();

    if (conf.hasPath(args[0])) {
      conf = conf.getConfig(args[0]);
    } else {
      if (args.length != 3) {
        System.out.println("Unknown configuration");
        System.exit(1);
      }
      Map<String, String> importedConf = new HashMap<>();
      importedConf.put("dataset", args[0]);
      importedConf.put("dim", args[1]);
      importedConf.put("table", args[2]);
      conf = ConfigFactory.parseMap(importedConf);
    }

    return conf;
  }

  private static URI createUri (Config conf) throws URISyntaxException {
      return new URIBuilder("http://apistat.istat.it/")
        .addParameter("q", "getdatajson")
        .addParameter("dataset", conf.getString("dataset"))
        .addParameter("dim", conf.getString("dim"))
        .build();
  }

}

package eu.sia.innhub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import no.ssb.jsonstat.v2.Dataset;
import no.ssb.jsonstat.v2.DatasetBuildable;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;


class DatasetManipulator {

  private ObjectMapper mapper;
  DatasetManipulator (ObjectMapper mapper) {
    this.mapper = mapper;
  }

  Stream<IstatEntry> getIstatEntries(Map.Entry<String, JsonNode> dataNode) throws ParseException, IOException {
    return Streams.zip(this.getDates(dataNode).stream(), this.getValues(dataNode).stream(), IstatEntry::new);
  }

  private List<Number> getValues(Map.Entry<String, JsonNode> dataNode)
    throws IOException {

    // null values are discarded from JSONStat mapper
    Dataset jsonStat = mapper.readValue(
      dataNode.getValue().toString().replace("null", "-1"),
      DatasetBuildable.class
    ).build();

    // table of size (jsonStat.getSize().get(0)-1)*jsonStat.getSize().get(1) -- take the last line
    return new ArrayList<>(jsonStat.getValue().values()).subList(
      (jsonStat.getSize().get(0) - 1) * jsonStat.getSize().get(1),
      jsonStat.getValue().values().size()
    );

  }

  private List<LocalDate> getDates(Map.Entry<String, JsonNode> dataNode) throws ParseException {
    DateFormat df = new SimpleDateFormat("yyyyMM", Locale.ITALIAN);

    JsonNode jsonLabels = dataNode.getValue()
      .findPath("dimension")
      .findPath("IDTIME")
      .findPath("category")
      .findPath("label");

    List<LocalDate> dates = new ArrayList<>();
    // from quarters to months
    for (JsonNode jsonLabel : jsonLabels) {
      String[] jsonTexts = jsonLabel.asText().split("-");
      int quarter = Integer.parseInt(jsonTexts[0].substring(1));
      String year = jsonTexts[1];
      // last day of quarter
      LocalDate date = df.parse(year + String.format("%02d", quarter * 3 + 1))
        .toInstant().atZone(TimeZone.getTimeZone("Europe/Rome").toZoneId()).toLocalDate()
        .minusDays(1L);
      dates.add(date);
    }

    return dates;

  }
}

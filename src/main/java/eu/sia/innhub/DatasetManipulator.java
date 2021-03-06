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

    // last slice of the hypercube
    return new ArrayList<>(jsonStat.getValue().values()).subList(
      jsonStat.getValue().values().size() - jsonStat.getSize().get(0),
      jsonStat.getValue().values().size()
    );

  }

  private List<LocalDate> getDates(Map.Entry<String, JsonNode> dataNode) throws ParseException {
    DateFormat df = new SimpleDateFormat("yyyyMM", Locale.ITALIAN);
    SimpleDateFormat mf = new SimpleDateFormat("MMM", Locale.ITALIAN);

    JsonNode jsonLabels = dataNode.getValue()
      .findPath("dimension")
      .findPath("IDTIME")
      .findPath("category")
      .findPath("label");

    List<LocalDate> dates = new ArrayList<>();
    for (JsonNode jsonLabel : jsonLabels) {
      String[] jsonTexts = jsonLabel.asText().split("-");
      int month;
      // try from quarters to months
      try {
        int quarter = Integer.parseInt(jsonTexts[0].substring(1));
        month = quarter*3+1;
      } catch (NumberFormatException e) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(mf.parse(jsonTexts[0]));
        month = cal.get(Calendar.MONTH) + 1 + 1;
      }
      String year = jsonTexts[1];
      // last day of quarter
      LocalDate date = df.parse(year + String.format("%02d", month))
        .toInstant().atZone(TimeZone.getTimeZone("Europe/Rome").toZoneId()).toLocalDate()
        .minusDays(1L);
      dates.add(date);
    }

    return dates;

  }
}

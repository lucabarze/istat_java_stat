package eu.sia.innhub;

import java.time.LocalDate;

public class IstatEntry {

  final LocalDate date;
  final Number value;

  IstatEntry(LocalDate date, Number value) {
    this.date = date;
    this.value = value;
  }

  public String toString() {
    return "(" + date + ", " + value + ")";
  }

}

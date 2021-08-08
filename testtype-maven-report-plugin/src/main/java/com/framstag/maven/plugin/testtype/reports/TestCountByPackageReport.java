package com.framstag.maven.plugin.testtype.reports;

import com.framstag.maven.plugin.testtype.Test;
import com.framstag.maven.plugin.testtype.TestType;
import org.apache.maven.doxia.sink.Sink;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestCountByPackageReport {

  public void execute(Sink mainSink, List<Test> tests) {
    List<String> packageList = tests.stream().map(Test::getPackageName).distinct().sorted().collect(Collectors.toList());

    mainSink.table();

    // Header

    mainSink.tableRow();
    mainSink.tableHeaderCell();
    mainSink.text("Package");
    mainSink.tableHeaderCell_();
    mainSink.tableHeaderCell();
    mainSink.text("Tests");
    mainSink.tableHeaderCell_();
    mainSink.tableRow_();

    // Data

    for (String p : packageList) {
      int count = 0;

      for (Test t : tests) {
        if (t.getPackageName().equals(p)) {
          count++;
        }
      }

      mainSink.tableRow();
      mainSink.tableCell();
      mainSink.text(p);
      mainSink.tableCell_();

      mainSink.tableCell();
      mainSink.text(String.valueOf(count));
      mainSink.tableCell_();
      mainSink.tableRow_();
    }

    mainSink.tableRow();
    mainSink.tableCell();
    mainSink.text("\u2211");
    mainSink.tableCell_();

    mainSink.tableCell();
    mainSink.text(String.valueOf(tests.size()));
    mainSink.tableCell_();
    mainSink.tableRow_();

    mainSink.table_();
  }
}

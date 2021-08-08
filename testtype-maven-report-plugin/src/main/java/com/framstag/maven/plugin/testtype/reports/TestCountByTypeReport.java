package com.framstag.maven.plugin.testtype.reports;

import com.framstag.maven.plugin.testtype.Test;
import com.framstag.maven.plugin.testtype.TestType;
import org.apache.maven.doxia.sink.Sink;

import java.util.List;

public class TestCountByTypeReport {

  public void execute(Sink mainSink, List<Test> tests) {
    mainSink.table();

    // Header

    mainSink.tableRow();
    mainSink.tableHeaderCell();
    mainSink.text("Test Type");
    mainSink.tableHeaderCell_();

    mainSink.tableHeaderCell();
    mainSink.text("Count");
    mainSink.tableHeaderCell_();
    mainSink.tableRow_();

    // Data

    for (TestType type : TestType.values()) {
      int count = 0;

      for (Test test : tests) {
        if (test.getType().equals(type)) {
          count++;
        }
      }

      mainSink.tableRow();
      mainSink.tableCell();
      mainSink.text(type.getLabel());
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

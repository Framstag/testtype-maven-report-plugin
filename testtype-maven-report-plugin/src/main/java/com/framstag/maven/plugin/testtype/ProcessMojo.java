package com.framstag.maven.plugin.testtype;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.doxia.sink.Sink;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Scan a module for tests
 */
@Mojo(
  name = "process",
  defaultPhase = LifecyclePhase.SITE,
  requiresDependencyResolution = ResolutionScope.TEST,
  threadSafe = true)
public class ProcessMojo extends AbstractMavenReport {

  @Override
  protected void executeReport(Locale locale) throws MavenReportException {
    Log log = getLog();
    List<String> jarDependencies = new LinkedList<>();

    try {
      for (String path : project.getTestClasspathElements()) {
        if (path.endsWith(".jar")) {
          getLog().debug("jar dependency: " + path);
          jarDependencies.add(path);
        } else {
          log.debug("Other classpath element: " + path);
        }
      }
    } catch (DependencyResolutionRequiredException e) {
      e.printStackTrace();
    }

    AnnotationParser parser = new AnnotationParser();

    List<Clazz> allClasses = new LinkedList<>();

    for (String root : project.getTestCompileSourceRoots()) {
      try {
        getLog().debug("Parsing directory " + root + "...");

        allClasses.addAll(parser.parseFiles(getLog(), Paths.get(root), jarDependencies));
      } catch (IllegalStateException | IOException e) {
        getLog().warn(e);
      }
    }

    getLog().debug("Resolving test types...");

    List<Test> tests = new LinkedList<>();
    TestTypeResolver resolver = new TestTypeResolver();

    // Spring Boot + JUnit 5
    resolver.addPattern(new ResolverPattern("org.springframework.boot.test.context.SpringBootTest", "org.junit.jupiter.api.Test", TestType.SPRING_BOOT_JUNIT_5));
    resolver.addPattern(new ResolverPattern("org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest", "org.junit.jupiter.api.Test", TestType.SPRING_BOOT_WEBMVC_JUNIT_5));
    resolver.addPattern(new ResolverPattern("org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest", "org.junit.jupiter.api.Test", TestType.SPRING_BOOT_DATAJPA_JUNIT_5));

    // JUnit 4
    resolver.addPattern(new ResolverPattern(null, "org.junit.Test", TestType.JUNIT_4));

    // JUnit 5
    resolver.addPattern(new ResolverPattern(null, "org.junit.jupiter.api.Test", TestType.JUNIT_5));

    for (Clazz clazz : allClasses) {
      for (Method method : clazz.getMethods()) {
        Optional<Test> test = resolver.resolve(clazz, method);

        test.ifPresent(tests::add);
      }
    }

    Set<String> packages = new HashSet<>();

    for (Test test : tests) {
      packages.add(test.getPackageName());
    }

    List<String> packageList = packages.stream().sorted().collect(Collectors.toList());

    log.info("Generating " + getOutputName() + ".html"
      + " for " + project.getName() + " " + project.getVersion());

    Sink mainSink = getSink();
    if (mainSink == null) {
      throw new MavenReportException("Could not get the Doxia sink");
    }

    // Page title
    mainSink.head();
    mainSink.title();
    mainSink.text("Test Type Usage Report");
    mainSink.title_();
    mainSink.head_();

    mainSink.body();

    // Heading 1
    mainSink.section1();
    mainSink.sectionTitle1();
    mainSink.text("Test Type Usage Report");
    mainSink.sectionTitle1_();

    // Summary: Count for each type

    mainSink.section2();
    mainSink.sectionTitle2();
    mainSink.text("Summary");
    mainSink.sectionTitle2_();

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
      mainSink.tableHeaderCell();
      mainSink.text(type.getLabel());
      mainSink.tableHeaderCell_();

      mainSink.tableCell();
      mainSink.text(String.valueOf(count));
      mainSink.tableCell_();

      mainSink.tableRow_();
    }

    mainSink.table_();

    mainSink.section2_();

    // List of packages

    mainSink.section2();
    mainSink.sectionTitle2();
    mainSink.text("Package List");
    mainSink.sectionTitle2_();

    mainSink.table();

    // Header

    mainSink.tableRow();
    mainSink.tableHeaderCell();
    mainSink.text("Package");
    mainSink.tableHeaderCell_();

    // Data

    for (String p : packageList) {
      mainSink.tableRow();
      mainSink.tableHeaderCell();
      mainSink.text(p);
      mainSink.tableHeaderCell_();
      mainSink.tableRow_();
    }

    mainSink.table_();
    mainSink.section2_();

    // Section per Package

    for (String p : packageList) {
      mainSink.section2();
      mainSink.sectionTitle2();
      mainSink.text(p);
      mainSink.sectionTitle2_();
      mainSink.section2_();
    }

    // List of tests

    mainSink.section2();
    mainSink.sectionTitle2();
    mainSink.text("Test List");
    mainSink.sectionTitle2_();

    mainSink.table();

    mainSink.tableRow();
    mainSink.tableHeaderCell();
    mainSink.text("Class");
    mainSink.tableHeaderCell_();

    mainSink.tableHeaderCell();
    mainSink.text("Method");
    mainSink.tableHeaderCell_();

    mainSink.tableHeaderCell();
    mainSink.text("Type");
    mainSink.tableHeaderCell_();
    mainSink.tableRow_();


    for (Test test : tests) {
      mainSink.tableRow();

      mainSink.tableCell();
      mainSink.text(test.getFullName());
      mainSink.tableCell_();

      mainSink.tableCell();
      mainSink.text(test.getMethodName());
      mainSink.tableCell_();

      mainSink.tableCell();
      mainSink.text(test.getType().getLabel());
      mainSink.tableCell_();

      mainSink.tableRow_();
    }

    mainSink.table_();
    mainSink.section2_();

    // Close
    mainSink.section1_();
    mainSink.body_();
  }

  @Override
  public String getOutputName() {
    return "test-type-usage";
  }

  @Override
  public String getName(Locale locale) {
    return "Test Type Report";
  }

  @Override
  public String getDescription(Locale locale) {
    return "List of tests together with their type";
  }
}

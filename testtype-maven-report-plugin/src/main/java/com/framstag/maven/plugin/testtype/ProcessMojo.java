package com.framstag.maven.plugin.testtype;

import com.framstag.maven.plugin.testtype.reports.TestCountByPackageReport;
import com.framstag.maven.plugin.testtype.reports.TestCountByTypeReport;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.doxia.sink.Sink;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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

    List<Clazz> allClasses = parser.parseDirectories(getLog(),project.getTestCompileSourceRoots(),jarDependencies);

    getLog().debug("Resolving test types...");

    TestTypeResolver resolver = new TestTypeResolver();

    // Spring Boot + JUnit 5
    resolver.addPattern(new ResolverPattern("org.springframework.boot.test.context.SpringBootTest", "org.junit.jupiter.api.Test", TestType.SPRING_BOOT_JUNIT_5));
    resolver.addPattern(new ResolverPattern("org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest", "org.junit.jupiter.api.Test", TestType.SPRING_BOOT_WEBMVC_JUNIT_5));
    resolver.addPattern(new ResolverPattern("org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest", "org.junit.jupiter.api.Test", TestType.SPRING_BOOT_DATAJPA_JUNIT_5));

    // JUnit 4
    resolver.addPattern(new ResolverPattern(null, "org.junit.Test", TestType.JUNIT_4));

    // JUnit 5
    resolver.addPattern(new ResolverPattern(null, "org.junit.jupiter.api.Test", TestType.JUNIT_5));
    resolver.addPattern(new ResolverPattern(null, "org.junit.jupiter.params.ParameterizedTest", TestType.JUNIT_5));

    int classes = 0;
    int methods = 0;
    List<Test> tests = new LinkedList<>();

    for (Clazz clazz : allClasses) {
      classes++;

      for (Method method : clazz.getMethods()) {
        methods++;

        resolver
          .resolve(clazz, method)
          .ifPresent(tests::add);
      }
    }

    log.info("Parser detected " + classes + " classes with " + methods + " methods, resolved to " + tests.size() + " tests");

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

    TestCountByTypeReport testCountByTypeReport = new TestCountByTypeReport();
    testCountByTypeReport.execute(mainSink, "Test Type count", tests);

    mainSink.section2_();

    // List of packages

    mainSink.section2();
    mainSink.sectionTitle2();
    mainSink.text("Package List");
    mainSink.sectionTitle2_();

    TestCountByPackageReport testCountByPackageReport = new TestCountByPackageReport();
    testCountByPackageReport.execute(mainSink, tests);

    mainSink.section2_();

    PackageListCompleter completer = new PackageListCompleter();

    List<String> packages = completer.convert(tests
      .stream()
      .map(Test::getPackageName)
      .distinct().collect(Collectors.toList()))
      .stream()
      .sorted()
      .collect(Collectors.toList());

    // Section per Package

    for (String p : packages) {
      mainSink.section2();
      mainSink.sectionTitle2();
      mainSink.text(p);
      mainSink.sectionTitle2_();

      List<Test> packageTests = tests.stream().filter( t -> t.isInPackage(p)).collect(Collectors.toList());
      List<Test> transitivePackageTests = tests.stream().filter( t -> t.isInPackageOrSubPackage(p)).collect(Collectors.toList());
      List<String> packageTestClasses = packageTests.stream().map(Test::getClassName).distinct().sorted().collect(Collectors.toList());

      testCountByTypeReport.execute(mainSink, "Test types count", packageTests);
      testCountByTypeReport.execute(mainSink, "Transitive test type count", transitivePackageTests);

      mainSink.table();

      // Header

      mainSink.tableRow();
      mainSink.tableHeaderCell();
      mainSink.text("Test class");
      mainSink.tableHeaderCell_();
      mainSink.tableHeaderCell();
      mainSink.text("Tests");
      mainSink.tableHeaderCell_();
      mainSink.tableRow_();

      for (String testClass : packageTestClasses) {
        mainSink.tableRow();
        mainSink.tableCell();
        mainSink.text(testClass);
        mainSink.tableCell_();

        mainSink.tableCell();
        mainSink.text(String.valueOf(packageTests.stream().filter(t -> t.getClassName().equals(testClass)).count()));
        mainSink.tableCell_();
        mainSink.tableRow_();
      }

      mainSink.section2_();
      mainSink.table_();
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

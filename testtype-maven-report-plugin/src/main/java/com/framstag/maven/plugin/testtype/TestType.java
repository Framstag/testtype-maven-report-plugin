package com.framstag.maven.plugin.testtype;

public enum TestType {
  JUNIT_4("JUnit 4"),
  JUNIT_5("JUnit 5"),

  SPRING_BOOT_JUNIT_5 ("Spring Boot and JUnit 5"),
  SPRING_BOOT_WEBMVC_JUNIT_5("Spring Boot WebMVC and JUnit 5"),
  SPRING_BOOT_DATAJPA_JUNIT_5("Spring Boot DataJPA and JUnit 5");

  private final String label;

  TestType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}

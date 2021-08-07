package com.framstag.maven.plugin.testtype;

public class ResolverPattern {
  private final String classAnnotationName;
  private final String methodAnnotationName;
  private final TestType type;

  public ResolverPattern(String classAnnotationName,
                         String methodAnnotationName,
                         TestType type) {
    this.classAnnotationName = classAnnotationName;
    this.methodAnnotationName = methodAnnotationName;
    this.type = type;
  }

  public String getClassAnnotationName() {
    return classAnnotationName;
  }

  public String getMethodAnnotationName() {
    return methodAnnotationName;
  }

  public TestType getType() {
    return type;
  }
}

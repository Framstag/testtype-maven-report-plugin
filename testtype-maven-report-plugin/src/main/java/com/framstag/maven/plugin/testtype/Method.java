package com.framstag.maven.plugin.testtype;

import java.util.HashMap;

public class Method {
  private final String name;
  private final HashMap<String,Annotation> annotations;

  public Method(String name) {
    this.name = name;
    this.annotations = new HashMap<>();
  }

  public String getName() {
    return name;
  }

  public void addAnnotation(Annotation annotation) {
    annotations.put(annotation.getName(),annotation);
  }

  public boolean hasAnnotationWithName(String methodAnnotationName) {
    return annotations.containsKey(methodAnnotationName);
  }
}

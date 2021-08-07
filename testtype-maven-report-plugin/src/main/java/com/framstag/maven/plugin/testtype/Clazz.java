package com.framstag.maven.plugin.testtype;

import java.util.*;

public class Clazz {
  private final String packageName;
  private final String className;
  private final Map<String,Annotation> annotations;
  private final List<Method> methods;

  public Clazz(String packageName, String className) {
    this.packageName = packageName;
    this.className = className;
    this.annotations = new HashMap<>();
    this.methods = new LinkedList<>();
  }

  public String getFullName() {
    if (packageName != null && !packageName.isEmpty()) {
      return packageName + "." + className;
    }
    else {
      return className;
    }
  }

  public String getPackageName() {
    return packageName;
  }

  public String getClassName() {
    return className;
  }

  public void addAnnotation(Annotation annotation) {
    annotations.put(annotation.getName(),annotation);
  }

  public boolean hasAnnotationWithName(String name) {
    return annotations.containsKey(name);
  }

  public void addMethod(Method method) {
    methods.add(method);
  }

  public Method[] getMethods() {
    return methods.toArray(new Method[0]);
  }
}

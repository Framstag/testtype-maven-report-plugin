package com.framstag.maven.plugin.testtype;

public class Test {
  private final String packageName;
  private final String className;
  private final String methodName;
  private final TestType type;

  public Test(String packageName,
              String className,
              String methodName,
              TestType type) {
    this.packageName = packageName;
    this.className = className;
    this.methodName = methodName;
    this.type = type;
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

  public String getMethodName() {
    return methodName;
  }

  public TestType getType() {
    return type;
  }
}

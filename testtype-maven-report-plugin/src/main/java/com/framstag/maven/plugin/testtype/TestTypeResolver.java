package com.framstag.maven.plugin.testtype;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TestTypeResolver {
  private final List<ResolverPattern> patterns;

  public TestTypeResolver() {
    patterns = new LinkedList<>();
  }

  public Optional<Test> resolve(Clazz clazz, Method method) {
    for (ResolverPattern pattern : patterns) {
      if (pattern.getClassAnnotationName() != null) {
        if (!clazz.hasAnnotationWithName(pattern.getClassAnnotationName())) {
          continue;
        }
      }

      if (pattern.getMethodAnnotationName() != null) {
        if (!method.hasAnnotationWithName(pattern.getMethodAnnotationName())) {
          continue;
        }
      }

      return Optional.of(new Test(clazz.getPackageName(),
        clazz.getClassName(),
        method.getName(),
        pattern.getType()));
    }

    return Optional.empty();
  }

  public void addPattern(ResolverPattern pattern) {
    patterns.add(pattern);
  }
}

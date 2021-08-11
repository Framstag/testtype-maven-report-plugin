package com.framstag.maven.plugin.testtype;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PackageNameCompleter {

  public List<String> convert(List<String> packages) {
    Set<String> packageSet = new HashSet<>();

    for (String p : packages) {
      packageSet.add(p);

      int pos = p.lastIndexOf('.');
      while (pos >= 0) {
        p = p.substring(0, pos);

        packageSet.add(p);
        pos = p.lastIndexOf('.');
      }
    }

    return new ArrayList<>(packageSet);
  }
}

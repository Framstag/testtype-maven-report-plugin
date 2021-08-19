package com.framstag.maven.plugin.testtype;

import java.util.*;

/**
 * Completes the given package list with missing intermediate packages.
 */
public class PackageListCompleter {

  private void addValue(Map<Long, Set<String>> workingSet, Long key, String value) {
    Set<String> list = workingSet.get(key);
    if (list == null) {
      list = new HashSet<>();
      list.add(value);
      workingSet.put(key, list);
    } else {
      list.add(value);
    }
  }

  private Map<Long, Set<String>> getWorkingSet(Collection<String> packages) {
    Map<Long, Set<String>> workingSet = new HashMap<>();

    for (String p : packages) {
      long count = p.chars().filter(ch -> ch == '.').count();
      Long countValue = Long.valueOf(count);

      addValue(workingSet,countValue,p);
    }

    return workingSet;
  }

  private Long getMaxKey(Map<Long, Set<String>> workingSet) {
    long max = -1;
    for (Long count : workingSet.keySet()) {
      if (count > max) {
        max = count;
      }
    }

    return max;
  }

  /**
   * Tries to complete the list of package names with intermediate packages, that are
   * not explicitly defined in the code but still are important for logical
   * structuring.
   *
   * @param packages Unsorted list of packages which have classes
   * @return list of packages (unsorted)
   */
  public List<String> convert(List<String> packages) {
    Set<String> packageSet = new HashSet<>(packages);
    Map<Long, Set<String>> workingSet = getWorkingSet(packages);

    Long max = getMaxKey(workingSet);

    while (max > 0) {
      if (workingSet.size() == 0) {
        break;
      }

      if (workingSet.size() == 1 && workingSet.get(max).size() == 1) {
        break;
      }

      Set<String> longPackages = workingSet.get(max);

      for (String p : longPackages) {
        String smallerPackage = p.substring(0,p.lastIndexOf('.'));

        packageSet.add(smallerPackage);
        addValue(workingSet,max-1, smallerPackage);
      }

      workingSet.remove(max);
      max = max -1;
    }

    return new ArrayList<>(packageSet);
  }
}

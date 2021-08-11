package com.framstag.maven.plugin.testtype;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PackageNameCompleterTest {

  @Test
  public void testEmptyList() {
    List<String> packages = Collections.emptyList();

    PackageNameCompleter completer = new PackageNameCompleter();

    List<String> completedPackages = completer.convert(packages);

    Assert.assertEquals(0, completedPackages.size());
  }

  @Test
  public void testOnePackage() {
    List<String> packages = Arrays.asList("com.framstag");

    PackageNameCompleter completer = new PackageNameCompleter();

    List<String> completedPackages = completer.convert(packages);

    completedPackages.sort(String::compareTo);

    Assert.assertEquals(Arrays.asList("com","com.framstag"), completedPackages);
  }

  @Test
  public void testTowSubPackages() {
    List<String> packages = Arrays.asList("com.framstag.aaa","com.framstag.bbb");

    PackageNameCompleter completer = new PackageNameCompleter();

    List<String> completedPackages = completer.convert(packages);

    completedPackages.sort(String::compareTo);

    Assert.assertEquals(Arrays.asList("com","com.framstag","com.framstag.aaa","com.framstag.bbb"), completedPackages);
  }
}

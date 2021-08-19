package com.framstag.maven.plugin.testtype;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PackageListCompleterTest {

  @Test
  public void testCompleteEmptyList() {
    List<String> packages = Collections.emptyList();

    PackageListCompleter completer = new PackageListCompleter();

    List<String> completedPackages = completer.convert(packages);

    completedPackages.sort(String::compareTo);

    Assert.assertEquals(0, completedPackages.size());
  }

  @Test
  public void testCompleteOnePackage() {
    List<String> packages = Arrays.asList("com.framstag");

    PackageListCompleter completer = new PackageListCompleter();

    List<String> completedPackages = completer.convert(packages);

    completedPackages.sort(String::compareTo);

    Assert.assertEquals(Arrays.asList("com.framstag"), completedPackages);
  }

  @Test
  public void testCompleteTwoParallelSubPackages() {
    List<String> packages = Arrays.asList("com.framstag.aaa","com.framstag.bbb");

    PackageListCompleter completer = new PackageListCompleter();

    List<String> completedPackages = completer.convert(packages);

    completedPackages.sort(String::compareTo);

    Assert.assertEquals(Arrays.asList("com.framstag","com.framstag.aaa","com.framstag.bbb"), completedPackages);

  }

  @Test
  public void testCompleteParentWithTwoParallelSubPackages() {
    List<String> packages = Arrays.asList("com.framstag","com.framstag.aaa","com.framstag.bbb");

    PackageListCompleter completer = new PackageListCompleter();

    List<String> completedPackages = completer.convert(packages);

    completedPackages.sort(String::compareTo);

    Assert.assertEquals(Arrays.asList("com.framstag","com.framstag.aaa","com.framstag.bbb"), completedPackages);
  }

  @Test
  public void testCompleteParentWithOneSubPackages() {
    List<String> packages = Arrays.asList("com.framstag","com.framstag.aaa");

    PackageListCompleter completer = new PackageListCompleter();

    List<String> completedPackages = completer.convert(packages);

    completedPackages.sort(String::compareTo);

    Assert.assertEquals(Arrays.asList("com.framstag","com.framstag.aaa"), completedPackages);
  }

  @Test
  public void testCompleteTwoDisjunctPackages() {
    List<String> packages = Arrays.asList("com.aaa.bbb","com.bbb.ccc");

    PackageListCompleter completer = new PackageListCompleter();

    List<String> completedPackages = completer.convert(packages);

    completedPackages.sort(String::compareTo);

    Assert.assertEquals(Arrays.asList("com","com.aaa","com.aaa.bbb","com.bbb","com.bbb.ccc"), completedPackages);
  }
}

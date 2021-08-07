# testtype-maven-report-plugin 

## About

This plugin tries to detect various test frameworks and test types
by analysing the annotation of the test classes
(using [javaparser](https://javaparser.org/)) and
generating a report with some statistic information.

## Goal

Goal is to get some (architecture, quality-assurance) overview
over the project regarding the used test strategies.

## Detected Frameworks

* JUnit 4
* JUnit 5
* Spring
  * SpringBootTest
  * WebMvcTest
  * DataJpaTest

## Current state

This is a proof of concept. The report generated is already OK but not
complete. The code needs some refactoring (move more code out of the
Mojo class).

## TODOs

* Get clear how to signal combinations of frameworks
* Add support for more test frameworks
* Add links to the report
* Add some graphics to the report
* Handle multi-module projects (possibly we need to store intermediate
* data and split analyse and aggregation phases?)
* Get rid of possible warnings if annotations of the JDK itself are used. 
* Add Unit- and Maven-tests
* Understand which versions of dependencies for Maven plugin development
  we should use (and not use).
* Release Process
  * Get a release working using GitHUb Actions
  * Publish release to Maven Central

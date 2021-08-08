package com.framstag.maven.plugin.testtype;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedAnnotationDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class AnnotationParser {
  private List<Clazz> parseUnit(Log log, CompilationUnit unit) {
    List<Clazz> classes = new LinkedList<>();

    for (TypeDeclaration<?> type : unit.getTypes()) {
      if (!(type instanceof ClassOrInterfaceDeclaration)) {
        continue;
      }

      ClassOrInterfaceDeclaration c = type.asClassOrInterfaceDeclaration();
      ResolvedReferenceTypeDeclaration rc = c.resolve();

      if (rc.isAnnotation()) {
        continue;
      }

      if (rc.isAnonymousClass()) {
        continue;
      }

      String packageName = rc.getPackageName();
      String className = rc.getClassName();
      Clazz clazz = new Clazz(packageName,className);

      classes.add(clazz);

      for (AnnotationExpr annotation : c.getAnnotations()) {
        try {
          ResolvedAnnotationDeclaration ra = annotation.resolve();

          clazz.addAnnotation(new Annotation(ra.getQualifiedName()));
        } catch (UnsolvedSymbolException e) {
          log.warn("Cannot resolve @" + annotation.getNameAsString() + " for " + clazz.getFullName());
        }
      }

      for (MethodDeclaration m : c.getMethods()) {
        ResolvedMethodDeclaration rm = m.resolve();
        String methodName = rm.getName();
        Method method = new Method(methodName);

        clazz.addMethod(method);

        for (AnnotationExpr annotation : m.getAnnotations()) {
          try {
            ResolvedAnnotationDeclaration ra = annotation.resolve();

            method.addAnnotation(new Annotation(ra.getQualifiedName()));

            //log.info(clazz.getName() +"." + method.getName() + " " + ra.getQualifiedName());
          } catch (UnsolvedSymbolException e) {
            log.warn("Cannot resolve @" + annotation.getNameAsString() + " for " + clazz.getFullName() + "/" + method.getName());
          }
        }
      }
    }

    return classes;
  }

  public List<Clazz> parseFiles(Log log, Path path, List<String> jarDependencies) throws IOException {
    List<Clazz> classes = new LinkedList<>();

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    for (String file : jarDependencies) {
      combinedTypeSolver.add(new JarTypeSolver(file));
    }

    combinedTypeSolver.add(new ReflectionTypeSolver());
    combinedTypeSolver.add(new JavaParserTypeSolver(path));

    // Configure JavaParser to use type resolution
    JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

    SourceRoot sourceRoot = new SourceRoot(path);
    sourceRoot.getParserConfiguration()
      .setSymbolResolver(symbolSolver)
      .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_11);
    List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse();

    for (ParseResult<CompilationUnit> parseResult : parseResults) {
      if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
        CompilationUnit unit = parseResult.getResult().get();
        unit.getStorage().ifPresent(storage -> log.debug("Parsed unit: " + storage.getPath().toString()));

        classes.addAll(parseUnit(log,parseResult.getResult().get()));
      }
      else {
        parseResult.getResult().flatMap(CompilationUnit::getStorage).ifPresent(storage -> log.error("Failed unit: " + storage.getPath().toString()));

        for (Problem problem: parseResult.getProblems()) {
          log.error(" * " + problem.getVerboseMessage());
        }
      }
    }

    return classes;
  }
}

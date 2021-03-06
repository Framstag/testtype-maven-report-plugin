package com.framstag.maven.plugin.testtype;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;

import org.junit.Rule;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

public class MyMojoTest {
  @Rule
  public MojoRule rule = new MojoRule()
  {
    @Override
    protected void before()
    {
    }

    @Override
    protected void after()
    {
    }
  };

  /**
   * @throws Exception if any
   */
  @Test
  public void testSomething() throws Exception {
    File pom = new File("target/test-classes/project-to-test/").getAbsoluteFile();
    assertNotNull(pom);
    assertTrue(pom.exists());

    ProcessMojo mojo = (ProcessMojo) rule.lookupConfiguredMojo(pom,"process");
    assertNotNull(mojo);
    mojo.execute();

    // TODO
  }

  /**
   * Do not need the MojoRule.
   */
  @WithoutMojo
  @Test
  public void testSomethingWhichDoesNotNeedTheMojoAndProbablyShouldBeExtractedIntoANewClassOfItsOwn() {
    assertTrue(true);
  }
}


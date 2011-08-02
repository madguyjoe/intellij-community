package com.intellij.structuralsearch;

import com.intellij.dupLocator.DuplicatesTestCase;
import com.intellij.lang.Language;
import com.intellij.openapi.application.PathManager;
import org.jetbrains.plugins.groovy.GroovyFileType;

/**
 * @author Eugene.Kudelevsky
 */
public class GroovyDuplicatesTest extends DuplicatesTestCase {

  @Override
  protected String getTestDataPath() {
    return PathManager.getHomePath() + "/plugins/structuralsearch/testData/groovy/duplicates/";
  }

  @Override
  protected Language[] getLanguages() {
    return new Language[] {GroovyFileType.GROOVY_LANGUAGE};
  }

  public void test1() throws Exception {
    doTest("grdups1.groovy", false, true, false, 1, 2, "_2", 1);
    doTest("grdups1.groovy", true, true, true, 1, 1, "_0", 1);
    doTest("grdups1.groovy", false, true, true, 1, 1, "_1", 1);
  }

  public void test2() throws Exception {
    doTest("grdups2.groovy", false, false, true, 4, 3, "", 1);
  }

  public void test3() throws Exception {
    doTest("grdups3.groovy", true, true, true, 2, 1, "", 8);
  }

  public void test4() throws Exception {
    // todo: move detection of code blocks to DuplicatesProfile
    doTest("grdups4.groovy", true, true, true, 1, 1, "_0", 8);
    doTest("grdups4.groovy", true, true, false, 1, 1, "_1", 8);
  }

  public void test5() throws Exception {
    doTest("grdups5.groovy", true, true, true, 1, 1, "", 10);
  }
}

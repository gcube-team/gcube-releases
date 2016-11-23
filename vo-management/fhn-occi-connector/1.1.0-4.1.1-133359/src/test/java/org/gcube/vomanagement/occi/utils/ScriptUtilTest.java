package org.gcube.vomanagement.occi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class ScriptUtilTest {

  @Test
  public void testGetScriptFromFile() {
    try {
      String content = "testcontent";
      Writer writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream("/tmp/test.txt"), "utf-8"));
      writer.write(content);
      writer.close();
      String str = ScriptUtil.getScriptFromFile(new File("/tmp/test.txt"));
      assertEquals("message", content, str);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }

  }

}

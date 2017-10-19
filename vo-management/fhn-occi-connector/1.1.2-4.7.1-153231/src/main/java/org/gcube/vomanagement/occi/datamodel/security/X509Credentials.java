package org.gcube.vomanagement.occi.datamodel.security;

import org.gcube.vomanagement.occi.utils.ScriptUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class X509Credentials extends Credentials {

  private String certificateContent;
  private File certificateFile;

  public X509Credentials(String certificate) {
    this.certificateContent = certificate;
  }

  public X509Credentials(File pem) {
    this.certificateFile = pem;
  }

  /**
   * Return the certificate content.
   * @return the content of the certificate.
   */
  public String getCertificateContent() {
    if (certificateContent == null) {
      // read from file
      try {
        this.certificateContent = ScriptUtil
            .getScriptFromFile(this.certificateFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return certificateContent;
  }

  /**
   * Return the path to the certificate. When the certificate is created
   * starting from its content, it's serialized to a file and then a path is
   * returned.
   * 
   * @return the File to the certificate.
   */
  public File getCertificateFile() {
    if (this.certificateFile == null) {
      // create file
      try {
        String fileName = "/tmp/" + UUID.randomUUID().toString() + ".pem";
        PrintWriter out = new PrintWriter(fileName);
        out.print(this.certificateContent);
        out.close();
        this.certificateFile = new File(fileName);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    return this.certificateFile;
  }

}

package org.gcube.vomanagement.occi.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class X509CredentialManager {

  /**
   * The location where proxy certificates are generated.
   */
  public static final String OUTPUT_PATH = "/tmp/";

  /**
   * Generates a certificate proxy starting from the given certificate,
   * protected by password pwd.
   * 
   * @param path
   *          the path to the original certificate
   * @param pwd
   *          the password used to encrypt the certificate
   * @return the path to the proxy certificate
   */
  public static String createProxy(String path, String pwd,String vo) {
    Random random = new Random();
    int number = random.nextInt();
    if (number < 0) {
      number = -number;
    }
    String outputCert = OUTPUT_PATH + "proxy" + number + ".pem";
    try {
      StringBuilder vomsCommand = new StringBuilder();
      vomsCommand.append("echo ");
      vomsCommand.append(pwd);
      vomsCommand
          .append(" | voms-proxy-init --voms " +  vo + " --rfc --noregen -dont_verify_ac -cert ");
      //-dont_verify_ac ok in local
      //-dont_verify_ac ok in d4s infra vms
      vomsCommand.append(path);
      vomsCommand.append(" -out ");
      vomsCommand.append(outputCert);
      vomsCommand.append(" -pwstdin");
      String[] command = { "/bin/sh", "-c", vomsCommand.toString() };
      System.out.println(command[2]);

      ProcessBuilder pb = new ProcessBuilder(command);
      pb.redirectErrorStream(true);
      Process proc = pb.start();
      System.out.println("Process started !");
      String line;
      BufferedReader in = new BufferedReader(new InputStreamReader(
          proc.getInputStream()));
      while ((line = in.readLine()) != null) {
        System.out.println(line);
      }
      proc.destroy();
      System.out.println("Process ended !");
      System.out.println("Proxy Certificate created");

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return outputCert;
  }

}

package org.gcube.vomanagement.occi.utils;

import cz.cesnet.cloud.occi.core.Attribute;
import cz.cesnet.cloud.occi.core.Entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtil {

  public static String extractStringAttribute(Entity entity, String name) {
    String value = entity.getAttributes().get(new Attribute(name));
    return value;
  }

  /**
   * Extract the attribute named 'name' of type Double from the given Entity.
   * 
   * @param entity
   * @param name
   *          the name of the attribute to look for
   * @return the value of the attribute named 'name'
   */
  public static Double extractDoubleAttribute(Entity entity, String name) {
    String value = entity.getAttributes().get(new Attribute(name));
    if (value == null) {
      return null;
    }
    try {
      return Double.parseDouble(value);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Extract the "protocol://host:port" part of the given URI.
   * 
   * @param uri
   *          the uri to parse.
   * @return the 'protocol://host:port' part of the given URI.
   */
  public static String extractHostAndPort(String uri) {
    Pattern pattern = Pattern
        .compile("(http(s)?://[0-9a-z\\.]+(:[0-9]+)?)/(.*)");
    Matcher matcher = pattern.matcher(uri);
    if (matcher.matches()) {
      return matcher.group(1);
    } else {
      return null;
    }
  }

  /**
   * Extract the path part of the given URI.
   * 
   * @param uri
   *          the URI to parse.
   * @return the path part of the given URI.
   */
  public static String extractPath(String uri) {
    Pattern pattern = Pattern
        .compile("(http(s)?://[0-9a-z\\.]+(:[0-9]+)?)/(.*)");
    Matcher matcher = pattern.matcher(uri);
    if (matcher.matches()) {
      return matcher.group(4);
    } else {
      return null;
    }
  }

}

package org.gcube.common.authorization.library.utils;

import javax.xml.bind.annotation.XmlElement;

class MapElements
{
  @XmlElement public String  key;
  @XmlElement public String value;

  @SuppressWarnings("unused")
private MapElements() {} //Required by JAXB

  public MapElements(String key, String value)
  {
    this.key   = key;
    this.value = value;
  }
}

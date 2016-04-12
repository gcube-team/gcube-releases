package org.gcube.datatransfer.portlets.user.client.obj;


import java.util.Date;
 
import com.google.gwt.i18n.client.DateTimeFormat;
 
public class Uri { 
  private DateTimeFormat df = DateTimeFormat.getFormat("MM/dd/y");
  private static int AUTO_ID = 0;
 
  private int id;
  private String name;
  private String URI;
  private boolean toBeTransferred;

public Uri() {
    id = AUTO_ID++; 
  }
 
  public Uri(String name, String URI) {
    this();
    setName(name);
    setURI(URI);
  }
 
 
  public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getURI() {
	return URI;
}

public void setURI(String uRI) {
	URI = uRI;
}

@Override
  public String toString() {
    return name != null ? name : super.toString();
  }

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public boolean isToBeTransferred() {
	return toBeTransferred;
}

public void setToBeTransferred(boolean toBeTransferred) {
	this.toBeTransferred = toBeTransferred;
}


}
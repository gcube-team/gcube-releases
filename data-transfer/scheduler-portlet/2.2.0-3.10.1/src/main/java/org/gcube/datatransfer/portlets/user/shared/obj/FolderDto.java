package org.gcube.datatransfer.portlets.user.shared.obj;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.kfuntak.gwt.json.serialization.client.JsonSerializable;
import com.kfuntak.gwt.json.serialization.client.Serializer;
import com.thoughtworks.xstream.XStream;

public class FolderDto extends BaseDto implements JsonSerializable  {

  private List<FolderDto> children;


public FolderDto() {
  }
  public FolderDto(Integer id, String name) {
    super(id, name);
  }

  public static Serializer createSerializer(){
	   return GWT.create(Serializer.class);
 }
  
  public List<FolderDto> getChildren() {
    return children;
  }

  public void setChildren(List<FolderDto> children) {
    this.children = children;
  }

  public void addChild(FolderDto child) {
    getChildren().add(child);
  }
  

}

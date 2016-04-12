package org.gcube.datatransfer.portlets.user.shared.prop;

import org.gcube.datatransfer.portlets.user.shared.obj.BaseDto;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface BaseDtoProperties extends PropertyAccess<BaseDto> {
  
  public final ModelKeyProvider<BaseDto> key = new ModelKeyProvider<BaseDto>() {
    public String getKey(BaseDto item) {
      return (item instanceof FolderDto ? "f-" : "m-") + item.getId();
    }
  };
  
  ValueProvider<BaseDto, String> name();
  ValueProvider<BaseDto, String> shortname();

}

package org.gcube.datatransfer.portlets.user.client.prop;

import org.gcube.datatransfer.portlets.user.client.obj.Uri;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface UriProperties extends PropertyAccess<Uri> {

	  @Path("id")
	  ModelKeyProvider<Uri> key();
	   
	  @Path("name")
	  LabelProvider<Uri> nameLabel();
		
	ValueProvider<Uri, String> name();
	ValueProvider<Uri, String> URI();
	ValueProvider<Uri, Boolean> toBeTransferred();

}
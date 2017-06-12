package org.gcube.data.tr;

import java.util.Arrays;
import java.util.List;

import org.gcube.data.tmf.api.Plugin;
import org.gcube.data.tmf.api.Property;
import org.gcube.data.tr.requests.BindSource;

public class TreeRepository implements Plugin {

	@Override
	public String name() {
		return Constants.TR_NAME;
	}

	@Override
	public String description() {
		return Constants.TR_DESCRIPTION;
	}

	@Override
	public List<Property> properties() {
		return null;
	}

	@Override
	public Binder binder() {
		return new Binder();
	}

	@Override
	public List<String> requestSchemas() {
		return Arrays.asList(Utils.toSchema(BindSource.class));
	}

	@Override
	public boolean isAnchored() {
		return true;
	}
}

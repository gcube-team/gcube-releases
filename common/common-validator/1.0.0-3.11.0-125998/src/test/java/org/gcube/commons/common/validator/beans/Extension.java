package org.gcube.commons.common.validator.beans;

import org.gcube.common.validator.annotations.IsValid;
import org.gcube.common.validator.annotations.NotEmpty;

public class Extension {

	@IsValid @NotEmpty
	Person[] brothers;
	
}

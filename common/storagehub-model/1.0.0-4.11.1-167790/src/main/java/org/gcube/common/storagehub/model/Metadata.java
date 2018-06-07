package org.gcube.common.storagehub.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.gcube.common.storagehub.model.annotations.MapAttribute;

@Getter
@Setter
@NoArgsConstructor
public class Metadata {

	@MapAttribute(excludeStartWith="jcr:")
	Map<String, Object> values = new HashMap<String, Object>();
}

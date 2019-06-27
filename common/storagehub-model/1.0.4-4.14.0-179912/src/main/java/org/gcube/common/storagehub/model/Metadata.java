package org.gcube.common.storagehub.model;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.storagehub.model.annotations.MapAttribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {

	@MapAttribute(excludeStartWith="jcr:")
	Map<String, Object> map = new HashMap<String, Object>();
}

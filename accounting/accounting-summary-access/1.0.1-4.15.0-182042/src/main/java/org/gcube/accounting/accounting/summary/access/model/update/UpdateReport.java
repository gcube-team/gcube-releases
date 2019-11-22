package org.gcube.accounting.accounting.summary.access.model.update;

import java.util.Set;

import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.internal.Dimension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


@AllArgsConstructor
@ToString
@Getter
public class UpdateReport {
	
	private long previousCount;
	private long currentCount;
	private long writeCount;
	private Set<ScopeDescriptor> registeredConstexts;
	private Set<Dimension> registeredDimensions;
}

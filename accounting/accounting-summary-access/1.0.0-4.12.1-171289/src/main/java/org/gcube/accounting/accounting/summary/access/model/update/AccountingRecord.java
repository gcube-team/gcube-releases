package org.gcube.accounting.accounting.summary.access.model.update;

import java.time.Instant;

import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.internal.Dimension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AccountingRecord {

	private ScopeDescriptor context;
	private Instant time;
	private Dimension dimension;
	private Long measure;
}

package org.gcube.common.storagehub.model;
import java.util.Arrays;
import java.util.List;

public class Excludes {

	public static final List<String> ALL = Arrays.asList(NodeConstants.ACCOUNTING_NAME, NodeConstants.METADATA_NAME, NodeConstants.CONTENT_NAME);
	
	public static final List<String> GET_ONLY_CONTENT = Arrays.asList(NodeConstants.ACCOUNTING_NAME, NodeConstants.METADATA_NAME);
	
	public static final List<String> EXCLUDE_ACCOUNTING = Arrays.asList(NodeConstants.ACCOUNTING_NAME);
}

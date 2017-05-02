package org.gcube.common.core.informationsystem.client.queries;

import org.gcube.common.core.informationsystem.client.ISTemplateQuery;
import org.gcube.common.core.informationsystem.client.RPDocument;

/**
 * A specialisation of {@link ISTemplateQuery} to {@link RPDocument RPDocuments} of WS-Resources.
 * @author Fabio Simeoni (University of Strathclyde)
 */
public interface WSResourceQuery extends ISTemplateQuery<RPDocument> {}

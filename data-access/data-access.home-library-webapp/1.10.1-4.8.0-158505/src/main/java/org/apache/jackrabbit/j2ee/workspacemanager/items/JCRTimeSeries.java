package org.apache.jackrabbit.j2ee.workspacemanager.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.jackrabbit.j2ee.workspacemanager.JCRWorkspaceItem;
import org.gcube.common.homelibary.model.items.type.NodeProperty;

import com.thoughtworks.xstream.XStream;


public class JCRTimeSeries extends JCRWorkspaceItem {

	public JCRTimeSeries(Node node, String login) throws RepositoryException {
		super(node, login);

		Node contentNode = node.getNode(NodeProperty.CONTENT.toString());
		item.setContent(new JCRFile(contentNode).getMap());

		List<String> headerLabels = new ArrayList<String>();
		try{
			for(Value value: contentNode.getProperty(NodeProperty.HEADER_LABELS.toString()).getValues()) {
				headerLabels.add(value.getString());
			}
		}catch (Exception e) {
			logger.error("No property " + NodeProperty.HEADER_LABELS.toString());
		}	

		String timeseriesId = contentNode.getProperty(NodeProperty.TIMESERIES_ID.toString()).getString();

		String title = contentNode.getProperty(NodeProperty.TIMESERIES_TITLE.toString()).getString();
		String creator = contentNode.getProperty(NodeProperty.TIMESERIES_CREATOR.toString()).getString();
		String timeseriesDescription = contentNode.getProperty(NodeProperty.TIMESERIES_DESCRIPTION.toString()).getString();
		String timeseriesCreationDate = contentNode.getProperty(NodeProperty.TIMESERIES_CREATED.toString()).getString();
		String publisher = contentNode.getProperty(NodeProperty.TIMESERIES_PUBLISHER.toString()).getString();
		String sourceId = contentNode.getProperty(NodeProperty.TIMESERIES_SOURCE_ID.toString()).getString();
		String sourceName = contentNode.getProperty(NodeProperty.TIMESERIES_SOURCE_NAME.toString()).getString();
		String rights = contentNode.getProperty(NodeProperty.TIMESERIES_RIGHTS.toString()).getString();
		long dimension = contentNode.getProperty(NodeProperty.TIMESERIES_DIMENSION.toString()).getLong();


		Map<NodeProperty, String> content = item.getContent();

		content.put(NodeProperty.TIMESERIES_ID, timeseriesId);
		content.put(NodeProperty.TIMESERIES_TITLE, title);
		content.put(NodeProperty.TIMESERIES_CREATOR, creator);
		content.put(NodeProperty.TIMESERIES_DESCRIPTION, timeseriesDescription);
		content.put(NodeProperty.TIMESERIES_CREATED, timeseriesCreationDate);
		content.put(NodeProperty.TIMESERIES_PUBLISHER, publisher);
		content.put(NodeProperty.TIMESERIES_SOURCE_ID, sourceId);
		content.put(NodeProperty.TIMESERIES_SOURCE_NAME, sourceName);
		content.put(NodeProperty.TIMESERIES_RIGHTS, rights);
		content.put(NodeProperty.TIMESERIES_DIMENSION, xstream.toXML(dimension));
		content.put(NodeProperty.HEADER_LABELS, xstream.toXML(headerLabels));


	}
}

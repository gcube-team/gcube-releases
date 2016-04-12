package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ts.TimeSeries;
import org.gcube.common.homelibrary.home.workspace.folder.items.ts.TimeSeriesInfo;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.homelibrary.util.zip.UnzipUtil;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

import com.thoughtworks.xstream.XStream;

public class JCRTimeSeries extends JCRWorkspaceFolderItem implements TimeSeries {

	public JCRTimeSeries(JCRWorkspace workspace, ItemDelegate node) throws RepositoryException, InternalErrorException  {
		super(workspace, node);
	}


	public JCRTimeSeries(JCRWorkspace workspace,ItemDelegate itemDelegate, String name, String description,
			String timeseriesId, String title, String creator,
			String timeseriesDescription, String timeseriesCreationDate,
			String publisher, String sourceId, String sourceName,
			String rights, long dimension, List<String> headerLabels,
			InputStream compressedCSV) throws RepositoryException, InternalErrorException, RemoteBackendException  {

		super(workspace, itemDelegate, name, description);

		Validate.notNull(timeseriesId, "TimeSeriesId must be not null");
		Validate.notNull(title, "Title must be not null");
		Validate.notNull(creator, "Creator must be not null");
		Validate.notNull(timeseriesDescription, "TimeseriesDescription must be not null");
		Validate.notNull(timeseriesCreationDate, "TimeSeriesCreationDate must be not null");
		Validate.notNull(publisher, "Publisher must be not null");
		Validate.notNull(sourceId, "SourceId must be not null");
		Validate.notNull(sourceName, "SourceName must be not null");
		Validate.notNull(rights, "Rights must be not null");
		Validate.notNull(headerLabels, "HeaderLabels must be not null");
		Validate.notNull(compressedCSV, "CompressedCSV must be not null");

		Map<NodeProperty, String> properties = itemDelegate.getProperties();
		properties.put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.TIME_SERIES.toString());


		File tmpFile = null;
		try{
			tmpFile = WorkspaceUtil.getTmpFile(compressedCSV);
			new JCRFile(workspace, itemDelegate, null, tmpFile);
		}catch (Exception e){
			logger.error("Error creating TimeSeries from " + itemDelegate.getPath());
		}finally{
			if (tmpFile!= null)
				tmpFile.delete();
		}
			
		Map<NodeProperty, String> content = itemDelegate.getContent();
		content.put(NodeProperty.TIMESERIES_CREATED, new XStream().toXML(timeseriesCreationDate));
		content.put(NodeProperty.TIMESERIES_CREATOR, creator);
		content.put(NodeProperty.TIMESERIES_DESCRIPTION, description);
		content.put(NodeProperty.TIMESERIES_DIMENSION, new XStream().toXML(dimension));
		content.put(NodeProperty.TIMESERIES_ID, timeseriesId);
		content.put(NodeProperty.TIMESERIES_PUBLISHER, publisher);
		content.put(NodeProperty.TIMESERIES_RIGHTS, rights);
		content.put(NodeProperty.TIMESERIES_SOURCE_ID, sourceId);
		content.put(NodeProperty.TIMESERIES_SOURCE_NAME, sourceName);
		content.put(NodeProperty.TIMESERIES_TITLE, title);
		content.put(NodeProperty.HEADER_LABELS, new XStream().toXML(headerLabels.toArray(new String[headerLabels.size()])));


	}


	@Override
	public long getLength() throws InternalErrorException {
		long lenght = 0;
		try {
			lenght = (long) new XStream().fromXML(delegate.getContent().get(NodeProperty.SIZE));
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return lenght; 

	}

	@SuppressWarnings("unchecked")
	@Override
	public int getNumberOfColumns() {

		int size = 0;
		Map<String, String> labels;

		try {
			labels = (Map<String, String>) new XStream().fromXML(delegate.getContent().get(NodeProperty.HEADER_LABELS));
			size = labels.size();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		} 	
		return size;

	}

	@Override
	public TimeSeriesInfo getTimeSeriesInfo() {

		try {
			Map<NodeProperty, String> content = delegate.getContent();

			String timeseriesId = content.get(NodeProperty.TIMESERIES_ID);
			String title = content.get(NodeProperty.TIMESERIES_TITLE);
			String creator = content.get(NodeProperty.TIMESERIES_CREATOR);
			String timeseriesDescription = content.get(NodeProperty.TIMESERIES_DESCRIPTION);
			String timeseriesCreationDate = content.get(NodeProperty.TIMESERIES_CREATED);
			String publisher = content.get(NodeProperty.TIMESERIES_PUBLISHER);
			String sourceId = content.get(NodeProperty.TIMESERIES_SOURCE_ID);
			String sourceName = content.get(NodeProperty.TIMESERIES_SOURCE_NAME);
			String rights = content.get(NodeProperty.TIMESERIES_RIGHTS);
			long dimension = (long) new XStream().fromXML(content.get(NodeProperty.TIMESERIES_DIMENSION));

			TimeSeriesInfo timeSeriesInfo = new TimeSeriesInfo(timeseriesId, title, creator, timeseriesDescription,
					timeseriesCreationDate, publisher, sourceId, sourceName, rights, dimension);
			return timeSeriesInfo;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getHeaderLabels() {


		Map<String, String> labels;

		try {
			labels =  (Map<String, String>) new XStream().fromXML(delegate.getContent().get(NodeProperty.HEADER_LABELS));

		} catch (Exception e) {
			throw new RuntimeException(e) ;
		} 	
		return (List<String>) labels.keySet();

	}

	@Override
	public InputStream getData() throws InternalErrorException {

		try {
			return UnzipUtil.unzipToTmp(getCompressedData());
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public InputStream getCompressedData() throws InternalErrorException {

		try {
			JCRFile file = new JCRFile(workspace, delegate);
			return file.getData();
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		} 
	}

	@Override
	public FolderItemType getFolderItemType() {
		return FolderItemType.TIME_SERIES;
	}


	@Override
	public String getMimeType() throws InternalErrorException {
		String mimetype;
		try {
			mimetype = (String) new XStream().fromXML(delegate.getContent().get(NodeProperty.MIME_TYPE));
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return mimetype; 
	}

}

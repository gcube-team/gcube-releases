package org.gcube.portlets.user.dataminerexecutor.server.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.data.analysis.dataminermanagercl.server.util.ServiceCredentials;
import org.gcube.data.analysis.dataminermanagercl.shared.data.ColumnItem;
import org.gcube.data.analysis.dataminermanagercl.shared.data.TableItemSimple;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;
import org.gcube.portlets.user.dataminerexecutor.server.storage.StorageUtil;
import org.gcube.portlets.user.dataminerexecutor.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.csv4j.CSVReader;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TableReader {
	private static Logger logger = LoggerFactory.getLogger(TableReader.class);

	private ServiceCredentials serviceCredentials;
	private ItemDescription item;

	public TableReader(ServiceCredentials serviceCredentials, ItemDescription item) {
		this.serviceCredentials = serviceCredentials;
		this.item= item;
	}

	public TableItemSimple getTableItemSimple() throws ServiceException {
		StorageUtil storageUtil=new StorageUtil();
		InputStream is = storageUtil.getFileOnWorkspace(
				serviceCredentials.getUserName(), item.getId());
		Reader fileReader = new InputStreamReader(is);
		CSVReader csvReader = new CSVReader(fileReader);
		List<String> firstLine = getFirstLine(csvReader, false);
		ArrayList<String> columns = new ArrayList<String>(firstLine);
		
		String publicLink=storageUtil.getPublicLink(serviceCredentials.getUserName(), item.getId());
		
		TableItemSimple tableItemSimple=new TableItemSimple(publicLink,item.getName(), item.getType());
		ArrayList<ColumnItem> columnItemList=new ArrayList<ColumnItem>();
		for(int i=0; i<columns.size(); i++){
			ColumnItem columnItem=new ColumnItem(String.valueOf(i), columns.get(i));
			columnItemList.add(columnItem);
		}
		tableItemSimple.setColumns(columnItemList);
		return tableItemSimple;

	}

	public List<String> getFirstLine(CSVReader csvReader, boolean includeComment)
			throws ServiceException {
		try {
			logger.trace("getFirstLine includeComment: " + includeComment);

			List<String> header = csvReader.readLine(includeComment);
			return header == null ? Collections.<String> emptyList() : header;

		} catch (Throwable e) {
			logger.error("Error reading csv file: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

}

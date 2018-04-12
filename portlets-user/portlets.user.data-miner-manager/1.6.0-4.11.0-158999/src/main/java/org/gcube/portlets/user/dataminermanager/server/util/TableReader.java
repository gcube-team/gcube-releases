package org.gcube.portlets.user.dataminermanager.server.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.csv4j.CSVReader;

import org.gcube.portlets.user.dataminermanager.server.storage.StorageUtil;
import org.gcube.portlets.user.dataminermanager.shared.data.ColumnItem;
import org.gcube.portlets.user.dataminermanager.shared.data.TableItemSimple;
import org.gcube.portlets.user.dataminermanager.shared.exception.ServiceException;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TableReader {
	private static Logger logger = LoggerFactory.getLogger(TableReader.class);

	private ServiceCredentials serviceCredentials;
	private Item item;

	public TableReader(ServiceCredentials serviceCredentials, Item item) {
		this.serviceCredentials = serviceCredentials;
		this.item= item;
	}

	public TableItemSimple getTableItemSimple() throws ServiceException {

		InputStream is = StorageUtil.getInputStreamForItemOnWorkspace(
				serviceCredentials.getUserName(), item.getId());
		Reader fileReader = new InputStreamReader(is);
		CSVReader csvReader = new CSVReader(fileReader);
		List<String> firstLine = getFirstLine(csvReader, false);
		ArrayList<String> columns = new ArrayList<String>(firstLine);
		
		String publicLink=StorageUtil.getPublicLink(serviceCredentials.getUserName(), item.getId());
		
		TableItemSimple tableItemSimple=new TableItemSimple(publicLink,item.getName(), item.getType().name());
		ArrayList<ColumnItem> columnItemList=new ArrayList<ColumnItem>();
		for(int i=0; i<columns.size(); i++){
			ColumnItem columnItem=new ColumnItem(i, columns.get(i));
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

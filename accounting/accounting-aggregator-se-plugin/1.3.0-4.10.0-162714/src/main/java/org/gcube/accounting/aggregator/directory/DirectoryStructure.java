package org.gcube.accounting.aggregator.directory;

import java.text.DateFormat;
import java.util.Date;

import org.gcube.accounting.aggregator.aggregation.AggregationType;
import org.gcube.accounting.aggregator.plugin.AccountingAggregatorPluginDeclaration;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class DirectoryStructure<D> {

	public D getTargetFolder(AggregationType aggregationType, Date aggregationStartDate) throws Exception {
		D root = getRoot();
		D aggregatorPluginDirectory = createDirectory(root, AccountingAggregatorPluginDeclaration.NAME);
		D aggregationTypeDirectory = createDirectory(aggregatorPluginDirectory, aggregationType.name());
		DateFormat dateFormat  = aggregationType.getDateFormat();
		String dateString = dateFormat.format(aggregationStartDate);
		String[] splittedDate = dateString.split(AggregationType.DATE_SEPARATOR);
		D d = aggregationTypeDirectory;
		// lenght-1 because the last part is used as filename of file
		for(int i=0; i<(splittedDate.length-1); i++){
			d = createDirectory(d, splittedDate[i]);
		}
		return d;
	}
	
	protected abstract D getRoot() throws Exception;
	
	protected abstract D createDirectory(D parent, String name) throws Exception;

}

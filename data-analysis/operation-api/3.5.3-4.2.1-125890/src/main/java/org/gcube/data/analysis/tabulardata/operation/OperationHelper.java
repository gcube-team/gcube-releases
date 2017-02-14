package org.gcube.data.analysis.tabulardata.operation;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPURLConnection;
import org.gcube.data.analysis.tabulardata.metadata.MetadataHolder;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.metadata.Locales;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedTextHolder;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;

public class OperationHelper {

	@SuppressWarnings("unchecked")
	public static <T> T getParameter(LeafParameter<T> parameter, OperationInvocation invocation) {
		Object instance = invocation.getParameterInstances().get(parameter.getIdentifier());
		if (instance == null) throw new RuntimeException(String.format("Parameter with id '%s' not found in invocation",parameter.getIdentifier()));
		try {
			return (T) instance;
		} catch(ClassCastException e){
			throw new RuntimeException(String.format("Provided parameter with id '%s' is not of valid type",parameter.getIdentifier()));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getParameter(LeafParameter<T> parameter,Map<String, Object> parameters) {
		Object instance = parameters.get(parameter.getIdentifier());
		if (instance == null) throw new RuntimeException(String.format("Parameter with id '%s' not found in invocation",parameter.getIdentifier()));
		try {
			return (T) instance;
		} catch(ClassCastException e){
			throw new RuntimeException(String.format("Provided parameter with id '%s' is not of valid type",parameter.getIdentifier()));
		}
	}
	
	public final static String retrieveColumnLabel(Column targetColumn) {
		if (targetColumn.contains(NamesMetadata.class))	{
			NamesMetadata namesMeta = targetColumn.getMetadata(NamesMetadata.class);
			if (namesMeta.hasTextWithLocale(Locales.getDefaultLocale()))
				return namesMeta.getTextWithLocale(Locales.getDefaultLocale()).getValue();
			if (namesMeta.getTexts().size() > 0)
				return namesMeta.getTexts().get(0).getValue();
		}
		return targetColumn.getName();

	}

	
	public static final String retrieveTableLabel(Table targetTable){
		if (targetTable.contains(NamesMetadata.class))	{
			NamesMetadata namesMeta = targetTable.getMetadata(NamesMetadata.class);
			if (namesMeta.hasTextWithLocale(Locales.getDefaultLocale()))
				return namesMeta.getTextWithLocale(Locales.getDefaultLocale()).getValue();
			if (namesMeta.getTexts().size() > 0)
				return namesMeta.getTexts().get(0).getValue();
		} else if (targetTable.contains(TableDescriptorMetadata.class)){
			TableDescriptorMetadata tdm = targetTable.getMetadata(TableDescriptorMetadata.class);
			return String.format("%s[%s]", tdm.getName(), tdm.getVersion());
		} 
		return targetTable.getName();
	}
	
	public final static String getColumnNamesSnippet(Collection<Column> columns){
		StringBuilder columnNamesSnippet = new StringBuilder();
		for (Column col : columns) {
			columnNamesSnippet.append(col.getName() + ", ");
		}
		columnNamesSnippet.delete(columnNamesSnippet.length() - 2, columnNamesSnippet.length() - 1);
		return columnNamesSnippet.toString();
	}
	
	public final static String getColumnLabelsSnippet(Collection<Column> columns){
		StringBuilder columnNamesSnippet = new StringBuilder();
		for (Column col : columns) {
			columnNamesSnippet.append(retrieveColumnLabel(col) + ", ");
		}
		columnNamesSnippet.delete(columnNamesSnippet.length() - 2, columnNamesSnippet.length() - 1);
		return columnNamesSnippet.toString();
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final ArrayList<String> getLabels(MetadataHolder holder){
		ArrayList<String> labels=new ArrayList<>();
		try{
			for(LocalizedText text:((LocalizedTextHolder) holder.getMetadata(NamesMetadata.class)).getTexts())
				labels.add(text.getValue());
		}catch(NoSuchMetadataException e){
			//no labels			
		}
		return labels;
	}
}

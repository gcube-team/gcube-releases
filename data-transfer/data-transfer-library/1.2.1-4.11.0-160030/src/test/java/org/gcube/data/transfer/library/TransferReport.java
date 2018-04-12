package org.gcube.data.transfer.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.transfer.library.model.Source;
import org.gcube.data.transfer.model.TransferCapabilities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public class TransferReport {
	
	public  static enum ReportType{
		local,uri,storage
	}
	
	
	private static final char end='\n';
	
	@AllArgsConstructor
	@Getter
	@ToString
	private static class ReportItem{
		String source;
		long size;
		long elapsed;	
		
		
		// bytes/msec
		public long getAvgSpeed(){
			return size/elapsed;
		}
		
		
		
		
	}
	
	
	protected TransferCapabilities host;
	
	private Map<ReportType,ArrayList<ReportItem>> reports=new HashMap<>(); 
	
	
	
	public TransferReport(TransferCapabilities host) {
		super();
		this.host = host;
	}

	
	public void addReport(ReportType sourceType,String theSource,long size,long elapsed){		
		if(!reports.containsKey(sourceType)) reports.put(sourceType, new ArrayList<ReportItem>());
		reports.get(sourceType).add(new ReportItem(theSource, size, elapsed));		
	}
	
	
	public String print(){
		StringBuilder builder=new StringBuilder(" Report for : "+host+end);
		for(Entry<ReportType,ArrayList<ReportItem>> entry:reports.entrySet()){
			builder.append("Source type "+entry.getKey()+end);
			ReportItem maxSizeItem=null;
			ReportItem maxAvgSpeedItem=null;
			ReportItem minSizeItem=null;
			ReportItem minAvgSpeedItem=null;
			long currentAvgSpeedCounter=0l;			
			
			for(ReportItem item:entry.getValue()){
				if(maxSizeItem==null||maxSizeItem.getSize()<item.getSize()) maxSizeItem=item;
				if(maxAvgSpeedItem==null||maxAvgSpeedItem.getAvgSpeed()<item.getAvgSpeed()) maxAvgSpeedItem=item;
				if(minSizeItem==null||minSizeItem.getSize()>item.getSize()) minSizeItem=item;
				if(minAvgSpeedItem==null||minAvgSpeedItem.getAvgSpeed()>item.getAvgSpeed()) minAvgSpeedItem=item;
				currentAvgSpeedCounter+=item.getAvgSpeed();
			}
			
			builder.append("Max Size Item : "+maxSizeItem+end);
			builder.append("Max Avg Speed Item : "+maxAvgSpeedItem+end);
			builder.append("Min Size Item : "+minSizeItem+end);
			builder.append("Min Avg Speed Item : "+minAvgSpeedItem+end);
			builder.append("Total avg speed : "+currentAvgSpeedCounter/entry.getValue().size()+end);			
		}
		
		return builder.toString();
	};
}

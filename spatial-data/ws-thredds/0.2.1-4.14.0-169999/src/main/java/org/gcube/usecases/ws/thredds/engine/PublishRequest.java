package org.gcube.usecases.ws.thredds.engine;

import java.io.File;
import java.util.HashSet;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Data
public class PublishRequest {

	@Getter
	@AllArgsConstructor
	@ToString
	public static class PublishItem{
		String url;
		String name;
		String id;
		
		public PublishItem(WorkspaceItem item) throws InternalErrorException {
			url=item.getPublicLink(false);
			name=item.getName();
			id=item.getId();
		}
		
	}
	
	
	public static enum Mode{
		NCML,NC
	}
	
	@NonNull
	private PublishItem source;	
	@NonNull
	private Mode mode;
	@NonNull
	private String catalog;
	@NonNull
	private String publishToken;

	
	private Integer queueCount=0;
	private String queueId;
	private File metadata=null;
	
	
	private HashSet<String> toGatherReportsId=null;
	
	public boolean isQueue() {
		return queueCount>0;
	}
	
	
	public boolean isGenerateMeta() {
		return metadata==null;
	}
}

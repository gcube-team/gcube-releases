/**
 * 
 */
package org.gcube.common.core.resources.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Plugin package 
 * 
 * @author Manuele Simi (ISTI-CNR) 
 *
 */
public class Plugin extends Package {
	
	protected TargetService targetService;

	protected String entryPoint;	
	protected List<String> files= new ArrayList<String>();
	
	
	
	public TargetService getTargetService() {return this.targetService;}
	
	public void setTargetService(TargetService service) {this.targetService = service;}		
	
	public String getEntryPoint() {	return entryPoint;}
		
	public void setEntryPoint(String entryPoint) {this.entryPoint = entryPoint;}
	
	public List<String> getFiles() {return this.files;}
	
	public void setFiles(List<String> files) {this.files=files;}

	public static class Service extends org.gcube.common.core.resources.service.Dependency.Service {}
	
	/**
	 * Plugin's target service
	 * @author Manuele Simi (ISTI-CNR(	 
	 */
	public static class TargetService extends Service {
		protected String _package;
		protected String version;
		
		public String getTargetPackage(){return this._package;}		
		public void setTargetPackage(String name) {this._package = name;}		
		public String getTargetVersion() {return this.version;}		
		public void setTargetVersion(String version) {this.version = version; /*Version.completeVersionRange(version);*/}
	}
}

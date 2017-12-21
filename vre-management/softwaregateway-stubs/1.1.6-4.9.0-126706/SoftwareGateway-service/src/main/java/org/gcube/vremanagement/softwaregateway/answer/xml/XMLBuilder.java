package org.gcube.vremanagement.softwaregateway.answer.xml;

import java.util.Iterator;
import java.util.List;

import org.gcube.vremanagement.softwaregateway.answer.ReportObject;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
/**
 *  Build XML answers
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class XMLBuilder extends XMLModel {
	
	
	/**
	 * Add the xml header 
	 */
	@Override
	public void addHeader() {
		xml.append("<Packages>\n");
	}
	/**
	 * add the xml footer
	 */
	@Override
	public void addFooter() {
		xml.append("</Packages>\n");
	}
	/**
	 * Add dependencies list in xml format
	 */
	@Override
	public void addDependencies(List<GCubeCoordinates> resolved,
			List<GCubeCoordinates> missing) {
		xml.append("<DependencyResolutionReport>\n");
		xml.append("\t<ResolvedDependencies>\n");
		if((resolved != null ) && (resolved.size() > 0)){
			for(Iterator<GCubeCoordinates> it=resolved.iterator(); it.hasNext();){
				GCubeCoordinates gCubeCoordinates= it.next();
				xml.append("\t\t\t<Dependency>\n");
				xml.append("\t\t\t\t<Service>\n");
				xml.append("\t\t\t\t\t<Class>").append(gCubeCoordinates.getServiceClass().trim()).append("</Class>\n");
				xml.append("\t\t\t\t\t<Name>").append(gCubeCoordinates.getServiceName().trim()).append("</Name>\n");
				xml.append("\t\t\t\t\t<Version>").append(gCubeCoordinates.getServiceVersion().trim()).append("</Version>\n");
				xml.append("\t\t\t\t</Service>\n");
				xml.append("\t\t\t\t<Package>").append(gCubeCoordinates.getPackageName().trim()).append("</Package>\n");
				xml.append("\t\t\t\t<Version>").append(gCubeCoordinates.getPackageVersion()).append("</Version>\n");
		
//				xml.append("\t\t\t\t<Scope level=\"GHN\"/>\n");
				xml.append("\t\t\t</Dependency>\n");
			}

		}
		xml.append("\t</ResolvedDependencies>\n");
		xml.append("\t<MissingDependencies>\n");
		if((missing != null ) && (missing.size() > 0)){
			for(Iterator<GCubeCoordinates> it=missing.iterator(); it.hasNext();){
				GCubeCoordinates gCubeCoordinates= it.next();
				xml.append("\t\t\t<MissingDependency>\n");
				xml.append("\t\t\t\t<Service>\n");
				xml.append("\t\t\t\t\t<Class>").append(gCubeCoordinates.getServiceClass().trim()).append("</Class>\n");
				xml.append("\t\t\t\t\t<Name>").append(gCubeCoordinates.getServiceName().trim()).append("</Name>\n");
				xml.append("\t\t\t\t\t<Version>").append(gCubeCoordinates.getServiceVersion().trim()).append("</Version>\n");
				xml.append("\t\t\t\t</Service>\n");
				xml.append("\t\t\t\t<Package>").append(gCubeCoordinates.getPackageName().trim()).append("</Package>\n");
				xml.append("\t\t\t\t<Version>").append(gCubeCoordinates.getPackageVersion()).append("</Version>\n");
		
//				xml.append("\t\t\t\t<Scope level=\"GHN\"/>\n");
				xml.append("\t\t\t</MissingDependency>\n");
			}

		}	
		xml.append("\t</MissingDependencies>\n");
		xml.append("</DependencyResolutionReport>\n");
	}

	/**
	 * Add  plugin list in xml format
	 */
	@Override
	public void addPlugin(List<GCubeCoordinates> plugin) {
		xml.append("<ServicePlugins>\n");
		for(Iterator<GCubeCoordinates> it=plugin.iterator(); it.hasNext();){
			GCubeCoordinates gCubeCoordinates= it.next();
			xml.append("\t<Plugin>\n");
			xml.append("\t\t<Service>\n");
			xml.append("\t\t\t<Class>").append(gCubeCoordinates.getServiceClass()).append("</Class>\n");
			xml.append("\t\t\t<Name>").append(gCubeCoordinates.getServiceName()).append("</Name>\n");
			xml.append("\t\t\t<Version>").append(gCubeCoordinates.getServiceVersion()).append("</Version>\n");
			xml.append("\t\t</Service>\n");
			xml.append("\t</Plugin>\n");
		}
		xml.append("</ServicePlugins>");
	}

	
	/**
	 * add package list in xml format
	 */
	@Override
	public void addPackages(List<GCubeCoordinates> plugin) {
		xml.append("<Package>\n");
		for(Iterator<GCubeCoordinates> it=plugin.iterator(); it.hasNext();){
			GCubeCoordinates gCubeCoordinates= it.next();
			xml.append("\t\t\t<ServiceClass>").append(gCubeCoordinates.getServiceClass()).append("</ServiceClass>\n");
			xml.append("\t\t\t<ServiceName>").append(gCubeCoordinates.getServiceName()).append("</ServiceName>\n");
			xml.append("\t\t\t<ServiceVersion>").append(gCubeCoordinates.getServiceVersion()).append("</ServiceVersion>\n");
			xml.append("\t\t\t<PackageName>").append(gCubeCoordinates.getServiceName()).append("</PackageName>\n");
			xml.append("\t\t\t<PackageVersion>").append(gCubeCoordinates.getServiceVersion()).append("</PackageVersion>\n");
		}
		xml.append("</Package>");
		
	}
	
	/**
	 * build a record of report
	 */
	@Override
	public void addReportPackage(ReportObject obj){
		xml.append("\t<Package>\n");
			xml.append("\t\t<groupID>").append(obj.getGroupId()).append("</groupID>\n");
			xml.append("\t\t<artifactID>").append(obj.getArtifactId()).append("</artifactID>\n");
			xml.append("\t\t<version>").append(obj.getVersion()).append("</version>\n");
			xml.append("\t\t<ID>").append(obj.getID()).append("</ID>\n");
			xml.append("\t\t<URL>").append(obj.getUrl()).append("</URL>\n");
			xml.append("\t\t<javadoc>").append(obj.getJavadocUrl()).append("</javadoc>\n");
			xml.append("\t\t<Status>").append(obj.getStatus()).append("</Status>\n");
			xml.append("\t\t<Operation>").append(obj.getOperation()).append("</Operation>\n");
			xml.append("\t\t<Timestamp>").append(obj.getTimestamp()).append("</Timestamp>\n");
		xml.append("\t</Package>\n");
	}

}

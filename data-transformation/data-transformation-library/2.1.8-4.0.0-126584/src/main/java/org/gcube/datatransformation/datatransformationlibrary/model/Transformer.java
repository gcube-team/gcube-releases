package org.gcube.datatransformation.datatransformationlibrary.model;

import java.net.URL;
import java.util.ArrayList;

import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * <tt>Transformer</tt> references to a {@link Program} class and also contains info about the software where the program is located.
 * </p>
 */
public class Transformer {
	
	private String programClass;
	private ArrayList<Parameter> globalProgramParameters = new ArrayList<Parameter>();
	
	private ArrayList<SoftwarePackage> softwarePackages = new ArrayList<SoftwarePackage>();
	
	/**
	 * Returns the software packages of the program.
	 * @return the software packages of the program.
	 */
	public ArrayList<SoftwarePackage> getSoftwarePackages() {
		return softwarePackages;
	}

	/**
	 * Sets the software packages of the program.
	 * @param softwarePackages the software packages of the program.
	 */
	public void setSoftwarePackages(ArrayList<SoftwarePackage> softwarePackages) {
		this.softwarePackages = softwarePackages;
	}

	/**
	 * Returns the class of the program.
	 * @return the class of the program.
	 */
	public String getProgramClass() {
		return programClass;
	}

	/**
	 * Sets the class of the program.
	 * @param programClass the class of the program.
	 */
	public void setProgramClass(String programClass) {
		this.programClass = programClass;
	}

	/**
	 * Returns the global program parameters.
	 * @return the global program parameters.
	 */
	public ArrayList<Parameter> getGlobalProgramParams() {
		return globalProgramParameters;
	}

	/**
	 * Sets the global program parameters.
	 * @param programParams the global program parameters.
	 */
	public void setGlobalProgramParams(ArrayList<Parameter> programParams) {
		this.globalProgramParameters = programParams;
	}

	protected void fromDOM(Element transformer) throws Exception {
		Element progclass = (Element)transformer.getElementsByTagName(XMLDefinitions.ELEMENT_class).item(0);
		if(progclass!=null && progclass.getTextContent().trim().length()>0){
			this.setProgramClass(progclass.getTextContent());
			ArrayList <Parameter> programParametersList = new ArrayList<Parameter>();
			Element parameter;int cnt=0;
			while((parameter=(Element)transformer.getElementsByTagName(XMLDefinitions.ELEMENT_parameter).item(cnt++))!=null){
				Parameter param = new Parameter();
				param.setName(parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterName));
				param.setValue(parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterValue));
				programParametersList.add(param);
			}
			this.setGlobalProgramParams(programParametersList);
			softwarePackages.clear();
			Element software;
			if((software = (Element)transformer.getElementsByTagName(XMLDefinitions.ELEMENT_software).item(0))!=null){
				Element swrpackage;int cnt2=0;
				while((swrpackage=(Element)software.getElementsByTagName(XMLDefinitions.ELEMENT_package).item(cnt2++))!=null){
					SoftwarePackage pkg = new SoftwarePackage();
					pkg.setId(swrpackage.getElementsByTagName(XMLDefinitions.ELEMENT_packageID).item(0).getTextContent());
					pkg.setLocation(new URL(swrpackage.getElementsByTagName(XMLDefinitions.ELEMENT_packageLocation).item(0).getTextContent()));
					softwarePackages.add(pkg);
				}
			}
		}
	}
	
	protected void toDOM(Element tp){
		Document doc = tp.getOwnerDocument();
		
		Element transformerelm = doc.createElement(XMLDefinitions.ELEMENT_transformer);
		tp.appendChild(transformerelm);

		Element sftelm = doc.createElement(XMLDefinitions.ELEMENT_software);
		if(this.softwarePackages!=null && this.softwarePackages.size()>0){
			for(SoftwarePackage pkg: this.softwarePackages){
				Element pkgelm = doc.createElement(XMLDefinitions.ELEMENT_package);
				Element pkgid = doc.createElement(XMLDefinitions.ELEMENT_packageID);
				pkgid.setTextContent(pkg.getId());
				Element pkgloc = doc.createElement(XMLDefinitions.ELEMENT_packageLocation);
				pkgloc.setTextContent(pkg.getLocation().toString());
				pkgelm.appendChild(pkgid);
				pkgelm.appendChild(pkgloc);
				sftelm.appendChild(pkgelm);
			}
		}
		transformerelm.appendChild(sftelm);
		
		Element cls = doc.createElement(XMLDefinitions.ELEMENT_class);
		cls.setTextContent(this.programClass);
		transformerelm.appendChild(cls);
		
		Element progparams = doc.createElement(XMLDefinitions.ELEMENT_globalprogramparams);
		if(this.globalProgramParameters!=null && this.globalProgramParameters.size()>0){
			for(Parameter param: this.globalProgramParameters){
				param.toDOM(progparams);
			}
		}
		transformerelm.appendChild(progparams);
		
		tp.appendChild(transformerelm);
	}
}

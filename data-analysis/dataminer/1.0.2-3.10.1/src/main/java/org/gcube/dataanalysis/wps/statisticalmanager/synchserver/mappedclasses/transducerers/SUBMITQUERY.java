package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.XmlObject;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings.*;
import org.n52.wps.algorithm.annotation.*;
import org.n52.wps.io.data.*;
import org.n52.wps.io.data.binding.complex.*;
import org.n52.wps.io.data.binding.literal.*;
import org.n52.wps.server.*;import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.AbstractEcologicalEngineMapper;import org.n52.wps.server.*;import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.*;
@Algorithm(statusSupported=false, title="SUBMITQUERY", abstrakt="Algorithm that allows to submit a query", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SUBMITQUERY", version = "1.1.0")
public class SUBMITQUERY extends AbstractEcologicalEngineMapper implements ITransducer{
@LiteralDataInput(abstrakt="Name of the parameter: ResourceName. The name of the resource", defaultValue="", title="The name of the resource", identifier = "ResourceName", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setResourceName(String data) {inputs.put("ResourceName",data);}
@LiteralDataInput(abstrakt="Name of the parameter: DatabaseName. The name of the database", defaultValue="", title="The name of the database", identifier = "DatabaseName", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setDatabaseName(String data) {inputs.put("DatabaseName",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Read-Only Query. Check the box if the query must be read-only", defaultValue="true", allowedValues= {"true","false"}, title="Check the box if the query must be read-only", identifier = "Read-Only Query", maxOccurs=1, minOccurs=1,binding = LiteralBooleanBinding.class) public void setRead_Only_Query(Boolean data) {inputs.put("Read-Only Query",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: Apply Smart Correction. Check the box for smart correction", defaultValue="true", allowedValues= {"true","false"}, title="Check the box for smart correction", identifier = "Apply Smart Correction", maxOccurs=1, minOccurs=1,binding = LiteralBooleanBinding.class) public void setApply_Smart_Correction(Boolean data) {inputs.put("Apply Smart Correction",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: Language. Language", allowedValues= {"NONE","POSTGRES","MYSQL"}, defaultValue="NONE", title="Language", identifier = "Language", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setLanguage(String data) {inputs.put("Language",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Query. query", defaultValue="", title="query", identifier = "Query", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setQuery(String data) {inputs.put("Query",data);}

@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }
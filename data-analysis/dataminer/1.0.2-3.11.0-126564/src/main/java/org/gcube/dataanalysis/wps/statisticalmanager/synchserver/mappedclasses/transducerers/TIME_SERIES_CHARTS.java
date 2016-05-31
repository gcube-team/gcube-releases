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
@Algorithm(statusSupported=false, title="TIME_SERIES_CHARTS", abstrakt="An algorithm producing time series charts of attributes vs. quantities. Charts are displayed per quantity column and superposing quantities are summed.", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIME_SERIES_CHARTS", version = "1.1.0")
public class TIME_SERIES_CHARTS extends AbstractEcologicalEngineMapper implements ITransducer{
@ComplexDataInput(abstrakt="Name of the parameter: InputTable. The input table [a http link to a table in UTF-8 encoding following this template: (GENERIC) A generic comma separated csv file in UTF-8 encoding]", title="The input table [a http link to a table in UTF-8 encoding following this template: (GENERIC) A generic comma separated csv file in UTF-8 encoding]", maxOccurs=1, minOccurs=1, identifier = "InputTable", binding = GenericFileDataBinding.class)	public void setInputTable(GenericFileData file) {inputs.put("InputTable",file);}
@LiteralDataInput(abstrakt="Name of the parameter: Attributes. The dimensions to consider in the charts [a sequence of names of columns from InputTable separated by | ]", defaultValue="", title="The dimensions to consider in the charts [a sequence of names of columns from InputTable separated by | ]", identifier = "Attributes", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setAttributes(String data) {inputs.put("Attributes",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Quantities. The numeric quantities to visualize  [a sequence of names of columns from InputTable separated by | ]", defaultValue="", title="The numeric quantities to visualize  [a sequence of names of columns from InputTable separated by | ]", identifier = "Quantities", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setQuantities(String data) {inputs.put("Quantities",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Time. The column containing time information [the name of a column from InputTable]", defaultValue="year", title="The column containing time information [the name of a column from InputTable]", identifier = "Time", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setTime(String data) {inputs.put("Time",data);}

@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }
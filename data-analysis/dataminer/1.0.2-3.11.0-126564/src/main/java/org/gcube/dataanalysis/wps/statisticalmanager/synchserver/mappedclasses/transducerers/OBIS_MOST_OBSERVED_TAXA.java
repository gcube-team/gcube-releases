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
@Algorithm(statusSupported=false, title="OBIS_MOST_OBSERVED_TAXA", abstrakt="An algorithm producing a bar chart for the most observed taxa in a certain years range (with respect to the OBIS database)", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OBIS_MOST_OBSERVED_TAXA", version = "1.1.0")
public class OBIS_MOST_OBSERVED_TAXA extends AbstractEcologicalEngineMapper implements ITransducer{
@LiteralDataInput(abstrakt="Name of the parameter: Taxa_number. Number of taxa to report", defaultValue="10", title="Number of taxa to report", identifier = "Taxa_number", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setTaxa_number(String data) {inputs.put("Taxa_number",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Level. Choose the taxonomy level", allowedValues= {"GENUS","FAMILY","ORDER","CLASS"}, defaultValue="GENUS", title="Choose the taxonomy level", identifier = "Level", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setLevel(String data) {inputs.put("Level",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Start_year. Starting year of the analysis", defaultValue="1800", title="Starting year of the analysis", identifier = "Start_year", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setStart_year(String data) {inputs.put("Start_year",data);}
@LiteralDataInput(abstrakt="Name of the parameter: End_year. Ending year of the analysis", defaultValue="2020", title="Ending year of the analysis", identifier = "End_year", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setEnd_year(String data) {inputs.put("End_year",data);}
@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }
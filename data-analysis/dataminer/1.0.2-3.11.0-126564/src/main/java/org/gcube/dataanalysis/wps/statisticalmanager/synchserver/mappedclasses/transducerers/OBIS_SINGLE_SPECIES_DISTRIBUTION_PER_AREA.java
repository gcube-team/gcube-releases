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
@Algorithm(statusSupported=false, title="OBIS_SINGLE_SPECIES_DISTRIBUTION_PER_AREA", abstrakt="An algorithm producing a bar chart for the distribution of a species along a certain type of marine area (e.g. LME or MEOW)", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OBIS_SINGLE_SPECIES_DISTRIBUTION_PER_AREA", version = "1.1.0")
public class OBIS_SINGLE_SPECIES_DISTRIBUTION_PER_AREA extends AbstractEcologicalEngineMapper implements ITransducer{
@LiteralDataInput(abstrakt="Name of the parameter: Species. The species to analyze", defaultValue="", title="The species to analyze", identifier = "Species", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setSpecies(String data) {inputs.put("Species",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Area. Choose the area type", allowedValues= {"LME","MEOW"}, defaultValue="LME", title="Choose the area type", identifier = "Area", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setArea(String data) {inputs.put("Area",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Start_year. Starting year of the analysis", defaultValue="1800", title="Starting year of the analysis", identifier = "Start_year", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setStart_year(String data) {inputs.put("Start_year",data);}
@LiteralDataInput(abstrakt="Name of the parameter: End_year.  Ending year of the analysis", defaultValue="2020", title=" Ending year of the analysis", identifier = "End_year", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setEnd_year(String data) {inputs.put("End_year",data);}
@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }
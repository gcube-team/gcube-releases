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
@Algorithm(statusSupported=false, title="FAO_OCEAN_AREA_COLUMN_CREATOR_FROM_QUADRANT", abstrakt="An algorithm that adds a column containing the FAO Ocean Area codes associated to longitude, latitude and quadrant columns.", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.FAO_OCEAN_AREA_COLUMN_CREATOR_FROM_QUADRANT", version = "1.1.0")
public class FAO_OCEAN_AREA_COLUMN_CREATOR_FROM_QUADRANT extends AbstractEcologicalEngineMapper implements ITransducer{
@ComplexDataInput(abstrakt="Name of the parameter: InputTable. The table to which the algorithm adds the csquare column [a http link to a table in UTF-8 encoding following this template: (GENERIC) A generic comma separated csv file in UTF-8 encoding]", title="The table to which the algorithm adds the csquare column [a http link to a table in UTF-8 encoding following this template: (GENERIC) A generic comma separated csv file in UTF-8 encoding]", maxOccurs=1, minOccurs=1, identifier = "InputTable", binding = GenericFileDataBinding.class)	public void setInputTable(GenericFileData file) {inputs.put("InputTable",file);}
@LiteralDataInput(abstrakt="Name of the parameter: Longitude_Column. The column containing Longitude information [the name of a column from InputTable]", defaultValue="x", title="The column containing Longitude information [the name of a column from InputTable]", identifier = "Longitude_Column", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setLongitude_Column(String data) {inputs.put("Longitude_Column",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Latitude_Column. The column containing Latitude information [the name of a column from InputTable]", defaultValue="y", title="The column containing Latitude information [the name of a column from InputTable]", identifier = "Latitude_Column", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setLatitude_Column(String data) {inputs.put("Latitude_Column",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Quadrant_Column. The column containing Quadrant information [the name of a column from InputTable]", defaultValue="quadrant", title="The column containing Quadrant information [the name of a column from InputTable]", identifier = "Quadrant_Column", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setQuadrant_Column(String data) {inputs.put("Quadrant_Column",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Resolution. The resolution of the FAO Ocean Area codes", defaultValue="5", title="The resolution of the FAO Ocean Area codes", identifier = "Resolution", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setResolution(Integer data) {inputs.put("Resolution",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: OutputTableName. The name of the output table", defaultValue="faooceanarea_", title="The name of the output table", identifier = "OutputTableName", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setOutputTableName(String data) {inputs.put("OutputTableName",data);}
@ComplexDataOutput(abstrakt="Name of the parameter: OutputTable. Output table [a http link to a table in UTF-8 ecoding following this template: (GENERIC) A generic comma separated csv file in UTF-8 encoding]", title="Output table [a http link to a table in UTF-8 ecoding following this template: (GENERIC) A generic comma separated csv file in UTF-8 encoding]", identifier = "OutputTable", binding = CsvFileDataBinding.class)	public GenericFileData getOutputTable() {URL url=null;try {url = new URL((String) outputs.get("OutputTable")); return new GenericFileData(url.openStream(),"text/csv");} catch (Exception e) {e.printStackTrace();return null;}}
@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }
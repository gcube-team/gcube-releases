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
@Algorithm(statusSupported=false, title="ZEXTRACTION", abstrakt="An algorithm to extract the Z values from a geospatial features repository (e.g. NETCDF, ASC, GeoTiff files etc. ). The algorithm analyses the repository and automatically extracts the Z values according to the resolution wanted by the user. It produces one chart of the Z values and one table containing the values.", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ZEXTRACTION", version = "1.1.0")
public class ZEXTRACTION extends AbstractEcologicalEngineMapper implements ITransducer{
@LiteralDataInput(abstrakt="Name of the parameter: Layer. Layer Title or UUID or HTTP link. E.g. the title or the UUID (preferred) of a layer indexed in the e-Infrastructure on GeoNetwork - You can retrieve it from GeoExplorer. Otherwise you can supply the direct HTTP link of the layer. The format will be guessed from the link. The default is GeoTiff. Supports several standards (NETCDF, WFS, WCS ASC, GeoTiff )", defaultValue="", title="Layer Title or UUID or HTTP link. E.g. the title or the UUID (preferred) of a layer indexed in the e-Infrastructure on GeoNetwork - You can retrieve it from GeoExplorer. Otherwise you can supply the direct HTTP link of the layer. The format will be guessed from the link. The default is GeoTiff. Supports several standards (NETCDF, WFS, WCS ASC, GeoTiff )", identifier = "Layer", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setLayer(String data) {inputs.put("Layer",data);}
@LiteralDataInput(abstrakt="Name of the parameter: OutputTableLabel. The name of the table to produce", defaultValue="extr_", title="The name of the table to produce", identifier = "OutputTableLabel", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setOutputTableLabel(String data) {inputs.put("OutputTableLabel",data);}
@LiteralDataInput(abstrakt="Name of the parameter: X. X coordinate", defaultValue="0", title="X coordinate", identifier = "X", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setX(Double data) {inputs.put("X",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: Y. Y coordinate", defaultValue="0", title="Y coordinate", identifier = "Y", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setY(Double data) {inputs.put("Y",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: TimeIndex. Time Index. The default is the first time indexed dataset", defaultValue="0", title="Time Index. The default is the first time indexed dataset", identifier = "TimeIndex", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setTimeIndex(Integer data) {inputs.put("TimeIndex",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: Resolution. Step for Z values", defaultValue="100", title="Step for Z values", identifier = "Resolution", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setResolution(Double data) {inputs.put("Resolution",""+data);}
@ComplexDataOutput(abstrakt="Name of the parameter: OutputTable1. Output table [a http link to a table in UTF-8 ecoding following this template: (TIMESERIES) http://goo.gl/DoW6fg]", title="Output table [a http link to a table in UTF-8 ecoding following this template: (TIMESERIES) http://goo.gl/DoW6fg]", identifier = "OutputTable1", binding = CsvFileDataBinding.class)	public GenericFileData getOutputTable1() {URL url=null;try {url = new URL((String) outputs.get("OutputTable1")); return new GenericFileData(url.openStream(),"text/csv");} catch (Exception e) {e.printStackTrace();return null;}}
@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }
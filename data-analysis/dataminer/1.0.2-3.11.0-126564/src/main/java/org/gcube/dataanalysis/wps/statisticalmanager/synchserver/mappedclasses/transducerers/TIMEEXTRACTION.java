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
@Algorithm(statusSupported=false, title="TIMEEXTRACTION", abstrakt="An algorithm to extract a time series of values associated to a geospatial features repository (e.g. NETCDF, ASC, GeoTiff files etc. ). The algorithm analyses the time series and automatically searches for hidden periodicities. It produces one chart of the time series, one table containing the time series values and possibly the spectrogram.", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.TIMEEXTRACTION", version = "1.1.0")
public class TIMEEXTRACTION extends AbstractEcologicalEngineMapper implements ITransducer{
@LiteralDataInput(abstrakt="Name of the parameter: Layer. Layer Title or UUID or HTTP link. E.g. the title or the UUID (preferred) of a layer indexed in the e-Infrastructure on GeoNetwork - You can retrieve it from GeoExplorer. Otherwise you can supply the direct HTTP link of the layer. The format will be guessed from the link. The default is GeoTiff. Supports several standards (NETCDF, WFS, WCS ASC, GeoTiff )", defaultValue="", title="Layer Title or UUID or HTTP link. E.g. the title or the UUID (preferred) of a layer indexed in the e-Infrastructure on GeoNetwork - You can retrieve it from GeoExplorer. Otherwise you can supply the direct HTTP link of the layer. The format will be guessed from the link. The default is GeoTiff. Supports several standards (NETCDF, WFS, WCS ASC, GeoTiff )", identifier = "Layer", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setLayer(String data) {inputs.put("Layer",data);}
@LiteralDataInput(abstrakt="Name of the parameter: OutputTableLabel. The name of the table to produce", defaultValue="extr_", title="The name of the table to produce", identifier = "OutputTableLabel", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setOutputTableLabel(String data) {inputs.put("OutputTableLabel",data);}
@LiteralDataInput(abstrakt="Name of the parameter: X. X coordinate", defaultValue="0", title="X coordinate", identifier = "X", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setX(Double data) {inputs.put("X",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: Y. Y coordinate", defaultValue="0", title="Y coordinate", identifier = "Y", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setY(Double data) {inputs.put("Y",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: Z. Value of Z. Default is 0, that means processing will be at surface level or at the first avaliable Z value in the layer", defaultValue="0", title="Value of Z. Default is 0, that means processing will be at surface level or at the first avaliable Z value in the layer", identifier = "Z", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setZ(Double data) {inputs.put("Z",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: Resolution. Extraction point resolution", defaultValue="0.5", title="Extraction point resolution", identifier = "Resolution", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setResolution(Double data) {inputs.put("Resolution",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: SamplingFreq. Sampling frequency in Hz. Leave it to -1 if unknown or under 1", defaultValue="-1", title="Sampling frequency in Hz. Leave it to -1 if unknown or under 1", identifier = "SamplingFreq", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setSamplingFreq(Integer data) {inputs.put("SamplingFreq",""+data);}
@ComplexDataOutput(abstrakt="Name of the parameter: OutputTable1. Output table [a http link to a table in UTF-8 ecoding following this template: (TIMESERIES) http://goo.gl/DoW6fg]", title="Output table [a http link to a table in UTF-8 ecoding following this template: (TIMESERIES) http://goo.gl/DoW6fg]", identifier = "OutputTable1", binding = CsvFileDataBinding.class)	public GenericFileData getOutputTable1() {URL url=null;try {url = new URL((String) outputs.get("OutputTable1")); return new GenericFileData(url.openStream(),"text/csv");} catch (Exception e) {e.printStackTrace();return null;}}
@LiteralDataOutput(abstrakt="Name of the parameter: Note. Note about the signal", title="Note about the signal", identifier = "Note", binding = LiteralStringBinding.class)	public String getNote() {return (String) outputs.get("Note");}
@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }
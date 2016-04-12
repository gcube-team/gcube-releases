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
@Algorithm(statusSupported=false, title="RASTER_DATA_PUBLISHER", abstrakt="This algorithm publishes a raster file as a maps or datasets in the e-Infrastructure. NetCDF-CF files are encouraged, as WMS and WCS maps will be produced using this format. For other types of files (GeoTiffs, ASC etc.) only the raw datasets will be published. The resulting map or dataset will be accessible via the VRE GeoExplorer by the VRE participants.", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.RASTER_DATA_PUBLISHER", version = "1.1.0")
public class RASTER_DATA_PUBLISHER extends AbstractEcologicalEngineMapper implements ITransducer{
@LiteralDataInput(abstrakt="Name of the parameter: DatasetTitle. Title of the geospatial dataset to be shown on GeoExplorer", defaultValue="Generic Raster Layer", title="Title of the geospatial dataset to be shown on GeoExplorer", identifier = "DatasetTitle", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setDatasetTitle(String data) {inputs.put("DatasetTitle",data);}
@LiteralDataInput(abstrakt="Name of the parameter: DatasetAbstract. Abstract defining the content, the references and usage policies", defaultValue="Abstract", title="Abstract defining the content, the references and usage policies", identifier = "DatasetAbstract", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setDatasetAbstract(String data) {inputs.put("DatasetAbstract",data);}
@LiteralDataInput(abstrakt="Name of the parameter: InnerLayerName. Name of the inner layer or band to be published as a Map (ignored for non-NetCDF files)", defaultValue="band_1", title="Name of the inner layer or band to be published as a Map (ignored for non-NetCDF files)", identifier = "InnerLayerName", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setInnerLayerName(String data) {inputs.put("InnerLayerName",data);}
@LiteralDataInput(abstrakt="Name of the parameter: FileNameOnInfra. Name of the file that will be created in the infrastructures", defaultValue="raster-1452703434486.nc", title="Name of the file that will be created in the infrastructures", identifier = "FileNameOnInfra", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setFileNameOnInfra(String data) {inputs.put("FileNameOnInfra",data);}
@ComplexDataInput(abstrakt="Name of the parameter: RasterFile. Raster dataset to process", title="Raster dataset to process", maxOccurs=1, minOccurs=1, identifier = "RasterFile", binding = D4ScienceDataInputBinding.class)	public void setRasterFile(GenericFileData file) {inputs.put("RasterFile",file);}
@LiteralDataInput(abstrakt="Name of the parameter: Topics. Topics to be attached to the published dataset. E.g. Biodiversity, D4Science, Environment, Weather [a sequence of values separated by | ] (format: String)", defaultValue="", title="Topics to be attached to the published dataset. E.g. Biodiversity, D4Science, Environment, Weather [a sequence of values separated by | ] (format: String)", identifier = "Topics", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setTopics(String data) {inputs.put("Topics",data);}
@LiteralDataInput(abstrakt="Name of the parameter: SpatialResolution. The resolution of the layer. For NetCDF file this is automatically estimated by data (leave -1)", defaultValue="-1d", title="The resolution of the layer. For NetCDF file this is automatically estimated by data (leave -1)", identifier = "SpatialResolution", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setSpatialResolution(Double data) {inputs.put("SpatialResolution",""+data);}

@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }
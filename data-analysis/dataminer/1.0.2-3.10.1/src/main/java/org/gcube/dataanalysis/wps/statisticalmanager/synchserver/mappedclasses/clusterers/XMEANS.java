package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers;
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
@Algorithm(statusSupported=false, title="XMEANS", abstrakt="A clustering algorithm for occurrence points that relies on the X-Means algorithm, i.e. an extended version of the K-Means algorithm improved by an Improve-Structure part. A Maximum of 4000 points is allowed.", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers.XMEANS", version = "1.1.0")
public class XMEANS extends AbstractEcologicalEngineMapper implements IClusterer{
@ComplexDataInput(abstrakt="Name of the parameter: OccurrencePointsTable. Occurrence Points Table. Max 4000 points [a http link to a table in UTF-8 encoding following this template: (GENERIC) A generic comma separated csv file in UTF-8 encoding]", title="Occurrence Points Table. Max 4000 points [a http link to a table in UTF-8 encoding following this template: (GENERIC) A generic comma separated csv file in UTF-8 encoding]", maxOccurs=1, minOccurs=1, identifier = "OccurrencePointsTable", binding = GenericFileDataBinding.class)	public void setOccurrencePointsTable(GenericFileData file) {inputs.put("OccurrencePointsTable",file);}
@LiteralDataInput(abstrakt="Name of the parameter: FeaturesColumnNames. column Names for the features [a sequence of names of columns from OccurrencePointsTable separated by | ]", defaultValue="", title="column Names for the features [a sequence of names of columns from OccurrencePointsTable separated by | ]", identifier = "FeaturesColumnNames", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setFeaturesColumnNames(String data) {inputs.put("FeaturesColumnNames",data);}
@LiteralDataInput(abstrakt="Name of the parameter: OccurrencePointsClusterLabel. table name of the resulting distribution", defaultValue="OccCluster_", title="table name of the resulting distribution", identifier = "OccurrencePointsClusterLabel", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setOccurrencePointsClusterLabel(String data) {inputs.put("OccurrencePointsClusterLabel",data);}
@LiteralDataInput(abstrakt="Name of the parameter: maxIterations. XMeans max number of overall iterations of the clustering learning", defaultValue="10", title="XMeans max number of overall iterations of the clustering learning", identifier = "maxIterations", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setmaxIterations(Integer data) {inputs.put("maxIterations",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: minClusters. minimum number of expected clusters", defaultValue="1", title="minimum number of expected clusters", identifier = "minClusters", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setminClusters(Integer data) {inputs.put("minClusters",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: maxClusters. maximum number of clusters to produce", defaultValue="50", title="maximum number of clusters to produce", identifier = "maxClusters", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setmaxClusters(Integer data) {inputs.put("maxClusters",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: min_points. number of points which define an outlier set", defaultValue="2", title="number of points which define an outlier set", identifier = "min_points", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setmin_points(Integer data) {inputs.put("min_points",""+data);}
@ComplexDataOutput(abstrakt="Name of the parameter: OutputTable. Output cluster table [a http link to a table in UTF-8 ecoding following this template: (CLUSTER) http://goo.gl/PnKhhb]", title="Output cluster table [a http link to a table in UTF-8 ecoding following this template: (CLUSTER) http://goo.gl/PnKhhb]", identifier = "OutputTable", binding = CsvFileDataBinding.class)	public GenericFileData getOutputTable() {URL url=null;try {url = new URL((String) outputs.get("OutputTable")); return new GenericFileData(url.openStream(),"text/csv");} catch (Exception e) {e.printStackTrace();return null;}}
@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }
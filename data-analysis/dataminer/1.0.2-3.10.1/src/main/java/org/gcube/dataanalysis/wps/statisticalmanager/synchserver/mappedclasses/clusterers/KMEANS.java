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
@Algorithm(statusSupported=false, title="KMEANS", abstrakt="A clustering algorithm for real valued vectors that relies on the k-means algorithm, i.e. a method aiming to partition n observations into k clusters in which each observation belongs to the cluster with the nearest mean, serving as a prototype of the cluster.  A Maximum of 4000 points is allowed.", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers.KMEANS", version = "1.1.0")
public class KMEANS extends AbstractEcologicalEngineMapper implements IClusterer{
@ComplexDataInput(abstrakt="Name of the parameter: OccurrencePointsTable. Occurrence Points Table. Max 4000 points [a http link to a table in UTF-8 encoding following this template: (GENERIC) A generic comma separated csv file in UTF-8 encoding]", title="Occurrence Points Table. Max 4000 points [a http link to a table in UTF-8 encoding following this template: (GENERIC) A generic comma separated csv file in UTF-8 encoding]", maxOccurs=1, minOccurs=1, identifier = "OccurrencePointsTable", binding = GenericFileDataBinding.class)	public void setOccurrencePointsTable(GenericFileData file) {inputs.put("OccurrencePointsTable",file);}
@LiteralDataInput(abstrakt="Name of the parameter: FeaturesColumnNames. column Names for the features [a sequence of names of columns from OccurrencePointsTable separated by | ]", defaultValue="", title="column Names for the features [a sequence of names of columns from OccurrencePointsTable separated by | ]", identifier = "FeaturesColumnNames", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setFeaturesColumnNames(String data) {inputs.put("FeaturesColumnNames",data);}
@LiteralDataInput(abstrakt="Name of the parameter: OccurrencePointsClusterLabel. table name of the resulting distribution", defaultValue="OccCluster_", title="table name of the resulting distribution", identifier = "OccurrencePointsClusterLabel", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setOccurrencePointsClusterLabel(String data) {inputs.put("OccurrencePointsClusterLabel",data);}
@LiteralDataInput(abstrakt="Name of the parameter: k. expected Number of Clusters", defaultValue="3", title="expected Number of Clusters", identifier = "k", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setk(Integer data) {inputs.put("k",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: max_runs. max runs of the clustering procedure", defaultValue="10", title="max runs of the clustering procedure", identifier = "max_runs", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setmax_runs(Integer data) {inputs.put("max_runs",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: max_optimization_steps. max number of internal optimization steps", defaultValue="5", title="max number of internal optimization steps", identifier = "max_optimization_steps", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setmax_optimization_steps(Integer data) {inputs.put("max_optimization_steps",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: min_points. number of points which define an outlier set", defaultValue="2", title="number of points which define an outlier set", identifier = "min_points", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setmin_points(Integer data) {inputs.put("min_points",""+data);}
@ComplexDataOutput(abstrakt="Name of the parameter: OutputTable. Output cluster table [a http link to a table in UTF-8 ecoding following this template: (CLUSTER) http://goo.gl/PnKhhb]", title="Output cluster table [a http link to a table in UTF-8 ecoding following this template: (CLUSTER) http://goo.gl/PnKhhb]", identifier = "OutputTable", binding = CsvFileDataBinding.class)	public GenericFileData getOutputTable() {URL url=null;try {url = new URL((String) outputs.get("OutputTable")); return new GenericFileData(url.openStream(),"text/csv");} catch (Exception e) {e.printStackTrace();return null;}}
@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }
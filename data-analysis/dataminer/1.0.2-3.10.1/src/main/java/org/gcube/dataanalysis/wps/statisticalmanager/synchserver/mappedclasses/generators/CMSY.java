package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.generators;
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
@Algorithm(statusSupported=false, title="CMSY", abstrakt="An algorithm to estimate the Maximum Sustainable Yield from a catch statistic. If also a Biomass trend is provided, MSY estimation is provided also with higher precision. The method has been developed by R. Froese, G. Coro, N. Demirel and K. Kleisner.", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.generators.CMSY", version = "1.1.0")
public class CMSY extends AbstractEcologicalEngineMapper implements IGenerator{
@LiteralDataInput(abstrakt="Name of the parameter: IDsFile. Http link to a file containing prior information about the stocks, in WKLife IV format. Example: http://goo.gl/9rg3qK", defaultValue="", title="Http link to a file containing prior information about the stocks, in WKLife IV format. Example: http://goo.gl/9rg3qK", identifier = "IDsFile", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setIDsFile(String data) {inputs.put("IDsFile",data);}
@LiteralDataInput(abstrakt="Name of the parameter: StocksFile. Http link to a file containing catch and biomass (or CPUE) trends , in WKLife IV format. Example: http://goo.gl/Mp2ZLY", defaultValue="", title="Http link to a file containing catch and biomass (or CPUE) trends , in WKLife IV format. Example: http://goo.gl/Mp2ZLY", identifier = "StocksFile", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setStocksFile(String data) {inputs.put("StocksFile",data);}
@LiteralDataInput(abstrakt="Name of the parameter: SelectedStock. The stock on which the procedure has to focus e.g. HLH_M07", defaultValue="", title="The stock on which the procedure has to focus e.g. HLH_M07", identifier = "SelectedStock", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setSelectedStock(String data) {inputs.put("SelectedStock",data);}
@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }
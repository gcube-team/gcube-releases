package gr.cite.geoanalytics.geospatialbackend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gr.cite.gaap.datatransferobjects.SeedGWCRequest;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeospatialServices {

    private  static final ObjectMapper mapper = new ObjectMapper();

    public InputStream convertGeojsonToShapefile(InputStream geojsonStream,String shapefileName) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(geojsonStream, writer, "UTF8");
        String geojson = writer.toString();

        String fileName = FilenameUtils.removeExtension(shapefileName);

        File shpFolder = new File(fileName + "_zip");
        shpFolder.setWritable(true);
        shpFolder.mkdir();
        File shpFile = new File(shpFolder.getAbsolutePath() + "/" + fileName + ".shp");
        shpFile.getParentFile().setWritable(true);
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", shpFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);
        InputStream in = IOUtils.toInputStream(geojson);
        int decimals = 15;
        GeometryJSON gjson = new GeometryJSON(decimals);
        FeatureJSON fjson = new FeatureJSON(gjson);

        FeatureCollection<SimpleFeatureType, SimpleFeature> fc = fjson.readFeatureCollection(in);
        fc.getSchema();

        try {
            WriteShapefile shpWriter = new WriteShapefile(shpFile);

            boolean created = shpWriter.writeFeatures(fc, FilenameUtils.removeExtension(shapefileName));

            if (created != true) {
                //   Utils.deleteDir(shpFolder)
                deleteDir(shpFolder);;
                return null;
            }

            WriteShapefile.pack(shpFolder.getAbsolutePath(), FilenameUtils.removeExtension(shapefileName) + ".zip");
            deleteDir(shpFolder);
            File shapefile = new File(FilenameUtils.removeExtension(shapefileName) + ".zip");
            InputStream shapeStream = new FileInputStream(shapefile);
            deleteDir(shapefile);
            return  shapeStream;
        } catch (FactoryException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteDir(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
    public static String createSeedRequest(String name, int threadNum, Bounds bounds, String crs, String type, int zoomStart, int zoomStop, String style) throws ParserConfigurationException, JsonProcessingException {
        String json ="";
        SeedGWCRequest seedGWCRequest = new SeedGWCRequest();
        seedGWCRequest.setName(name);
        seedGWCRequest.setBounds(bounds.getMinx(), bounds.getMiny(), bounds.getMaxx(), bounds.getMaxy());
        seedGWCRequest.setThreadCount(threadNum);
        seedGWCRequest.setGridSetId(crs);
        seedGWCRequest.setType(type);
        seedGWCRequest.setZoomStart(zoomStart);
        seedGWCRequest.setZoomStop(zoomStop);
        ObjectNode request = mapper.valueToTree(seedGWCRequest);
        ObjectNode  parameters = mapper.createObjectNode();
        ObjectNode  styleNode = mapper.createObjectNode();
        ObjectNode seedRequest = mapper.createObjectNode();
        seedRequest.set("seedRequest", request);
//        styleNode.put("STYLES", style);
//        parameters.set("entry", styleNode);
//        request.putPOJO("parameters", parameters);
//        JsonNode request = mapper.createObjectNode();
//        ((ObjectNode) request).put("")
        json = mapper.writeValueAsString(seedRequest);

        return json;
    }

    public static String createXmlSeedRequest(String name, int threadNum, Bounds bounds, String crs, String type, int zoomStart, int zoomEnd, String style) throws ParserConfigurationException {
        String xmlString = "";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("seedRequest");
        doc.appendChild(rootElement);

        // staff elements
        Element nameElement = doc.createElement("name");
        nameElement.appendChild(doc.createTextNode(name));
        rootElement.appendChild(nameElement);

        // firstname elements
        Element boundsElement = doc.createElement("bounds");
        rootElement.appendChild(boundsElement);

        Element coords = doc.createElement("coords");
        boundsElement.appendChild(coords);

        Element minX = doc.createElement("double");
        minX.appendChild(doc.createTextNode(String.valueOf(bounds.getMinx())));
        coords.appendChild(minX);

        Element maxX = doc.createElement("double");
        maxX.appendChild(doc.createTextNode(String.valueOf(bounds.getMaxx())));
        coords.appendChild(maxX);

        Element minY = doc.createElement("double");
        minY.appendChild(doc.createTextNode(String.valueOf(bounds.getMiny())));
        coords.appendChild(minY);

        Element maxY = doc.createElement("double");
        maxY.appendChild(doc.createTextNode(String.valueOf(bounds.getMaxy())));
        coords.appendChild(maxY);

        Element gridSetId = doc.createElement("gridSetId");
        gridSetId.appendChild(doc.createTextNode(crs));
        rootElement.appendChild(gridSetId);

        Element threadCount = doc.createElement("threadCount");
        threadCount.appendChild(doc.createTextNode(String.valueOf(threadNum)));
        rootElement.appendChild(threadCount);

        Element typeElement = doc.createElement("type");
        typeElement.appendChild(doc.createTextNode(type));
        rootElement.appendChild(typeElement);

        Element format = doc.createElement("format");
        format.appendChild(doc.createTextNode("image/png"));
        rootElement.appendChild(format);

        Element zoomStartElement = doc.createElement("zoomStart");
        zoomStartElement.appendChild(doc.createTextNode(String.valueOf(zoomStart)));
        rootElement.appendChild(zoomStartElement);

        Element zoomEndElement = doc.createElement("zoomStop");
        zoomEndElement.appendChild(doc.createTextNode(String.valueOf(zoomEnd)));
        rootElement.appendChild(zoomEndElement);

        /*****************     Parameters      *********************/
        Element parameters = doc.createElement("parameters");
        Element entry = doc.createElement("entry");

        Element styleString = doc.createElement("string");
        styleString.appendChild(doc.createTextNode("STYLES"));

        Element styleName = doc.createElement("string");
        styleName.appendChild(doc.createTextNode(style));

        entry.appendChild(styleString);
        entry.appendChild(styleName);
        parameters.appendChild(entry);
        rootElement.appendChild(parameters);

        xmlString = toString(doc);

        return xmlString;
    }

    public static String toString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

}

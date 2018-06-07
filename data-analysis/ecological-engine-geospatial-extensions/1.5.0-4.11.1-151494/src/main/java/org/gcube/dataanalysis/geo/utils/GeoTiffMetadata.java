package org.gcube.dataanalysis.geo.utils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class GeoTiffMetadata {

    public static void main(String[] args) throws Exception{
    	String[] argss={"C:/Users/coro/Dropbox/Public/geoserver-GetCoverage.image.geotiff"};
    	
        GeoTiffMetadata meta = new GeoTiffMetadata();
        int length = argss.length;
        for ( int i = 0; i < length; i++ )
            meta.readAndDisplayMetadata( argss[i] );

        
        
    }

    public void readAndDisplayMetadata( String fileName ) {
    	ImageInputStream iis =null;
    	try {

            File file = new File( fileName );
            iis = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {

                // pick the first available ImageReader
                ImageReader reader = readers.next();

                // attach source to the reader
                reader.setInput(iis, true);

                // read metadata of first image
                IIOMetadata metadata = reader.getImageMetadata(0);

                String[] names = metadata.getMetadataFormatNames();
                int length = names.length;
                for (int i = 0; i < length; i++) {
                   AnalysisLogger.getLogger().debug("Format name: " + names[ i ] );
                    displayMetadata(metadata.getAsTree(names[i]));
                }
            }
            
            AnalysisLogger.getLogger().debug("scalex "+xScale);
            AnalysisLogger.getLogger().debug("scaley "+yScale);
            AnalysisLogger.getLogger().debug("scalez "+zScale);
        }
        catch (Exception e) {

            e.printStackTrace();
        }finally{
        	if (iis!=null)
				try {
					iis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        }
    }

    void displayMetadata(Node root) {
        displayMetadata(root, 0);
    }

    void indent(int level) {
        for (int i = 0; i < level; i++)
            System.out.print("    ");
    }

    boolean capturexyz=false;
   public double xScale=-1;
   public double yScale=-1;
   public double zScale=-1;
    
    void displayMetadata(Node node, int level) {
        // print open tag of element
        indent(level);
        System.out.print("<" + node.getNodeName());
        NamedNodeMap map = node.getAttributes();
        if (map != null) {

            // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                if (name.equalsIgnoreCase("name")&&value.equalsIgnoreCase("ModelPixelScaleTag"))
                	capturexyz=true;
                else if (name.equalsIgnoreCase("name"))
                	capturexyz=false;
                
                if (capturexyz && name.equalsIgnoreCase("value")){
                	if (xScale<0)
                		xScale=Double.parseDouble(value);
                	else if (yScale<0)
                		yScale=Double.parseDouble(value);
                	else if (zScale<0)
                		zScale=Double.parseDouble(value);
                }
                System.out.print(" " + attr.getNodeName() +
                                 "=\"" + attr.getNodeValue() + "\"");
            }
        }

        Node child = node.getFirstChild();
        if (child == null) {
            // no children, so close element and return
            AnalysisLogger.getLogger().debug("/>");
            return;
        }

        // children, so close current tag
        AnalysisLogger.getLogger().debug(">");
        while (child != null) {
            // print children recursively
            displayMetadata(child, level + 1);
            child = child.getNextSibling();
        }

        // print close tag of element
        indent(level);
        AnalysisLogger.getLogger().debug("</" + node.getNodeName() + ">");
    }
}

package gr.cite.gaap.utilities;

import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
 
public class PresentationConfigXMLHandler 
{
 
    public static String marshal(SystemPresentationConfig pc)
            throws IOException, JAXBException 
    {
        JAXBContext context;
        StringWriter sw = new StringWriter();
        context = JAXBContext.newInstance(SystemPresentationConfig.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty("com.sun.xml.bind.characterEscapeHandler",
            new CharacterEscapeHandler() 
        	{
                /*@Override*/
                public void escape(char[] ch, int start, int length,
                        boolean isAttVal, Writer writer)
                        throws IOException {
                    writer.write(ch, start, length);
                }
            });
        m.marshal(pc, sw);
        return sw.toString();
    }
 
    public static SystemPresentationConfig unmarshal(String config) throws JAXBException 
    {
        SystemPresentationConfig pc = null;
        JAXBContext context = JAXBContext.newInstance(SystemPresentationConfig.class);
        Unmarshaller um = context.createUnmarshaller();
        pc = (SystemPresentationConfig) um.unmarshal(new StringReader(config));
 
        return pc;
    }
}
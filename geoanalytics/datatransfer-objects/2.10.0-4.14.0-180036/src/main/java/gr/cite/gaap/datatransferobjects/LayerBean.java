package gr.cite.gaap.datatransferobjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "layer")
@XmlAccessorType(XmlAccessType.FIELD)
public class LayerBean {

    @XmlElement
    private String name;

    @XmlElement
    private String atom;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
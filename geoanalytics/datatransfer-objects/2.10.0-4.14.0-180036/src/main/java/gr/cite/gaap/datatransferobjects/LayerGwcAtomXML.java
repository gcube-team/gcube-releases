package gr.cite.gaap.datatransferobjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "layers")
@XmlAccessorType(XmlAccessType.FIELD)
public class LayerGwcAtomXML {

    @XmlElement(name = "layer")
    private List<LayerBean> layers;

    public List<LayerBean> getLayers() {
        return layers;
    }

    public void setLayers(List<LayerBean> layers) {
        this.layers = layers;
    }



}

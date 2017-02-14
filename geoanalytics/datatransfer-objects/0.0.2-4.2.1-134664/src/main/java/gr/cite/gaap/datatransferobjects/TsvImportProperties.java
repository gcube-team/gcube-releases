package gr.cite.gaap.datatransferobjects;

public class TsvImportProperties {

	private String newLayerName;
	private String templateLayerName;

	public String getNewLayerName() {
		return newLayerName;
	}

	public void setNewLayerName(String newLayerName) {
		this.newLayerName = newLayerName;
	}

	public String getTemplateLayerName() {
		return templateLayerName;
	}

	public void setTemplateLayerName(String templateLayerName) {
		this.templateLayerName = templateLayerName;
	}
	
	public void print(){
		System.out.println("newLayerName = " + newLayerName);
		System.out.println("templateLayerName = " + templateLayerName);
	}
}

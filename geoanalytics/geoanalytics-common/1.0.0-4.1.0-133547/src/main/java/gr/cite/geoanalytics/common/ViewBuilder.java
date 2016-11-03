package gr.cite.geoanalytics.common;

import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

public interface ViewBuilder {
	public ViewBuilder forShape(Shape shape) throws Exception;
	public ViewBuilder forIdentity(String identity, String identityName) throws Exception;
	public ViewBuilder withAttribute(String key, ShapeAttributeDataType value);
	public ViewBuilder createViewStatement() throws Exception;
	public String getViewStatement() throws Exception;
	public void removerViewIfExists() throws Exception;
	public void execute() throws Exception;
}

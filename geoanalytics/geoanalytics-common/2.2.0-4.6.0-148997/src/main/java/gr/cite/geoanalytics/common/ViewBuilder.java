package gr.cite.geoanalytics.common;

import java.util.List;

import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

public interface ViewBuilder {
	public ViewBuilder forShape(Shape shape) throws Exception;
	public ViewBuilder forShapes(List<Shape> shapes) throws Exception;
	public ViewBuilder withAttribute(String key, ShapeAttributeDataType value);
	public ViewBuilder createViewStatement() throws Exception;
	public ViewBuilder removeViewStatement() throws Exception;
	
	public String getViewStatement() throws Exception;
	public String removeViewIfExists() throws Exception;
	public boolean execute(String gosEndpoint) throws Exception;
	public ViewBuilder forIdentity(String identity) throws Exception;
}

package org.gcube.common.geoserverinterface.engine;

import static org.geotoolkit.style.StyleConstants.DEFAULT_DESCRIPTION;
import static org.geotoolkit.style.StyleConstants.DEFAULT_DISPLACEMENT;
import static org.geotoolkit.style.StyleConstants.LITERAL_ZERO_FLOAT;

import java.awt.Color;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.measure.unit.NonSI;
import javax.xml.bind.JAXBException;

import org.gcube.common.geoserverinterface.engine.Range.Condition;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.sld.xml.Specification;
import org.geotoolkit.sld.xml.XMLUtilities;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.style.Style;




public class MakeStyle {

	
	
	protected static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
	protected static final MutableSLDFactory SLDF = new DefaultSLDFactory();
	protected static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
			new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));	
	protected static XMLUtilities utils=new XMLUtilities();
	

	public static String createStyle(String nameStyle, String attributeName, int maxClasses, Color c1, Color c2, Class typeValue, Object maxValue, Object minValue) throws Exception{
		return createStyle(nameStyle, attributeName, maxClasses, c1, c2, typeValue, maxValue, minValue, true); 
	}

	public static String createStyleLog(String nameStyle, String attributeName, int maxClasses, Color c1, Color c2, Class typeValue, Object maxValue, Object minValue) throws Exception{
		return createStyle(nameStyle, attributeName, maxClasses, c1, c2, typeValue, maxValue, minValue, false); 
	}
	
	
	public static String createStyleScatterColors(String nameStyle, String attributeName, int nClasses,Class typeValue, Object maxValue, Object minValue) throws Exception {
		return createStyleScatterColors(nameStyle, attributeName, nClasses, typeValue, maxValue, minValue, true); 
	}
	public static String createStyleLogScatterColors(String nameStyle, String attributeName, int nClasses,Class typeValue, Object maxValue, Object minValue) throws Exception {
		return createStyleScatterColors(nameStyle, attributeName, nClasses, typeValue, maxValue, minValue, false); 
	}
	
	
	
	public static String createStyle(String nameStyle, String attributeName, ArrayList<ClassStyleDef> classes, Color c1, Color c2) throws Exception {
		if (classes.size() <= 0)
			throw new Exception("Invalid number of classes!!");
		
		
		MutableStyle style=SF.style();
		MutableFeatureTypeStyle fts=SF.featureTypeStyle();
		
		//Setting colors
		
		ArrayList<Color> colors=scatterColor(classes.size());
		
		for(int i=0;i<classes.size();i++){
			ClassStyleDef classStyle=classes.get(i);
			fts.rules().add(makeRule(new Range(attributeName, colors.get(i), classStyle.getFrom(), classStyle.getTo(), Condition.BETWEEN)));
		}
		
		style.featureTypeStyles().add(fts);
		style.setName(nameStyle);	
		
		return marshall(style);
	}
	
	private static String createStyle(String nameStyle, String attributeName, int maxClasses, Color c1, Color c2, Class typeValue, Object maxValue, Object minValue,boolean linear)throws Exception{
		if (maxClasses <= 0)
			throw new Exception("Invalid number of classes!!");
		
		
		MutableStyle style=SF.style();
		MutableFeatureTypeStyle fts=SF.featureTypeStyle();
		
		ArrayList<Range> ranges=getRanges(typeValue,maxClasses,maxValue,minValue,attributeName,linear);
		
		//Setting colors
		
		ArrayList<Color> colors=gradientColors(ranges.size(),c1,c2);
		for(int i=0;i<ranges.size();i++)ranges.get(i).setToAssignColor(colors.get(i));
		
		for(Range r:ranges){
			fts.rules().add(makeRule(r));
		}
		
		style.featureTypeStyles().add(fts);
		style.setName(nameStyle);	
		
		return marshall(style);
	}
	
	
	private static String createStyleScatterColors(String nameStyle, String attributeName, int maxClasses, Class typeValue, Object maxValue, Object minValue,boolean linear)throws Exception{
		if (maxClasses <= 0)
			throw new Exception("Invalid number of classes!!");
		
		
		MutableStyle style=SF.style();
		MutableFeatureTypeStyle fts=SF.featureTypeStyle();
		
		ArrayList<Range> ranges=getRanges(typeValue,maxClasses,maxValue,minValue,attributeName,linear);
		
		//Setting colors
		
		ArrayList<Color> colors=scatterColor(ranges.size());
		for(int i=0;i<ranges.size();i++)ranges.get(i).setToAssignColor(colors.get(i));
		
		for(Range r:ranges){
			fts.rules().add(makeRule(r));
		}
		
		style.featureTypeStyles().add(fts);
		style.setName(nameStyle);	
		
		return marshall(style);
	}
	
	
	private static String marshall(Style toMarshal) throws JAXBException{
		StringWriter writer=new StringWriter();
		utils.writeStyle(writer, toMarshal, Specification.StyledLayerDescriptor.V_1_0_0);
		return writer.toString().replaceAll("<([a-zA-Z][a-zA-Z0-9:]*)[^>]*>\\s*</\\1>", ""); //Erase all empty tags 
	}
	
	private static MutableRule makeRule(Range r){
		MutableRule toReturn=SF.rule();
		toReturn.setName(r.getToFilterProperty()+" in ["+r.getMin()+" , "+r.getMax()+")");		
		switch(r.getCondition()){
		case BETWEEN :	toReturn.setFilter(FF.and( // property => min AND property < max
				FF.greaterOrEqual(FF.property(r.getToFilterProperty()), FF.literal(r.getMin())),
				FF.less(FF.property(r.getToFilterProperty()), FF.literal(r.getMax()))));
		break;
		case GREATER_THEN_MIN : toReturn.setFilter(// property => min 
				FF.greaterOrEqual(FF.property(r.getToFilterProperty()), FF.literal(r.getMin())));
		break;
		case UP_TO_MAX : toReturn.setFilter(// property < max								
				FF.less(FF.property(r.getToFilterProperty()), FF.literal(r.getMax())));
		break;
		}
		
		
		toReturn.symbolizers().add(SF.polygonSymbolizer(toReturn.getName(),"the_geom",DEFAULT_DESCRIPTION,NonSI.PIXEL,null,SF.fill(r.getToAssignColor()),DEFAULT_DISPLACEMENT,LITERAL_ZERO_FLOAT));
		return toReturn;
	}

	
	
	private static ArrayList<Range> getRanges(Class typeValue,int maxClasses, Object maxValue, Object minValue, String attributeName, boolean linear) throws Exception{
		//Check class coherence 
		if(maxValue.getClass()!=minValue.getClass()) throw new Exception("Min ("+minValue.getClass()+")and Max ("+maxValue.getClass()+") value must be of same class");
		boolean integerRanges=(typeValue.isAssignableFrom(Integer.class));

		// Get double values
		Double dMax;
		Double dMin;
		if(maxValue instanceof Double){
			dMax=(Double) maxValue;
			dMin=(Double) minValue;
		}else if(maxValue instanceof Float){
			dMax=new Double((Float) maxValue);
			dMin=new Double((Float) minValue);
		}else if(maxValue instanceof Integer){
			dMax=(Integer)maxValue*1d;
			dMin=(Integer)minValue*1d;
		}else if(maxValue instanceof String){
			dMax=Double.parseDouble((String)maxValue);
			dMin=Double.parseDouble((String)minValue);
		}else throw new Exception ("Unable to handle range values class "+maxValue.getClass());

		if(dMax.compareTo(dMin)<0) throw new Exception("Specified Range ["+dMin+" , "+dMax+") is invalid");

		
		//Check integer ranges -> can be less then max Classes
		ArrayList<Range> toReturn=new ArrayList<Range>();
		

		if(linear){ //Linear interpolation
			double distance=Math.abs(dMin-dMax);
			double step=distance/maxClasses;
			if(integerRanges&&step<1)step=1;
			
			Double toInsertMin=roundDecimal(dMin,integerRanges?0:2);
			Double toInsertMax=roundDecimal(toInsertMin+step,integerRanges?0:2);
			while(dMax.compareTo(toInsertMax)>=0){ //While in total range			
				//InsertRange
				if(integerRanges)
					toReturn.add(new Range(attributeName,Color.RED, toInsertMin.intValue(),toInsertMax.intValue(),Condition.BETWEEN));
				else
					toReturn.add(new Range(attributeName,Color.RED, toInsertMin,toInsertMax,Condition.BETWEEN));
				
				toInsertMin=toInsertMax;
				toInsertMax=roundDecimal(toInsertMin+step,integerRanges?0:2);
			}
			if(!dMax.equals(toInsertMin)){
				if(integerRanges)
					toReturn.add(new Range(attributeName,Color.RED, toInsertMin.intValue(),dMax.intValue(),Condition.BETWEEN));
				else
					toReturn.add(new Range(attributeName,Color.RED, toInsertMin,dMax,Condition.BETWEEN));
			}
		}else { // Logarithmic interpolation
			Double[] logSub=logSubdivision(dMin, dMax, maxClasses);
			for(int i =0;i<logSub.length;i++){
				Double upperBound=(i==logSub.length-1?dMax:logSub[i+1]);
				if(dMax.compareTo(upperBound)<=0) upperBound=roundDecimal(dMax,integerRanges?0:2);
				else upperBound=roundDecimal(upperBound,integerRanges?0:2);
				Double lowerBound=roundDecimal(logSub[i],integerRanges?0:2);
				if(integerRanges)
					toReturn.add(new Range(attributeName,Color.RED,lowerBound.intValue(),upperBound.intValue(),Condition.BETWEEN));
				else 
					toReturn.add(new Range(attributeName,Color.RED,lowerBound,upperBound,Condition.BETWEEN));
			}
		}
		
		
		
		return toReturn;
	}


	private static ArrayList<Color> gradientColors(int nColors, Color c1, Color c2) {
		// a linear gradient.
		ArrayList<Color> colors = new ArrayList<Color>();
		for (int i = 0; i < nColors; i++) {
			float ratio = (float) i / (float) nColors;
			int red = (int) (c2.getRed() * ratio + c1.getRed() * (1 - ratio));
			int green = (int) (c2.getGreen() * ratio + c1.getGreen() * (1 - ratio));
			int blue = (int) (c2.getBlue() * ratio + c1.getBlue() * (1 - ratio));
			colors.add(new Color(red, green, blue));
		}

		return colors;
	}




	
	private static Double[] logSubdivision(double start, double end, int numberOfParts) {

		if (end <= start)
			return null;

		double logStart = Math.log(start);
		double logEnd = Math.log(end);
		double difference = logEnd - logStart;
		double step = 0;
		if (numberOfParts > 0)
			step = (difference / (double) numberOfParts);
		// double [] points = new double[numberOfParts+1];
		Double[] linearpoints = new Double[numberOfParts + 1];

		for (int i = 0; i < numberOfParts + 1; i++) {

			// points[i] = logStart+(i*step);

			linearpoints[i] = Math.exp(logStart + (i * step));
			if (linearpoints[i] < 0.011)
				linearpoints[i] = 0.0;
		}

		return linearpoints;
	}

	public static ArrayList<Color> scatterColor(int nColors) {

		ArrayList<Color> colors = new ArrayList<Color>();
		float saturation = 1;
		float brightness = 1;
		for (int i = 0; i < nColors; i++) {
			float ratio = (((float) i)*1.5f) / (float) nColors;
			if (i%10==0)
				brightness = (float) Math.max(0.1,brightness-0.1);
//			else
//				saturation = (float) Math.max(0.1,saturation-0.1);
			
//			System.out.println("ratio degrees "+ratio);
			int rgb = Color.HSBtoRGB(ratio, brightness, saturation);
			Color color = new Color(rgb);
			colors.add(color);
		}

		return colors;
	}

	public static int HexToR(String h) {

		return Integer.parseInt(cutHex(h).substring(0, 2), 16);
	}

	public static int HexToG(String h) {

		return Integer.parseInt(cutHex(h).substring(2, 4), 16);
	}

	public static int HexToB(String h) {

		return Integer.parseInt(cutHex(h).substring(4, 6), 16);
	}

	public static String cutHex(String h) {

		return h.substring(0, 1).contentEquals("#") ? h.substring(1) : h;
	}

	public static double round(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	// rounds to the xth decimal position
	public static double roundDecimal(double number, int decimalposition) {

		double n = (double) Math.round(number * Math.pow(10.00, decimalposition)) / Math.pow(10.00, decimalposition);
		return n;
	}
	
	
	
	/**
	 * Create a rendering style to display features from the given feature source by matching unique values of the specified feature attribute to colours
	 * 
	 * @param reader
	 *            the feature source
	 * @return a new Style instance
	 * @throws Exception
	 */

	public class ClassStyleDef {
		public ClassStyleDef() {
			super();
			// TODO Auto-generated constructor stub
		}

		/**
		 * @uml.property name="from"
		 */
		Object from = 0;
		/**
		 * @uml.property name="to"
		 */
		Object to = 0;

		public ClassStyleDef(Object from, Object to) {
			super();
			this.from = from;
			this.to = to;
		}

		/**
		 * @return
		 * @uml.property name="from"
		 */
		public Object getFrom() {
			return from;
		}

		/**
		 * @return
		 * @uml.property name="to"
		 */
		public Object getTo() {
			return to;
		}

		/**
		 * @param from
		 * @uml.property name="from"
		 */
		public void setFrom(Object from) {
			this.from = from;
		}

		/**
		 * @param to
		 * @uml.property name="to"
		 */
		public void setTo(Object to) {
			this.to = to;
		}
	}
}

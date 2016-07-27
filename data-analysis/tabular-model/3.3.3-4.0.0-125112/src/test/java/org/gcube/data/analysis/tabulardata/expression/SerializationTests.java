package org.gcube.data.analysis.tabulardata.expression;

import java.util.Date;

import javax.xml.bind.JAXBException;

import org.gcube.data.analysis.tabulardata.SerializationTester;
import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Avg;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Count;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Max;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Min;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Sum;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Addition;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Division;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Exponentiation;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Modulus;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Multiplication;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Subtraction;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotGreater;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotLess;
import org.gcube.data.analysis.tabulardata.expression.composite.condtional.Case;
import org.gcube.data.analysis.tabulardata.expression.composite.condtional.Case.WhenConstruct;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Concat;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Length;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Lower;
import org.gcube.data.analysis.tabulardata.expression.composite.text.MD5;
import org.gcube.data.analysis.tabulardata.expression.composite.text.RepeatText;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringPosition;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextBeginsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextContains;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextEndsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchPosixRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Trim;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Upper;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNotNull;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNull;
import org.gcube.data.analysis.tabulardata.expression.logical.ValueIsIn;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.junit.Test;

public class SerializationTests extends SerializationTester {

	private ColumnReference targetColumnReference = new ColumnReference(new TableId(10), new ColumnLocalId("test"),new TextType(5));
	
	private ColumnReferencePlaceholder columnReferencePlaceholder = new ColumnReferencePlaceholder(new BooleanType(),"myRef","My Reference");

	
	@Test
	public final void testComposites() throws JAXBException,MalformedExpressionException {

//		// Other expression
//		Expression isNull = createIsNull();
//		Expression columnIsIn = createValueIsIn();
//		Expression isNotNull= createIsNotNull();
//		
//		
//		// Text expression
//		Expression textContains = createTextContains();
//		
//		Expression textMatchRegexp = createTextMatchSQLRegexp();
//
//		// Comparable
//		Expression equals = createEquals();
//		Expression greaterThan = createGreaterThan();
//		Expression lessThan = createLessThan();
//		Expression notEquals = createNotEquals();
//
//		// Composite
//		Expression and = new And(textContains, isNull, columnIsIn, textContains,isNotNull);
//		Expression or = new Or(and, textMatchRegexp, equals, greaterThan, lessThan, notEquals);
//		Expression not = new Not(or);
		roundTripTest(ExpressionExample.getExample());
		
	}

	
	@Test
	public final void testTypedColumnReference()throws JAXBException{
		roundTripTest(targetColumnReference);
	}
	
	@Test
	public final void testColumnReferencePlaceholder()throws JAXBException{
		roundTripTest(columnReferencePlaceholder);
	}
	
	@Test
	public final void testIsNotNull() throws JAXBException {
		roundTripTest(createIsNotNull());
	}

	@Test
	public final void testIsNull() throws JAXBException {
		roundTripTest(createIsNull());
	}

	@Test
	public final void testColumnIsIn() throws JAXBException {
		roundTripTest(createValueIsIn());
	}

	@Test
	public final void testTextContains() throws JAXBException {
		roundTripTest(createTextContains());
	}


	@Test
	public final void testTextMatchSQLRegexp() throws JAXBException {
		roundTripTest(createTextMatchSQLRegexp());
	}
	
	@Test
	public final void testTextMatchPosixRegexp() throws JAXBException {
		roundTripTest(createTextMatchPosixRegexp());
	}

	@Test
	public final void testEquals() throws JAXBException {
		roundTripTest(createEquals());
	}

	@Test
	public final void testGreaterThan() throws JAXBException {
		roundTripTest(createGreaterThan());
	}

	@Test
	public final void testLessThan() throws JAXBException {
		roundTripTest(createLessThan());
	}

	@Test
	public final void testNotEquals() throws JAXBException {
		roundTripTest(createNotEquals());
	}

	
	@Test
	public final void testAddition() throws JAXBException {
		roundTripTest(createAddition());
	}
	
	@Test
	public final void testSubtraction() throws JAXBException {
		roundTripTest(createSubtraction());
	}
	
	@Test
	public final void testExponentiation() throws JAXBException {
		roundTripTest(createExponentiation());
	}
	
	@Test
	public final void testMultiplication() throws JAXBException {
		roundTripTest(createMultiplication());
	}
	
	@Test
	public final void testDivision() throws JAXBException {
		roundTripTest(createDivision());
	}
	
	@Test
	public final void testModulus() throws JAXBException {
		roundTripTest(createModulus());
	}
	
	@Test
	public final void testGreaterOrEquals() throws JAXBException {
		roundTripTest(createGreaterOrEquals());
	}
	
	@Test
	public final void testLessOrEquals() throws JAXBException {
		roundTripTest(createLessOrEquals());
	}
	
	@Test
	public final void testNotGreater() throws JAXBException {
		roundTripTest(createNotGreater());
	}
	
	@Test
	public final void testNotLess() throws JAXBException {
		roundTripTest(createNotLess());
	}
	
	@Test
	public final void testTextBegins() throws JAXBException {
		roundTripTest(createTextBeginsWith());
	}
	
	@Test
	public final void testTextEndsWith() throws JAXBException {
		roundTripTest(createTextEndsWith());
	}
	
	@Test
	public final void testTextReplaceRegex() throws JAXBException {
		roundTripTest(createTextReplaceMatchingRegex());
	}
	
	
	@Test
	public final void testSubstringByRegexp() throws JAXBException{
		roundTripTest(createSubstringByREgexp());
	}
	
	@Test
	public final void testSubstringByIndex() throws JAXBException{
		roundTripTest(createSubstringByIndex());
	}
	
	@Test
	public final void testSubstringPosition() throws JAXBException{
		roundTripTest(createSubstringPosition());
	}
	
	@Test
	public final void testTrim() throws JAXBException{
		roundTripTest(createTrim());
	}
	
	@Test
	public final void testLength() throws JAXBException{
		roundTripTest(createLength());
	}
	
	@Test
	public final void testUpper() throws JAXBException{
		roundTripTest(createUpper());
	}
	
	@Test
	public final void testLower() throws JAXBException{
		roundTripTest(createLower());
	}
	
	@Test
	public final void testConcat()throws JAXBException{
		roundTripTest(createConcat());
	}
	
	@Test
	public final void testCast()throws JAXBException{
		roundTripTest(createCast());
	}
	
	@Test
	public final void testRepeat()throws JAXBException{
		roundTripTest(createRepeatText());
	}
	
	@Test
	public final void testCase()throws JAXBException{
		roundTripTest(createCase());
	}
	
	@Test
	public final void testMD5()throws JAXBException{
		roundTripTest(createMD5());
	}
	
	
	@Test
	public final void testAggregation()throws JAXBException{
		roundTripTest(new Avg(new TDInteger(10)));
		roundTripTest(new Count(new TDInteger(10)));
		roundTripTest(new Max(new TDInteger(10)));
		roundTripTest(new Min(new TDInteger(10)));
		roundTripTest(new Sum(new TDInteger(10)));
	}
	
	@Test
	public final void testMerge() throws JAXBException{
		ColumnReference ref=new ColumnReference(new TableId(10), new ColumnLocalId("bla"));
		roundTripTest(new Concat(ref, new Concat(new TDText("--"),ref)));
	}
	
	
	// ******************** Expression Creation
	
	//Arithmetic
	
	private Addition createAddition(){
		return new Addition(targetColumnReference, new TDInteger(10));
	}
	
	private Subtraction createSubtraction(){
		return new Subtraction(targetColumnReference, new TDInteger(10));
	}
	
	private Exponentiation createExponentiation(){
		return new Exponentiation(targetColumnReference, new TDInteger(10));
	}
	
	private Multiplication createMultiplication(){
		return new Multiplication(columnReferencePlaceholder, new TDInteger(10));
	}
	
	private Division createDivision(){
		return new Division(targetColumnReference, new TDInteger(10));
	}
		
	private Modulus createModulus(){
		return new Modulus(targetColumnReference, new TDInteger(10));
	}
	
	// Comparison
	
	private Equals createEquals() {
		return new Equals(targetColumnReference, new TDDate(new Date()));
	}
	
	private GreaterThan createGreaterThan() {
		return new GreaterThan(targetColumnReference, new TDInteger(5));
	}
	
	private LessThan createLessThan() {
		return new LessThan(targetColumnReference, new TDNumeric(5.1d));
	}
	
	private GreaterOrEquals createGreaterOrEquals() {
		return new GreaterOrEquals(targetColumnReference, new TDNumeric(5.1d));
	}
	
	private LessOrEquals createLessOrEquals() {
		return new LessOrEquals(targetColumnReference, new TDNumeric(5.1d));
	}
	
	private NotEquals createNotEquals() {
		return new NotEquals(targetColumnReference, new TDBoolean(false));
	}
	
	private NotGreater createNotGreater() {
		return new NotGreater(targetColumnReference, new TDBoolean(false));
	}
	
	private NotLess createNotLess() {
		return new NotLess(targetColumnReference, new TDBoolean(false));
	}
	
	
	// Logical
	
	private IsNull createIsNull() {
		return new IsNull(targetColumnReference);
	}
	
	private IsNotNull createIsNotNull() {
		return new IsNotNull(targetColumnReference);
	}
	
	private ValueIsIn createValueIsIn() {
		return new ValueIsIn(targetColumnReference, new ExternalReferenceExpression(targetColumnReference, createIsNotNull()));
	}

	
	// TEXT
	
	private TextBeginsWith createTextBeginsWith(){
		return new TextBeginsWith(targetColumnReference, new TDText("test"));
	}
	
	private TextContains createTextContains() {
		return new TextContains(targetColumnReference,new TDText("test"));
	}
	
	
	private TextEndsWith createTextEndsWith(){
		return new TextEndsWith(targetColumnReference,new TDText("test"));
	}
	
	private TextMatchSQLRegexp createTextMatchSQLRegexp() {
		return new TextMatchSQLRegexp(targetColumnReference, new TDText("[a-b]*"));
	}
	
	private TextMatchPosixRegexp createTextMatchPosixRegexp() {
		return new TextMatchPosixRegexp(targetColumnReference, new TDText("[a-b]*"));
	}
	
	private TextReplaceMatchingRegex createTextReplaceMatchingRegex(){
		return new TextReplaceMatchingRegex(targetColumnReference, new TDText("[a-b]*"),new TDText("bla!"));
	}
	
	private SubstringByRegex createSubstringByREgexp(){
		return new SubstringByRegex(targetColumnReference, new TDText("[a-b]*"));
	}
	
	private SubstringByIndex createSubstringByIndex(){
		return new SubstringByIndex(new TDText("blabla"), new TDInteger(3), new TDInteger(7));
	}
	
	private SubstringPosition createSubstringPosition(){
		return new SubstringPosition(new TDText("blabla"), new TDText("bla"));
	}
	
	private Trim createTrim(){
		return new Trim(new TDText("blabla "));
	}
	
	private Length createLength(){
		return new Length(new TDText("abl"));
	}
	
	private Upper createUpper(){
		return new Upper(new TDText("bla"));
	}
	
	private Lower createLower(){
		return new Lower(new TDText("BLA"));
	}
	
	private Concat createConcat(){
		return new Concat(new TDText("bla"),new TDText("BLA"));
	}
	
	private Cast createCast(){
		return new Cast(new TDInteger(5),new TextType(12));
	}
	
	private Case createCase(){
		return new Case(new WhenConstruct(createIsNotNull(), createCast()));
	}
	
	private RepeatText createRepeatText(){
		return new RepeatText(new TDText("bla"), new TDInteger(5));
	}
	
	private MD5 createMD5(){
		return new MD5(new TDText("asdfhkvb"));
	}
	
	
}

package org.gcube.data.trees.patterns;

import static org.gcube.data.trees.data.Nodes.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.gcube.data.trees.constraints.After;
import org.gcube.data.trees.constraints.AfterDate;
import org.gcube.data.trees.constraints.All;
import org.gcube.data.trees.constraints.AnyValue;
import org.gcube.data.trees.constraints.Before;
import org.gcube.data.trees.constraints.BeforeDate;
import org.gcube.data.trees.constraints.Constraint;
import org.gcube.data.trees.constraints.Either;
import org.gcube.data.trees.constraints.Less;
import org.gcube.data.trees.constraints.Match;
import org.gcube.data.trees.constraints.More;
import org.gcube.data.trees.constraints.Not;
import org.gcube.data.trees.constraints.Same;
import org.gcube.data.trees.data.InnerNode;


/**
 * Defines a simple EDSL for {@link Pattern}s and other related facilities.
 * 
 * @author Fabio Simeoni
 *
 */
public class Patterns {
	
	private static JAXBContext context;
	
	static {
		try {
			context = JAXBContext.newInstance(TreePattern.class);
		}
		catch(Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	/**
	 * Returns a {@link Marshaller} for {@link Pattern}s.
	 * @return the {@link Marshaller}
	 * @throws JAXBException if the {@link Marshaller} could not be returned.
	 */
	public static Marshaller getMarshaller() throws JAXBException {return context.createMarshaller();}
	
	/**
	 * Returns an {@link Unmarshaller} for {@link Pattern}s.
	 * @return the {@link Unmarshaller}
	 * @throws JAXBException if the {@link Unmarshaller} could not be returned.
	 */
	public static Unmarshaller getUnMarshaller() throws JAXBException {return context.createUnmarshaller();}

	
	//cross-type
	
	/**
	 * A wildcard for labels.
	 */
	public static final QName any=new QName(".*",".*");
	
	/**
	 * A marker for condition edges.
	 */
	public static final boolean C = true;
	
	/**
	 * The {@link AnyPattern}.
	 */
	public static final AnyPattern any() {
		return new AnyPattern();
	}
	
	/**
	 * Clones a {@link Pattern}.
	 * @param pattern the pattern to clone
	 * @return the pattern's clone
	 * @throws Exception if the pattern could not be cloned
	 */
	public static final Pattern clone(Pattern pattern) throws Exception
	{
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(s);
		os.writeObject(pattern);
		os.close();
		ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(s.toByteArray()));
		return (Pattern) is.readObject();
	}
	
	/**
	 * Clones an {@link EdgePattern}.
	 * @param pattern the pattern to clone
	 * @return the pattern's clone
	 * @throws Exception if the pattern could not be cloned
	 */
	public static final EdgePattern clone(EdgePattern pattern) throws Exception
	{
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(s);
		os.writeObject(pattern);
		os.close();
		ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(s.toByteArray()));
		return (EdgePattern) is.readObject();
	}
	
	//tree patterns
	
	/**
	 * Returns a {@link TreePattern} with one or more {@link EdgePattern}s.
	 * @param patterns the {@link EdgePattern}s
	 * @return the {@link TreePattern}
	 */
	public static final TreePattern tree(EdgePattern ... patterns) {
		return new TreePattern(Arrays.asList(patterns));
	}
	
	/**
	 * Returns a {@link Pattern} that matches {@link InnerNode}s with a path of one or more node identifiers.
	 * @param ids the identifiers
	 * @return the pattern
	 * @throws IllegalArgumentException if it is invoked with <code>null</code> or no input
	 */
	public static final Pattern hasPath(String ...ids) throws IllegalArgumentException {
		
		if (ids==null || ids.length<1) 
			throw new IllegalArgumentException();
		
		Pattern p = id(ids[ids.length-1],any());
		
		for (int i=ids.length-2; i>=0;i--)
			p = id(ids[i],tree(atleast(any, p)));
	
		return p;
	}

	
	/**
	 * Returns a {@link TreePattern} that matches {@link InnerNode}s with a path of one or more edge labels.
	 * @param labels the local names of the labels.
	 * @return the pattern.
	 * @throws IllegalArgumentException if it is invoked with <code>null</code> or no input.
	 */
	public static final TreePattern hasLabelPath(String ...labels) throws IllegalArgumentException {
		
		List<QName> qnames = new ArrayList<QName>();
		for (String label : labels)
			qnames.add(new QName(label));
		return hasLabelPath(qnames.toArray(new QName[0]));
	}
	
	/**
	 * Returns a {@link TreePattern} that matches {@link InnerNode}s with a path of one or more edge labels.
	 * @param labels the local names of the labels.
	 * @return the pattern.
	 * @throws IllegalArgumentException if it is invoked with <code>null</code> or no input.
	 */
	public static final TreePattern hasLabelPath(QName ...labels) throws IllegalArgumentException {
		
		if (labels==null || labels.length<1) throw new IllegalArgumentException();
		
		TreePattern p = tree(atleast(labels[labels.length-1],any()));
		
		for (int i=labels.length-2; i>=0;i--)
			p = tree(atleast(labels[i], p));
	
		return p;
	}
	
	/**
	 * Marks an {@link EdgePattern} as a condition.
	 * @param p the pattern
	 * @return the pattern
	 */
	public static EdgePattern cond(EdgePattern p) {
		p.setAsCondition();
		return p;
	}
	
	/**
	 * Adds a {@link LeafPattern} over the identifier of nodes matched by an {@link AbstractPattern}. 
	 * @param idp the {@link LeafPattern}
	 * @param p the {@link AbstractPattern}
	 * @return the {@link AbstractPattern} obtained from the original {@link AbstractPattern} by adding the {@link LeafPattern}
	 */
	public static final <T extends AbstractPattern> T id(LeafPattern<?,?> idp,T p) {
		p.setIdPattern(idp);
		return p;
		
	}

	/**
	 * Adds a {@link LeafPattern} for text equality over the identifier of nodes matched by a {@link AbstractPattern}. 
	 * @param identifier the string to compare with node identifiers
	 * @param pattern the {@link AbstractPattern}
	 * @return the {@link AbstractPattern} obtained from the original {@link TreePattern} by adding the {@link AbstractPattern}
	 */
	public static final <T extends AbstractPattern> T id(String identifier,T pattern) {
		return id(text(is(identifier)),pattern);
	}
	
	
	/**
	 * Returns a {@link OnePattern} for a given label and a given {@link Pattern}.
	 * @param label the label
	 * @param pattern the pattern
	 * @return the {@link OnePattern}
	 */
	public static final OnePattern one(QName label,Pattern pattern) {
		return new OnePattern(label,pattern);
	}
	
	/**
	 * Returns a {@link OnePattern} for a given label and a given {@link Pattern}.
	 * @param label the label
	 * @param pattern the pattern
	 * @return the {@link OnePattern}
	 */
	public static final OnePattern one(String label,Pattern pattern) {
		return one(q(label),pattern);
	}
	
	/**
	 * Returns an {@link OptPattern} for a given label and a given {@link Pattern}.
	 * @param label the label
	 * @param pattern the pattern
	 * @return the {@link OptPattern}
	 */
	public static final OptPattern opt(QName label,Pattern pattern) {
		return new OptPattern(label,pattern);
	}

	/**
	 * Returns an {@link OptPattern} for a given label and a given {@link Pattern}.
	 * @param label the label
	 * @param pattern the pattern
	 * @return the {@link OptPattern}
	 */
	public static final OptPattern opt(String label,Pattern pattern) {
		return opt(q(label),pattern);
	}
	
	/**
	 * Returns an {@link AtLeastPattern} for a given label and a given {@link Pattern}.
	 * @param label the label
	 * @param pattern the {@link Pattern}
	 * @return the {@link AtLeastPattern}
	 */
	public static final AtLeastPattern atleast(QName label,Pattern pattern) {
		return new AtLeastPattern(label,pattern);
	}
	
	/**
	 * Returns an {@link AtLeastPattern}  for a given label and a given {@link Pattern}.
	 * @param label the label
	 * @param pattern the {@link Pattern}
	 * @return the {@link AtLeastPattern}
	 */
	public static final AtLeastPattern atleast(String label,Pattern pattern) {
		return atleast(q(label),pattern);
	}
	
	/**
	 * Returns a {@link ManyPattern} for a given label and a given {@link Pattern}.
	 * @param label the label
	 * @param pattern the {@link Pattern}.
	 * @return the {@link ManyPattern}.
	 */
	public static final ManyPattern many(QName label,Pattern pattern) {
		return new ManyPattern(label,pattern);
	}
	
	/**
	 * Returns a {@link ManyPattern} for a given label and a given {@link Pattern}.
	 * @param label the label
	 * @param pattern the {@link Pattern}.
	 * @return the {@link ManyPattern}.
	 */
	public static final ManyPattern many(String label,Pattern pattern) {
		return many(q(label),pattern);
	}
	
	/**
	 * Returns an {@link OnlyPattern} for a given label and a given {@link Pattern}.
	 * @param label the label
	 * @param pattern the {@link Pattern}
	 * @return the {@link OnlyPattern}
	 */
	public static final OnlyPattern only(QName label,Pattern pattern) {
		return new OnlyPattern(label,pattern);
	}
	
	/**
	 * Returns an {@link OnlyPattern} for a given label and a given {@link Pattern}.
	 * @param label the label
	 * @param pattern the {@link Pattern}
	 * @return the {@link OnlyPattern}
	 */
	public static final OnlyPattern only(String label,Pattern pattern) {
		return only(q(label),pattern);
	}
	
	/**
	 * Returns a {@link ManyPattern} that matches all the children of a node <em>not</em> previously matched by the {@link EdgePattern}s of a {@link TreePattern}.
	 * @return the pattern
	 */
	public static ManyPattern tail() {
		return many(any,any());
	}
	
	/**
	 * Returns a {@link CutTreePattern} with the {@link EdgePattern}s of a given {@link TreePattern} but removes all
	 * the edges of a matching node under pruning
	 * @param pattern the {@link Pattern}
	 * @return the {@link CutTreePattern}
	 */
	public static CutTreePattern cut(TreePattern pattern) {
		return new CutTreePattern(pattern.patterns());
	}
	
	//text leaves

	/**
	 * Returns a {@link TextPattern} with a given constraint.
	 * @param constraint the constraint
	 * @return the {@link TextPattern}
	 */
	public static TextPattern text(Constraint<? super String> constraint) {
		return new TextPattern(constraint);
	}
	
	/**
	 * Returns an unconstrained {@link TextPattern}.
	 * @return the {@link TextPattern} 
	 */
	public static TextPattern text() {
		return new TextPattern(anyval);
	}
	
	/**
	 * Returns a {@link NumPattern} with a given constraint.
	 * @param c the constraint
	 * @return the {@link NumPattern}
	 */
	public static NumPattern num(Constraint<? super Double> c) {
		return new NumPattern(c);
	}
	
	/**
	 * Returns an unconstrained {@link NumPattern}.
	 * @return the {@link NumPattern}
	 */
	public static NumPattern num() {
		return new NumPattern(anyval);
	}
	
	/**
	 * Returns a {@link BoolPattern} with a given constraint.
	 * @param c the constraint
	 * @return the {@link BoolPattern}
	 */
	public static BoolPattern bool(Constraint<? super Boolean> c) {
		return new BoolPattern(c);
	}
	
	/**
	 * Returns an unconstrained {@link BoolPattern} predicate.
	 * @return the {@link BoolPattern}
	 */
	public static BoolPattern bool() {
		return new BoolPattern(anyval);
	}
	
	/**
	 * Returns a {@link DatePattern} with a given constraint.
	 * @param c the constraint
	 * @return the {@link DatePattern}
	 */
	public static DatePattern date(Constraint<? super java.util.Date> c) {
		return new DatePattern(c);
	}
	
	/**
	 * Returns an unconstrained {@link DatePattern}.
	 * @return the {@link DatePattern}
	 */
	public static DatePattern date() {
		return new DatePattern(anyval);
	}
	
	/**
	 * Returns a {@link URIPattern} with a given constraint.
	 * @param c the constraint
	 * @return the {@link URIPattern}
	 */
	public static URIPattern uri(Constraint<? super URI> c) {
		return new URIPattern(c);
	}
	
	/**
	 * Returns an unconstrained {@link URIPattern}.
	 * @return the {@link URIPattern}
	 */
	public static URIPattern uri() {
		return new URIPattern(anyval);
	}
	
	/**
	 * Returns a {@link CalendarPattern}with a given constraint.
	 * @param c the constraint
	 * @return the {@link CalendarPattern}
	 */
	public static CalendarPattern calendar(Constraint<? super java.util.Calendar> c) {
		return new CalendarPattern(c);
	}
	
	/**
	 * Returns an unconstrained {@link CalendarPattern}.
	 * @return the {@link CalendarPattern}
	 */
	public static CalendarPattern calendar() {
		return new CalendarPattern(anyval);
	}

	////////////////////////////////////////////////////////////////////////////////// CONSTRAINTS
	
	//////////////////////////////////////////////////////////////// cross-type
	
	/**
	 * The null constraint.
	 */
	public final static AnyValue anyval = new AnyValue(); 

	/**
	 * Returns a {@link Same} constraint for a given value.
	 * @param v the value
	 * @return the constraint
	 */
	public static <T> Same<T> is(T v) {
		return new Same<T>(v);
	}

	/**
	 * Returns a {@link Match} constraint for a given regular expression.
	 * @param regex the expression
	 * @return the constraint
	 */
	public static Match matches(String regex) {
		return new Match(regex);
	}
	
	/**
	 * Returns an {@link All} constraint that combines two or more {@link Constraint}s.
	 * @param <T> the type of constrained values
	 * @param constraint1 the first constraint
	 * @param constraint2 the second constraint
	 * @return the {@link All} constraint
	 */
	public static <T> All<T> all(Constraint<T> constraint1, Constraint<T> constraint2) {
		return new All<T>(constraint1,constraint2);
	}
	
	/**
	 * Returns an {@link Either} constraint that combines two or more {@link Constraint}s.
	 * @param <T> the type of constrained values
	 * @param constraint1 the first constraint
	 * @param constraint2 the second constraint
	 * @return the {@link Either} constraint
	 */
	public static <T> Either<T> either(Constraint<T> constraint1, Constraint<T> constraint2) {
		return new Either<T>(constraint1,constraint2);
	}
	
	/**
	 * Returns an {@link Not} constraint for a given constraint.
	 * @param <T> the type of constrained values
	 * @param constraint the constraint
	 * @return the {@link Not} constraint
	 */
	public static <T> Not<T> not(Constraint<T> constraint) {
		return new Not<T>(constraint);
	}
	
	
	//////////////////////////////////////////////////////////////// numbers
	
	/**
	 * Returns a {@link More} constraint for a given value.
	 * @param than the value
	 * @return the constraint
	 */
	public static More more(double than) {
		return new More(than);
	}


	/**
	 * Returns a {@link Less} constraint for a given value.
	 * @param than the value
	 * @return the constraint
	 */
	public static Less less(double than) {
		return new Less(than);
	}
		

	//////////////////////////////////////////////////////////////// dates
	
	/**
	 * Returns a {@link BeforeDate} constraint for a given date.
	 * @param d the date
	 * @return the constraint
	 */
	public static BeforeDate before(Date d) {
		return new BeforeDate(d);
	}
	
	/**
	 * Returns an {@link AfterDate} constraint for a given date.
	 * @param d the date
	 * @return the constraint
	 */
	public static AfterDate afterDate(Date d) {
		return new AfterDate(d);
	}
	
	/**
	 * Returns an {@link AfterDate} constraint for the current date.
	 * @return the constraint
	 */
	public static AfterDate futureDate() {
		return new AfterDate(new Date());
	}
	
	/**
	 * Returns a {@link BeforeDate} constraint for the current date.
	 * @return the constraint.
	 */
	public static BeforeDate pastDate() {
		return new BeforeDate(new Date());
	}
	
	/**
	 * Returns a {@link Before} constraint for a given calendar.
	 * @param c the calendar
	 * @return the constraint
	 */
	public static Before before(Calendar c) {
		return new Before(c);
	}

	
	/**
	 * Returns a {@link After} constraint for a given calendar.
	 * @param c the calendar
	 * @return the constraint
	 */
	public static After after(Calendar c) {
		return new After(c);
	}
	
	/**
	 * Returns an {@link After} constraint for the current date.
	 * @return the constraint
	 */
	public static After future() {
		return new After(Calendar.getInstance());
	}

	
	/**
	 * Returns an {@link Before} constraint for the current date.
	 * @return the constraint
	 */
	public static Before past() {
		return new Before(Calendar.getInstance());
	}
}

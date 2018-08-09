package org.gcube.common.storagehub.model.expressions.logical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.gcube.common.storagehub.model.expressions.Expression;

public class And implements Expression<Boolean>{

	private List<Expression<Boolean>> expressions = new ArrayList<>();
	
	protected And() {}
	
	public And(Expression<Boolean> first , Expression<Boolean> second , Expression<Boolean> ... others ) {
		expressions = new ArrayList<>(2);
		expressions.add(first);
		expressions.add(second);
		if (others !=null && others.length>0)
			expressions.addAll(Arrays.asList(others));
	}
	
	public List<Expression<Boolean>> getExpressions() {
		return expressions;
	}

	@Override
	public String toString() {
		return "And [" + expressions.stream().map(Object::toString).collect(Collectors.joining(",")).toString() + "]";
	}
	
	
}

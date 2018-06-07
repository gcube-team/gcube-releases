package org.gcube.data.trees.generators;

import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.generators.StructuralTemplate.STBuilder;

/**
 * Factory of {@link TreeTemplate}s.
 * 
 * @author Fabio Simeoni
 *
 */
public class TemplateFactory {

	/**
	 * Returns a builder of {@link StructuralTemplate}s.
	 * @return the builder
	 */
	public static STBuilder aTree() {
		return new STBuilder();
	}
	
	
	/**
	 * Returns a {@link SimilarityTemplate} for a given prototype and similarity coefficient.
	 * @param proto the prototype
	 * @param similarity the similarity coefficient
	 * @return the template
	 */
	public static SimilarityTemplate aTreeLike(Tree proto,double similarity) {
		return new SimilarityTemplate(proto, similarity);
	}
	
	/**
	 * Returns a {@link SimilarityTemplate} for a given prototype and a similarity coefficient of .9.
	 * @param proto the prototype
	 * @return the template
	 */
	public static SimilarityTemplate aTreeLike(Tree proto) {
		return new SimilarityTemplate(proto,.9);
	}
}

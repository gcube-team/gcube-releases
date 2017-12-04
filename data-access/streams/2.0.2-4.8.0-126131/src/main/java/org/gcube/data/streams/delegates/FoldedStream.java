package org.gcube.data.streams.delegates;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.streams.Stream;

/**
 * A {@link Stream} that folds into lists the elements of another {@link Stream}.
 * 
 * @author Fabio Simeoni
 *
 * @param <E> the type of stream element
 */
public class FoldedStream<E> extends AbstractDelegateStream<E,List<E>> {

	private final int foldSize;
	
	/**
	 * Creates an instance with a {@link Stream} and a fold size.
	 * @param stream the stream
	 * @param foldSize the fault size
	 * @throws IllegalArgumentException if the stream is <code>null</code> or the size is not positive
	 */
	public FoldedStream(Stream<E> stream,int foldSize) throws IllegalArgumentException {
		
		super(stream);
		
		if (foldSize<1)
			throw new IllegalArgumentException("invalid foldsize is not positive");
		
		this.foldSize=foldSize;
	}
	
	@Override
	protected List<E> delegateNext() {
		
		//we do not deal with failures, streams will need to be guarded upstream
		//we also do not deal with transformations, which will need to be applied upstream
				
		List<E> fold = new ArrayList<E>();
		
		for (int i=0;i<foldSize;i++) 
			if (stream().hasNext())
				fold.add(stream().next());
					
		
		return fold;
	}
	
	@Override
	protected boolean delegateHasNext() {
		return stream().hasNext();
	}

}

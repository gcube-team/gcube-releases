package gr.uoa.di.madgik.searchlibrary.operatorlibrary.indexfuse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to hold elements from content and metadata 
 * collections that refer to the same Information Object.
 *  
 * @author UoA
 *
 */
public class ResultElement {
	
	/**
	 * The Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(ResultElement.class.getName());
	/**
	 * stores the JoinElements for the content and metadata of the related Information Object
	 */
	private JoinElement[] elements = null;
	/**
	 * the aggregated rank for this result
	 */
	private double rank = 0.0;
	/**
	 * the id of the related Information Object -OID
	 */ 
	private String id = null;
	/**
	 * how many JoinElements are inserted
	 */
	private int inserted = 0;
	/**
	 * indicates if this element is sent
	 */
	private boolean sent = false;
	
	
	/**
	 * public Constructor for a ResultElement
	 * @param id - The OID
	 * @param metaCols - upper bound for the content/metadata elements that this result can have
	 */
	public ResultElement(String id, int cols){
		this.id = id;
		//the addition of one refers to the content element(if there is such one)
		this.elements = new JoinElement[cols+1];
	}
	
	/**
	 * Inserts a new element for the IO, this result refers to 
	 * @param element - the new element
	 * @param weight - the weight for the rank of this new element
	 * @return true in case of success
	 */
	public boolean insertElement(JoinElement element, double weight)
	{
		short colID = element.getMetaColID();
		if(elements[colID] != null)
		{
			logger.warn("An element for DocID: " + element.getId() + " from the " + colID + "-th RS(received by the operator) for collection:"+element.getCollectionID()+" was already found");
			return false;
		}else{
			elements[colID] = element;
			rank += weight*element.getRank();
			inserted++;
			return true;
		}
	}
	
	/**
	 * Gets the number of inserted elements for this result
	 * @return inserted
	 */
	public int getInserted()
	{
		return inserted;
	}
	
	
	/**
	 * Tag this element as sent
	 */
	public void tagSent()
	{
		sent = true;
	}
	
	/**
	 * Return if this element is sent 
	 */
	public boolean isSent()
	{
		return sent;
	}
	
	/**
	 * Return the rank of this element
	 */
	public double getRank()
	{
		return this.rank;
	}
	
	/**
	 * Return the id for this element
	 */
	public String getId()
	{
		return this.id;
	}
}

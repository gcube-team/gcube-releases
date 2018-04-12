package org.gcube.common.portal.mailing.templates;


/**
 * Base interface for any email Template. A template must
 * 
 * @author M. Assante, CNR-ISTI
 *
 */
public interface Template {
	public String compile(String templateContent);
	public String getTextHTML();
	public String getTextPLAIN();
}

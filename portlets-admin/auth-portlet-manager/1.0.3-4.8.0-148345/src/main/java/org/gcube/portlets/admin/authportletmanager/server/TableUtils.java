package org.gcube.portlets.admin.authportletmanager.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.authportletmanager.shared.Quote;





public class TableUtils {

	public static List<Quote> quoteList = Arrays.asList(
//			new Quote(100000l,"user.name","devNext",ManagerType.STORAGE,TimeInterval.WEEKLY,500.00)
			);
	public static Map<Long, Quote> SERVICESQUOTE = new HashMap<Long, Quote>();
	static {
		for (Quote quote: quoteList)
			SERVICESQUOTE.put(quote.getIdQuote(), quote);
	}













}

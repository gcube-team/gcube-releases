package gr.cite.gaap.utilities;

import org.apache.commons.lang.StringUtils;

public class HtmlUtils
{
	public static String htmlEscape(String input)
	{
		return StringUtils.replaceEach(input, new String[]{"&", "\"", "<", ">"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;"});
	}
	
	public static String htmlWeakEscape(String input)
	{
		return StringUtils.replaceEach(input, new String[]{"<script>", "</script>", "<link>", "</link>"}, new String[]{"&lt;script&gt;", "&lt;/script&gt;", "&lt;link&gt;", "&lt;/link&gt;"});
	}
}

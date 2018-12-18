package org.gcube.accounting.aggregator;

import java.util.List;

import org.gcube.accounting.datamodel.validations.validators.RegexReplace;
import org.gcube.testutility.ScopedTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexRulesAggregatorTest extends ScopedTest {
	
	private static final Logger logger = LoggerFactory.getLogger(RegexRulesAggregatorTest.class);
	
	@Test
	public void test() {
		RegexRulesAggregator regexRulesAggregator = RegexRulesAggregator.getInstance();
		List<RegexReplace> list = regexRulesAggregator.getRegexReplaceList();
		for(RegexReplace regexReplace : list) {
			logger.debug("{} {} {} {}", regexReplace.getServiceClass(), regexReplace.getServiceName(), regexReplace.getRegex(), regexReplace.getReplace());
		}
	}
	
}

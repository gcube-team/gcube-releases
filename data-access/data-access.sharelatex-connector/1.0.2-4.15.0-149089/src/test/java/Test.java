import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test {

	@org.junit.Test
	public void test(){
		String resp = "input name=\"_csrf\" type=\"hidden\" value=\"04g6q9eT-8JEdbPRIAUq1Y9sn7eu8OBB72aU\"";
		
		Pattern pattern = Pattern.compile("input name=\"_csrf\" type=\"hidden\" value=\"([^\"]*)\"");
		Matcher matcher = pattern.matcher(resp);
		System.out.println(matcher.find());
		System.out.println(matcher.group(1));
	}
	
}

/*import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.bluebridge.analytics.web.Parameters;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:/WEB-INF/beans.xml" })

public class ControllerTest {

    @Autowired WebApplicationContext wac; 
    @Autowired MockHttpSession session;
    @Autowired MockHttpServletRequest request;
    private String devVRE = "/gcube/devsec/devVRE";
    private String devVRE_token = "fb01ba32-4e1b-4c3f-a9dc-298ea3eeb9f2-98187548";
    
    private String NextNext = "/gcube/devNext/NextNext";
    private String NextNext_token = "97e289af-e33f-472f-b836-45befe8c8638-98187548"; 
    
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void performAnalysis() throws Exception {
        Parameters params = new Parameters(); 
        params.setModelId((long) 1167);
        params.setTaxRate(29d);
        params.setDiscountRate(3.75d);
        params.setInflationRate("{ \"2018\" : \"0.65\" } ");
        params.setMaturity(18);
        params.setFeedPrice(1.15d);
        params.setFryPrice(0.2d);
        params.setSellingPrice(5.20d);
        params.setIsOffShoreAquaFarm(true);
		
        mockMvc.perform(post("/performAnalysis")
        		.content(asJsonString(params))
        		.contentType(MediaType.APPLICATION_JSON)
        		.accept(MediaType.APPLICATION_JSON)
        		.session((MockHttpSession)session)
        		.header("scope", NextNext)
        		.header("gcube-token", NextNext_token))
        .andDo(MockMvcResultHandlers.print());        
    }
        
    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    } 
}

*/
package gr.cite.repo.auth.app.views;

import io.dropwizard.views.View;

public class LoginView extends View {

	String idp;
	String sp;
	String issuer;
	
    public LoginView(String idp, String sp, String issuer) {
        super("login.ftl");
        this.idp = idp;
        this.sp = sp;
        this.issuer = issuer;
    }

	public String getIdp() {
		return idp;
	}

	public String getSp() {
		return sp;
	}

	public String getIssuer() {
		return issuer;
	}
    
    

}
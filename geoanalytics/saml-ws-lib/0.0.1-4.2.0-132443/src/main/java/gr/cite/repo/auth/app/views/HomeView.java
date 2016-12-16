package gr.cite.repo.auth.app.views;

import io.dropwizard.views.View;

public class HomeView extends View {

	String name;
	String sp;
	
    public HomeView(String sp, String name) {
        super("home.ftl");
        this.sp = sp;
        this.name = name;
    }


	public String getSp() {
		return sp;
	}


	public String getName() {
		return name;
	}

}
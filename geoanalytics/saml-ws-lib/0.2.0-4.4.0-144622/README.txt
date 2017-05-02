1)	Add 
	-----------------------------------
	security:
	    unauthorizedLocation: http://localhost:9180/saml/sendLoginRequest
	    includeTarget: true
	    spHost: http://localhost:9180
	    idpHost: http://192.168.11.97:8080
	    privateKeyFilename: /home/user/Desktop/my_keys/rsa_private_key.pk8
	    certificateFilename: /home/user/Desktop/my_keys/cert.pem
	    
	distributedSession:
	    workerName: index1
	
	    databaseInfo:
	        database: postgres
	        username: USERNAME
	        password: PASSWORD
	        databaseName: sessions
	        serverName: localhost
	-----------------------------------
	to your yaml file

2)

Add the following lines in Application#run:

    @Override
	public void run(SamlSecurityConfiguration configuration,
			Environment environment) throws Exception {		
		this.secureAppHelper = new SecureAppHelpers(environment);
		this.secureAppHelper.applySessionManager(configuration.getSessionManager());
		this.secureAppHelper.applySecurity(configuration.getSecurity(), ImmutableList.<String>of("/protected/*"));
		
		...
	}
	
Change the main as the example below:
	public static void main(String[] args) throws Exception {
	    ...
		SampleSamlApp app = new SampleSamlApp();
		app.run(args);
		app.secureAppHelper.overrideServer();
	}	
  
  and bootstrap.addBundle(new ViewBundle()); to Application#initialize
  
3) Change application configuration class so that it "extends  gr.cite.repo.auth.app.config.SamlSecurityConfiguration"         
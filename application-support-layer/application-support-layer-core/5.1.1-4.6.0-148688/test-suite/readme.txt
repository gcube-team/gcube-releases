TEST SUITE FOR THE ASL CORE
-----------------------------------------------------------
-Author
* Rena Tsantouli (e.tsantoylh@di.uoa.gr)

User Authentication:

-Build

Dependencies: ApplicationSupportLayerCore.jar and (runtime dependencies): commons-logging.jar, jboss-common.jar, jbosssx.jar included with a standard JBoss Application Server.

-Set the System property "java.security.auth.login.config" pointing to the location where the configuration file "jaas.config" is placed.


 
  The contents of the jaas.config file are the following:
  
  
  Gridsphere {
	org.jboss.security.auth.spi.LdapExtLoginModule required
        java.naming.factory.initial=com.sun.jndi.ldap.LdapCtxFactory
        java.naming.provider.url="ldap://ldap.research-infrastructures.eu/"
        java.naming.security.authentication=simple
        bindDN="cn=anonymous,ou=System,dc=research-infrastructures,dc=eu"
        bindCredential=freeToSearch
        baseCtxDN="ou=Organizations,dc=research-infrastructures,dc=eu"
        baseFilter="(uid={0})"
        rolesCtxDN="ou=Groups,ou=DevelopmentPortal,ou=D4Science,ou=Applications,dc=research-infrastructures,dc=eu"
        roleFilter="(uniqueMember={1})"
        roleAttributeID="cn"
        roleRecursion="-1"
	;
      };
package snippet;

public class Snippet {
//	The security model is based on two pillars:
//	** username/password security factor, provided by SOA3
//	** ''Transport Layer Security'' (TLS), in particular HTTP on TLS (HTTPS) based on Public Key Infrastructure
//	: SOA3 Security Framework provides [[SOA3_Authentication_Module|authentication]] and [[SOA3 Authorization Module|authorization]] based on username/password security factor and Attribute Based Authorization. Transport Layer Security is a cryptographic protocol derived from Secure Socket Layer (SSL): it is based on Public Key Cryptography <ref>http://en.wikipedia.org/wiki/Public-key_cryptography</ref>, symmetric encryption <ref>http://en.wikipedia.org/wiki/Symmetric-key_algorithm</ref> and message authentication code <ref>http://en.wikipedia.org/wiki/Message_authentication_code</ref> and provides digital signature, privacy and integrity. <code>Foo</code> may be configured with settings implementing a security level appropriate to the exposed functionalities and data. Basing on <code>foo</code> configuration and according to the proposed security model a Client can access <code>foo</code> using different security factors:
//	:: no security at all
//	:: '''Username/Password''' previously registered on the infrastructure
//	:: Transport Layer Security (HTTPS) with, if required, client authentication by client certificate 
//	:: SAML based impersonation 
//	
//	: Username/Password authentication factor and SAML based impersonation, which are alternative authentication methods, can be combined with TLS/HTTPS in order to exploit the features of encryption data and '''Server''' certificate validation improving the security.
//	
}


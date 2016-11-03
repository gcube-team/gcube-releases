<#-- @ftlvariable name="" type="gr.cite.repo.fulltextindex.views.LoginView" -->
<html>
    <body>
        <h1>Login Page!</h1>
        <a href="${sp}/saml/sendLoginRequest?target=${sp}/saml/home">Login SP-init</a>
        <br>
        <a href="${idp}/idp/profile/SAML2/Unsolicited/SSO?providerId=${issuer}&target=${sp}/saml/home">Login Idp-init</a>
        
    </body>
</html>
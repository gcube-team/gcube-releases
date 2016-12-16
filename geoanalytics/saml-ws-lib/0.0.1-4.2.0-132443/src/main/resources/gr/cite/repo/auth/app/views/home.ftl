<#-- @ftlvariable name="" type="gr.cite.repo.fulltextindex.views.LoginView" -->
<html>
    <body>
        <h1>Home Page!</h1>
        <p>Welcome ${name}</p>
        <br>
        <a href="${sp}/saml/sendLogoutRequest?target=${sp}/saml/home">Logout</a>
    </body>
</html>
package org.gcube.index;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.semantic.DataServer;

@Path("/dataserver")
public class DataServerServlet {

    private static final String defaultCallback = "smartfish_response";

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response find(
            @QueryParam("term") String term, 
            @QueryParam("op") String op, 
            @QueryParam("target") List<String> target,
            @QueryParam("page") String page, 
            @QueryParam("concept_filter") List<String> concept_filter, 
            @QueryParam("callback") @DefaultValue(defaultCallback) String callback) {
        
        String msg = DataServer.getInstance().find(term, op, target.toArray(new String[target.size()]), page, concept_filter.toArray(new String[concept_filter.size()]));
        
        Response.Status status = msg != null ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR;

        return Response.status(status).entity(callback + "(" + msg + ");").build();
    }

    @GET
    @Path("/entity")
    @Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response entity(
            @QueryParam("op") String op, 
            @QueryParam("target") List<String> target, 
            @QueryParam("lang") String lang, 
            @QueryParam("callback") @DefaultValue(defaultCallback) String callback) {
        
        String msg = DataServer.getInstance().entity(op, target.toArray(new String[target.size()]), lang);
        Response.Status status = msg != null ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR;

        return Response.status(status).entity(callback + "(" + msg + ");").build();
    }

    @GET
    @Path("/document")
    @Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response document(
            @QueryParam("op") String op, 
            @QueryParam("doc") String doc, 
            @QueryParam("target") List<String> target, 
            @QueryParam("lang") String lang, 
            @QueryParam("callback") @DefaultValue(defaultCallback) String callback) {
       
        String msg = DataServer.getInstance().document(op, doc, target.toArray(new String[target.size()]), lang);
        Response.Status status = msg != null ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR;

        return Response.status(status).entity(callback + "(" + msg + ");").build();
    }

    @GET
    @Path("/gis")
    @Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response gis(
            @QueryParam("op") String op, 
            @QueryParam("target") List<String> target, 
            @QueryParam("lang") String lang, 
            @QueryParam("callback") 
            @DefaultValue(defaultCallback) String callback) {
        
        if (lang != null && lang.trim().length() == 0) {
            lang = null;
        }

        String msg = DataServer.getInstance().gis(op, target.toArray(new String[target.size()]), lang);
        Response.Status status = msg != null ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR;

        return Response.status(status).entity(callback + "(" + msg + ");").build();
    }

    @GET
    @Path("/infobox")
    @Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response infobox(
            @QueryParam("op") String op, 
            @QueryParam("target") String target, 
            @QueryParam("lang") String lang, 
            @QueryParam("callback") 
            @DefaultValue(defaultCallback) String callback) {
        
        if (lang != null && lang.trim().length() == 0) {
            lang = null;
        }
        String msg = DataServer.getInstance().infobox(op, target, lang);
        Response.Status status = msg != null ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR;

        return Response.status(status).entity(callback + "(" + msg + ");").build();
    }

    @GET
    @Path("/exp")
    @Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response exp(
            @QueryParam("country") List<String> country, 
            @QueryParam("species") List<String> species, 
            @QueryParam("callback") 
            @DefaultValue(defaultCallback) String callback) {
        
        String msg = DataServer.getInstance().exp(country.toArray(new String[country.size()]), species.toArray(new String[species.size()]));
        Response.Status status = msg != null ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR;

        return Response.status(status).entity(callback + "(" + msg + ");").build();
    }

    @GET
    @Path("/statistics")
    @Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
    public Response statistics(
            @QueryParam("op") String op, 
            @QueryParam("target") List<String> target, 
            @QueryParam("lang") String lang, 
            @QueryParam("callback") 
            @DefaultValue(defaultCallback) String callback) {
        if (lang != null && lang.trim().length() == 0) {
            lang = null;
        }
        String msg = DataServer.getInstance().statistics(op, target.toArray(new String[target.size()]), lang);
        Response.Status status = msg != null ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR;

        return Response.status(status).entity(callback + "(" + msg + ");").build();
    }
}

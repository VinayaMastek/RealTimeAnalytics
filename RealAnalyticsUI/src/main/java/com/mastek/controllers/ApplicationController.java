package com.mastek.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSONObject;
import com.mastek.domain.PostgresImpl;

@Path("/events")
public class ApplicationController {
	@Context
	private HttpServletRequest request;

	@GET
	@Path("/getapplication")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject GetApplication(@Context HttpServletResponse servletResponse, @QueryParam("transactionid") Integer transactionid) {
		System.out.println(transactionid);
		JSONObject application = fetchApplication(transactionid);
		return application;
	}

	private JSONObject fetchApplication(Integer transactionId) {
		PostgresImpl pg = new PostgresImpl();
		pg.connect();
		JSONObject application = pg.fetchApplication(transactionId);
		pg.close();
		return application;
	}
	
	@POST
	@Path("/applicationupdt")
	@Consumes(MediaType.APPLICATION_JSON)
	public void ApplicationUpdt(Object aData,
			@Context HttpServletResponse servletResponse) {
		System.out.println(JSONObject.fromObject(aData));
		updateApplication(JSONObject.fromObject(aData));
	}

	private void updateApplication(JSONObject applicationData) {
		PostgresImpl pg = new PostgresImpl();
		pg.connect();
		pg.updateApplication(applicationData);
		pg.close();
	}
	
	@POST
	@Path("/applicationsave")
	@Consumes(MediaType.APPLICATION_JSON)
	public void ApplicationSave(Object aData,
			@Context HttpServletResponse servletResponse) {
		System.out.println(JSONObject.fromObject(aData));
		addApplication(JSONObject.fromObject(aData));
	}

	private void addApplication(JSONObject applicationData) {
		PostgresImpl pg = new PostgresImpl();
		pg.connect();
		pg.insertApplication(applicationData);
		pg.close();
	}

	@POST
	@Path("/applicationfinalize")
	@Consumes(MediaType.APPLICATION_JSON)
	public void ApplicationFinalize(Object aData,
			@Context HttpServletResponse servletResponse) {
		finalizeApplication(JSONObject.fromObject(aData));
	}

	private void finalizeApplication(JSONObject applicationData) {
		PostgresImpl pg = new PostgresImpl();
		pg.connect();
		pg.finalizeApplication(applicationData);
		pg.close();
		
	}
}

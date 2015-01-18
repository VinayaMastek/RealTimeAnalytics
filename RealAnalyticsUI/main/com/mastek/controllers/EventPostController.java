package com.mastek.controllers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;

import com.mastek.domain.PostgresImpl;

@ServerEndpoint("/websocket/notifyAnnotation")
@Path("/events")
public class EventPostController {

	public static class SessionRegistry {
		static HashMap<String, Session> registry = new HashMap<String, Session>();

		public static void registerTransaction(String transactionID,
				Session socketSession) {
			registry.put(transactionID, socketSession);
		}

		public static Session getTransaction(String transactionID) {
			return registry.get(transactionID);
		}

	}

	@Context
	private HttpServletRequest request;

	// http://localhost:8080/RealAnalyticsUI/rest/events/eventget?eventdata="test";

	@POST
	@Path("/eventpost")
	@Consumes(MediaType.APPLICATION_JSON)
	public void EventPost(Object eData,
			@Context HttpServletResponse servletResponse) {
		JSONObject eventData = JSONObject.fromObject(eData);
		eventData.put("sessionid", request.getSession(true).getId());
		addEvents(eventData);
	}

	// http://localhost:8080/RealAnalyticsUI/rest/events/eventpost?eventdata="test";

	private void addEvents(JSONObject eventdata) {
		PostgresImpl pg = new PostgresImpl();
		pg.connect();
		pg.insertEvents(eventdata);
		pg.close();
	}

	@POST
	@Path("/contactpost")
	@Consumes(MediaType.APPLICATION_JSON)
	public void ContactPost(Object cData,
			@Context HttpServletResponse servletResponse) {
		addContact(JSONObject.fromObject(cData));
	}

	// http://localhost:8080/RealAnalyticsUI/rest/events/eventpost?eventdata="test";

	private void addContact(JSONObject contactData) {
		PostgresImpl pg = new PostgresImpl();
		pg.connect();
		pg.insertContact(contactData);
		pg.close();
	}

	@POST
	@Path("/countUpdt")
	@Consumes(MediaType.APPLICATION_JSON)
	public void CountUpdt(Object eData,
			@Context HttpServletResponse servletResponse) {
		JSONObject eventData = JSONObject.fromObject(eData);

		Session s = SessionRegistry.getTransaction((String) eventData
				.get("tran"));
		if (s != null)
			echoTextMessage(s, eventData.getString("msg"), true);

	}

	// http://localhost:8080/RealAnalyticsUI/rest/events/countUpdt?eventdata="{tran="111","msg="abcd "};

	@OnMessage
	public void echoTextMessage(Session session, String msg, boolean last) {
		sendText(msg, session, last);
	}

	@OnMessage
	public void echoBinaryMessage(Session session, ByteBuffer bb, boolean last) {
		try {
			if (session.isOpen()) {
				session.getBasicRemote().sendBinary(bb, last);
			}
		} catch (IOException e) {
			try {
				session.close();
			} catch (IOException e1) {
				// Ignore
			}
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println(session.getId() + " has opened a connection");
		System.out.println("Transaction id is "
				+ session.getQueryString().split("=")[1]);
		SessionRegistry.registerTransaction(
				session.getQueryString().split("=")[1], session);
		
		sendText("Connection Established", session, true);
	}

	private void sendText(String msg, Session session,boolean last)
	{
		boolean done = false;
		while (!done) {
			try {
				if (session.isOpen()) {
					done = true;
					session.getBasicRemote().sendText(
							msg , last);

				}
			} catch (IOException e) {
				done = false;
			} catch (RuntimeException e ){
				done = false;
			}
		}

	}
	
	
	/**
	 * Process a received pong. This is a NO-OP.
	 * 
	 * @param pm
	 *            Ignored.
	 */
	@OnMessage
	public void echoPongMessage(PongMessage pm) {
		// NO-OP
	}
}

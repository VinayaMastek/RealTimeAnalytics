package com.mastek.domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PostgresImpl {
	private Connection conn = null;
	private Statement stmt = null;

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public Statement getStmt() {
		return stmt;
	}

	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}

	public void connect() {
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(
					"jdbc:postgresql://104.236.67.184:5432/postgres",
					"postgres", "online01");
			conn.setAutoCommit(false);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	/*
	 * public void insertEvents(String eventData) { String sql =
	 * "INSERT INTO events"; String fld = "%s"; String fieldStr =
	 * "("+fld+","+fld+","+fld+","+fld+","+fld+")";
	 * 
	 * String quote = "'"; String valueStrFld = quote + "%s" +quote; String
	 * valueStr =
	 * "("+valueStrFld+","+valueStrFld+","+valueStrFld+","+valueStrFld
	 * +","+valueStrFld+")"; String[] fields = eventData.split("&");
	 * 
	 * fieldStr =
	 * String.format(fieldStr,fields[0].split("=")[0],fields[1].split(
	 * "=")[0],fields
	 * [2].split("=")[0],fields[3].split("=")[0],fields[4].split("=")[0]);
	 * valueStr =
	 * String.format(valueStr,fields[0].split("=")[1],fields[1].split(
	 * "=")[1],fields
	 * [2].split("=")[1],fields[3].split("=")[1],fields[4].split("=")[1]); sql =
	 * sql + fieldStr + " VALUES " + valueStr; executeSql(sql); }
	 */
	public void insertEvents(JSONObject eventData) {
		String sql = "INSERT INTO events";
		// event, payload, timestamp, sessionid

		String fieldStr = "(" + "event" + "," + "payload" + "," + "timestamp"
				+ "," + "sessionid" + ")";
		String quote = "'";
/*		String valueStr = "(" + quote + eventData.getString("event") + quote
				+ "," + quote + eventData.getJSONObject("payload") + quote
				+ "," + quote + eventData.getString("timestamp") + quote + ","
				+ quote + eventData.getString("sessionid") + quote + ")";
*/
		String valueStr = "(" + quote + eventData.getString("event") + quote
				+ "," + quote + eventData.getJSONObject("payload") + quote
				+ "," + quote + eventData.getString("timestamp") + quote + ","
				+ quote + eventData.getJSONObject("payload").getString("tran") + quote + ")";
	
	
		sql = sql + fieldStr + " VALUES " + valueStr;
		System.out.println(sql);
		executeSql(sql);
	}

	public int openTran() {
		int tranid = 0;
		String sql = "INSERT INTO tran";
		// event, payload, timestamp, sessionid

		String fieldStr = "(" + "status" + ")";
		String quote = "'";
		String valueStr = "(" + quote + 'O' + quote+")";

	
	
		sql = sql + fieldStr + " VALUES " + valueStr;
		System.out.println(sql);
		executeSql(sql);
		
		String sql1 = "select max(tranid) mtranid from tran";
		 try {
		        stmt =conn.createStatement();
		        ResultSet rs = stmt.executeQuery(sql1);
		        while (rs.next()) {
		            tranid = rs.getInt("mtranid");
		        }
		        
		        return tranid;
		 } catch (SQLException e ) {
			 e.printStackTrace();
		    } finally {
		        }
		return tranid;
		    }

	
	public String fetchTran() {
		String outtran;
		String quote = "'";
		String sql = "select tranid from tran where status=" + quote + 'O' + quote;
		// event, payload, timestamp, sessionid


	
	
		System.out.println(sql);
		
		outtran="";
		String com=" ";
		
		 try {
		        stmt =conn.createStatement();
		        int tranid;
		        ResultSet rs = stmt.executeQuery(sql);
		        while (rs.next()) {
		            tranid = rs.getInt("tranid");
		        	outtran=outtran+com+"{\"tran\":\""+tranid+"\"}";
		        	com=",";
		        	
		            
		        }
		        
		        return outtran;
		 } catch (SQLException e ) {
			 e.printStackTrace();
		    } finally {
		        }
		return outtran;
		    }

	
	
	
	public void closeTran(JSONObject edata) {
		String quote = "'";

		String sql = "update tran set status = " + quote +"C" +quote + " where tranid="+edata.getString("tranid");
		// event, payload, timestamp, sessionid
	
		System.out.println(sql);
		executeSql(sql);
	}	

	
	
	private void commit() {
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void executeSql(String sql) {
		if (!sql.isEmpty()) {
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				stmt.close();
				commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void close() {
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	public JSONArray getEvents(String session) {
		JSONArray results = new JSONArray();
		JSONObject jobj = new JSONObject();

		if (conn == null)
			connect();

		String sql = "";
		ResultSet rs = null;
		sql = "SELECT event, field, value, timestamp, sessionid FROM events";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				jobj.put("event", rs.getString("event"));
				jobj.put("field", rs.getString("field"));
				jobj.put("value", rs.getString("value"));
				jobj.put("timestamp", rs.getString("timestamp"));
				jobj.put("sessionid", rs.getString("sessionid"));
				results.add(jobj);
				jobj = new JSONObject();
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	public void insertContact(JSONObject contactData) {
		String sql = "INSERT INTO contacts";
		// event, payload, timestamp, sessionid

		String fieldStr = "(" + "firstname" + "," + "lastname" + "," + "dob"
				+ "," + "martialstatus" + "," + "salutation" +"," + "pincode" + "," + "emailid"
				+ "," + "emptype" + "," + "industry" + "," + "annualincome"
				+ "," + "timestamp" + ")";
		String quote = "'";
		String valueStr = "(" + quote + contactData.getString("firstname")
				+ quote + "," + quote + contactData.getString("lastname")
				+ quote + "," + quote + contactData.getString("dob")
				+ quote + "," + quote + contactData.getString("maritalstatus")
				+ quote + "," + quote + contactData.getString("salutation")
				+ quote + "," + quote + contactData.getString("pincode")
				+ quote + "," + quote + contactData.getString("emailid")
				+ quote + "," + quote + contactData.getString("emptype")
				+ quote + "," + quote + contactData.getString("industry")
				+ quote + "," + quote + contactData.getString("annualincome")
				+ quote + "," + quote + contactData.getString("timestamp")
				+ quote + ")";

		sql = sql + fieldStr + " VALUES " + valueStr;
		System.out.println(sql);
		executeSql(sql);

	}

}
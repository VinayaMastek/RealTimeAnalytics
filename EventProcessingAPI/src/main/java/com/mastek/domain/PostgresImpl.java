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
		/*
		 * String valueStr = "(" + quote + eventData.getString("event") + quote
		 * + "," + quote + eventData.getJSONObject("payload") + quote + "," +
		 * quote + eventData.getString("timestamp") + quote + "," + quote +
		 * eventData.getString("sessionid") + quote + ")";
		 */
		String valueStr = "(" + quote + eventData.getString("event") + quote
				+ "," + quote + eventData.getJSONObject("payload") + quote
				+ "," + quote + eventData.getString("timestamp") + quote + ","
				+ quote + eventData.getJSONObject("payload").getString("tran")
				+ quote + ")";

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
		String valueStr = "(" + quote + 'O' + quote + ")";

		sql = sql + fieldStr + " VALUES " + valueStr;
		System.out.println(sql);
		executeSql(sql);

		String sql1 = "select max(tranid) mtranid from tran";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql1);
			while (rs.next()) {
				tranid = rs.getInt("mtranid");
			}

			return tranid;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		}
		return tranid;
	}

	public String fetchTran() {
		String outtran;
		String quote = "'";
		String sql = "select tranid from tran where status=" + quote + "O"
				+ quote + "order by tranid desc";
		;
		System.out.println(sql);

		outtran = "";
		String com = " ";

		try {
			stmt = conn.createStatement();
			int tranid;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				tranid = rs.getInt("tranid");
				outtran = outtran + com + "{\"tran\":\"" + tranid + "\"}";
				com = ",";

			}

			return outtran;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		}
		return outtran;
	}

	public void closeTran(JSONObject edata) {
		String quote = "'";

		String sql = "update tran set status = " + quote + "C" + quote
				+ " where tranid=" + edata.getString("tranid");
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

	public void insertApplication(JSONObject applicationData) {
		String sql = "INSERT INTO application";
		String fieldStr = "(" + "title" +","+ "firstname" + "," + "lastname" + ","
				+ "dob" + "," + "maritalstatus" + "," + "address" + ","
				+ "pincode" + "," + "email" + "," + "emptype" + ","
				+ "industry" + "," + "annualincome" + "," + "loanamount" + ","
				 + "passportnumber"  + "," + "passportdate"  + ")";

		String quote = "'";
		String valueStr = "(" + quote + applicationData.getString("title") + quote
				+ "," + quote + applicationData.getString("firstName") + quote
				+ "," + quote + applicationData.getString("lastName") + quote
				+ "," + quote + applicationData.get("dob") + quote 
				+ "," + quote + applicationData.getString("maritalStatus") + quote
				+ "," + quote + applicationData.getString("address") + quote
				+ "," + quote + applicationData.getString("pincode") + quote
				+ "," + quote + applicationData.getString("email") + quote
				+ "," + quote + applicationData.getString("emptype") + quote
				+ "," + quote + applicationData.getString("industry") + quote
				+ "," + applicationData.get("annualincome") 
				+ "," + applicationData.get("loanamount") 
				+ "," + quote + applicationData.getString("passportnumber")+ quote 
				+ "," + applicationData.get("passportdate")
				+ ")";
		sql = sql + fieldStr + " VALUES " + valueStr;
		System.out.println(sql);
		executeSql(sql);
	}

	public JSONObject fetchApplication(Integer transactionId) {
		JSONObject jobj = new JSONObject();
		if (conn == null)
			connect();
		String sql = "";
		ResultSet rs = null;
		sql = "SELECT " + "	id,title,firstname,lastname,dob,"
				+ "maritalstatus, emptype,pincode, address, "
				+ "industry, annualincome, loanamount, email,status, "
				+ "passportnumber,passportdate, transactionid, "
				+ "timestamp " + "FROM application where transactionid = "
				+ transactionId;
		System.out.println(sql);
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				jobj.put("id", rs.getInt("event"));
				jobj.put("title", rs.getString("title"));
				jobj.put("firstname", rs.getString("firstname"));
				jobj.put("lastname", rs.getString("lastname"));
				jobj.put("dob", rs.getDate("dob"));
				jobj.put("maritalstatus", rs.getString("maritalstatus"));
				jobj.put("emptype", rs.getString("emptype"));
				jobj.put("pincode", rs.getString("pincode"));
				jobj.put("address", rs.getString("address"));
				jobj.put("industry", rs.getString("industry"));
				jobj.put("annualincome", rs.getDouble("annualincome"));
				jobj.put("loanamount", rs.getDouble("loanamount"));
				jobj.put("email", rs.getString("loanamount"));
				jobj.put("status", rs.getDouble("status"));
				jobj.put("passportnumber", rs.getString("passportnumber"));
				jobj.put("passportdate", rs.getDate("passportdate"));
				jobj.put("transactionid", rs.getInt("transactionid"));
				break;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jobj;
	}

	public void finalizeApplication(JSONObject applicationData) {
		String sql = "UPDATE application SET status = %s WHERE applicationid = %s";
		String quote = "'";
		sql = String.format(sql, quote + applicationData.getString("status")
				+ quote, applicationData.getString("id"));
		System.out.println(sql);
		executeSql(sql);
	}

	public void updateApplication(JSONObject applicationData) {
		String sql = "UPDATE application SET "
				+ "title = %s, "
				+ "firstname = %s, "
				+ "lastname = %s, "
				+ "dob = %s, "
				+ "maritalstatus = %s, "
				+ "pincode= %s, "
				+ "address = %s, "
				+ "emptype = %s, "
				+ "industry = %s, "
				+ "annualincome= %s, "
				+ "loanamount= %s, "
				+ "email= %s, "
				+ "passportnumber = %s, "
				+ "passportdate = %s, "
				+ "status = %s  "
				+ "WHERE "
				+ "applicationid = %s";
		String quote = "'";
		sql = String.format(sql, 
				quote + applicationData.getString("status") + quote ,
				quote + applicationData.getString("firstname") + quote ,
				quote + applicationData.getString("lastname") + quote ,
				quote + applicationData.getString("dob") + quote ,
				quote + applicationData.getString("maritalstatus") + quote ,
				quote + applicationData.getString("pincode") + quote ,
				quote + applicationData.getString("address") + quote ,
				quote + applicationData.getString("emptype") + quote ,
				quote + applicationData.getString("industry") + quote ,
				quote + applicationData.getDouble("annualincome") + quote ,
				quote + applicationData.getDouble("loanamount") + quote ,
				quote + applicationData.getString("email") + quote ,
				quote + applicationData.getString("passportnumber") + quote ,
				quote + applicationData.getString("passportdate") + quote ,
				applicationData.getString("id"));
		System.out.println(sql);
		executeSql(sql);
	}

}
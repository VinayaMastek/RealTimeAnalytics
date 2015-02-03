package com.mastek.domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import net.sf.json.xml.JSONTypes;

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

	public void insertApplication(JSONObject applicationData) {
		String sql = "INSERT INTO application";
		String fieldStr = "(" + "title" +","+ "firstname" + "," + "lastname" + ","
				+ "dob" + "," + "maritalstatus" + "," + "address" + ","
				+ "pincode" + "," + "email" + "," + "emptype" + ","
				+ "industry" + "," + "annualincome" + "," + "loanamount" + "," +"transactionid" + ","+"timestamp" + ","
				 + "passportnumber"  + "," + "passportdate"  + ")";

		String quote = "'";
		String pdt = null;
		String dt = null;

		if (!JSONUtils.isNull(applicationData.getString("dob")))
			dt ="to_date("+quote+applicationData.get("dob")+quote+",'dd/MM/yyyy')";

		if (!JSONUtils.isNull(applicationData.getString("passportdate")))
			pdt ="to_date("+quote+applicationData.get("passportdate")+quote+",'dd/MM/yyyy')";

		String valueStr = "(" + checkNull(applicationData.getString("title"))
				+ "," + checkNull(applicationData.getString("firstName"))
				+ "," + checkNull(applicationData.getString("lastName"))
				+ "," + dt
				+ "," + checkNull(applicationData.getString("maritalStatus"))
				+ "," + checkNull(applicationData.getString("address"))
				+ "," + checkNull(applicationData.getString("pincode"))
				+ "," + checkNull(applicationData.getString("email"))
				+ "," + checkNull(applicationData.getString("emptype"))
				+ "," + checkNull(applicationData.getString("industry"))
				+ "," + applicationData.get("annualincome") 
				+ "," + applicationData.get("loanamount") 
				+ "," + applicationData.getInt("transactionid") 
				+ "," + applicationData.getString("timestamp") 
				+ "," + checkNull(applicationData.getString("passportnumber")) 
				+ "," + pdt
				+ ")";
		
			
		sql = sql + fieldStr + " VALUES " + valueStr;
		System.out.println(sql);
		executeSql(sql);
	}

	
	
	private String checkNull(String value)
	{
		String quote = "'";
		if (value == null)
			return null;
		else 
			return quote + value + quote;
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
				jobj.put("id", rs.getInt("id"));
				jobj.put("title", rs.getString("title"));
				jobj.put("firstname", rs.getString("firstname"));
				jobj.put("lastname", rs.getString("lastname"));
				jobj.put("dob", rs.getString("dob"));
				jobj.put("maritalstatus", rs.getString("maritalstatus"));
				jobj.put("emptype", rs.getString("emptype"));
				
				jobj.put("pincode", rs.getString("pincode").contains("null")?"":rs.getString("pincode"));
				jobj.put("address", rs.getString("address").contains("null")?"":rs.getString("address"));
				jobj.put("industry", rs.getString("industry").contains("null")?"":rs.getString("industry"));
				jobj.put("annualincome", rs.getDouble("annualincome"));
				jobj.put("loanamount", rs.getDouble("loanamount"));
				jobj.put("email", rs.getString("email").contains("null")?"":rs.getString("email"));
				jobj.put("status", rs.getString("status")==null?"":rs.getString("status"));
				jobj.put("passportnumber", rs.getString("passportnumber").contains("null")?"":rs.getString("passportnumber")); 
				jobj.put("passportdate", "");
				jobj.put("transactionid", rs.getInt("transactionid"));
				jobj.put("timestamp", rs.getString("timestamp"));
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
				+ "status = %s,  "
				+ "timestamp = %s"
				+ " WHERE "
				+ " id = %s";
		String quote = "'";
		String dt=null;
		String pdt =null;
		
		if (!JSONUtils.isNull(applicationData.get("dob")))
			dt ="to_date("+quote+applicationData.get("dob")+quote+",'dd/MM/yyyy')";

		if (!JSONUtils.isNull(applicationData.get("passportdate")))
			pdt ="to_date("+quote+applicationData.get("passportdate")+quote+",'dd/MM/yyyy')";				

	/*	Double income;
		if (!JSONUtils.isNull(applicationData.get("annualincome"))) {
			income=(double) 0;
		} else income =  (Double) applicationData.get("annualincome");
		
		JSONUtils.doubleToString(d)
		if (!JSONUtils.isNull(applicationData.get("loanamount"))) {
			loan=(double) 0;
		} else loan =  applicationData.getDouble("loanamount");
		*/
		
		sql = String.format(sql, 
				checkNull( applicationData.getString("title") ) ,
				checkNull( applicationData.getString("firstName") ) ,
				checkNull( applicationData.getString("lastName") ) ,
				dt ,
				checkNull( applicationData.getString("maritalStatus") ) ,
				checkNull( applicationData.getString("pincode") ) ,
				checkNull( applicationData.getString("address") ) ,
				checkNull( applicationData.getString("emptype") ) ,
				checkNull( applicationData.getString("industry") ) ,
				JSONUtils.isNull(applicationData.get("annualincome"))?null:applicationData.get("annualincome"),
				JSONUtils.isNull(applicationData.get("loanamount"))?null:applicationData.get("loanamount"),
				checkNull( applicationData.getString("email") ) ,
				checkNull( applicationData.getString("passportnumber") ) ,
				pdt ,
				checkNull( applicationData.getString("status") ) ,
				checkNull( applicationData.getString("timestamp") ),
				applicationData.getString("id"));
		System.out.println(sql);
		executeSql(sql);
	}

}
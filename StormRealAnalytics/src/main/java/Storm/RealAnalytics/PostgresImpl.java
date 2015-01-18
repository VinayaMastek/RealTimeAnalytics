package Storm.RealAnalytics;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PostgresImpl implements Serializable{
	private static final long serialVersionUID = 1L;

	
	transient private Connection conn;
	
	
	
	transient private Statement stmt = null;

	
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
					"jdbc:postgresql://104.236.67.184:5432/postgres", "postgres",
					"online01");
			conn.setAutoCommit(false);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	private void commit()
	{
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	private void executeSql(String sql)
	{
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

	
	public JSONArray getEvents(String session)
	{
		JSONArray results = new JSONArray();
		JSONObject jobj = new JSONObject();
		
		if (conn == null)
			connect();

		String sql ="";
		ResultSet rs = null;
		sql = "SELECT event_id,event, payload, timestamp, sessionid, status FROM events where status = 'N' order by timestamp";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()){
				jobj.put("eventid", rs.getInt("event_id"));
				jobj.put("event", rs.getString("event"));
				jobj.put("payload", rs.getString("payload"));
				jobj.put("timestamp", rs.getString("timestamp"));
				jobj.put("sessionid", rs.getString("sessionid"));
				jobj.put("status", rs.getString("status"));
				results.add(jobj);
				jobj = new JSONObject();
				updateEvntStatus(rs.getInt("event_id"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}


	public void insertlog(String description)
	{
		String sql = "INSERT INTO logtable";
		//event, payload, timestamp, sessionid 
		
		String fieldStr = "("+"description"+")";
		String quote = "'";
		String valueStr = "("+ quote + description+ quote  +")";
		
		sql = sql + fieldStr + " VALUES " + valueStr;
		System.out.println(sql);
		executeSql(sql);
	}
	
	
	public void updateEvntStatus(Integer eventId)
	{
		String sql = "UPDATE EVENTS set status = 'P' where event_id = "+eventId;
		executeSql(sql);
	}

	
	
	public void createEvent(JSONObject eventData) {
		String sql = "INSERT INTO events";
		//event, payload, timestamp, sessionid 
		
		String fieldStr = "("+"event"+","+"payload"+","+"timestamp"+","+"sessionid"+")";
		String quote = "'";
		Date dt = new Date();
		String valueStr = "("+ quote + eventData.getString("event") + quote +"," 
							 + quote + eventData.getJSONObject("payload") + quote +","
							 + quote + dt.toString() + quote +","
							 + quote + eventData.getString("sessionid")+ quote +")";
		
		sql = sql + fieldStr + " VALUES " + valueStr;
		System.out.println(sql);
		executeSql(sql);
	}
}
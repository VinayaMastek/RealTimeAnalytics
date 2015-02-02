package Storm.RealAnalytics;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class StatBolt extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String webserver;
	HttpClient client;

	public void prepare(Map config, TopologyContext context,
			OutputCollector collector) {
		this.webserver = config.get("webserver").toString();
		reconnect();
	}

	private void reconnect() {
		client = new DefaultHttpClient();
	}

	public void execute(Tuple tuple) {
		String session = null;
		String action = null;
		if (tuple != null) {
			System.out.println("Inside Stat blot");
			if (tuple.contains("session"))
			{
				 session = tuple.getStringByField("session");
				 if (tuple.contains("action"))
					 action=tuple.getStringByField("action");
				 else
					 action = "NOACTION";
			}
			
			if (tuple.contains("eventoutput"))
			{
				 JSONObject js = (JSONObject) tuple.getValueByField("eventoutput");
				 System.out.println("######" + js.toString());
				 action = tuple.getStringByField("action");
				 session = js.getString("sessionid");
			}

			String description = tuple.getStringByField("desc");
			String content = "{ \"tran\": \"" + session + "\",\"msg\": \""
					+ description +"\" ,\"action\": \""
					+ action +"\"}";

			System.out.println(webserver.toString());
			System.out.println(content);
			HttpPost post = new HttpPost(webserver);

			try {
				post.setEntity(new StringEntity(content));
				post.setHeader("Content-Type", "application/json");
				
				HttpResponse response = client.execute(post);
				org.apache.http.util.EntityUtils.consume(response.getEntity());
				System.out.println("executed post");
			} catch (Exception e) {
				e.printStackTrace();
				reconnect();
			}
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// this bolt does not emit anything
	}

	@Override
	public void cleanup() {
		client = null;
	}
}

package Storm.RealAnalytics;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class EmailNameChangeBolt extends BaseRichBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private HashMap<String, String> emailName = null;
	private String session = "";
	private String eventType = "";

	public void prepare(Map config, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		this.emailName = new HashMap<String, String>();
	}

	public void execute(Tuple tuple) {
		JSONObject eventObj = (JSONObject) tuple.getValueByField("eventoutput");
		this.session = eventObj.getString("sessionid");
		this.eventType = eventObj.getString("event");
		JSONObject payload = eventObj.getJSONObject("payload");
		
		if (payload != null) {
			System.out
					.println("************Inside emailname change bolt  - if payload**************");

			setEmailChange(payload.getString("field"));
			checkNameChangeAfterEmail(payload.getString("field"),
					payload.getString("value"));
		}

	}

	private void checkNameChangeAfterEmail(String field, String value) {
		// TODO Auto-generated method stub

		System.out.println("******Inside checkname field = " + field);
		if (field.equals("firstname") && emailName.containsKey(session)) {
			System.out
					.println("************Inside emailname change bolt  - if exists in hash map**************");

			String desc = "For Session : " + session
					+ " First Name changed after Email where Firstname : "
					+ value;

			emailName.remove(session);
			this.collector
					.emit(new Values(session, "EmailNameChange", desc, 0L,"INCREASE_RISK"));
		}
	}

	private void setEmailChange(String field) {
		if (field.equals("emailid"))
			emailName.put(this.session, "Y");
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("session", "eventType", "desc", "count","action"));
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		super.cleanup();
		this.emailName.clear();
		System.out.println("inside email change cleanup");
	}
}

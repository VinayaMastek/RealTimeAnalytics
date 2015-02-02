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

public class PassPortDetailsEnteredBolt extends BaseRichBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private HashMap<String, String> passportDetails = null;
	private String session = "";
	private String eventType = "";

	public void prepare(Map config, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		this.passportDetails = new HashMap<String, String>();
	}

	public void execute(Tuple tuple) {
		JSONObject eventObj = (JSONObject) tuple.getValueByField("eventoutput");
		this.session = eventObj.getString("sessionid");
		this.eventType = eventObj.getString("event");
		JSONObject payload = eventObj.getJSONObject("payload");
		
		if (payload != null) {
			if (this.eventType.equals("passportnochange"))
				setChange("passportno");

			if (this.eventType.equals("passportdtchange"))
				setChange("passportdt");
		}
		
		if (passportDetails.containsKey(session +"passportno") && passportDetails.containsKey(session+"passportdt"))
		{
			String desc = "For Session : " + session
					+ " Passport number and Passport Date provided";

			passportDetails.remove(session+"passportno");
			passportDetails.remove(session+"passportdt");
			this.collector
					.emit(new Values(session, "PassportDtLProvided", desc, 0L,"PASSPORTDTLPROVIDED"));
		}

	}

	private void setChange(String field) {
		passportDetails.put(this.session+field, "Y");
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("session", "eventType", "desc", "count","action"));
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		super.cleanup();
		this.passportDetails.clear();
		System.out.println("inside email change cleanup");
	}
}

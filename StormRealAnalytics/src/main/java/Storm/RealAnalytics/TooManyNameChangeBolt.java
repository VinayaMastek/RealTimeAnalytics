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

public class TooManyNameChangeBolt extends BaseRichBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private HashMap<String, Long> counts = null;

	public void prepare(Map config, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		this.counts = new HashMap<String, Long>();
	}

	public void execute(Tuple tuple) {
		JSONObject eventObj = (JSONObject) tuple.getValueByField("eventoutput");
		String session = eventObj.getString("sessionid");
		String eventType = eventObj.getString("event");

		if (eventType.equals("change")) {
			JSONObject payload = eventObj.getJSONObject("payload");
			
			if (payload.getString("field").equals("firstname")) {
				Long count = this.counts.get(session);
				if (count == null) {
					count = 0L;
				}
				count++;
				this.counts.put(session, count);

				if (count > 2) {
					String desc = "In the " + session + " : "
							+ "firstname was changed more then " + count
							+ "times";
					System.out.println(desc);
					eventType = "TooManyNameChange";
					eventObj.put("event", eventType);
					this.collector.emit(new Values(eventObj, desc,"APPUI_ACCEPT_PASSPORT"));
				}
			}

		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("eventoutput", "desc","action"));
	}
}

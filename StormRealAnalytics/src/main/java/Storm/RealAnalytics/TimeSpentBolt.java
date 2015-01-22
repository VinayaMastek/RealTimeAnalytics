package Storm.RealAnalytics;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONObject;

public class TimeSpentBolt extends BaseRichBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private HashMap<String, String> timeStamp = null;
	private String session = "";
	private String eventType = "";

	public void prepare(Map config, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		this.timeStamp = new HashMap<String, String>();
	}

	public void execute(Tuple tuple) {
		JSONObject eventObj = (JSONObject) tuple.getValueByField("eventoutput");
		this.session = eventObj.getString("sessionid");
		this.eventType = eventObj.getString("event");
		JSONObject payload = eventObj.getJSONObject("payload");
		if (eventType.equals("TooManyNameChange"))
				return;
		if (payload != null) {
			System.out
					.println("************Inside Timespent change bolt  - if payload**************");

		    if (!timeStamp.containsKey(this.session))
		    	timeStamp.put(this.session, eventObj.getString("timestamp"));
			calculateTime(eventObj.getString("timestamp"));
		}

	}

	private void calculateTime(String timeStampValue) {
		// TODO Auto-generated method stub

		System.out.println("******Inside calculate time");
		
		
		Date current = new Date(Long.parseLong(timeStampValue));
		Date start = new Date(Long.parseLong(timeStamp.get(this.session)));

		System.out.println("current - "+current.toString());
		System.out.println("start - "+start.toString());

		long duration  = current.getTime() - start.getTime();

		long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration)%60;
		long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration)%60;
		long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);		
		
			String desc = "For Session : " + session
					+ " Time Spent is : "
					+ diffInHours +" Hours "+ diffInMinutes +" Minutes " + diffInSeconds + " Seconds ";

			this.collector
					.emit(new Values(session, "TimeSpent", desc, diffInHours,diffInMinutes,diffInSeconds,"TIMESPENT"));
	}


	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("session", "eventType", "desc", "hours","min","sec","action"));
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		super.cleanup();
		this.timeStamp.clear();
		System.out.println("inside TimeStamp cleanup");
	}
}

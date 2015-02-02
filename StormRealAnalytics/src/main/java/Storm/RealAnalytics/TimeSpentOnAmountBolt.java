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

public class TimeSpentOnAmountBolt extends BaseRichBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private HashMap<String, String> timeStamp = null;
	private HashMap<String, Long> timeStampCalc = null;
	private String session = "";
	private String eventType = "";
	

	public void prepare(Map config, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		this.timeStamp = new HashMap<String, String>();
		this.timeStampCalc = new HashMap<String, Long>();
	}

	public void execute(Tuple tuple) {
		JSONObject eventObj = (JSONObject) tuple.getValueByField("eventoutput");
		this.session = eventObj.getString("sessionid");
		this.eventType = eventObj.getString("event");
		JSONObject payload = eventObj.getJSONObject("payload");
		
		String focuskey  = this.session +"focus";
		
		String focusTimeIn = null;
		String blurTime = null;

		if (eventType.equals("focus"))
		{
		    if (timeStamp.containsKey(focuskey))
		    	timeStamp.remove(focuskey);
	    	timeStamp.put(focuskey, eventObj.getString("timestamp"));
		}	
		
		if (eventType.equals("blur"))
		{
		    if (timeStamp.containsKey(focuskey))
		    	focusTimeIn = timeStamp.get(focuskey);
		    blurTime = eventObj.getString("timestamp");
		    
		}
		
	    if (focusTimeIn !=null && blurTime !=null)
	    	calculateTime(focusTimeIn, blurTime);

	}

	private void calculateTime(String focusTimeIn, String blurTime) {
		// TODO Auto-generated method stub

		System.out.println("******Inside calculate time");
		
		
		Date current = new Date(Long.parseLong(blurTime));
		Date start = new Date(Long.parseLong(focusTimeIn));

		System.out.println("blur - "+current.toString());
		System.out.println("focus - "+start.toString());

		long duration  = current.getTime() - start.getTime();
		long oldDuration =0;
		
		if (this.timeStampCalc.containsKey(this.session))
			oldDuration = this.timeStampCalc.get(this.session);

		if (oldDuration < duration)
		{

			this.timeStampCalc.put(this.session, duration);
			long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration)%60;
			long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration)%60;
			long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);		
			
			String desc = "For Session : " + session
					+ " Time Spent on amount is : "
					+ diffInHours +" Hours "+ diffInMinutes +" Minutes " + diffInSeconds + " Seconds ";
	
			this.collector
					.emit(new Values(session, "TimeSpentAmt", desc, diffInHours,diffInMinutes,diffInSeconds,"TIMESPENTONAMT"));
		}
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

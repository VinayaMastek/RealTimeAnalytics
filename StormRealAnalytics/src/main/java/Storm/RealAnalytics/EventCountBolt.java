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

public class EventCountBolt extends BaseRichBolt{
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
        Long count = this.counts.get(session);
    	if(count == null){
	        count = 0L;
	    }
		
        if (eventType.equals("change"))
        {
			count++;
			this.counts.put(session, count);
		
		    String desc = session + " : " + count;
		    this.collector.emit(new Values(session,eventType, desc, count,"NOACTION"));
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("session","eventType", "desc","count","action"));
    }
}

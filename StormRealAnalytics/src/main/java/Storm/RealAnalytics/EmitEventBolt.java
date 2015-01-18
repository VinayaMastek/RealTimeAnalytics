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

public class EmitEventBolt extends BaseRichBolt{
    private OutputCollector collector;
    private HashMap<String, Long> counts = null;
    private PostgresImpl pg = null;
    
    public void prepare(Map config, TopologyContext context, 
            OutputCollector collector) {
    	if (pg == null)
    	{
    		pg = new PostgresImpl();
    		pg.connect();
    	}
    	
		
    	this.collector = collector;
        this.counts = new HashMap<String, Long>();
    }

    public void execute(Tuple tuple) {
    	JSONObject eventObj = (JSONObject) tuple.getValueByField("eventoutput");
        createEvent(eventObj);
    }

	private void createEvent(JSONObject eventData) {
		// TODO Auto-generated method stub
		pg.createEvent(eventData);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		super.cleanup();
		pg.close();
	}
}

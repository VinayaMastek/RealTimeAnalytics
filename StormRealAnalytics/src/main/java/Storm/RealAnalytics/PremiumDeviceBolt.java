package Storm.RealAnalytics;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import java.util.Map;

public class PremiumDeviceBolt extends BaseRichBolt{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;

    public void prepare(Map config, TopologyContext context, 
            OutputCollector collector) {
        this.collector = collector;
    }

    public void execute(Tuple tuple) {
    	if  (tuple != null){
    		String device = (String) tuple.getStringByField("desc");
    		String session = tuple.getStringByField("session");
    		if (device.contains("iPhone"))
    		{
    			this.collector.emit(new Values(session, "premiumdevice", "Y","PREMIUMDEVICE"));
    		}
    	}
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("session","eventType", "desc","action"));
    }
}

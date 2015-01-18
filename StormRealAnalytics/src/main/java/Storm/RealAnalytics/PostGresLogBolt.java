package Storm.RealAnalytics;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import java.util.Map;

public class PostGresLogBolt extends BaseRichBolt {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PostgresImpl pg =null;

    public void prepare(Map config, TopologyContext context, OutputCollector collector) {
        if (pg == null)
        {	
        	pg = new PostgresImpl();
        	pg.connect();
        }
    }

    public void execute(Tuple tuple) {
    	if (tuple != null){
	    	String description = tuple.getStringByField("desc");
	        pg.insertlog(description);
	    }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // this bolt does not emit anything
    }

    
    @Override
    public void cleanup() {
		super.cleanup();

        if (pg != null)
        	pg.close();
    }
}

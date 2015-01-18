package Storm.RealAnalytics;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.util.Map;

public class EmailBolt extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute(Tuple tuple) {
		if (tuple != null) {
			String description = tuple.getStringByField("desc");
			System.out.println(description);
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// this bolt does not emit anything
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		// TODO Auto-generated method stub

	}

}

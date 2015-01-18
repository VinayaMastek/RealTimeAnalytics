package Storm.RealAnalytics;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.util.Map;

import net.sf.json.JSONArray;
import Storm.RealAnalytics.PostgresImpl;
import Storm.RealAnalytics.Utils;

public class EventSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	private JSONArray events;
	private int index = 0;
	private PostgresImpl pg;
	
	public EventSpout() {
		super();
		pg = new PostgresImpl();
		pg.connect();
		events = pg.getEvents("session");
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("eventoutput"));
	}

	public void open(Map config, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
	}

	public void nextTuple() {
		if (index < events.toArray().length) {
			this.collector.emit(new Values(events.toArray()[index]));
			index++;
		} else{
			index =0;
			events = pg.getEvents("session");
	
		}
		Utils.waitForMillis(1);
	}
	
}

package Storm.RealAnalytics;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import static Storm.RealAnalytics.Utils.*;

public class EventCountTopology {

    private static final String EVENT_SPOUT_ID = "event-spout";
    private static final String EMIT_BOLT_ID = "emit-bolt";
    
    private static final String COUNT_BOLT_ID = "count-bolt";
    private static final String EMAILNAMECHANGE_BOLT_ID = "email-name-change-bolt";
    private static final String TOOMANYNAMECHANGE_BOLT_ID = "too-many-name-change-bolt";
    
    private static final String POSTGRESLOG_BOLT_ID = "report-bolt";
    private static final String POSTGRESLOG_BOLT_ID_EVNT2 = "report-bolt2";
    private static final String POSTGRESLOG_BOLT_ID_EVNT3 = "report-bolt3";
    
    private static final String EMAIL_BOLT_ID = "email-bolt";
    private static final String EMAIL_BOLT_ID_EVNT2 = "email-bolt2";
    private static final String EMAIL_BOLT_ID_EVNT3 = "email-bolt3";
    
    private static final String STAT_BOLT_ID = "stat_blot";
    private static final String STAT_BOLT_ID_EVTN2 = "stat_blot2";
    private static final String STAT_BOLT_ID_EVTN3 = "stat_blot3";
    private static final String STAT_BOLT_ID_EVTN4 = "stat_blot4";

    private static final String TIME_SPENT_BOLT_ID = "timespent_blot";

    
    private static final String TOPOLOGY_NAME = "event-count-topology";

    public final static String WEBSERVER = "http://localhost:8080/EventProcessingAPI/rest/events/countUpdt";
	public final static long DOWNLOAD_TIME = 100;
	
    
    public static void main(String[] args) throws Exception {
    	EventSpout spout = new EventSpout();
    	
        EventCountBolt countBolt = new EventCountBolt();
        EmailNameChangeBolt emailNamechangeBolt = new EmailNameChangeBolt();
        TooManyNameChangeBolt tooManyNameChangeBolt = new TooManyNameChangeBolt();
        
        PostGresLogBolt pgBolt = new PostGresLogBolt();
        PostGresLogBolt emailChangePgBolt = new PostGresLogBolt();
        PostGresLogBolt tooManyNameChangePgBolt = new PostGresLogBolt();
        
        EmailBolt emailBolt = new EmailBolt();
        EmailBolt emailChangeEmailBolt = new EmailBolt();
        EmailBolt tooManyNameChangeEmailBolt = new EmailBolt();
        
        StatBolt sb = new StatBolt();
        StatBolt sb2 = new StatBolt();
        StatBolt sb3 = new StatBolt();
        StatBolt sb4 = new StatBolt();

        TimeSpentBolt tsb = new TimeSpentBolt();
        
        EmitEventBolt emitEventBolt  = new EmitEventBolt();

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(EVENT_SPOUT_ID, spout);
        
        builder.setBolt(COUNT_BOLT_ID, countBolt)
        		.setNumTasks(1)
                .fieldsGrouping(EVENT_SPOUT_ID, new Fields("eventoutput"));

        builder.setBolt(EMAILNAMECHANGE_BOLT_ID, emailNamechangeBolt)
		.setNumTasks(1)
        .fieldsGrouping(EVENT_SPOUT_ID, new Fields("eventoutput"));

        builder.setBolt(TOOMANYNAMECHANGE_BOLT_ID, tooManyNameChangeBolt)
		.setNumTasks(1)
        .fieldsGrouping(EVENT_SPOUT_ID, new Fields("eventoutput"));
        
        
        builder.setBolt(POSTGRESLOG_BOLT_ID, pgBolt)
                .globalGrouping(COUNT_BOLT_ID);

        builder.setBolt(POSTGRESLOG_BOLT_ID_EVNT2, emailChangePgBolt)
        .globalGrouping(EMAILNAMECHANGE_BOLT_ID);

        builder.setBolt(POSTGRESLOG_BOLT_ID_EVNT3, tooManyNameChangePgBolt)
        .globalGrouping(TOOMANYNAMECHANGE_BOLT_ID);
        
        
        builder.setBolt(EMAIL_BOLT_ID, emailBolt)
        .globalGrouping(COUNT_BOLT_ID);
        
        builder.setBolt(EMAIL_BOLT_ID_EVNT2, emailChangeEmailBolt)
        .globalGrouping(EMAILNAMECHANGE_BOLT_ID);

        builder.setBolt(EMAIL_BOLT_ID_EVNT3, tooManyNameChangeEmailBolt)
        .globalGrouping(TOOMANYNAMECHANGE_BOLT_ID);

        
        builder.setBolt(TIME_SPENT_BOLT_ID, tsb)
        .globalGrouping(EVENT_SPOUT_ID);

        
        builder.setBolt(STAT_BOLT_ID, sb)
        .globalGrouping(COUNT_BOLT_ID);      

       builder.setBolt(STAT_BOLT_ID_EVTN2, sb2)
        .globalGrouping(EMAILNAMECHANGE_BOLT_ID);

        builder.setBolt(STAT_BOLT_ID_EVTN3, sb3)
        .globalGrouping(TOOMANYNAMECHANGE_BOLT_ID);

        builder.setBolt(STAT_BOLT_ID_EVTN4, sb4)
        .globalGrouping(TIME_SPENT_BOLT_ID);

        builder.setBolt(EMIT_BOLT_ID, emitEventBolt)
        .globalGrouping(TOOMANYNAMECHANGE_BOLT_ID);

        Config conf = new Config();
        conf.put("webserver", WEBSERVER);
        conf.put("download-time", DOWNLOAD_TIME);
        
/*   	
  		config.setNumWorkers(1);
        StormSubmitter.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
 */       
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(TOPOLOGY_NAME, conf, builder.createTopology());
        waitForSeconds(10);
/*        cluster.killTopology(TOPOLOGY_NAME);
        cluster.shutdown();
*/    }
}

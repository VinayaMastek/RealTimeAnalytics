truncate events;

truncate logtable;
update events set status = 'N';

select * from events
select * from logtable

SELECT event_id,event, payload, timestamp, sessionid, status FROM events where status = 'N' order by timestamp

insert into events (event,sessionid,payload,timestamp,status)
select event,sessionid,payload,timestamp,status from backup;


insert into events (event,payload,"timestamp",sessionid, status) values
('change','{"field":"email","value":"a@b.com"}','1420199684747','89D8B5EDC9BFD9BE69C577B7593B84F1','N');

insert into events (event,payload,"timestamp",sessionid, status) values
('change','{"field":"firstname","value":"Suraj"}','1420199684750','89D8B5EDC9BFD9BE69C577B7593B84F1','N');

insert into events (event,payload,"timestamp",sessionid, status) values
('change','{"field":"firstname","value":"Vaikunt"}','1420199684759','89D8B5EDC9BFD9BE69C577B7593B84F1','N');

insert into events (event,payload,"timestamp",sessionid, status) values
('change','{"field":"firstname","value":"Robin"}','1420199684765','89D8B5EDC9BFD9BE69C577B7593B84F1','N');


insert into events (event,sessionid,status) values
('Output','89D8B5EDC9BFD9BE69C577B7593B84F1','N');

delete from events where event like 'TooManyNameChange'
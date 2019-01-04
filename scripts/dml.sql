psql -U esabirzh -d mixed -a -f ./scripts/ddl.sql

COPY flows(flow_id, start_active, last_active, service_port, protocol) FROM '/Users/esabirzh/mixed/output-data/message3.log' with null as 'null' delimiter as E'\t';
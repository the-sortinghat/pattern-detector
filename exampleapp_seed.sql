DO $$
DECLARE
    table_names TEXT[] := ARRAY['consumedoperations', 'databases', 'databaseusages', 'messagechannels', 'modules', 'operations', 'publications', 'servicedependencies', 'services', 'subscriptions'];
    table_name TEXT;
BEGIN
    -- Loop through each table name and reset the sequence
    FOREACH table_name IN ARRAY table_names
    LOOP
        -- Remove single quotes around table names
        EXECUTE format('UPDATE %I SET id = nextval(%I || ''_id_seq'');', table_name, table_name);
    END LOOP;
END $$;

INSERT INTO modules(id) VALUES
(1), (2), (3), (4), (5), (6), (7), (8);

INSERT INTO databases(name, type) VALUES
('database1', 'oracle'),
('database2', 'oracle'),
('database3', 'sqlserver');

INSERT INTO services(name, system_name, module_id) VALUES
('Service1', 'exampleapp',1),
('Service2', 'exampleapp',2),
('Service3', 'exampleapp',3),
('Service4', 'exampleapp',4),
('Service5', 'exampleapp',5),
('Service6', 'exampleapp',6),
('Service7', 'exampleapp',7),
('Service8', 'exampleapp',8);

INSERT INTO databaseusages(service_id, database_id, access_mode) VALUES
(1, 1, 'rw'),
(3, 1, 'rw'),
(4, 1, 'rw'),
(1, 2, 'rw'),
(2, 2, 'rw'),
(4, 2, 'rw'),
(7, 3, 'rw');

INSERT INTO operations(verb, uri, exposer_id) VALUES
('GET',    '/api/v1', 1),
('POST',   '/cache', 2),
('GET',    '/checkingaccount', 2),
('POST',   '/customerimport/v1/updatemigration', 2),
('POST',   '/customerimport/v2/startmigration', 2),
('POST',   '/customerimport/v2/updatecustomerintegration', 2),
('GET',    '/pending/v2/pending', 2),
('GET',    '/api/v1', 3),
('GET',    '/api/getServices', 3),
('POST',   '/api/createsysintaccount', 3),
('GET',    '/api/checkiscustomer', 4),
('GET',    '/api/validate-prospect', 4),
('POST',   '/api/save-customer', 4),
('POST',   '/api/send-email', 4),
('GET',    '/api/prospect-elastic-service', 5),
('GET',    '/api/multiple-account', 6),
('POST',   '/api/customer-event', 6),
('POST',   '/api/process-multiple-account', 6),
('POST',   '/api/sysint-integration', 6),
('GET',    '/api/v1', 7),
('POST',   '/sendtobureau', 7),
('GET',    '/accountdetails', 7),
('GET',    '/registrationstatus', 7),
('GET',    '/registrationbyphonenumber', 7),
('GET',    '/api/bureau', 8),
('GET',    '/api/v1', 8),
('GET',    '/api/v2', 8),
('GET',    '/api/v3', 8);

INSERT INTO consumedoperations(operation_id, consumer_id) VALUES
(10, 6), (19, 4), (13, 3), (14, 3), (14, 7), (21, 4), (21, 8), (23, 4), (23, 8);

INSERT INTO messagechannels(name) VALUES
('import.exchange');

INSERT INTO publications(channel_id, publisher_id) VALUES
(1, 2);

INSERT INTO subscriptions(channel_id, subscriber_id) VALUES
(1, 1);

INSERT INTO servicedependencies(service_id, service_dep_id) VALUES
(3, 4),
(6, 3),
(5, 4),
(4, 7),
(4, 6),
(8, 7),
(7, 4);
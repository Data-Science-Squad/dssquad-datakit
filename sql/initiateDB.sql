CREATE TABLE incident(
   id INT NOT NULL AUTO_INCREMENT,
   incident_id INT(12) NOT NULL,
   incident_date DATETIME,
   case_number VARCHAR(50) NOT NULL,
   address_id INT(12) NOT NULL,
   incident_type_id INT(3) NOT NULL,
   PRIMARY KEY ( id )
);
CREATE TABLE incident_type(
   id INT NOT NULL AUTO_INCREMENT,
   incident_type VARCHAR(50) NOT NULL,
   incident_parent_id INT(12) NOT NULL,
   PRIMARY KEY ( id )
);
CREATE TABLE incident_type_parent(
   id INT NOT NULL,
   incident_parent VARCHAR(50) NOT NULL,
   PRIMARY KEY ( id )
);
CREATE TABLE address(
   id INT NOT NULL AUTO_INCREMENT,
   address1 VARCHAR(255) ,
   address2 VARCHAR(255) ,
   location VARCHAR(255) ,
   council_district VARCHAR(50) ,
   police_district VARCHAR(50) ,
   neighborhood VARCHAR(50) ,
   census_block_group VARCHAR(5) ,
   zipcode VARCHAR(5) ,
   PRIMARY KEY ( id )
);
CREATE TABLE date(
   id INT NOT NULL AUTO_INCREMENT,
   date DATETIME NOT NULL,
   name VARCHAR(50) ,
   date_type INT(2) NOT NULL,
   PRIMARY KEY ( id )
);
CREATE TABLE date_type(
   id INT NOT NULL AUTO_INCREMENT,
   date_type VARCHAR(50) NOT NULL,
   PRIMARY KEY ( id )
);
CREATE TABLE weather(
   id INT NOT NULL AUTO_INCREMENT,
   date DATETIME NOT NULL,
   tempF INT(3) NOT NULL,
   type_id INT(3) NOT NULL,
   PRIMARY KEY ( id )
);
CREATE TABLE weather_type(
   id INT NOT NULL AUTO_INCREMENT,
   weather_type VARCHAR(50) NOT NULL,
   PRIMARY KEY ( id )
);
ALTER TABLE incident ADD FOREIGN KEY (address_id) REFERENCES address(id);
ALTER TABLE incident ADD FOREIGN KEY (incident_date) REFERENCES dates(date);
ALTER TABLE incident ADD FOREIGN KEY (incident_date) REFERENCES weather(date);
ALTER TABLE incident ADD FOREIGN KEY (incident_type_id) REFERENCES incident_type(id);
ALTER TABLE incident_type ADD FOREIGN KEY (incident_parent_id) REFERENCES incident_type_parent(id);

CREATE VIEW full_incidents AS SELECT incident.incident_id, incident.case_number, incident_date, incident_type.incident_type, incident_type_parent.incident_parent,address.location, 
address.address1, address.address2, address.neighborhood, address.council_district, address.police_district, address.census_block_group, address.zipcode
FROM incident
LEFT JOIN incident_type ON incident.incident_type_id = incident_type.id
LEFT JOIN incident_type_parent ON incident_type.incident_parent_id = incident_type_parent.id
LEFT JOIN address ON incident.address_id = address.id;

CREATE INDEX incident_date_index ON incident (incident_date);
CREATE INDEX incident_type_index ON incident_type (incident_type);
CREATE INDEX neighborhood_index ON address (neighborhood);
CREATE INDEX police_district_index ON address (police_district);
CREATE INDEX council_district_index ON address (council_district);

CREATE VIEW all_dates AS SELECT date.date, date.name, date_type.date_type FROM date LEFT JOIN date_type ON date.date_type = date_type.id;
CREATE VIEW all_neighborhoods AS SELECT DISTINCT neighborhood FROM address;
CREATE VIEW all_police_districts AS SELECT DISTINCT police_district FROM address;
CREATE VIEW all_council_districts AS SELECT DISTINCT council_district FROM address;
CREATE VIEW all_zipcodes AS SELECT DISTINCT zipcode FROM address;

INSERT INTO incident_type_parent VALUES (1,'Assault'),(2,'Breaking & Entering'),(3,'Homicide'),(4,'Other Sexual Offense'),(5,'parent_incident_type'),(6,'Robbery'),(7,'Sexual Assault'),(8,'Sexual Offense'),(9,'Theft'),(10,'Theft of Vehicle');
INSERT INTO incident_type  VALUES (1,'ASSAULT',1),(2,'AGGR ASSAULT',1),(3,'AGG ASSAULT ON P/OFFICER',1),(4,'BURGLARY',2),(5,'Breaking & Entering',2),(6,'MURDER',3),(7,'CRIM NEGLIGENT HOMICIDE',3),(8,'MANSLAUGHTER',3),(9,'Homicide',3),(10,'SEXUAL ABUSE',4),(11,'incident_type_primary',5),(12,'ROBBERY',6),(13,'RAPE',7),(14,'SEXUAL ABUSE',7),(15,'Sexual Assault',7),(16,'Other Sexual Offense',7),(17,'LARCENY/THEFT',8),(18,'THEFT OF SERVICES',8),(19,'Theft',8),(20,'UUV',9),(21,'Theft of Vehicle',9);
INSERT INTO date_type VALUES (1,'Annual monthly observance'),(2,'Christian'),(3,'Clock change/Daylight Saving Time'),(4,'COVID-19 Lockdown'),(5,'Federal Holiday'),(6,'Hindu Holiday'),(7,'Jewish commemoration'),(8,'Jewish holiday'),(9,'Muslim'),(10,'Observance'),(11,'Orthodox'),(12,'Season'),(13,'Sporting event');
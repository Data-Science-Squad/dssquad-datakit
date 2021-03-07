/*CREATE TABLES FOR ML PREDICTION / PERFORMANCE*/
CREATE TABLE predictions(
   id INT NOT NULL AUTO_INCREMENT,
   location VARCHAR(50) NOT NULL,
   level VARCHAR(50) NOT NULL,
   freq VARCHAR(50) NOT NULL,
   start_date DATE NOT NULL,   
	end_date DATE NOT NULL,
   predicted_incidents FLOAT(8,6) NOT NULL,
   lower_predicted_incidents FLOAT(8,6) NOT NULL,
   upper_predicted_incidents FLOAT(8,6) NOT NULL,
   PRIMARY KEY ( id )
);
CREATE TABLE performance(
   id INT NOT NULL AUTO_INCREMENT,
   entity VARCHAR(50) NOT NULL,
   level VARCHAR(50) NOT NULL,
   freq VARCHAR(50) NOT NULL,
   rmse FLOAT(8,6) NOT NULL,
   PRIMARY KEY ( id )
);


/*TRUNCATE CONTENTS OF TABLES */

TRUNCATE TABLE predictions;
TRUNCATE TABLE performance;

/*INSERT INTO TABLES */

INSERT INTO predictions (location,level,freq,start_date,end_date,predicted_incidents,lower_predicted_incidents,upper_predicted_incidents) VALUES ('Allentown','Neighborhood','Daily','2021-03-06','2021-03-06',40.23235,35.333,48.2323232),('Allentown','Neighborhood','Weekly','2021-03-15','2021-03-22',40.23235,35.333,48.2323232),('Allentown','Neighborhood','Monthly','2021-03-01','2021-04-01',40.23235,35.333,48.2323232);

INSERT INTO performance (entity,level,freq,rmse) VALUES ('Allentown','Neighborhood','Weekly',3.3),('Allentown','Neighborhood','Daily',3.5),('Allentown','Neighborhood','Monthly',3.5);
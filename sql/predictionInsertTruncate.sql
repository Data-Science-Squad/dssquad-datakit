
/*DELETE CONTENT  OF DAY PREDICATION TABLES*/
TRUNCATE TABLE police_district_predictions_day;
TRUNCATE TABLE council_district_predictions_day;
TRUNCATE TABLE neighborhood_predictions_day;
TRUNCATE TABLE zip_predictions_day;

/*INSERT INTO TABLE id,name,date,double*/
INSERT INTO police_district_predictions_day (police_district,dy,predicted_incidents) VALUES ('DISTRICT A','2021-03-06',40.23235), ('DISTRICT B','2021-03-06',38.23235), ('DISTRICT C','2021-03-06',40.23238);
INSERT INTO council_district_predictions_day (council_district,dy,predicted_incidents) VALUES ('UNIVERSITY','2021-03-06',40.23235), ('NIAGARA','2021-03-06',38.23235), ('DELAWARE','2021-03-06',40.23238);
INSERT INTO neighborhood_predictions_day (neighborhood,dy,predicted_incidents) VALUES ('Allentown','2021-03-06',40.23235), ('Central','2021-03-06',38.23235), ('First Ward','2021-03-06',40.23238);
INSERT INTO zip_predictions_day (zipcode,dy,predicted_incidents) VALUES ('14212','2021-03-06',40.23235), ('14207','2021-03-06',38.23235), ('14222','2021-03-06',40.23238);

/*DELETE CONTENT  OF WEEK PREDICATION TABLES*/
TRUNCATE TABLE police_district_predictions_week;
TRUNCATE TABLE council_district_predictions_week;
TRUNCATE TABLE neighborhood_predictions_week;
TRUNCATE TABLE zip_predictions_week;

/*INSERT INTO TABLE id,name,date,double*/
INSERT INTO police_district_predictions_week (police_district,end_of_week,predicted_incidents) VALUES ('DISTRICT A','2021-03-06',40.23235), ('DISTRICT B','2021-03-06',38.23235), ('DISTRICT C','2021-03-06',40.23238);
INSERT INTO council_district_predictions_week (council_district,end_of_week,predicted_incidents) VALUES ('UNIVERSITY','2021-03-06',40.23235), ('NIAGARA','2021-03-06',38.23235), ('DELAWARE','2021-03-06',40.23238);
INSERT INTO neighborhood_predictions_week (neighborhood,end_of_week,predicted_incidents) VALUES ('Allentown','2021-03-06',40.23235), ('Central','2021-03-06',38.23235), ('First Ward','2021-03-06',40.23238);
INSERT INTO zip_predictions_week (zipcode,end_of_week,predicted_incidents) VALUES ('14212','2021-03-06',40.23235), ('14207','2021-03-06',38.23235), ('14222','2021-03-06',40.23238);


/*DELETE CONTENT  OF MONTH PREDICATION TABLES*/
TRUNCATE TABLE police_district_predictions_month;
TRUNCATE TABLE council_district_predictions_month;
TRUNCATE TABLE neighborhood_predictions_month;
TRUNCATE TABLE zip_predictions_month;


/*INSERT INTO TABLE id,name,varchar,double*/
INSERT INTO police_district_predictions_month(police_district,mnth,predicted_incidents) VALUES ('DISTRICT A','Mar-2021',40.23235), ('DISTRICT B','Mar-2021',38.23235), ('DISTRICT C','Mar-2021',40.23238);
INSERT INTO council_district_predictions_month (council_district,mnth,predicted_incidents) VALUES ('UNIVERSITY','2021-03-06',40.23235), ('NIAGARA','Mar-2021',38.23235), ('DELAWARE','Mar-2021',40.23238);
INSERT INTO neighborhood_predictions_month (neighborhood,mnth,predicted_incidents) VALUES ('Allentown','Mar-2021',40.23235), ('Central','Mar-2021',38.23235), ('First Ward','Mar-2021',40.23238);
INSERT INTO zip_predictions_month (zipcode,mnth,predicted_incidents) VALUES ('14212','Mar-2021',40.23235), ('14207','Mar-2021',38.23235), ('14222','Mar-2021',40.23238);



package initiateDB; // Package (main )

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import obj.Tables;
import ref.Config;
import work.DateRoutines;
import work.DB;
import work.HandleFile;

/*ScriptName : InitiateDB.java / initiate.py
 * Authors : Eric Caskey , Melvin Sebastian
 * Version : 0.1
 * Purpose : This script is designed to initiate the database the process
 *           The starting file was updated to backfill null values 
 *           The format is different than our BAU process
 *            Uses work.file to read input files and write logs
 *            		- compiles list of incidents / addresses to add
 *            Uses work.db to look up ids and insert new entities
 *            		- compiles incidents and addresses, automatic fills where needed
 * Input :  datakit/tmp/Crime_Incidents.csv
 * Output : written to caskeycoding.com:3306/caskey5_buffaloCrime 
 * */

public class InitiateDB { // START CLASS DEFINE NAME
	/*CREATE A MAPS AND ARRAYS TO TRACK OUR ENTITIES (OBJECTS)
	 * IDEALLY WE WOULD CREATE CLASSES TO REPRESENT EACH ENTITY - NOT USE ARRAYS
	 * 		- WILL RE-EVALUATE IF THERE IS TIME 
	 * EACH MAP IS A KEY,VALUE PAIR
	 * KEY = # RECORD IN DB (ALSO KEY IN DB)
	 * 		ADDRESS ARRAY VALUE={address1,address2,location,council_district,police_district,neighborhood,census_block, zipcode};
	 * 		INCIDENT ARRAY VALUE={id	incident_id	incident_date	case_number	address_id};
	 * */
	
	public static ConcurrentHashMap<String,ConcurrentHashMap<Integer,String[]>> entityMap =new ConcurrentHashMap<>();//Public Matrix of all entities (DB)
	public static ConcurrentHashMap<Integer,String[]> incidentTable=new ConcurrentHashMap<>();//Public Matrix of incident table(DB)	
	
	public static ConcurrentHashMap<Integer,String[]> addressTable=new ConcurrentHashMap<>();//Public Matrix of address table (DB) 
	public static ConcurrentHashMap<String,String[]> dateTable=new ConcurrentHashMap<>();//Public Matrix of date table (DB)	
	public static ConcurrentHashMap<Integer,String[]> weatherTable=new ConcurrentHashMap<>();//Public Matrix of weather table (DB)

	public static void main(String[] args) {		// MAIN RUNNABLE STARTS HERE .. WE EXPECT SOME WORD AS A ARGUMENT / PARAMETER
		if(args.length > 0) // IF WE HAVE AN ARGUEMENT, LOG IT
			HandleFile.logMessage("Started compile Operation using arg " + args[0]);//LOG MESSAGE WITH ARGUMENT WE STARTED WITH
		boolean status = Config.setConfig("All"); //SET JOB DESC
		if (Config.jobDesc.contains("Crime")) status =compileCrime(); // COMPILE CRIME		
		if (Config.jobDesc.contains("Dates")) status =compileDates(); // COMPILE WEATHER
		if (Config.jobDesc.contains("Weather")) status =compileWeather(); // COMPILE DATES
		if (Config.jobDesc.contains("All")) { // RUN EM ALL
			status = Config.setConfig("Crime"); //SET JOB DESC  	
			status =compileCrime(); // COMPILE CRIME
			status = Config.setConfig("Dates"); //SET JOB DESC  	
			status =compileDates(); // COMPILE DATES
			status = Config.setConfig("Dates"); //SET JOB DESC  	
			status =compileWeather(); // COMPILE WEATHER
		}
		HandleFile.logMessage("Completed compile " + Config.jobDesc+" " + status ); // LOG THAT WIL COMPLETED THE JOB */
	}
	public static boolean compileCrime(){
		DB.connect(); // CONNECT TO DATABASE
		Tables.collectTables(); // COLLECT DB TABLES TO REFERENCE LOCALLY
		 boolean status = collectCrimeEntities(); // COLLECT TABLES FROM FILE
		 // BELOW WE WILL SPLIT OUR SQL STRINGS INTO ARRAYS
		 // THIS WILL LET US EVALUATE IF THERE ARE ACTUALLY ENTITIES
		 // SPLIT ON VALUES WINTHIN THE STATMENT AND LOOK AT EVERYTHING AFTER THAT
		 // IF THERE ARE MORE THAN 5 CHARACTERS IT IS ASSUMED THERE ARE VALUES AND INSERT IS EXECUTED 
		 // EXECUTION IS SKIPPED IF THERE ARE NOT VALUES (...)
		 String sql = generateAddressInsertStatement(); // GENERATE SQL STATEMENT FOR ADDRESSES
		 if(sql.split("VALUES")[1].length()>5)status = DB.insert(sql); // IF STATEMENT AFTER VALUES CONTAINS (..) - EXECUTE SQL STATEMENT FOR INCIDENTS
		 sql = generateIncidentInsertStatement();// GENERATE SQL STATEMENT FOR INCIDENTS
		if(sql.split("VALUES")[1].length()>5)status = DB.insert(sql);// IF STATEMENT AFTER VALUES CONTAINS (..) - EXECUTE SQL STATEMENT FOR INCIDENTS

			DB.close();
		return status; 
	}
	
	public static String generateAddressInsertStatement() { // Class to build VALUES to PASS WITH INSERT STATEMENT
		String line="INSERT into address (address1,address2,location,council_district,police_district,neighborhood,census_block_group,zipcode) VALUES "; // START INSERT SATEMENT, THEN ADD VALUES ITERATIVELY FROM MAP
		HandleFile.logMessage("Adding "+Tables.addressTable.size()+" New rows to addresses " + addressTable.size());
		//IF ADDRESS IS IN ADDRESS TABLE, IT DOES NOT EXIST IN DB
		int j=0;
		for(String r : Tables.addressTable.keySet()) { //ITERATE THROUGH EACH ITEM WITH THE KEY AS i		
			int i =Integer.parseInt(Tables.addressTable.get(r));
			//System.out.println(i);
			//START RETREIVING OUR DATA DATA POINS FROM ARRAY
			//NORAMLLY I WOULD NOT STORE IN STRINGS, AND JUST BUILD LINE WITH SAME LOGIC BELOW 
			//if(addressTable.get(i) == null)continue;
			String[] row = addressTable.get(i);
			String address1 = "";
			if(!row[0].toUpperCase().contains("NULL") && !row[0].toUpperCase().contains("UNKNOWN") ) // IF LOCOATION IN OUR ARRAY ISNT NULL OR UNKOWN
				address1 = row[0];
			String address2=""; // GET KEY FROM MAP AND STORE STORE SECOND ARRAY POSITION AS ADDRESS2
			if(!row[1].toUpperCase().contains("NULL") && !row[1].toUpperCase().contains("UNKNOWN") ) // IF LOCOATION IN OUR ARRAY ISNT NULL OR UNKOWN
				address2 = row[1];
			String location="";
			if(!row[2].toUpperCase().contains("NULL") && !row[2].toUpperCase().contains("UNKNOWN") ) // IF LOCOATION IN OUR ARRAY ISNT NULL OR UNKOWN
				location=row[2]; // GET KEY FROM MAP AND STORE THIRD ARRAY POSITION IS LOCATION			
			String council_district="";
			if(!row[3].toUpperCase().contains("NULL") && !row[3].toUpperCase().contains("UNKNOWN") ) // IF LOCOATION IN OUR ARRAY ISNT NULL OR UNKOWN
				council_district=row[3]; //GET KEY FROM MAP AND STORE FOURTH ARRAY POSITION IS COUNCIL_DISTRICT			  
			String police_district="";
			if(!row[4].toUpperCase().contains("NULL") && !row[4].toUpperCase().contains("UNKNOWN") ) // IF LOCOATION IN OUR ARRAY ISNT NULL OR UNKOWN
				police_district=row[4]; // GET KEY FROM MAP AND STORE FIFTH ARRAY POSITION IS POLICE_DISTRICT
			String neighborhood="";
			if(!row[5].toUpperCase().contains("NULL") && !row[5].toUpperCase().contains("UNKNOWN") ) // IF LOCOATION IN OUR ARRAY ISNT NULL OR UNKOWN
				neighborhood =row[5]; // GET KEY FROM MAP AND STORE SIXTH ARRAY POSITION IS NEIGHBORHOOD
			String census_block="";
			if(!row[6].toUpperCase().contains("NULL") && !row[6].toUpperCase().contains("UNKNOWN") ) // IF LOCOATION IN OUR ARRAY ISNT NULL OR UNKOWN
				census_block=row[6]; // GET KEY FROM MAP AND STORE SEVENTH  ARRAY POSITION IS CENSUS_BLOCK
			String zipcode="";
			if(!row[7].toUpperCase().contains("NULL") && !row[7].toUpperCase().contains("UNKNOWN") ) // IF LOCOATION IN OUR ARRAY ISNT NULL OR UNKOWN
				zipcode=row[7]; // GET KEY FROM MAP AND STORE EIGHTH ARRAY POSITION
			
			if(zipcode.length() > 5) zipcode = zipcode.substring(0, 5); // SOME IPS ARE LONGER
			// WE SHOULD CONVERT TO INT AND STORE IN DATABASE, BUT WILL USE STRING TO MAKE IT EASIER
			//END RETREIVING OUR DATA DATA POINS BELOW
			
			if (j==0)line = line + "('"+address1+"','"+address2+"','"+location+"','"+council_district+"','"+police_district+"','"+neighborhood+"','"+census_block+"','"+zipcode+"')";
			else line = line + ",('"+address1+"','"+address2+"','"+location+"','"+council_district+"','"+police_district+"','"+neighborhood+"','"+census_block+"','"+zipcode+"')";
			j++;
			if(j == 1000) {
				j=0;
				DB.insert(line);
				line="INSERT into address  (address1,address2,location,council_district,police_district,neighborhood,census_block_group,zipcode) VALUES ";
			}

		 }
		if(line.endsWith(","))line.substring(0, line.length() - 1); // DROP LAST CHARACTER IF ITS A COMMA
		HandleFile.logMessage(line);
		return line; // STRING YOU ARE RETURNING  
					// INSERT into address VALUES (...),(...),(...)
	}
	public static String generateIncidentInsertStatement() {
		String line = "INSERT INTO incident (incident_id,incident_date,case_number,address_id,incident_type_id) VALUES ";// START INSERT SATEMENT, THEN ADD VALUES ITERATIVELY FROM MAP
	
		//IF INCIDENT IS IN INCIDENT TABLE, IT DOES NOT EXIST IN DB
		HandleFile.logMessage("Adding "+incidentTable.size()+" New rows to incidents");
		int j=0;
		for(int i : incidentTable.keySet()) { //ITERATE THROUGH EACH ITEM WITH THE KEY AS i
			String[] value = incidentTable.get(i); // INITIATE AN ARRAY 
			String incident_id=value[0]; // FIRST ARRAY POSITION IS INCIDENT_ID
			if(incident_id.length() < 2) incident_id="0";
			String case_number=value[1]; // SECOND ARRAY POSITION IS CASE_NUMBER
			String dateTime =value[2]; // THIRD ARRAY POSITION IS INCIDENT_DATETIME MM/DD/YYYY HH:MM:SS AM
			Date incidentDate =  DateRoutines.stringtoDate(dateTime); // STORE INCIDENT DATE IN DATE VAIRABLE TO CONVERT TO MYSQL DATE FORMAT    			
			String mySQLDateTime = DateRoutines.datetoString(":-",incidentDate);//VALUE[2] NEEDS TO BE PROCESSED TO MYSQL DATE FORMAT   yyyy-MM-dd HH:mm:ss
			String incident_type=value[3]; // FOURTH ARRAY POSITION IS INCIDENT_TYPE_PRIMARY
			//int incident_type_id=db.idWhere("incident_type", "incident_type='"+incident_type+"'");
			if(mySQLDateTime.length()<1)  mySQLDateTime ="1900-01-01 00:00:00";
			int incident_type_id=0;
			if(	Tables.incidentTypeTable.get(incident_type)!=null)incident_type_id=Tables.incidentTypeTable.get(incident_type); // GET INTEGER VALUE MAPPED TO INCIDENT IN TABLES CLASS
			
			String address1 = ""; // INITIATE ADDRESS1 COULD BE NULL
			String address2=""; // INITIATE ADDRESS2 COULD BE NULL
			String location=""; //INITIATE LOCATION COULD BE NULL
			if (value.length > 4 ) address1=value[4]; // FIFTH ARRAY POSITION IS ADDRESS1 {USED TO LOOK UP ADDRESS ID}
			if (value.length > 5 ) address2 =value[5]; // SIXTH ARRAY POSITION IS ADDRESS2 {USED TO LOOK UP ADDRESS ID}
			if (value.length > 6 ) location =value[6]; // SEVENTH ARRAY POSITION IS LOCATION{USED TO LOOK UP ADDRESS ID}
			//int addressint address_id_id = db.idWhere("address", "address1='"+address1+"' AND address2='"+address2+"' AND location_id="+db.idWhere("location", "location='"+location+"'") );
			int address_id = 0;
			if(Tables.addressTable.containsKey(address1+"-"+address2+"-"+location)  )address_id =Integer.parseInt(Tables.addressTable.get(address1+"-"+address2+"-"+location));
			if (j==0)line = line + "("+incident_id+",'"+mySQLDateTime+"','"+case_number+"',"+address_id+","+incident_type_id+")";
			else line = line + ",("+incident_id+",'"+mySQLDateTime+"','"+case_number+"',"+address_id+","+incident_type_id+")";
			j++;
			if(j == 1000) {
				j=0;
				HandleFile.logMessage(line);
				DB.insert(line);
				line="INSERT into incident (incident_id,incident_date,case_number,address_id,incident_type_id) VALUES ";
			}
		}		
		if(line.endsWith(","))line.substring(0, line.length() - 1); // DROP LAST CHARACTER IF ITS A COMMA
		
		HandleFile.logMessage(line);
		return line; // STRING YOU ARE RETURNING  
		// INSERT into address VALUES (...),(...),(...)
	}
	public static boolean compileDates() {
		DB.connect();
		Tables.collectTables();
		 boolean status = collectDateEntities(); // COLLECT TABLES FROM FILE
		 String sql = generateDateInsertStatement(); // GENERATE SQL STATEMENT FOR ADDRESSES
		if(sql.split("VALUES")[1].length()>5)status = DB.insert(sql); // IF STATEMENT AFTER VALUES CONTAINS (..) - EXECUTE SQL STATEMENT FOR INCIDENTS

		DB.close();
		return status; 
	}
	public static String generateDateInsertStatement() {
		String line = "INSERT INTO date (date,name,date_type) VALUES ";// START INSERT SATEMENT, THEN ADD VALUES ITERATIVELY FROM MAP
		
		//IF INCIDENT IS IN INCIDENT TABLE, IT DOES NOT EXIST IN DB
		HandleFile.logMessage("Adding "+dateTable.size()+" New rows to dates");
		int j=0;
		for(String s : dateTable.keySet()) { //ITERATE THROUGH EACH ITEM WITH THE KEY AS i
			String[] value = dateTable.get(s); // INITIATE AN ARRAY 
			String date=value[0]; // FIRST ARRAY POSITION IS INCIDENT_ID
			if(date.length()<1)  date="1900-01-01 00:00:00";
			String name =value[1]; // FIRST ARRAY POSITION IS INCIDENT_ID
			String type =value[2]; // FIRST ARRAY POSITION IS INCIDENT_ID
			int type_id = Tables.dateTypeTable.get(type); // GET DATE TYPE ID
			
			if (j==0)line = line + "('"+date+"','"+name+"',"+type_id+")"; //FIRST VALUE DOESNT NEED COMMA
			else line = line + ",('"+date+"','"+name+"',"+type_id+")";  // AFTER FIRST NEEDS COMMA AT BEGINNING
			j++;
			if(j == 1000) {
				j=0;
				HandleFile.logMessage(line);
				DB.insert(line);
				line="INSERT INTO date (date,name,date_type) VALUES ";
			}
		}		
		if(line.endsWith(","))line.substring(0, line.length() - 1); // DROP LAST CHARACTER IF ITS A COMMA
		
		HandleFile.logMessage(line);
		return line; // STRING YOU ARE RETURNING  
		// INSERT into address VALUES (...),(...),(...)
	}
	public static boolean compileWeather() {
		return true;
	}
	public static boolean collectCrimeEntities() { // THIS WILL BREAKDOWN OUR INPUT CSV
		try(BufferedReader br = new BufferedReader(new FileReader(Config.inputFile.replace(".csv", "_initiate.csv")))) { // OPEN CRIME_INCIDENTS.CSV
			String line=""; // INITIATE A LINE TO STORE FILE LINE CONTENTS
			int i = 0; // INITIATE INT I TO DETERMINE IF HEADERS
			while((line=br.readLine()) != null) {
				String[] row  = line.replace("'","").split(","); // START AN ROW ARRAY, SPLIT ON COMMA
				i++; 
				if (i<2) {continue;} //IF I = 1, THESE ARE HEADERS, SKIP
				// 
				//	RAW CRIME_INCIDENTS ARRAY ORDER/MAP	- THIS WAS UPDATED WITH CALCULATED NEIGHBORHOOD / POLICE / COUNCIL	
				//
				//	  0 incident_id,1 case_number,2 incident_datetime,3 incident_type_primary,4 incident_description,
				//	  5 clearance_type,6 address_1,7 address_2,8 city,9 state,10 zip,11 country,12 latitude,13 longitude,
				//	  14 created_at,15 updated_at,16 location,17 hour_of_day,18 day_of_week,19 parent_incident_type,
				//	  20 Census Block Group 1,21 Neighborhood 1,22 Police District 1, 23 Council District 1
				//
				
				String[] value = {"","","","","","","",""}; // INITIATE AN ARRAY TO STORE ADDRESS
				String address1=""; // INITIATE ADDRESS1
				String address2=""; // INITIATE ADDRESS2
				String location=""; // INITIATE LOCATION
				if (row.length>6)address1=row[6]; // FIRST ARRAY POSITION IS ADDRESS1
				if (row.length>7)address2=row[7]; // SECOND ARRAY POSITION IS ADDRESS2
				if( row.length>16)location=row[16]; // THIRD ARRAY POSITION IS LOCATION
				// THIS IS OUR TABLE KEY IS ADDRESS1, ADDRESS2 AND LOCATION - AN ADDRESS IS CONSIDERED UNIQUE BY THESE FACTORS  
				if(!Tables.addressTable.containsKey(address1+"-"+address2+"-"+location)) {			// IF WE HAVE IT, DON'T ADD IT
					int id = Tables.addressTable.size() + 1; // ID WILL BE SIZE OF TABLE + 1
					if (row.length>6)value[0]=row[6]; // FIRST ARRAY POSITION IS ADDRESS1
					if (row.length>7)value[1]=row[7]; // SECOND ARRAY POSITION IS ADDRESS2
					if( row.length>16)value[2]=row[16]; // THIRD ARRAY POSITION IS LOCATION
					if( row.length>23)value[3]=row[23]; // FOURTH ARRAY POSITION IS COUNCIL_DISTRICT
					if( row.length>22)value[4]=row[22]; // FIFTH ARRAY POSITION IS POLICE_DISTRICT
					if( row.length>21)value[5]=row[21]; // SIXTH ARRAY POSITION IS NEIGHBORHOOD
					if( row.length>20)value[6]=row[20]; // SEVENTH  ARRAY POSITION IS CENSUS_BLOCK_GROUP		
					if( row.length>10)value[7]=row[10]; // EIGHT  ARRAY POSITION IS CENSUS_BLOCK_GROUP	
					InitiateDB.addressTable.put(id, value); // ADD TO A MAP IN THE COMPILE CLASS (key,value)
					String refKey = address1+"-"+address2+"-"+location; // FINALIZE KEY ADDRESS1 - ADDRESS2 - LOCATION
					Tables.addressTable.put(refKey,Integer.toString(id)); // STORE THE KEY AND TABLE ID LOCALLY SO WE DON'T NEED TO CALL DB LATER 
					HandleFile.writeFileName("D:\\add.txt",id + "," +address1+"-"+address2+"-"+location );  // WRITE ADDS TO FILE FOR TRACKING
				}
				//NOTE : incidentTable IS (KEY,VALUE)
				    //THE KEY = SIZE OF ADDRESS TABLE IN SCRIPT (NEEDS TO BE ADDED )+ SIZE OF address DB IN SB + 1
					//THE VALUE = ARRAY ORGANIZING ADDRESS PEICES
				//COLLECT INCIDENT INFO AND SAVE IN MAP TO INSERT LATER
				value = new String[7]; // INITIATE AN ARRAY 
				value[0]=row[0]; // FIRST ARRAY POSITION IS INCIDENT_ID
				value[1]=row[1]; // SECOND ARRAY POSITION IS CASE_NUMBER
				value[2]=row[2]; // THIRD ARRAY POSITION IS INCIDENT_DATETIME MM/DD/YYYY HH:MM:SS AM
				//VALUE[2] NEEDS TO BE PROCESSED TO MYSQL DATE FORMAT
				value[3]=row[3]; // FOURTH ARRAY POSITION IS INCIDENT_TYPE_PRIMARY
				value[4]=row[6].replace("'", ""); // FIFTH ARRAY POSITION IS ADDRESS1 {USED TO LOOK UP ADDRESS ID}
				value[5]=row[7]; // SIXTH ARRAY POSITION IS ADDRESS2 {USED TO LOOK UP ADDRESS ID}
				value[6]=row[16]; // SEVENTH ARRAY POSITION IS LOCATION {USED TO LOOK UP ADDRESS ID}
				InitiateDB.incidentTable.put((InitiateDB.incidentTable.size() +1), value); // ADD TO A MAP IN THE COMPILE CLASS (key, value)
				//NOTE : incidentTable IS (KEY,VALUE)
					//THE KEY = SIZE OF INCIDENTS TABLE IN SCRIPT (NEEDS TO BE ADDED )+ SIZE OF address DB IN SB + 1
					//THE VALUE = ARRAY ORGANIZING INCIDENT PEICES
			}//END FILE
		}catch (Exception e) {		
			e.printStackTrace(); // PRINTS ERROR TO CONSOLE
			HandleFile.logMessage(Config.jobDesc + " Error : "+ e.getMessage() +"  "+ Config.inputFile); // LOGERROR MESSAGE AND FILENAME WE ARE READING
			return false; // RETURN BOOLEAN TO RESUME OPERATION
		} 
		return true; // RETURN BOOLEAN TO RESUME OPERATION
	}
	public static boolean collectDateEntities() { // THIS WILL BREAKDOWN OUR INPUT CSV
		try(BufferedReader br = new BufferedReader(new FileReader(Config.inputFile))) { // OPEN CRIME_INCIDENTS.CSV
			String line=""; // INITIATE A LINE TO STORE FILE LINE CONTENTS
			int i = 0; // INITIATE INT I TO DETERMINE IF HEADERS
			while((line=br.readLine()) != null) { // WHILE THERE IS A LINE
				String[] row  = line.replace("'","").split(","); // START AN ROW ARRAY, SPLIT ON COMMA
				i++; 
				if (i<2) {continue;} //IF I = 1, THESE ARE HEADERS, SKIP
				// 
				//	RAW CRIME_INCIDENTS ARRAY ORDER/MAP		
				//
				//	  0 date,1 name,2 type
				//
				//CHECK TO SEE IF DATE EXISTS IN DB
				String date=row[0]; // FIRST POSITION IN ARRAY WE ARE READING IS DATE
				String name=row[1]; // SECOND POSITION IN ARRAY WE ARE READING IS NAME
				String type=row[2];	// THIRD POSITION IN ARRAY WE ARE READING IS TYPE				
				if(!Tables.dateTable.containsKey(date+"-"+name)) { // ADD IT IF WE DON'T ALREADY HAVE IT
					String[] value = {"","",""};
					value[0] = date;// FIRST POSITION IN ARRAY WE ARE READING IS DATE
					value[1] = name;// SECOND POSITION IN ARRAY WE ARE STORING IS DATE
					value[2] = type;// SECOND POSITION IN ARRAY WE ARE STORING IS DATE
					InitiateDB.dateTable.put(date+"-"+name, value); // ADD TO A MAP IN THE COMPILE CLASS (key,value)
					Tables.dateTable.put(date+"-"+name, value); // ADD TO A MAP IN THE COMPILE CLASS (key,value)
				}

			}//END FILE
		}catch (Exception e) {		//CATCH EXCEPTIONS
			e.printStackTrace(); // WRITE ERROR TO CONSOLE
			HandleFile.logMessage(Config.jobDesc + " Error : "+ e.getMessage() +"  "+ Config.inputFile); // LOGERROR MESSAGE AND FILENAME WE ARE READING
			return false; // RETURN BOOLEAN TO RESUME OPERATION
		} 
		return true; // RETURN BOOLEAN TO RESUME OPERATION
	}
}

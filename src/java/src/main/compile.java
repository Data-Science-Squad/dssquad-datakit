
package main; // Package (main )

import java.util.Date; // IMPORT PACKAGE FOR DATE CONVERSION
import java.util.concurrent.ConcurrentHashMap; // IMPORT PACKAGE FOR CONCURRENTHASHMAP

import ref.Config;
import work.DateRoutines;
import work.DB;
import work.HandleFile;

/*ScriptName : compile.java / compile.py
 * Authors : Eric Caskey , Melvin Sebastian
 * Version : 0.1
 * Purpose : This script is designed to orchestrate the process
 *            Uses work.file to read input files and write logs
 *            		- compiles list of incidents / addresses to add
 *            Uses work.db to look up ids and insert new entities
 *            		- compiles incidents and addresses, automatic fills where needed
 * Input :  datakit/tmp/Crime_Incidents.csv
 * Output : written to caskeycoding.com:3306/caskey5_buffaloCrime 
 * */

public class Compile { // START CLASS DEFINE NAME
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
	public static ConcurrentHashMap<Integer,String[]> dateTable=new ConcurrentHashMap<>();//Public Matrix of date table (DB)	
	public static ConcurrentHashMap<Integer,String[]> weatherTable=new ConcurrentHashMap<>();//Public Matrix of weather table (DB)

	public static boolean initiateDB=false; // PUBLIC BOOLEAN TO KNOW IF WE SHOULD LOOK AT ALL RECORDS OR JUST RECENT
	
	
	public static void main(String[] args) {		// MAIN RUNNABLE STARTS HERE .. WE EXPECT SOME WORD AS A ARGUMENT / PARAMETER
		//args[0]="Crime";
		//file.logMessage("Started compile Operation using arg " + args[0]);//LOG MESSAGE WITH ARGUMENT WE STARTED WITH
		DB.connect();
		boolean status = Config.setConfig("Crime"); //SET JOB DESC  		
		if (Config.jobDesc.contains("Crime")) status =compileCrime(); // COMPILE CRIME		
		if (Config.jobDesc.contains("Dates")) status =compileDates(); // COMPILE WEATHER
		if (Config.jobDesc.contains("Weather")) status =compileWeather(); // COMPILE DATES
		if (Config.jobDesc.contains("All")) { // RUN EM ALL
			status =compileCrime(); // COMPILE CRIME
			status =compileDates(); // COMPILE DATES
			status =compileWeather(); // COMPILE WEATHER
		}
		DB.close();
		HandleFile.logMessage("Completed compile " + Config.jobDesc+" " + status );
	}
	public static boolean compileCrime(){
		//tables.collectTables();
		 boolean status= HandleFile.getURI(); //UPDATE CRIMES FILE 
		 status = HandleFile.collectCrimeEntities(); // COLLECT TABLES FROM FILE
		 // BELOW WE WILL SPLIT OUR SQL STRINGS INTO ARRAYS
		 // THIS WILL LET US EVALUATE IF THERE ARE ACTUALLY ENTITIES
		 // SPLIT ON VALUES WINTHIN THE STATMENT AND LOOK AT EVERYTHING AFTER THAT
		 // IF THERE ARE MORE THAN 5 CHARACTERS IT IS ASSUMED THERE ARE VALUES AND INSERT IS EXECUTED 
		 // EXECUTION IS SKIPPED IF THERE ARE NOT VALUES (...)
		String sql = generateAddressInsertStatement(); // GENERATE SQL STATEMENT FOR ADDRESSES
		if(sql.split("VALUES")[1].length()>5)status = DB.insert(sql); // IF STATEMENT AFTER VALUES CONTAINS (..) - EXECUTE SQL STATEMENT FOR INCIDENTS
		sql = generateIncidentInsertStatement();// GENERATE SQL STATEMENT FOR INCIDENTS
		if(sql.split("VALUES")[1].length()>5)status = DB.insert(sql);// IF STATEMENT AFTER VALUES CONTAINS (..) - EXECUTE SQL STATEMENT FOR INCIDENTS
		return status; 
	}
	
	public static String generateAddressInsertStatement() { // Class to build VALUES to PASS WITH INSERT STATEMENT
		String line="INSERT into address (address1,address2,location,council_district,police_district,neighborhood,census_block_group,zipcode) VALUES "; // START INSERT SATEMENT, THEN ADD VALUES ITERATIVELY FROM MAP
		//System.out.println("New address entries to INSERT : " + addressTable.size() );
		HandleFile.logMessage("Adding "+incidentTable.size()+" New rows to addresses");
		//IF ADDRESS IS IN ADDRESS TABLE, IT DOES NOT EXIST IN DB
		int j=0;
		for(int i : addressTable.keySet()) { //ITERATE THROUGH EACH ITEM WITH THE KEY AS i			
			//START RETREIVING OUR DATA DATA POINS FROM ARRAY
			//NORAMLLY I WOULD NOT STORE IN STRINGS, AND JUST BUILD LINE WITH SAME LOGIC BELOW 
			String address1=addressTable.get(i)[0]; // GET KEY FROM MAP STORE FIRST ARRAY POSITION AS ADDRESS1
			String address2=addressTable.get(i)[1]; // GET KEY FROM MAP AND STORE STORE SECOND ARRAY POSITION AS ADDRESS2
			String location=addressTable.get(i)[2]; // GET KEY FROM MAP AND STORE THIRD ARRAY POSITION IS LOCATION
			String council_district=addressTable.get(i)[3]; //GET KEY FROM MAP AND STORE FOURTH ARRAY POSITION IS COUNCIL_DISTRICT 
			String police_district=addressTable.get(i)[4]; // GET KEY FROM MAP AND STORE FIFTH ARRAY POSITION IS POLICE_DISTRICT			
			String neighborhood =addressTable.get(i)[5]; // GET KEY FROM MAP AND STORE SIXTH ARRAY POSITION IS NEIGHBORHOOD			
			String census_block_group=addressTable.get(i)[6]; // GET KEY FROM MAP AND STORE SEVENTH  ARRAY POSITION IS CENSUS_BLOCK
			String zipcode=addressTable.get(i)[7]; // GET KEY FROM MAP AND STORE eighth array position is zipcode
			//END RETREIVING OUR DATA DATA POINTS BELOW			
			//BUILD SQL 
			//IF FIRST LINE, BUILD VALUE WITHOUT FIRST COMMA
			if (j==0)line = line + "("+i+",'"+address1+"','"+address2+"',"+location+","+council_district+","+police_district+","+neighborhood+","+census_block_group+","+zipcode+")";
			//ALL VALUES AFTER FIRST SHOULD HAVE A LEADING COMMA
			else line = line + ",("+i+",'"+address1+"','"+address2+"',"+location+","+council_district+","+police_district+","+neighborhood+","+census_block_group+","+zipcode+")";
			j++;

		 }
		if(line.endsWith(","))line.substring(0, line.length() - 1); // DROP LAST CHARACTER IF ITS A COMMA
		System.out.println(line);
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
			int incident_type_id=DB.idWhere("incident_type", "incident_type='"+incident_type+"'");
			String address1 = ""; // INITIATE ADDRESS1 COULD BE NULL
			String address2=""; // INITIATE ADDRESS2 COULD BE NULL
			String location=""; //INITIATE LOCATION COULD BE NULL
			if (value.length > 4 ) address1=value[4]; // FIFTH ARRAY POSITION IS ADDRESS1 {USED TO LOOK UP ADDRESS ID}
			if (value.length > 5 ) address2 =value[5]; // SIXTH ARRAY POSITION IS ADDRESS2 {USED TO LOOK UP ADDRESS ID}
			if (value.length > 6 ) location =value[6]; // SEVENTH ARRAY POSITION IS LOCATION{USED TO LOOK UP ADDRESS ID}
			int address_id = DB.idWhere("address", "address1='"+address1+"' AND address2='"+address2+"' AND location='"+location+"'");

			if (j==0)line = line + "('"+incident_id+"','"+mySQLDateTime+"','"+case_number+"',"+address_id+","+incident_type_id+")";
			else line = line + ",('"+incident_id+"','"+mySQLDateTime+"','"+case_number+"',"+address_id+","+incident_type_id+")";
			j++;
		}		
		if(line.endsWith(","))line.substring(0, line.length() - 1); // DROP LAST CHARACTER IF ITS A COMMA
		System.out.println(line);
		return line; // STRING YOU ARE RETURNING  
		// INSERT into address VALUES (...),(...),(...)
	}
	public static boolean compileDates() {
		return true;
	}
	public static boolean compileWeather() {
		return true;
	}
}

package retired_OLD; // PACKAGE / FOLDER 

import java.io.BufferedReader; // IMPORT JAVA BUFFERED READER TO READ
import java.io.FileReader;// IMPORT JAVA FILEREADER TO READ

import obj.Tables; // IMPORT DB TABLES TO REFERENCE
import ref.Config;// IMPORT CONFIGS TO REFERENCE
import work.HandleFile; // IMPORT OTHER FILE FOR BASIC READ WRITE

/*ScriptName : file.java / file.py
 * Author : Eric Caskey , Melvin Sebastian
 * Version : 0.1
 * Purpose : This script is designed for file Input/Output handling
 * 			 It will sort data from input files, write to output/log files
 *            
 * 
 * Input :  datakit/tmp/Crime_Incidents.csv
 * Output : written to caskeycoding.com:3306/caskey5_buffaloCrime 
 * */
public class fileINIT2 { // DEFINE CLASS NAME
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

package work; // PACKAGE / FOLDER 

import java.io.BufferedInputStream; // IMPORT JAVA FILEOUTPUTSTREAM TO STREAM BYTES IN
import java.io.BufferedReader; // IMPORT JAVA BUFFERED READER TO READ
import java.io.BufferedWriter; // IMPORT JAVA BUFFERED WRITER TO WRITE
import java.io.File; // IMPORT JAVA FILE TO WORK WITH FILES
import java.io.FileOutputStream; // IMPORT JAVA FILEOUTPUTSTREAM TO STREAM BYTES OUT
import java.io.FileReader;// IMPORT JAVA FILEREADER TO READ
import java.io.FileWriter;// IMPORT JAVA FILEWRITER TO WRITE 
import java.net.URL; // IMPORT NET URL GET CSV USING URI 
import java.util.concurrent.ConcurrentHashMap; // IMPORT CONCURRENTHASHMAP TO WORK WITH 

import main.compile; // IMPORT MAIN COMPILE CLASS
import ref.config; // IMPORT CONFIGS CLASS 

/*ScriptName : file.java / file.py
 * Author : Eric Caskey , Melvin Sebastian
 * Version : 0.1
 * Purpose : This script is designed for file Input/Output handling
 * 			 It will sort data from input files, write to output/log files
 * 
 * Input :  datakit/tmp/Crime_Incidents.csv
 * Output : written to caskeycoding.com:3306/caskey5_buffaloCrime 
 * */
public class file { // DEFINE CLASS NAME
	public static void main(String[] args) {config.setConfig("Crime");getURI();} // MAIN RUNNABLE TO TEST URI 
	public static String getSQL(String fileName) { // GETSQL QUERY FROM CONFIG FILE
		String sql=""; //INITIATE SQL STRING
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))){ // OPEN FILE READER TO
			String line=""; //INITIATE LINE WE ARE READING
			while((line =br.readLine()) != null) { // STORE LINE IN OUR VARIABLE
				sql = sql + " " +line;	//ADD EACH SQL LINE ONTO THE LINE WE'RE STORING			
			}
			return sql; // RETURN LINE - THIS WILL BE COMPLETE FILE CONTENTS
		}catch(Exception e) { //CATCH IO ERRORS
			e.printStackTrace();// PRINT ERROR ON SYSTEM CONSOLE
			logMessage("Error : "+ e.getMessage() +"  "+ fileName); // LOGERROR MESSAGE AND FILENAME WE ARE READING
			return sql;// RETURN WHAT WE HAVE	
		}
	}
	public static void writeFileName(String fileName, String line) { //PRINTS NEW LINE TO OUTPUT PATH
		File file = new File(fileName); // INITIATE INSTANCE OF FILENAME PROVIDED
		boolean firstLine = false; // INIATE FIRST LINE BOOLEAN 
		if(!file.exists()) firstLine=true; // IF FILE DOES NOT EXIST, ITS THE FIRST LINE 
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))){//OPEN WRITER TO FILE PATH
		    if(firstLine) bw.write( line); //IF FIRST LINE, WE DO NOT NEED NEW LINE JUST WRITE STRING
		    else  bw.write("\r\n"  +  line);//IF NOT FIRST LINE CREATE NEW LINE AND WRITE STRING
		} catch (Exception e) {// CATCH IO EXCEPTIONS
			e.printStackTrace(); // PRINT ERROR ON SYSTEM CONSOLE
			logMessage("Error : "+ e.getMessage() +"  "+ fileName); // LOGERROR MESSAGE AND FILENAME WE ARE WRITING
		}
	}	
    public static void logMessage(String line) { // USED TO WRITE TO LOG FILES
    	System.out.println(line); // ALL LOGS SHOULD BE WRITTEN TO CONSOLE
		File file = new File(config.logFile); // INITIATE LOG FILE
		boolean firstLine = false; // INITIATE FIRST LINE BOOLEAN
		if(!file.exists()) firstLine=true; // IF FILE DOES NOT EXIST ITS THE FIRST LINE
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {// OPEN WRITER TO LOG FILE PATH 
		    if(firstLine) bw.write(  date.timestamp() + ":: "+  line); //FIRST LINE, WRITE TIME STAMP A SEPARATOR (::) AND LOG MESSAGE
		    else  bw.write("\r\n" + date.timestamp() + ":: "+  line);	//NEW LINE, WRITE TIME STAMP A SEPARATOR (::) AND LOG MESSAGE
		} catch(Exception e) {//CATCH IO EXCEPTIONS
			e.printStackTrace(); // PRINT ERROR ON SYSTEM CONSOLE
			//DON'T LOG THIS MESSAGE - THAT BECOMES AN ENDLESS LOOP
		}
	}
    public static boolean getURI() { // THIS URI WILL GET CSV FROM API AND STORE IN LOCAL FILE
    	try(BufferedInputStream in = new BufferedInputStream(new URL(config.inputURL).openStream());// OPEN BYTE STREAM AT URL
    			FileOutputStream fileOutputStream = new FileOutputStream(config.inputFile)) {// OPEN FILE TO WRITE TO
    			byte dataBuffer[] = new byte[1024]; // INITIATE BYTE ARRAY
    			int bytesRead; // INIATE BYTE INT
    			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) // WHILE THERE IS DATA
    		       fileOutputStream.write(dataBuffer, 0, bytesRead); // WRITE TO OUTPUT FILE    			
             return true; // RETURN SUCCESSFUL IF WE GET THROUGH WHOLE FILE
    	}catch (Exception e) {// CATCH IO EXCEPTIONS
			e.printStackTrace(); // PRINT ERROR ON SYSTEM CONSOLE
			logMessage("Error : "+ e.getMessage() +"  "+ config.inputURL); // LOGERROR MESSAGE AND FILENAME WE ARE WRITING
			return false ;// RETURN FALSE BOOLEAN
		}
    }
	public static boolean collectCrimeEntities() { // THIS WILL BREAKDOWN OUR INPUT CSV
		try(BufferedReader br = new BufferedReader(new FileReader(config.inputFile))) { // OPEN CRIME_INCIDENTS.CSV
			String line=""; // INITIATE A LINE TO STORE FILE LINE CONTENTS
			file.logMessage("Reading " + config.inputFile); // LOT LOCATION OF INPUT FILE
			while((line=br.readLine()) != null) { // WHILE LINE DOES NOT EQUAL NULL
				String[] row  = line.replace("\"","").split(","); // START AN ROW ARRAY, SPLIT ON COMMA				
				if (line.contains("updated_at")) {continue;} //THESE ARE HEADERS, SKIP
				// 
				//	RAW CRIME_INCIDENTS ARRAY ORDER/MAP		
				//
				//	  0 incident_id,1 case_number,2 incident_datetime,3 incident_type_primary,4 incident_description,
				//	  5 clearance_type,6 address_1,7 address_2,8 city,9 state,10 zip,11 country,12 latitude,13 longitude,
				//	  14 created_at,15 updated_at,16 location,17 hour_of_day,18 day_of_week,19 parent_incident_type,
				//	  20 Census Tract 1,21 Census Block 1,22 Census Block Group 1,23 Neighborhood 1,24 Police District 1,
				//	  25 Council District 1
				//
				//System.out.println(line);
				//CHECK TO SEE IF ADDRESS EXISTS IN DB
				
				if(row.length > 6 && !db.exists("address", "address1='"+row[6]+"' AND address2='"+row[7]+"'" )) {
					//String[] value = new String[8]; // INITIATE AN ARRAY TO STORE ADDRESS
					//COLLECT ADDRESS INFO AND SAVE IN MAP TO INSERT LATER
					String[] value = {"","","","","","","",""};
					if (row.length>6)value[0]=row[6]; // FIRST ARRAY POSITION IS ADDRESS1
					if (row.length>7)value[1]=row[7]; // SECOND ARRAY POSITION IS ADDRESS2
					if( row.length>16)value[2]=row[16]; // THIRD ARRAY POSITION IS LOCATION
					if( row.length>25)value[3]=row[25]; // FOURTH ARRAY POSITION IS COUNCIL_DISTRICT
					if( row.length>24)value[4]=row[24]; // FIFTH ARRAY POSITION IS POLICE_DISTRICT
					if( row.length>23)value[5]=row[23]; // SIXTH ARRAY POSITION IS NEIGHBORHOOD
					if( row.length>21)value[6]=row[22]; // SEVENTH  ARRAY POSITION IS CENSUS_BLOCK !!//GROUP
					if( row.length>10)value[7]=row[10]; // EIGHTH ARRAY POSITION IS ZIPCODE
					compile.addressTable.put((compile.addressTable.size() + db.tableSize("address")+1), value); // ADD TO A MAP IN THE COMPILE CLASS (key,value) 
					if (compile.addressTable.size() == 5) {
						String sql =compile.generateAddressInsertStatement();
						db.insert(sql);
						compile.addressTable = new ConcurrentHashMap<>();
					}
					//NOTE : incidentTable IS (KEY,VALUE)
					//THE KEY = SIZE OF ADDRESS TABLE IN SCRIPT (NEEDS TO BE ADDED )+ SIZE OF address DB IN SB + 1
					//THE VALUE = ARRAY ORGANIZING ADDRESS PEICES
				}
				//SEE IF INCIDENT EXISTS IN DB
				if(row.length > 6 && !db.exists("incident", "case_number='"+row[1]+"' AND incident_id='"+row[0]+"';" )) {
					//COLLECT INCIDENT INFO AND SAVE IN MAP TO INSERT LATER
					String[] value = new String[7]; // INITIATE AN ARRAY 
					value[0]=row[0]; // FIRST ARRAY POSITION IS INCIDENT_ID
					//System.out.println(row[0]);
					//System.out.println(line);
					value[1]=row[1]; // SECOND ARRAY POSITION IS CASE_NUMBER
					value[2]=row[2]; // THIRD ARRAY POSITION IS INCIDENT_DATETIME MM/DD/YYYY HH:MM:SS AM
					//VALUE[2] NEEDS TO BE PROCESSED TO MYSQL DATE FORMAT
					value[3]=row[3]; // FOURTH ARRAY POSITION IS INCIDENT_TYPE_PRIMARY
					value[4]=row[6].replace("'", ""); // FIFTH ARRAY POSITION IS ADDRESS1 {USED TO LOOK UP ADDRESS ID}
					value[5]=row[7]; // SIXTH ARRAY POSITION IS ADDRESS2 {USED TO LOOK UP ADDRESS ID}
					value[6]=row[16]; // SIXTH ARRAY POSITION IS LOCATION {USED TO LOOK UP ADDRESS ID}
					compile.incidentTable.put((compile.incidentTable.size() + db.tableSize("incident")+1), value); // ADD TO A MAP IN THE COMPILE CLASS (key, value)			
					//NOTE : compile.incidentTable IS (KEY,VALUE)
					//THE KEY = SIZE OF INCIDENTS TABLE IN SCRIPT (NEEDS TO BE ADDED )+ SIZE OF address DB IN SB + 1
					//THE VALUE = ARRAY ORGANIZING INCIDENT PEICES
				}
			}//END FILE
		}catch (Exception e) {		
			e.printStackTrace();
			logMessage(config.jobDesc + " Error : "+ e.getMessage() +"  "+ config.inputFile); // LOGERROR MESSAGE AND FILENAME WE ARE READING
			return false; // RETURN BOOLEAN TO RESUME OPERATION
		} 
		return true; // RETURN BOOLEAN TO RESUME OPERATION
	}
}

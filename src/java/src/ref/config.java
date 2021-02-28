/*ScriptName : compile.java / compile.py
 * Authors : Eric Caskey , Melvin Sebastian
 * Version : 0.1
 * Purpose : This script is a configuration file designed to 
 * 			store and define common variables a single runtime 
 * 			will have the file script will reference this for 
 * 			log, input and output information. Not run in our
 * 			operations but 
 * */

package ref; // PACKAGE (FOLDER NAME)

import java.util.Date;

import work.DateRoutines; //IMPORT DATE CRUNCHING ROUTINES (NOT SURE HOW PYTHON IS WITH DATES)
import work.HandleFile; // IMPORT FILE ROUTINES

public class Config {
	public static void main(String[] args) { // THIS IS A MAIN METHOD TO RUN THE FILE - I USE IT TO TEST PATH
		setConfig("Crime"); // TEST CRIME PARAM
		HandleFile.logMessage("Test"); // LOG A TEST MESSAGE TO CONFIRM OUR LOCATION
	} // END MAIN METHOD
	//START DEFINED PUBLIC CONFIG VARIABLES
	public static String jobDesc="";	//WHAT WE CALL THE JOB WE ARE RUNNING
	public static String inputFile=""; // WHERE WE LOOK REPORT CSV INPUT 
	public static String inputURL=""; // URI SYNTAX FROM API 
	public static String outputFile="";  // WHERE WE WRITE REPORT CSV OUTPUT
	public static String logFile=""; // WHERE WE WRITE LOG OUTPUT
	public static Date today=new Date(); // WHAT DAY TODAY IS
	public static Date pastDate=null; // HOW FAR BACK WE'RE INTERESTED IN
	//END DEFINED PUBLIC  CONFIG VARIABLES
	
	public static boolean setConfig(String job) { // PUBLIC METHOD CALLED BY MAIN CLASS DEFINING JOB DESC
		jobDesc=job; // STORE PARAMETER PASSED AS jobDesc
		today = new Date() ; // STORE DAY AS TODAY
		pastDate = DateRoutines.minusDays(today, 7);
			//IN PYTHON I'd LIKE TO DEFINE AS ONE PATH BACKWARDS TO THE DIRECTORY STRUCTURE WE HAVE DEFINED 
		 		// (THIS IS COMPLICATED IN JAVA - DONT WANT TO CONFUSE)
			//	UNTIL THEN WE CAN USE FULL PATH TO FILE
		if(job.contains("Crime")) { // IF CRIME IS PASSED, DEFINE LOG AND INPUT FILES
			String yyyymmdd=DateRoutines.datetoString("-",pastDate); // FORMATTING PAST DATE TO YYYY-MM-DD
			System.out.println(yyyymmdd);
			inputURL="https://data.buffalony.gov/resource/d6g9-xbgu.csv?$where=created_at%20%3E%20%27"+yyyymmdd+"T00:00:00%27"; // URI FOR FILE
			//inputFile="H:\\RASkit\\tmp\\Crime_Incidents.csv"; // INPUT CSV
			inputFile="D:\\BuffaloCrime\\gitRepo\\ds-squad-project\\datakit\\tmp\\Crime_Incidents.csv"; // INPUT CSV
			logFile="D:\\BuffaloCrime\\gitRepo\\ds-squad-project\\datakit\\logs\\CompileCrime_"+DateRoutines.timestamp()+".txt"; // LOG FILE
		}
		else if(job.contains("Date")) { // IF DATE IS PASSED, DEFINE LOG AND INPUT FILES	
			inputFile="D:\\BuffaloCrime\\gitRepo\\ds-squad-project\\datakit\\tmp\\Date.csv"; // INPUT CSV
			logFile="D:\\BuffaloCrime\\gitRepo\\ds-squad-project\\datakit\\logs\\CompileDate_"+DateRoutines.timestamp()+".txt"; // LOG FILE		

		}
		return true; // RETURN TRUE TO RESUME SCRIPT OPERATION
	}
}

package obj;

import java.util.concurrent.ConcurrentHashMap;

import work.DB;
import work.HandleFile;

public class Tables {
	/*CREATE A MAPS AND ARRAYS TO TRACK OUR ENTITIES (OBJECTS)
	 * IDEALLY WE WOULD CREATE CLASSES TO REPRESENT EACH ENTITY - NOT USE ARRAYS
	 * 		- WILL RE-EVALUATE IF THERE IS TIME 
	 * EACH MAP IS A KEY,VALUE PAIR
	 * KEY = # RECORD IN DB (ALSO KEY IN DB)
	 * 		ADDRESS ARRAY VALUE={address1,address2,location,council_district,police_district,neighborhood,census_block, zipcode};
	 * 		INCIDENT ARRAY VALUE={address1,address2,location,council_district,police_district,neighborhood,census_block, zipcode};
	 * */
	public static ConcurrentHashMap<String,ConcurrentHashMap<Integer,String[]>> entityMap =new ConcurrentHashMap<>();//Public Matrix of all entities (DB)
	public static ConcurrentHashMap<Integer,String[]> incidentTable=new ConcurrentHashMap<>();//Public Matrix of incident table(DB)	
	
	public static ConcurrentHashMap<String,String> addressTable=new ConcurrentHashMap<>();//Public Matrix of address table (DB) 
	public static ConcurrentHashMap<String,String[]> dateTable=new ConcurrentHashMap<>();//Public Matrix of date table (DB)	
	public static ConcurrentHashMap<Integer,String[]> weatherTable=new ConcurrentHashMap<>();//Public Matrix of weather table (DB)

	//EXPORT TABLES TO CHECK IDS AND IF VALUES EXIST
	public static ConcurrentHashMap<String, Integer> incidentParentTypeTable=new ConcurrentHashMap<>();//Public Matrix of incident parent type table (DB)
	public static ConcurrentHashMap<Integer,String[]> weatherTypeTable=new ConcurrentHashMap<>();//Public Matrix of weather type table (DB)
	public static ConcurrentHashMap<String,Integer> incidentTypeTable=new ConcurrentHashMap<>();//Public Matrix of incident type table (DB)
	public static ConcurrentHashMap<String ,Integer> dateTypeTable=new ConcurrentHashMap<>();//Public Matrix of date type table (DB)
	public static boolean collectTables() {

		ConcurrentHashMap<Integer, String> results=new ConcurrentHashMap<>(); //INITIATE A TEMP RESULTS MAP TO GET SQL QUERY 
		results = DB.selectStatement("SELECT * FROM incident_type_parent");
		for(int i : results.keySet()) {
			if(results.get(i).split(",").length>1 ) { // IF WE HAVE 1 OR MORE COMMA AND 
				String key = results.get(i).split(",")[1]; // GET SECOND POSITION IN COMMA SEPARATED ARRY (STRING VALUE)
				incidentParentTypeTable.put(key,Integer.parseInt(results.get(i).split(",")[0]));
			}
			else incidentParentTypeTable.put("",0); // RESERVE 0 FOR NULL  
		}
		results=new ConcurrentHashMap<>();
		results = DB.selectStatement("SELECT * FROM incident_type");
		for(int i : results.keySet()) {
			if(results.get(i).split(",").length>1 ) { // IF WE HAVE 1 OR MORE COMMA AND 
				String key = results.get(i).split(",")[1]; // GET SECOND POSITION IN COMMA SEPARATED ARRY (STRING VALUE)
				incidentTypeTable.put(key,Integer.parseInt(results.get(i).split(",")[0]));
			}
			else incidentParentTypeTable.put("",0); // RESERVE 0 FOR NULL 
		}
		results=new ConcurrentHashMap<>();
		results = DB.selectStatement("SELECT * FROM date_type");
		for(int i : results.keySet()) {
			if(results.get(i).split(",").length>1 ) { // IF WE HAVE 1 OR MORE COMMA AND 
				String key = results.get(i).split(",")[1]; // GET SECOND POSITION IN COMMA SEPARATED ARRY (STRING VALUE)
				dateTypeTable.put(key,Integer.parseInt(results.get(i).split(",")[0]));
			}
			else incidentParentTypeTable.put("",0); // RESERVE 0 FOR NULL 
		}
		results = DB.selectStatement("SELECT * FROM incident");
		for(int i : results.keySet()) {
			String[] row = results.get(i).split(",");
			incidentTable.put(Integer.parseInt(results.get(i).split(",")[0]),row);
		}
		
		

		 HandleFile.logMessage(" Imported  Tabels  " ); // LOG SIZE OF TABLES WE COLLECTED
		 HandleFile.logMessage("address : " + addressTable.size());// LOG ADDRESS TABLE SIZE
		 HandleFile.logMessage("incident : " + addressTable.size());// LOG ADDRESS TABLE SIZE
		 HandleFile.logMessage("incident_type : " + incidentTypeTable.size());// LOG ADDRESS TABLE SIZE
		 HandleFile.logMessage("incident_type_parent : " + incidentParentTypeTable.size());// LOG ADDRESS TABLE SIZE
		 HandleFile.logMessage("date : " + dateTable.size());// LOG ADDRESS TABLE SIZE
		return true;
	}
	public static void main(String[] args) { // RUNNABLE TO TEST collectTables METHOD
		 collectTables() ; // RUN METHOD TO COLLECT TABLES - THIS WILL ONLY RUN CODE IN THIS FILE
	}
}

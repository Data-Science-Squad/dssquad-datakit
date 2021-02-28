package retired_OLD;

import java.util.concurrent.ConcurrentHashMap;

import work.DB;

public class tables {
	/*CREATE A MAPS AND ARRAYS TO TRACK OUR ENTITIES (OBJECTS)
	 * IDEALLY WE WOULD CREATE CLASSES TO REPRESENT EACH ENTITY - NOT USE ARRAYS
	 * 		- WILL RE-EVALUATE IF THERE IS TIME 
	 * EACH MAP IS A KEY,VALUE PAIR
	 * KEY = # RECORD IN DB (ALSO KEY IN DB)
	 * 		ADDRESS ARRAY VALUE={address1,address2,location,council_district,police_district,neighborhood,census_block, zipcode};
	 * 		INCIDENT ARRAY VALUE={address1,address2,location,council_district,police_district,neighborhood,census_block, zipcode};
	 * 		INCIDENT ARRAY VALUE={address1,address2,location,council_district,police_district,neighborhood,census_block, zipcode};
	 * */
	public static ConcurrentHashMap<String,ConcurrentHashMap<Integer,String[]>> entityMap =new ConcurrentHashMap<>();//Public Matrix of all entities (DB)
	public static ConcurrentHashMap<Integer,String[]> incidentTable=new ConcurrentHashMap<>();//Public Matrix of incident table(DB)	
	
	public static ConcurrentHashMap<String,String> addressTable1=new ConcurrentHashMap<>();//Public Matrix of address table (DB) 
	public static ConcurrentHashMap<Integer,String[]> dateTable=new ConcurrentHashMap<>();//Public Matrix of date table (DB)	
	public static ConcurrentHashMap<Integer,String[]> weatherTable=new ConcurrentHashMap<>();//Public Matrix of weather table (DB)

	//EXPORT TABLES TO CHECK IDS AND IF VALUES EXIST
	public static ConcurrentHashMap<String, Integer> neighborhoods= new ConcurrentHashMap<>(); //Track unique neighborhoods
	public static ConcurrentHashMap<String, Integer> locations=new ConcurrentHashMap<>(); //Track unique locations
	public static ConcurrentHashMap<String, String> zipcodes=new ConcurrentHashMap<>(); //Track unique locations
	public static ConcurrentHashMap<String, Integer>council_district=new ConcurrentHashMap<>();//Track unique council_district
	public static ConcurrentHashMap<String, Integer>police_district=new ConcurrentHashMap<>(); //Track unique police_district
	public static ConcurrentHashMap<String, Integer> census_block=new ConcurrentHashMap<>();//Track unique neighborhoods
	public static ConcurrentHashMap<String, Integer> incidentParentTypeTable=new ConcurrentHashMap<>();//Public Matrix of incident parent type table (DB)
	public static ConcurrentHashMap<Integer,String[]> weatherTypeTable=new ConcurrentHashMap<>();//Public Matrix of weather type table (DB)
	public static ConcurrentHashMap<String,String> incidentTypeTable=new ConcurrentHashMap<>();//Public Matrix of incident type table (DB)
	public static ConcurrentHashMap<Integer,String[]> dateTypeTable=new ConcurrentHashMap<>();//Public Matrix of date type table (DB)
	public static boolean collectTables() {
		DB.connect();
		ConcurrentHashMap<Integer, String > results = DB.selectStatement("SELECT * FROM neighborhood");
		for(int i : results.keySet()) {
			if(results.get(i).split(",").length>1) {
				neighborhoods.put(results.get(i).split(",")[1],Integer.parseInt(results.get(i).split(",")[0]));
			}
			else neighborhoods.put("",0);
		}
		
		results=new ConcurrentHashMap<>();
		results = DB.selectStatement("SELECT * FROM location");
		for(int i : results.keySet()) {			
			if(results.get(i).split(",").length>1)locations.put(results.get(i).split(",")[1],Integer.parseInt(results.get(i).split(",")[0]));
			else locations.put("",0);
		}		
		results=new ConcurrentHashMap<>();
		results = DB.selectStatement("SELECT * FROM zipcode");
		for(int i : results.keySet()) {
			if(results.get(i).split(",").length>1)zipcodes.put(results.get(i).split(",")[1],results.get(i).split(",")[0]);
			else zipcodes.put("0","0");
		}

		results=new ConcurrentHashMap<>();
		results = DB.selectStatement("SELECT * FROM council_district");
		for(int i : results.keySet()) {
			if(results.get(i).split(",").length>1)council_district.put(results.get(i).split(",")[1],Integer.parseInt(results.get(i).split(",")[0]));
			else council_district.put("",0);
		}

		results=new ConcurrentHashMap<>();
		results = DB.selectStatement("SELECT * FROM police_district");
		for(int i : results.keySet()) {
			if(results.get(i).split(",").length>1)police_district.put(results.get(i).split(",")[1],Integer.parseInt(results.get(i).split(",")[0]));
			else police_district.put("",0);
		}
		results=new ConcurrentHashMap<>();
		results = DB.selectStatement("SELECT * FROM incident_type_parent");
		for(int i : results.keySet()) {
			if(results.get(i).split(",").length>1)incidentParentTypeTable.put(results.get(i).split(",")[1],Integer.parseInt(results.get(i).split(",")[0]));
			else incidentParentTypeTable.put("",0);
		}
		results=new ConcurrentHashMap<>();
		results = DB.selectStatement("SELECT * FROM incident_type");
		for(int i : results.keySet()) {
			System.out.println(results.get(i));
			if(results.get(i).split(",").length>1)incidentTypeTable.put(results.get(i).split(",")[1],results.get(i).split(",")[0]);
			else incidentTypeTable.put("","0");
		}
		results=new ConcurrentHashMap<>();
		results = DB.selectStatement("SELECT * FROM census_block_group");
		for(int i : results.keySet()) {
			if(results.get(i).split(",").length>1)census_block.put(results.get(i).split(",")[1],Integer.parseInt(results.get(i).split(",")[0]));
			else census_block.put("",0);
		}	

		 fileINIT.logMessage("location : " + locations.size());
		 fileINIT.logMessage("location : " + locations.size());
		 fileINIT.logMessage("police_district : " + police_district.size());
		 fileINIT.logMessage("council_district : " + council_district.size());
		 fileINIT.logMessage("neighborhood : " + neighborhoods.size());
		 fileINIT.logMessage("census_block : " + census_block.size());
		 fileINIT.logMessage("incident_type_parent : " + incidentParentTypeTable.size());
		//db.close();
		return true;
	}
	public static void main(String[] args) {
		 collectTables() ;
	}
}

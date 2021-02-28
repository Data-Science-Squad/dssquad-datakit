package work;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

public class DB {
	public static Connection con;
	
	public static Connection connect() {
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			con=DriverManager.getConnection(  
			"jdbc:mysql://192.249.124.190:3306/caskey5_buffaloCrime_test","caskey5_ericCaskey","dsSquad12");
			return con;
		}
		catch(Exception e) {return null;}
	}
	public static boolean close() {
		try {
			con.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HandleFile.logMessage(e.getMessage());
			return false;
		}
	}
	public static boolean insert(String table, String values) {
		try{  
			//Connection con = connect();
			Statement stmt=con.createStatement();  
			System.out.println("INSERT INTO "+table+" VALUES "+values);  
			stmt.executeUpdate("INSERT INTO "+table+" VALUES "+values);  
		   // con.close();  
			return true;
		}
		catch(Exception e) {e.printStackTrace();
		HandleFile.logMessage(e.getMessage());return false;}
	}
	public static boolean insert(String sqlInsert) {
		try{  
			//Connection con = connect();
			Statement stmt=con.createStatement();  
			stmt.executeUpdate(sqlInsert);  
			//con.close();  
			return true;
		}
		catch(Exception e) {e.printStackTrace();HandleFile.logMessage(e.getMessage());return false;}
	}
	public static int tableSize(String table) {
		int size=0;
		try{  
			//Connection con = connect();
			Statement stmt=con.createStatement();  
		
			ResultSet rs=stmt.executeQuery("SELECT COUNT(*) FROM "+table);  
			while(rs.next())  size=rs.getInt(1);
			//con.close();  
			return size;
		}
		catch(Exception e) {e.printStackTrace();HandleFile.logMessage(e.getMessage());return size;}
	}
	public static boolean exists( String table, String where) {
		boolean exists=false;
		int size =0;
		if (where.length()<1)return exists;
		try{  
			//Connection con = connect();
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("SELECT COUNT(*) FROM "+table + " WHERE "+where);  
			while(rs.next())  {
				size=rs.getInt(1);
			}
			rs.close();
			//con.close();
			if(size > 0) exists =true;
			return exists;
		}
		catch(Exception e) {e.printStackTrace();HandleFile.logMessage(e.getMessage());return exists;}
	}
	public static int idWhere(String table, String where) {
		int id =0;
		try{  
			//Connection con = connect();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT id FROM "+table + " WHERE "+where);  
			while(rs.next())  {
				id=rs.getInt(1);
				//System.out.println(size);
			}		
			rs.close();
			if( id==0 &&where.split("=")[0].replace("'","").length()>0) {
				rs=stmt.executeQuery("SELECT * FROM "+ table + " LIMIT 1"); //GET ONE ROW FROM TABLE TO EVALUATE COLUMNS
			    ResultSetMetaData rsmd = rs.getMetaData();//STORE THE RESULTSETMETADATA
			    if(rsmd.getColumnCount()==2) { // GET RESULTSET COLUMN COUNT
			    	//IF 2, IT IS A SMALL, KEY VALUE TABLE AND WE CAN UPDATE			
			    	int tableKey=tableSize(table)+1;//KEY OR ID IN DB IS TABLE SIZE + 1
			    	String value = where.split("=")[1].replace("'",""); // VALUE ALWAYS AFTER EQUALS IN WHERE | DELETE '  AROUND VALUE
			    	if(value.length() > 0) {
				    	insert(table,"("+tableKey+",'"+value+"')"); // INSERT TABLE
				    	HandleFile.logMessage("New Value='"+value+"' added to Table='"+table);
			    	}
			    }
			    
			}
			//con.close();
			//System.out.println(" id "  + id);
			return id;
		}
		catch(Exception e) {e.printStackTrace();HandleFile.logMessage(e.getMessage());return id;}
	}
	public static ConcurrentHashMap<Integer, String >selectStatement(String select) {
		ConcurrentHashMap<Integer, String > results = new ConcurrentHashMap<>(); // Initiate our map of table lines
		try{  
			//Connection con = connect(); // CONNECT TO THE DATABASE
			Statement stmt=con.createStatement();   // INITIATE OUR STATEMENT
			ResultSet rs=stmt.executeQuery(select);//SEND SELECT STATEMENT
			System.out.println("Returned Data Set" );
		    ResultSetMetaData rsmd = rs.getMetaData();//STORE THE RESULTSETMETADATA
		    int columnCount = rsmd.getColumnCount(); // GET RESULTSET COLUMN COUNT
		    int j = 0;
			while(rs.next())  {			
				System.out.println(j++);
				int c = 1; //INITIATE COLUMN PLACEMENT
				String line="";//INITIATE A STRING FOR COLUMN LINE				
				while(c<=columnCount) { // WHILE COLUMN COUNT IS LESS THAN RESULTS
					if(c ==1) line = ""+rs.getString(c); // FIRST COLUMN SHOULDN'T HAVE A LEADING COMMA  
					else line = line +"," +rs.getString(c); // ALL OTHERS SHOULD HAVE A LEADING COMMA
					c++;
				}
				results.put(results.size()+1, line);
				//System.out.println(size);
			}
			//con.close();
			//System.out.println(" id "  + id);
			return results;
		}
		catch(Exception e) {e.printStackTrace();HandleFile.logMessage(e.getMessage());return results;}
	}
	public static void main(String[] args) {
		String sql = HandleFile.getSQL("D:\\BuffaloCrime\\gitRepo\\ds-squad-project\\datakit\\queries\\incidentsFull.sql");
		connect();
		ConcurrentHashMap<Integer, String > map =selectStatement(sql);
		close();
		for (int i : map.keySet() ){			
			HandleFile.writeFileName("D:\results.csv", map.get(i));
		}
	}
}

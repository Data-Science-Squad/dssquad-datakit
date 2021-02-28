package work;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class date {
	//INITIATE DATE FORMATS WE MIGHT SEE
	private static DateFormat MMddyyyySlash = new SimpleDateFormat("MM/dd/yyyy"); 	
	private static DateFormat yyyyMMddSlashHHmmss = new SimpleDateFormat("MM/dd/yyyy HH:mm"); 
	private static DateFormat yyyyMMddDash = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat yyyyMM= new SimpleDateFormat("yyyyMM"); 
	private static SimpleDateFormat yyyy= new SimpleDateFormat("yyyy"); 
	private static DateFormat yyyyMMdd= new SimpleDateFormat("yyyyMMdd"); 
	private static DateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");  
	private static DateFormat yyyyMMddDashTHHmmss = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss"); 
	private static DateFormat yyyyMMddDashHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	 
	/*START DATE CONVERSIONS*/
	//STRING TO DATE
	public static Date stringtoDate(String s) {
		DateFormat use = new SimpleDateFormat("yyyyMMdd");
		Date newDate=null;
		if(s.contains(":") && s.contains("/")) 			use = yyyyMMddSlashHHmmss;		
		else if(s.contains("/"))use = MMddyyyySlash;
		else if(s.contains("T")) use = yyyyMMddDashTHHmmss;
		else if(s.contains(":") && s.contains("-")) use = yyyyMMddDashHHmmss;
		else if(s.contains("-"))use = yyyyMMddDash;
		else if(s.length() == 6 )use = yyyyMM;
		else if(s.length() == 8 )use = yyyyMMdd;
		try {
			newDate = use.parse(s); // PARSE DATE		
		}catch(Exception e ) {
			file.logMessage("FAILED CONVERTING" + s  + " TO DATE ");
		}
		return newDate ;
	}
	public static String datetoString(String s, Date d) {
		DateFormat use = new SimpleDateFormat("yyyyMMdd");
		String date="";
		if(s.contains("/"))use = MMddyyyySlash;
		else if(s.contains("T")) use = yyyyMMddDashTHHmmss;
		else if(s.contains(":") && s.contains("-")) {
			use = yyyyMMddDashHHmmss;
		}
		else if(s.contains("-"))use = yyyyMMddDash;
		else if(s.length() == 6 )use = yyyyMM;
		else if(s.length() == 8 )use = yyyyMMdd;
		else if(s.length() == 4 )use = yyyy;
		try {
			date = use.format(d); // PARSE DATE
			
		}catch(Exception e ) {
			file.logMessage("FAILED CONVERTING" + d  + " TO  + " +s +" DATE ");
		}
		return date ;
	}
	public static Date minusDays(Date date, int days) {
    	Date newDate =  null ;	    
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR, -(24*days));
		newDate = calendar.getTime();       	
    	return newDate;
    }
    public static String timestamp() {
     	String newDate = yyyyMMddHHmmss.format(new Date());
        return newDate;
    }	
}

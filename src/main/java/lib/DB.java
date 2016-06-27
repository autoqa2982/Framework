package lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import core.framework.Globals;
import oracle.jdbc.pool.OracleDataSource;

public class DB {

	public static List<ResultSet> masterRecordSet = new ArrayList<ResultSet>();
	public static HashMap<String, Connection> dbConnections = new HashMap<String, Connection>();
	
	public static ResultSet executeQuery(String dbName,String query,String... queryParameterValues) {
		
		ResultSet tempRecordSet = null;
		PreparedStatement stmt;

		try {				
			stmt = DB.getPreparedStatement(dbName, query, queryParameterValues);
			
			tempRecordSet = stmt.executeQuery();
			masterRecordSet.add(tempRecordSet);

		} catch (SQLException e) {
			throw new Error ("Execution of the Query \n" + query + "\nFailed with message \n" + e.getMessage());
		}
		return tempRecordSet;
	}
	

	public static int executeUpdate(String dbName,String query,String... queryParameterValues) throws Exception{
		
		int recordsUpdated = 0;
		PreparedStatement stmt;

		try {
		
			stmt = DB.getPreparedStatement(dbName, query, queryParameterValues);
			recordsUpdated = stmt.executeUpdate();
		} catch (SQLException e) {
			throw new Exception ("Execution of the Query \n" + query + "\nFailed with message \n" + e.getMessage());
		}
		return recordsUpdated;
	}
	
	private static PreparedStatement getPreparedStatement(String dbName,String query,String[] queryParameterValues)  {
		PreparedStatement stmt = null;
		int queryParamCnt = 0;
		String dataBaseName = Stock.getConfigParam(dbName.trim());
		
		Connection conn = getDBConnection(dataBaseName.trim());
		
		try {

			if(!conn.isClosed()){
				dbConnections.put(dataBaseName,conn);
				
				for (int i = 0; i < query.length(); i++) {
					if ("?".codePointAt(0) == query.codePointAt(i)) {
						queryParamCnt++;
					}
				}
			
				//queryParamCnt = query.replaceAll("= ? ", "=? ").split("=? ").length;
				
				stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				if (queryParamCnt == queryParameterValues.length) {
					for (int i = 1; i <= queryParameterValues.length; i++) {
						stmt.setString(i,queryParameterValues[i-1]);
					
					}
					
				} else {
					throw new Error ("Parameters in the query and the values sent missmatched");
				}
			}
		} catch (SQLException e) {
			throw new Error ("SQL Exception occurred while preparing query:\n" + query + "\n\nFailed with message:\n" + e.getMessage());
		}
		
		return stmt;
	}
	
	
	private static Connection getDBConnection(String DBName) {
		
		OracleDataSource dataSource;
		Connection conn;
		
		
		String jdbcUrl = null;
		String dbuserid = null;
		String dbpassword = null;
		//String actualDBName = ReadProperties.getEnvVariableValue(DBName.trim().toUpperCase()).trim().toUpperCase();
		
		if (dbConnections.containsKey(DBName)) {
			return dbConnections.get(DBName);
		} else {
	
			
			try {
				dataSource = new OracleDataSource();
				//jdbcUrl = Stock.getConfigParam(DBName);
				jdbcUrl = Globals.databaseConnectionStrings.get(DBName);
				dbuserid = Stock.getConfigParam("DBUSERID");
				dbpassword = Stock.getConfigParam("DBPASSWORD");
				
				if (jdbcUrl.trim().length() == 0) {
					throw new Error ("No connection string found for the DB: " + DBName);
				}
				
				dataSource.setURL(jdbcUrl);
				
				conn = dataSource.getConnection(dbuserid,dbpassword);
				
			} catch (SQLException e) {
				throw new Error ("Establishing connection to DB " + DBName + " Failed \n " + e.getMessage());
			}
			
			return conn;
		}
		
		
	}
	
	public static int getRecordSetCount(ResultSet resultSet) {
		
		int rSize = 0;
		
		try {
			resultSet.last();
			rSize = resultSet.getRow();
			resultSet.beforeFirst();
		} catch (SQLException e) {
			//System.out.println("RecordSet is empty");
			throw new Error ("SQL Exception: " + e.getMessage());
		}
		
		return rSize;
		
	}
	

	public static void closeDBConnections() throws Exception{
		
		Iterator<String> connectionsIterator = dbConnections.keySet().iterator();
		Iterator<ResultSet> recordSetsIterator = masterRecordSet.iterator();
		
		while (recordSetsIterator.hasNext()) {
			
			ResultSet currRecordSet = (ResultSet) recordSetsIterator.next();
			
			try {
				if (!currRecordSet.isClosed()) {
					currRecordSet.close();
					
				}
			} catch (SQLException e) {
			   throw new Exception ("list<Connections> :- a list containing db connections did not close properly");
			}	
		}
		recordSetsIterator.remove();
		
		
		while (connectionsIterator.hasNext()) {
			String connectionName = (String) connectionsIterator.next();
			
			try {
				if (!dbConnections.get(connectionName).isClosed()) {
						dbConnections.get(connectionName).close();
									
				}
			} catch (SQLException e) {
				
				System.out.println("reporter.report - clean connections for DB"); 
			} 
			
			dbConnections.remove(connectionName);	
		}
		
	}

	/*
	 * Compare data base date and web date
	 * 
	 * @PARAMETER = dbDate
	 * 
	 * @PARAMETER = webDate
	 * 
	 * @Author: Ranjan
	 */
	public static boolean compareDB_Date_With_Web_Date(String dbDate,
			String webDate) {

		boolean isDateEqual_Flag = false;
		Date date;
		try {
			date = new Date();
			dbDate = dbDate.substring(0, dbDate.indexOf(" "));
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			date = df.parse(dbDate);
			SimpleDateFormat sfd = new SimpleDateFormat("dd-MMM-yyyy");
			String strDate = sfd.format(date);
			if (strDate.equalsIgnoreCase(webDate))
				isDateEqual_Flag = true;
		} catch (ParseException e) {			
			e.printStackTrace();
		}
		return isDateEqual_Flag;
	}

}

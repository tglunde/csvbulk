package csvbulk;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;

import oracle.jdbc.OraclePreparedStatement;

public class RowBatchInsert {
	private String table;
	private String[] header;
	private Connection connection;
	private PreparedStatement stmt;

	public RowBatchInsert(String aTable, String[] aHeader, String url,
			String user, String pwd, String driverClass) throws SQLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		DriverManager.registerDriver((Driver) Class.forName(driverClass)
				.newInstance());
		table = aTable;
		header = aHeader;
		connection = initializeConnection(url, user, pwd);
		stmt = initializeSql(connection, table, header);
	}

	private PreparedStatement initializeSql(Connection aConnection,
			String aTable, String[] aHeader) throws SQLException {
		String sql = initializeSQL(aTable, aHeader);
		PreparedStatement aStmt = aConnection.prepareStatement(sql);
		if (aStmt instanceof OraclePreparedStatement) {
			((OraclePreparedStatement) aStmt).setExecuteBatch(1000000);
		}
		return aStmt;
	}

	private String initializeSQL(String aTable, String[] aHeader) {
		StringBuffer sb = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		StringBuffer params = new StringBuffer();
		Iterator<String> iter = Arrays.asList(aHeader).iterator();
		while (iter.hasNext()) {
			fields.append(iter.next());
			params.append('?');
			if (iter.hasNext()) {
				fields.append(',');
				params.append(',');
			}
		}
		sb.append("INSERT /*+ APPEND */ INTO ").append(aTable).append(" (")
				.append(fields).append(')');
		sb.append(" VALUES (").append(params).append(')');
		return sb.toString();
	}

	private Connection initializeConnection(String aUrl, String aUser,
			String aPassword) throws SQLException {
		Connection conn = DriverManager.getConnection(aUrl, aUser, aPassword);
		conn.setAutoCommit(false);
		conn.createStatement().executeQuery(
				"alter session set nls_date_format='MM/DD/YYYY HH24:MI:SS'");
		return conn;
	}

	public void addRow(String[] row) throws SQLException {
		for (int i = 0; i < row.length; i++) {
			stmt.setObject(i + 1, row[i]);
		}
		stmt.execute();
	}

	public void finish() throws SQLException {
		connection.commit();
		stmt.close();
	}

	public void rollback() throws SQLException {
		connection.rollback();
		stmt.close();
	}
}

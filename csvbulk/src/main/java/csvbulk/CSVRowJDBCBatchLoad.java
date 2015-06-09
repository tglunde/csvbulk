package csvbulk;

import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

public class CSVRowJDBCBatchLoad implements Runnable {
	private BlockingQueue<CSVRow> queue;

	private int batchSize;
	private String tableName;
	private String driverClass;
	private String url;
	private String userName;
	private String password;

	public CSVRowJDBCBatchLoad(BlockingQueue<CSVRow> queue, int batchSize,
			String tableName, String driverClass, String url, String userName,
			String password) {
		super();
		this.queue = queue;
		this.batchSize = batchSize;
		this.tableName = tableName;
		this.driverClass = driverClass;
		this.url = url;
		this.userName = userName;
		this.password = password;
	}

	public void run() {
		try {
			CSVRow msg;
			RowBatchInsert rbi = null;
			while ((msg = queue.take()).getMsg() != "exit") {
				try {
					if (rbi == null) {
						rbi = new RowBatchInsert(tableName, msg.getHeader(),
								batchSize, url, userName, password, driverClass);
					}
					rbi.addRow(msg.getHeader());

				} catch (SQLException sql) {
					if (rbi != null) {
						try {
							rbi.rollback();
						} catch (SQLException e) {
						}
					}
					throw new RuntimeException(sql);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

package csvbulk;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CSVBulkLoad {

	public static void main(String[] args) throws InterruptedException {
		int parallel = 10;
		String tableName = "NO_PARAM_SET";
		if (args != null && args.length > 0) {
			tableName = args[0];
		}
		BlockingQueue<CSVRow> queue = new ArrayBlockingQueue<CSVRow>(parallel);
		CSVRowProducer producer = new CSVRowProducer(queue, Thread
				.currentThread().getContextClassLoader()
				.getResourceAsStream("test.csv"));

		ExecutorService consumerService = Executors
				.newFixedThreadPool(parallel + 1);
		for (int i = 0; i < 10; i++) {
			consumerService.execute(new CSVRowConsumer(queue, tableName,
					"oracle.jdbc.driver.OracleDriver",
					"jdbc:oracle:thin:@presmaus.zd.guj.de:1522:HDWHDPV",
					"DM_BUS", "00Dj7jSDX2MD67"));
		}
		consumerService.execute(producer);
		consumerService.shutdown();
		// if(!consumerService.awaitTermination(3, TimeUnit.HOURS)) {
		// throw new
		// RuntimeException("Tasks not finished. See previous errors.");
		// };
	}
}

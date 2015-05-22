package csvbulk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class CSVRowProducer implements Runnable {

	private BlockingQueue<CSVRow> queue;
	private InputStream input;

	public CSVRowProducer(BlockingQueue<CSVRow> queue, InputStream input) {
		this.queue = queue;
		this.input = input;
	}

	public void run() {
		RowHeaderProcessor processor = new RowHeaderProcessor();
		CsvParserSettings parserSetting = new CsvParserSettings();
		parserSetting.setRowProcessor(processor);
		CsvFormat csvFormat = new CsvFormat();
		csvFormat.setComment('|');
		parserSetting.setFormat(csvFormat);
		parserSetting.setHeaderExtractionEnabled(true);
		parserSetting.setIgnoreLeadingWhitespaces(true);
		parserSetting.setIgnoreTrailingWhitespaces(true);

		CsvParser parser = new CsvParser(parserSetting);
		try {
			parser.beginParsing(new BufferedReader(new InputStreamReader(input)));
			String[] row = null;
			String[] header = null;
			while ((row = parser.parseNext()) != null) {
				// intentionally left blank
				if (header == null) {
					header = processor.getHeaders();
					if (header == null || header.length != row.length) {
						throw new IOException("Invalid parsing");
					}
				}
				queue.put(new CSVRow(header, row));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			parser.stopParsing();
			try {
				queue.put(new CSVRow("exit"));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

}

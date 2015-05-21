package csvbulk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class CSVBulkLoad {

	public static void main(String[] args) {
		String tableName = "NO_PARAM_SET";
		if (args != null && args.length > 0) {
			tableName = args[0];
		}
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
		// parser.beginParsing(new BufferedReader(new
		// InputStreamReader(FileUtils.openInputStream(new
		// File("X://Temp//test2.csv")))));
		RowBatchInsert rbi = null;
		try {
			parser.beginParsing(new BufferedReader(new InputStreamReader(
					System.in)));
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
				if (rbi == null) {
					rbi = new RowBatchInsert(
							tableName,
							header,
							"jdbc:oracle:thin:@presmaus.zd.guj.de:1522:HDWHDPV",
							"DM_BUS", "00Dj7jSDX2MD67");
				}
				rbi.addRow(row);
			}
			rbi.finish();
		} catch (SQLException sql) {
			sql.printStackTrace();
			if (rbi != null) {
				try {
					rbi.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			parser.stopParsing();
		}
	}
}

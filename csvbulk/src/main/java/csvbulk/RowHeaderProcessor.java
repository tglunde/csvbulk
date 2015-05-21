package csvbulk;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.AbstractRowProcessor;

public class RowHeaderProcessor extends AbstractRowProcessor {

	private String[] headers = null;

	public String[] getHeaders() {
		return headers;
	}

	@Override
	public void processEnded(ParsingContext context) {
		this.headers = context.headers();
		correct(headers);
	}

	@Override
	public void rowProcessed(String[] row, ParsingContext context) {
		this.headers = context.headers();
		correct(headers);
	}

	private void correct(String[] header) {
		for (int i = 0; i < header.length; i++) {
			if (header[i].startsWith("#")) {
				header[i] = header[i].substring(1);
			}
		}
	}

}

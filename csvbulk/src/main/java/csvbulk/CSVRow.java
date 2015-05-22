package csvbulk;

import java.io.Serializable;

public class CSVRow implements Serializable {

	private static final long serialVersionUID = 1L;
	private String msg="run";
	private String[] header;
	private String[] row;

	public CSVRow(String msg) {
		this.msg=msg;
	}

	public CSVRow(String[] header, String[] row) {
		super();
		this.header = header;
		this.row = row;
	}

	public String[] getHeader() {
		return header;
	}

	public void setHeader(String[] header) {
		this.header = header;
	}

	public String[] getRow() {
		return row;
	}

	public void setRow(String[] row) {
		this.row = row;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}

package cn.vpfinance.vpjr.model;

import java.util.List;

public class BaoliItem
{
	private int id;
	private int sort;
	private int loansignId;
	private int borrowbaseId;
	private String content;
	private String name;
	private int ctype;
	private List<File> files;

	public static class File
	{
		private int id;
		private int borrowersAdditionalId;
		private String fileName;
		private String filePath;
		private String fileRemark;

		public int getBorrowersAdditionalId() {
			return borrowersAdditionalId;
		}

		public void setBorrowersAdditionalId(int borrowersAdditionalId) {
			this.borrowersAdditionalId = borrowersAdditionalId;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		public String getFileRemark() {
			return fileRemark;
		}

		public void setFileRemark(String fileRemark) {
			this.fileRemark = fileRemark;
		}
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBorrowbaseId() {
		return borrowbaseId;
	}

	public void setBorrowbaseId(int borrowbaseId) {
		this.borrowbaseId = borrowbaseId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCtype() {
		return ctype;
	}

	public void setCtype(int ctype) {
		this.ctype = ctype;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLoansignId() {
		return loansignId;
	}

	public void setLoansignId(int loansignId) {
		this.loansignId = loansignId;
	}
}
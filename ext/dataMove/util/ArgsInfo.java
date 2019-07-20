package ext.dataMove.util;

import java.io.Serializable;

public class ArgsInfo implements Serializable {

	private static final long serialVersionUID = 5135071814006183179L;

	private String cabinet;

	private String beginTime;

	private String endTime;

	private String detail;

	private String type;
	
	private String content;
	
	private String productType;
	

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCabinet() {
		return cabinet;
	}

	public void setCabinet(String cabinet) {
		this.cabinet = cabinet;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public static ArgsInfo getInstance(String[] args) {
		ArgsInfo argsInfo = new ArgsInfo();
		return argsInfo.initialize(args);
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public ArgsInfo initialize(String[] args) {
		if (args == null) {
			return this;
		}
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("-cabinet".equalsIgnoreCase(arg)) {
				i++;
				this.cabinet = args[i];
			} else if ("-begintime".equalsIgnoreCase(arg)) {
				i++;
				this.beginTime = args[i];
			} else if ("-endtime".equalsIgnoreCase(arg)) {
				i++;
				this.endTime = args[i];
			} else if ("-d".equalsIgnoreCase(arg)) {
				i++;
				this.detail = args[i];
			} else if ("-type".equalsIgnoreCase(arg)) {
				i++;
				this.type = args[i];
			}else if("-content".equalsIgnoreCase(arg)){
				i++;
				this.content = args[i];
			}else if("-productType".equalsIgnoreCase(arg)){
				i++;
				this.productType = args[i];
			}
			System.out.println("args[" + i + "] = " + args[i]);
		}

		return this;
	}

}

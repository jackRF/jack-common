package org.jack.common.logger;

public class InvokeInfo<T> {
	private String clazz;
	private String method;
	private String idNo;
	private String loanNo;
	private T start;
	private T end;
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getLoanNo() {
		return loanNo;
	}
	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}
	public T getStart() {
		return start;
	}
	public void setStart(T start) {
		this.start = start;
	}
	public T getEnd() {
		return end;
	}
	public void setEnd(T end) {
		this.end = end;
	}
	
}

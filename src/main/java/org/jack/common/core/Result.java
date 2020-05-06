package org.jack.common.core;

public class Result<D>{
    public static final String CODE_SUCCESS="0000";
    public static final String CODE_FAIL="0001";
    public static final Result<Void> SUCCESS=new UnModifiableResult<>();
    private String code;
	private String message;
	private D data;
	public Result() {
		this(CODE_SUCCESS,"");
	}
	public Result(String code,String message) {
		this.code=code;
		this.message=message;
	}
	public Result(D data){
		this();
		this.data=data;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code=code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message=message;
	}
	public D getData() {
		return data;
	}
	public void setData(D data) {
		this.data = data;
	}
	public boolean isSuccess() {
		return CODE_SUCCESS.equals(code);
	}
	public static <D> Result<D> fail(String message){
		return new Result<D>(CODE_FAIL,message);
	}
	public static <D> Result<D> success(D data){
		return new Result<D>(data);
	}
	public static class UnModifiableResult<D> extends Result<D>{
		public UnModifiableResult() {
			super();
		}
		public UnModifiableResult(String code,String message){
			super(code, message);
		}
		public UnModifiableResult(D data){
			super(data);
		}
		@Override
		public void setCode(String code) {
			throw new UnsupportedOperationException("不可修改");
		}
		@Override
		public void setMessage(String message) {
			throw new UnsupportedOperationException("不可修改");
		}
		@Override
		public void setData(D data) {
			throw new UnsupportedOperationException("不可修改");
		}
	}
}
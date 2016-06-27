package com.abook23.utils.http.bean;

public class HttpInfo {

	/**
	 * http 返回code
	 */
	private int HttpCode;
	/**
	 * http 异常
	 */
	private String CodeExplain;
	/**
	 * http 返回数据
	 */
	private String result;
	
	public int getHttpCode() {
		return HttpCode;
	}
	public void setHttpCode(int httpCode) {
		HttpCode = httpCode;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getCodeExplain() {
		return CodeExplain;
	}
	public void setCodeExplain(String codeExplain) {
		CodeExplain = codeExplain;
	}

	
	
}

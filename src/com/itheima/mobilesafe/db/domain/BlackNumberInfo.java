package com.itheima.mobilesafe.db.domain;

public class BlackNumberInfo {

	private String phone;
	private String mode;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public BlackNumberInfo(String phone, String mode) {
		super();
		this.phone = phone;
		this.mode = mode;
	}
	@Override
	public String toString() {
		return "BlackNumberInfo [phone=" + phone + ", mode=" + mode + "]";
	}
	
	
}

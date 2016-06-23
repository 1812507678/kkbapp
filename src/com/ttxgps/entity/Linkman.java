package com.ttxgps.entity;

public class Linkman {
	private String alias;
	private int grade;
	private String number;


	public Linkman(int grade, String alias, String number) {
		this.grade = grade;
		this.alias = alias;
		this.number = number;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getGrade() {
		return this.grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

}

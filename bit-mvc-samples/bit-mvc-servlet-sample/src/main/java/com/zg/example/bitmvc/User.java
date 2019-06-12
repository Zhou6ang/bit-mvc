package com.zg.example.bitmvc;

public class User {
	
	private int id;
	private String name;
	private int age;
	
	public User(int id, String name,int age) {
		this.id = id;
		this.name = name;
		this.age =age;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
}
class Data {
	
	private int id;
	private int score;
	private int rank;
	
	public Data(int id, int score,int rank) {
		this.id = id;
		this.score = score;
		this.rank =rank;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
	
}

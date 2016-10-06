package com.squad.voice.skill;

import java.util.*;

public class Event{
	
	private String eventName, date, startTime, cost, category, description;
	
	//Default constructor
	public Event(){
		eventName = "";
		date = "";
		startTime = "";
		cost = "";
		category = "";
		description = "";
	}
	//Constructor
	public Event(String eventName, String date, String startTime, String cost, String categ, String desc){
		this.eventName = eventName;
		this.date = date;
		this.startTime = startTime;
		this.cost = cost;
		category = categ;
		description = desc;
	}
	
	//Setters
	public void setEventName(String name){eventName = name;}
	public void setDate(String date){this.date = date;}
	public void setTime(String time){startTime = time;}
	public void setCost(String cost){this.cost = cost;}
	public void setCategory(String category){this.category = category;}
	public void setDesc(String desc){description = desc;}
	//Getters
	public String getEventName(){return eventName;}
	public String getDate(){return date;}
	public String getTime(){ return startTime; }
	public String getCost(){ return cost; }
	public String getCategroy(){ return category; }
	public String getDesc(){ return description; }

	
	
}
	
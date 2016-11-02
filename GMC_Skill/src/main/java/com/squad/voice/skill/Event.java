package com.squad.voice.skill;

import java.net.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.jsoup.Jsoup;

public class Event{
	
//	public static void main(String[] args) {
//	 	System.out.println("test");
//	 	Event e = new Event();
//	 	System.out.println(e.getFirst3Events());
//	}
	private int size = 0;
	private String eventName, date, startTime, cost, category, description, website;
	private Event[] events;
	private final String url = "http://25livepub.collegenet.com/calendars/Highlighted_Event.rss";
	private boolean bFirst3Events = false;
	
	//Default constructor
	public Event(){
		eventName = "";
		date = "";
		startTime = "";
		cost = " Not Provided: ";
		category = "";
		description = "";
		website = "";
		int size = 0;
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
	public void setWebsite(String site){website = site;}
	//Getters
	public String getTitle(){return eventName;}
	public String getDate(){return date;}
	public String getTime(){ return startTime;}
	public String getPrice(){ return cost;}
	public String getCategroy(){ return category;}
	public String getDesc(){ return description;}
	public String getSite(){return website;}
	public int numEvents(){ return events.length; }
	public int size(){return size;}



	public static String replaceAll(String source, String pattern, String replacement) {
        if (source == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        int index;
        int patIndex = 0;
        while ((index = source.indexOf(pattern, patIndex)) != -1) {
            sb.append(source.substring(patIndex, index));
            sb.append(replacement);
            patIndex = index + pattern.length();
        }
        sb.append(source.substring(patIndex));
        return sb.toString();
    }
	
	//Builds the XML document from the RSS feed
	public Document buildXMLDoc(String url){
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new URL(url).openStream());
			return doc;
		}catch(Exception e){
			System.out.println("Error");
			return null;
		}
	}
	
	//formats the date string so Alexa can interpret it as a date
	public String formatDate(String stng){
		int start = stng.indexOf('(');
		stng = stng.substring(0, start);
		stng = stng.replace('/', '-');
		stng = stng.replaceAll("\\s+","");

		return stng;
	}
	
	public String cleanDescription(String desc){
		Document doc = buildXMLDoc(url);
		//return Jsoup.parse(doc.getElementsByTagName("description").item(index).getTextContent()).text();
		return Jsoup.parse(desc).text();
	}

    public Event[] parseRSSFeed(){
    	
	   	Document doc = buildXMLDoc(url);
	   	size = doc.getElementsByTagName("item").getLength();
	    //NodeList eventList = doc.getElementsByTagName("item");
	    events = new Event[size];
	    int mod = 0;
	    String eventDesc, eventTitle, eventPrice, eventDate, eventTime, eventSite;
		    for (int i = 0 ; i < size - mod; i++){	
		    	events[i] = new Event();
		    	eventTitle = doc.getElementsByTagName("item").item(i+mod).getChildNodes().item(1).getTextContent();
		    	eventTitle = eventTitle.replace("&", "and");
		    	events[i].eventName = eventTitle; 
		    	eventDate = doc.getElementsByTagName("item").item(i+mod).getChildNodes().item(9).getTextContent();
		   		eventDate = formatDate(eventDate);
		   		events[i].date = eventDate;
		   		eventDesc = Jsoup.parse(doc.getElementsByTagName("item").item(i+mod).getChildNodes().item(3).getTextContent()).text();
		   		eventSite = doc.getElementsByTagName("item").item(i+mod).getChildNodes().item(3).getTextContent();
		   		int temp5 = eventSite.indexOf("Web");
		   		eventSite = eventSite.substring(temp5,eventSite.indexOf("target" , temp5));
		   		eventSite = eventSite.replace("</b>:&nbsp;<a href=", ": ");
		   		eventSite = eventSite.replace("\\", "");
		   		eventSite = eventSite.replace("\"", "");
		   		eventTime = doc.getElementsByTagName("item").item(i+mod).getChildNodes().item(3).getTextContent();
		  	 	int temp3 = eventTime.indexOf("<br/>");
				int temp4 = eventTime.indexOf("<br/>", temp3+1);
				eventTime = eventTime.substring(temp3, temp4);
		   		events[i].startTime = Jsoup.parse(eventTime).text();
		   		
		   		int temp = eventDesc.indexOf("pm") + 2;
		   		int temp2 = eventDesc.indexOf("Web Site");
		   		eventDesc = eventDesc.substring(temp, temp2);
		   		eventDesc = eventDesc.replace("&", "and");
		   		if(eventDesc.contains("$")){
			   		temp = eventDesc.indexOf("$");
			   		temp2 = temp + 3; 
			   		eventPrice = eventDesc.substring(temp, temp2);
			   		if(eventPrice.contains("-")){
			   			eventPrice = eventPrice.replace("-", "");
			   		}
			   		events[i].cost = eventPrice;
			   	}else{
			   		events[i].cost = " not provided: ";
			   	}

			   	if(i > 0){
				   	if(eventTitle.equals(events[i-1].getTitle())){
				   		if(!(events[i-1].getDate().contains(eventDate))){
				   			events[i-1].setDate(events[i-1].getDate() + ", " + eventDate);
				   			events[i-1].setTime(events[i-1].getTime() + ", " + eventTime);
				   			eventTime = events[i-1].getTime();
				   			eventTime = (eventTime.replace("2016, ", ""));
				   			eventTime = (eventTime.replace("pm,", "pm;"));
				   			events[i-1].setTime(Jsoup.parse(eventTime).text());
				   		}
				   		i--;
				   		mod += 1;
				   	}
			  	 }
		   		events[i].description = eventDesc;
		    	events[0].size = i + 1;
		    }
    	
    	return (events);
    }
    
    
	
}
	
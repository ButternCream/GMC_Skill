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
	public void setEventName(int i){
		Document doc = buildXMLDoc(url);
		String eventTitle = doc.getElementsByTagName("item").item(i).getChildNodes().item(1).getTextContent();
	    eventTitle = eventTitle.replace("&", "and");
		eventName = eventTitle;

	}

	public void setDate(int i){
		Document doc = buildXMLDoc(url);
		String eventDate = doc.getElementsByTagName("item").item(i).getChildNodes().item(9).getTextContent();
		eventDate = formatDate(eventDate);
		this.date = date;
	}

	public void resetDate(String eventDate){this.date = eventDate;}

	public void setTime(int i){
		Document doc = buildXMLDoc(url);
   		String eventTime = doc.getElementsByTagName("item").item(i).getChildNodes().item(3).getTextContent();
  	 	int temp = eventTime.indexOf("<br/>");
		int temp2 = eventTime.indexOf("<br/>", temp + 1);
		eventTime = eventTime.substring(temp, temp2);
   		startTime = Jsoup.parse(eventTime).text();;
	}

	public void resetTime(String time){this.startTime = time;}

	public void setCost(int i){
		Document doc = buildXMLDoc(url);
		String eventPrice;
		if(this.getDesc().contains("$")){
	   		int temp = this.getDesc().indexOf("$");
	   		eventPrice = this.getDesc().substring(temp, temp + 3);
	   		if(eventPrice.contains("-")){
	   			eventPrice = eventPrice.replace("-", "");
	   		}
	   	}else{
	   		eventPrice = " not provided: ";
	   	}
		this.cost = eventPrice;
	}

	public void setDesc(int i){
		Document doc = buildXMLDoc(url);
		String eventDesc = Jsoup.parse(doc.getElementsByTagName("item").item(i).getChildNodes().item(3).getTextContent()).text();
   		int temp = eventDesc.indexOf("pm") + 2;
   		int temp2; 
   		if (eventDesc.contains("General Admission"))
   		temp2 = eventDesc.indexOf("General Admission"); 
   		else 
   			temp2 = eventDesc.indexOf("Web");
   		eventDesc = eventDesc.substring(temp, temp2);
   		eventDesc = eventDesc.replace("&", "and");
		description = eventDesc;
	}
	public void setWebsite(int i){
		Document doc = buildXMLDoc(url);
		String eventSite = doc.getElementsByTagName("item").item(i).getChildNodes().item(3).getTextContent();
   		int temp = eventSite.indexOf("Web");
   		eventSite = eventSite.substring(temp,eventSite.indexOf("target" , temp));
   		eventSite = eventSite.replace("</b>:&nbsp;<a href=", ": ");
   		eventSite = eventSite.replace("\\", "");
   		eventSite = eventSite.replace("\"", "");
		website = "";
	}
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
		    	events[i].setEventName(i+mod);
		    	events[i].setDesc(i+mod);
		    	events[i].setTime(i+mod);
		    	events[i].setDate(i+mod);
		    	events[i].setWebsite(i+mod);
		    	events[i].setCost(i+mod);
			   	if(i > 0){
				   	if(events[i].getTitle().equals(events[i-1].getTitle())){
				   		if(!(events[i-1].getDate().contains(events[i].getDate()))){
				   			events[i-1].resetDate(events[i-1].getDate() + ", " + events[i].getDate());
				   			eventTime = events[i-1].getTime() + ", " + events[i].getTime();
				   			eventTime = (eventTime.replace("2016, ", ""));
				   			eventTime = (eventTime.replace("pm,", "pm;"));
				   			eventTime = Jsoup.parse(eventTime).text();
				   			events[i-1].resetTime(eventTime);
				   		}
				   		i--;
				   		mod += 1;
				   	}
			  	 }
		    	events[0].size = i + 1;
		    }
    	
    	return (events);
    }
    
    
	
}
	
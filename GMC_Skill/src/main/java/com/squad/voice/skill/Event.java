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

	private String eventName, date, startTime, cost, category, description;
	private Event[] events;
	private final String url = "http://25livepub.collegenet.com/calendars/Highlighted_Event.rss";
	private boolean bFirst3Events = false;
	
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
	public String getTitle(){return eventName;}
	public String getDate(){return date;}
	public String getTime(){ return startTime;}
	public String getPrice(){ return cost;}
	public String getCategroy(){ return category;}
	public String getDesc(){ return description;}
	public int numEvents(){ return events.length; }

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
	public String formatDate(String datestr){
		int start = datestr.indexOf("(");
		int end = datestr.indexOf(")");
		String toBeReplaced = datestr.substring(start-1, end+1);
		return datestr.replace(toBeReplaced, "");
	}
	
	public String cleanDescription(String desc){
		//Document doc = buildXMLDoc(url);
		//return Jsoup.parse(doc.getElementsByTagName("description").item(index).getTextContent()).text();
		return Jsoup.parse(desc).text();
	}
	
	//Gets the first 3 events and their dates
//    public String getFirst3Events(){
//    	String titles = "";
//    	//String desc = cleanDescription(3);
//    	try{
//    			Document doc = buildXMLDoc(url);
//				for (int i = 2; i < 5; i++){
//					String currentDate = doc.getElementsByTagName("category").item(i-2).getTextContent();
//					int start = desc.indexOf('$');
//					if (start != -1)
//					{
//						int end = start+1;
//						while (Character.isDigit(desc.charAt(end))){
//							end++;
//						}
//						if (desc.charAt(end) == '-'){
//							end++;
//							while (Character.isDigit(desc.charAt(end+1))){
//								end++;
//							}
//						}
//						//System.out.println(desc.substring(start, end+1));
//						
//						desc = desc.substring(start, end+1);
//						desc = replaceAll(desc, "-", " to ");
//			
//					}
//					//description = Jsoup.parse(description).text();
//					currentDate = formatDate(currentDate);
//					currentDate = replaceAll(currentDate, "/", "");
//					titles += doc.getElementsByTagName("title").item(i).getTextContent() 
//							+ " on" + "<say-as interpret-as=\"date\">" 
//							+ currentDate + "</say-as>." 
//							+ "<break strength=\"strong\"/>";
//				}
//				//Parsed description
//				//description += Jsoup.parse(doc.getElementsByTagName("description").item(2).getTextContent()).text();
//			}catch(Exception e){
//				System.out.println("Error");
//			}
//    		bFirst3Events = true;
//    		
//			//return "The next 3 events are <break strength=\"medium\"/>" + titles;
//    		return desc;
//	}

    public Event[] parseRSSFeed(){
    	
	   	Document doc = buildXMLDoc(url);
	   	int size = doc.getElementsByTagName("item").getLength();
	    //NodeList eventList = doc.getElementsByTagName("item");
	    events = new Event[size];
	    String eventDesc, eventTitle, eventPrice, eventDate;
		    for (int i = 0 ; i < size; i++){	
		    	events[i] = new Event();
		    	eventTitle = doc.getElementsByTagName("item").item(i).getChildNodes().item(1).getTextContent();
		    	events[i].eventName = eventTitle; 
		   		//eventDesc = cleanDescription(doc.getElementsByTagName("item").item(i).getChildNodes().item(1).getTextContent());
		   		//events[i].description = eventDesc;
		   		//eventDate = doc.getElementsByTagName("item").item(i).getChildNodes().item(4).getTextContent();
		   		//events[i].date = formatDate(eventDate);
		    }
    	
    	return (events);
    }
    
    
	/*
	System.out.println("----------Descriptions-----------");
	for (int i = 0; i < doc.getElementsByTagName("description").getLength(); i++){
		String desc = "\nDescription:\n" + doc.getElementsByTagName("description").item(i).getTextContent().replaceAll("\\<.*?>", "") + "\n";
		desc = replaceAll(desc, "&quot;", "\"");
		desc = replaceAll(desc, "&amp;", "&");
		desc = replaceAll(desc, "&rsquo;", "'");
		desc = replaceAll(desc, "&nbsp;", " ");
		desc = replaceAll(desc, "&ndash;", "-");
		System.out.println(desc);
	}
	System.out.println("----------Dates/Category-----------");
	for (int i = 0; i < doc.getElementsByTagName("category").getLength(); i++){
		System.out.println(doc.getElementsByTagName("category").item(i).getTextContent());
	}
	*/
	
}
	
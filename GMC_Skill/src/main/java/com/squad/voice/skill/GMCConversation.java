package com.squad.voice.skill;

import com.amazon.speech.slu.Intent;

import java.util.concurrent.ThreadLocalRandom;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.ui.*;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.squad.voice.model.base.Conversation;
import java.util.Map;
import com.squad.voice.skill.Event;
import java.util.concurrent.ThreadLocalRandom;

public class GMCConversation extends Conversation {
	// Intent names
	private final static String INTENT_START = "StartGMCIntent";
	private final static String INTENT_UPCOMING_GMC = "GenericUpcomingEventsIntent";
	private final static String INTENT_DATE_GMC = "DateSpecifiedEventsIntent";
	private final static String INTENT_SPECIFIC_EVENT_DETAILS = "SpecificEventDetailsIntent";
	private final static String INTENT_MORE_EVENTS = "MoreIntent";
	private final static String INTENT_SPECIFIC_EVENT_PURCHASE = "SpecificEventPurchaseIntent";
	private final static String INTENT_LAST_EVENT_PURCHASE = "KnownEventTicketsIntent";
	private final static String INTENT_HOW_MUCH = "HowMuchIntent";
	private final static String INTENT_WHAT_DATE = "WhatDateIntent";
	private final static String INTENT_WHAT_TIME = "WhatTimeIntent";

	private final static String INTENT_KNOWN_EVENT_HOW_MUCH = "KnownEventHowMuchIntent";
	private final static String INTENT_KNOWN_EVENT_WHAT_DATE = "KnownEventWhatDateIntent";
	private final static String INTENT_KNOWN_EVENT_WHAT_TIME = "KnownEventWhatTimeIntent";
	private final static String INTENT_KEYWORD = "handleKeywordIntent";


	private final static String INTENT_YES = "AMAZON.YesIntent";
	private final static String INTENT_NO = "AMAZON.NoIntent";
	private final static String INTENT_HELP = "AMAZON.HelpIntent";
	private final static String INTENT_STOP = "AMAZON.StopIntent";
	private final static String INTENT_REPEAT = "AMAZON.RepeatIntent";
	private final static String INTENT_CANCEL = "AMAZON.CancelIntent";



	// Slots
	private final static String EVENT_NAME = "specificEvent";
	private final static String DATE = "dateSpecified";

	// State
	// These can be used to change the state of the conversation
	private final static Integer STATE_GIVEN_DETAILS = 100000;
	private final static Integer STATE_GIVEN_EVENTS = 100001;
	private final static Integer STATE_WAITING_FOR_EVENT_REQ = 100002;
	private final static Integer STATE_MADE_RESERV = 100003;

	// Session state storage key
	private final static String SESSION_EVENT_STATE = "eventState";

	// Parse the RSS feed into the array of events
	public Event[] events = new Event().parseRSSFeed();
<<<<<<< HEAD
	public static String[] eventResponses = {"A few events after that are: ", 
			"Some of the next events coming up are: ", 
			"Following those events are: " };  
=======
	public static String[] eventResponses = {"A few events after that are: ",
			"Some of the next events coming up are: ",
			"Following those events are: " };
>>>>>>> dc771def0c33a26e6ef8bcc6b23f48b0cd346820

	public static final String purchaseURL = "http://25livepub.collegenet.com/calendars/highlighted_event";
	// Globals
	public int lastRead = 0;
	SpeechletResponse storedResponse = newAskResponse("I haven't said anything yet", false, "You can ask for events occurring on a specific date", false);
	SimpleCard lastReadCard = new SimpleCard();
	
	public GMCConversation() {
		super();
		//Add custom intent names for dispatcher use
		//**I don't have a list of our functions made @ last meeting, is anything missing?**
		supportedIntentNames.add(INTENT_START);
		supportedIntentNames.add(INTENT_UPCOMING_GMC);
		supportedIntentNames.add(INTENT_DATE_GMC);
		supportedIntentNames.add(INTENT_SPECIFIC_EVENT_DETAILS);
		supportedIntentNames.add(INTENT_MORE_EVENTS);
		supportedIntentNames.add(INTENT_SPECIFIC_EVENT_PURCHASE);
		supportedIntentNames.add(INTENT_LAST_EVENT_PURCHASE);
		supportedIntentNames.add(INTENT_KEYWORD);


		supportedIntentNames.add(INTENT_HOW_MUCH);
		supportedIntentNames.add(INTENT_WHAT_DATE);
		supportedIntentNames.add(INTENT_WHAT_TIME);
		supportedIntentNames.add(INTENT_KNOWN_EVENT_HOW_MUCH);
		supportedIntentNames.add(INTENT_KNOWN_EVENT_WHAT_DATE);
		supportedIntentNames.add(INTENT_KNOWN_EVENT_WHAT_TIME);

		supportedIntentNames.add(INTENT_YES);
		supportedIntentNames.add(INTENT_NO);
		supportedIntentNames.add(INTENT_HELP);
		supportedIntentNames.add(INTENT_STOP);
		supportedIntentNames.add(INTENT_REPEAT);
		supportedIntentNames.add(INTENT_CANCEL);

	}

	/**
	 * Conversation handler. Decides which handler will respond to the user based on the Intent of their utterance
	 */
	@Override
	public SpeechletResponse respondToIntentRequest(IntentRequest intentReq, Session session) {
		Intent intent = intentReq.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;
		SpeechletResponse response = null;

		if (INTENT_START.equals(intentName)) {
			response = handleGMCIntentStart(intentReq, session);
		}
		else if (INTENT_UPCOMING_GMC.equals(intentName)) {
			response = handleGenericUpcomingIntent(intentReq, session);
		}
		else if (INTENT_DATE_GMC.equals(intentName)) {
			response = handleDateSpecifiedIntent(intentReq, session);
		}
		else if (INTENT_SPECIFIC_EVENT_DETAILS.equals(intentName)) {
			response = handleSpecificEventDetailsIntent(intentReq, session);
		}
		else if (INTENT_SPECIFIC_EVENT_PURCHASE.equals(intentName)) {
			response = handleSpecificEventPurchaseIntent(intentReq, session);
		}
		else if (INTENT_LAST_EVENT_PURCHASE.equals(intentName)){
			response = handleLastEventPurchaseIntent(intentReq, session);
		}
		else if (INTENT_WHAT_TIME.equals(intentName)){
			response = handleWhatTimeIntent(intentReq, session);
		}
		else if (INTENT_WHAT_DATE.equals(intentName)){
			response = handleWhatDateIntent(intentReq, session);
		}
		else if (INTENT_HOW_MUCH.equals(intentName)){
			response = handleHowMuchIntent(intentReq, session);
		}
		else if (INTENT_KNOWN_EVENT_WHAT_TIME.equals(intentName)){
			response = handleKnownEventWhatTimeIntent(intentReq, session);
		}
		else if (INTENT_KNOWN_EVENT_WHAT_DATE.equals(intentName)){
			response = handleKnownEventWhatDateIntent(intentReq, session);
		}
		else if (INTENT_KNOWN_EVENT_HOW_MUCH.equals(intentName)){
			response = handleKnownEventHowMuchIntent(intentReq, session);
		}
		else if (INTENT_CANCEL.equals( intentName )){
			response = handleEndConvoIntent(intentReq, session);
		}
		else if (INTENT_REPEAT.equals(intentName)) {
			response = handleRepeatIntent(intentReq, session);
		}
		else {
			response = newTellResponse("Sorry, I didn't get that.", false);
		}

		return response;
	}

	/**
	 * Intent: cancel skill
	 * Terminates the conversation
     */
	private SpeechletResponse handleEndConvoIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = newTellResponse("Alright, let me know if you need anything else.", false);
		session.removeAttribute(SESSION_EVENT_STATE);

		return response;


	}

	/**
	 * Intent: repeat
	 * Repeats the last response read to the user
     */
	private SpeechletResponse handleRepeatIntent(IntentRequest intentreq, Session session) {
		return storedResponse;
	}

	/**
	 * Intent: none/help
	 * Provides information regarding the skill and sends a skill instruction card to the app
     */
	private SpeechletResponse handleGMCIntentStart(IntentRequest intentReq, Session session) {
		SimpleCard card = new SimpleCard();
		card.setTitle("Green Music Center");
		String speechOutput = "Hello, I am GMC event helper, I can provide information about events " +
				"happening at the Green music center, try asking me about upcoming events";
		card.setContent(speechOutput);
		SpeechletResponse response = newAskResponse(speechOutput, false, "You can also ask for events occurring on a specific date", false);
		response.setCard(card);
		session.setAttribute(SESSION_EVENT_STATE, STATE_WAITING_FOR_EVENT_REQ);

		storedResponse = response;
		return response;


	}

	/**
	 * Intent: Request information about an unspecified range of events
	 * Provides the names of the three events following the last read event
     */
	private SpeechletResponse handleGenericUpcomingIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response;
		if (lastRead >= events[0].size()){
			response = newTellResponse("I'm sorry, there are no more events listed.", false);
			lastRead++;
			session.removeAttribute(SESSION_EVENT_STATE);
			return response;
		}
		else if (lastRead >= events[0].size()-1){
			response = newAskResponse("The next event is: " + events[lastRead].getTitle(), false, "You can also ask about a specific event.", false);
			session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_EVENTS);
			storedResponse = response;
			lastRead++;
			return response;
		}
		else if (lastRead >= events[0].size()-2){
			response = newAskResponse("The next two events are: " + events[lastRead].getTitle() + " and " + events[++lastRead].getTitle(), false,"You can also ask about a specific event.",false);
			lastRead++;
			session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_EVENTS);
			storedResponse = response;
			return response;
		}
		String randomResp;
		if (lastRead > 0){
			int rand = ThreadLocalRandom.current().nextInt(0, eventResponses.length);
<<<<<<< HEAD
			randomResp = eventResponses[rand]; 
			
=======
			randomResp = eventResponses[rand];

>>>>>>> dc771def0c33a26e6ef8bcc6b23f48b0cd346820
		}
		else{
			randomResp = "The next three events are: ";
		}
<<<<<<< HEAD
		response = newAskResponse(randomResp + events[lastRead].getTitle() + 
=======
		response = newAskResponse(randomResp + events[lastRead].getTitle() +
>>>>>>> dc771def0c33a26e6ef8bcc6b23f48b0cd346820
				": " + events[lastRead += 1].getTitle() + ": and " + events[lastRead += 1].getTitle() + "; You can ask for information about a specific event, or for more upcoming events",false, "Try asking for more events.",false);
		session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_EVENTS);
		lastRead++;

		storedResponse = response;
		return response;

	}

	/**
	 * Intent: Request for information regarding events happening on a certain date
	 * Responds with a list of events occurring on the specified date as well as sending a card with the list
	 * to the alexa app. If there are no events on that day it informs the user of that.
     */
	private SpeechletResponse handleDateSpecifiedIntent(IntentRequest intentReq, Session session) {
		Intent intent = intentReq.getIntent();
		Map<String, Slot> slots = intent.getSlots();
		Slot dateSlot = slots.get("dateSpecified");
		String date = dateSlot.getValue();
		SpeechletResponse response = null;
		boolean eventOnDate = false; 
		int eventDateNum = 0;
		//String scrubbedAlexaDate = date.replace("-", "");
		//int alexaDate = Integer.parseInt(scrubbedAlexaDate);
		//int prev = 0;
		//int next = 0;
		
		//Updated so it lists all the events on a date if there is more than 1
		String events_str = "";
		int num_events = 0;
		for(int i = 0; i < events.length; i++){
				if((events[i].getDate()).contains(date) ){
					eventOnDate = true;
					//eventDateNum = i;
					events_str += events[i].getTitle() + ", ";
				}
				//I'd like to do something like this, but the events[i].date would need to be in an array.
				/*String scrubbedEventDate = events[i].getDate().replace("-", "");
				int lastRead = eventDateNum; 
				int eventsDate = Integer.parseInt(scrubbedEventDate);
			
				if(eventsDate > alexaDate){
						prev = i - 1; 
						next = i;
						break;
					}
				*/

		}
		//Get rid of the last comma

		
		int temp = eventDateNum + 1;
		if(eventOnDate){
			StringBuilder b = new StringBuilder(events_str);
			b.replace(events_str.lastIndexOf(","), events_str.lastIndexOf(",") + 1, "");
			events_str = b.toString();

			//Create a card to send events on that date to their phone
			SimpleCard card = new SimpleCard();
			card.setTitle("Events on " + date);
			card.setContent(events_str);

			response = newAskResponse("There is an event on that date! " + events_str, false, "You can ask to reserve tickets for this event, or for more details", false);
			response.setCard(card);
		}
		else{
			response = newAskResponse("Sorry, there are no events happening on that date.", false, "", false);
		}

		session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_EVENTS);

		storedResponse = response;
		return response;

	}

	/**
	 * Intent: Request for information on a specific event
	 * Responds to the user with the description, cost, and time of the event
     */
	private SpeechletResponse handleSpecificEventDetailsIntent(IntentRequest intentReq, Session session) {
		Intent intent = intentReq.getIntent();
		Map<String, Slot> slots = intent.getSlots();
		Slot eventNameSlot = slots.get("specificEvent");
		String event = eventNameSlot.getValue();
		SpeechletResponse response = null;

		boolean eventMatch = false; 
		int eventMatchNum = 0;
		for(int i = 0; i < events[0].size(); i++){
				if(((events[i].getTitle()).toLowerCase()).contains(event.toLowerCase()) ){
					eventMatch = true;
					eventMatchNum = i;
					lastRead = eventMatchNum; 
					break; 
				}
		}
		//Card to give overview of event
		SimpleCard card = new SimpleCard();
		card.setTitle(events[eventMatchNum].getTitle());
		String content = "Date(s): " + events[eventMatchNum].getTime();
		content += "\nCost: " + events[eventMatchNum].getPrice();
		card.setContent(content);
		
		if (eventMatch) {
			response = newAskResponse(events[eventMatchNum].getDesc(), false, "You can ask for the time or date of this event, " +
					"ask to buy tickets, or ask for more events.", false);
			session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_DETAILS);
		}
		else {
			response = newAskResponse("I'm sorry, could you please repeat that.", false, "I'm sorry, could you please repeat that.", false);
		}
		lastReadCard = card;

		storedResponse = response;
		return response;


	}

	/**
	 * Intent: reserve tickets for a specified event
	 * Sends an info card with puchasing detail the users alexa app
     */
	private SpeechletResponse handleSpecificEventPurchaseIntent(IntentRequest intentReq, Session session) {
		Intent intent = intentReq.getIntent();
		Map<String, Slot> slots = intent.getSlots();
		Slot eventNameSlot = slots.get("specificEvent");
		String event = eventNameSlot.getValue();
		SpeechletResponse response = null;
	
		boolean eventMatch = false;
		int eventMatchNum = 0;
		for(int i = 0; i < events[0].size(); i++){
			if(((events[i].getTitle()).toLowerCase()).contains(event.toLowerCase()) ){
				eventMatch = true;
				eventMatchNum = i;
				int lastRead = eventMatchNum;
				break;
			}
		}
		if (eventMatch) {
			response = newTellResponse(events[eventMatchNum].getTitle() + " costs: " + events[eventMatchNum].getPrice() + "; I have sent a card to your alexa app with a information for purchase.", false);

			SimpleCard card = new SimpleCard();
			card.setTitle(events[eventMatchNum].getTitle());
			String content = "Date(s): " + events[eventMatchNum].getTime();
			content += "\nCost: " + events[eventMatchNum].getPrice() + "\n\n" + purchaseURL;
			card.setContent(content);
			response.setCard(card);

			session.removeAttribute(SESSION_EVENT_STATE);
		}
		else {
			response = newAskResponse("I'm sorry, could you please repeat that.", false, "I'm sorry, could you please repeat that.", false);
		}

		storedResponse = response;
		return response;

	}


	/**
	 * Intent: recieve purchasing info on the last event read off
	 * Sends a card with information regarding the event whose detail were last read
     */
	private SpeechletResponse handleLastEventPurchaseIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = null;
		if (session.getAttribute(SESSION_EVENT_STATE) != null
				&& STATE_GIVEN_DETAILS.compareTo((Integer)session.getAttribute(SESSION_EVENT_STATE)) == 0) {
			response = newTellResponse(events[lastRead].getTitle() + " costs: " + events[lastRead].getPrice() + "; " + events[lastRead].getSite() + "; I have sent a card to your alexa app with a information regarding purchase.", false);
			session.removeAttribute(SESSION_EVENT_STATE);
		}
		else {
			response = newAskResponse("I haven't listed off any events details", false, "Try asking about a specific event first", false);
		}
		response.setCard(lastReadCard);

		storedResponse = response;
		return response;
	}



	/**
	 * Intent: what day is the event occurring?
	 * responds with the date of the event
	 */
	private SpeechletResponse handleWhatDateIntent(IntentRequest intentReq, Session session) {
		Intent intent = intentReq.getIntent();
		Map<String, Slot> slots = intent.getSlots();
		Slot eventNameSlot = slots.get("specificEvent");
		String event = eventNameSlot.getValue();
		SpeechletResponse response = null;

		// This function is going to have to use the slot input and search for it in our database
		// but for now its hardcoded		
		boolean eventMatch = false; 
		int eventMatchNum = 0;
		for(int i = 0; i < events[0].size(); i++){
				if(((events[i].getTitle()).toLowerCase()).contains(event.toLowerCase()) ){
					eventMatch = true;
					eventMatchNum = i;
					lastRead = eventMatchNum; 
					break; 
				}
		}
		//Create card to send date & time
		SimpleCard card = new SimpleCard();
		card.setTitle(events[eventMatchNum].getTitle());
		card.setContent("The event is on: "+ events[eventMatchNum].getTime());
		
		if (eventMatch) {
			response = newAskResponse(events[eventMatchNum].getTitle() + " is on: " + events[eventMatchNum].getTime(), false, "You can ask to buy tickets if you would like.", false);
			session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_DETAILS);
		}
		else {
			response = newAskResponse("Sorry, I didn't get that.", false, "Please rephrase your question.", false);
		}
		response.setCard(card);

		storedResponse = response;
		return response;


	}

	/**
	 * Intent: when is the event occurring?
	 * responds with the time of the event
	 */
	private SpeechletResponse handleWhatTimeIntent(IntentRequest intentReq, Session session) {
		Intent intent = intentReq.getIntent();
		Map<String, Slot> slots = intent.getSlots();
		Slot eventNameSlot = slots.get("specificEvent");
		String event = eventNameSlot.getValue();
		SpeechletResponse response = null;

		// This function is going to have to use the slot input and search for it in our database
		// but for now its hardcoded		
		boolean eventMatch = false; 
		int eventMatchNum = 0;
		for(int i = 0; i < events[0].size(); i++){
				if(((events[i].getTitle()).toLowerCase()).contains(event.toLowerCase()) ){
					eventMatch = true;
					eventMatchNum = i;
					lastRead = eventMatchNum; 
					break; 
				}
		}
		//Create card to send date & time
		SimpleCard card = new SimpleCard();
		card.setTitle(events[eventMatchNum].getTitle());
		card.setContent("The event is on: "+ events[eventMatchNum].getTime());
		
		if(eventMatch){
			response = newAskResponse(events[eventMatchNum].getTitle() + " is on: " + events[eventMatchNum].getTime(), false, "You can ask to buy tickets if you would like.", false);
			session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_DETAILS);
		}else{
			response = newAskResponse("Sorry, I didn't get that.", false, "Please rephrase your question", false);
		}
		response.setCard(card);

		storedResponse = response;
		return response;


	}

	/**
	 * Intent: event cost
	 * provides the user with the lowest price for tickets to the event
     */
	private SpeechletResponse handleHowMuchIntent(IntentRequest intentReq, Session session) {
		Intent intent = intentReq.getIntent();
		Map<String, Slot> slots = intent.getSlots();
		Slot eventNameSlot = slots.get("specificEvent");
		String event = eventNameSlot.getValue();
		SpeechletResponse response = null;

		// This function is going to have to use the slot input and search for it in our database
		// but for now its hardcoded		
		boolean eventMatch = false; 
		int eventMatchNum = 0;
		for(int i = 0; i < events[0].size(); i++){
				if(((events[i].getTitle()).toLowerCase()).contains(event.toLowerCase()) ){
					eventMatch = true;
					eventMatchNum = i;
					lastRead = eventMatchNum; 
					break; 
				}
		}
		if (eventMatch) {
			response = newAskResponse(events[eventMatchNum].getTitle() + " costs: " + events[eventMatchNum].getPrice(), false, "You can ask to buy tickets if you would like.", false);
			session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_DETAILS);
		}
		else {
			response = newAskResponse("Sorry, I didn't get that.", false, "Please rephrase your question.", false);
		}

		storedResponse = response;
		return response;


	}

	/**
	 * Intent: Time of the last event read
	 * Provides the time
     */
	private SpeechletResponse handleKnownEventWhatTimeIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = null;
		if (session.getAttribute(SESSION_EVENT_STATE) != null
				&& STATE_GIVEN_DETAILS.compareTo((Integer)session.getAttribute(SESSION_EVENT_STATE)) == 0) {
			response = newAskResponse(events[lastRead].getTitle() + " is on: " + events[lastRead].getTime(), false, "What else would you like to know?", false);
		}
		else {
			response = newAskResponse("I haven't listed off any events details", false, "Try asking about a specific event first", false);
		}
		storedResponse = response;
		return response;
	}

	/**
	 * Intent: cost of the last event read
	 * Provides the cost
     */
	private SpeechletResponse handleKnownEventHowMuchIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = null;
		if (session.getAttribute(SESSION_EVENT_STATE) != null
				&& STATE_GIVEN_DETAILS.compareTo((Integer)session.getAttribute(SESSION_EVENT_STATE)) == 0) {
			response = newAskResponse(events[lastRead].getTitle() + " costs: " + events[lastRead].getPrice() + " and up.", false, "What else would you like to know?", false);
		}
		else {
			response = newAskResponse("I haven't listed off any events details", false, "Try asking about a specific event first", false);
		}

		storedResponse = response;
		return response;
	}

	/**
	 * Intent: Date of the last event read
	 * provides the date
     */
	private SpeechletResponse handleKnownEventWhatDateIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = null;
		if (session.getAttribute(SESSION_EVENT_STATE) != null
				&& STATE_GIVEN_DETAILS.compareTo((Integer)session.getAttribute(SESSION_EVENT_STATE)) == 0) {
			response = newAskResponse(events[lastRead].getTitle() + " is on: " + events[lastRead].getTime(), false, "What else would you like to know?", false);
		}
		else {
			response = newAskResponse("I haven't listed off any events details", false, "Try asking about a specific event first", false);
		}

		storedResponse = response;
		return response;
	}

}


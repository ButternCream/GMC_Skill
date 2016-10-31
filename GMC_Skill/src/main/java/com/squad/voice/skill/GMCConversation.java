package com.squad.voice.skill;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.squad.voice.model.base.Conversation;
import java.util.Map;
import com.squad.voice.skill.Event;

/*
 * 
 * FIX THE NewAskResponse parameters.
 * 
 */


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

	private final static String INTENT_YES = "AMAZON.YesIntent";
	private final static String INTENT_NO = "AMAZON.NoIntent";
	private final static String INTENT_HELP = "AMAZON.HelpIntent";
	private final static String INTENT_STOP = "AMAZON.StopIntent";
	private final static String INTENT_CANCEL = "AMAZON.CancelIntent";
	private final static String INTENT_END_CONVERSATION = "EndConvoIntent";


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
	// Im still not sure what this does, I think it prevents the session from terminating
	private final static String SESSION_EVENT_STATE = "eventState";

	//Parse the RSS feed into the array of events
	public Event[] events = new Event().parseRSSFeed();

	public int lastRead = 0;
	
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
		supportedIntentNames.add(INTENT_END_CONVERSATION);


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
		supportedIntentNames.add(INTENT_CANCEL);

	}

	// TODO: implement handler functions to create a functioning state machine for our conversational model
	// functions that handle requests go below this line

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
		else if (INTENT_END_CONVERSATION.equals( intentName )){
			response = handleEndConvoIntent(intentReq, session);
}
		else {
			response = newTellResponse("Sorry, I didn't get that.", false);
		}

		return response;
	}

	private SpeechletResponse handleEndConvoIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = newTellResponse("Alright, let me know if you need anything else.", false);
		session.setAttribute(SESSION_EVENT_STATE, STATE_WAITING_FOR_EVENT_REQ);

		return response;


	}

	//Pre: Takes in generic call to GMC skill
	//Post: Prompts user to ask about upcoming events
	private SpeechletResponse handleGMCIntentStart(IntentRequest intentReq, Session session) {
		SpeechletResponse response = newAskResponse("Hello, I am GMC event helper, I can provide information about events " +
				"happening at the Green music center, try asking me about upcoming events", false, "You can also" +
				" ask for events occurring on a specific date", false);
		session.setAttribute(SESSION_EVENT_STATE, STATE_WAITING_FOR_EVENT_REQ);

		return response;


	}

	//Pre: Takes a generic request for upcoming events
	//Post: Lists three most recent events, prompts user to ask about a specific event or ask for more events
	private SpeechletResponse handleGenericUpcomingIntent(IntentRequest intentReq, Session session) {
		
		SpeechletResponse response = newAskResponse("The next three events are: " + events[lastRead].getTitle() + 
				": " + events[lastRead += 1].getTitle() + ": and " + events[lastRead += 1].getTitle() + "; You can ask about a specific event, or ask to hear about more events. " ,false, "Try asking for more events.",false);
		lastRead++;
		session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_EVENTS);
		return response;

	}

	//Pre: 	Takes a request for events on or around a specified date.
	//		**I think this declaration needs to change to accept Amazon.date slot**
	//Post: Lists events in specified range, or explains there are no events in range and presents user
	//	    with three events closest to desired date.
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
		for(int i = 0; i < events[0].size(); i++){
				if((events[i].getDate()).contains(date) ){
					eventOnDate = true;
					eventDateNum = i;
					break; 
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
		int temp = eventDateNum + 1;
		if(eventOnDate){
			response = newAskResponse("There is an event on that date! " + events[eventDateNum].getTitle(), false, "You can ask to reseve tickets for this event, or for more details", false);
		}
		else{
			response = newAskResponse("Sorry, there are no events on that date.", false, "", false);
		}

		session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_EVENTS);

		return response;

	}

	//Pre: Takes a request for details about a specified Event
	//		**This needs to be changed to accept a specific event as a slot, that slot needs to be defined above**
	//Post: Reads a one or two sentace description of the event specified, prompts user to purchase tickets.
	private SpeechletResponse handleSpecificEventDetailsIntent(IntentRequest intentReq, Session session) {
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
		if(eventMatch){
			response = newAskResponse(events[eventMatchNum].getDesc(), false, "You can ask for the time or date of this event, ask to buy tickets, or ask for more events.", false);
			session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_DETAILS);
		}else{
			response = newAskResponse("Sorry, I didn't get that.", false, "", false);
		}
		return response;


	}

	//Pre: Takes a request to purchase tickets for specific event
	//		**This needs to be changed to accept a specific event as a slot, that slot needs to be defined above**
	//Post: if(personal){Asks user for confirmation they'd like to purchase ticket, bills their amazon account for the
	//		ticket and sends the ticket to user's amazon email}**How do we tie this to amazon acct/email?**
	// 		else{prompt user for their email and sends link to purchase tickets to aforementioned email.
	//		**We may want to fork this call to give options for ticketing levels, ask about #of tickets to purchase**
		private SpeechletResponse handleSpecificEventPurchaseIntent(IntentRequest intentReq, Session session) {
			Intent intent = intentReq.getIntent();
			Map<String, Slot> slots = intent.getSlots();
			Slot eventNameSlot = slots.get("specificEvent");
			String event = eventNameSlot.getValue();
			SpeechletResponse response = null;
	
			// This function is going to have to use the slot input and search for it in our database
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
			if(eventMatch){
				response = newTellResponse(events[eventMatchNum].getTitle() + " costs: " + events[eventMatchNum].getPrice() + "; I have sent a card to your alexa app with a link to purchase.", false);
				session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_DETAILS);
			}else{
				response = newAskResponse("Sorry, I didn't get that.", false, "", false);
			}
			return response;

	}
	

	private SpeechletResponse handleLastEventPurchaseIntent(IntentRequest intentReq, Session session) {
			SpeechletResponse response = null;
			response = newTellResponse(events[lastRead].getTitle() + " costs: " + events[lastRead].getPrice() + "; " + events[lastRead].getSite() + "; I have sent a card to your alexa app with a link to purchase.",false);
			session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_DETAILS);
			return response;
	}




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
		if(eventMatch){
			response = newAskResponse(events[eventMatchNum].getTitle() + " is on: " + events[eventMatchNum].getTime(), false, "You can ask to buy tickets if you would like.", false);
			session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_DETAILS);
		}else{
			response = newAskResponse("Sorry, I didn't get that.", false, "Please rephrase your question.", false);
		}
		return response;


	}

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
		if(eventMatch){
			response = newAskResponse(events[eventMatchNum].getTitle() + " is on: " + events[eventMatchNum].getTime(), false, "You can ask to buy tickets if you would like.", false);
			session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_DETAILS);
		}else{
			response = newAskResponse("Sorry, I didn't get that.", false, "Please rephrase your question", false);
		}
		return response;


	}

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
		if(eventMatch){
			response = newAskResponse(events[eventMatchNum].getTitle() + " costs: " + events[eventMatchNum].getPrice() + " and up.", false, "You can ask to buy tickets if you would like.", false);
			session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_DETAILS);
		}else{
			response = newAskResponse("Sorry, I didn't get that.", false, "Please rephrase your question.", false);
		}
		return response;


	}
	private SpeechletResponse handleKnownEventWhatTimeIntent(IntentRequest intentReq, Session session) {
			SpeechletResponse response = null;
			response = newAskResponse(events[lastRead].getTitle() + " is on: " + events[lastRead].getTime(),false, "What else would you like to know?", false);
			return response;
	}
	private SpeechletResponse handleKnownEventHowMuchIntent(IntentRequest intentReq, Session session) {
			SpeechletResponse response = null;
			response = newAskResponse(events[lastRead].getTitle() + " costs: " + events[lastRead].getPrice() + " and up.",false, "What else would you like to know?", false);
			return response;
	}
	private SpeechletResponse handleKnownEventWhatDateIntent(IntentRequest intentReq, Session session) {
			SpeechletResponse response = null;
			response = newAskResponse(events[lastRead].getTitle() + " is on: " + events[lastRead].getTime(), false, "What else would you like to know?", false);
			return response;
	}


}


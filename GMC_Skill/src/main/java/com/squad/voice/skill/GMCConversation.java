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
	private final static String INTENT_YES = "AMAZON.YesIntent";
	private final static String INTENT_NO = "AMAZON.NoIntent";
	private final static String INTENT_HELP = "AMAZON.HelpIntent";
	private final static String INTENT_STOP = "AMAZON.StopIntent";
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
		else {
			response = newTellResponse("Sorry, I didn't get that.", false);
		}

		return response;
	}

	//Pre: Takes in generic call to GMC skill
	//Post: Prompts user to ask about upcoming events
	private SpeechletResponse handleGMCIntentStart(IntentRequest intentReq, Session session) {
		SpeechletResponse response = newAskResponse("Hello, I am GMC event helper, I can provide information about events" +
				"happening at the Green music center, try asking me about upcoming events", false, "You can also specify" +
				" ask for a list of events occurring on a specific date", false);
		session.setAttribute(SESSION_EVENT_STATE, STATE_WAITING_FOR_EVENT_REQ);

		return response;


	}

	//Pre: Takes a generic request for upcoming events
	//Post: Lists three most recent events, prompts user to ask about a specific event or ask for more events
	private SpeechletResponse handleGenericUpcomingIntent(IntentRequest intentReq, Session session) {
		
		SpeechletResponse response = newAskResponse("<speak>" + "The next three events are: Event " + ++lastRead + " " + events[lastRead-1].getTitle() + 
				", Event " + ++lastRead + " " + events[lastRead-1].getTitle() + ", Event " + ++lastRead + " " + events[lastRead-1].getTitle()+ "</speak>",true,"Testing",true);
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
		for(int i = 0; i < 30; i++){
				if(events[i].getDate() == date){
					eventOnDate = true;
					eventDateNum = i;
					int lastRead = eventDateNum; 
					break; 
				}
		}
		int temp = eventDateNum + 1;
		if(eventOnDate)
			response = newAskResponse("There is an event on that date! Event " + temp + ", " + events[eventDateNum].getTitle(), false, "", false);
		else
			response = newAskResponse("Sorry, there are no events on that date.", false, "test", false);

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

		switch (event){
			case "1":
				response = newAskResponse(events[0].getDesc(), false, "", false);
				break;
			case "2":
				response = newAskResponse(events[1].getDesc(), false, "", false);
				break;
			case "3":
				response = newAskResponse(events[2].getDesc(), false, "", false);
				break;
			case "4":
				response = newAskResponse(events[3].getDesc(), false, "", false);
				break;
			case "5":
				response = newAskResponse(events[4].getDesc(), false, "", false);
				break;
			case "6":
				response = newAskResponse(events[5].getDesc(), false, "", false);
				break;
			case "7":
				response = newAskResponse(events[6].getDesc(), false, "", false);
				break;
			case "8":
				response = newAskResponse(events[7].getDesc(), false, "", false);
				break;
			case "9":
				response = newAskResponse(events[8].getDesc(), false, "", false);
				break;
			case "10":
				response = newAskResponse(events[9].getDesc(), false, "", false);
				break;
			case "11":
				response = newAskResponse(events[10].getDesc(), false, "", false);
				break;
			case "12":
				response = newAskResponse(events[11].getDesc(), false, "", false);
				break;
			case "13":
				response = newAskResponse(events[12].getDesc(), false, "", false);
				break;
			case "14":
				response = newAskResponse(events[13].getDesc(), false, "", false);
				break;
			case "15":
				response = newAskResponse(events[14].getDesc(), false, "", false);
				break;
			case "16":
				response = newAskResponse(events[15].getDesc(), false, "", false);
				break;
			case "17":
				response = newAskResponse(events[16].getDesc(), false, "", false);
				break;
			case "18":
				response = newAskResponse(events[17].getDesc(), false, "", false);
				break;
			case "19":
				response = newAskResponse(events[18].getDesc(), false, "", false);
				break;
			case "20":
				response = newAskResponse(events[19].getDesc(), false, "", false);
				break;
			case "21":
				response = newAskResponse(events[20].getDesc(), false, "", false);
				break;
			case "22":
				response = newAskResponse(events[21].getDesc(), false, "", false);
				break;
			case "23":
				response = newAskResponse(events[22].getDesc(), false, "", false);
				break;
			case "24":
				response = newAskResponse(events[23].getDesc(), false, "", false);
				break;
			case "25":
				response = newAskResponse(events[24].getDesc(), false, "", false);
				break;
			case "26":
				response = newAskResponse(events[25].getDesc(), false, "", false);
				break;
			case "27":
				response = newAskResponse(events[26].getDesc(), false, "", false);
				break;
			case "28":
				response = newAskResponse(events[27].getDesc(), false, "", false);
				break;
			case "29":
				response = newAskResponse(events[28].getDesc(), false, "", false);
				break;
			case "30":
				response = newAskResponse(events[29].getDesc(), false, "", false);
				break;
			default:
				response = newAskResponse("I didn't get that", false, "please try again", false);
				break;
		}

		session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_DETAILS);
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
	
			switch (event){
			case "1":
				response = newAskResponse("The price is: " + events[0].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "2":
				response = newAskResponse("The price is: " + events[1].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "3":
				response = newAskResponse("The price is: " + events[2].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "4":
				response = newAskResponse("The price is: " + events[3].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "5":
				response = newAskResponse("The price is: " + events[4].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "6":
				response = newAskResponse("The price is: " + events[5].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "7":
				response = newAskResponse("The price is: " + events[6].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "8":
				response = newAskResponse("The price is: " + events[7].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "9":
				response = newAskResponse("The price is: " + events[8].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "10":
				response = newAskResponse("The price is: " + events[9].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "11":
				response = newAskResponse("The price is: " + events[10].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "12":
				response = newAskResponse("The price is: " + events[11].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "13":
				response = newAskResponse("The price is: " + events[12].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "14":
				response = newAskResponse("The price is: " + events[13].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "15":
				response = newAskResponse("The price is: " + events[14].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "16":
				response = newAskResponse("The price is: " + events[15].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "17":
				response = newAskResponse("The price is: " + events[16].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "18":
				response = newAskResponse("The price is: " + events[17].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "19":
				response = newAskResponse("The price is: " + events[18].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "20":
				response = newAskResponse("The price is: " + events[19].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "21":
				response = newAskResponse("The price is: " + events[20].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "22":
				response = newAskResponse("The price is: " + events[21].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "23":
				response = newAskResponse("The price is: " + events[22].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "24":
				response = newAskResponse("The price is: " + events[23].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "25":
				response = newAskResponse("The price is: " + events[24].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "26":
				response = newAskResponse("The price is: " + events[25].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "27":
				response = newAskResponse("The price is: " + events[26].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "28":
				response = newAskResponse("The price is: " + events[27].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "29":
				response = newAskResponse("The price is: " + events[28].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			case "30":
				response = newAskResponse("The price is: " + events[29].getPrice() + "I've sent a card with a link to puchase to your Alexa app.", false, "", false);
				break;
			default:
				response = newAskResponse("I didn't get that", false, "please try again", false);
				break;
		}
		session.setAttribute(SESSION_EVENT_STATE, STATE_MADE_RESERV);
		return response;
	}
}
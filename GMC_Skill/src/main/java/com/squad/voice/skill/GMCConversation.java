package com.squad.voice.skill;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.squad.voice.model.base.Conversation;

import java.util.Map;


public class GMCConversation extends Conversation {
	//Intent names
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


	//Slots
	private final static String EVENT_NAME = "specificEvent";
	private final static String DATE = "dateSpecified";

	//State
	//These can be used, I believe after prompting users for a response to avoid kicking back to default. 
	private final static Integer STATE_WAITING_DETAILS = 100000;
	private final static Integer STATE_WAITING_MORE_EVENTS = 100001;
	private final static Integer STATE_WAITING_FOR_EVENT_REQ = 100002;

	//Session state storage key
	private final static String SESSION_EVENT_STATE = "eventState";


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
		else if (INTENT_MORE_EVENTS.equals(intentName)) {
			response = handleMoreEventsIntent(intentReq, session);
		}
		else if (INTENT_SPECIFIC_EVENT_DETAILS.equals(intentName)) {
			response = handleSpecificEventDetailsIntent(intentReq, session);
		}
		else if (INTENT_SPECIFIC_EVENT_PURCHASE.equals(intentName)) {
			response = handleSpecificEventPurchaseIntent(intentReq, session);
		}
		else {
			response = newTellResponse("Sorry, I didn't get that. I can list upcoming events or tell you about specific events.", false);
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
		SpeechletResponse response = newAskResponse("The next three events are a Performance: The Magic of the Flute, on" +
				" October 8th, 9th and 10th; A play: Waiting for the Parade, on October 13th and 14th; and a Performance: " +
				"Philharmonia Baroque Orchestra, on October 15th", false, "You can ask about a specific event or events for other dates.", false);
		session.setAttribute(SESSION_EVENT_STATE, STATE_WAITING_MORE_EVENTS);
		return response;

	}

	//Pre: 	Takes a request for events on or around a specified date.
	//		**I think this declaration needs to change to accept Amazon.date slot**
	//Post: Lists events in specified range, or explains there are no events in range and presents user
	//	    with three events closest to desired date.
	private SpeechletResponse handleDateSpecifiedIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = newAskResponse("This isn't implemented yet.", false, "You can ask about a specific event or events for other dates.", false);
		session.setAttribute(SESSION_EVENT_STATE, STATE_WAITING_MORE_EVENTS);

		return response;

	}

	//Pre: Takes a request for details about a specified Event
	//		**This needs to be changed to accept a specific event as a slot, that slot needs to be defined above**
	//Post: Reads a one or two sentace description of the event specified, prompts user to purchase tickets.
	private SpeechletResponse handleSpecificEventDetailsIntent(IntentRequest intentReq, Session session) {
		Intent intent = intentReq.getIntent();
		Map<String, Slot> slots = intent.getSlots();
		Slot eventNameSlot = slots.get("SpecificEvent");
		String event = eventNameSlot.getValue();

		// This function is going to have to use the slot input and search for it in our database

		SpeechletResponse response = newAskResponse(event + "is playing", false, "You can ask about a specific event or events for other dates.", false);

		session.setAttribute(SESSION_EVENT_STATE, STATE_WAITING_MORE_EVENTS);
		return response;

	}

	private SpeechletResponse handleMoreEventsIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = newAskResponse("This isn't implemented yet.", false, "You can ask about a specific event or events for other dates.", false);
		session.setAttribute(SESSION_EVENT_STATE, STATE_WAITING_MORE_EVENTS);
		return response;

	}

	//Pre: Takes a request to purchase tickets for specific event
	//		**This needs to be changed to accept a specific event as a slot, that slot needs to be defined above**
	//Post: if(personal){Asks user for confirmation they'd like to purchase ticket, bills their amazon account for the
	//		ticket and sends the ticket to user's amazon email}**How do we tie this to amazon acct/email?**
	// 		else{prompt user for their email and sends link to purchase tickets to aforementioned email.
	//		**We may want to fork this call to give options for ticketing levels, ask about #of tickets to purchase**
	private SpeechletResponse handleSpecificEventPurchaseIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = newAskResponse("This isn't implemented yet.", false, "You can ask about a specific event or events for other dates.", false);
		session.setAttribute(SESSION_EVENT_STATE, STATE_WAITING_MORE_EVENTS);
		return response;

	}
}
	
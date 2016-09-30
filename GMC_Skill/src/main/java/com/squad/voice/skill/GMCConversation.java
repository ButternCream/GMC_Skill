package com.squad.voice.skill;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.squad.voice.model.base.Conversation;


public class GMCConversation extends Conversation {
	//Intent names
	private final static String INTENT_START = "StartGMCIntent";
	private final static String INTENT_UPCOMING_GMC = "GenericUpcomingEventsIntent";
	private final static String INTENT_DATE_GMC = "DateSpecifiedEventsIntent";
	private final static String INTENT_SPECIFIC_EVENT_DETAILS = "SpecificEventDetailsIntent";
	private final static String INTENT_SPECIFIC_EVENT_PURCHASE = "SpecificEventPurchaseIntent";
	private final static String INTENT_YES = "AMAZON.YesIntent";
	private final static String INTENT_NO = "AMAZON.NoIntent";


	//Slots
	private final static String EVENT_NAME = "specificEvent";

	//State
	//These can be used, I believe after prompting users for a response to avoid kicking back to default. 
	private final static Integer STATE_WAITING_DETAILS = 100000;
	private final static Integer STATE_WAITING_MORE_EVENTS = 100001;

	//Session state storage key
	//**What is this for? Never used again in Jeff's code.**
	private final static String SESSION_EVENT_STATE = "eventState";

	public GMCConversation() {
		super();

		//Add custom intent names for dispatcher use
		//**I don't have a list of our functions made @ last meeting, is anything missing?**
		supportedIntentNames.add(INTENT_START);
		supportedIntentNames.add(INTENT_UPCOMING_GMC);
		supportedIntentNames.add(INTENT_DATE_GMC);
		supportedIntentNames.add(INTENT_SPECIFIC_EVENT_DETAILS);
		supportedIntentNames.add(INTENT_SPECIFIC_EVENT_PURCHASE);
		supportedIntentNames.add(INTENT_YES);
		supportedIntentNames.add(INTENT_NO);


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
		} else if (INTENT_UPCOMING_GMC.equals(intentName)) {
			response = handleGenericUpcomingIntent(intentReq, session);
		} else if (INTENT_DATE_GMC.equals(intentName)) {
			response = handleDateSpecifiedIntent(intentReq, session);
		} else if (INTENT_SPECIFIC_EVENT_DETAILS.equals(intentName)) {
			response = handleSpecificEventDetailsIntent(intent, session);
		} else if (INTENT_SPECIFIC_EVENT_PURCHASE.equals(intentName)) {
			response = handleSpecificEventPurchaseIntent(intentReq, session);
		} else {
			response = newTellResponse("Sorry, I didn't get that. I can list upcoming events or tell you about specific events.", false);
		}

		return response;
	}

	//Pre: Takes in generic call to GMC skill
	//Post: Prompts user to ask about upcoming events
	private SpeechletResponse handleGMCIntentStart(IntentRequest intentReq, Session session) {
		SpeechletResponse response = newAskResponse("Hello, I am GMC event helper, would you like to hear about upcoming events?", false, "You can also specify a date to hear events around", false);
		session.setAttribute(SESSION_EVENT_STATE, STATE_WAITING_MORE_EVENTS);

		/*Intent intent = intentReq.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;
		if (INTENT_YES.equals(intentName)) {
			response = handleGenericUpcomingIntent(intentReq, session);
		} else if (INTENT_UPCOMING_GMC.equals(intentName)) {
			response = handleGenericUpcomingIntent(intentReq, session);
		} else if (INTENT_DATE_GMC.equals(intentName)) {
			response = handleDateSpecifiedIntent(intentReq, session);
		} else if (INTENT_NO.equals(intentName)) {
			response = newAskResponse("I won't bother you again, bitch");
		} else {
			response = newAskResponse("I didn't get that, please try again");
			//I'm not positive this call will work the way I hope it to, this may be the default
			response = respondToIntentRequest(intentReq, session);
		}*/
		//Idk what the fuck this does
		//session.setAttribute(SESSION_KNOCK_STATE, STATE_WAITING_WHO_DER);
		return response;


	}

	//Pre: Takes a generic request for upcoming events
	//Post: Lists three most recent events, prompts user to ask about a specific event or ask for more events
	private SpeechletResponse handleGenericUpcomingIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = newAskResponse("The next three events are a Performance: The Magic of the Flute, on October 8th, 9th and 10th; A play: Waiting for the Parade, on October 13th and 14th; and a Performance: Philharmonia Baroque Orchestra, on October 15th", false, "You can ask about a specific event or events for other dates.", false);
		session.setAttribute(SESSION_EVENT_STATE, STATE_WAITING_MORE_EVENTS);
		/*Intent intent = intentReq.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;
		if (INTENT_DATE_GMC.equals(intentName)) {
			response = handleDateSpecifiedIntent(intentReq, session);
		} else if (We'll need a new intent for asking about specific events here. ie: "Tell me more about [event slot]"false) {
			response = handleSpecificEventDetailsIntent(intentReq, session);
		} else {
			response = newAskResponse("I didn't catch that. If I can help you with anything else, let me know!");
			//I'm not positive this call will work the way I hope it to, this may be the default
			response = respondToIntentRequest(intentReq, session);

		}*/
		return response;

	}

	//Pre: 	Takes a request for events on or around a specified date.
	//		**I think this declaration needs to change to accept Amazon.date slot**
	//Post: Lists events in specified range, or explains there are no events in range and presents user
	//	    with three events closest to desired date.
	private SpeechletResponse handleDateSpecifiedIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = null;
		session.setAttribute(SESSION_EVENT_STATE, STATE_WAITING_MORE_EVENTS);

		return response;

	}

	//Pre: Takes a request for details about a specified Event
	//		**This needs to be changed to accept a specific event as a slot, that slot needs to be defined above**
	//Post: Reads a one or two sentace description of the event specified, prompts user to purchase tickets.
	private SpeechletResponse handleSpecificEventDetailsIntent(Intent intent, Session session) {
		SpeechletResponse response = newAskResponse("working on this implementation.", false, "You can ask about a specific event or events for other dates.", false);

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
	
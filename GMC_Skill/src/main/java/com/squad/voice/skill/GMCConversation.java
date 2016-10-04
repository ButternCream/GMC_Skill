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
		SpeechletResponse response = newAskResponse("The next three events are a comedy show: funny or die jokes for votes, on" +
				" October 6th; A performance: Philharmonia Baroque Orchestra, on October 15th; and a talk by " +
				"Adam Savage, on October 17th", false, "You can ask about a specific event or events for other dates.", false);
		session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_EVENTS);
		return response;

	}

	//Pre: 	Takes a request for events on or around a specified date.
	//		**I think this declaration needs to change to accept Amazon.date slot**
	//Post: Lists events in specified range, or explains there are no events in range and presents user
	//	    with three events closest to desired date.
	private SpeechletResponse handleDateSpecifiedIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = newAskResponse("The next three events for that range are a comedy show: funny or die jokes for votes, on" +
				" October 6th; A performance: Philharmonia Baroque Orchestra, on October 15th; and a talk by " +
				"Adam Savage, on October 17th", false, "You can ask about a specific event or events for other dates.", false);
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
			case "funny or die jokes for votes":
				response = newAskResponse("A free comedy show and voter registration opportunity combining the powers of Next" +
						" Gen and Funny or Die. This event starts at 7:00 and is free.", false, "Do you need me to repeat that?", false);
				break;
			case "philharmonia baroque orchestra":
				response = newAskResponse("The Philharmonia Baroque Orchestra, directed by Nicholas McGegan brings audiences " +
						"back in time, performing on the period instruments for which this music was originally written. Joining" +
						" them for this all-Beethoven program is Robert Levin on fortepiano. It starts at 7:30 and costs 35" +
						" dollars", false, "Do you need me to repeat that?", false);
				break;
			case "Adam savage":
				response = newAskResponse("Mythbusters' 160-and-counting episode hours have tackled over 750 myths and performed " +
						"nearly 2,500 experiments. Adam and Jamie travel the country to corporate events, museums, and colleges," +
						" for groups as small as 20 and as large as 20,000, telling tales of experiments, explosions and hijinks. " +
						"It starts at 7:30 and costs 35 dollars.", false, "Do you need me to repeat that?", false);
				break;
			case "Sonoma state university sustainability day":
				response = newAskResponse("Over the past three decades, Bill McKibben has shaped public perception—and public " +
						"action—on climate change, alternative energy, and the need for localized economies. An environmental" +
						" activist, bestselling author, and the planet's best green journalist, McKibben is the founder of " +
						"350.org, the massive grassroots climate change initiative. This event starts at 7:30 and costs 20" +
						" dollars.", false, "Do you need me to repeat that?", false);
				break;
			case "Itzhak Perlman and Rohan de Silva":
				response = newAskResponse("Undeniably the reigning virtuoso of the violin, Itzhak Perlman enjoys superstar" +
						" status rarely afforded a classical musician. Beloved for his charm and humanity as well as his " +
						"talent, he is treasured by audiences throughout the world who respond not only to his remarkable " +
						"artistry but also to his irrepressible joy for making music. It starts at 7:30 and costs 50 " +
						"dollars.", false, "Do you need me to repeat that?", false);
				break;
			case "Denis matsuev":
				response = newAskResponse("Winner of the prestigious Tchaikovsky Competition, Denis Matsuev is “a virtuoso " +
						"in the grandest of Russian pianistic tradition,” and one of the most highly-regarded pianists of his" +
						" generation. His captivating live performances showcase his unique ability to move seamlessly between" +
						" thundering ferocity and graceful nuance. This event costs 35 dollars and starts at 7:30", false, "Do" +
						" you need me to repeat that?", false);
				break;
			default:
				response = newAskResponse("I didn't get that", false, "please try again", false);
				break;
		}

		session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_DETAILS);
		return response;

	}

	private SpeechletResponse handleMoreEventsIntent(IntentRequest intentReq, Session session) {
		SpeechletResponse response = null;
		if(session.getAttribute(SESSION_EVENT_STATE) != null
				&& STATE_GIVEN_EVENTS.compareTo((Integer)session.getAttribute(SESSION_EVENT_STATE)) == 0) {
			response = newAskResponse("The next three events are a presentation: Sonoma State university sustainability" +
					" day, on October 18th; A performance: Itzhak Perlman and Rohan De Selva, on October 20th; and a Performance:" +
					" Denis Matsuev, on October 22th", false, "You can ask about a specific event or events for other dates.", false);
			session.setAttribute(SESSION_EVENT_STATE, STATE_GIVEN_EVENTS);
		}
		else {
			response = newTellResponse("You have to ask for a list of events before I can tell you more.", false);
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
		SpeechletResponse response = newAskResponse("This isn't implemented yet.", false, "You can ask about a specific event or events for other dates.", false);
		session.setAttribute(SESSION_EVENT_STATE, STATE_MADE_RESERV);
		return response;

	}
}

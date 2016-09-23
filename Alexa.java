//I have no idea what these do, or if we need them, I believe they allow us to use our own files for intents etc.
//rather than inputting them directly to amazon's Alexa site. 
/*
	
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.neong.voice.model.base.Conversation;
*/

public class KnockKnockConversation extends Conversation {
	//Intent names
	private final static String INTENT_START = "StartGMCIntent";
	private final static String INTENT_UPCOMING_GMC = "GenericUpcomingEventsIntent";
	private final static String INTENT_DATE_GMC = "DateSpecifiedEventsIntent";
	private final static String INTENT_SPECIFIC_EVENT_DETAILS = "SpecificEventDetailsIntent";
	private final static String INTENT_SPECIFIC_EVENT_PURCHASE = "SpecificEventPurchaseIntent";

	//Slots
	//We need slots for all the different events; I am not sure how to implement them. 
	//private final static String SLOT_RELATIVE_TIME = "timeOfDay";

	//State
	//These can be used, I believe after prompting users for a response to avoid kicking back to default. 
	private final static Integer STATE_WAITING_DETAILS = 100000;
	private final static Integer STATE_WAITING_MORE_EVENTS = 100001;

	//Session state storage key
	//**What is this for? Never used again in Jeff's code.**
	//private final static String SESSION_KNOCK_STATE = "knockState";
	
	public GMCConversation() {
		super();
		
		//Add custom intent names for dispatcher use
		//**I don't have a list of our functions made @ last meeting, is anything missing?**
		supportedIntentNames.add(INTENT_START);
		supportedIntentNames.add(INTENT_UPCOMING_GNC);
		supportedIntentNames.add(INTENT_DATE_GMS);
		supportedIntentNames.add(INTENT_SPECIFIC_EVENT_DETAILS);
		supportedIntentNames.add(INTENT_SPECIFIC_EVENT_PURCHASE);
	}
	
	@Override
	public SpeechletResponse respondToIntentRequest(IntentRequest intentReq, Session session) {
		Intent intent = intentReq.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;
		SpeechletResponse response = null;
		
		if (INTENT_START.equals(intentName)) {
			response = handleGMCIntentStart(intentReq, session);
        }
		else if (INTENT_UPCOMING_GNC.equals(intentName)) {
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
			response = newTellResponse("Sorry, I didn't get that. I can list upcoming events or tell you about specific events.", false);
		}
		
		return response;
	}
	
	//Pre: Takes in generic call to GMC skill
	//Post: Pompts user to ask about upcoming events
	private SpeechletResponse handleGMCIntentStart(IntentRequest intentReq, Session session) {
	}
	
	//Pre: Takes a generic request for upcoming events
	//Post: Lists three most recent events, prompts user to ask about a specific event or ask for more events
	private SpeechletResponse handleGenericUpcomingIntent(IntentRequest intentReq, Session session) {
	}
	
	//Pre: 	Takes a request for events on or around a specified date.
	//		**I think this declaration needs to change to accept Amazon.date slot**
	//Post: Lists events in specified range, or explains there are no events in range and presents user
	//	    with three events closest to desired date.
	private SpeechletResponse handleDateSpecifiedIntent(IntentRequest intentReq, Session session) {
	}
	
	//Pre: Takes a request for details about a specified Event
	//		**This needs to be changed to accept a specific event as a slot, that slot needs to be defined above**
	//Post: Reads a one or two sentace description of the event specified, prompts user to purchase tickets. 
	private SpeechletResponse handleSpecificEventDetailsIntent(IntentRequest intentReq, Session session) {
	}
	
	//Pre: Takes a request to purchase tickets for specific event
	//		**This needs to be changed to accept a specific event as a slot, that slot needs to be defined above**
	//Post: if(personal){Asks user for confirmation they'd like to purchase ticket, bills their amazon account for the 
	//		ticket and sends the ticket to user's amazon email}**How do we tie this to amazon acct/email?**
	// 		else{prompt user for their email and sends link to purchase tickets to aforementioned email.
	//		**We may want to fork this call to give options for ticketing levels, ask about #of tickets to purchase**
	private SpeechletResponse handleSpecificEventPurchaseIntent(IntentRequest intentReq, Session session) {
	}
	
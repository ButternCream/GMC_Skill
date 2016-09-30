package com.squad.voice.model.interfaces;

import java.util.List;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;

/**
 * The ConversationInterface must be implemented by any Conversational object
 * in order to support the integration of many Conversations into a single
 * digital persona or skill. 
 * 
 * @author Jeffrey Neong
 * @version 1.0
 * 
 */

public interface ConversationInterface {
	
	//Returns a list of all Intents it supports
	public List<String> getSupportedIntentNames();

	//Returns the appropriate response to the IntentRequest
	public SpeechletResponse respondToIntentRequest(IntentRequest intentReq, Session session);
}

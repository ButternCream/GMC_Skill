/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.squad.voice.speechlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.squad.voice.skill.GMCConversation;
import com.squad.voice.model.base.Conversation;

/**
 * This TemplateBaseSkillSpeechlet class functions as a "dispatcher" that passes Intents
 * to the proper Conversation object that supports it. You should only need to add a new
 * instance of your custom Conversation objects to the supportedConversations[] List in the 
 * onSessionStarted() method.
 * 
 * NOTE: You should not need to edit anything else within this class file, except noted above.
 * 
 * @author Jeffrey Neong
 * @version 1.0
 * 
 */

public class TemplateBaseSkillSpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(TemplateBaseSkillSpeechlet.class);

    //Add a new instance of your Conversation to this List in the onSessionStarted method below
    List<Conversation> supportedConversations = new ArrayList<Conversation>();
    
    //Populated from supportedConversations List
    Map<String,Conversation> supportedIntentsByConversation = new HashMap<String,Conversation>();
        
    
    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        //All session initialization goes here - Beginning of lifecycle
        
        
        supportedConversations.add(new GMCConversation());
        
        
        //Populate a map of supported intents to conversations for later dispatch
        for(Conversation convo : supportedConversations) {
        	for(String intentName : convo.getSupportedIntentNames()) {
        		supportedIntentsByConversation.put(intentName, convo);
        	}
        }
    }

    
    //This method is called if the skill is invoked with a "start" intent
    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        String welcomeStr = "Welcome to the GMC event reservation skill. Try asking me about upcoming events.";
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(welcomeStr);
        
        return SpeechletResponse.newTellResponse(outputSpeech);
    }

    
    //This method is called to service any known intents defined in your voice interaction model
    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        SpeechletResponse response = null;
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        
        //Check for convo handling
        Conversation convo = getConvoForIntent(intentName);
        if(convo != null) {
        	response = convo.respondToIntentRequest(request, session);
        }
        else {
            throw new SpeechletException("Invalid Intent");
        }
        
        return response;
    }



    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any session cleanup logic would go here
    }


    private Conversation getConvoForIntent(String intentName) {
    	Conversation convo = null;
		//Get a new instance of a proper conversation. 
    	//TODO: Filter out common answers so they do not create an erroneously new convo that is ambiguous.
    	convo = supportedIntentsByConversation.get(intentName);
    	if(convo == null) {
    		log.error("Cannot find a Conversation object that supports intent name "+intentName);
    	}
    	return convo;
    }

}

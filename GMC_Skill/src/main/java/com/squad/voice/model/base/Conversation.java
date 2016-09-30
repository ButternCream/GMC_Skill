package com.squad.voice.model.base;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.squad.voice.model.interfaces.ConversationInterface;

/**
 * Base class for Conversations to subclass as a quick start
 * to implementing the ConversationInterface. It also has a few
 * convenience functions to build Ask and Tell SpeechletResponses.
 * 
 * @author Jeffrey Neong
 * @version 1.0
 * 
 */

public class Conversation implements ConversationInterface {
    private static final Logger log = LoggerFactory.getLogger(Conversation.class);

	public List<String> supportedIntentNames;

	public Conversation() {
		supportedIntentNames = new ArrayList<String>();
	}
	
	@Override
	public List<String> getSupportedIntentNames() {
		return supportedIntentNames;
	}

	/*
	 * The respondToIntentRequest() method below should be Overridden in your subclass to
	 * service the routed intents.
	*/
	@Override
	public SpeechletResponse respondToIntentRequest(IntentRequest intentReq, Session session) {
		log.error("respondToIntentRequest called within Conversation superclass.");
		return newTellResponse("Sorry, I am unsure what conversational context we're in. I'm just here to look pretty.", false);
	}

	

    /**
     * Wrapper for creating the Ask response from the input strings.
     * 
     * @param stringOutput
     *            the output to be spoken
     * @param isOutputSsml
     *            whether the output text is of type SSML
     * @param repromptText
     *            the reprompt for if the user doesn't reply or is misunderstood.
     * @param isRepromptSsml
     *            whether the reprompt text is of type SSML
     * @return SpeechletResponse the speechlet response
     */
    public static SpeechletResponse newAskResponse(final String stringOutput, final boolean isOutputSsml,
            final String repromptText, final boolean isRepromptSsml) {
        OutputSpeech outputSpeech, repromptOutputSpeech;
        if (isOutputSsml) {
            outputSpeech = new SsmlOutputSpeech();
            ((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);
        } else {
            outputSpeech = new PlainTextOutputSpeech();
            ((PlainTextOutputSpeech) outputSpeech).setText(stringOutput);
        }

        if (isRepromptSsml) {
            repromptOutputSpeech = new SsmlOutputSpeech();
            ((SsmlOutputSpeech) repromptOutputSpeech).setSsml(repromptText);
        } else {
            repromptOutputSpeech = new PlainTextOutputSpeech();
            ((PlainTextOutputSpeech) repromptOutputSpeech).setText(repromptText);
        }
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);
        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
    }


    /**
     * Creates and returns a response intended to tell the user something. After the specified
     * output is read to the user, the session ends. Note that the response created with this method
     * does not include a graphical card for the companion app.
     * <p>
     * All arguments in this method are required and cannot be null.
     * 
     * @param outputSpeech
     *            output speech content for the tell voice response
     * @return SpeechletResponse spoken response for the given input
     */
    public static SpeechletResponse newTellResponse(final String stringOutput, final boolean isOutputSsml) {
        if (stringOutput == null) {
            throw new IllegalArgumentException("OutputSpeech cannot be null");
        }

        OutputSpeech outputSpeech;
        if (isOutputSsml) {
            outputSpeech = new SsmlOutputSpeech();
            ((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);
        } else {
            outputSpeech = new PlainTextOutputSpeech();
            ((PlainTextOutputSpeech) outputSpeech).setText(stringOutput);
        }
        
        SpeechletResponse response = new SpeechletResponse();
        response.setShouldEndSession(true);
        response.setOutputSpeech(outputSpeech);
        return response;
    }

    /**
     * Creates and returns a response intended to tell the user something, both in speech and with a
     * graphical card in the companion app. After the tell output is read to the user, the session
     * ends.
     * <p>
     * All arguments in this method are required and cannot be null.
     * 
     * @param outputSpeech
     *            output speech content for the tell voice response
     * @param card
     *            card to display in the companion application
     * @return SpeechletResponse spoken and visual response for the given input
     */
    public static SpeechletResponse newTellResponse(final String stringOutput, final boolean isOutputSsml, final Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        
        OutputSpeech outputSpeech;
        if (isOutputSsml) {
            outputSpeech = new SsmlOutputSpeech();
            ((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);
        } else {
            outputSpeech = new PlainTextOutputSpeech();
            ((PlainTextOutputSpeech) outputSpeech).setText(stringOutput);
        }

        SpeechletResponse response = newTellResponse(stringOutput, isOutputSsml);
        response.setCard(card);
        return response;
    }
}

/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.cs370.alexa.handler;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import com.cs370.alexa.speechlet.TemplateBaseSkillSpeechlet;

/**
 * This project contains the Alexa Skills Kit implementation in an AWS Lambda stream handler. 
 * 
 * NOTE: You should not need to edit anything within this class file. When defining your Lamda
 * function, this is considered the Stream Handler and should be defined in Lambda with the full
 * class name below:
 * 
 * com.neong.voice.handler.TemplateSpeechletRequestStreamHandler
 * 
 * @author Jeffrey Neong
 * @version 1.0
 * 
 */

public class TemplateSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> supportedApplicationIds;

    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds = new HashSet<String>();
        //supportedApplicationIds.add("amzn1.echo-sdk-ams.app.99745801-04fd-4f42-a8f5-2204fd366942");
    }

    public TemplateSpeechletRequestStreamHandler() {
        super(new TemplateBaseSkillSpeechlet(), supportedApplicationIds);
    }

    public TemplateSpeechletRequestStreamHandler(Speechlet speechlet,
            Set<String> supportedApplicationIds) {
        super(speechlet, supportedApplicationIds);
    }

}

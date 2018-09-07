/*
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.semaifour.facesix.aws.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;

/**
 * This class extends {@link AWSIotMessage} to provide customized handlers for
 * non-blocking message publishing.
 */
public class NonBlockingPublishListener extends AWSIotMessage {

	static Logger LOG = LoggerFactory.getLogger(PublishSubscribeSample.class.getName());
    public NonBlockingPublishListener(String topic, AWSIotQos qos, String payload) {
        super(topic, qos, payload);
    }

    @Override
    public void onSuccess() {
    	LOG.info(" >>> " + getStringPayload());
    }

    @Override
    public void onFailure() {
    	LOG.info(" publish failed for " + getStringPayload());
    }

    @Override
    public void onTimeout() {
    	LOG.info(" publish timeout for " + getStringPayload());
    }

}

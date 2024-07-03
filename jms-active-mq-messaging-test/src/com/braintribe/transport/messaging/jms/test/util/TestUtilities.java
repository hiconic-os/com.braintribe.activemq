// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.transport.messaging.jms.test.util;

import java.util.Map;

import javax.jms.Message;

import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.codec.marshaller.bin.Bin2Marshaller;
import com.braintribe.model.messaging.Queue;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.jms.JmsMessageConsumer;
import com.braintribe.transport.messaging.jms.JmsSession;

public class TestUtilities {

	public static MessagingContext getMessagingContext() {
		MessagingContext context = new MessagingContext();
		context.setMarshaller(getMessageMarshaller());
		return context;
	}
	
	public static Marshaller getMessageMarshaller() {
		// @formatter:off
		return new Bin2Marshaller();
		// @formatter:on
	}

	public static void emptyQueue(JmsSession session, String queueName) throws Exception {
		Queue queue = session.createQueue(queueName);
		JmsMessageConsumer messageConsumer = (JmsMessageConsumer) session.createMessageConsumer(queue);
		javax.jms.MessageConsumer jmsMessageConsumer = messageConsumer.getJmsMessageConsumer();
		while(true) {
			Message msg = jmsMessageConsumer.receive(2000L);
			if (msg == null) {
				break;
			}
		}
		messageConsumer.close();
	}
	
	public static void checkNeedleInHaystack(Map<String,Object> hayStack, Map<String,Object> needle) {
		for (Map.Entry<String,Object> needleEntry : needle.entrySet()) {
			boolean found = false;
			for (Map.Entry<String,Object> haystackEntry : hayStack.entrySet()) {
				if (needleEntry.getKey().equals(haystackEntry.getKey()) &&
						needleEntry.getValue().equals(haystackEntry.getValue())) {
					found = true;
					break;
				}
			}
			if (!found) {
				throw new AssertionError("Could not find entry "+needleEntry);
			}
		}
	}
	
}

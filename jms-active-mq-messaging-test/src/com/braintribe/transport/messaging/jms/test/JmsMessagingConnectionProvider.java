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
package com.braintribe.transport.messaging.jms.test;

import com.braintribe.codec.marshaller.bin.Bin2Marshaller;
import com.braintribe.model.messaging.expert.Messaging;
import com.braintribe.model.messaging.jms.JmsActiveMqConnection;
import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.jms.JmsActiveMqMessaging;
import com.braintribe.transport.messaging.jms.test.config.TestConfiguration;

public class JmsMessagingConnectionProvider {
	
	public static final JmsMessagingConnectionProvider instance = new JmsMessagingConnectionProvider();
	
	private final MessagingConnectionProvider<? extends MessagingConnection> messagingConnectionProvider;
	
	private JmsMessagingConnectionProvider() {
		messagingConnectionProvider = getMessagingConnectionProvider();
	}
	
	public MessagingConnectionProvider<? extends MessagingConnection> get() {
		return messagingConnectionProvider;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected com.braintribe.transport.messaging.api.MessagingConnectionProvider<? extends MessagingConnection> getMessagingConnectionProvider() {
		
		com.braintribe.model.messaging.expert.Messaging denotationType = queryDenotationType("cortex");
		
		com.braintribe.transport.messaging.api.Messaging messaging = getExpertByDenotationType(denotationType);
		
		return messaging.createConnectionProvider(denotationType, getMessagingContext());
		
	}
	
	@SuppressWarnings("unused")
	protected com.braintribe.model.messaging.expert.Messaging queryDenotationType(String name) {

		try {
			TestConfiguration testConfiguration = ConfigurationHolder.configurator.getTestConfiguration();
			if (testConfiguration == null) {
				throw new RuntimeException("Could not find bean 'testConfiguration'");
			}

			JmsActiveMqConnection provider = JmsActiveMqConnection.T.create();
			provider.setHostAddress(testConfiguration.getProviderURL());
			provider.setUsername(testConfiguration.getUsername());
			provider.setPassword(testConfiguration.getPassword());

			return provider;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	protected com.braintribe.transport.messaging.api.Messaging<? extends Messaging> getExpertByDenotationType(com.braintribe.model.messaging.expert.Messaging denotationType) {
		
		if (denotationType instanceof JmsActiveMqConnection) {
			JmsActiveMqMessaging messaging = new JmsActiveMqMessaging();
			return messaging;
		}
		
		return null;
		
	}

	protected MessagingContext getMessagingContext() {
		MessagingContext context = new MessagingContext();
		context.setMarshaller(new Bin2Marshaller());
		return context;
	}
	
}

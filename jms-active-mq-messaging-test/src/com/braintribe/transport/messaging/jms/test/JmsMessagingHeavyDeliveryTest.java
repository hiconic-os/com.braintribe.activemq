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

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.test.GmMessagingHeavyDeliveryTest;
import com.braintribe.transport.messaging.jms.test.config.Configurator;

public class JmsMessagingHeavyDeliveryTest extends GmMessagingHeavyDeliveryTest {

	@BeforeClass
	public static void initTests() throws Exception {

		// Test parameters
		maxConcurrentTests = 10;
		multipleConsumersQty = 2;
		multipleMessagesQty = 5;

		ConfigurationHolder.configurator = new Configurator();

		ActiveMQConnectionFactory f = new ActiveMQConnectionFactory();
		f.setBrokerURL(null);
	}

	@AfterClass
	public static void shutdown() {
		ConfigurationHolder.configurator.close();
	}

	@Override
	protected MessagingConnectionProvider<? extends MessagingConnection> getMessagingConnectionProvider() {
		return JmsMessagingConnectionProvider.instance.get();
	}

	@Override
	protected MessagingContext getMessagingContext() {
		return JmsMessagingConnectionProvider.instance.getMessagingContext();
	}

}

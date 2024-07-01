// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.transport.messaging.jms.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.test.GmMessagingHeavyDistributionTest;
import com.braintribe.transport.messaging.jms.test.config.Configurator;

public class JmsMessagingHeavyDistributionTest extends GmMessagingHeavyDistributionTest {

	@BeforeClass
	public static void initTests() throws Exception {

		// Test parameters
		maxConcurrentTests = 10;
		messages = new int[] { 1, 4, 8 };
		sessionGroup = 2;
		messageGroupForDestination = 4;
		messageGroupForProducer = 2;
		messageGroupForConsumer = 2;

		ConfigurationHolder.configurator = new Configurator();

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

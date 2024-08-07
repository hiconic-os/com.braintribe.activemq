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
package com.braintribe.transport.messaging.jms;

import java.net.URL;

import javax.jms.Connection;
import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportFactory;
import org.apache.activemq.transport.tcp.TcpTransportFactory;

import com.braintribe.logging.Logger;
import com.braintribe.model.messaging.jms.JmsActiveMqConnection;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.utils.classloader.ClassLoaderTools;
import com.braintribe.utils.lcd.StringTools;

public class JmsActiveMqConnectionProvider extends JmsConnectionProvider {

	private static final Logger logger = Logger.getLogger(JmsActiveMqConnectionProvider.class);
	
	protected JmsActiveMqConnection configuration = null;
	protected ActiveMQConnectionFactory connectionFactory = null;

	@Override
	public JmsConnection provideMessagingConnection() throws MessagingException {
		try {
			
			Connection jmsConnection = this.createJmsConnection();

			logger.trace("Successfully created queue connection");
			JmsConnection connection = new JmsConnection(this.configuration, jmsConnection, this);
			
			super.addConnection(connection);
			
			return connection;
			
		} catch(Exception e) {
			throw new MessagingException("Could not initialize JMS connection.", e);
		}
	}

	@Override
	protected Connection createJmsConnection() throws MessagingException {
		Thread currentThread = Thread.currentThread();
		ClassLoader originalCl = currentThread.getContextClassLoader();
		try {
			currentThread.setContextClassLoader(getClass().getClassLoader());
			
			this.createConnectionFactory();
			
			Connection jmsConnection = null;

			String username = this.configuration.getUsername();
			String password = this.configuration.getPassword();

			if ((username != null) && (password != null)) {
				logger.debug(String.format("Creating queue connection for user '%s'", username));
				jmsConnection = this.connectionFactory.createConnection(username, password);
			} else {
				logger.debug("Creating anonymous queue connection");
				jmsConnection = this.connectionFactory.createConnection();
			}

			jmsConnection.setExceptionListener(new JmsExceptionListener(logger));

			logger.trace("Successfully created queue connection");
			return jmsConnection;
			
		} catch(Exception e) {
			ClassLoader classLoader1 = TcpTransportFactory.class.getClassLoader();
			ClassLoader classLoader2 = TransportFactory.class.getClassLoader();
			logger.error("TcpTransportFactory classloader: "+classLoader1+", TransportFactory classloader: "+classLoader2);
			logger.error("TcpTransportFactory classloader hashcode: "+System.identityHashCode(classLoader1)+", TransportFactory classloader hashcode: "+System.identityHashCode(classLoader2));
			try {
				URL classLocation1 = ClassLoaderTools.getClassLocation(TcpTransportFactory.class);
				URL classLocation2 = ClassLoaderTools.getClassLocation(TransportFactory.class);
				logger.error("TcpTransportFactory location: "+classLocation1+", TransportFactory location: "+classLocation2);
			} catch(Throwable t) {
				logger.error("Could not determine class locations.", t);
			}
			throw new MessagingException("Could not create JMS connection.", e);
		} finally {
			currentThread.setContextClassLoader(originalCl);
		}
	}
	
	protected void createConnectionFactory() throws Exception {
		if (this.connectionFactory != null) {
			return;
		}
		try {

			String username = this.configuration.getUsername();
			String password = this.configuration.getPassword();

			if ((username != null) && (password != null)) {			
				this.connectionFactory = new ActiveMQConnectionFactory(username, password, this.configuration.getHostAddress());
			} else {
				this.connectionFactory = new ActiveMQConnectionFactory(this.configuration.getHostAddress());
			}

		} catch (Exception e) {
			throw new Exception("Could not initialize naming context.", e);
		}
	}

	public JmsActiveMqConnection getConfiguration() {
		return configuration;
	}
	public void setConfiguration(JmsActiveMqConnection configuration) {
		this.configuration = configuration;
	}

	@Override
	public Queue getQueue(JmsSession session, String queueName) throws MessagingException {
		try {
			Queue queue = session.getJmsSession().createQueue(queueName);
			return queue;
		} catch(Exception e) {
			throw new MessagingException("Could not access queue "+queueName, e);
		}
	}


	@Override
	public Topic getTopic(JmsSession session, String topicName) throws MessagingException {
		try {
			Topic topic = session.getJmsSession().createTopic(topicName);
			return topic;
		} catch(Exception e) {
			throw new MessagingException("Could not access topic "+topicName, e);
		}
	}
	
	@Override
	public String toString() {
		if (configuration == null) {
			return "ActiveMQ Messaging";
		} else {
			String addr = configuration.getHostAddress();
			if (StringTools.isBlank(addr)) {
				return "ActiveMQ Messaging";
			} else {
				return "ActiveMQ Messaging connected to "+addr;
			}
		}
	}

	@Override
	public String description() {
		return toString();
	}
}

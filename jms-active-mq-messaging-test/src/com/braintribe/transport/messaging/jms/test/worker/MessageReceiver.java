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
package com.braintribe.transport.messaging.jms.test.worker;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.model.messaging.Message;
import com.braintribe.transport.messaging.api.MessageConsumer;

public class MessageReceiver implements Runnable {

	protected MessageConsumer messageConsumer = null;
	protected int expectedMessages = 0;
	protected long timeout = 60000L;

	protected List<Message> receivedMessages = new ArrayList<Message>();
	protected Throwable throwable = null;
	protected boolean done = false;

	public static List<Message> receiveMessagesSync(MessageConsumer messageConsumer, int expectedMessages, long timeout) throws Exception {
		MessageReceiver receiver = new MessageReceiver(messageConsumer, expectedMessages, timeout);
		receiver.run();
		receiver.waitFor();

		List<Message> receivedMessages = receiver.getReceivedMessages();
		if (receiver.getThrowable() != null) {
			throw new Exception("Receiver received an exception", receiver.getThrowable());
		} else {
			if (receivedMessages.size() != expectedMessages) {
				throw new AssertionError("Expected " + expectedMessages + " messages but received only " + receivedMessages.size());
			}
		}
		return receivedMessages;
	}

	public static MessageReceiver receiveMessagesAsync(MessageConsumer messageConsumer, int expectedMessages, long timeout) {
		MessageReceiver receiver = new MessageReceiver(messageConsumer, expectedMessages, timeout);
		Thread t = new Thread(receiver, "Receiver");
		t.setDaemon(true);
		t.start();
		return receiver;
	}

	public MessageReceiver(MessageConsumer messageConsumer, int expectedMessages, long timeout) {
		this.messageConsumer = messageConsumer;
		this.expectedMessages = expectedMessages;
		this.timeout = timeout;
	}

	@Override
	public void run() {

		long start = System.currentTimeMillis();

		while (true) {
			if (receivedMessages.size() >= expectedMessages) {
				break;
			}
			if ((System.currentTimeMillis() - start) >= timeout) {
				break;
			}

			try {
				Message message = this.messageConsumer.receive(1000L);
				if (message != null) {
					this.receivedMessages.add(message);
				}
			} catch (Throwable t) {
				this.throwable = t;
				break;
			}
		}
		this.done = true;
		synchronized (this) {
			notify();
		}
	}

	public void waitFor() throws InterruptedException {
		synchronized (this) {
			if (done) {
				return;
			}
			wait(this.timeout);
		}
	}
	public List<Message> getReceivedMessages() {
		return receivedMessages;
	}
	public Throwable getThrowable() {
		return throwable;
	}

}

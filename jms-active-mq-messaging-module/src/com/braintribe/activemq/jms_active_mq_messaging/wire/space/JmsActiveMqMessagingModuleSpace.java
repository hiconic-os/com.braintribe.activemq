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
package com.braintribe.activemq.jms_active_mq_messaging.wire.space;

import com.braintribe.activemq.model.deployment.JmsActiveMqConnection;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.processing.deployment.api.ExpertContext;
import com.braintribe.model.processing.deployment.api.binding.DenotationBindingBuilder;
import com.braintribe.transport.messaging.jms.JmsActiveMqConnectionProvider;
import com.braintribe.transport.messaging.jms.JmsActiveMqMessaging;
import com.braintribe.utils.lcd.NullSafe;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.module.api.DenotationTransformationContext;
import tribefire.module.api.DenotationTransformerRegistry;
import tribefire.module.wire.contract.ClusterBindersContract;
import tribefire.module.wire.contract.TribefireModuleContract;
import tribefire.module.wire.contract.TribefireWebPlatformContract;

@Managed
public class JmsActiveMqMessagingModuleSpace implements TribefireModuleContract {

	@Import
	private TribefireWebPlatformContract tfPlatform;

	@Import
	private ClusterBindersContract clusterBinders;

	@Override
	public void bindHardwired() {
		DenotationTransformerRegistry dtRegistry = tfPlatform.hardwiredExperts().denotationTransformationRegistry();

		dtRegistry.registerStandardMorpher( //
				com.braintribe.model.messaging.jms.JmsActiveMqConnection.T, //
				JmsActiveMqConnection.T, //
				JmsActiveMqMessagingModuleSpace::morphMqConnection //
		);
	}

	private static Maybe<JmsActiveMqConnection> morphMqConnection(DenotationTransformationContext context,
			com.braintribe.model.messaging.jms.JmsActiveMqConnection pluggable) {

		JmsActiveMqConnection deployable = context.create(JmsActiveMqConnection.T);
		deployable.setGlobalId("edr2cc:active-mq/" + context.denotationId());

		deployable.setExternalId(deployable.getGlobalId());
		deployable.setName(NullSafe.get(pluggable.getName(), "etcd Message Queue"));

		deployable.setAcknowledgeMode(pluggable.getAcknowledgeMode());
		deployable.setHostAddress(pluggable.getHostAddress());
		deployable.setTransacted(pluggable.getTransacted());
		deployable.setUsername(pluggable.getUsername());
		deployable.setPassword(pluggable.getPassword());

		return Maybe.complete(deployable);
	}

	@Override
	public void bindDeployables(DenotationBindingBuilder bindings) {
		bindings.bind(JmsActiveMqConnection.T) //
				.component(clusterBinders.messaging()) //
				.expertFactory(this::jmsActiveMqMessaging);
	}

	@Managed
	private JmsActiveMqConnectionProvider jmsActiveMqMessaging(ExpertContext<JmsActiveMqConnection> expertContext) {
		JmsActiveMqConnection deployable = expertContext.getDeployable();

		return new JmsActiveMqMessaging().createConnectionProvider(toLegacyConfig(deployable), tfPlatform.messaging().context());
	}

	private com.braintribe.model.messaging.jms.JmsActiveMqConnection toLegacyConfig(JmsActiveMqConnection deployable) {
		com.braintribe.model.messaging.jms.JmsActiveMqConnection result = com.braintribe.model.messaging.jms.JmsActiveMqConnection.T.create();

		result.setHostAddress(deployable.getHostAddress());
		result.setUsername(deployable.getUsername());
		result.setPassword(deployable.getPassword());
		result.setTransacted(deployable.getTransacted());
		result.setAcknowledgeMode(deployable.getAcknowledgeMode());
		result.setName(deployable.getName());

		return result;
	}

}

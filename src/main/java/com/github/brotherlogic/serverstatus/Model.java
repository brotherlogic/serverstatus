package com.github.brotherlogic.serverstatus;

import discovery.Discovery.RegistryEntry;
import discovery.Discovery.ServiceList;
import discovery.DiscoveryServiceGrpc;

public class Model extends NetworkObject {
	State s;

	// Updates the state of the system
	private void updateState() {
		DiscoveryServiceGrpc.DiscoveryServiceBlockingStub service = DiscoveryServiceGrpc
				.NewBlockingStub(dial("discover"));
		ServiceList serviceList = service.listAllServices(discovery.Discovery.Empty.newBuilder().build());

		for (RegistryEntry entry : serviceList.getServicesList()) {
			s.update(entry);
		}
	}

}

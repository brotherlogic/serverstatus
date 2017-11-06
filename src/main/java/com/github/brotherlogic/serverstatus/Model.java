package com.github.brotherlogic.serverstatus;

import com.github.brotherlogic.javaserver.NetworkObject;

import discovery.Discovery.RegistryEntry;
import discovery.Discovery.ServiceList;
import discovery.DiscoveryServiceGrpc;

public class Model extends NetworkObject {
	State s;

	String bServer;

	public Model(String baseServer) {
		bServer = baseServer;
	}

	// Updates the state of the system
	private void updateState() {
		DiscoveryServiceGrpc.DiscoveryServiceBlockingStub service = DiscoveryServiceGrpc
				.NewBlockingStub(dial(bServer, "discover"));
		ServiceList serviceList = service.listAllServices(discovery.Discovery.Empty.newBuilder().build());

		for (RegistryEntry entry : serviceList.getServicesList()) {
			s.update(entry);
		}
	}

}

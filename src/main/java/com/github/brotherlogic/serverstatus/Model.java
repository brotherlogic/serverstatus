package com.github.brotherlogic.serverstatus;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.github.brotherlogic.javaserver.NetworkObject;

import discovery.Discovery.RegistryEntry;
import discovery.Discovery.ServiceList;
import discovery.DiscoveryServiceGrpc;

public class Model extends NetworkObject {
	State s = new State();

	String bServer;

	public Model(String baseServer) {
		bServer = baseServer;
	}

	public int getNumberOfJobs() {
		return s.getNumberOfJobs();
	}

	public Collection<Job> getJobs() {
		return s.getJobs();
	}

	public void update() {
		updateState();
		System.out.println("STATE = " + s);
	}

	// Updates the state of the system
	private void updateState() {
		DiscoveryServiceGrpc.DiscoveryServiceBlockingStub service = DiscoveryServiceGrpc
				.newBlockingStub(dial(bServer, "discovery")).withDeadlineAfter(1, TimeUnit.SECONDS);
		try {
			ServiceList serviceList = service.listAllServices(discovery.Discovery.Empty.newBuilder().build());

			for (RegistryEntry entry : serviceList.getServicesList()) {
				s.update(entry);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Just cleaning then I guess");
		}
		s.clean();
	}

	public static void main(String[] args) {
		Model m = new Model(args[0]);
		m.update();
	}

}

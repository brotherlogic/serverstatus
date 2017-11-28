package com.github.brotherlogic.serverstatus;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.github.brotherlogic.javaserver.NetworkObject;

import discovery.Discovery.RegistryEntry;
import discovery.Discovery.ServiceList;
import discovery.DiscoveryServiceGrpc;
import goserver.goserverServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

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

	private void updateState(RegistryEntry entry) throws Exception {
		goserverServiceGrpc.goserverServiceBlockingStub service = goserverServiceGrpc
				.newBlockingStub(
						ManagedChannelBuilder.forAddress(entry.getIp(), entry.getPort()).usePlaintext(true).build())
				.withDeadlineAfter(1, TimeUnit.SECONDS);
		goserver.Goserver.ServerState state = service.state(goserver.Goserver.Empty.newBuilder().build());
		for (goserver.Goserver.State st : state.getStatesList()) {
			if (st.getKey().equals("core")) {
				s.update(entry, st.getTimeValue() + "");
			}
		}

	}

	// Updates the state of the system
	private void updateState() {
		ManagedChannel c = dial(bServer, "discovery");
		DiscoveryServiceGrpc.DiscoveryServiceBlockingStub service = DiscoveryServiceGrpc.newBlockingStub(c)
				.withDeadlineAfter(1, TimeUnit.SECONDS);
		try {
			ServiceList serviceList = service.listAllServices(discovery.Discovery.Empty.newBuilder().build());

			for (final RegistryEntry entry : serviceList.getServicesList()) {
				boolean exists = s.update(entry, null);
				if (!exists) {
					System.out.println("Starting update thread for " + entry);
					Thread updater = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								while (true) {
									Thread.sleep(5000);
									updateState(entry);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					updater.start();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Just cleaning then I guess");
		}
		s.clean();

		try {
			c.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Model m = new Model(args[0]);
		m.update();
	}

}

package com.github.brotherlogic.serverstatus;

import server.ServerOuterClass.Empty;

public class Model extends NetworkObject{
	State s;
	
	//Updates the state of the system
	private void updateState() {
		DiscoverServiceGrpc.DiscoverServiceBlockingStub service = DiscoverServiceGrpc.NewBlockingStub(dial("discover"));
		serviceList = service.ListAllServices(Empty.newBuilder().build());
		
		for(RegistryEntry entry : serviceList.getServices()) {
			GoServerServiceGrpc.GoServiceBlockingStub server = GoServerServiceGrpc.NewBlockingStub(dial(entry));
			alive = server.IsAlive(Empty.newBuilder().build());
			
			s.update(entry,alive);
		}
	}
	
}

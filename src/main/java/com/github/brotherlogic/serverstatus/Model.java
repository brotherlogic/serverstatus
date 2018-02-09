package com.github.brotherlogic.serverstatus;

import com.github.brotherlogic.javaserver.NetworkObject;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import discovery.Discovery.RegistryEntry;
import discovery.Discovery.ListRequest;
import discovery.Discovery.ServiceList;
import discovery.DiscoveryServiceGrpc;
import io.grpc.ManagedChannel;

public class Model extends NetworkObject {
    State s = new State();

    String bServer;

    public Model(String baseServer) {
        bServer = baseServer;
    }

    public static void main(String[] args) {
        Model m = new Model(args[0]);
        m.update();
    }

    public int getNumberOfJobs() {
        return s.getNumberOfJobs();
    }

    public Collection<Job> getJobs() {
        return s.getJobs();
    }

    public void update() {
        updateState();
    }

    // Updates the state of the system
    private void updateState() {
        ManagedChannel c = dial(bServer, "discovery");
        DiscoveryServiceGrpc.DiscoveryServiceBlockingStub service = DiscoveryServiceGrpc.newBlockingStub(c)
                .withDeadlineAfter(1, TimeUnit.SECONDS);
        try {
            ServiceList serviceList = service.listAllServices(discovery.Discovery.ListRequest.newBuilder().build()).getServices();

            long timestamp = System.currentTimeMillis();
            for (final RegistryEntry entry : serviceList.getServicesList()) {
                s.update(entry, timestamp);
            }
            s.clean(timestamp);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            c.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

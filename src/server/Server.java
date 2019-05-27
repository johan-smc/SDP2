package server;

import protocol2pc.Coordinator;
import protocol2pc.Participant;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class Server {
    protected Coordinator coordinator;
    protected Participant participant;
    protected Map<String, Resource> resources;
    protected ServerReference myServer;
    public static final String NAME_SERVICE = "service";

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    protected String fileName = "server.txt";

    protected  Server(ServerReference coordinator, ServerReference participant, ServerReference serverReference){
        this.resources = new HashMap<>();
        try{

            Registry registry = LocateRegistry.createRegistry(coordinator.getPort());
            this.coordinator= new Coordinator(coordinator);
            registry.bind(Coordinator.NAME_SERVICE, this.coordinator);
            System.err.println("Coordinator ready");


            registry = LocateRegistry.createRegistry(participant.getPort());
            this.participant= new Participant(this.resources, participant);
            registry.bind(Participant.NAME_SERVICE, this.participant);
            System.err.println("Coordinator ready");


            this.myServer = serverReference;

        }catch (Exception e){
            System.err.println("Server exception: "+ e.toString());
            e.printStackTrace();
        }
    }
    public void addResource(Resource resource)
    {
        this.resources.put(resource.getId(), resource);
    }

}

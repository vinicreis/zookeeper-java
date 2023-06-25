package server.thread;

import model.request.PutRequest;
import model.request.ReplicationRequest;

import java.net.ServerSocket;

public class ServerNodeThread extends ServerThread {

    public ServerNodeThread(ServerSocket serverSocket) {
        super(serverSocket);
    }

    @Override
    protected void put(PutRequest putRequest) {

    }

    protected void replication(ReplicationRequest replicationRequest) {

    }
}

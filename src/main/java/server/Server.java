package server;

import model.request.JoinRequest;
import model.request.PutRequest;
import model.request.ReplicationRequest;
import model.response.PutResponse;
import model.response.ReplicationResponse;

public interface Server {
    void start();
    void stop();
    JoinResponse join(JoinRequest request);
    PutResponse put(PutRequest request);
    ReplicationResponse replicate(ReplicationRequest request);
    void get();
}

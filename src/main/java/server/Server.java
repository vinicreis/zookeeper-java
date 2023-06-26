package server;

import model.request.GetRequest;
import model.request.JoinRequest;
import model.request.PutRequest;
import model.request.ReplicationRequest;
import model.response.GetResponse;
import model.response.JoinResponse;
import model.response.PutResponse;
import model.response.ReplicationResponse;

public interface Server {
    void start();
    void stop();
    int getPort();
    JoinResponse join(JoinRequest request);
    PutResponse put(PutRequest request);
    ReplicationResponse replicate(ReplicationRequest request);
    GetResponse get(GetRequest request);
}

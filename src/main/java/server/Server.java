package server;

import model.request.GetRequest;
import model.request.PutRequest;
import model.request.ReplicationRequest;
import model.response.GetResponse;
import model.response.PutResponse;
import model.response.ReplicationResponse;

public interface Server {
    void start();
    void stop();
    int getPort();
    PutResponse put(PutRequest request);
    ReplicationResponse replicate(ReplicationRequest request);
    GetResponse get(GetRequest request);
}

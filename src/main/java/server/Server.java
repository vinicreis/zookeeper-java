package server;

import model.Result;
import model.exception.OutdatedEntryException;
import model.repository.KeyValueRepository;
import model.repository.TimestampRepository;
import model.request.GetRequest;
import model.request.PutRequest;
import model.request.ReplicationRequest;
import model.response.GetResponse;
import model.response.PutResponse;
import model.response.ReplicationResponse;

import static util.AssertionUtils.handleException;

public interface Server {
    int getPort();
    TimestampRepository getTimestampRepository();
    KeyValueRepository getKeyValueRepository();
    void start();
    void stop();
    PutResponse put(PutRequest request);
    ReplicationResponse replicate(ReplicationRequest request);
    default GetResponse get(GetRequest request) {
        try {
            final KeyValueRepository.Entry entry = getKeyValueRepository().find(request.getKey(), request.getTimestamp());

            if (entry == null) return new GetResponse.Builder()
                    .result(Result.ERROR)
                    .message(String.format("Key %s not found...", request.getKey()))
                    .build();

            return new GetResponse.Builder()
                    .value(entry.getValue())
                    .timestamp(entry.getTimestamp())
                    .build();
        } catch (OutdatedEntryException e) {
            handleException("Server", String.format("Data with key %s is outdated", request.getKey()), e);
            return new GetResponse.Builder()
                    .result(Result.ERROR)
                    .message("Try other server or try later")
                    .exception(e)
                    .build();
        } catch (Exception e) {
            handleException("Server", "Failed to process JOIN operation", e);
            return new GetResponse.Builder().exception(e).build();
        }
    }
}

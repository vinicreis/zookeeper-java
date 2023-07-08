package server;

import model.enums.Result;
import model.exception.OutdatedEntryException;
import model.repository.KeyValueRepository;
import model.request.GetRequest;
import model.request.PutRequest;
import model.request.ReplicationRequest;
import model.response.GetResponse;
import model.response.PutResponse;
import model.response.ReplicationResponse;

import static util.AssertionUtils.handleException;
import static util.IOUtil.*;

public interface Server {
    int getPort();
    KeyValueRepository getKeyValueRepository();
    void start();
    void stop();
    PutResponse put(PutRequest request);
    ReplicationResponse replicate(ReplicationRequest request);

    default GetResponse get(GetRequest request) {
        GetResponse response;

        try {
            final KeyValueRepository.Entry entry = getKeyValueRepository().find(request.getKey(), request.getTimestamp());

            printf(
                    "Cliente %s:%d GET key: %s ts: %d. ",
                    request.getHost(),
                    request.getPort(),
                    request.getKey(),
                    request.getTimestamp()
            );

            if (entry == null) {
                response = new GetResponse.Builder()
                        .result(Result.NOT_FOUND)
                        .message(String.format("Valor com a chave %s não encontrado", request.getKey()))
                        .build();
            } else {
                printfLn(
                        "Cliente %s:%d GET key: %s ts: %d. Meu ts é %d, portanto devolvendo %s",
                        request.getHost(),
                        request.getPort(),
                        request.getKey(),
                        request.getTimestamp(),
                        entry.getTimestamp(),
                        entry.getValue()
                );

                response = new GetResponse.Builder()
                        .timestamp(entry.getTimestamp())
                        .value(entry.getValue())
                        .result(Result.OK)
                        .build();
            }
        } catch (OutdatedEntryException e) {
            handleException("Server", String.format("Data with key %s is outdated", request.getKey()), e);

            response = new GetResponse.Builder()
                    .result(Result.TRY_OTHER)
                    .message("Try other server or try later")
                    .exception(e)
                    .build();
        } catch (Exception e) {
            handleException("Server", "Failed to process GET operation", e);

            response = new GetResponse.Builder().exception(e).build();
        }

        printf("Meu ts é %d, portanto devolvendo ", response.getTimestamp());

        if (response.getResult() == Result.OK)
            printLn(response.getValue());
        else
            printLn(response.getResult().toString());

        return response;
    }
}

package server.model.request;

public class PutRequest implements Request {
    @Override
    public RequestType getType() {
        return RequestType.PUT;
    }
}

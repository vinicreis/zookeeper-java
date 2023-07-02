package model.request;

import com.google.gson.Gson;
import model.enums.Operation;

public interface Request {
    Operation getOperation();
    String getHost();
    int getPort();

    default String toJson() {
        return new Gson().toJson(this);
    }
}

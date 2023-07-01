package model;

import com.google.gson.Gson;

public interface Request {
    Operation getOperation();
    String getHost();
    int getPort();

    default String toJson() {
        return new Gson().toJson(this);
    }
}

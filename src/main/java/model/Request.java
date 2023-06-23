package model;

import com.google.gson.Gson;

public interface Request {
    Operation getOperation();

    default String toJson() {
        return new Gson().toJson(this);
    }
}

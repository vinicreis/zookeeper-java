package model.request;

import model.enums.Operation;

public interface Request {
    Operation getOperation();
    String getHost();
    int getPort();
}

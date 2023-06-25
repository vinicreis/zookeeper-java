package model;

public interface Response {
    Operation getOperation();
    Result getResult();
    String getMessage();
}

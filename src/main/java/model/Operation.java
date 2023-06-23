package model;

public enum Operation {
    PUT("PUT");

    private final String code;

    Operation(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

package model;

public enum Operation {
    JOIN("JOIN"),
    PUT("PUT"),
    GET("GET"),
    REPLICATE("REPLICATE");

    private final String code;

    Operation(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Operation fromCode(String code) {
        switch (code) {
            case "JOIN": return JOIN;
            case "PUT": return PUT;
            case "GET": return GET;
            case "REPLICATE": return REPLICATE;
            default:
                throw new IllegalArgumentException(String.format("Operation of code %s not found!", code));
        }
    }
}

package model;

import java.util.Arrays;

public enum Operation {
    JOIN("JOIN", 0),
    GET("GET", 1),
    PUT("PUT", 2),
    REPLICATE("REPLICATE", 3);

    private final String name;
    private final int code;

    Operation(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
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

    public static String printClient() {
        return String.join(
                " | ",
                (String[])Arrays.stream(new Operation[] { GET, PUT }).map((o) -> String.format("%s [%d]", o.getName(),o.getCode())).toArray()
        );
    }
}

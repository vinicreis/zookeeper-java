package model.enums;

import util.IOUtil;

import java.io.IOException;
import java.util.Arrays;

public enum Operation {
    JOIN("JOIN", 0),
    GET("GET", 1),
    PUT("PUT", 2),
    REPLICATE("REPLICATE", 3),
    EXIT("EXIT", 4);

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
            case "EXIT": return EXIT;
            default:
                throw new IllegalArgumentException(String.format("Operation of code %s not found!", code));
        }
    }

    public static Operation fromClient(int code) throws InterruptedException {
        switch (code) {
            case 1: return GET;
            case 2: return PUT;
            default:
                throw new InterruptedException("Interrupt command from input!");
        }
    }

    public static Operation readToClient() throws IOException, InterruptedException {
        return fromClient(Integer.parseInt(IOUtil.read("Digite a operação desejada ou outra tecla para encerrar...\n%s: ", printToClient())));
    }

    private static String printToClient() {
        return String.join(
                " | ",
                Arrays.stream(
                        new Operation[] { GET, PUT }).map((o) -> String.format("%s [%d]", o.getName(),o.getCode())
                ).toArray(String[]::new)
        );
    }
}

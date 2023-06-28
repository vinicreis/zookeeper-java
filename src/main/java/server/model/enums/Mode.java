package server.model.enums;

import java.util.Arrays;

public enum Mode {
    PRIMARY("Central", 0),

    SECONDARY("Nó", 1);

    private final Integer code;
    private final String name;

    Mode(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static String print() {
        return String.join(
                " | ",
                (String[])Arrays.stream(values()).map(
                    (m) -> String.format("%s [%d]", m.getName(), m.getCode())
                ).toArray()
        );
    }

    public static Mode fromInt(int code) {
        switch (code) {
            case 1: return SECONDARY;
            default: return PRIMARY;
        }
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

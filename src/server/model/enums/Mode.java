package server.model.enums;

public enum Mode {
    PRIMARY(0),

    SECONDARY(1);

    private final int code;

    Mode(int code) {
        this.code = code;
    }

    public static Mode fromInt(int code) {
        switch (code) {
            case 1: return SECONDARY;
            default: return PRIMARY;
        }
    }

    public int getCode() {
        return code;
    }
}

package server;

import com.google.gson.Gson;
import javafx.util.Pair;
import log.ConsoleLog;
import log.Log;
import ui.Message;
import util.IOUtil;

import java.util.HashMap;

import static util.IOUtil.read;

public class ServerNodeImpl {
    private static final String TAG = "ServerNodeImpl";
    private static final Log log = new ConsoleLog(TAG);
    private static final Gson gson = new Gson();
    private final String host;
    private final String port;
    private final HashMap<Pair<String, Long>, Object> data = new HashMap<>();

    public void put() {
        this.host = read(Message.ENTER_HOST);
        this.
    }
}

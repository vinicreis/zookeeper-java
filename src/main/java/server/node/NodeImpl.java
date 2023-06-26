package server.node;

import com.google.gson.Gson;
import log.ConsoleLog;
import log.Log;
import model.type.Pair;

import java.io.IOException;
import java.util.HashMap;

public class NodeImpl extends ServerImpl {
    private static final String TAG = "ServerNodeImpl";
    private static final Log log = new ConsoleLog(TAG);
    private static final Gson gson = new Gson();
    private final HashMap<Pair<String, Long>, Object> data = new HashMap<>();

    public NodeImpl() throws IOException {
        super();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}

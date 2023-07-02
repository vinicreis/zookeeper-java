package util;

import model.Request;
import model.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkUtil {
    public static <Req extends Request, Res extends Response> Res doRequest(String host,
                                                                            int port,
                                                                            Req request,
                                                                            Class<Res> responseClass) throws IOException {
        try(Socket socket = new Socket(host, port)) {
            final DataInputStream in = new DataInputStream(socket.getInputStream());
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(request.getOperation().getName());
            out.flush();
            out.writeUTF(Serializer.toJson(request));
            out.flush();

            return Serializer.fromJson(in.readUTF(), responseClass);
        } catch (IOException e) {
            throw new IOException("Failed to make request", e);
        }
    }
}

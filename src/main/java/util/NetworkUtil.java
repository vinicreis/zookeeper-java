package util;

import model.request.Request;
import model.response.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkUtil {
    /**
     * Opens a socket to the {@code host} and {@code port}, sends the {@code request} operation
     * as a header to tell the receiver which operation this request refers, and then, sends
     * the request in JSON format. After, listens for the JSON response from the Socket.
     * @param host receiver host address
     * @param port receiver port
     * @param request request to be sent to receiver
     * @param responseClass expected response class
     * @return returns a {@code Response} instance sent by the receiver
     * @param <Req> Request type parameter
     * @param <Res> Response type parameter
     * @throws IOException if an I/O error occurs
     */
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

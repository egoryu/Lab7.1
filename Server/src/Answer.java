import java.net.SocketAddress;

public class Answer {
    private Request request;
    private SocketAddress client;

    public Answer(Request request, SocketAddress client) {
        this.request = request;
        this.client = client;
    }

    public Request getRequest() {
        return request;
    }

    public SocketAddress getClient() {
        return client;
    }
}

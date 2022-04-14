import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;

import static java.lang.System.exit;

public class Server {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Analise analyst = new Analise();
        if (args.length == 0) {
            analyst.loadFile("");
        }
        else {
            analyst.loadFile(args[0]);
        }

        /*DatagramSocket server = null;
        try {
            server = new DatagramSocket(MyConstant.PORT);
        } catch (SocketException e) {
            System.out.println("Сокет не создан");
            exit(0);
        }*/

        DatagramChannel s = null;
        try {
            s = DatagramChannel.open();
        } catch (IOException e) {
            System.out.println("Канал не создан");
            exit(0);
        }

        InetSocketAddress iAdd = new InetSocketAddress(MyConstant.HOST, MyConstant.PORT);
        s.bind(iAdd);
        System.out.println("Server Started: " + iAdd);
        s.configureBlocking(false);

        while (!analyst.exit) {
            analyst.startAnalise(s);
        }
        s.close();
    }
}

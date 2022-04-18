
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Scanner;

import static java.lang.System.exit;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("Ошибка создания сокета");
            exit(0);
        }
        InetAddress inetAddress  = null;
        try {
            inetAddress = InetAddress.getByName(MyConstant.HOST);
        } catch (UnknownHostException e) {
            System.out.println("Ошибка определения адреса");
            exit(0);
        }

        datagramSocket.setSoTimeout(5000);
        Transfer transfer = new Transfer(new InetSocketAddress(inetAddress, MyConstant.PORT), datagramSocket);
        transfer.authorization();
        transfer.Start();
    }
}


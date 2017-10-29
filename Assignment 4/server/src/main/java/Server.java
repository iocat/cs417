import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;


public class Server {


    private static void fatal(String err) {
        System.err.println(err);
        System.exit(1);
    }

    private static final int INIT_UDP_PACKET_SIZE = 8; //bytes

    private static DatagramPacket makeAckPacket(SocketAddress client) {
        return new DatagramPacket(new byte[1], 1, client);
    }

    private static void udpServer(int port) throws SocketException {

        while (true) {
            int ack;
            int msg_size;
            int bytes_read = 0;
            int msg_read = 0;
            SocketAddress clientAddr;
            try {
                // Make a server socket
                DatagramSocket socket = new DatagramSocket(port);
                // Receive the init packet
                DatagramPacket initPacket = new DatagramPacket(new byte[INIT_UDP_PACKET_SIZE], INIT_UDP_PACKET_SIZE);
                socket.receive(initPacket);


                // Get client addr
                clientAddr = initPacket.getSocketAddress();

                // Read from the init packet
                ByteArrayInputStream initBytes = new ByteArrayInputStream(initPacket.getData());
                DataInputStream initReader = new DataInputStream(initBytes);
                ack = initReader.readInt();
                if (ack == 0){
                    System.out.println("ERR: UDP Streaming selected. Results will not be printed if the " +
                            "data (1GB) is not sent entirely. Please restart the server for each run in this " +
                            "configuration." +
                            ".");
                }
                msg_size = initReader.readInt();
                initReader.close();
                msg_read += 1;
                bytes_read += initPacket.getData().length;

                // ACK the init packet
                if (ack == 1) {
                    socket.send(makeAckPacket(clientAddr));
                }

                int count = 0;
                while (true) {
                    DatagramPacket received = new DatagramPacket(new byte[msg_size], msg_size);
                    socket.receive(received);
                    msg_read += 1;
                    bytes_read += received.getLength();
                    count += received.getLength();
                    if (ack == 1) {
                        socket.send(makeAckPacket(clientAddr));
                    }
                    if (count >= TOTAL_SIZE_TO_SEND) {
                        break;
                    }
                }
                if (ack == 0) {
                    System.out.println("Acknowledgement and protocol used: Pure streaming UDP");
                } else if (ack == 1) {
                    System.out.println("Acknowledgement and protocol used: Stop-and-wait UDP");
                }
                System.out.println("Number of messages read: " + msg_read + " messages.");
                System.out.println("Number of bytes read: " + bytes_read + " bytes.");
                socket.close();

            } catch (IOException e) {
                System.err.println("udp server: receive init packet: " + e.getMessage());
            }
        }
    }


    private static int TOTAL_SIZE_TO_SEND = 1 << 30; // 2^30 don't change

    private static void handleTCPClient(Socket socket) {
        int ack = 0;
        int msg_size = 0;
        int msg_read = 0;
        int bytes_read = 0;
        try {
            // Get and process the first message
            DataInputStream reader = new DataInputStream(socket.getInputStream());
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            ack = reader.readInt();
            msg_size = reader.readInt();
            msg_read += 1;
            bytes_read += 8;
            // ACK the first one
            if (ack == 1) {
                writer.writeByte(0);
                writer.flush();
            }

            int count = 0;
            while (true) {
                byte[] received = new byte[msg_size];
                int hasRead = reader.read(received, 0, msg_size);
                count += hasRead;
                msg_read += 1;
                bytes_read += hasRead;

                if (ack == 1) {
                    writer.writeByte(0);
                    writer.flush();
                }
                if (count >= TOTAL_SIZE_TO_SEND) {
                    break;
                }
            }
            reader.close();
            writer.close();
            socket.close();
            if (ack == 0) {
                System.out.println("Acknowledgement and protocol used: Pure streaming TCP");
            } else if (ack == 1) {
                System.out.println("Acknowledgement and protocol used: Stop-and-wait TCP");
            }
            System.out.println("Number of messages read: " + msg_read + " messages.");
            System.out.println("Number of bytes read: " + bytes_read + " bytes.");
        } catch (IOException e) {
            System.err.println("tcp client: error: " + e.getMessage());
            return;
        }
    }

    private static void tcpServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port, 5);
            while (true) {
                Socket sock = serverSocket.accept();
                new Thread(() -> handleTCPClient(sock)).start();
            }
        } catch (IOException e) {
            fatal("tcp server: " + e.getMessage());
        }
    }

    private static int tcpPort;

    private static int udpPort;

    public static void main(String args[]) throws Exception {
        if (args.length != 2) {
            fatal("Server: server needs 2 arguments " +
                    "\n" +
                    "\tUsage: ./server tcpPort udpPort");
        }
        tcpPort = Integer.parseInt(args[0]);
        udpPort = Integer.parseInt(args[1]);

        new Thread(() -> tcpServer(tcpPort)).start();
        udpServer(udpPort);
    }
}

import java.io.*;
import java.net.*;


public class Server {
	//this is a per-thread copy of the client socket
	Socket conn;

	//if we defined this static, then it would be shared among threads
	private static ServerSocket svc;

	Server(Socket sock) {
		this.conn = sock;	//store the socket for the connection
	}


	private static void fatal(String err){
		System.err.println(err);
		System.exit(1);
	}

	private static final int INIT_UDP_PACKET_SIZE = 20; //bytes

	private static DatagramPacket makeAckPacket(SocketAddress client){
		return new DatagramPacket(new byte[1], 1, client);
	}

	private static void udpServer(int port) throws  SocketException{

		while(true) {
			int ack;
			int msg_size;
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
				msg_size = initReader.readInt();
				initReader.close();

				if (ack == 1) {
					socket.send(makeAckPacket(clientAddr));
				}

				int count = 0;
				while (true) {
					DatagramPacket received = new DatagramPacket(new byte[msg_size], msg_size);
					socket.receive(received);
					count += received.getLength();
					if (ack == 1) {
						socket.send(makeAckPacket(clientAddr));
					}
					if (count >= TOTAL_SIZE_TO_SEND) {
						break;
					}
				}
				socket.close();
			} catch (IOException e) {
				System.err.println("udp server: receive init packet: " + e.getMessage());
			}
		}
	}


	private static int TOTAL_SIZE_TO_SEND = 1 << 30; // 2^30 don't change

	private static  void handleTCPClient(Socket socket){
		int ack;
		int msg_size;
		try {
			DataInputStream reader = new DataInputStream(socket.getInputStream());
			DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
			ack = reader.readInt();
			msg_size = reader.readInt();
			if (ack == 1){
				writer.writeByte(0);
				writer.flush();
			}
			int count = 0;
			while(true){
				byte[] received = new byte[msg_size];
				int temp = reader.read(received);
				count += temp;
				if(ack == 1){
					writer.writeByte(0);
					writer.flush();
				}
				if (count >=TOTAL_SIZE_TO_SEND){
					break;
				}
			}
			reader.close();
			writer.close();
			socket.close();
		}catch (IOException e){
			System.err.println("tcp client: error: "+ e.getMessage());
			return;
		}
	}

	private static void tcpServer(int port){
		try {
			ServerSocket serverSocket = new ServerSocket(port, 5);
			while(true){
				Socket sock = serverSocket.accept();
				new Thread(()->handleTCPClient(sock)).start();
			}
		}catch (IOException e){
			fatal("tcp server: "+ e.getMessage());
		}
	}

	private static int tcpPort;

	private static int udpPort;

	public static void main(String args[]) throws Exception {
		if (args.length != 2){
			fatal("Server: server needs 2 arguments " +
					"\n" +
					"\tUsage: ./server tcpPort udpPort");
		}
		tcpPort = Integer.parseInt(args[0]);
		udpPort = Integer.parseInt(args[1]);

		new Thread(()-> tcpServer(tcpPort)).start();
		udpServer(udpPort);
	}
}

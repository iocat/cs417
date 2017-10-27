import java.io.*;
import java.net.*;

public class Client {

	private static final int CLIENT_PORT_NUMBER = 12345;


	private static int TOTAL_SIZE_TO_SEND = 1 << 30; // 2^30 don't change
	private static double BYTES_IN_MB = 1 << 20;
	private static String serverHost ;
	private static int serverPort;
	private static String protocol;
	private static int ack;
	private static int msg_size;
	private static int bytes_count;

	private static int msg_count = 0;
	private static void fatal(String err){
		System.err.println(err);
		System.exit(1);
	}

	// the client sends out new-line-delimited byte chunk
	public static void main (String args[]) throws Exception {

		if (args.length != 5){
			fatal("Error: Client needs 5 arguments\n" +
					"\tUsage: ./client serverHost serverPort protocol ack messageSize");
		}
		serverHost = args[0];
		serverPort = Integer.parseInt(args[1]);
		protocol = args[2];
		ack = Integer.parseInt(args[3]);
		if (ack != 0 && ack != 1){
			fatal("Error: Acknowledgement argument should be either 0 or 1");
		}
		msg_size = Integer.parseInt(args[4]);
		if(msg_size <= 0 || msg_size > 65536){
			fatal("Error: Message size argument should be in the range of [1,65536]");
		}

		long startTime = System.currentTimeMillis();
		if (protocol.equals("tcp")){
			Socket socket = new Socket(serverHost, serverPort);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			out.writeInt(ack); // sends ack info
			out.writeInt(msg_size); // sends message size
			out.flush();
			msg_count += 1;
			bytes_count += 8;
			// get ack for the initial packet
			if(ack == 1){
				in.readByte();
			}

			int count = TOTAL_SIZE_TO_SEND;
			while(count > 0){
				byte[] sent = new byte[msg_size];
				out.write(sent);
				out.flush();
				if (ack == 1){
					in.readByte();
				}
				count -= msg_size;
				msg_count += 1;
				bytes_count += msg_size;
			}
		}else if (protocol.equals("udp")){

			// Set up a socket
			DatagramSocket socket = new DatagramSocket(CLIENT_PORT_NUMBER);

			// Construct the initial packet
			ByteArrayOutputStream initBytes = new ByteArrayOutputStream();
			DataOutputStream initWriter = new DataOutputStream(initBytes);
			initWriter.writeInt(ack);
			initWriter.writeInt(msg_size);
			initWriter.flush();
			byte[] initData = initBytes.toByteArray();
			DatagramPacket initPacket = new DatagramPacket(initData,initData.length,new InetSocketAddress
					(serverHost,serverPort));

			// Send the init packet
			socket.send(initPacket);
			msg_count += 1;
			bytes_count += initData.length;

			// wait for acknowledgement
			if(ack == 1){
				DatagramPacket initRes = new DatagramPacket(new byte[1], 0);
				socket.receive(initRes);
			}

			int count = TOTAL_SIZE_TO_SEND;
			while(count > 0 ){
				byte[] sent = new byte[msg_size];
				DatagramPacket packet = new DatagramPacket(sent,sent.length,new InetSocketAddress
						(serverHost,serverPort));
				socket.send(packet);
				if (ack == 1){
					DatagramPacket packetAck = new DatagramPacket(new byte[1], 1);
					socket.receive(packetAck);
				}
				count -= msg_size;
				bytes_count += sent.length;
				msg_count += 1;
			}
		}else{
			fatal("Error: The protocol is not supported");
		}
		long stopTime = System.currentTimeMillis();
		long elapsed = stopTime - startTime;
		double rate = ((double)bytes_count)/BYTES_IN_MB/((double) elapsed) *1000d;
		// Number of messages sent
		// Number of bytes sent
		// Total transmit time
		System.out.println("Number of messages sent: "+ msg_count+" messages.");
		System.out.println("Number of bytes sent: "+bytes_count+" bytes.");
		System.out.println("Total transmit time: "+ elapsed+" milliseconds.");
		System.out.println("Rate of transmission: "+ Math.round(rate)+ " Mbps." );
	}

}

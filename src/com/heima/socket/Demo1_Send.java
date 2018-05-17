package com.heima.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Demo1_Send {
	public static void main(String[] args) throws IOException {
		String sendMessage = "What are you doing?";
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket packet = new DatagramPacket(sendMessage.getBytes(), sendMessage.getBytes().length,
				InetAddress.getByName("127.0.0.1"), 6666);
		socket.send(packet);
		socket.close();
	}
}

package com.heima.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Demo3_MoreThred {
	public static void main(String[] args) {
		System.out.println("开始");
		new Receive().start();
		new Send().start();
	}
}

class Receive extends Thread {
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket(6666);
			DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
			for (;;) {
				socket.receive(packet);
				byte[] arr = packet.getData();
				int len = packet.getLength();
				String ip = packet.getAddress().getHostAddress();
				int port = packet.getPort();
				System.out.println(ip + ":" + port + ":" + new String(arr, 0, len));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Send extends Thread {
	public void run() {
		try {
			Scanner sc = new Scanner(System.in);
			DatagramSocket socket = new DatagramSocket();
			for (;;) {
				String sendMessage = sc.nextLine();
				if (("exit".equals(sendMessage)) || ("quit".equals(sendMessage))) {
					break;
				}
				DatagramPacket packet = new DatagramPacket(sendMessage.getBytes(), sendMessage.getBytes().length,
						InetAddress.getByName("127.0.0.1"), 6666);
				socket.send(packet);
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

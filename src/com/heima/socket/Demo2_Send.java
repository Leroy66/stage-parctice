package com.heima.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Demo2_Send {
	public static void main(String[] args)
		    throws IOException
		  {
		    Scanner sc = new Scanner(System.in);
		    DatagramSocket socket = new DatagramSocket();
		    for (;;)
		    {
		      String sendMessage = sc.nextLine();
		      if (("exit".equals(sendMessage)) || ("quit".equals(sendMessage))) {
		        break;
		      }
		      DatagramPacket packet = new DatagramPacket(sendMessage.getBytes(), sendMessage.getBytes().length, 
		        InetAddress.getByName("127.0.0.1"), 6666);
		      socket.send(packet);
		    }
		    socket.close();
		  }
}

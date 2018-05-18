package com.heima.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Demo1_Client {

	/**
	 * * 1.客户端 
	 * 创建Socket连接服务端(指定ip地址,端口号)
	 * 通过ip地址找对应的服务器
	 * 调用Socket的getInputStream()和getOutputStream()方法获取和服务端相连的IO流
	 * 输入流可以读取服务端输出流写出的数据 输出流可以写出数据到服务端的输入流
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("127.0.0.1", 9999);		//创建Socket指定ip地址和端口号
		InputStream is = socket.getInputStream();			//获取输入流
		OutputStream os = socket.getOutputStream();			//获取输出流
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		PrintStream ps = new PrintStream(os);               //PrintStream里有写出换行的方法
		
		System.out.println(br.readLine());					//有还行才会打印
		ps.println("我想报名就业班");
		System.out.println(br.readLine());
		ps.println("爷不学了");
		socket.close();
	}
}

package com.heima.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Demo1_Server {
	
	/**
	 * * 2.服务端 
	 * 创建ServerSocket(需要指定端口号)
	 * 调用ServerSocket的accept()方法接收一个客户端请求，得到一个Socket
	 * 调用Socket的getInputStream()和getOutputStream()方法获取和客户端相连的IO流
	 * 输入流可以读取客户端输出流写出的数据 输出流可以写出数据到客户端的输入流
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//singleMethod();---单线程
		threadMethod();//---多线程
	}

	private static void threadMethod() throws IOException {
		ServerSocket server = new ServerSocket(9999);			//创建服务器
		while(true) {											//一直监听客户端
			final Socket socket = server.accept();				//接受客户端的请求
			new Thread() {										//匿名内部类,socket必须final修饰
				public void run() {
					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						PrintStream ps = new PrintStream(socket.getOutputStream());
						ps.println("欢迎咨询传智播客");
						System.out.println(br.readLine());
						ps.println("报满了,请报下一期吧");
						System.out.println(br.readLine());
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	private static void singleMethod() throws IOException {
		ServerSocket server = new ServerSocket(9999);	//创建服务器
		Socket socket = server.accept();				//接受客户端的请求
		InputStream is = socket.getInputStream();		//获取输入流
		OutputStream os = socket.getOutputStream();		//获取输出流
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		PrintStream ps = new PrintStream(os);
		
		ps.println("欢迎咨询传智播客");                      //println---不能是print
		System.out.println(br.readLine());
		ps.println("报满了,请报下一期吧");
		System.out.println(br.readLine());
		server.close();
		socket.close();
	}

}

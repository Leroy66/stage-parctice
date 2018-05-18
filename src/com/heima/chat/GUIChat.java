package com.heima.chat;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GUIChat extends Frame {

	private Button log;
	private Button send;
	private Button clear;
	private Button shake;
	private TextField text;
	private TextArea viewText;
	private TextArea sendText;
	private DatagramSocket socket;
	private BufferedWriter bw;

	public static void main(String[] args) {
		new GUIChat();
	}

	public GUIChat() {
		init();			//初始化
		southPanel();	//南边的窗口
		centerPanel();	//中间的窗口
		event();		//监听事件
	}

	// 事件监听
	public void event() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {// 点击关闭窗口
				socket.close();
				try {
					bw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});

		// 监听发送按钮
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try {
					send();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		});

		// 监听记录按钮
		log.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					logFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// 监听清屏按钮
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewText.setText("");
			}
		});

		// 监听震动
		shake.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {//点击震动发送-1,接收的审核判断是不是-1,是-1接收端才震动
				 try {
					 DatagramPacket packet = send(new byte[]{-1},text.getText());//发送一个-1,和ip
					socket.send(packet);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	
		//监听发送区发送快捷键
		sendText.addKeyListener(new KeyAdapter() {//监听键盘事件
			@Override
			public void keyReleased(KeyEvent e) {
//				if(e.getKeyCode()==KeyEvent.VK_ENTER && e.isControlDown()){//enter+ctrl
				if(e.getKeyCode()==KeyEvent.VK_ENTER){//enter
					try {
						send();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
	}

	private void logFile() throws IOException {
		bw.flush();// 刷新缓冲区
		FileInputStream fis = new FileInputStream("config.txt");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();// 在内存中设置缓冲区

		int len;
		byte[] arr = new byte[8192];
		while ((len = fis.read(arr)) != -1) {
			baos.write(arr, 0, len);
		}
		String str = baos.toString(); // 将内存里的内容转换成字符串
		viewText.setText(str); // append会重复,所以是set

		fis.close();
	}

	public void send() throws IOException {
		String message = sendText.getText();// 获取发送区域的内容
		String ip = text.getText(); // 获取ip
		ip=ip.trim().length()==0?"255.255.255.255":ip;
		
		DatagramPacket packet = send(message.getBytes(), ip);
		socket.send(packet);
		String time = getCurrentTime();
		String str = time + " 我对 " + (ip.equals("255.255.255.255")?"所有人":"ip") + "说:\r\n " + message + "\r\n";
		viewText.append(str);
		bw.write(str);
		sendText.setText("");
	}

	private DatagramPacket send(byte[] arr, String ip) throws UnknownHostException {
		DatagramPacket packet = new DatagramPacket(arr, arr.length, InetAddress.getByName(ip), 9999);
		return packet;
	}

	private String getCurrentTime() {
		Date d = new Date();
		SimpleDateFormat sfd = new SimpleDateFormat("yyyy年MM月dd月 HH:mm:ss");
		return sfd.format(d);
	}

	public void centerPanel() {
		Panel center = new Panel(); // 创建中间的Panel
		viewText = new TextArea();
		sendText = new TextArea(5, 1);
		center.setLayout(new BorderLayout()); // Panel默认是流式布局，这里需要设置为边界布局管理器
		center.add(sendText, new BorderLayout().SOUTH);
		center.add(viewText, new BorderLayout().CENTER);
		viewText.setEditable(false); // 设置不可编辑
		viewText.setBackground(Color.WHITE); // 设置背景颜色
		viewText.setFont(new Font("xxx", Font.PLAIN, 15)); // 设置字体大小
		sendText.setFont(new Font("xxx", Font.PLAIN, 15));
		this.add(center, BorderLayout.CENTER);
	}

	public void southPanel() {
		Panel south = new Panel(); // 创建南边的Panel
		text = new TextField(15);
		text.setText("127.0.0.1");
		send = new Button("send");
		log = new Button("log");
		clear = new Button("clear");
		shake = new Button("shake");
		south.add(text);
		south.add(send);
		south.add(log);
		south.add(clear);
		south.add(shake);
		this.add(south, BorderLayout.SOUTH); // 将south panel 放到fram里
	}

	private void init() {
		this.setLocation(500, 50);
		this.setSize(400, 600);
		new Receive().start();
		try {
			socket = new DatagramSocket();
			bw = new BufferedWriter(new FileWriter("config.txt", true));// true代表追加内容
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setVisible(true);
	}

	private class Receive extends Thread {
		public void run() {

			DatagramSocket socket;
			try {
				socket = new DatagramSocket(9999);
				DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
				while (true) {
					socket.receive(packet);
					byte[] arr = packet.getData();
					int len = packet.getLength();
					if(arr[0]==-1 && len==1){
						shake();
						continue;
					}
					String message = new String(arr, 0, len);
					String time = getCurrentTime();
					String ip = packet.getAddress().getHostAddress();
					String str = time + " " + ip + " 对我说:\r\n " + message + "\r\n";
					viewText.append(str);
					bw.write(str);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	private void shake() {
		int x=this.getLocation().x;
		int y=this.getLocation().y;
		for(int i=0;i<10;i++){
			try {
				this.setLocation(x+10, y+10);
				Thread.sleep(20);
				this.setLocation(x+10, y-10);
				Thread.sleep(20);
				this.setLocation(x-10, y+10);
				Thread.sleep(20);
				this.setLocation(x-10, y-10);
				Thread.sleep(20);
				this.setLocation(x, y);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

package com.chatroom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

public class ServerMainThread extends Thread {
	private int id = 0;
	private ServerSocket serverSocket;
	private int port;
	private Server sFrame;
	public Vector<String> onlineUser = new Vector<String>(10, 5);
	public Vector<Socket> socketUser = new Vector<Socket>(10, 5);
	
	//在用户列表中加入一个用户
	public String addUser(String name, Socket socket){
		
		name = name+"#"+(id++);//防止重名
		onlineUser.addElement(name);
		socketUser.addElement(socket);
		return name;
	}
	
	//在用户列表中删除一个用户
	public boolean deleteUser(String name){
		int i = onlineUser.indexOf(name);
		onlineUser.remove(i);
		socketUser.remove(i);
		return true;
	}

	public ServerMainThread(int p, Server sFrame) {
		this.port = p;
		this.sFrame = sFrame;
		try {
			serverSocket = new ServerSocket(port);
			this.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		listen();
	}
	
	//等待客户端接入，并启动一个新的线程与之交互
	public void listen() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				ServerMessageThread smt = new ServerMessageThread(socket, sFrame, this);
				smt.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}

class ServerMessageThread extends Thread {

	private Socket socket;// 定义客户端套接字

	private BufferedReader in;// 定义输入流
	private PrintWriter out;// 定义输出流
	
	private ServerMainThread serverMainThread;


	private String strReceive, strKey;
	private StringTokenizer st;

	private Server sFrame;

	@Override
	public void run() {
		try {
			String lgmsg = in.readLine();
			StringTokenizer sttmp = new StringTokenizer(lgmsg, "|");
			String key = sttmp.nextToken();
			
			//客户端发送的为登陆消息
			if(key.equals("login")){
				String name = sttmp.nextToken();
				String trueName = serverMainThread.addUser(name, socket);
				out.println("name|"+trueName);
			}
			freshUser();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		while (true) {
			try {
				strReceive = in.readLine();
				st = new StringTokenizer(strReceive, "|");
				strKey = st.nextToken();
				
				//客户端发送的为对话消息
				if (strKey.equals("talk")) {
					talk();
				} 
				//客户端发送的为退出消息
				else if(strKey.equals("delete")){
					serverMainThread.deleteUser(st.nextToken());
					freshUser();
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	//刷新自己与所有客户端的用户列表
	public void freshUser(){
		String[] names = new String[100];
		String msg = "users";
		Vector<String> onlineUser = serverMainThread.onlineUser;
		for(int i = 0;i<onlineUser.size();i++){
			String name = onlineUser.elementAt(i);
			msg = msg+"|"+name;
			names[i] = name;
		}
		sFrame.setUserList(names);
		
		for(Socket soc : serverMainThread.socketUser){
			PrintWriter outSend;
			try {
				outSend = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(soc.getOutputStream())),
						true);
				outSend.println(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	//将消息发送到所有的客户端
	public void talk(){
		String name = st.nextToken();
		String msg = st.nextToken();
		for(Socket soc : serverMainThread.socketUser){
			PrintWriter outSend;
			try {
				outSend = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(soc.getOutputStream())),
						true);
				outSend.println("talk|"+name+"|" + msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}

	public ServerMessageThread(Socket socket, Server sFrame, ServerMainThread serverMainThread) {
		
		this.socket = socket;
		this.sFrame = sFrame;
		this.serverMainThread = serverMainThread;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);// 客户端输出
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

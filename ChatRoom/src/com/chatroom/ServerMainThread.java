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
	
	public String addUser(String name, Socket socket){
		
		name = name+"#"+(id++);
		onlineUser.addElement(name);
		socketUser.addElement(socket);
		return name;
	}
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		listen();
	}

	public void listen() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				ServerMessageThread smt = new ServerMessageThread(socket, sFrame, this);
				smt.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
			//System.out.println(lgmsg);
			StringTokenizer sttmp = new StringTokenizer(lgmsg, "|");
			String key = sttmp.nextToken();
			if(key.equals("login")){
				String name = sttmp.nextToken();
				String trueName = serverMainThread.addUser(name, socket);
				out.println("name|"+trueName);
			}
			freshUser();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while (true) {
			try {
				strReceive = in.readLine();
				st = new StringTokenizer(strReceive, "|");
				strKey = st.nextToken();
				if (strKey.equals("talk")) {
					talk();
				} 
				else if(strKey.equals("delete")){
					serverMainThread.deleteUser(st.nextToken());
					freshUser();
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	public void talk(){
		String name = st.nextToken();
		String msg = st.nextToken();
		//System.out.println(name+": " + msg);
		for(Socket soc : serverMainThread.socketUser){
			PrintWriter outSend;
			try {
				outSend = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(soc.getOutputStream())),
						true);
				outSend.println("talk|"+name+"|" + msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 客户端接收
	}

}

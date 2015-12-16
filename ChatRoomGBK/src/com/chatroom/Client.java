package com.chatroom;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends Thread {
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 4625662251265921723L;
	String name;
	String trueName;
	JFrame loginFrame;
	JLabel ipLabel;
	JLabel portLabel;
	JLabel nameLabel;
	JTextField ipField;
	JTextField portField;
	JTextField nameField;
	JButton loginBtn;
	JPanel southLoginPanel;
	JPanel northLoginPanel;

	JFrame topFrame;
	JPanel northPanel;
	JPanel midPanel;
	JPanel southPanel;
	JPanel southBtnPanel;
	JLabel rcdLabel;
	JLabel listLabel;
	JTextArea rcdText;
	JList<String> userList;
	JScrollPane userListSP;
	JTextField messageBar;
	JButton sendBtn;
	JButton clearBtn;
	Socket socket;
	BufferedReader in;
	PrintWriter out;

	public void initFrame() {
		loginFrame = new JFrame("登陆");
		ipLabel = new JLabel("服务器地址：");
		portLabel = new JLabel("端口号：");
		nameLabel = new JLabel("用户名：");
		loginBtn = new JButton("登陆");

		ipField = new JTextField(5);
		ipField.setText("127.0.0.1");
		portField = new JTextField(5);
		portField.setText("8888");
		nameField = new JTextField(5);
		//nameField.setText("qweasd");

		northLoginPanel = new JPanel(new GridLayout(3, 3));
		southLoginPanel = new JPanel();

		northLoginPanel.add(ipLabel);
		northLoginPanel.add(ipField);
		northLoginPanel.add(portLabel);
		northLoginPanel.add(portField);
		northLoginPanel.add(nameLabel);
		northLoginPanel.add(nameField);

		southLoginPanel.add(loginBtn);

		loginFrame.add(southLoginPanel, BorderLayout.SOUTH);
		loginFrame.add(northLoginPanel, BorderLayout.NORTH);
		loginFrame.pack();
		
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;

        loginFrame.setLocation(width / 2-150 , height / 2-150 );
        loginFrame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
        	
		});
		loginFrame.setVisible(true);
		loginBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = ipField.getText();
				int port = Integer.valueOf(portField.getText());
				name = nameField.getText();
				try {
					socket = new Socket(ip, port);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
					loginFrame.setVisible(false);
					startClient();

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		topFrame = new JFrame("客户端");
		northPanel = new JPanel(new GridLayout(1, 2));
		midPanel = new JPanel(new GridLayout(1, 2));
		southPanel = new JPanel(new BorderLayout());
		southBtnPanel = new JPanel(new FlowLayout());
		rcdLabel = new JLabel("聊天记录：");
		listLabel = new JLabel("在线用户：");
		rcdText = new JTextArea(30, 30);
		userList = new JList<String>();
		userListSP = new JScrollPane();
		messageBar = new JTextField(30);
		sendBtn = new JButton("发送");
		clearBtn = new JButton("清除");
		
		rcdText.setEditable(false);

		userListSP.getViewport().setView(userList);
		
		clearBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rcdText.setText("");
				
			}
		});

		sendBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String message = messageBar.getText();
				// System.out.println(message);
				out.println("talk|" + trueName + '|' + message);
				messageBar.setText("");
				// System.out.println(message);
				// out.flush();

			}
		});

		northPanel.add(rcdLabel);
		northPanel.add(listLabel);
		midPanel.add(rcdText);
		midPanel.add(userListSP);
		southBtnPanel.add(sendBtn);
		southBtnPanel.add(clearBtn);
		southPanel.add(messageBar, BorderLayout.WEST);
		southPanel.add(southBtnPanel, BorderLayout.EAST);

		topFrame.add(northPanel, BorderLayout.NORTH);
		topFrame.add(midPanel, BorderLayout.CENTER);
		topFrame.add(southPanel, BorderLayout.SOUTH);
		topFrame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				out.println("delete|" + trueName);
				System.exit(0);
			}

		});
		// topFrame.setSize(500, 600);
		topFrame.pack();
		topFrame.setLocation(width / 2-350 , height / 2-350 );
		// topFrame.setVisible(true);
	}

	public void startClient() {
		topFrame.setVisible(true);
		out.println("login|" + name);
		try {
			String str = in.readLine();
			StringTokenizer st = new StringTokenizer(str, "|");
			String strKey = st.nextToken();

			if (strKey.equals("name")) {
				trueName = st.nextToken();
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String strRec = in.readLine();
			StringTokenizer st = new StringTokenizer(strRec, "|");
			String strKey = st.nextToken();

			if (strKey.equals("users")) {
				String[] names = new String[100];
				int i = 0;
				while (st.hasMoreTokens()) {
					String name = st.nextToken();
					names[i++] = name;
				}

				userList.setListData(names);

			}
			this.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				String strRec;
				try {
					strRec = in.readLine();
					StringTokenizer st = new StringTokenizer(strRec, "|");
					String strKey = st.nextToken();
					if (strKey.equals("talk")) {
						String name = st.nextToken();
						String msg = st.nextToken();
						rcdText.append(name + ": " + msg + '\n');
					} else if (strKey.equals("users")) {
						String[] names = new String[100];
						int i = 0;
						while (st.hasMoreTokens()) {
							String name = st.nextToken();
							names[i++] = name;
						}

						userList.setListData(names);

					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.initFrame();
	}

}
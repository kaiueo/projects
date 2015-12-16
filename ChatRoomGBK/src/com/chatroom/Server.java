package com.chatroom;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class Server {
	private JFrame topFrame;
	private JPanel northPanel;
	private JPanel midPanel;
	private JPanel southPanel;
	private JLabel listLabel;
	private JLabel portLabel;
	private JScrollPane userListSP;
	private JList<String> userList;
	private JTextField portText;
	private JButton startBtn;
	private JPanel midSouthPanel;
	private JPanel midNorthPanel;
	private Server sFrame;
	
	public void initFrame(){
		
		topFrame = new JFrame("服务器");
		northPanel = new JPanel();
		midPanel = new JPanel(new BorderLayout());
		southPanel = new JPanel();
		midNorthPanel = new JPanel();
		midSouthPanel = new JPanel();
		
		listLabel = new JLabel("当前用户：");
		portLabel = new JLabel("端口号：");
		userListSP = new JScrollPane();
		userList = new JList<String>();
		portText = new JTextField(5);
		portText.setText("8888");
		startBtn = new JButton("启动服务器");
		userListSP.getViewport().setView(userList);
		
		northPanel.add(listLabel);
		midNorthPanel.add(userListSP);
		midSouthPanel.add(portLabel);
		midSouthPanel.add(portText);
		midPanel.add(midNorthPanel, BorderLayout.NORTH);
		midPanel.add(midSouthPanel, BorderLayout.SOUTH);
		southPanel.add(startBtn);
		topFrame.add(northPanel, BorderLayout.NORTH);
		topFrame.add(southPanel, BorderLayout.SOUTH);
		topFrame.add(midPanel, BorderLayout.CENTER);
		startBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int port;
				String sPort = portText.getText();
				try{
					port = Integer.valueOf(sPort);
					portLabel.setText("端口号：");
					new ServerMainThread(port, sFrame);
					sFrame.startBtn.setEnabled(false);
				}
				catch(Exception e1){
					portLabel.setText("请填写正确的端口号！！！");
				}
				
				
			}
		});
		topFrame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			
		});
		
		//使窗口处在桌面的靠近中间位置
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;

        topFrame.setLocation(width / 2-150 , height / 2-150 );
		topFrame.pack();
		topFrame.setVisible(true);
		
		
		
	}
	
	public void setUserList(String[] names){
		userList.setListData(names);
	}
	
	public Server(){
		sFrame = this;
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.initFrame();
	}
}


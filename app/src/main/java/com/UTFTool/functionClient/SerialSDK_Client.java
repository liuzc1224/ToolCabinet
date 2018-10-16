package com.UTFTool.functionClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.UTFTool.functionUtils.SerialSDK_Commons;

public class SerialSDK_Client {

	private static ServerSocket server;
	private Socket clinetSocket;
	private static final int port = 7880;
	private static SerialSDK_Commons comm = new SerialSDK_Commons();
	private SerialClientThread client = null;

	private static SerialSDK_Client sdkClient = new SerialSDK_Client();

	public SerialSDK_Client() {

	}

	public void onCreate() {
		try {
			server = new ServerSocket(port);
			while(true) {
				clinetSocket = server.accept();
				new Thread(client = new SerialClientThread(clinetSocket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeClient_Serial() throws IOException {
		if (clinetSocket != null)
			clinetSocket.close();
		if (server != null)
			server.close();
	}

	public void setOnDataReceiveListener(OnDataReceiveListener onDataReceiveListener) {
		client.setOnDataReceiveListener(onDataReceiveListener);
	}

	public void setStartInventory() throws IOException {
		client.setStartInventory();
	}

	public void setCanceInventory() throws IOException {
		client.setCanceInventory();
	}
	
//	public static void main(String[] args) {
//		try {
//			sdkClient.onCreate(); //连接读写器
//			SocketSDK_RFID rfid = new SocketSDK_RFID();
//			sdkClient.setDataListener(rfid);
//			Thread.sleep(1000);
//			sdkClient.client.setStartInventory();  // 开始盘点
//			Thread.sleep(1000*600);
//			sdkClient.client.setCanceInventory(); //停止盘点
////			sdkClient.client.WriteFRID();
//		} catch (Exception EX) {
//         System.out.println(EX.getMessage());
//		}
//
//	}

	
}

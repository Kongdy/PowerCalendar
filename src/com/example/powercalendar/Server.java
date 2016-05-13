package com.example.powercalendar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Server extends Thread {

	Socket socket;
	boolean HAVE = false;
	String clientName;
	DataInputStream in = null;
	DataOutputStream out = null;
	String s = null;
	ClientList clientList;
	ClientNode pC;

	// 重载构造方法
	Server(Socket soc, ClientList list) {
//		获取socket
		socket = soc;
		// 通过通信地址设置线程名称
		setName(String.valueOf(socket.getInetAddress()));
		System.out.println(String.valueOf(socket.getInetAddress()));
		// 客户端链表
		clientList = list;
		clientName = String.valueOf(socket.getInetAddress());
		try {
			// 获取通信的输入输出流
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
		}
	}

	public void run() {
		// 初始化链表
		clientList.inList(this);
		// 获得通信链表中的第一个线程
		pC = clientList.getHead();
		pC = pC.next;
		System.out.println("客户列表：\n");
		// 循环输出所有通信线程名字，作为客户的名字使用,例如：79.54.12.1
		while (pC != null) {
			System.out.println("" + pC.thread.getName());
			pC = pC.next;
		}
		try {
			// 接收通信线程输入流的第一个单位长度(几个字节)的文件，并且并且转换成UTF格式的字符串
			s = in.readUTF();
		} catch (IOException e) {
		}
		// 判断是否是群发
		String str = "#toAll";
		if (s.equals(str)) {
			// 接收多个客户端发送来的信息
			while (true) {
				try {
					// 执行死循环，保持通信线程，每次从输入流读进来固定单位长度的数据，长度大小如上
					s = in.readUTF();
					// 不停的获取客户端发送过来的数据
					pC = clientList.getHead();
					pC = pC.next;
					while (pC != null) {
						// 防止发生数据重复发送
						if (pC.thread == Thread.currentThread()) {
							pC = pC.next;
						} else {
							// 打印接收到的信息
							pC.thread.out.writeUTF(getName() + "说：\n" + s
									+ "\n\n");
							pC = pC.next;
						}
					}
				}
				// 输入输出流出错时，停止通信线程
				catch (IOException e) {
					System.out.println("客户离开了");
					return;
				}
			}
		} else {
			// 接收单个客户端发送来的信息
			pC = clientList.getHead();
			int n = Integer.parseInt(s);
			System.out.println("" + n);
			for (int i = 0; i < n; i++)
				pC = pC.next;
			// 将客户端发送的所有信息循环打印出来,每次clientList指向下个节点，直到下个节点为空，结束，并且输出链接失败
			if (pC == null) {
				try {
					out.writeUTF("与好友连接失败！");
					return;
				} catch (IOException e) {
				}
			} else {
				while (true) {
					try {
						s = in.readUTF();
						pC.thread.out.writeUTF(getName() + "说：\n" + s + "\n\n");
					} catch (IOException e) {
						return;
					}
				}
			}
		}
	}
}

class ClientNode {
	Server thread;
	ClientNode next;
}

// 链表
class ClientList {
	ClientNode pHead = null, p = null, pLast = null;

	ClientList() {
		pHead = new ClientNode();
		pLast = pHead;
	}

	// 初始化链表
	public void inList(Server thread) {
		p = new ClientNode();
		p.thread = thread;
		pLast.next = p;
		pLast = p;
		pLast.next = null;
	}

	// 获取头元素
	ClientNode getHead() {
		return pHead;
	}

	// 获取指定位置的元素
	ClientNode getClient(int i) {
		ClientNode pC = pHead;
		for (int j = 0; j < i; j++) {
			pC = pC.next;
		}
		return pC;
	}
}
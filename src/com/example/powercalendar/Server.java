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

	// ���ع��췽��
	Server(Socket soc, ClientList list) {
//		��ȡsocket
		socket = soc;
		// ͨ��ͨ�ŵ�ַ�����߳�����
		setName(String.valueOf(socket.getInetAddress()));
		System.out.println(String.valueOf(socket.getInetAddress()));
		// �ͻ�������
		clientList = list;
		clientName = String.valueOf(socket.getInetAddress());
		try {
			// ��ȡͨ�ŵ����������
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
		}
	}

	public void run() {
		// ��ʼ������
		clientList.inList(this);
		// ���ͨ�������еĵ�һ���߳�
		pC = clientList.getHead();
		pC = pC.next;
		System.out.println("�ͻ��б�\n");
		// ѭ���������ͨ���߳����֣���Ϊ�ͻ�������ʹ��,���磺79.54.12.1
		while (pC != null) {
			System.out.println("" + pC.thread.getName());
			pC = pC.next;
		}
		try {
			// ����ͨ���߳��������ĵ�һ����λ����(�����ֽ�)���ļ������Ҳ���ת����UTF��ʽ���ַ���
			s = in.readUTF();
		} catch (IOException e) {
		}
		// �ж��Ƿ���Ⱥ��
		String str = "#toAll";
		if (s.equals(str)) {
			// ���ն���ͻ��˷���������Ϣ
			while (true) {
				try {
					// ִ����ѭ��������ͨ���̣߳�ÿ�δ��������������̶���λ���ȵ����ݣ����ȴ�С����
					s = in.readUTF();
					// ��ͣ�Ļ�ȡ�ͻ��˷��͹���������
					pC = clientList.getHead();
					pC = pC.next;
					while (pC != null) {
						// ��ֹ���������ظ�����
						if (pC.thread == Thread.currentThread()) {
							pC = pC.next;
						} else {
							// ��ӡ���յ�����Ϣ
							pC.thread.out.writeUTF(getName() + "˵��\n" + s
									+ "\n\n");
							pC = pC.next;
						}
					}
				}
				// �������������ʱ��ֹͣͨ���߳�
				catch (IOException e) {
					System.out.println("�ͻ��뿪��");
					return;
				}
			}
		} else {
			// ���յ����ͻ��˷���������Ϣ
			pC = clientList.getHead();
			int n = Integer.parseInt(s);
			System.out.println("" + n);
			for (int i = 0; i < n; i++)
				pC = pC.next;
			// ���ͻ��˷��͵�������Ϣѭ����ӡ����,ÿ��clientListָ���¸��ڵ㣬ֱ���¸��ڵ�Ϊ�գ������������������ʧ��
			if (pC == null) {
				try {
					out.writeUTF("���������ʧ�ܣ�");
					return;
				} catch (IOException e) {
				}
			} else {
				while (true) {
					try {
						s = in.readUTF();
						pC.thread.out.writeUTF(getName() + "˵��\n" + s + "\n\n");
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

// ����
class ClientList {
	ClientNode pHead = null, p = null, pLast = null;

	ClientList() {
		pHead = new ClientNode();
		pLast = pHead;
	}

	// ��ʼ������
	public void inList(Server thread) {
		p = new ClientNode();
		p.thread = thread;
		pLast.next = p;
		pLast = p;
		pLast.next = null;
	}

	// ��ȡͷԪ��
	ClientNode getHead() {
		return pHead;
	}

	// ��ȡָ��λ�õ�Ԫ��
	ClientNode getClient(int i) {
		ClientNode pC = pHead;
		for (int j = 0; j < i; j++) {
			pC = pC.next;
		}
		return pC;
	}
}
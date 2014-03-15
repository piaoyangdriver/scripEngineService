package cn.com.sony.csc.sqa.mbt.scriptengine.thread;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import cn.com.sony.csc.sqa.mbt.bean.CommandBean;
import cn.com.sony.csc.sqa.mbt.bean.ExecuteTestCaseBean;
import cn.com.sony.csc.sqa.mbt.scriptengine.run.DataProvider;

/**
 * 接收ExecuteTestCaseBean
 * 
 * 
 * @author Williams Ding
 * @version 1.0
 * 
 */
public class ReceiveQueueProducer implements Runnable {

	private ServerSocket serverSocket = null;

	/**
	 * default constructor method
	 */
	public ReceiveQueueProducer() {
		try {
			serverSocket = new ServerSocket(DataProvider.LISTEN_PORT);
		} catch (IOException e) {
			System.out.println("--- ReceiveQueueProducer init error, cannot get ServerSocket!");
			e.printStackTrace();
			System.out.println("---");
		}
	}

	/**
	 * implement from Runnable interface
	 */
	@Override
	public void run() {
		while (true) {
			doListen();
		}

	}

	/**
	 * 接收ExecuteTestCaseBean
	 */
	private void doListen() {
		Socket socket = null;
		ObjectInputStream ois = null;
		BufferedOutputStream bos = null;
		try {
			socket = serverSocket.accept();
			// input
			ois = new ObjectInputStream(socket.getInputStream());
			Object obj = ois.readObject();
			if (null == obj)
				return;
			if (obj instanceof ExecuteTestCaseBean)
				DataProvider.RECEIVE_QUEUE.add((ExecuteTestCaseBean) obj);
			else if (obj instanceof CommandBean) {
				CommandBean commandBean = (CommandBean) obj;
				DataProvider.COMMAND_MAP.put(commandBean.getExecuteTestCaseId(), commandBean);
			} else {
				System.out.println("--- WARN: what is this obj? ");
				return;
			}
			socket.shutdownInput();
			// output
			bos = new BufferedOutputStream(socket.getOutputStream());
			String response = "OK";
			bos.write(response.getBytes("UTF-8"));
			bos.flush();
			socket.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != bos)
					bos.close();
				if (null != ois)
					ois.close();
				if (null != socket)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}

package cn.com.sony.csc.sqa.mbt.scriptengine.thread;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import cn.com.sony.csc.sqa.mbt.bean.ExecuteStatementBean;
import cn.com.sony.csc.sqa.mbt.scriptengine.run.DataProvider;

/**
 * 向WebServer端发送StatementResultBean
 * 
 */

public class SendQueueConsumer implements Runnable {

	
	private void doSend() {
		ExecuteStatementBean seb = null;
		try {
			seb = DataProvider.SEND_QUEUE.take();
			send(seb);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param esb
	 * @return
	 */
	private boolean send(ExecuteStatementBean esb) {
		Socket socket = null;
		ObjectOutputStream oos = null;
		BufferedInputStream bis = null;

		try {
			socket = new Socket(DataProvider.WEB_SERVER_IP, DataProvider.WEB_SERVER_PORT);

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(esb);
			oos.flush();
			socket.shutdownOutput();

			bis = new BufferedInputStream(socket.getInputStream());
			byte[] inputBuffer = new byte[100];
			int readLength = bis.read(inputBuffer);
			String response = new String(inputBuffer, 0, readLength, "UTF-8");
			socket.shutdownInput();

			if (response.equals("OK")) {
				return true;
			} else {
				return false;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {

			try {
				if (null != bis)
					bis.close();
				if (null != oos)
					oos.close();
				if (null != socket)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void run() {
		while (true) {
			doSend();
		}

	}

}

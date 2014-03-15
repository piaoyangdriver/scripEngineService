package cn.com.sony.csc.sqa.mbt.scriptengine.run;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import cn.com.sony.csc.sqa.mbt.bean.CommandBean;
import cn.com.sony.csc.sqa.mbt.bean.ExecuteStatementBean;
import cn.com.sony.csc.sqa.mbt.bean.ExecuteTestCaseBean;

/**
 * 定义执行器端的接收队列和发送队列，以及一些常量信息
 * 
 */
public class DataProvider {

	/**
	 * RECEIVE_QUEUE
	 */
	public static BlockingQueue<ExecuteTestCaseBean> RECEIVE_QUEUE = new LinkedBlockingQueue<ExecuteTestCaseBean>();

	/**
	 * SEND_QUEUE
	 */
	public static BlockingQueue<ExecuteStatementBean> SEND_QUEUE = new LinkedBlockingQueue<ExecuteStatementBean>();

	/**
	 * COMMAND_MAP
	 * key: ExecuteTestCase id
	 * value: CommandBean
	 */
	public static ConcurrentHashMap<Integer, CommandBean> COMMAND_MAP = new ConcurrentHashMap<Integer, CommandBean>();

	/**
	 * scirpt file path
	 */
	public static String SCRIPT_FILE;

	/**
	 * log file path
	 */
	public static String LOG_FILE;

	/**
	 * COMMAND_LINE
	 */
	public static String COMMAND_LINE;

	/**
	 * LISTEN_PORT
	 */
	public static int LISTEN_PORT;

	/**
	 * WEB_SERVER_IP
	 */
	public static String WEB_SERVER_IP;

	/**
	 * WEB_SERVER_PORT
	 */
	public static int WEB_SERVER_PORT;

	static {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("scriptengine.properties"));
			SCRIPT_FILE = properties.getProperty("scirpt_file");
			LOG_FILE = properties.getProperty("log_file");
			COMMAND_LINE = properties.getProperty("command_line");
			LISTEN_PORT = Integer.parseInt(properties.getProperty("listen_port"));
			WEB_SERVER_IP = properties.getProperty("web_server_ip");
			WEB_SERVER_PORT = Integer.parseInt(properties.getProperty("web_server_port"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

}

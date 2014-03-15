package cn.com.sony.csc.sqa.mbt.scriptengine.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import cn.com.sony.csc.sqa.mbt.bean.CommandBean;
import cn.com.sony.csc.sqa.mbt.bean.ExecuteStatementBean;
import cn.com.sony.csc.sqa.mbt.bean.ExecuteTestCaseBean;
import cn.com.sony.csc.sqa.mbt.constant.Command;
import cn.com.sony.csc.sqa.mbt.constant.ExecuteStatementStatus;
import cn.com.sony.csc.sqa.mbt.scriptengine.run.DataProvider;

/**
 * 执行脚本内容，并添加到发送队列
 * 
 */

public class ReceiveQueueConsumer implements Runnable {

	@Override
	public void run() {
		while (true) {
			doExecute();
		}
	}

	/**
	 * real execute method
	 */
	private void doExecute() {

		ExecuteTestCaseBean executeTestCaseBean = null;
		try {
			executeTestCaseBean = DataProvider.RECEIVE_QUEUE.take();
			System.out.println("--- INFO: Going to Execute ExecuteTestCaseBean, id is: " + executeTestCaseBean.getExecuteTestCaseId());
			for (ExecuteStatementBean esb : executeTestCaseBean.getExecuteStatementBeanList()) {
				boolean flag = execStatementBean(esb);
				DataProvider.SEND_QUEUE.put(esb);
				if (flag) {
					System.out.println("--- INFO: Going to break this ExecuteTestCase!");
					break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 执行Statement
	 * 
	 * @param executeStatementBean
	 * @return 返回是否停止执行
	 */
	private boolean execStatementBean(ExecuteStatementBean executeStatementBean) {

		// step 0: check Command
		this.checkCommand(executeStatementBean);
		if (executeStatementBean.getStatus() == ExecuteStatementStatus.STOP)
			return true;

		// step 1: parse script
		String scriptStr = this.parseScript(executeStatementBean);
		if (null == scriptStr)
			return false;

		// step 2: write scriptStr to script file & clean log file
		this.prepare(scriptStr);

		// step 3: execute script file
		this.execute(executeStatementBean);
		if (executeStatementBean.getStatus() == ExecuteStatementStatus.FAIL)
			return true;
		return false;

	}

	/**
	 * get command
	 * 
	 * @param executeStatementBean
	 * @return boolean 是否停止执行
	 * @throws InterruptedException
	 */
	private void checkCommand(ExecuteStatementBean executeStatementBean) {

		int executeId = executeStatementBean.getExecuteId();
		int executeTestCaseId = executeStatementBean.getExecuteTestCaseId();

		if (DataProvider.COMMAND_MAP.isEmpty())
			return;
		CommandBean commandBean = DataProvider.COMMAND_MAP.get(executeTestCaseId);
		if (null == commandBean)
			return;

		//PAUSE
		if (commandBean.getCommand() == Command.PAUSE) {
			System.out.println("--- INFO: ExecuteTestCase " + executeStatementBean.getExecuteTestCaseId() + "get Command PAUSE");
			new Thread(new NotifyThread(this, executeStatementBean, commandBean)).start();
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		// STOP
		if (commandBean.getCommand() == Command.STOP) {
			System.out.println("--- INFO: ExecuteTestCase " + executeStatementBean.getExecuteTestCaseId() + "get Command STOP");
			Iterator<ExecuteTestCaseBean> iterator = DataProvider.RECEIVE_QUEUE.iterator();
			while (iterator.hasNext()) {
				ExecuteTestCaseBean bean = iterator.next();
				if (bean.getExecuteId() == executeId)
					DataProvider.RECEIVE_QUEUE.remove(bean);
			}
			// 设置为STOP状态 
			executeStatementBean.setStatus(ExecuteStatementStatus.STOP);
			executeStatementBean.setRuntime(0);
			executeStatementBean.setLog("cancel");
		}

		//remove command
		DataProvider.COMMAND_MAP.remove(executeStatementBean.getExecuteTestCaseId());

	}

	/**
	 * 解析脚本
	 * 
	 * @param executeStatementBean
	 * @return 解析好的脚本
	 */
	private String parseScript(ExecuteStatementBean executeStatementBean) {

		String scripts = executeStatementBean.getScript();
		if (null == scripts || scripts.trim().isEmpty()) {
			executeStatementBean.setStatus(ExecuteStatementStatus.SUCCESS);
			executeStatementBean.setRuntime(0);
			executeStatementBean.setLog("scirpt is null");
			return null;
		}

		StringBuffer sb = new StringBuffer();
		String[] strs = scripts.trim().split(";");
		for (int i = 0; i < strs.length; i++) {
			String temp = strs[i].trim();
			if (temp.isEmpty())
				continue;
			sb.append(temp);
			if (i != strs.length - 1)
				sb.append("\r\n");
		}
		return sb.toString();

	}

	/**
	 * prepare
	 * 
	 * @param scriptStr
	 * @throws IOException
	 */
	private void prepare(String scriptStr) {

		File scriptFile = new File(DataProvider.SCRIPT_FILE);
		File logFile = new File(DataProvider.LOG_FILE);
		FileOutputStream fos = null;
		FileOutputStream fos2 = null;
		try {

			//write scirpt to SCRIPT_FILE

			fos = new FileOutputStream(scriptFile, false);
			fos.write(scriptStr.getBytes("UTF-8"));
			fos.flush();

			//clean LOG_FILE

			fos2 = new FileOutputStream(logFile, false);
			fos2.write("".getBytes("UTF-8"));
			fos2.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fos)
					fos.close();
				if (null != fos2)
					fos2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * execute script & get log
	 * 
	 * @param executeStatementBean
	 */
	private void execute(ExecuteStatementBean executeStatementBean) {
		try {
			Process p = null;
			long start = System.currentTimeMillis();
			p = Runtime.getRuntime().exec(DataProvider.COMMAND_LINE + " " + DataProvider.SCRIPT_FILE);
			if (null == p) {
				executeStatementBean.setStatus(ExecuteStatementStatus.FAIL);
				executeStatementBean.setLog("ScriptEngine startup process fail");
				executeStatementBean.setRuntime(0);
				return;
			}
			int exitValue = p.waitFor();
			if (exitValue != 0) {
				executeStatementBean.setStatus(ExecuteStatementStatus.FAIL);
				executeStatementBean.setLog("ScriptEngineCMD.exe exit value error");
				executeStatementBean.setRuntime(0);
				return;
			}
			long end = System.currentTimeMillis();

			//parse log file
			String prefix = "Result[[[";
			String suffix = "]]]";
			StringBuffer log = new StringBuffer();
			boolean isSuccess = true;
			Scanner scanner = null;
			scanner = new Scanner(new File(DataProvider.LOG_FILE), "UTF-8");
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.isEmpty())
					continue;
				if (isSuccess && line.startsWith(prefix)) {
					String result = line.substring(prefix.length(), line.length() - suffix.length());
					if (!result.equals("SUCESS")) {
						isSuccess = false;
					}
				}
				log.append(line);
				log.append("\r\n");
			}
			if (null != scanner)
				scanner.close();

			if (isSuccess)
				executeStatementBean.setStatus(ExecuteStatementStatus.SUCCESS);
			else
				executeStatementBean.setStatus(ExecuteStatementStatus.FAIL);
			executeStatementBean.setRuntime((int) (end - start));
			executeStatementBean.setLog(log.toString());

		} catch (IOException e) {
			executeStatementBean.setStatus(ExecuteStatementStatus.FAIL);
			executeStatementBean.setRuntime(0);
			executeStatementBean.setLog(e.getMessage());
		} catch (InterruptedException e) {
			executeStatementBean.setStatus(ExecuteStatementStatus.FAIL);
			executeStatementBean.setRuntime(0);
			executeStatementBean.setLog(e.getMessage());
		}
	}

}

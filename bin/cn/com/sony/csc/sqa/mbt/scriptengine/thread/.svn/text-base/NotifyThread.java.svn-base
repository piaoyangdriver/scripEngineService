package cn.com.sony.csc.sqa.mbt.scriptengine.thread;

import cn.com.sony.csc.sqa.mbt.bean.CommandBean;
import cn.com.sony.csc.sqa.mbt.bean.ExecuteStatementBean;
import cn.com.sony.csc.sqa.mbt.constant.Command;
import cn.com.sony.csc.sqa.mbt.scriptengine.run.DataProvider;

public class NotifyThread implements Runnable {

	private ReceiveQueueConsumer target = null;
	private ExecuteStatementBean executeStatementBean = null;
	private CommandBean commandBean = null;

	public NotifyThread(ReceiveQueueConsumer arg, ExecuteStatementBean bean, CommandBean commandBean) {
		this.target = arg;
		this.executeStatementBean = bean;
		this.commandBean = commandBean;
	}

	@Override
	public void run() {
		while (true) {

			try {
				// 500ms
				Thread.sleep(500);
				CommandBean commandBean2 = DataProvider.COMMAND_MAP.get(executeStatementBean.getExecuteTestCaseId());
				if (null == commandBean2)
					continue;
				Command command = commandBean2.getCommand();
				if (Command.RESUME == command || Command.STOP == command) {
					commandBean.setCommand(command);
					synchronized (target) {
						target.notify();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

}

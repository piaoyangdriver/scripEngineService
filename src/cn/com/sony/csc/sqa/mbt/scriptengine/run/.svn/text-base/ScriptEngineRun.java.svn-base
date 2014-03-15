package cn.com.sony.csc.sqa.mbt.scriptengine.run;

import cn.com.sony.csc.sqa.mbt.scriptengine.thread.ReceiveQueueConsumer;
import cn.com.sony.csc.sqa.mbt.scriptengine.thread.ReceiveQueueProducer;
import cn.com.sony.csc.sqa.mbt.scriptengine.thread.SendQueueConsumer;

public class ScriptEngineRun {

	public static void main(String[] args) {

		new Thread(new ReceiveQueueProducer()).start();
		new Thread(new ReceiveQueueConsumer()).start();
		new Thread(new SendQueueConsumer()).start();
		
		System.out.println("--- INFO: startup success! ---");

	}

}

package cn.op.zdf;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Intent;
import cn.op.common.util.Log;
import cn.op.zdf.ui.MainActivity;

/**
 * 全局异常处理，not use
 * @author lufei
 *
 */
public class UEHandler implements UncaughtExceptionHandler {

	private AppContext softApp;

	public UEHandler(AppContext app) {
		softApp = app;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// fetch Excpetion Info
		String info = null;
		ByteArrayOutputStream baos = null;
		PrintStream printStream = null;
		try {
			baos = new ByteArrayOutputStream();
			printStream = new PrintStream(baos);
			ex.printStackTrace(printStream);
			byte[] data = baos.toByteArray();
			info = new String(data);
			data = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (printStream != null) {
					printStream.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// print
		long threadId = thread.getId();
		Log.e("ANDROID_LAB", "Thread.getName()=" + thread.getName() + " id="
				+ threadId + " state=" + thread.getState());
		Log.e("ANDROID_LAB", "Error[" + info + "]");

		Intent intent = new Intent(softApp, MainActivity.class);
		// 如果没有NEW_TASK标识且是UI线程抛的异常则界面卡死直到ANR
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		softApp.startActivity(intent);

		// kill App Progress
		android.os.Process.killProcess(android.os.Process.myPid());

	}

}

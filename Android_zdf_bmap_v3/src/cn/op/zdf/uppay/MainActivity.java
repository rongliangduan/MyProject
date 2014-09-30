package cn.op.zdf.uppay;

import java.io.File;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.op.common.AppException;
import cn.op.common.UIHelper;
import cn.op.common.util.FileUtils;
import cn.op.common.util.StringUtils;
import cn.op.zdf.ApiClient;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;

import com.unionpay.UPPayAssistEx;

public class MainActivity extends Activity implements Callback {
	private static final String LOG_TAG = "PayDemo";
	private Context mContext = null;
	private int mGoodsIdx = 0;
	private Handler mHandler = null;
	private ProgressDialog mLoadingDialog = null;

	private static final int PLUGIN_VALID = 0;
	private static final int PLUGIN_NOT_INSTALLED = -1;
	private static final int PLUGIN_NEED_UPGRADE = 2;

	/*****************************************************************
	 * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
	 *****************************************************************/
	private String mMode = "01";
	private static final String TN_URL_01 = "http://222.66.233.198:8080/sim/gettn";
	protected static final int WHAT_DOWNLOAD_SUCCESS = 3;

	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.e(LOG_TAG, " " + v.getTag());

			// 测试
			String tn = etTn.getText().toString();
			if (StringUtils.isEmpty(tn)) {
				AppContext.toastShow("请输入流水号");
				return;
			}
			
			
			toPay(tn);

//			mGoodsIdx = (Integer) v.getTag();
//
//			mLoadingDialog = ProgressDialog.show(mContext, // context
//					"", // title
//					"正在努力的获取tn中,请稍候...", // message
//					true); // 进度是否是不确定的，这只和创建进度条有关
//
//			/*************************************************
//			 * 
//			 * 步骤1：从网络开始,获取交易流水号即TN
//			 * 
//			 ************************************************/
//
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					String tn = null;
//					InputStream is;
//					try {
//
//						String url = TN_URL_01;
//
//						URL myURL = new URL(url);
//						URLConnection ucon = myURL.openConnection();
//						ucon.setConnectTimeout(120000);
//						is = ucon.getInputStream();
//						int i = -1;
//						ByteArrayOutputStream baos = new ByteArrayOutputStream();
//						while ((i = is.read()) != -1) {
//							baos.write(i);
//						}
//
//						tn = baos.toString();
//						is.close();
//						baos.close();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//					Message msg = mHandler.obtainMessage();
//					msg.obj = tn;
//					mHandler.sendMessage(msg);
//				}
//			}).start();
		}
	};
	private View pb;
	private EditText etTn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mHandler = new Handler(this);

		setContentView(R.layout.activity_main_uppay);

		pb = findViewById(R.id.pb);
		etTn = (EditText) findViewById(R.id.editText1);
		Button btn0 = (Button) findViewById(R.id.btn0);
		btn0.setTag(0);
		btn0.setOnClickListener(mClickListener);
	}

	@Override
	public boolean handleMessage(Message msg) {
		Log.e(LOG_TAG, " " + "" + msg.obj);
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}

		String tn = "";
		if (msg.obj == null || ((String) msg.obj).length() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("错误提示");
			builder.setMessage("网络连接失败,请重试!");
			builder.setNegativeButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		} else {
			tn = (String) msg.obj;

			toPay(tn);

		}

		return false;
	}

	private void toPay(String tn) {
		// TODO 测试
		tn = etTn.getText().toString();

		/*************************************************
		 * 
		 * 步骤2：通过银联工具类启动支付插件
		 * 
		 ************************************************/
		// mMode参数解释：
		// 0 - 启动银联正式环境
		// 1 - 连接银联测试环境
		int ret = UPPayAssistEx.startPay(this, null, null, tn, mMode);
		if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
			// 需要重新安装控件
			Log.e(LOG_TAG, " plugin not found or need upgrade!!!");

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("完成购买需要安装银联支付控件，是否安装？");

			builder.setNegativeButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

							// TODO 下载安装

							downloadUppay();
						}
					});

			builder.setPositiveButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();

		}
		Log.e(LOG_TAG, "" + ret);
	}

	private void downloadUppay() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				pb.setVisibility(View.GONE);

				switch (msg.what) {
				case WHAT_DOWNLOAD_SUCCESS:
					File apkFile = (File) msg.obj;

					UIHelper.installAPK(MainActivity.this, apkFile);

					break;

				case -WHAT_DOWNLOAD_SUCCESS:

					break;

				default:
					break;
				}
			}
		};

		pb.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = "http://42.96.189.25:7001/Hotels/download/UPPayPluginExPro.apk";
				Message msg = new Message();
				try {
					InputStream inputStream = ApiClient
							.http_get_inputstream(url);
					File outFile = new File(FileUtils
							.getDirOnExtStore("/Download/cache"),
							"UPPayPlugin.apk");
					FileUtils.writeFile(inputStream, outFile);

					msg.what = WHAT_DOWNLOAD_SUCCESS;
					msg.obj = outFile;
					handler.sendMessage(msg);
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_DOWNLOAD_SUCCESS;
					handler.sendMessage(msg);
				}
			}
		}).start();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*************************************************
		 * 
		 * 步骤3：处理银联手机支付控件返回的支付结果
		 * 
		 ************************************************/
		if (data == null) {
			return;
		}

		String msg = "";
		/*
		 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
		String str = data.getExtras().getString("pay_result");
		if (str.equalsIgnoreCase("success")) {
			msg = "支付成功！";
		} else if (str.equalsIgnoreCase("fail")) {
			msg = "支付失败！";
		} else if (str.equalsIgnoreCase("cancel")) {
			msg = "用户取消了支付";
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("支付结果通知");
		builder.setMessage(msg);
		builder.setInverseBackgroundForced(true);
		// builder.setCustomTitle();
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	int startpay(Activity act, String tn, int serverIdentifier) {
		return 0;
	}
}

package cn.op.zdf;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import cn.op.common.UIHelper;
import cn.op.common.domain.RspMsg;
import cn.op.common.util.Log;
import cn.op.common.util.StringUtils;

public class TestActivity extends Activity {

	protected static final String TAG = Log.makeLogTag(TestActivity.class);

	private AppContext ac;

	private EditText etClientNum;

	private EditText etName;

	/**
	 * 线程可以同时访问数
	 */
	private static int thread_num = 100;

	/**
	 * 模拟客户端数量
	 */
	private static int client_num = 10;

	private int okNum;

	private TextView tvOkNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		ac = AppContext.getAc();

		etClientNum = (EditText) findViewById(R.id.editText1);
		tvOkNum = (TextView) findViewById(R.id.textView3);
		etName = (EditText) findViewById(R.id.editText2);
		View btnGetHotelsPrice = findViewById(R.id.button1);
		View btnSubmitOrder = findViewById(R.id.button2);

		etClientNum.setText("" + client_num);

		btnGetHotelsPrice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				request(1);
			}
		});

		btnSubmitOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				request(2);
			}
		});

	}

	protected void request(final int method) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				tvOkNum.setText("成功数：" + okNum);
			}
		};

		okNum = 0;

		String clientNum = etClientNum.getText().toString();
		client_num = StringUtils.toInt(clientNum, client_num);

		final String name = etName.getText().toString();

		Log.d(TAG, "======request====== client_num=" + client_num);

		ExecutorService exec = Executors.newCachedThreadPool();
		// 线程可以同时访问数
		final Semaphore semp = new Semaphore(thread_num);

		// 模拟客户端数量
		for (int index = 0; index < client_num; index++) {

			final int NO = index;

			Runnable run = new Runnable() {

				public void run() {
					try {
						// 获取许可
						semp.acquire();
						Log.d(TAG, "======test====== 开始：NO=" + NO + " ,");

						RspMsg rsp = null;

						String hotelIds = "3745,5740,3743,2526,4317,4316,6959,5084,2527,7161";
						int saleType = 1;

						String hotelId = null;
						String userId = null;
						String roomSaleId = null;
						String brandId = null;
						String hotelName = null;

						// 200
						// arg0=6971,
						// arg1=4612,
						// arg2=20131231182640728,
						// arg12=16,
						// arg13=锦江之星（西安小寨地铁站店）

						// arg0=5738,
						// arg1=4221,
						// arg2=20131231182640728
						// arg12=19,
						// arg13=速8酒店（西安南稍门店）

						switch (NO) {
						// 42
						case 0:
							hotelId = "3739";
							roomSaleId = "18787";
							userId = "20131231182640728";
							brandId = "7";
							hotelName = "如家快捷酒店（西安含光南路大明宫购物广场店）";
							break;
						case 1:
							hotelId = "3743";
							roomSaleId = "28383";
							userId = "20131231182640728";
							brandId = "7";
							hotelName = "如家快捷酒店（西安大雁塔历史博物馆店）";
							break;

						// 200
						// case 2:
						// hotelId = "6971";
						// roomSaleId = "4612";
						// userId = "20131231182640728";
						// brandId = "16";
						// hotelName = "锦江之星（西安小寨地铁站店）";
						// break;
						// case 3:
						// hotelId = "5738";
						// roomSaleId = "4221";
						// userId = "20131231182640728";
						// brandId = "19";
						// hotelName = "速8酒店（西安南稍门店）";
						// break;
						// default:
						// hotelId = "1807";
						// roomSaleId = "4985";
						// userId = "20131231182640728";
						// brandId = "10";
						// hotelName = "7天连锁酒店（北京大观园店）";
						// break;
						default:
							hotelId = "3743";
							roomSaleId = "28383";
							userId = "20131231182640728";
							brandId = "7";
							hotelName = "如家快捷酒店（西安大雁塔历史博物馆店）";
							break;
						}

						int roomType = -1;
						String dateArrive = "1030";
						String liveMan = name;
						String liveManPhone = "18602990487";
						String contact = name;
						String contactPhone = "18602990487";
						String count = "0";
						int payWay = 43;
						String hotelCode = null;
						String roomPlanId = null;
						String roomPrice = null;

						switch (method) {
						case 1:
							rsp = ac.getHotelsPrice(hotelIds).rspMsg;
							break;

						case 2:
							rsp = ac.submitOrder(hotelId, userId, roomSaleId,
									roomType, saleType, dateArrive, liveMan,
									liveManPhone, contact, contactPhone, count,
									payWay, hotelCode, roomPlanId, roomPrice,
									brandId, hotelName).rspMsg;
							break;
						}

						okNum++;
						// 释放
						Log.d(TAG,
								"======test====== 结束：NO=" + NO + " ,"
										+ rsp.OK() + " ,num=" + okNum);

						handler.sendEmptyMessage(1);

						semp.release();
					} catch (Exception e) {
						Log.e(TAG, "======test====== error：NO=" + NO + " , "
								+ e.toString());
						// Log.e(TAG, "======test====== error：NO=" + NO, e);
					}
				}
			};

			exec.execute(run);
		}

		// 退出线程池
		exec.shutdown();
	}
}

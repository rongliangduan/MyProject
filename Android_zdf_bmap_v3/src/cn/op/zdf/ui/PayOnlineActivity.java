package cn.op.zdf.ui;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.op.common.AppException;
import cn.op.common.UIHelper;
import cn.op.common.constant.Tags;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.URLs;
import cn.op.common.domain.UserInfo;
import cn.op.common.util.Constants;
import cn.op.common.util.DateUtil;
import cn.op.common.util.FileUtils;
import cn.op.common.util.Log;
import cn.op.common.util.StringUtils;
import cn.op.zdf.ApiClient;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.alipay.Keys;
import cn.op.zdf.alipay.Result;
import cn.op.zdf.alipay.Rsa;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.Recharge;
import cn.op.zdf.event.Event;
import cn.op.zdf.event.LoginEvent;
import cn.op.zdf.event.OrderChangeEvent;
import cn.op.zdf.event.UserBalanceChangeEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.alipay.android.app.sdk.AliPay;
import com.meizu.smartbar.SmartBarUtils;
import com.tendcloud.tenddata.TCAgent;
import com.unionpay.UPPayAssistEx;

import de.greenrobot.event.EventBus;

/**
 * 在线支付
 * 
 * @author lufei
 * 
 */
public class PayOnlineActivity extends SherlockFragmentActivity {

	private static final String TAG = Log.makeLogTag(PayOnlineActivity.class);

	private static final int WHAT_RQF_PAY_PALTFORM = 1;
	protected static final int WHAT_ORDER_DETAIL = 2;
	protected static final int WHAT_RECHARGE = 4;
	protected static final int WHAT_RECHARGE_SUCCESS = 5;
	protected static final int WHAT_QUERY_BALANCE = 6;

	protected static final int WHAT_GET_UPPAY_TN = 8;
	protected static final int WHAT_DOWNLOAD_SUCCESS = 9;

	/**
	 * 在线,未使用余额支付
	 */
	static final String PAY_TYPE_NOT_USE_BALANCE = "1";
	/**
	 * 在线+使用一部分余额支付
	 */
	static final String PAY_TYPE_USE_BALANCE_HALF = "2";
	/**
	 * 充值
	 */
	public static final String PAY_TYPE_RECHARGE = "3";
	/**
	 * 在线+优惠券
	 */
	public static final String PAY_TYPE_USE_COUPON = "4";
	/**
	 * 在线+余额+优惠券
	 */
	public static final String PAY_TYPE_USE_BALANCE_COUPON = "5";

	/**
	 * 支付平台 阿里支付宝
	 */
	protected static final String PAY_PLATFORM_ALIPAY = "1";
	/**
	 * 支付平台 银联
	 */
	protected static final String PAY_PLATFORM_UPPAY = "2";

	protected static final int MAX_RECHARGE_MONEY = 2000;

	protected String payPlatform = PAY_PLATFORM_ALIPAY;

	private PayOnlineActivity activity;
	private FragmentManager fm;
	Item itemOrder;

	protected String subject;

	protected String body;

	protected String price;

	private AppContext ac;

	private OrderDetailFragment fragmentOrderDetail;

	private View pb;

	private boolean noAnim;

	private View layoutPayWay;

	// private CheckBox cbIsUseBalance;

	// private TextView tvOrderPrice;

	private TextView tvPayMoney;

	private TextView tvPayMoneyWithBlance;

	/**
	 * 是否使用余额支付
	 */
	protected boolean isUseBalance;

	private TextView tvBalanceTitle;

	/**
	 * 支付的金额
	 */
	protected int payMoney;

	/**
	 * 使用的余额
	 */
	protected int useBalance;

	/**
	 * 订单价格
	 */
	private int orderPrice;

	private View layoutPayPsw;

	// private EditText etPayPsw;
	// private View btnBalancePay;

	private EditText etRechargeMoney;

	String mPayType;

	protected String mRechargeMoney;

	private View topBar;

	private View btnLeft;

	private View layoutStub;

	private boolean orderIsDetail;

	/**
	 * 支付类型：支付宝、银联
	 */
	private View layoutPayPlatform;

	private ImageView cbPayAli;
	private ImageView cbPayUp;

	private TextView tvTopTitle;

	private Button btnRecharge;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "======onCreate======");

		if (SmartBarUtils.hasSmartBar()) {
			getSherlock().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);

			setTheme(R.style.Holo_Theme_CustomAbsOverlay);

			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), true);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_online);

		ac = AppContext.getAc();
		activity = this;
		fm = getSupportFragmentManager();

		EventBus.getDefault().register(this);

		// 在打开支付平台时，某些安全软件会清理后台程序，有可能将当前应用清理掉
		if (!ac.isLogin()) {
			AppContext.toastShow(R.string.pleaseRetry);
		}

		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (PAY_TYPE_RECHARGE.equals(mPayType)) {

			UIHelper.makeEmptyMenu(getSupportMenuInflater(), menu);

			// getSupportMenuInflater().inflate(R.menu.recharge_menu, menu);
			// Button menuRecharge = (Button) menu.findItem(R.id.menu_recharge)
			// .getActionView();
			//
			// menuRecharge.setText("立即充值");
			// menuRecharge
			// .setBackgroundResource(R.drawable.btn_trans_black_click);
			//
			// menuRecharge
			// .setCompoundDrawables(
			// getResources().getDrawable(
			// R.drawable.ic_action_left_click), null,
			// null, null);
			//
			// menuRecharge.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// btnRecharge.performClick();
			// }
			// });
		}

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			btnLeft.performClick();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onEventMainThread(Event e) {
		Log.d(TAG, "======onEventMainThread======"
				+ e.getClass().getSimpleName());

		if (e instanceof UserBalanceChangeEvent) {
			UserBalanceChangeEvent ev = (UserBalanceChangeEvent) e;
			if (ev.isChange) {

				// setBalanceCbEnable();
				// if (cbIsUseBalance != null) {
				// cbIsUseBalance.performClick();
				// }
			}
		}

		if (e instanceof LoginEvent) {
			// 在打开支付平台时，某些安全软件会清理后台程序，有可能将当前应用清理掉
			// 清理掉后，应用会重启，并自动登录，登录后需重新刷新页面
			LoginEvent ev = (LoginEvent) e;
			if (ev.success) {
				initView();
			}
		}

	}

	private void initView() {
		// if (SmartBarUtils.hasSmartBar()) {
		// findViewById(R.id.layoutRoot).setPadding(
		// 0,
		// 0,
		// 0,
		// getResources().getDimensionPixelSize(
		// R.dimen.abs__action_bar_default_height));
		// }

		pb = findViewById(R.id.pbPay);
		layoutStub = findViewById(R.id.layoutStub);
		topBar = findViewById(R.id.topBar);
		btnLeft = topBar.findViewById(R.id.btnLeft);
		tvTopTitle = (TextView) topBar.findViewById(R.id.tvTitle);

		pb.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});

		mPayType = getIntent().getStringExtra(
				cn.op.common.constant.Keys.PAY_TYPE);

		if (PAY_TYPE_RECHARGE.equals(mPayType)) {
			initViewRecharge();
		} else {
			itemOrder = (Item) getIntent().getSerializableExtra(
					cn.op.common.constant.Keys.ORDER);
			orderIsDetail = getIntent().getBooleanExtra(
					cn.op.common.constant.Keys.ORDER_IS_DETAIL, false);

			addFragmentOrder();
		}
	}

	private void initViewRecharge() {

		layoutStub.setVisibility(View.VISIBLE);
		topBar.setVisibility(View.VISIBLE);
		tvTopTitle.setText("充值");

		if (SmartBarUtils.hasSmartBar()) {
			btnLeft.setVisibility(View.VISIBLE);
		} else {
			btnLeft.setVisibility(View.VISIBLE);
		}

		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				onBackPressed();
			}
		});

		View layoutRecharge = findViewById(R.id.layoutRecharge);
		layoutRecharge.setVisibility(View.VISIBLE);
		etRechargeMoney = (EditText) layoutRecharge
				.findViewById(R.id.etRechargeMoney);
		TextView tvUsername = (TextView) layoutRecharge
				.findViewById(R.id.tvUsername);
		TextView tvBalance = (TextView) layoutRecharge
				.findViewById(R.id.tvBalance);

		View layoutBottomToRecharge = layoutRecharge
				.findViewById(R.id.layoutBottomToPay);
		final TextView tvRechargeMoney = (TextView) layoutRecharge
				.findViewById(R.id.tvMoneyPay);
		btnRecharge = (Button) layoutBottomToRecharge
				.findViewById(R.id.btnToPay);

		btnRecharge.setText("确认");

		// if (SmartBarUtils.hasSmartBar()) {
		// layoutBottomToRecharge.setVisibility(View.VISIBLE);
		//
		// UIHelper.setMeizuBtn(getResources(), layoutBottomToRecharge,
		// btnRecharge);
		//
		// } else {
		// layoutBottomToRecharge.setVisibility(View.VISIBLE);
		// }

		layoutBottomToRecharge.setVisibility(View.VISIBLE);

		// 在打开支付平台时，某些安全软件会清理后台程序，有可能将当前应用清理掉
		// 清理掉后，应用会重启，再次恢复页面时，用户信息可能被清掉
		if (ac.isLogin()) {
			if (UserInfo.USER_TYPE_NORMAL.equals(ac.user.userType)) {
				tvUsername.setText("账户：" + ac.user.username);
			} else {
				// OAuth用户
				tvUsername.setText("账户：" + ac.user.nickname);
			}
			tvBalance.setText("余额：" + (int) ac.user.balance);
		} else {

			AppContext.toastShow(R.string.pleaseRetry);
			Log.w(TAG, "======initViewRecharge====== ac.user = null");
		}

		etRechargeMoney.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable.length() > 1 && editable.charAt(0) == '0') {
					Integer integer = Integer.valueOf(editable.toString());
					etRechargeMoney.setText(integer.toString());
					etRechargeMoney.setSelection(etRechargeMoney.length());
				}

				tvRechargeMoney.setText(etRechargeMoney.getText());
			}
		});

		btnRecharge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				String rechargeMoney = etRechargeMoney.getText().toString();

				if (StringUtils.toFloat(rechargeMoney) == 0f) {
					AppContext.toastShow("请输入充值金额");
					return;
				}

				if (StringUtils.toInt(rechargeMoney) > MAX_RECHARGE_MONEY) {
					AppContext.toastShow("充值金额超出最大限制2000");
					return;
				}

				mRechargeMoney = rechargeMoney;

				submitRecharge(mRechargeMoney);
			}
		});

		layoutPayPlatform = layoutRecharge.findViewById(R.id.layoutPayPlatform);
		initViewPayPlatform(layoutPayPlatform);

	}

	void initViewPayPlatform(View layoutPayPlatform) {
		View layoutPayAli = layoutPayPlatform.findViewById(R.id.layoutPayAli);
		View layoutPayUp = layoutPayPlatform.findViewById(R.id.layoutPayUp);
		cbPayAli = (ImageView) layoutPayAli.findViewById(R.id.cbPayAli);
		cbPayUp = (ImageView) layoutPayUp.findViewById(R.id.cbPayUp);

		layoutPayAli.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!PAY_PLATFORM_ALIPAY.equals(payPlatform)) {
					payPlatform = PAY_PLATFORM_ALIPAY;
					cbPayAli.setImageResource(R.drawable.ic_cb_green_true);
					cbPayUp.setImageResource(R.drawable.ic_cb_green);
				}

			}
		});
		layoutPayUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!PAY_PLATFORM_UPPAY.equals(payPlatform)) {
					payPlatform = PAY_PLATFORM_UPPAY;
					cbPayUp.setImageResource(R.drawable.ic_cb_green_true);
					cbPayAli.setImageResource(R.drawable.ic_cb_green);
				}
			}
		});
	}

	/**
	 * 获取银联交易流水号
	 * 
	 * @param booksId
	 *            订单Id、充值订单Id
	 * @param mPayType
	 *            支付类型
	 * @param payMoney
	 *            支付金额
	 */
	protected void getUppayTn(final String booksId, final String mPayType,
			final String payMoney, final String couponId) {
		pb.setVisibility(View.VISIBLE);
		new Thread() {

			public void run() {
				Message msg = new Message();
				try {

					RspMsg r = ac.getUppayTn(booksId, mPayType, payMoney,
							couponId);

					msg.what = WHAT_GET_UPPAY_TN;
					msg.obj = r;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			}
		}.start();
	}

	// private void setBalanceCbEnable() {
	//
	// if (tvBalance != null) {
	// tvBalance.setText("￥" + (int) ac.user.balance);
	// }
	//
	// if (cbIsUseBalance != null) {
	// if (ac.user.balance == 0) {
	// cbIsUseBalance.setVisibility(View.INVISIBLE);
	// tvBalance.setEnabled(false);
	// tvBalance.setTextColor(getResources().getColor(
	// R.color.gray_light));
	// } else {
	// cbIsUseBalance.setVisibility(View.VISIBLE);
	// tvBalance.setEnabled(true);
	// tvBalance.setTextColor(getResources().getColor(
	// R.color.white_zdf));
	// }
	// }
	// }

	protected void showInputPsw() {
		layoutPayPsw.setVisibility(View.VISIBLE);

		layoutPayWay.setVisibility(View.GONE);
	}

	protected void hideInputPsw() {
		layoutPayPsw.setVisibility(View.GONE);

		layoutPayWay.setVisibility(View.VISIBLE);
	}

	protected void showPayWay() {
		// setBalanceCbEnable();

		layoutStub.setVisibility(View.VISIBLE);
		layoutPayWay.setVisibility(View.VISIBLE);
		if (SmartBarUtils.hasSmartBar()) {
			btnLeft.setVisibility(View.VISIBLE);
		} else {
			btnLeft.setVisibility(View.VISIBLE);
		}
		tvTopTitle.setText("确认订单");
	}

	private void hidePayWay() {
		layoutStub.setVisibility(View.GONE);
		layoutPayWay.setVisibility(View.GONE);
	}

	protected void submitRecharge(final String rechargeMoney) {
		pb.setVisibility(View.VISIBLE);
		new Thread() {

			public void run() {
				Message msg = new Message();
				try {

					Recharge order = ac.submitRecharge(rechargeMoney,
							ac.getLoginUserId(), payPlatform);

					msg.what = WHAT_RECHARGE;
					msg.obj = order;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "======onResume======");
		super.onResume();
		TCAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "======onPause======");

		if (mTimerCheckOrder != null) {
			mTimerCheckOrder.cancel();
			mTimerCheckOrder = null;
		}
		if (mTimerQueryRecharge != null) {
			mTimerQueryRecharge.cancel();
			mTimerQueryRecharge = null;
		}

		super.onPause();
		TCAgent.onPause(this);
		// overridePendingTransition(R.anim.slide_in_from_right,
		// R.anim.slide_out_to_right);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "======onSaveInstanceState======");
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(TAG, "======onRestoreInstanceState======");
		super.onRestoreInstanceState(savedInstanceState);
	}

	public void finish() {
		Log.d(TAG, "======finish======");
		UIHelper.hideSoftInput(activity, pb);

		super.finish();
		if (!noAnim) {
			overridePendingTransition(R.anim.slide_in_from_left,
					R.anim.slide_out_to_right);
		}
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "======onDestroy======");
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (layoutPayPsw != null
				&& layoutPayPsw.getVisibility() == View.VISIBLE) {
			hideInputPsw();

		} else if (layoutPayWay != null
				&& layoutPayWay.getVisibility() == View.VISIBLE) {
			hidePayWay();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "======onActivityResult======");
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
			if (PAY_TYPE_RECHARGE.equals(mPayType)) {
				Log.d(TAG, "======onActivityResult====== PAY_TYPE_RECHARGE");
				// 充值
				checkRechargeBalance();
			} else {
				Log.d(TAG, "======onActivityResult====== PayOrder");
				// 支付订单
				checkOrderState();
			}
			msg = "支付成功！";

			return;
		} else if (str.equalsIgnoreCase("fail")) {
			msg = "支付失败！";
		} else if (str.equalsIgnoreCase("cancel")) {
			msg = "取消";
		}

		AppContext.toastShow(msg);
	}

	/**
	 * 订单支付是否超时，必须在订单提交成功后15分钟内在线支付
	 * 
	 * @param itemOrder
	 * @return true-超时
	 */
	boolean isTimeOut4Pay(Item itemOrder) {
		boolean timeOut = false;

		long curtTime = System.currentTimeMillis();

		long orderSubmitSuccessTime = DateUtil.str2Date(
				itemOrder.getCommitBookTime(), "yyyy-MM-dd HH:mm").getTime();

		if (curtTime - orderSubmitSuccessTime > 1000 * 60 * 15) {
			timeOut = true;
		}
		return timeOut;
	}

	protected void addFragmentOrder() {
		Bundle args = new Bundle();
		args.putSerializable(cn.op.common.constant.Keys.ORDER, itemOrder);
		args.putBoolean(cn.op.common.constant.Keys.ORDER_IS_DETAIL,
				orderIsDetail);

		fragmentOrderDetail = (OrderDetailFragment) fm
				.findFragmentByTag(Tags.ORDER_DETAIL);
		if (fragmentOrderDetail != null) {

			fm.beginTransaction().attach(fragmentOrderDetail);
		} else {
			fragmentOrderDetail = (OrderDetailFragment) Fragment.instantiate(
					activity, OrderDetailFragment.class.getName(), args);

			fm.beginTransaction()
					.add(R.id.content_pay_online, fragmentOrderDetail,
							Tags.ORDER_DETAIL).commit();
		}

	}

	void topayAlipay(String subject, String body, String price) {
		try {
			String info = getNewOrderInfo(subject, body, price);
			String sign = Rsa.sign(info, Keys.PRIVATE);
			sign = URLEncoder.encode(sign, Constants.CHAR_SET_UTF_8);
			info += "&sign=\"" + sign + "\"&" + getSignType();
			Log.i("PayOnlineActivity", "start pay");
			// start the pay.
			Result.sResult = null;
			Log.i(TAG, "info = " + info);
			final String orderInfo = info;
			new Thread() {
				public void run() {
					String result = new AliPay(PayOnlineActivity.this,
							myHandler).pay(orderInfo);

					Log.i(TAG, "result = " + result);
					Message msg = new Message();
					msg.what = WHAT_RQF_PAY_PALTFORM;
					msg.obj = result;
					myHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(PayOnlineActivity.this,
					"Failure calling remote service", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private String getNewOrderInfo(String subject, String body, String price)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();

		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		// 订单id
		sb.append(getOutTradeNo());
		sb.append("\"&subject=\"");
		sb.append(subject);
		sb.append("\"&body=\"");
		sb.append(body);
		sb.append("\"&total_fee=\"");
		sb.append(price);
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		// sb.append(URLEncoder.encode("http://notify.java.jpxx.org/index.jsp"));
		sb.append(URLEncoder.encode(URLs.URL_PAY_NOTIFY_URL,
				Constants.CHAR_SET_UTF_8));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com",
				Constants.CHAR_SET_UTF_8));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}

	/**
	 * @return 商户网站唯一订单号
	 */
	private String getOutTradeNo() {
		String outTradeNo = null;

		if (PAY_TYPE_RECHARGE.equals(mPayType)) {

			outTradeNo = mPayType + "--" + mRechargeId;

		} else {

			outTradeNo = mPayType + "--" + itemOrder.booksId;

			if (mCoupon != null) {
				outTradeNo += "--" + mCoupon.couponId;
			}
		}

		return outTradeNo;
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	private MyHandler myHandler = new MyHandler(this);

	private Timer mTimerCheckOrder;
	private Timer mTimerQueryRecharge;

	public String mRechargeId;

	/**
	 * 当前使用的优惠券
	 */
	public Item mCoupon;

	static class MyHandler extends Handler {
		// 注意下面的“<T>”类是MyHandler类所在的外部类
		private WeakReference<PayOnlineActivity> mWr;

		public MyHandler(PayOnlineActivity activity) {
			mWr = new WeakReference<PayOnlineActivity>(activity);
		}

		public void handleMessage(android.os.Message msg) {
			PayOnlineActivity activity = mWr.get();

			if (activity == null) {
				return;
			}

			// String ss = Result.sResult;
			//
			// =="resultStatus={9000};memo={};result={partner="2088111220362925"&out_trade_no="110816530088909"&subject="钟点房"&body="钟点房"&total_fee="0.1"&notify_url="http%3A%2F%2Fnotify.java.jpxx.org%2Findex.jsp"&service="mobile.securitypay.pay"&_input_charset="UTF-8"&return_url="http%3A%2F%2Fm.alipay.com"&payment_type="1"&seller_id="2088111220362925"&it_b_pay="1m"&success="true"&sign_type="RSA"&sign="Y2CxWyChzF63dvJhxSARCu7FkmXXJahDFp4UZwbW5z9NL4+FdkXEz5SBDwSWeyLJ4HQANr+qRSjtM70BKm3XEE3tmlKry3je+q7a+5SDOvtzvk+Zk2uVwgKPQfADYjZ8LTyoF2H3QUAzZBNyb3cN2Zxcp0SBX9swouXG25hYNN0="}";

			switch (msg.what) {
			case WHAT_RQF_PAY_PALTFORM:

				Result.sResult = (String) msg.obj;

				Log.d(TAG, "======pay success======" + Result.sResult);

				if (Result.sResult != null) {
					String[] parseResult = Result.parseResult();
					String resultStatusCode = parseResult[0];
					if (Result.SRESULT_CODE.equals(resultStatusCode)) {

						if (PAY_TYPE_RECHARGE.equals(activity.mPayType)) {

							// 充值 客户端轮询检查充值结果
							activity.checkRechargeBalance();

						} else {
							// 支付订单， 客户端轮询检查订单结果
							activity.checkOrderState();
						}

					} else {
						AppContext.toastShow(Result.getResult());
					}
				}

				break;

			case WHAT_ORDER_DETAIL:

				Item rsp = (Item) msg.obj;

				if (rsp.rspMsg.OK()) {
					activity.itemOrder = rsp;
					activity.paySuccess();
				}

				break;

			case WHAT_RECHARGE:
				activity.pb.setVisibility(View.GONE);

				Recharge r = (Recharge) msg.obj;

				if (r.rspMsg.OK()) {

					if (PAY_PLATFORM_ALIPAY.equals(activity.payPlatform)) {
						// 支付宝充值
						activity.mRechargeId = r.rechargeId;
						activity.topayAlipay("有间房-充值", "有间房-充值",
								activity.mRechargeMoney);
					} else if (PAY_PLATFORM_UPPAY.equals(activity.payPlatform)) {
						// 银联充值
						String tn = r.rechargeId;
						activity.topayUppay(tn);
					}

				} else {
					AppContext.toastShowException(r.rspMsg.message);
				}

				break;

			case WHAT_RECHARGE_SUCCESS:
				activity.pb.setVisibility(View.GONE);

				Recharge recharge = (Recharge) msg.obj;

				if (recharge.rspMsg.OK()) {

					activity.rechargeSuccess(recharge);

				} else {
					AppContext.toastShowException(recharge.rspMsg.message);
				}

				break;
			case WHAT_QUERY_BALANCE:
				activity.pb.setVisibility(View.GONE);

				Recharge balance = (Recharge) msg.obj;

				if (balance.rspMsg.OK()) {

					if (PAY_TYPE_RECHARGE.equals(activity.mPayType)) {
						// 充值
						activity.rechargeSuccess(balance);
					} else {
						// 支付订单
						activity.ac.user.balance = StringUtils
								.toFloat(balance.balance);
						// activity.setBalanceCbEnable();

						UserBalanceChangeEvent event = new UserBalanceChangeEvent();
						event.isChange = true;
						EventBus.getDefault().post(event);

						if (PAY_TYPE_USE_BALANCE_HALF.equals(activity.mPayType)) {
							return;
						}

						activity.finish();

						activity.noAnim = true;
						activity.itemOrder.booksStatus = Item.ORDER_STATE_WAIT_RESPONSE;
						Intent intent = new Intent(activity,
								PayOnlineActivity.class);
						intent.putExtra(cn.op.common.constant.Keys.ORDER,
								activity.itemOrder);
						activity.startActivity(intent);
					}
				} else {
					AppContext.toastShowException(balance.rspMsg.message);
				}

				break;

			case WHAT_GET_UPPAY_TN:
				activity.pb.setVisibility(View.GONE);

				RspMsg rTn = (RspMsg) msg.obj;

				if (rTn.OK() && !StringUtils.isEmpty(rTn.message)) {
					activity.topayUppay(rTn.message);
				} else if (RspMsg.CODE_UPPAY_REPEAT.equals(rTn.code)) {
					AppContext.toastShow(rTn.message);
				} else {
					AppContext.toastShow(rTn.message);
				}

				break;
			case WHAT_DOWNLOAD_SUCCESS:
				activity.pb.setVisibility(View.GONE);
				File apkFile = (File) msg.obj;

				UIHelper.installAPK(activity, apkFile);

				break;

			case -WHAT_DOWNLOAD_SUCCESS:
				activity.pb.setVisibility(View.GONE);
				AppContext.toastShow("插件下载失败");
				break;

			case -1:
				activity.pb.setVisibility(View.GONE);
				((AppException) msg.obj).makeToast(activity);
				break;
			default:
				break;
			}
		};

	}

	/**
	 * 充值完成后，客户端轮询检查充值结果
	 */
	public void checkRechargeBalance() {
		pb.setVisibility(View.VISIBLE);

		mTimerQueryRecharge = new Timer();
		mTimerQueryRecharge.scheduleAtFixedRate(new TimerTask() {

			Message msg = new Message();

			@Override
			public void run() {

				try {
					Recharge r = null;

					r = ac.queryRechargeBalance(ac.getLoginUserId(),
							mRechargeId);

					// 查询账户余额
					if (mRechargeId == null
							|| PAY_PLATFORM_UPPAY.equals(activity.payPlatform)) {
						mTimerQueryRecharge.cancel();
						mTimerQueryRecharge = null;

						msg.what = WHAT_QUERY_BALANCE;
						msg.obj = r;

						myHandler.sendMessage(msg);
					} else if (r.status == true) {
						// 查询充值结果
						mTimerQueryRecharge.cancel();
						mTimerQueryRecharge = null;

						msg.what = WHAT_RECHARGE_SUCCESS;
						msg.obj = r;

						myHandler.sendMessage(msg);
					}

				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;

					myHandler.sendMessage(msg);
				}

			}

		}, 3000, 3000);
	}

	private static final int PLUGIN_NOT_INSTALLED = -1;
	private static final int PLUGIN_NEED_UPGRADE = 2;

	/**
	 * 银联支付
	 * 
	 * @param tn
	 *            银联流水号
	 */
	public void topayUppay(String tn) {
		/*************************************************
		 * 
		 * 步骤2：通过银联工具类启动支付插件
		 * 
		 ************************************************/
		// mMode参数解释：
		// 0 - 启动银联正式环境
		// 1 - 连接银联测试环境
		/*****************************************************************
		 * mMode参数解释： "00"-启动银联正式环境; "01"-连接银联测试环境
		 *****************************************************************/
		String mMode = "00";

		int ret = UPPayAssistEx.startPay(this, null, null, tn, mMode);
		if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
			// 需要重新安装控件
			Log.e(TAG, " plugin not found or need upgrade!!!");

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("完成购买需要安装银联支付控件，是否安装？");

			builder.setNegativeButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

							// 下载安装银联插件
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
	}

	private void downloadUppay() {
		pb.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = URLs.DOWNLOAD_UPPAY_PLUGIN;
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
					myHandler.sendMessage(msg);
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_DOWNLOAD_SUCCESS;
					myHandler.sendMessage(msg);
				}
			}
		}).start();

	}

	/**
	 * 支付平台支付成功后，客户端轮询检查订单结果
	 */
	protected void checkOrderState() {
		pb.setVisibility(View.VISIBLE);

		mTimerCheckOrder = new Timer();
		mTimerCheckOrder.scheduleAtFixedRate(new TimerTask() {

			Message msg = new Message();

			@Override
			public void run() {

				try {

					Item r = ac.getOrderDetail(itemOrder.booksId,
							itemOrder.sellType);

					if (r.booksStatus != Item.ORDER_STATE_WAIT_PAY_ONLINE) {
						mTimerCheckOrder.cancel();
						mTimerCheckOrder = null;

						msg.what = WHAT_ORDER_DETAIL;
						msg.obj = r;

						myHandler.sendMessage(msg);
					}

				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;

					myHandler.sendMessage(msg);
				}

			}

		}, 3000, 3000);

	}

	protected void paySuccess() {
		ac.queryBalanceAfterPay(ac.getLoginUserId());

		OrderChangeEvent event = new OrderChangeEvent();
		event.change = true;
		EventBus.getDefault().post(event);

		noAnim = true;
		finish();

		// itemOrder.booksStatus = Item.ORDER_STATE_WAIT_RESPONSE;

		Intent intent = new Intent(activity, PayOnlineActivity.class);

		intent.putExtra(cn.op.common.constant.Keys.ORDER_IS_DETAIL, true);
		intent.putExtra(cn.op.common.constant.Keys.ORDER, itemOrder);
		activity.startActivity(intent);
	}

	protected void rechargeSuccess(Recharge recharge) {
		ac.user.balance = StringUtils.toFloat(recharge.balance);

		// setBalanceCbEnable();

		UserBalanceChangeEvent event = new UserBalanceChangeEvent();
		event.isChange = true;
		EventBus.getDefault().post(event);

		finish();
	}

}

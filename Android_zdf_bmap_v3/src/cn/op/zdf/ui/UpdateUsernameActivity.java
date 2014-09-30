package cn.op.zdf.ui;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.op.common.AppConfig;
import cn.op.common.AppException;
import cn.op.common.UIHelper;
import cn.op.common.constant.Constant;
import cn.op.common.constant.Keys;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.UserInfo;
import cn.op.common.util.Constants;
import cn.op.common.util.Log;
import cn.op.common.util.RegUtil;
import cn.op.common.util.StringUtils;
import cn.op.common.util.ViewFlipperUtil;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.event.UpdateUserEvent;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.meizu.smartbar.SmartBarUtils;
import com.tendcloud.tenddata.TCAgent;

import de.greenrobot.event.EventBus;

/**
 * 修改用户名、修改手机号
 * 
 * @author lufei
 * 
 */
public class UpdateUsernameActivity extends SherlockActivity {
	protected static final String TAG = Log
			.makeLogTag(UpdateUsernameActivity.class);

	private EditText etUsername;
	private EditText etVerifycode;
	private View pb;

	private AppContext ac;

	private LayoutInflater inflater;

	private ViewFlipper viewFlipper;

	private View layoutVerifyCode;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if (SmartBarUtils.hasSmartBar()) {
			getSherlock().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);

			setTheme(R.style.Holo_Theme_CustomAbsOverlay);

			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), true);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_username);

		ac = AppContext.getAc();
		inflater = LayoutInflater.from(ac);

		pb = findViewById(R.id.pb);
		View btnLeft = findViewById(R.id.btnLeft);
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);

		tvTitle.setText("修改手机");
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				back();
			}
		});

		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		View btnGetVerifycode = findViewById(R.id.btnNext);
		View layoutPsw = findViewById(R.id.layoutPsw);
		TextView tvTip = (TextView) findViewById(R.id.tvTip);
		final EditText etPsw = (EditText) findViewById(R.id.etPsw);
		etUsername = (EditText) findViewById(R.id.etPhone);

		UIHelper.limitPhoneEditTextInput(etUsername);

		if (UserInfo.USER_TYPE_NORMAL.equals(ac.user.userType)) {
			tvTip.setText("请输入登录密码及新的手机号码，我们会将验证码通过短信发送至您的手机");
			layoutPsw.setVisibility(View.VISIBLE);
		} else {
			// OAuth用户没有登录密码
			tvTip.setText("请输入新的手机号码，我们会将验证码通过短信发送至您的手机");
			layoutPsw.setVisibility(View.GONE);
		}

		btnGetVerifycode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				String psw = etPsw.getText().toString();
				String username = etUsername.getText().toString();

				if (UserInfo.USER_TYPE_NORMAL.equals(ac.user.userType)) {
					if (StringUtils.isEmpty(psw)) {
						AppContext.toastShow(R.string.pleaseInputLoginPsw);
						return;
					}

					if (!psw.equals(ac.user.login_pwd)) {
						AppContext.toastShow(R.string.pswIsIncorrect);
						return;
					}

					if (!RegUtil.isMobileNO(username)) {
						AppContext.toastShow(R.string.pleaseInputLegalPhone);
						return;
					}

					if (username.equals(ac.user.username)) {
						AppContext.toastShow(R.string.pleaseInputNewPhone);
						return;
					}

				} else {
					// OAuth用户没有登录密码
					if (!RegUtil.isMobileNO(username)) {
						AppContext.toastShow(R.string.pleaseInputNewPhone);
						return;
					}

					if (username.equals(ac.user.userPhone)) {
						AppContext.toastShow(R.string.pleaseInputNewPhone);
						return;
					}
				}

				showVerifyCode(username);

			}
		});

	}

	protected void back() {
		if (viewFlipper.getDisplayedChild() == 0) {
			clearLastGetVerifycode();
			super.onBackPressed();
		} else {
			ViewFlipperUtil.showPrevious(ac, viewFlipper);
		}
	}

	protected void showVerifyCode(String username) {

		if (timerGetVerifycode != null) {
			AppContext.toastShow(R.string.tip_get_verify_code_frequent);
			return;
		}

		if (layoutVerifyCode == null) {
			layoutVerifyCode = inflater.inflate(R.layout.layout_verify_code,
					viewFlipper, false);
		}

		ViewFlipperUtil.showNext(ac, viewFlipper, layoutVerifyCode);

		Button btnUpdate = (Button) layoutVerifyCode
				.findViewById(R.id.btnNextDown);
		TextView tvPhoneTip = (TextView) layoutVerifyCode
				.findViewById(R.id.tvPhoneTip);
		TextView tvCountDown = (TextView) layoutVerifyCode
				.findViewById(R.id.tvCountDown);
		etVerifycode = (EditText) layoutVerifyCode
				.findViewById(R.id.etVerifyCode);

		tvPhoneTip.setText("接收验证码的手机号为：" + username);
		btnUpdate.setText("完成");

		btnUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				String username = etUsername.getText().toString();
				String verifycode = etVerifycode.getText().toString();

				if (!RegUtil.isMobileNO(username)) {
					AppContext.toastShow(R.string.pleaseInputLegalPhone);
					return;
				}

				if (StringUtils.isEmpty(verifycode.trim())) {
					AppContext.toastShow(R.string.pleaseInputVerifycode);
					return;
				}

				if (!verifycode.equals(lastGetVerifycode)) {
					AppContext
							.toastShow(R.string.tip_get_verify_code_input_error);
					return;
				}

				if (!verifycode.equals(verifyCodeMap.get(username))) {
					AppContext
							.toastShow(R.string.tip_get_verify_code_phone_has_change);
					return;
				}

				updateUsername(ac.getLoginUserId(), username, verifycode);
			}
		});

		getVerifycode(username, tvCountDown);
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

		super.onPause();
		TCAgent.onPause(this);
		overridePendingTransition(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater supportMenuInflater = getSupportMenuInflater();

		if (SmartBarUtils.hasSmartBar()) {
			UIHelper.makeEmptyMenu(supportMenuInflater, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void finish() {
		timerCancel(null);
		UIHelper.hideSoftInput(getApplicationContext(), pb);
		super.finish();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "======onDestroy======");
		timerCancel(null);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		back();
	}

	protected void updateUsername(final String userId, final String username,
			String verifycode) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				pb.setVisibility(View.GONE);
				if (msg.what == 1) {
					RspMsg rsp = (RspMsg) msg.obj;

					if (rsp.OK()) {

						verifyCodeMap.remove(username);
						if (UserInfo.USER_TYPE_NORMAL.equals(ac.user.userType)) {
							ac.user.username = username;
							ac.user.userPhone = username;

							// 修改登录信息，以便下次自动登录使用
							AppConfig acf = AppConfig.getAppConfig(ac);
							acf.set(Keys.LAST_LOGIN_USERNAME, username);
						} else {
							ac.user.userPhone = username;
						}

						UpdateUserEvent event = new UpdateUserEvent();
						event.success = true;
						EventBus.getDefault().post(event);

						finish();
					} else {
						AppContext.toastShow(rsp.message);
					}

				} else if (msg.what == -1) {
					((AppException) msg.obj).makeToast(ac);
				}
			}
		};

		pb.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					RspMsg rsp = null;

					// 普通用户修改用户名,OAuth 用户修改手机号

					if (UserInfo.USER_TYPE_NORMAL.equals(ac.user.userType)) {
						rsp = ac.updateUsername(userId, username);
					} else {
						rsp = ac.updateUserPhone(userId, username);
					}

					msg.what = 1;
					msg.obj = rsp;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}

	public Timer timerGetVerifycode;
	private final int TIME_REMAIN = 120;
	private int timeRemain = TIME_REMAIN;
	protected static final int WHAT_TIMER_REMAIN = 2;
	protected static final int WHAT_GET_VERIFY_CODE = 3;
	protected String lastGetVerifycode;

	private HashMap<String, String> verifyCodeMap;

	protected void getVerifycode(final String username,
			final TextView btnGetVerifycode) {

		if (verifyCodeMap == null) {
			verifyCodeMap = new HashMap<String, String>();
		}

		if (timerGetVerifycode != null) {
			return;
		}

		timeRemain = TIME_REMAIN;

		final Handler handler = new Handler() {
			private RspMsg rsp;

			public void handleMessage(Message msg) {
				pb.setVisibility(View.GONE);
				if (msg.what == WHAT_GET_VERIFY_CODE) {
					rsp = (RspMsg) msg.obj;

					if (rsp.OK()) {

						lastGetVerifycode = rsp.message;
						verifyCodeMap.put(username, rsp.message);
						AppContext
								.toastShow(R.string.tip_get_verify_code_success);
					} else {
						Log.d(TAG, "======getVerifycode====== code="
								+ rsp.message + "，msg=" + rsp.message);

						AppContext.toastShow(rsp.message);

						timerCancel(btnGetVerifycode);
						timeRemain = TIME_REMAIN;
					}

				} else if (msg.what == WHAT_TIMER_REMAIN) {
					timeRemain--;

					btnGetVerifycode.setText("" + timeRemain);
					if (timeRemain <= 0) {
						timerCancel(btnGetVerifycode);
					}
				} else if (msg.what == -1) {
					timerCancel(btnGetVerifycode);
					((AppException) msg.obj).makeToast(ac);
				}
			}
		};

		pb.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					RspMsg rsp = null;

					String type;
					if (UserInfo.USER_TYPE_NORMAL.equals(ac.user.userType)) {
						type = Constant.GET_VERIFY_CODE_TYPE_UPDATE_USERNAME;
					} else {
						type = Constant.GET_VERIFY_CODE_TYPE_UPDATE_PHONE;
					}

					rsp = ac.getVerifyCode(username, type);

					msg.what = WHAT_GET_VERIFY_CODE;
					msg.obj = rsp;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = WHAT_TIMER_REMAIN;
				handler.sendMessage(message);
			}
		};

		timerGetVerifycode = new Timer();
		timerGetVerifycode.schedule(task, 0, 1000);
	}

	/**
	 * 清除上一次获得的验证码
	 */
	protected void clearLastGetVerifycode() {
		lastGetVerifycode = null;
	}

	protected void timerCancel(TextView tvCountDown) {
		if (timerGetVerifycode != null) {
			timerGetVerifycode.cancel();
			timerGetVerifycode = null;
		}

		if (tvCountDown != null) {
			tvCountDown.setClickable(true);
			tvCountDown.setText(getResources()
					.getString(R.string.getIdentiCode));
		}
	}

}

package cn.op.zdf.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.op.common.AppConfig;
import cn.op.common.AppException;
import cn.op.common.BaseApplication;
import cn.op.common.UIHelper;
import cn.op.common.constant.Constant;
import cn.op.common.constant.Keys;
import cn.op.common.constant.Tags;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.URLs;
import cn.op.common.domain.UserInfo;
import cn.op.common.util.Constants;
import cn.op.common.util.Log;
import cn.op.common.util.PhoneUtil;
import cn.op.common.util.RegUtil;
import cn.op.common.util.StringUtils;
import cn.op.common.util.ViewFlipperUtil;
import cn.op.zdf.ApiClient;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.domain.Room;
import cn.op.zdf.event.LoginEvent;
import cn.op.zdf.event.ResetPswSuccessEvent;

import com.ps.utils.verticalviewpager.PagerAdapter;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;

import de.greenrobot.event.EventBus;

/**
 * 登录、注册、特殊注册（无帐号预订）、找回密码、OAuth用户绑定手机号
 * 
 * @author lufei
 * 
 */
public class LoginFragment extends Fragment {

	private static final String TAG = Log.makeLogTag(LoginFragment.class);

	private final static int WHAT_INIT = 1;
	protected static final int WHAT_TIMER_REMAIN = 2;
	protected static final int WHAT_GET_VERIFY_CODE = 3;
	protected static final int WHAT_LOGIN_OAUTH = 4;
	protected static final int WHAT_LOGIN = 5;
	protected static final int WHAT_REGISTER_SPECIAL = 6;
	protected static final int WHAT_EXCEPTION = -1;
	protected static final int WHAT_OAUTH_BIND_PHONE = 7;

	LayoutInflater inflater;
	private AppContext ac;
	public ViewFlipper vfFindPsw;
	public View titleView;
	private View pb;
	private FragmentActivity activity;

	public Timer timerGetVerifycode;
	private String tag;

	protected String lastGetVerifycode;
	protected String username4Login;
	private View layoutNextGetVerifycode;

	private ViewFlipper viewFliper;

	private EditText etUsername;

	private final int TIME_REMAIN = 120;
	/**
	 * 获取验证码间隔时间
	 */
	private int timeRemain = TIME_REMAIN;

	protected static final int TYPE_LOGIN = 1;
	protected static final int TYPE_REGISTER = 2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ac = AppContext.getAc();
		activity = getActivity();
		this.inflater = inflater;

		View view = inflater.inflate(R.layout.frag_login, container, false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		return view;
	}

	private MyHandler myHandler = new MyHandler(this);

	private ViewGroup layoutLogin;

	private View layoutRegister;

	private TextView btnRight;

	private TextView tvTitle;

	private TextView tvShowRegister;

	private Button btnLogin;

	// /**
	// * 是否预订登录
	// */
	// private boolean isReserveLogin;

	/**
	 * 是否预订注册（无帐号预订）
	 */
	private boolean isReserveRegister;

	/**
	 * 是否是预订登录（预订前登录）、预订注册（无帐号预订）
	 */
	private boolean isSpecial;

	private String mUid;

	private String mUserType;

	private String mNickname;

	private View layoutOAuthBindPhone;

	protected String lastGetVerifycodePhone;

	/**
	 * 记录获取验证码时的: 手机号-验证码(key-value)
	 */
	private HashMap<String, String> verifyCodeMap;

	private ViewGroup layoutTemp;

	private boolean isLoginReserve;

	private View layoutOAuthLogin;

	static class MyHandler extends Handler {
		// 注意下面的“<T>”类是MyHandler类所在的外部类
		private WeakReference<LoginFragment> mWr;

		public MyHandler(LoginFragment frag) {
			mWr = new WeakReference<LoginFragment>(frag);
		}

		public void handleMessage(Message msg) {
			LoginFragment frag = mWr.get();

			// TODO 解决bug:Fragment被销毁了导致一些空指针异常、或者fragment not attached to
			// activity异常；原因是Fragment被销毁或者deatach后，线程才返回结果；最好的解决办法应该是，在Fragment被销毁时结束线程请求
			if (frag == null || !frag.isAdded()) {
				return;
			}

			switch (msg.what) {
			case WHAT_LOGIN:
				frag.pb.setVisibility(View.GONE);

				UserInfo user = (UserInfo) msg.obj;

				if (user.rspMsg.OK()) {
					frag.ac.user = user;

					if (frag.verifyCodeMap != null) {
						frag.verifyCodeMap.remove(user.username);
					}

					// 保存登录信息
					AppConfig acf = AppConfig.getAppConfig(frag.ac);
					acf.set(Keys.LAST_LOGIN_USER_TYPE, "" + user.userType);
					acf.set(Keys.LAST_LOGIN_USERNAME, user.username);
					acf.set(Keys.PSW, user.login_pwd);

					frag.activity.onBackPressed();
					UIHelper.hideSoftInput(frag.activity, frag.pb);

					LoginEvent event = new LoginEvent();
					event.success = true;
					event.isSpecial = frag.isSpecial;
					EventBus.getDefault().post(event);
				} else {
					AppContext.toastShow(user.rspMsg.message);
				}

				break;

			case -WHAT_LOGIN:
				frag.pb.setVisibility(View.GONE);
				AppContext.toastShow(R.string.pleaseRetry);
				((AppException) msg.obj).makeToast(frag.ac);
				break;

			case WHAT_REGISTER_SPECIAL:
				frag.pb.setVisibility(View.GONE);

				user = (UserInfo) msg.obj;

				if (user.rspMsg.OK()) {
					frag.ac.user = user;

					frag.verifyCodeMap.remove(user.username);

					// 保存登录信息
					AppConfig acf = AppConfig.getAppConfig(frag.ac);
					acf.set(Keys.LAST_LOGIN_USER_TYPE, "" + user.userType);
					acf.set(Keys.LAST_LOGIN_USERNAME, user.username);
					acf.set(Keys.PSW, user.login_pwd);

					frag.activity.onBackPressed();
					UIHelper.hideSoftInput(frag.activity, frag.pb);

					LoginEvent event = new LoginEvent();
					event.success = true;
					event.isSpecial = frag.isSpecial;
					EventBus.getDefault().post(event);
				} else {
					AppContext.toastShow(user.rspMsg.message);
				}
				break;

			case -WHAT_REGISTER_SPECIAL:
				frag.pb.setVisibility(View.GONE);
				AppContext.toastShow(R.string.pleaseRetry);
				((AppException) msg.obj).makeToast(frag.ac);
				break;

			case WHAT_GET_VERIFY_CODE:

				break;
			case -WHAT_GET_VERIFY_CODE:

				break;
			case WHAT_INIT:

				break;

			case WHAT_OAUTH_BIND_PHONE:
				frag.pb.setVisibility(View.GONE);

				user = (UserInfo) msg.obj;

				if (user.rspMsg.OK()
						|| RspMsg.CODE_OAUTH_BIND_PHONE
								.equals(user.rspMsg.code)) {

					frag.ac.user = user;

					if (frag.verifyCodeMap != null) {
						frag.verifyCodeMap.remove(user.username);
					}

					AppConfig acf = AppConfig.getAppConfig(frag.ac);
					acf.set(Keys.LAST_LOGIN_USER_TYPE, "" + user.userType);
					acf.set(Keys.LAST_LOGIN_USERNAME, user.username);
					acf.set(Keys.LAST_LOGIN_NICKNAME, user.nickname);

					LoginEvent event = new LoginEvent();
					event.success = true;
					EventBus.getDefault().post(event);

					frag.activity.finish();
				} else {
					AppContext.toastShow(user.rspMsg.message);
				}

				break;

			case WHAT_LOGIN_OAUTH:
				frag.pb.setVisibility(View.GONE);

				user = (UserInfo) msg.obj;

				if (user.rspMsg.OK()) {

					frag.ac.user = user;

					AppConfig acf = AppConfig.getAppConfig(frag.ac);
					acf.set(Keys.LAST_LOGIN_USER_TYPE, "" + user.userType);
					acf.set(Keys.LAST_LOGIN_USERNAME, user.username);
					acf.set(Keys.LAST_LOGIN_NICKNAME, user.nickname);

					LoginEvent event = new LoginEvent();
					event.success = true;
					EventBus.getDefault().post(event);

					if (frag.activity instanceof LoginActivity) {
						frag.activity.finish();
					} else {
						frag.activity.onBackPressed();
					}

				} else if (RspMsg.CODE_OAUTH_LOGIN_NOT_BIND_PHONE
						.equals(user.rspMsg.code)
						|| RspMsg.CODE_OAUTH_LOGIN_NOT_EXIST
								.equals(user.rspMsg.code)) {

					if (frag.activity instanceof LoginActivity) {
						LoginActivity loginActivity = (LoginActivity) frag.activity;
						loginActivity.pb.setVisibility(View.GONE);
					}

					// 引导OAuth用户绑定手机号
					frag.showOAuthBindPhone(frag.mUid, frag.mUserType,
							frag.mNickname);
				} else {
					AppContext.toastShow(user.rspMsg.message);
				}

				break;

			case -WHAT_LOGIN_OAUTH:
				frag.pb.setVisibility(View.GONE);
				AppContext.toastShow(R.string.pleaseRetry);
				((AppException) msg.obj).makeToast(frag.ac);

				break;
			}
		}
	};

	protected void login(final String username, final String psw,
			final String verifycode, final int type) {

		if (!ac.isNetworkConnected()) {
			AppContext.toastShow(R.string.pleaseCheckNet);
			return;
		}

		if (!RegUtil.isMobileNO(username)) {
			AppContext.toastShow(R.string.pleaseInputLegalPhone);
			return;
		}

		if (type == TYPE_REGISTER) {
			if (StringUtils.isEmpty(verifycode.trim())) {
				AppContext.toastShow(R.string.pleaseInputVerifycode);
				return;
			}
			if (!verifycode.equals(lastGetVerifycode)) {
				AppContext.toastShow(R.string.tip_get_verify_code_input_error);
				return;
			}

			if (!verifycode.equals(verifyCodeMap.get(username))) {
				AppContext
						.toastShow(R.string.tip_get_verify_code_phone_has_change);
				return;
			}

		}

		if (!RegUtil.isLegalPsw(psw)) {
			AppContext.toastShow(R.string.pleaseInputLegalPassword);
			return;
		}

		pb.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					UserInfo user = null;
					if (type == TYPE_LOGIN) {
						user = ac.login(username, psw);
					}
					if (type == TYPE_REGISTER) {
						user = ac.register(username, psw, verifycode);
					}

					user.login_pwd = psw;

					msg.what = WHAT_LOGIN;
					msg.obj = user;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_LOGIN;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 引导OAuth用户绑定手机号
	 * 
	 * @param uid
	 * @param userType
	 * @param nickname
	 */
	public void showOAuthBindPhone(final String uid, final String userType,
			final String nickname) {

		btnRight.setVisibility(View.INVISIBLE);
		tvTitle.setText("绑定手机号");

		if (layoutOAuthBindPhone == null) {
			layoutOAuthBindPhone = inflater.inflate(
					R.layout.layout_oauth_bind_phone, viewFliper, false);
		}

		View btnGetVerifycode = layoutOAuthBindPhone
				.findViewById(R.id.btnGetVerifycode);
		Button btnDone = (Button) layoutOAuthBindPhone
				.findViewById(R.id.btnRegister);

		final EditText etPhone = (EditText) layoutOAuthBindPhone
				.findViewById(R.id.login_et_name);

		final EditText etVerifycode = (EditText) layoutOAuthBindPhone
				.findViewById(R.id.etVerifycode);

		UIHelper.limitPhoneEditTextInput(etPhone);

		btnGetVerifycode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				String username = etPhone.getText().toString();

				getVerifycode(username,
						Constant.GET_VERIFY_CODE_TYPE_BIND_PHONE, (Button) v);
			}
		});

		btnDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				String phone = etPhone.getText().toString();
				String verifycode = etVerifycode.getText().toString();

				oauthBindPhone(uid, userType, nickname, phone, verifycode);
			}
		});

		ViewFlipperUtil.showNext(activity, viewFliper, layoutOAuthBindPhone);
	}

	protected void oauthBindPhone(final String uid, final String userType,
			final String nickname, final String phone, final String verifycode) {
		if (!RegUtil.isMobileNO(phone)) {
			AppContext.toastShow(R.string.pleaseInputLegalPhone);
			return;
		}

		if (StringUtils.isEmpty(verifycode.trim())) {
			AppContext.toastShow(R.string.pleaseInputVerifycode);
			return;
		}
		if (!verifycode.equals(lastGetVerifycode)) {
			AppContext.toastShow(R.string.tip_get_verify_code_input_error);
			return;
		}

		if (!verifycode.equals(verifyCodeMap.get(phone))) {
			AppContext.toastShow(R.string.tip_get_verify_code_phone_has_change);
			return;
		}

		pb.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			public void run() {

				Message msg = new Message();

				try {
					UserInfo userInfo = ApiClient.oauthBindPhone(uid, userType,
							nickname, phone);

					msg.what = WHAT_OAUTH_BIND_PHONE;
					msg.obj = userInfo;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = WHAT_EXCEPTION;
					msg.obj = e;
				}

				myHandler.sendMessage(msg);
			}
		}).start();
	}

	private void registerSpecial(final String username, final String realname,
			String verifycode) {

		if (!RegUtil.isMobileNO(username)) {
			AppContext.toastShow(R.string.pleaseInputLegalPhone);
			return;
		}

		if (StringUtils.isEmpty(verifycode.trim())) {
			AppContext.toastShow(R.string.pleaseInputVerifycode);
			return;
		}
		if (!verifycode.equals(lastGetVerifycode)) {
			AppContext.toastShow(R.string.tip_get_verify_code_input_error);
			return;
		}

		if (!verifycode.equals(verifyCodeMap.get(username))) {
			AppContext.toastShow(R.string.tip_get_verify_code_phone_has_change);
			return;
		}

		pb.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			public void run() {

				Message msg = new Message();

				try {
					UserInfo userInfo = ApiClient.registerSpecial(username,
							realname);

					msg.what = WHAT_REGISTER_SPECIAL;
					msg.obj = userInfo;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = WHAT_EXCEPTION;
					msg.obj = e;
				}

				myHandler.sendMessage(msg);
			}
		}).start();

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "======onViewCreated======");

		if (savedInstanceState != null) {
			String verifycode = savedInstanceState
					.getString("lastGetVerifycode");
			if (verifycode != null) {
				lastGetVerifycode = verifycode;
			}
		}

		pb = view.findViewById(R.id.pb);
		pb.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});

		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		ImageView btnLeft = (ImageView) view.findViewById(R.id.btnLeft);
		btnRight = (TextView) view.findViewById(R.id.tvRight);

		tvTitle.setText("登录");
		btnRight.setVisibility(View.VISIBLE);
		btnRight.setText("忘记密码");

		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				back();
			}
		});

		btnRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				showFindPsw();
			}
		});

		viewFliper = (ViewFlipper) view.findViewById(R.id.layoutContentLogin);

		tag = getTag();
		if (Tags.RESERVE_LOGIN.equals(tag)) {
			ac.isReserve = false;
			isSpecial = true;
		}

		Bundle arg = getArguments();
		if (arg != null) {
			isReserveRegister = arg.getBoolean(Keys.RESERVE_REGISTER, false);
			isLoginReserve = arg.getBoolean(Keys.LOGIN_RESERVE, false);
		}

		if (isReserveRegister) {
			// 无帐号预订
			showRegister();
		} else {
			initLayoutLogin();
		}
	}

	private void initLayoutLogin() {
		layoutLogin = (ViewGroup) viewFliper.findViewById(R.id.layoutLogin);
		layoutTemp = (ViewGroup) viewFliper.findViewById(R.id.layoutTemp);

		btnLogin = (Button) layoutLogin.findViewById(R.id.btnLogin);
		layoutOAuthLogin = layoutLogin.findViewById(R.id.layoutOAuthLogin);
		final View layoutOAuthLoginBtn = layoutLogin
				.findViewById(R.id.layoutOAuthLoginBtn);
		tvShowRegister = (TextView) layoutLogin
				.findViewById(R.id.tvShowRegister);
		etUsername = (EditText) layoutLogin.findViewById(R.id.login_et_name);
		final EditText etPsw = (EditText) layoutLogin
				.findViewById(R.id.login_et_pwd);
		final ImageView ivLoginOauthHandler = (ImageView) layoutOAuthLogin
				.findViewById(R.id.ivLoginOauthHandler);
		final ImageView ivLoginQQ = (ImageView) layoutOAuthLogin
				.findViewById(R.id.ivLoginQQ);
		final ImageView ivLoginSina = (ImageView) layoutOAuthLogin
				.findViewById(R.id.ivLoginSina);

		AppConfig appConfig = AppConfig.getAppConfig(ac);
		if (appConfig.get(Keys.IS_FIRST_ENTER) == null) {
			appConfig.set(Keys.IS_FIRST_ENTER, "no");
		}

		UIHelper.limitPhoneEditTextInput(etUsername);
		// UIHelper.limitPswEditTextInput(etPsw);

		AppConfig acf = AppConfig.getAppConfig(ac);
		String username = acf.get(Keys.LAST_LOGIN_USERNAME);

		if (username != null && RegUtil.isMobileNO(username)) {
			if (!isLoginReserve) {
				etUsername.setText(username);
			}
		}

		tvShowRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showRegister();
			}
		});

		layoutOAuthLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				if (layoutOAuthLoginBtn.getVisibility() == View.VISIBLE) {
					layoutOAuthLoginBtn.setVisibility(View.GONE);
					ivLoginOauthHandler
							.setImageResource(R.drawable.img_login_oauth_handler_up);
				} else {
					layoutOAuthLoginBtn.setVisibility(View.VISIBLE);
					ivLoginOauthHandler
							.setImageResource(R.drawable.img_login_oauth_handler_down);
				}

			}
		});

		ivLoginSina.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				doOauthVerify(SHARE_MEDIA.SINA);

				// 用户是否已经授权，比较耗时
				// boolean authenticated = OauthHelper.isAuthenticated(
				// activity, SHARE_MEDIA.SINA);
				// if (authenticated) {
				//
				// getPlatformInfoAfterOauthVerifyComplete(SHARE_MEDIA.SINA);
				// } else {
				//
				// doOauthVerify(SHARE_MEDIA.SINA);
				// }
			}
		});

		ivLoginQQ.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				doOauthVerify(SHARE_MEDIA.QZONE);
				// 用户是否已经授权，比较耗时
				// boolean authenticated = OauthHelper.isAuthenticated(
				// activity, SHARE_MEDIA.QZONE);
				// if (authenticated) {
				//
				// getPlatformInfoAfterOauthVerifyComplete(SHARE_MEDIA.QZONE);
				// } else {
				// doOauthVerify(SHARE_MEDIA.QZONE);
				// }

			}

		});

		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				final String username = etUsername.getText().toString();
				final String psw = etPsw.getText().toString();

				login(username, psw, null, TYPE_LOGIN);
			}
		});

		initReserveLogin();

	}

	/**
	 * 登录并预定
	 */
	private void initReserveLogin() {
		// ======提交订单，登录并预订
		Bundle arg = getArguments();
		if (arg != null) {

			String username4OrderSubmit = arg.getString(Keys.PHONE);

			String hotelsName = arg.getString(Keys.NAME);
			String hotelsPhyaddress = arg.getString(Keys.ADDR);
			String logopath = arg.getString(Keys.LOGO);
			String roomPrice = arg.getString(Keys.PRICE);
			String hotelsDuration = arg.getString(Keys.HOURS);
			String roomTypeName = arg.getString(Keys.ROOM_TYPE);
			int sellType = arg.getInt(Keys.SALE_TYPE);

			if (isLoginReserve) {
				// 隐藏OAuth登录
				layoutOAuthLogin.setVisibility(View.GONE);

				// etUsername.setText(username4OrderSubmit);
				btnLogin.setText("登录并预订");
				tvShowRegister.setText("无帐号预订");

				View layoutReverseRoom = inflater.inflate(
						R.layout.layout_reserve_room, layoutTemp, true);
				layoutReverseRoom.findViewById(R.id.layoutBtn).setVisibility(
						View.GONE);

				TextView tvHotelNameOrder = (TextView) layoutReverseRoom
						.findViewById(R.id.tvHotelNameOrder);
				TextView tvAddrOrder = (TextView) layoutReverseRoom
						.findViewById(R.id.tvAddrOrder);
				ImageView ivHotelLogoOrder = (ImageView) layoutReverseRoom
						.findViewById(R.id.ivHotelLogoOrder);
				TextView tvHoursOrder = (TextView) layoutReverseRoom
						.findViewById(R.id.tvHoursOrder);
				TextView tvRoomTypeName = (TextView) layoutReverseRoom
						.findViewById(R.id.tvRoomTypeOrder);
				TextView tvRoomPrice = (TextView) layoutReverseRoom
						.findViewById(R.id.textView5);

				tvHotelNameOrder.setText(hotelsName);
				tvAddrOrder.setText(hotelsPhyaddress);
				ac.mImageLoader.displayImage(URLs.URL_ZDF_API + logopath,
						ivHotelLogoOrder, ac.optionsLogo);

				tvRoomPrice.setText(roomPrice);
				if (sellType == Room.SALE_TYPE_ZDF) {

					tvHoursOrder.setText(hotelsDuration);

					String roomType = "小时";

					if (!StringUtils.isEmpty(roomTypeName)) {
						roomType += "  " + roomTypeName;
					}

					tvRoomTypeName.setText(roomType);

				} else if (sellType == Room.SALE_TYPE_WYF
						|| sellType == Room.SALE_TYPE_LSF) {

					if (!StringUtils.isEmpty(roomTypeName)) {
						tvRoomTypeName.setText(roomTypeName);
					} else {
						tvRoomTypeName.setText("午夜房");
					}
				}
			}
		}
	}

	/**
	 * 注册、无帐号预订
	 */
	protected void showRegister() {

		btnRight.setVisibility(View.INVISIBLE);

		if (layoutRegister == null) {
			layoutRegister = inflater.inflate(R.layout.layout_register,
					viewFliper, false);
		}

		titleView = layoutRegister.findViewById(R.id.tvTitle);
		Button btnGetVerifycode = (Button) layoutRegister
				.findViewById(R.id.btnGetVerifycode);
		Button btnRegister = (Button) layoutRegister
				.findViewById(R.id.btnRegister);
		View layoutPsw = layoutRegister.findViewById(R.id.layoutPsw);
		final EditText etUsername = (EditText) layoutRegister
				.findViewById(R.id.login_et_name);
		final EditText etPsw = (EditText) layoutRegister
				.findViewById(R.id.login_et_pwd);
		final EditText etVerifycode = (EditText) layoutRegister
				.findViewById(R.id.etVerifycode);

		etVerifycode.setText("");
		etPsw.setText("");

		UIHelper.limitPhoneEditTextInput(etUsername);

		btnGetVerifycode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				String username = etUsername.getText().toString();

				String type;
				if (isSpecial) {
					type = Constant.GET_VERIFY_CODE_TYPE_REGISTER_SPECIAL;
				} else {
					type = Constant.GET_VERIFY_CODE_TYPE_REGISTER;
				}

				getVerifycode(username, type, (Button) v);
			}
		});

		btnGetVerifycode.setClickable(true);
		btnGetVerifycode.setText(getResources().getString(
				R.string.getIdentiCode));

		// ======无帐号预订
		if (isSpecial) {

			layoutPsw.setVisibility(View.GONE);

			// Bundle arg = getArguments();
			// String username4OrderSubmit = arg.getString(Keys.PHONE);
			// if (!StringUtils.isEmpty(username4OrderSubmit)) {
			// etUsername.setText(username4OrderSubmit);
			// }

			tvTitle.setText("无帐号预订");
			btnRegister.setText("无帐号预订");

			btnRegister.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (UIHelper.isFastDoubleClick(v)) {
						return;
					}

					String username = etUsername.getText().toString();
					String verifycode = etVerifycode.getText().toString();

					String realname = null;
					registerSpecial(username, realname, verifycode);
				}
			});
		} else {

			tvTitle.setText("注册");
			btnRegister.setText("注册");

			btnRegister.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (UIHelper.isFastDoubleClick(v)) {
						return;
					}

					String username = etUsername.getText().toString();
					String psw = etPsw.getText().toString();
					String verifycode = etVerifycode.getText().toString();

					login(username, psw, verifycode, TYPE_REGISTER);
				}
			});
		}

		ViewFlipperUtil.showNext(activity, viewFliper, layoutRegister);
	}

	/**
	 * 忘记密码：1.输入手机号
	 */
	protected void showFindPsw() {
		tvTitle.setText("忘记密码");
		btnRight.setVisibility(View.INVISIBLE);

		if (vfFindPsw == null) {
			vfFindPsw = (ViewFlipper) inflater.inflate(
					R.layout.layout_find_psw, viewFliper, false);
		}

		layoutNextGetVerifycode = vfFindPsw
				.findViewById(R.id.layoutNextGetVerifycode);
		final EditText etLoginPhone = (EditText) layoutNextGetVerifycode
				.findViewById(R.id.etLoginPhone);
		View btnNextGetVerifycode = vfFindPsw
				.findViewById(R.id.btnNextGetVerifycode);

		etLoginPhone.setText(etUsername.getText().toString());
		UIHelper.limitPhoneEditTextInput(etLoginPhone);

		btnNextGetVerifycode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				String username = etLoginPhone.getText().toString();

				if (StringUtils.isEmpty(username)) {
					AppContext.toastShow("请填写绑定手机号码");
					return;
				}

				if (!RegUtil.isMobileNO(username)) {
					AppContext.toastShow(R.string.pleaseInputLegalPhone);
					return;
				}

				// if (lastGetVerifycodePhone != null
				// && !username.equals(lastGetVerifycodePhone)) {
				//
				// AppContext.toastShow(R.string.tip_get_verify_code_frequent);
				// return;
				// }

				showVerifyCode(username);
			}
		});

		ViewFlipperUtil.showNext(activity, viewFliper, vfFindPsw);
	}

	/**
	 * 忘记密码：2.输入验证码
	 * 
	 * @param phone
	 */
	protected void showVerifyCode(final String phone) {

		tvTitle.setText("输入验证码");

		Animation inAnimation = AnimationUtils.loadAnimation(activity,
				R.anim.slide_in_from_right);
		Animation outAnimation = AnimationUtils.loadAnimation(activity,
				R.anim.slide_out_to_left);

		vfFindPsw.setInAnimation(inAnimation);
		vfFindPsw.setOutAnimation(outAnimation);
		vfFindPsw.showNext();

		View layoutVerifyCode = vfFindPsw.findViewById(R.id.layoutVerifyCode);

		Button btnNext = (Button) layoutVerifyCode
				.findViewById(R.id.btnNextDown);
		TextView tvPhoneTip = (TextView) layoutVerifyCode
				.findViewById(R.id.tvPhoneTip);
		TextView tvCountDown = (TextView) layoutVerifyCode
				.findViewById(R.id.tvCountDown);
		final EditText etVerifycode = (EditText) layoutVerifyCode
				.findViewById(R.id.etVerifyCode);

		tvPhoneTip.setText("接收验证码的手机号为：" + phone);

		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				String verifycode = etVerifycode.getText().toString();

				if (StringUtils.isEmpty(verifycode.trim())) {
					AppContext.toastShow(R.string.pleaseInputVerifycode);
					return;
				}

				if (!verifycode.equals(lastGetVerifycode)) {
					AppContext
							.toastShow(R.string.tip_get_verify_code_input_error);
					return;
				}

				if (!verifycode.equals(verifyCodeMap.get(phone))) {
					AppContext
							.toastShow(R.string.tip_get_verify_code_phone_has_change);
					return;
				}

				showResetPswDone(phone);
			}
		});

		if (timerGetVerifycode != null && phone.equals(lastGetVerifycodePhone)) {
			return;
		} else {
			getVerifycode(phone, Constant.GET_VERIFY_CODE_TYPE_FORGET_PSW,
					tvCountDown);
		}

	}

	/**
	 * 忘记密码：3.修改密码
	 * 
	 * @param username
	 */
	protected void showResetPswDone(final String username) {
		tvTitle.setText("修改密码");

		Animation inAnimation = AnimationUtils.loadAnimation(activity,
				R.anim.slide_in_from_right);
		Animation outAnimation = AnimationUtils.loadAnimation(activity,
				R.anim.slide_out_to_left);

		vfFindPsw.setInAnimation(inAnimation);
		vfFindPsw.setOutAnimation(outAnimation);
		vfFindPsw.showNext();

		View layoutResetPswDown = vfFindPsw
				.findViewById(R.id.layoutResetPswDown);
		final EditText etResetPsw = (EditText) layoutResetPswDown
				.findViewById(R.id.etNewPsw);
		final EditText etNewPswRepeat = (EditText) layoutResetPswDown
				.findViewById(R.id.etNewPswRepeat);

		Button btnResetPsw = (Button) vfFindPsw.findViewById(R.id.btnResetPsw);

		btnResetPsw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				final String newPsw = etResetPsw.getText().toString();
				final String newPswRepeat = etNewPswRepeat.getText().toString();

				resetPsw(username, newPsw, newPswRepeat);
			}
		});
	}

	void back() {
		if (layoutLogin != null && layoutLogin.getVisibility() == View.VISIBLE) {
			UIHelper.hideSoftInput(activity, layoutLogin);

			if (isSpecial) {
				// activity 是 HotelDetailActivity
				activity.onBackPressed();
			} else {
				// activity 是 LoginActivity
				activity.finish();
			}

		} else if (vfFindPsw != null
				&& vfFindPsw.getCurrentView() != layoutNextGetVerifycode) {
			vfFindPsw.setInAnimation(activity, R.anim.slide_in_from_left);
			vfFindPsw.setOutAnimation(activity, R.anim.slide_out_to_right);
			vfFindPsw.showPrevious();

			int backShowViewId = vfFindPsw.getCurrentView().getId();

			switch (backShowViewId) {
			case R.id.layoutVerifyCode:
				tvTitle.setText("输入验证码");
				break;

			case R.id.layoutNextGetVerifycode:
				tvTitle.setText("忘记密码");
				break;
			}

		} else {

			int backShowViewId = ViewFlipperUtil.showPrevious(activity,
					viewFliper);

			switch (backShowViewId) {
			case R.id.layoutLogin:
				tvTitle.setText("登录");
				btnRight.setVisibility(View.VISIBLE);

				clearLastGetVerifycode();
				timerCancel(null);
				break;
			}
		}
	}

	protected void getVerifycode(final String username, final String type,
			final TextView tvCountDown) {

		if (verifyCodeMap == null) {
			verifyCodeMap = new HashMap<String, String>();
		}

		if (timerGetVerifycode != null) {
			AppContext.toastShow(R.string.tip_get_verify_code_frequent);
			return;
		}

		if (!RegUtil.isMobileNO(username)) {
			AppContext.toastShow(R.string.pleaseInputLegalPhone);
			return;
		}

		timeRemain = TIME_REMAIN;

		// TODO 使用成员变量mHandler
		final Handler handler = new Handler() {
			private RspMsg rsp;

			public void handleMessage(Message msg) {

				pb.setVisibility(View.GONE);
				if (msg.what == WHAT_GET_VERIFY_CODE) {
					rsp = (RspMsg) msg.obj;

					if (rsp.OK()) {

						username4Login = username;
						lastGetVerifycode = rsp.message;
						lastGetVerifycodePhone = username;

						verifyCodeMap.put(username, rsp.message);
						AppContext
								.toastShow(R.string.tip_get_verify_code_success);
					} else if (RspMsg.CODE_GET_VERIFYCODE_USER_EXIST
							.equals(rsp.code)) {

						AppContext.toastShow(rsp.message);

						timerCancel(tvCountDown);
						timeRemain = TIME_REMAIN;

					} else {
						Log.d(TAG, "======getVerifycode====== code="
								+ rsp.message + "，msg=" + rsp.message);
						AppContext
								.toastShow(R.string.tip_get_verify_code_retry);

						timerCancel(tvCountDown);
						timeRemain = TIME_REMAIN;
					}

				} else if (msg.what == WHAT_TIMER_REMAIN) {
					timeRemain--;

					tvCountDown.setText("" + timeRemain);
					if (timeRemain <= 0) {
						timerCancel(tvCountDown);
					}
				} else if (msg.what == -WHAT_GET_VERIFY_CODE) {
					timerCancel(tvCountDown);
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

					rsp = ac.getVerifyCode(username, type);

					msg.what = WHAT_GET_VERIFY_CODE;
					msg.obj = rsp;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_GET_VERIFY_CODE;
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
		lastGetVerifycodePhone = null;
	}

	/**
	 * 取消获取验证码计时
	 * 
	 * @param tvCountDown
	 */
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

	@Override
	public void onHiddenChanged(boolean hidden) {
		Log.d(TAG, "======onHiddenChanged======");
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString("lastGetVerifycode", lastGetVerifycode);

		Log.d(TAG, "======onSaveInstanceState======");
	}

	@Override
	public void onPause() {
		Log.d(TAG, "======onPause======");

		if (Tags.RESERVE_LOGIN.equals(tag)) {
			ac.isReserve = true;
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.d(TAG, "======onResume======");

		if (Tags.RESERVE_LOGIN.equals(tag)) {
			ac.isReserve = false;
		}

		super.onResume();
	}

	@Override
	public void onDestroyView() {
		Log.d(TAG, "======onDestroyView======");
		timerCancel(null);
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		Log.d(TAG, "======onDetach======");
		super.onDetach();
	}

	// public void hideFindPsw() {
	// btnGetVerifycode.setText("获取验证码");
	// animation(false, layoutFindPsw);
	// layoutFindPsw.setVisibility(View.GONE);
	// layoutResetPsw2.setVisibility(View.GONE);
	// layoutResetPsw1.setVisibility(View.VISIBLE);
	// btnResetPsw.setText("下一步");
	// }

	protected void resetPsw(final String phone, final String newPsw,
			final String newPswRepeat) {

		if (StringUtils.isEmpty(newPsw)) {
			AppContext.toastShow(R.string.pleaseInputNewPsw);
			return;
		}

		if (!RegUtil.isLegalPsw(newPsw)) {
			AppContext.toastShow("新密码不合法");
			return;
		}

		if (StringUtils.isEmpty(newPswRepeat)) {
			AppContext.toastShow("请再次填写新密码");
			return;
		}

		if (!newPsw.equals(newPswRepeat)) {
			AppContext.toastShow("两次填写的新密码需一致");
			return;
		}

		final Handler handler = new Handler() {
			private RspMsg rsbMsg;

			public void handleMessage(Message msg) {
				pb.setVisibility(View.GONE);
				if (msg.what == 1) {
					rsbMsg = (RspMsg) msg.obj;

					if (rsbMsg == null) {
						AppContext.toastShow(R.string.unCorrectUsernameOrPsw);
						return;
					}

					if (rsbMsg.OK()) {
						BaseApplication.toastShow(rsbMsg.message);

						UIHelper.hideSoftInput(activity, titleView);
						// hideFindPsw();
						// if (etUsername4Login != null) {
						// etUsername4Login.setText(username4Login);
						// }

						ResetPswSuccessEvent event = new ResetPswSuccessEvent();
						event.isSuccess = true;
						EventBus.getDefault().post(event);

						activity.finish();

					} else {
						AppContext.toastShow(rsbMsg.message);
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
					// RspMsg user = ac.findPsw(phone);
					RspMsg rsp = ac.resetPsw(phone, newPsw);

					if (rsp.OK()) {
						try {
							// login
							UserInfo user = ac.login(phone, newPsw);

							if (user != null && user.rspMsg.OK()) {
								user.login_pwd = newPsw;
								ac.user = user;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

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

	private void doOauthVerify(SHARE_MEDIA platform) {
		UMSocialService mController = UMServiceFactory.getUMSocialService(
				"com.umeng.login", RequestType.SOCIAL);

		switch (platform) {
		case QZONE:
			mController.getConfig().setSsoHandler(
					new QZoneSsoHandler(getActivity()));
			break;
		case SINA:
			mController.getConfig().setSsoHandler(new SinaSsoHandler());
			break;

		default:
			break;
		}

		mController.doOauthVerify(activity, platform, new UMAuthListener() {

			@Override
			public void onStart(SHARE_MEDIA platform) {
				Log.d(TAG, "======doOauthVerify======授权开始");
			}

			@Override
			public void onError(SocializeException e, SHARE_MEDIA platform) {
				Log.d(TAG, "======doOauthVerify======授权错误");
			}

			@Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				Log.d(TAG, "======doOauthVerify======授权成功");

				pb.setVisibility(View.VISIBLE);
				// 获取相关授权信息或者跳转到自定义的分享编辑页面
				getPlatformInfoAfterOauthVerifyComplete(platform);
			}

			@Override
			public void onCancel(SHARE_MEDIA platform) {
				Log.d(TAG, "======doOauthVerify======授权取消");
			}

		});
	}

	protected void getPlatformInfoAfterOauthVerifyComplete(
			final SHARE_MEDIA platform) {
		UMSocialService mController = UMServiceFactory.getUMSocialService(
				"com.umeng.login", RequestType.SOCIAL);

		mController.getPlatformInfo(activity, platform, new UMDataListener() {
			@Override
			public void onStart() {
				Log.d(TAG, "======getPlatformInfo======onStart()");
			}

			@Override
			public void onComplete(int status, Map<String, Object> info) {
				Log.d(TAG, "======getPlatformInfo======onComplete()");

				if (status == 200 && info != null) {

					// 打印信息
					// StringBuilder sb = new StringBuilder();
					// Set<String> keys = info.keySet();
					// for (String key : keys) {
					// sb.append(key + "=" + info.get(key).toString() + "\r\n");
					// }
					// Log.d(TAG,
					// "======getPlatformInfo======\r\n" + sb.toString());

					String uid = info.get("uid").toString();
					String screen_name = info.get("screen_name").toString();
					// String profile_image_url = info.get("profile_image_url")
					// .toString();

					// 登录桔家
					String userType = null;
					switch (platform) {
					case QZONE:
						userType = UserInfo.USER_TYPE_OAUTH_QZONE;
						break;
					case SINA:
						userType = UserInfo.USER_TYPE_OAUTH_SINA;
						break;
					}

					loginOAuth(uid, userType, screen_name);

				} else {
					Log.d(TAG, "====== getPlatformInfo ====== 发生错误:status="
							+ status);
				}
			}
		});
	}

	protected void loginOAuth(final String uid, final String userType,
			final String nickname) {

		pb.setVisibility(View.VISIBLE);

		mUid = uid;
		mUserType = userType;
		mNickname = nickname;

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					UserInfo user = ac.loginOAuth(uid, userType, nickname);

					msg.what = WHAT_LOGIN_OAUTH;
					msg.obj = user;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_LOGIN_OAUTH;
					msg.obj = e;
				}

				myHandler.sendMessage(msg);
			}
		}.start();
	}

}
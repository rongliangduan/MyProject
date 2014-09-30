package cn.op.zdf.ui;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.domain.Recharge;
import cn.op.zdf.event.Event;
import cn.op.zdf.event.LoginEvent;
import cn.op.zdf.event.UpdateUserEvent;
import cn.op.zdf.event.UserBalanceChangeEvent;

import com.meizu.smartbar.SmartBarUtils;

import de.greenrobot.event.EventBus;

/**
 * 个人资料、注销、修改密码
 * 
 * @author lufei
 * 
 */
public class AccountFragment extends BaseFragment {
	private String TAG = Log.makeLogTag(getClass());
	private MainActivity activity;
	private AppContext ac;

	protected static final int WHAT_QUERY_BALANCE = 1;

	LayoutInflater inflater;
	private View layoutChangePsw;
	private TextView tvUsername;
	private TextView etRealName;
	private TextView etEmail;
	private View pb;
	private TextView etPhone;
	private View layouPhone;
	private TextView tvBalance;
	private View layoutOAuthType;
	private TextView tvOAuthType;
	private TextView tvOAuthAlias;
	private Button btnGetCash;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ac = AppContext.getAc();
		activity = (MainActivity) getActivity();
		EventBus.getDefault().register(this);

		this.inflater = inflater;
		View view = inflater.inflate(R.layout.frag_account, container, false);

		// if (SmartBarUtils.hasSmartBar()) {
		// view.setPadding(0,
		// getResources().getDimensionPixelSize(R.dimen.marginTopAbs),
		// 0,
		// getResources().getDimensionPixelSize(R.dimen.marginTopAbs));
		// }

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "======onViewCreated======");
		super.onViewCreated(view, savedInstanceState);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		pb = view.findViewById(R.id.pb);
		pb.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});

		View btnShowRecharge = view.findViewById(R.id.btnShowRecharge);
		btnGetCash = (Button) view.findViewById(R.id.btnGetCash);
		layoutChangePsw = view.findViewById(R.id.layoutChangePsw);
		tvUsername = (TextView) view.findViewById(R.id.tvUsername);
		tvBalance = (TextView) view.findViewById(R.id.tvBalance);
		etRealName = (TextView) view.findViewById(R.id.tvRealName);
		etPhone = (TextView) view.findViewById(R.id.etPhone);
		etEmail = (TextView) view.findViewById(R.id.tvEmail);
		layouPhone = view.findViewById(R.id.layouPhone);
		View layoutEmail = view.findViewById(R.id.layoutEmail);
		View layoutRealname = view.findViewById(R.id.layoutRealname);
		View btnLogout = view.findViewById(R.id.btnLogout);
		layoutOAuthType = view.findViewById(R.id.layoutOAuthType);
		tvOAuthType = (TextView) layoutOAuthType.findViewById(R.id.tvOAuthType);
		tvOAuthAlias = (TextView) layoutOAuthType
				.findViewById(R.id.tvOAuthAlias);

		// UIHelper.limitChineseTextViewInput(etRealName);
		// UIHelper.limitPhoneTextViewInput(etPhone);

		setUserInfo();

		// btnUpdateUser.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (UIHelper.isFastDoubleClick(v)) {
		// return;
		// }
		//
		// if (SmartBarUtils.hasSmartBar()) {
		// if (isEditUserInfo) {
		// String realName = etRealName.getText().toString();
		// String email = etEmail.getText().toString();
		// String phone = etPhone.getText().toString();
		//
		// updateUserInfo(realName, email, phone);
		// } else {
		// activity.menuLogout.performClick();
		// }
		// } else {
		// String realName = etRealName.getText().toString();
		// String email = etEmail.getText().toString();
		// String phone = etPhone.getText().toString();
		//
		// updateUserInfo(realName, email, phone);
		// }
		//
		// }
		// });

		// if (SmartBarUtils.hasSmartBar()) {
		// UIHelper.setMeizuBtn(getResources(), layoutBottom,
		// (Button) btnUpdateUser);
		//
		// btnUpdateUser.setTextColor(colorWhite);
		// if (isEditUserInfo) {
		// btnUpdateUser.setText("保存修改");
		// btnUpdateUser.setBackgroundResource(R.drawable.btn_meizu_click);
		// } else {
		// btnUpdateUser.setText("退出");
		// btnUpdateUser
		// .setBackgroundResource(R.drawable.btn_meizu_red_click);
		// }
		//
		// btnUpdateUser.setOnLongClickListener(new OnLongClickListener() {
		//
		// @Override
		// public boolean onLongClick(View v) {
		//
		// if (SmartBarUtils.hasSmartBar()) {
		// if (isEditUserInfo) {
		//
		// } else {
		// activity.menuLogout.performLongClick();
		// }
		// }
		//
		// return false;
		// }
		// });
		// } else {
		// btnUpdateUser.setClickable(false);
		// }

		layoutChangePsw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				Intent intent = new Intent(activity, SimpleFragActivity.class);
				intent.putExtra(Keys.FRAG_NAME,
						UpdatePswFragment.class.getName());
				activity.startActivity(intent);
			}
		});

		layoutEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				Intent intent = new Intent(activity, UpdateEmailActivity.class);
				activity.startActivity(intent);
			}
		});

		layoutRealname.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				Intent intent = new Intent(activity,
						UpdateRealNameActivity.class);
				activity.startActivity(intent);
			}
		});

		layouPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				Intent intent = new Intent(activity,
						UpdateUsernameActivity.class);
				activity.startActivity(intent);
			}
		});

		btnShowRecharge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				UIHelper.showRecharge(activity);
			}
		});
		btnGetCash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				if (ac.user.balance <= 0) {
					return;
				}

				activity.showDialogGetCash();
			}
		});

		if (ac.user.balance <= 0) {
			btnGetCash.setTextColor(getResources().getColor(
					R.color.gray_order_tv));
		} else {
			btnGetCash.setTextColor(getResources().getColor(
					R.color.red_text_click));
		}

		btnLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				activity.showLogout();

			}
		});

	}

	@Override
	public void onResume() {
		Log.d(TAG, "======onResume======");
		setUserInfo();
		checkBalance();
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.d(TAG, "======onPause======");
		super.onPause();
	}

	private MyHandler myHandler = new MyHandler(this);
	protected boolean isEditUserInfo;

	static class MyHandler extends Handler {
		// 注意下面的“<T>”类是MyHandler类所在的外部类
		private WeakReference<AccountFragment> mWr;

		public MyHandler(AccountFragment activity) {
			mWr = new WeakReference<AccountFragment>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			AccountFragment frag = mWr.get();
			// 解决bug:Fragment被销毁了导致一些空指针异常、或者fragment not attached to
			// activity异常；原因是Fragment被销毁或者deatach后，线程才返回结果；最好的解决办法应该是，在Fragment被销毁时结束线程请求
			if (frag == null || !frag.isAdded()) {
				return;
			}

			switch (msg.what) {
			case WHAT_QUERY_BALANCE:
				Recharge r = (Recharge) msg.obj;

				if (r.rspMsg.OK()) {
					frag.ac.user.balance = StringUtils.toFloat(r.balance);
					frag.tvBalance.setText("余额：" + (int) frag.ac.user.balance);
				}

				break;
			case -1:
				((AppException) msg.obj).makeToast(frag.ac);
				break;
			}
		}
	}

	private void checkBalance() {
		if (ac.isLogin()) {
			new Thread() {
				Message msg = new Message();

				public void run() {
					try {
						Recharge r = null;

						r = ac.queryBalance(ac.getLoginUserId());

						msg.what = WHAT_QUERY_BALANCE;
						msg.obj = r;

					} catch (AppException e) {
						e.printStackTrace();
						msg.what = -1;
						msg.obj = e;

					}

					myHandler.sendMessage(msg);
				};
			}.start();
		}
	}

	// private void addUserInfoChangeListener(TextView etUserInfo,
	// final String userInfoPre) {
	// etUserInfo.addTextChangedListener(new TextWatcher() {
	// @Override
	// public void onTextChanged(CharSequence arg0, int arg1, int arg2,
	// int arg3) {
	// }
	//
	// @Override
	// public void beforeTextChanged(CharSequence arg0, int arg1,
	// int arg2, int arg3) {
	// }
	//
	// @Override
	// public void afterTextChanged(Editable editable) {
	// String strNew = editable.toString();
	// if (strNew.equals(userInfoPre)
	// || (StringUtils.isEmpty(strNew) && StringUtils
	// .isEmpty(userInfoPre))) {
	//
	// if (SmartBarUtils.hasSmartBar()) {
	// isEditUserInfo = false;
	// btnUpdateUser
	// .setBackgroundResource(R.drawable.btn_meizu_red_click);
	// btnUpdateUser.setText("退出");
	// } else {
	// btnUpdateUser.setTextColor(colorGray);
	// btnUpdateUser.setClickable(false);
	// }
	// } else {
	//
	// if (SmartBarUtils.hasSmartBar()) {
	// isEditUserInfo = true;
	// btnUpdateUser
	// .setBackgroundResource(R.drawable.btn_meizu_click);
	// btnUpdateUser.setText("保存修改");
	// } else {
	// btnUpdateUser.setTextColor(colorWhite);
	// btnUpdateUser.setClickable(true);
	// }
	//
	// }
	// }
	// });
	// }

	private void setUserInfo() {
		if (ac.isLogin()) {

			tvBalance.setText("余额：" + (int) ac.user.balance);

			if (ac.user.balance <= 0) {
				btnGetCash.setTextColor(getResources().getColor(
						R.color.gray_order_tv));
			} else {
				btnGetCash.setTextColor(getResources().getColor(
						R.color.red_text_click));
			}

			if (UserInfo.USER_TYPE_NORMAL.equals(ac.user.userType)) {
				tvUsername.setText("账户：" + ac.user.username);
				etEmail.setText(ac.user.email);
				etRealName.setText(ac.user.realname);

				layouPhone.setVisibility(View.VISIBLE);
				etPhone.setText(ac.user.userPhone);

				layoutChangePsw.setVisibility(View.VISIBLE);
				layoutOAuthType.setVisibility(View.GONE);
			} else {
				// OAuth用户
				tvUsername.setText("账户：" + ac.user.nickname);
				etEmail.setText(ac.user.email);
				etRealName.setText(ac.user.realname);

				layouPhone.setVisibility(View.VISIBLE);
				etPhone.setText(ac.user.userPhone);

				layoutChangePsw.setVisibility(View.GONE);
				layoutOAuthType.setVisibility(View.VISIBLE);

				String oauthType = "OAuth帐户";
				if (UserInfo.USER_TYPE_OAUTH_QZONE.equals(ac.user.userType)) {
					oauthType = "QQ帐户   ";

				} else if (UserInfo.USER_TYPE_OAUTH_SINA
						.equals(ac.user.userType)) {
					oauthType = "微博帐户";
				}

				tvOAuthType.setText(oauthType);
				tvOAuthAlias.setText(ac.user.nickname);

				// addUserInfoChangeListener(etPhone, ac.user.userPhone);
			}

			// addUserInfoChangeListener(etRealName, ac.user.realname);
			// addUserInfoChangeListener(etEmail, ac.user.email);
		}
	}

	public void onEventMainThread(Event e) {
		if (e instanceof UserBalanceChangeEvent) {
			UserBalanceChangeEvent ev = (UserBalanceChangeEvent) e;
			if (ev.isChange) {
				setUserInfo();
			}
		}
		if (e instanceof UpdateUserEvent) {
			UpdateUserEvent ev = (UpdateUserEvent) e;
			if (ev.success) {
				setUserInfo();
			}
		}
		if (e instanceof LoginEvent) {
			LoginEvent ev = (LoginEvent) e;
			if (ev.success) {
				setUserInfo();
			}
		}

	}

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "======onAttach======");
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		Log.d(TAG, "======onDetach======");
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "======onDestroy======");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		EventBus.getDefault().unregister(this);
		super.onDestroyView();
	}

	interface AccountListener {
		void logout();
	}

}
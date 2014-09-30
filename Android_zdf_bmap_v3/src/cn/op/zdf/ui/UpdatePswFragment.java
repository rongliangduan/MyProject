package cn.op.zdf.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import cn.op.common.AppException;
import cn.op.common.UIHelper;
import cn.op.common.domain.RspMsg;
import cn.op.common.util.RegUtil;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;

/**
 * 修改密码
 * 
 * @author lufei
 * 
 */
public class UpdatePswFragment extends BaseFragment {

	private FragmentActivity activity;
	private EditText etOldPsw;
	private EditText etNewPsw;
	private Button btnUpdatePsw;
	private AppContext ac;
	private View pb;
	private EditText etNewPswRepeat;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater
				.inflate(R.layout.frag_update_psw, container, false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		return view;
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ac = AppContext.getAc();
		activity = getActivity();

		tvTopBarTitle.setText("修改密码");

		btnRight.setVisibility(View.INVISIBLE);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				activity.finish();
			}
		});

		pb = view.findViewById(R.id.pb);
		pb.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		

		etOldPsw = (EditText) view.findViewById(R.id.etOldPsw);
		etNewPsw = (EditText) view.findViewById(R.id.etNewPsw);
		etNewPswRepeat = (EditText) view.findViewById(R.id.etNewPswRepeat);

		btnUpdatePsw = (Button) view.findViewById(R.id.btnSure);

		btnUpdatePsw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				String oldPsw = etOldPsw.getText().toString();
				String newPsw = etNewPsw.getText().toString();
				String newPswRepeat = etNewPswRepeat.getText().toString();
				updatePsw(ac.user.username, oldPsw, newPsw, newPswRepeat);
			}
		});

	};

	protected void updatePsw(final String username, final String oldPsw,
			final String newPsw, String newPswRepeat) {
		if (StringUtils.isEmpty(oldPsw)) {
			AppContext.toastShow(R.string.pleaseInputOldPsw);
			return;
		}

		if (!oldPsw.equals(ac.user.login_pwd)) {
			AppContext.toastShow(R.string.oldPswIsIncorrect);
			return;
		}

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
						ac.user.login_pwd = newPsw;

						activity.finish();
						UIHelper.hideSoftInput(activity, pb);
						AppContext.toastShow("修改成功");

						etOldPsw.setText("");
						etNewPsw.setText("");
						etNewPswRepeat.setText("");

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
					RspMsg user = ac.updatePsw(username, oldPsw, newPsw);
					msg.what = 1;
					msg.obj = user;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}

}

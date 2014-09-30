package cn.op.zdf.ui;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import cn.op.common.AppException;
import cn.op.common.UIHelper;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.UserInfo;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.event.UpdateUserEvent;
import cn.op.zdf.ui.OrderDetailFragment.MyHandler;
import de.greenrobot.event.EventBus;

public class UpdateEmailActivity extends Activity {

	private View pb;
	private AppContext ac;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_email);

		pb = findViewById(R.id.pb);
		ac = AppContext.getAc();
		View btnLeft = findViewById(R.id.btnLeft);
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("修改邮箱");
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		final EditText etEmail = (EditText) findViewById(R.id.etEmail);
		View btnAddCoupon = findViewById(R.id.btnSure);

		btnAddCoupon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				String email = etEmail.getText().toString();

				if (!StringUtils.isEmail(email)) {
					AppContext.toastShow("请输入邮箱");
					return;
				}

				updateEmail(email);
			}
		});
	}

	private MyHandler myHandler = new MyHandler(this);

	static class MyHandler extends Handler {
		// 注意下面的“<T>”类是MyHandler类所在的外部类
		private WeakReference<UpdateEmailActivity> mWr;

		public MyHandler(UpdateEmailActivity frag) {
			mWr = new WeakReference<UpdateEmailActivity>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			UpdateEmailActivity activity = mWr.get();

			if (activity == null) {
				return;
			}

			activity.pb.setVisibility(View.GONE);

			switch (msg.what) {
			case 1:
				RspMsg rsp = (RspMsg) msg.obj;

				if (rsp.OK()) {

					UpdateUserEvent event = new UpdateUserEvent();
					event.success = true;
					EventBus.getDefault().post(event);

					activity.finish();

				} else {
					AppContext.toastShow(rsp.message);
				}

				break;

			case -1:
				((AppException) msg.obj).makeToast(activity);
				break;
			}
		}
	}

	protected void updateEmail(final String email) {
		pb.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {

					RspMsg rsp = ac.updateEmail(ac.getLoginUserId(), email);

					if (rsp.OK()) {
						ac.user.email = email;
					}

					msg.what = 1;
					msg.obj = rsp;
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
	public void finish() {
		UIHelper.hideSoftInput(getApplicationContext(), pb);
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
	}
}

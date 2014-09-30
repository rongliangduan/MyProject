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
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.event.UpdateUserEvent;
import cn.op.zdf.ui.UpdateEmailActivity.MyHandler;
import de.greenrobot.event.EventBus;

public class UpdateRealNameActivity extends Activity {

	private View pb;
	private AppContext ac;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_real_name);

		ac = AppContext.getAc();
		pb = findViewById(R.id.pb);
		View btnLeft = findViewById(R.id.btnLeft);
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("修改入住人");
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		final EditText etRealName = (EditText) findViewById(R.id.etRealName);
		View btnAddCoupon = findViewById(R.id.btnSure);

		UIHelper.limitChineseEditTextInput(etRealName);

		btnAddCoupon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				String realName = etRealName.getText().toString();

				if (StringUtils.isEmpty(realName)) {
					AppContext.toastShow("请输入姓名");
					return;
				}

				updateRealName(realName);
			}
		});
	}

	private MyHandler myHandler = new MyHandler(this);

	static class MyHandler extends Handler {
		// 注意下面的“<T>”类是MyHandler类所在的外部类
		private WeakReference<UpdateRealNameActivity> mWr;

		public MyHandler(UpdateRealNameActivity frag) {
			mWr = new WeakReference<UpdateRealNameActivity>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			UpdateRealNameActivity activity = mWr.get();

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

	protected void updateRealName(final String realName) {
		pb.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {

					RspMsg rsp = ac.updateRealName(ac.getLoginUserId(), realName);

					if (rsp.OK()) {
						ac.user.realname = realName;
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

package cn.op.zdf.ui;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
import cn.op.zdf.event.AddCouponEvent;
import cn.op.zdf.event.UpdateUserEvent;
import cn.op.zdf.ui.UpdateEmailActivity.MyHandler;
import de.greenrobot.event.EventBus;

/**
 * 添加优惠券
 * 
 * @author lufei
 * 
 */
public class AddCouponActivity extends Activity {

	protected static final int WHAT_ADD_COUPON = 1;
	private AppContext ac;
	private View pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_coupon);

		ac = AppContext.getAc();
		pb = findViewById(R.id.pb);
		View btnLeft = findViewById(R.id.btnLeft);
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("添加优惠券");
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		final EditText etCouponKey = (EditText) findViewById(R.id.etCouponKey);
		final View btnAddCoupon = findViewById(R.id.btnAddCoupon);

		btnAddCoupon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				String couponKey = etCouponKey.getText().toString();

				if (StringUtils.isEmpty(couponKey)) {
					AppContext.toastShow("请输入优惠券代码");
					return;
				}

				addCoupon(couponKey);
			}
		});
		btnAddCoupon.setEnabled(false);

		etCouponKey.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (StringUtils.isEmpty(editable.toString())) {
					btnAddCoupon.setBackgroundResource(R.drawable.btn_gray);
					btnAddCoupon.setEnabled(false);
				} else {
					btnAddCoupon
							.setBackgroundResource(R.drawable.btn_orange_click);
					btnAddCoupon.setEnabled(true);
				}
			}
		});

	}

	private MyHandler myHandler = new MyHandler(this);

	static class MyHandler extends Handler {
		// 注意下面的“<T>”类是MyHandler类所在的外部类
		private WeakReference<AddCouponActivity> mWr;

		public MyHandler(AddCouponActivity frag) {
			mWr = new WeakReference<AddCouponActivity>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			AddCouponActivity activity = mWr.get();

			if (activity == null) {
				return;
			}

			activity.pb.setVisibility(View.GONE);

			switch (msg.what) {
			case WHAT_ADD_COUPON:
				RspMsg rsp = (RspMsg) msg.obj;

				if (rsp.OK()) {

					AddCouponEvent event = new AddCouponEvent();
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

	protected void addCoupon(final String couponKey) {
		pb.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {

					RspMsg rsp = ac.addCoupon(ac.getLoginUserId(), couponKey);

					msg.what = WHAT_ADD_COUPON;
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
		UIHelper.hideSoftInput(ac, pb);
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
	}

}

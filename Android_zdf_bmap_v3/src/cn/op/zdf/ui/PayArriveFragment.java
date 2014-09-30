package cn.op.zdf.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.zdf.R;
import cn.op.zdf.domain.Item;

/**
 * 到店支付，not use
 * 
 * @author lufei
 * 
 */
public class PayArriveFragment extends BaseFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.frag_pay_arrive, container, false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		tvTopBarTitle.setText("到店支付须知");

		btnRight.setVisibility(View.INVISIBLE);
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				EventBus.getDefault().post(new PayArriveCommitedEvent());
//				((MainActivity) activity).popActivityFragBackStack();
				
				activity.onBackPressed();
			}
		});

		Bundle arg = getArguments();
		final Item item = (Item) arg.getSerializable(Keys.ORDER);

		TextView tvOrderNum = (TextView) view.findViewById(R.id.textView2);
		TextView tvOrderTip = (TextView) view.findViewById(R.id.textView3);
		TextView tvHotelTel = (TextView) view.findViewById(R.id.textView12);
		TextView tvHotelAddr = (TextView) view.findViewById(R.id.textView10);

		tvOrderNum.setText(item.booksId);
		tvHotelTel.setText(item.hotelsTel);
		tvHotelAddr.setText(item.hotelsAddr);

		switch (item.booksStatus) {
		case Item.ORDER_STATE_WAIT_RESPONSE:
			tvOrderTip.setText("　　酒店正在处理订单，稍后将以短信告知处理结果");
			break;
		case Item.ORDER_STATE_WAIT_ARRIVE_PAY_LIVE:
			tvOrderTip.setText("　　到店支付请务必在　" + item.getRoomUseDate()
					+ "　前抵达入住酒店，并向前台出示身份证，说明是来自“有间房”的用户，同时出示本订单编号。\n\n　　本订单会在　"
					+ item.getRoomUseDate()
					+ "　后自动作废，如您无法按时前往，请及时与该酒店进行确认，以避免发生不必要的麻烦。");
			break;
		}

		tvHotelTel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UIHelper.call(activity, ((TextView) v).getText().toString());
			}
		});

		tvHotelAddr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO

				// showHotelMap();

				// Fragment fragmentHotelMap = Fragment.instantiate(activity,
				// AMapTestFragment.class.getName());
				// ((MainActivity) activity).fragmentUtil.addToBackStack(
				// R.id.slidingmenumain, fragmentHotelMap,
				// Tags.HOTEL_DETAIL, R.anim.slide_in_right,
				// R.anim.slide_out_right, R.anim.slide_in_right,
				// R.anim.slide_out_right);

				Intent intent = new Intent(activity, BMapRouteActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				intent.putExtra(Keys.ITEM, item);
				startActivity(intent);
			}
		});
	}

}

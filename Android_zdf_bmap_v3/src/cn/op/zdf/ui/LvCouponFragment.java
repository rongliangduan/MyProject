package cn.op.zdf.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.op.common.AppException;
import cn.op.common.BaseAdapter;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.URLs;
import cn.op.common.util.Log;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.ItemPage;
import cn.op.zdf.event.AddCouponEvent;
import cn.op.zdf.event.CouponSelectEvent;
import cn.op.zdf.event.Event;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meizu.smartbar.SmartBarUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import de.greenrobot.event.EventBus;

/**
 * 优惠券列表
 * 
 * @author lufei
 * 
 */
public class LvCouponFragment extends ListFragment {

	private static final String TAG = Log.makeLogTag(LvCouponFragment.class);
	private LayoutInflater inflater;
	private Activity activity;
	private AppContext ac;
	private LvAdapter adapter;
	protected int stateCurtShow;

	protected List<Item> listAll = new ArrayList<Item>();
	private View pb;
	private View view;
	private ListView lv;

	protected boolean multiCheck;
	protected static final int WHAT_INIT_DATA = 2;
	protected static final int WHAT_DELETE_ITEM = 3;
	protected static final int WHAT_CHECK_COUPON_4_ORDER = 4;

	protected static final int TODO_STATE = 1;
	protected static final int DONE_STATE = 2;

	/**
	 * 上次选中的优惠券
	 */
	protected Item lastSelectedItem;
	private TextView tvRight;
	private Item usedCoupon;
	private String orderId;
	private int mPageNum;
	private PullToRefreshListView mPullRefreshListView;
	private View layoutNoDataTip;
	private ImageView ivNoData;
	private View btnReload;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "======onCreateView======");
		EventBus.getDefault().register(this);

		this.inflater = inflater;
		activity = getActivity();
		ac = AppContext.getAc();

		View view = inflater.inflate(R.layout.frag_lv_coupon, container, false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

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
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "======onViewCreated======");

		this.view = view;

		initView();
	}

	protected List<Item> filterOrderByState(int state, List<Item> data) {
		List<Item> list = new ArrayList<Item>();

		int size = data.size();
		for (int i = 0; i < size; i++) {
			Item order = data.get(i);

			if (state == TODO_STATE) {
				if (order.booksStatus == Item.ORDER_STATE_WAIT_RESPONSE
						|| order.booksStatus == Item.ORDER_STATE_FAIL) {
					list.add(order);
				}
			} else if (state == DONE_STATE) {
				if (order.booksStatus == Item.ORDER_STATE_PAY_ONLINE_SUCCESS_WAIT_LIVE) {
					list.add(order);
				}
			}

		}

		return list;

	}

	private void initView() {
		pb = view.findViewById(R.id.pb);
		pb.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});

		layoutNoDataTip = view.findViewById(R.id.layoutNoDataTip);
		ivNoData = (ImageView) layoutNoDataTip.findViewById(R.id.ivNoData);
		btnReload = layoutNoDataTip.findViewById(R.id.btnReload);

		btnReload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				layoutNoDataTip.setVisibility(View.GONE);
				refreshData();
			}
		});

		View topBar = view.findViewById(R.id.topBar);
		if (isUseCouponFrag()) {
			topBar.setVisibility(View.VISIBLE);
			TextView tvTitle = (TextView) topBar.findViewById(R.id.tvTitle);
			tvRight = (TextView) topBar.findViewById(R.id.tvRight);
			View btnLeft = topBar.findViewById(R.id.btnLeft);

			tvTitle.setText("优惠券");
			tvRight.setText("确定");
			btnLeft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (UIHelper.isFastDoubleClick(v)) {
						return;
					}

					activity.finish();
				}
			});

			tvRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (UIHelper.isFastDoubleClick(v)) {
						return;
					}

					if (lastSelectedItem == null) {
						CouponSelectEvent event = new CouponSelectEvent();
						event.coupon = null;
						EventBus.getDefault().post(event);

						activity.finish();

						return;
					}

					checkCoupon4Order(lastSelectedItem);

				}
			});
		}

		View btnShowAddCoupon = view.findViewById(R.id.btnShowAddCoupon);
		btnShowAddCoupon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAddCoupon();
			}
		});

		if (isUseCouponFrag()) {
			btnShowAddCoupon.setVisibility(View.GONE);

			Bundle arguments = getArguments();
			if (arguments != null) {
				usedCoupon = (Item) arguments.getSerializable(Keys.ITEM);
				orderId = arguments.getString(Keys.ORDER_ID);
			}

		} else {
			btnShowAddCoupon.setVisibility(View.VISIBLE);
		}

		// 上拉下拉刷新
		mPullRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						initData(mPageNum);
					}
				});

		List<Item> list = new ArrayList<Item>();
		adapter = new LvAdapter(list);

		lv = (ListView) getListView();
		lv.setVerticalFadingEdgeEnabled(false);
		lv.setItemsCanFocus(false);
		lv.setAdapter(adapter);

		refreshData();
	}

	private void refreshData() {
		mPageNum = 1;
		if (adapter != null) {
			adapter.data.clear();
		}

		initData(mPageNum);
	}

	/**
	 * 检查优惠券
	 * 
	 * @param coupon
	 * @return
	 */
	protected void checkCoupon4Order(final Item coupon) {
		pb.setVisibility(View.VISIBLE);
		new Thread() {
			Message msg = new Message();

			public void run() {
				try {
					RspMsg r = ac.checkCoupon4Order(coupon.couponId, orderId);

					msg.what = WHAT_CHECK_COUPON_4_ORDER;
					msg.obj = r;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_CHECK_COUPON_4_ORDER;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			};
		}.start();

	}

	protected void showAddCoupon() {
		Intent intent = new Intent(activity, AddCouponActivity.class);
		startActivity(intent);
	}

	private MyHandler myHandler = new MyHandler(this);

	static class MyHandler extends Handler {
		private WeakReference<LvCouponFragment> mWr;

		public MyHandler(LvCouponFragment frag) {
			super();
			this.mWr = new WeakReference<LvCouponFragment>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			LvCouponFragment frag = mWr.get();

			if (frag == null || !frag.isAdded()) {
				return;
			}

			switch (msg.what) {
			case WHAT_INIT_DATA:
				frag.pb.setVisibility(View.GONE);
				frag.mPullRefreshListView.onRefreshComplete();

				ItemPage q = (ItemPage) msg.obj;

				if (q.rspMsg.OK()) {
					if (q.list.size() > 0) {
						frag.mPageNum++;
					}

					// 恢复上次选择使用的优惠券,将其置顶并选中
					if (frag.usedCoupon != null) {
						// 列表中有已使用的优惠券,则将此优惠券置顶
						for (int i = 0; i < q.list.size(); i++) {
							Item item = q.list.get(i);
							if (item.couponId.equals(frag.usedCoupon.couponId)) {

								Item move = q.list.remove(i);
								move.isSelected = true;
								q.list.add(0, move);

								frag.usedCoupon = null;
								break;
							}
						}

						// 列表中无已使用优惠券,则将此优惠券添加到列表顶部
						if (frag.usedCoupon != null) {
							frag.usedCoupon.isSelected = true;
							q.list.add(0, frag.usedCoupon);
							frag.usedCoupon = null;
						}

						frag.tvRight.setVisibility(View.VISIBLE);
					}

					int lastSelect = frag.lv.getSelectedItemPosition();
					if (frag.adapter.data.size() == 0) {
						// 初次加载、重新加载
						if (q.list.size() == 0) {
							frag.ivNoData
									.setImageResource(R.drawable.img_no_data_tip_coupon);
							frag.layoutNoDataTip.setVisibility(View.VISIBLE);
							frag.mPullRefreshListView.setVisibility(View.GONE);
						} else {
							frag.layoutNoDataTip.setVisibility(View.GONE);
							frag.mPullRefreshListView
									.setVisibility(View.VISIBLE);

							frag.adapter.data = q.list;
							frag.adapter.notifyDataSetChanged();
						}
					} else {
						frag.layoutNoDataTip.setVisibility(View.GONE);
						frag.mPullRefreshListView.setVisibility(View.VISIBLE);

						// 分页加载更多
						if (q.list.size() == 0) {
							AppContext.toastShow("没有更多数据了");
						} else {
							frag.adapter.data.addAll(q.list);
							frag.adapter.notifyDataSetChanged();
							frag.lv.setSelection(lastSelect);
						}
					}

				} else {
					AppContext.toastShow(q.rspMsg.message);
				}
				break;

			case -WHAT_INIT_DATA:
				frag.pb.setVisibility(View.GONE);
				frag.mPullRefreshListView.onRefreshComplete();

				// 数据加载失败
				if (frag.adapter.data.size() == 0) {
					// 点击重试
					frag.ivNoData
							.setImageResource(R.drawable.img_no_data_tip_fail);
					frag.btnReload.setVisibility(View.VISIBLE);
					frag.layoutNoDataTip.setVisibility(View.VISIBLE);
					frag.mPullRefreshListView.setVisibility(View.GONE);
				} else {
					AppContext.toastShow(R.string.pleaseRetry);
				}

				((AppException) msg.obj).makeToast(frag.ac);

				break;
			case WHAT_CHECK_COUPON_4_ORDER:
				frag.pb.setVisibility(View.GONE);

				RspMsg rsp = (RspMsg) msg.obj;

				if (rsp.OK()) {
					CouponSelectEvent event = new CouponSelectEvent();
					event.coupon = frag.lastSelectedItem;
					EventBus.getDefault().post(event);

					frag.activity.finish();
				} else {
					// TODO 对话框提示
					AppContext.toastShow(rsp.message);
				}

				break;
			case -WHAT_CHECK_COUPON_4_ORDER:
				frag.pb.setVisibility(View.GONE);
				// TODO 是否请重试
				AppContext.toastShow(R.string.pleaseRetry);
				((AppException) msg.obj).makeToast(frag.ac);
				break;
			}
		}
	}

	private void initData(final int pageNum) {
		if (adapter.data.size() == 0) {
			pb.setVisibility(View.VISIBLE);
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					ItemPage q = null;

					// int couponUseState = 0;
					//
					// if (isUseCouponFrag()) {
					// couponUseState = Item.COUPON_USE_STATE_NOT_USE;
					// }

					int couponUseState = Item.COUPON_USE_STATE_NOT_USE;

					q = ac.getCouponPage(ac.getLoginUserId(), pageNum,
							couponUseState);

					msg.what = WHAT_INIT_DATA;
					msg.obj = q;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_INIT_DATA;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			};
		}.start();
	}

	/**
	 * @return true-选择使用优惠券, false-普通优惠券列表
	 */
	public boolean isUseCouponFrag() {
		return !(activity instanceof MainActivity);
	}

	public void onEventMainThread(Event e) {
		Log.d(TAG, "======onEventMainThread======"
				+ e.getClass().getSimpleName());

		if (e instanceof AddCouponEvent) {
			AddCouponEvent ev = (AddCouponEvent) e;
			if (ev.success) {
				refreshData();
			}
		}

	}

	private class LvAdapter extends BaseAdapter<Item> {

		protected ViewHolder lastSelectedHolder;
		private DisplayImageOptions optionsLogo;

		public LvAdapter(List<Item> data) {
			super(data);

			optionsLogo = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.img_coupon_logo_default)
					.showImageForEmptyUri(R.drawable.img_coupon_logo_default)
					.resetViewBeforeLoading(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.showImageOnFail(R.drawable.img_coupon_logo_default)
					.cacheInMemory(true).cacheOnDisk(true)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}

		private class ViewHolder {
			public TextView tvTitle;
			public TextView tvBrief;
			public View layoutFront;
			public TextView tvKey;
			public TextView tvUseState;
			public TextView tvEndDate;
			public View layoutCenter;
			public ImageView ivLeft;
			public ImageView ivRight;
			public View layoutBottomCoupon;
			public ImageView ivCouponLogo;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = inflater.inflate(R.layout.coupon_lv_item, null);

				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tvTitle);
				holder.tvKey = (TextView) convertView.findViewById(R.id.tvKey);
				holder.tvBrief = (TextView) convertView
						.findViewById(R.id.tvBrief);
				holder.tvUseState = (TextView) convertView
						.findViewById(R.id.tvUseState);
				holder.tvEndDate = (TextView) convertView
						.findViewById(R.id.tvEndDate);

				holder.layoutCenter = convertView.findViewById(R.id.layout1);
				holder.layoutBottomCoupon = convertView
						.findViewById(R.id.layoutBottomCoupon);
				holder.ivLeft = (ImageView) convertView
						.findViewById(R.id.ivLeft);
				holder.ivRight = (ImageView) convertView
						.findViewById(R.id.ivRight);
				holder.ivCouponLogo = (ImageView) convertView
						.findViewById(R.id.ivCouponLogo);
				holder.layoutFront = convertView.findViewById(R.id.front);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Item i = getItem(position);

			holder.tvTitle.setText(i.couponName);
			holder.tvKey.setText(i.couponKey);
			holder.tvBrief.setText(i.couponBrief);
			holder.tvEndDate.setText("有效期至：" + i.getCouponEndData());

			String useState = "";
			switch (i.couponUseState) {
			case Item.COUPON_USE_STATE_NOT_USE:
				useState = "可以使用";
				break;
			case Item.COUPON_USE_STATE_USED:
				useState = "已使用";
				break;
			case Item.COUPON_USE_STATE_EXPIRE:
				useState = "已过期";
				break;
			}
			holder.tvUseState.setText(useState);

			if (i.isSelected) {
				setBgTrue(holder);
			} else {
				setBgFalse(holder);
			}

			if (isUseCouponFrag()) {
				holder.layoutFront.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (UIHelper.isFastDoubleClick(v)) {
							return;
						}

						if (i.isSelected) {
							// 取消选中
							i.isSelected = false;
							setBgFalse(lastSelectedHolder);

							if (lastSelectedItem != null
									&& !lastSelectedItem.isSelected) {
								// tvRight.setVisibility(View.INVISIBLE);
								lastSelectedItem = null;
							}
						} else {
							// 选中
							if (lastSelectedHolder != null) {
								setBgFalse(lastSelectedHolder);
							}

							if (lastSelectedItem != null) {
								lastSelectedItem.isSelected = false;
							}

							i.isSelected = true;
							lastSelectedItem = i;
							setBgTrue(holder);

							tvRight.setVisibility(View.VISIBLE);
						}
					}
				});
			}

			ac.mImageLoader.displayImage(URLs.URL_ZDF_API + i.couponLogo,
					holder.ivCouponLogo, optionsLogo);

			return convertView;
		}

		protected void setBgFalse(ViewHolder holder) {
			holder.layoutCenter
					.setBackgroundResource(R.drawable.img_coupon_item_center);
			holder.layoutBottomCoupon
					.setBackgroundResource(R.drawable.rounded_gray_bg_coupon_item);
			holder.ivLeft.setImageResource(R.drawable.img_coupon_item_left);
			holder.ivRight.setImageResource(R.drawable.img_coupon_item_right);
		}

		protected void setBgTrue(ViewHolder holder) {
			holder.layoutCenter
					.setBackgroundResource(R.drawable.img_coupon_item_center_true);
			holder.layoutBottomCoupon
					.setBackgroundResource(R.drawable.rounded_gray_bg_coupon_item_true);
			holder.ivLeft
					.setImageResource(R.drawable.img_coupon_item_left_true);
			holder.ivRight
					.setImageResource(R.drawable.img_coupon_item_right_true);

			lastSelectedHolder = holder;
		}

	}

	@Override
	public void onResume() {
		Log.d(TAG, "======onResume======");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.d(TAG, "======onPause======");
		super.onPause();
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
	public void onDestroyView() {
		Log.d(TAG, "======onDestroyView======");
		EventBus.getDefault().unregister(this);
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "======onDestroy======");
		super.onDestroy();
	}

}
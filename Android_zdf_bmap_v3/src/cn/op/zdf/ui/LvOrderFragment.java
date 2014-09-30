package cn.op.zdf.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.op.common.AppException;
import cn.op.common.BaseAdapter;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.constant.Tags;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.URLs;
import cn.op.common.util.Log;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.ItemPage;
import cn.op.zdf.domain.Room;
import cn.op.zdf.event.Event;
import cn.op.zdf.event.LoginEvent;
import cn.op.zdf.event.OrderChangeEvent;
import cn.op.zdf.event.SlidingMenuOpenEvent;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meizu.smartbar.SmartBarUtils;

import de.greenrobot.event.EventBus;

/**
 * 订单列表
 * 
 * @author lufei
 * 
 */
public class LvOrderFragment extends ListFragment {

	private static final String TAG = Log.makeLogTag(LvOrderFragment.class);
	private PullToRefreshListView mPullRefreshListView;
	private LayoutInflater inflater;
	private MainActivity activity;
	private String tag;
	private AppContext ac;
	private LvAdapter adapter;
	protected int stateCurtShow;

	protected List<Item> listAll = new ArrayList<Item>();
	private View pb;
	private View view;

	/**
	 * 是否多选
	 */
	protected boolean isMultiCheck;
	protected int state = TODO_STATE;
	protected static final int WHAT_INIT_DATA = 2;
	protected static final int WHAT_EXCEPTION = -1;
	protected static final int WHAT_DELETE_ITEM = 3;
	protected static final int WHAT_DELETE_ITEM_BATCH = 4;

	protected static final int TODO_STATE = 1;
	protected static final int DONE_STATE = 2;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		EventBus.getDefault().register(this);

		this.inflater = inflater;
		activity = (MainActivity) getActivity();
		ac = AppContext.getAc();

		tag = this.getTag();
		// temp
		if (StringUtils.isEmpty(tag)) {
			tag = Tags.HOTEL_LIST_MAP;
		}

		View view = inflater.inflate(R.layout.frag_lv_order, container, false);
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

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		this.view = view;

		// lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		// menu = (SatelliteMenu) view.findViewById(R.id.menu);
		// View layoutTopbar = view.findViewById(R.id.topBar);
		// View layoutBottomBar = view.findViewById(R.id.rl_bottom);
		// View layoutTab = view.findViewById(R.id.layoutTab);
		// TextView tvTitle = (TextView)
		// layoutTopbar.findViewById(R.id.tvTitle);
		// ImageView btnLeft = (ImageView)
		// layoutTopbar.findViewById(R.id.btnLeft);
		// ImageView btnRight = (ImageView) layoutTopbar
		// .findViewById(R.id.btnRight);

		// sw = (SwitchButton) layoutBottomBar.findViewById(R.id.myswitch);
		//
		// sw.setOnCheckedChangeListener(new
		// android.widget.CompoundButton.OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// closeAllItem();
		//
		// sw.changeBtnImg(isChecked);
		//
		// if (isChecked) {
		// state = TODO_STATE;
		// } else {
		// state = DONE_STATE;
		// }
		//
		// initData();
		//
		// // List<Item> list = filterOrderByState(state, listAll);
		// //
		// // adapter.data = list;
		// // adapter.notifyDataSetChanged();
		// // slv.setSelection(0);
		//
		// }
		// });
		// sw.performClick();

		// if (Tags.TAG_ORDER.equals(tag)) {
		// layoutBottomBar.setVisibility(View.VISIBLE);
		// }

		initView();

	}

	protected List<Item> filterOrderByState(int state, List<Item> data) {
		List<Item> list = new ArrayList<Item>();

		int size = data.size();
		for (int i = 0; i < size; i++) {
			Item order = data.get(i);

			if (state == TODO_STATE) {

				if (isTodo(order)) {
					list.add(order);
				}

			} else if (state == DONE_STATE) {

				if (isDone(order)) {
					list.add(order);
				}
			}

		}

		return list;

	}

	private boolean isDone(Item order) {
		return (order.booksStatus == Item.ORDER_STATE_CANCEL
				|| order.booksStatus == Item.ORDER_STATE_FAIL
				|| order.booksStatus == Item.ORDER_STATE_DONE || order.booksStatus == Item.ORDER_STATE_PAY_ONLINE_TIME_OVER);
	}

	private boolean isTodo(Item order) {
		return (order.booksStatus == Item.ORDER_STATE_WAIT_RESPONSE
				|| order.booksStatus == Item.ORDER_STATE_WAIT_PAY_ONLINE
				|| order.booksStatus == Item.ORDER_STATE_PAY_ONLINE_SUCCESS_WAIT_LIVE
				|| order.booksStatus == Item.ORDER_STATE_WAIT_ARRIVE_PAY_LIVE || order.booksStatus == Item.ORDER_STATE_PAY_ONLINE_SUCCESS_WAIT_RESPONSE);
	}

	private ListView slv;
	private View layoutBottomDelete;
	protected int mPageNum = 1;
	private View layoutNoDataTip;
	private ImageView ivNoData;
	private View btnReload;

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

		layoutBottomDelete = view.findViewById(R.id.layoutBottomDelete);
		View btnDelete = layoutBottomDelete.findViewById(R.id.btnDelete);
		View btnSelectAll = layoutBottomDelete.findViewById(R.id.btnSelectAll);

		btnSelectAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				int size = adapter.data.size();
				for (int i = 0; i < size; i++) {
					Item item = adapter.data.get(i);
					if (item.booksStatus == Item.ORDER_STATE_DONE
							|| item.booksStatus == Item.ORDER_STATE_CANCEL
							|| item.booksStatus == Item.ORDER_STATE_PAY_ONLINE_TIME_OVER) {

						item.isSelected = true;
					}
				}

				adapter.notifyDataSetChanged();
			}
		});

		btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				StringBuilder sb = new StringBuilder();
				int size = adapter.data.size();
				for (int i = 0; i < size; i++) {
					Item item = adapter.data.get(i);
					if (item.isSelected) {
						sb.append(item.booksId).append(",");
					}
				}

				if (sb.length() == 0) {
					return;
				}

				String itemIds = sb.deleteCharAt(sb.length() - 1).toString();

				delete(itemIds);
			}
		});

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

		ListView lv = (ListView) getListView();
		lv.setVerticalFadingEdgeEnabled(false);
		lv.setItemsCanFocus(false);

		slv = (ListView) lv;

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (!isMultiCheck) {
					activity.tvRight.setText("取消");
					layoutBottomDelete.setVisibility(View.VISIBLE);
					isMultiCheck = true;
					adapter.notifyDataSetChanged();
				}

				return true;
			}
		});

		// slv.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
		// slv.setOffsetLeft(activity.screenWidth - DisplayUtil.dip2px(ac,
		// 100));
		// slv.setSwipeCloseAllItemsWhenMoveList(true);

		// slv.setSwipeListViewListener(new BaseSwipeListViewListener() {
		// @Override
		// public void onOpened(int position, boolean toRight) {
		// Log.d(TAG, "======onOpened====== (position - 1)="
		// + (position - 1));
		//
		// }
		//
		// @Override
		// public void onClosed(int position, boolean fromRight) {
		// Log.d(TAG, "======onClosed====== (position - 1)="
		// + (position - 1));
		//
		// }
		//
		// @Override
		// public void onDismiss(int[] reverseSortedPositions) {
		// }
		//
		// @Override
		// public void onClickFrontView(int position) {
		// Log.d(TAG, "======onClickFrontView====== position=" + position);
		// clickItem(position - 1);
		// }
		//
		// @Override
		// public void onClickBackView(int position) {
		// Log.d(TAG, "======onClickBackView====== position=" + position);
		// }
		// });

		setListAdapter(adapter);

		refreshData();
	}

	private MyHandler myHandler = new MyHandler(this);

	static class MyHandler extends Handler {
		private WeakReference<LvOrderFragment> mWr;

		public MyHandler(LvOrderFragment frag) {
			super();
			this.mWr = new WeakReference<LvOrderFragment>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			LvOrderFragment frag = mWr.get();

			switch (msg.what) {
			case WHAT_INIT_DATA:
				frag.pb.setVisibility(View.GONE);
				frag.mPullRefreshListView.onRefreshComplete();

				ItemPage q = (ItemPage) msg.obj;

				if (q.rspMsg.OK()) {
					if (q.list.size() > 0) {
						frag.mPageNum++;
					}

					int lastSelect = frag.slv.getSelectedItemPosition();
					if (frag.adapter.data.size() == 0) {
						// 初次加载、重新加载
						if (q.list.size() == 0) {
							frag.ivNoData
									.setImageResource(R.drawable.img_no_data_tip_order);
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
						// 分页加载更多
						if (q.list.size() == 0) {
							AppContext.toastShow("没有更多数据了");
						} else {
							frag.adapter.data.addAll(q.list);
							frag.adapter.notifyDataSetChanged();
							frag.slv.setSelection(lastSelect);
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
			case WHAT_DELETE_ITEM:
				frag.pb.setVisibility(View.GONE);
				RspMsg deleteRspMsg = (RspMsg) msg.obj;
				if (deleteRspMsg.OK()) {
					frag.exitMultiCheck();

					// TODO 保持原列表位置
					frag.refreshData();
				} else {
					AppContext.toastShow(deleteRspMsg.message);
				}
				break;
			case WHAT_DELETE_ITEM_BATCH:
				frag.pb.setVisibility(View.GONE);
				RspMsg deleteBatchRspMsg = (RspMsg) msg.obj;
				if (deleteBatchRspMsg.OK()) {

				} else {
					AppContext.toastShow(deleteBatchRspMsg.message);
				}
				break;

			case WHAT_EXCEPTION:
				frag.pb.setVisibility(View.GONE);
				((AppException) msg.obj).makeToast(frag.ac);
				break;

			default:
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

					ItemPage q = ac.getOrderPage(ac.getLoginUserId(), pageNum);

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

	public void onEventMainThread(Event e) {
		Log.d(TAG, "======onEventMainThread======"
				+ e.getClass().getSimpleName());

		if (e instanceof OrderChangeEvent) {
			if (Tags.TAG_ORDER.equals(tag)) {
				OrderChangeEvent ev = (OrderChangeEvent) e;
				if (ev.change) {
					// initData();
					// TODO 保持原列表位置
					refreshData();
				}
			}
		}
		if (e instanceof LoginEvent) {
			if (Tags.TAG_ORDER.equals(tag) || Tags.TAG_COLLECT.equals(tag)) {
				LoginEvent ev = (LoginEvent) e;
				if (ev.success) {

					refreshData();

				}
			}
		}

		if (e instanceof SlidingMenuOpenEvent) {
			SlidingMenuOpenEvent ev = (SlidingMenuOpenEvent) e;
			if (ev.isOpen) {

				exitMultiCheck();
			}
		}
	}

	private void refreshData() {

		mPageNum = 1;
		if (adapter != null) {
			adapter.data.clear();
		}

		initData(mPageNum);
	}

	private class LvAdapter extends BaseAdapter<Item> {
		int colorRed;
		int colorGreen;
		int colorGray;

		public LvAdapter(List<Item> data) {
			super(data);

			colorRed = getResources().getColor(R.color.red_order_tv);
			colorGreen = getResources().getColor(R.color.green_order_tv);
			colorGray = getResources().getColor(R.color.gray_order_tv2);
		}

		private class ViewHolder {
			public TextView tvTitle;
			public TextView tvRoomType;
			public ImageView ivLogo;
			public TextView tvDate;
			public View layoutFront;
			public ImageView ivCheck;
			public TextView tvOrderState;
			public View btnToPay;
			public TextView tvOrderNum;

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = inflater.inflate(R.layout.order_lv_item, null);

				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tvHotelName);
				holder.tvOrderNum = (TextView) convertView
						.findViewById(R.id.tvOrderNum);
				holder.tvOrderState = (TextView) convertView
						.findViewById(R.id.tvOrderState);
				holder.btnToPay = convertView.findViewById(R.id.btnToPay);
				holder.tvRoomType = (TextView) convertView
						.findViewById(R.id.textView2);
				holder.tvDate = (TextView) convertView
						.findViewById(R.id.textView3);
				// holder.layoutPrice =
				// convertView.findViewById(R.id.layoutPrice);
				// holder.tvPriceOriginal = (TextView) convertView
				// .findViewById(R.id.textView4);
				// holder.tvPrice = (TextView) convertView
				// .findViewById(R.id.textView5);
				// holder.tvPricePre = convertView.findViewById(R.id.textView6);
				holder.ivLogo = (ImageView) convertView
						.findViewById(R.id.imageView1);
				holder.ivCheck = (ImageView) convertView
						.findViewById(R.id.ivCheck);
				holder.layoutFront = convertView.findViewById(R.id.front);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Item i = getItem(position);

			// int bg[] = new int[] { R.drawable.lv_item_white_click,
			// R.drawable.lv_item_white_gray_click };
			// int p = position % 2;

			if (isMultiCheck) {

				if (i.booksStatus == Item.ORDER_STATE_DONE
						|| i.booksStatus == Item.ORDER_STATE_CANCEL
						|| i.booksStatus == Item.ORDER_STATE_PAY_ONLINE_TIME_OVER) {

					holder.ivCheck.setVisibility(View.VISIBLE);

					if (i.isSelected) {
						holder.ivCheck
								.setImageResource(R.drawable.ic_cb_green_true);
					} else {
						holder.ivCheck.setImageResource(R.drawable.ic_cb_green);
					}
				} else {
					holder.ivCheck.setVisibility(View.GONE);
				}

			} else {
				holder.ivCheck.setVisibility(View.GONE);
			}

			holder.tvTitle.setText(i.hotelsName);
			if (!StringUtils.isEmpty(i.booksNum)) {
				holder.tvOrderNum.setText("订单号：" + i.booksNum);
			} else {
				holder.tvOrderNum.setText("订单号：" + i.booksId);
			}

			String roomUseDate = i.getRoomUseDate();
			if (StringUtils.isEmpty(roomUseDate)) {
				holder.tvDate.setText("保留至：未确认");
			} else {
				holder.tvDate.setText("保留至：" + roomUseDate);
			}

			if (Item.ORDER_STATE_WAIT_PAY_ONLINE == i.booksStatus) {
				holder.btnToPay.setVisibility(View.VISIBLE);

				holder.btnToPay.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (UIHelper.isFastDoubleClick(v)) {
							return;
						}
						clickItem(position);
					}
				});

				holder.layoutFront.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						holder.btnToPay.performClick();
					}
				});

			} else {
				holder.btnToPay.setVisibility(View.INVISIBLE);
			}

			// holder.tvPrice.setText(i.priceOrder);
			// if (!StringUtils.isEmpty(i.priceOrder) &&
			// !"0".equals(i.priceOrder)) {
			// holder.layoutPrice.setVisibility(View.VISIBLE);
			// holder.tvPrice.setText(i.priceOrder);
			// } else {
			// holder.layoutPrice.setVisibility(View.INVISIBLE);
			// }

			if (i.sellType == Room.SALE_TYPE_ZDF) {
				if (!StringUtils.isEmpty(i.hours)) {
					holder.tvRoomType.setText(i.hours + "小时钟点房");
				} else {
					holder.tvRoomType.setText("钟点房");
				}
			} else {
				if (!StringUtils.isEmpty(i.roomTypeName)) {
					holder.tvRoomType.setText(i.roomTypeName);
				} else {
					holder.tvRoomType.setText("午夜房");
				}
			}

			ac.mImageLoader.displayImage(URLs.URL_ZDF_API + i.logopath,
					holder.ivLogo, ac.optionsLogo);

			String orderStateName = "";
			int stateColor;
			switch (i.booksStatus) {
			case Item.ORDER_STATE_WAIT_RESPONSE:
				// 提交成功，等待确认
			case Item.ORDER_STATE_PAY_ONLINE_SUCCESS_WAIT_RESPONSE:
				// 支付成功，等待确认
				orderStateName = "等待确认";
				stateColor = colorRed;
				break;
			case Item.ORDER_STATE_WAIT_PAY_ONLINE:
				// 预订成功，等待支付
				orderStateName = "等待支付";
				stateColor = colorRed;
				break;
			case Item.ORDER_STATE_PAY_ONLINE_SUCCESS_WAIT_LIVE:
				// 支付成功，等待入住
			case Item.ORDER_STATE_WAIT_ARRIVE_PAY_LIVE:
				// 等待到店支付
				orderStateName = "等待入住";
				stateColor = colorRed;
				break;
			case Item.ORDER_STATE_CANCEL:
				// 订单取消
				orderStateName = "订单取消";
				stateColor = colorGray;
				break;

			case Item.ORDER_STATE_PAY_ONLINE_TIME_OVER:
				// 订单过期
				orderStateName = "订单过期";
				stateColor = colorGray;
				break;

			case Item.ORDER_STATE_CANCEL_WAIT_RESPONSE:
				// 取消中，待审核
				orderStateName = "取消确认中";
				stateColor = colorGray;
				break;
			case Item.ORDER_STATE_NOT_ARRIVE:
				// 未入住
			case Item.ORDER_STATE_ARRIVED:
				// 已入住
			case Item.ORDER_STATE_LEAVED:
				// 已离店
			case Item.ORDER_STATE_DONE:
				// 订单完成
				orderStateName = "订单完成";
				stateColor = colorGreen;
				break;

			default:
				orderStateName = "未知状态";
				stateColor = colorGray;
				break;
			}
			holder.tvOrderState.setTextColor(stateColor);
			holder.tvOrderState.setText(orderStateName);

			return convertView;
		}

	}

	class DeleteClickListener implements OnClickListener {
		int position;

		public DeleteClickListener(int position) {
			super();
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if (UIHelper.isFastDoubleClick(v)) {
				return;
			}

			Item item = adapter.data.get(position);
			delete(item.booksId);
		}
	}

	private void clickItem(int position) {
		Item item = adapter.getItem(position);

		Log.d(TAG, "======clickItem======" + item.hotelsName);

		Intent intent = new Intent(activity, PayOnlineActivity.class);
		intent.putExtra(Keys.ORDER, item);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.slide_in_from_right,
				R.anim.slide_out_to_left);

	}

	/**
	 * 删除订单
	 * 
	 * @param itemIds
	 */
	public void delete(final String itemIds) {

		pb.setVisibility(View.VISIBLE);
		new Thread() {
			Message msg = new Message();

			public void run() {
				try {

					RspMsg r = ac.deleteOrder(itemIds, ac.getLoginUserId());

					msg.what = WHAT_DELETE_ITEM;
					msg.obj = r;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = WHAT_EXCEPTION;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			};
		}.start();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// AppContext.toastShow("onListItemClick " + position);
		Log.d(TAG, "======onListItemClick======position=" + position);

		if (isMultiCheck) {

			Item i = adapter.getItem(position - 1);
			ImageView ivCheck = (ImageView) v.findViewById(R.id.ivCheck);

			if (i.booksStatus == Item.ORDER_STATE_DONE
					|| i.booksStatus == Item.ORDER_STATE_CANCEL
					|| i.booksStatus == Item.ORDER_STATE_PAY_ONLINE_TIME_OVER) {

				if (i.isSelected) {
					i.isSelected = false;
					ivCheck.setImageResource(R.drawable.ic_cb_green);
				} else {
					i.isSelected = true;
					ivCheck.setImageResource(R.drawable.ic_cb_green_true);
				}
			}

		} else {
			clickItem(position - 1);
		}

	}

	public void exitMultiCheck() {
		activity.tvRight.setText("");
		isMultiCheck = false;

		for (int i = 0; i < adapter.data.size(); i++) {
			adapter.data.get(i).isSelected = false;
		}

		adapter.notifyDataSetChanged();
		layoutBottomDelete.setVisibility(View.GONE);
	}

}
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.op.common.AppException;
import cn.op.common.BaseAdapter;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.constant.Tags;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.URLs;
import cn.op.common.util.AnimationUtil;
import cn.op.common.util.DisplayUtil;
import cn.op.common.util.Log;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.ItemPage;
import cn.op.zdf.domain.Room;
import cn.op.zdf.event.CollectionChangeEvent;
import cn.op.zdf.event.DeleteRecentBrowseHotelEvent;
import cn.op.zdf.event.Event;
import cn.op.zdf.event.LoginEvent;
import cn.op.zdf.event.SaveRecentBrowsHotelEvent;
import cn.op.zdf.event.ShowRemoveRecentBrowsEvent;
import cn.op.zdf.event.SlidingMenuOpenEvent;

import com.fortysevendeg.android.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshSwipeListView;
import com.meizu.smartbar.SmartBarUtils;

import de.greenrobot.event.EventBus;

/**
 * 最近浏览酒店列表
 * 
 * @author lufei
 * 
 */
public class LvRecentBrowseFragment extends ListFragment {

	private static final String TAG = Log
			.makeLogTag(LvRecentBrowseFragment.class);
	private PullToRefreshSwipeListView mPullRefreshListView;
	private LayoutInflater inflater;
	private MainActivity activity;
	private String tag;
	private AppContext ac;
	private LvAdapter adapter;
	protected int stateCurtShow;

	protected List<Item> listAll = new ArrayList<Item>();
	// private SwitchButton sw;
	private View pb;
	private View view;
	private TextView tvEmpty;
	private ListView lv;

	protected boolean multiCheck;
	private View layoutDialogDeleteRecentBrowse;
	private ImageView ivMask;
	protected static final int WHAT_INIT_DATA = 1;
	protected static final int WHAT_EXCEPTION = -1;
	protected static final int WHAT_DELETE_ITEM = 2;
	private static final int WHAT_INIT_HOTEL_PAGE_DATA = 3;
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

		View view = inflater.inflate(R.layout.frag_lv_recent_brows, container,
				false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		if (SmartBarUtils.hasSmartBar()) {
			view.setPadding(0,
					getResources().getDimensionPixelSize(R.dimen.marginTopAbs),
					0,
					getResources().getDimensionPixelSize(R.dimen.marginTopAbs));
		}

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

		pb = view.findViewById(R.id.pb);
		lv = (ListView) getListView();
		// lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		// menu = (SatelliteMenu) view.findViewById(R.id.menu);
		// View layoutTopbar = view.findViewById(R.id.topBar);
		// View layoutBottomBar = view.findViewById(R.id.rl_bottom);
		// View layoutTab = view.findViewById(R.id.layoutTab);
		tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);
		// TextView tvTitle = (TextView)
		// layoutTopbar.findViewById(R.id.tvTitle);
		// ImageView btnLeft = (ImageView)
		// layoutTopbar.findViewById(R.id.btnLeft);
		// ImageView btnRight = (ImageView) layoutTopbar
		// .findViewById(R.id.btnRight);

		ivMask = (ImageView) view.findViewById(R.id.sreach_bg);
		layoutDialogDeleteRecentBrowse = view
				.findViewById(R.id.dialogDeleteRecentBrowse);

		View btnOk = layoutDialogDeleteRecentBrowse.findViewById(R.id.button2);
		View btnCancel = layoutDialogDeleteRecentBrowse
				.findViewById(R.id.button1);

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				AnimationUtil.animationShowHideAlpha(activity, false, ivMask);
				AnimationUtil.animationShowSifbHideSotb(activity, false,
						layoutDialogDeleteRecentBrowse);

				adapter.data.clear();
				adapter.notifyDataSetChanged();
				tvEmpty.setVisibility(View.VISIBLE);

				ac.removeRecentBrowsHotel();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				AnimationUtil.animationShowHideAlpha(activity, false, ivMask);
				AnimationUtil.animationShowSifbHideSotb(activity, false,
						layoutDialogDeleteRecentBrowse);

			}
		});

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

	protected void hideMultiDelete() {
		multiCheck = false;
		// TODO
		// slv.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
	}

	private SwipeListView slv;
	private ArrayList<Integer> opend;
	protected int openedPosition;

	private void initView() {
		// 上拉下拉刷新
		mPullRefreshListView = (PullToRefreshSwipeListView) view
				.findViewById(R.id.pull_refresh_list);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<SwipeListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<SwipeListView> refreshView) {
						// initData();
					}
				});

		List<Item> list = new ArrayList<Item>();
		adapter = new LvAdapter(list);

		ListView lv = (ListView) getListView();
		lv.setVerticalFadingEdgeEnabled(false);
		lv.setItemsCanFocus(false);

		// swip
		opend = new ArrayList<Integer>();
		slv = (SwipeListView) lv;

		slv.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
		slv.setOffsetLeft(activity.screenWidth - DisplayUtil.dip2px(ac, 100));
		// slv.setOffsetRight(activity.screenWidth - DisplayUtil.dip2px(ac,
		// 100));
		slv.setSwipeCloseAllItemsWhenMoveList(true);

		slv.setSwipeListViewListener(new BaseSwipeListViewListener() {

			@Override
			public void onOpened(int position, boolean toRight) {
				Log.d(TAG, "======onOpened====== (position - 1)="
						+ (position - 1));

				openedPosition = position;
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {
				openedPosition = -1;
				Log.d(TAG, "======onDismiss====== reverseSortedPositions="
						+ reverseSortedPositions[0]);

				for (int i = 0; i < reverseSortedPositions.length; i++) {
					final Item item = adapter.data
							.remove(reverseSortedPositions[i] - 1);
					ac.removeRecentBrowsHotel(item);
					Log.d(TAG, "======delete======" + item.hotelsName);
				}

				// how to delete item from this swipe list view ?
				// https://github.com/47deg/android-swipelistview/issues/101
				// adapter.notifyDataSetChanged();
				int visiblePosition = slv.getFirstVisiblePosition();
				slv.setAdapter(adapter);
				slv.setSelection(visiblePosition);

			}

			@Override
			public void onClosed(int position, boolean fromRight) {
				openedPosition = -1;
				Log.d(TAG, "======onClosed====== (position - 1)="
						+ (position - 1));

			}

			@Override
			public void onClickFrontView(int position) {
				Log.d(TAG, "======onClickFrontView====== position=" + position
						+ " ,openedPosition=" + openedPosition);

				// 兼容2.3系统下点击删除按钮，响应的却是点击item事件
				if (openedPosition == position) {
					delete(position - 1);
				} else {
					clickItem(position);
				}
			}

			@Override
			public void onClickBackView(int position) {
				Log.d(TAG, "======onClickBackView====== position=" + position);
			}
		});

		setListAdapter(adapter);

		initData();
	}

	private MyHandler myHandler = new MyHandler(this);
	private ArrayList<String> checkedIds = new ArrayList<String>();;

	static class MyHandler extends Handler {
		private WeakReference<LvRecentBrowseFragment> mWr;

		public MyHandler(LvRecentBrowseFragment frag) {
			super();
			this.mWr = new WeakReference<LvRecentBrowseFragment>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			LvRecentBrowseFragment frag = mWr.get();

			switch (msg.what) {
			case WHAT_INIT_DATA:
				frag.pb.setVisibility(View.GONE);
				ItemPage q = (ItemPage) msg.obj;

				if (q.rspMsg.OK()) {
					if (q.list.size() == 0) {
						// AppContext.toastShow(R.string.noData);
						frag.tvEmpty.setVisibility(View.VISIBLE);
						frag.tvEmpty.setText("您还没有浏览过任何酒店哦");

						frag.adapter.data = q.list;
						frag.adapter.notifyDataSetChanged();
					} else {
						frag.tvEmpty.setVisibility(View.GONE);

						frag.listAll = q.list;
						frag.adapter.data = q.list;
						frag.adapter.notifyDataSetChanged();
					}
				} else {
					AppContext.toastShow(q.rspMsg.message);
				}
				break;

			case WHAT_DELETE_ITEM:
				frag.pb.setVisibility(View.GONE);
				RspMsg deleteRspMsg = (RspMsg) msg.obj;
				if (deleteRspMsg.OK()) {
					int position = msg.arg1;
					frag.adapter.data.remove(position);
					frag.adapter.notifyDataSetChanged();

					frag.slv.dismiss(position + 1);
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

	private void initData() {
		if (Tags.HOTEL_LIST_MAP.equals(tag)) {
			return;
		}

		pb.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					ItemPage q = null;

					if (Tags.TAG_COLLECT.equals(tag)) {
						// double latitude = 0;
						// double longitude = 0;
						// if (ac.isLogin()) {
						// if (ac.myLocation != null) {
						// latitude = ac.myLocation.getLatitude();
						// longitude = ac.myLocation.getLongitude();
						// }
						// q = ac.getCollectPage(ac.user.id, latitude,
						// longitude);
						// } else {
						// return;
						// }

						q = ac.getRecentBrowseHotel();
					}

					if (Tags.COMMENT.equals(tag)) {
						Bundle arg = getArguments();
						String hotelId = arg.getString(Keys.ID);
						q = ac.getCommentPage(hotelId);
					}

					msg.what = WHAT_INIT_DATA;
					msg.obj = q;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = WHAT_EXCEPTION;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			};
		}.start();
	}

	public void onEventMainThread(Event e) {
		Log.d(TAG, "======onEventMainThread======"
				+ e.getClass().getSimpleName());

		if (e instanceof ShowRemoveRecentBrowsEvent) {
			ShowRemoveRecentBrowsEvent ev = (ShowRemoveRecentBrowsEvent) e;
			if (ev.isShow) {
				if (layoutDialogDeleteRecentBrowse.getVisibility() == View.VISIBLE) {
					AnimationUtil.animationShowHideAlpha(activity, false,
							ivMask);
					AnimationUtil.animationShowSifbHideSotb(activity, false,
							layoutDialogDeleteRecentBrowse);
					return;
				} else {
					AnimationUtil
							.animationShowHideAlpha(activity, true, ivMask);
					AnimationUtil.animationShowSifbHideSotb(activity, true,
							layoutDialogDeleteRecentBrowse);
				}

			}
		}

		if (e instanceof DeleteRecentBrowseHotelEvent) {
			DeleteRecentBrowseHotelEvent ev = (DeleteRecentBrowseHotelEvent) e;
			if (ev.delete && adapter.data.size() > 0) {

				adapter.data.clear();
				adapter.notifyDataSetChanged();
				tvEmpty.setVisibility(View.VISIBLE);

			}
		}

		if (e instanceof SaveRecentBrowsHotelEvent) {
			SaveRecentBrowsHotelEvent ev = (SaveRecentBrowsHotelEvent) e;

			ItemPage hotelPage = ac.getRecentBrowseHotel();

			if (ev.save) {
				adapter.data = ac.getRecentBrowseHotel().list;
				adapter.notifyDataSetChanged();
			}
		}

		// if (e instanceof FilterBrandEvent) {
		// if (activity.isShowHotelMap) {
		// return;
		// }
		//
		// List<Item> list = filterDataBySaleType(activity.saleType);
		// if (activity.isFilterPrice) {
		// list = filterDataByPrice(activity.curtFilterPriceMin,
		// activity.curtFilterPriceMax, list);
		// }
		//
		// FilterBrandEvent ev = (FilterBrandEvent) e;
		// if (ev.isFilter) {
		// list = filterDataByBrand(ev.brandId, ev.brandName, list);
		// }
		//
		// notifyAdapterData(list);
		// }

		// if (e instanceof FilterPriceEvent) {
		// if (activity.isShowHotelMap) {
		// return;
		// }
		//
		// List<Item> list = filterDataBySaleType(activity.saleType);
		//
		// FilterPriceEvent ev = (FilterPriceEvent) e;
		// if (ev.isFilter) {
		// list = filterDataByPrice(ev.priceMin, ev.priceMax, list);
		// }
		//
		// if (activity.isFilterBrand) {
		// list = filterDataByBrand(activity.curtFilterBrand.brandId,
		// activity.curtFilterBrand.brandName, list);
		// }
		//
		// notifyAdapterData(list);
		// }

		// if (e instanceof SaleTypeChangeEvent) {
		// if (activity.isShowHotelMap) {
		// return;
		// }
		//
		// SaleTypeChangeEvent ev = (SaleTypeChangeEvent) e;
		// if (ev.saleTypeChange) {
		// List<Item> list = filterDataBySaleType(activity.saleType);
		// if (activity.isFilterPrice) {
		// list = filterDataByPrice(activity.curtFilterPriceMin,
		// activity.curtFilterPriceMax, list);
		// }
		// if (activity.isFilterBrand) {
		// list = filterDataByBrand(activity.curtFilterBrand.brandId,
		// activity.curtFilterBrand.brandName, list);
		// }
		// notifyAdapterData(list);
		// }
		// }

		if (e instanceof CollectionChangeEvent) {
			if (Tags.TAG_COLLECT.equals(tag)) {
				CollectionChangeEvent ev = (CollectionChangeEvent) e;
				if (ev.change) {
					initData();
				}
			}
		}

		if (e instanceof LoginEvent) {
			if (Tags.TAG_ORDER.equals(tag) || Tags.TAG_COLLECT.equals(tag)) {
				LoginEvent ev = (LoginEvent) e;
				if (ev.success) {
					initData();
				}
			}
		}

		if (e instanceof SlidingMenuOpenEvent) {
			SlidingMenuOpenEvent ev = (SlidingMenuOpenEvent) e;
			if (ev.isOpen) {

				slv.closeOpenedItems();

				// if (Tags.TAG_ORDER.equals(tag) ||
				// Tags.TAG_COLLECT.equals(tag)) {
				// for (int i = 0; i < opend.size(); i++) {
				// Integer open = opend.get(i);
				// slv.closeAnimate(open + 1);
				// }
				// }
			}
		}

	}

	private class LvAdapter extends BaseAdapter<Item> {

		public LvAdapter(List<Item> data) {
			super(data);
		}

		private class ViewHolder {
			public TextView tvTitle;
			public TextView tvAddr;
			public TextView tvDist;
			public TextView tvPriceOriginal;
			public TextView tvPrice;
			public ImageView ivLogo;
			public TextView tvContent;
			public TextView tvDate;
			public View layoutFront;
			public View ivSelected;
			public View layoutBack;
			public RatingBar rate;
			public View tvHasZdf;
			public View tvHasTjf;
			public View tvPricePre;
			public View layoutPrice;
			public ImageView ivFavorable;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = inflater.inflate(R.layout.recent_browse_lv_item,
						null);

				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.textView1);
				holder.tvAddr = (TextView) convertView
						.findViewById(R.id.textView2);
				holder.tvDist = (TextView) convertView
						.findViewById(R.id.tvDist);
				holder.layoutPrice = convertView.findViewById(R.id.layout2);
				holder.tvPrice = (TextView) convertView
						.findViewById(R.id.textView5);
				holder.tvPricePre = convertView.findViewById(R.id.textView6);
				holder.ivLogo = (ImageView) convertView
						.findViewById(R.id.imageView1);
				holder.ivFavorable = (ImageView) convertView
						.findViewById(R.id.imageView2);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Item i = getItem(position);

			// int bg[] = new int[] { R.drawable.lv_item_white_click,
			// R.drawable.lv_item_white_gray_click };
			// int p = position % 2;

			holder.tvTitle.setText(i.hotelsName);
			holder.tvAddr.setText(i.hotelsAddr);
			holder.tvDist.setText(i.dist + "km");

			// 根据当前销售时段显示相应的价格
			// if (activity.saleType == Room.SALE_TYPE_ZDF) {
			if (Room.isSellZdf()) {
				if (!StringUtils.isEmpty(i.hourroomPrice)) {
					holder.tvPricePre.setVisibility(View.VISIBLE);
					holder.tvPrice.setVisibility(View.VISIBLE);
					holder.tvPrice.setText(i.hourroomPrice);
					holder.layoutPrice.setVisibility(View.VISIBLE);
				} else {

					holder.layoutPrice.setVisibility(View.INVISIBLE);
				}

				if (i.hasTuan) {
					holder.ivFavorable.setVisibility(View.VISIBLE);
				} else {
					holder.ivFavorable.setVisibility(View.GONE);
				}

			} else if (Room.isSellWyf()) {
				if (!StringUtils.isEmpty(i.dayroomPrice)) {
					holder.layoutPrice.setVisibility(View.VISIBLE);
					holder.tvPricePre.setVisibility(View.VISIBLE);
					holder.tvPrice.setVisibility(View.VISIBLE);

					holder.tvPrice.setText(i.dayroomPrice);

				} else {
					holder.tvPricePre.setVisibility(View.INVISIBLE);
					holder.tvPrice.setVisibility(View.INVISIBLE);
					holder.layoutPrice.setVisibility(View.INVISIBLE);
				}

			} else if (Room.isSellLsf()) {
				if (!StringUtils.isEmpty(i.nightroomPrice)) {
					holder.layoutPrice.setVisibility(View.VISIBLE);
					holder.tvPricePre.setVisibility(View.VISIBLE);
					holder.tvPrice.setVisibility(View.VISIBLE);

					holder.tvPrice.setText(i.nightroomPrice);

				} else {
					holder.tvPricePre.setVisibility(View.INVISIBLE);
					holder.tvPrice.setVisibility(View.INVISIBLE);
					holder.layoutPrice.setVisibility(View.INVISIBLE);
				}

			}

			ac.mImageLoader.displayImage(URLs.URL_ZDF_API + i.logopath,
					holder.ivLogo, ac.optionsLogo);

			return convertView;
		}

		private void setDeleteListener(final ViewHolder holder,
				final int position, Item i) {
			// holder.layoutFront
			// .setOnLongClickListener(new OnLongClickListener() {
			// @Override
			// public boolean onLongClick(View v) {
			// AppContext.toastShow("lomg " + (position));
			//
			// // 选中当前长按的这条数据
			// holder.ivSelected.setVisibility(View.VISIBLE);
			// Item item = adapter.data.get(position);
			// item.isChecked = true;
			// checkedIds.add(item.hotelsId);
			//
			// // 开启多选
			// // TODO
			// // btnRight.setVisibility(View.VISIBLE);
			// slv.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
			// multiCheck = true;
			// return true;
			// }
			// });

			// Restore the checked state properly
			if (multiCheck) {
				if (i.isSelected) {
					holder.ivSelected.setVisibility(View.VISIBLE);
				} else {
					holder.ivSelected.setVisibility(View.GONE);
				}
			} else {
				holder.ivSelected.setVisibility(View.GONE);
			}

			// holder.layoutBack.findViewById(R.id.tvDelete).setOnClickListener(
			// new DeleteClickListener(position));
			holder.layoutFront.findViewById(R.id.layoutDelete)
					.setOnClickListener(new DeleteClickListener(position));
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

			delete(position);
		}
	}

	private void clickItem(int position) {
		Item item = adapter.getItem(position - 1);

		Log.d(TAG, "======clickItem======" + item.hotelsName);

		// // 多选状态
		// if (multiCheck) {
		// if (item.isChecked) {
		// v.findViewById(R.id.imageView3).setVisibility(View.GONE);
		// item.isChecked = false;
		//
		// checkedIds.remove(item.hotelsId);
		// if (checkedIds.size() == 0) {
		// hideMultiDelete();
		// }
		// } else {
		// v.findViewById(R.id.imageView3).setVisibility(View.VISIBLE);
		// item.isChecked = true;
		//
		// checkedIds.add(item.hotelsId);
		// }
		// return;
		// }
		//
		// if (UIHelper.isFastDoubleClick(v)) {
		// return;
		// }

		Bundle arg = new Bundle();
		arg.putSerializable(Keys.ITEM, item);

		if (Tags.TAG_COLLECT.equals(tag)) {

			UIHelper.showHotelActivity(activity, item);
			return;
		}
	}

//	/**
//	 * 批量 删除订单，取消收藏
//	 * 
//	 * @param batchDelIds
//	 */
//	protected void deleteBatch(final String batchDelIds) {
//		pb.setVisibility(View.VISIBLE);
//		new Thread() {
//			Message msg = new Message();
//
//			public void run() {
//				try {
//					RspMsg r = null;
//
//					if (ac.isLogin()) {
//						if (Tags.TAG_COLLECT.equals(tag)) {
//							r = ac.batchDeleteCollect(batchDelIds,
//									ac.getLoginUserId(), false);
//						}
//						if (Tags.TAG_ORDER.equals(tag)) {
//							r = ac.batchDeleteOrder(batchDelIds,
//									ac.getLoginUserId());
//						}
//					} else {
//						// TODO 获取未登录的本地缓存
//						r = RspMsg.parseDemo();
//					}
//
//					msg.what = WHAT_DELETE_ITEM_BATCH;
//					msg.obj = r;
//				} catch (AppException e) {
//					e.printStackTrace();
//					msg.what = WHAT_EXCEPTION;
//					msg.obj = e;
//				}
//				myHandler.sendMessage(msg);
//			};
//		}.start();
//	}

	/**
	 * 删除订单，取消收藏
	 * 
	 * @param position
	 */
	public void delete(final int position) {
		slv.dismiss(position + 1);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// AppContext.toastShow("onListItemClick " + position);
		Log.d(TAG, "======onListItemClick======position=" + position
				+ " ,openedPosition=" + openedPosition);
		// clickItem(position);

		// 兼容2.3系统下点击删除按钮，响应的却是点击item事件
		if (openedPosition == position) {
			delete(position - 1);
		} else {
			clickItem(position);
		}
	}

}
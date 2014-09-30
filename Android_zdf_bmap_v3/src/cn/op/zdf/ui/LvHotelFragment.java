package cn.op.zdf.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.op.common.AppException;
import cn.op.common.BaseAdapter;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.domain.URLs;
import cn.op.common.util.Constants;
import cn.op.common.util.Log;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.HotelUtil;
import cn.op.zdf.R;
import cn.op.zdf.dao.MyDbHelper;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.ItemPage;
import cn.op.zdf.domain.Room;
import cn.op.zdf.event.Event;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meizu.smartbar.SmartBarUtils;

import de.greenrobot.event.EventBus;

/**
 * 酒店列表
 * 
 * @author lufei
 * 
 */
public class LvHotelFragment extends SherlockFragment {

	private static final String TAG = Log.makeLogTag(LvHotelFragment.class);
	private PullToRefreshListView mPullRefreshListView;
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
	protected boolean isFirstItemVisible;
	private TextView tvKeyword;
	protected int mPageNum;
	protected boolean isSearching = false;
	private TextView tvTitle;
	private View layoutNoDataTip;
	private ImageView ivNoData;
	private View btnReload;
	private boolean isActivityResult4ChooseCity;
	private boolean isActivityResult4Search;

	protected static final int WHAT_INIT_DATA = 1;
	protected static final int WHAT_EXCEPTION = -1;
	protected static final int WHAT_DELETE_ITEM = 2;
	private static final int WHAT_INIT_HOTEL_PAGE_DATA = 3;
	protected static final int WHAT_DELETE_ITEM_BATCH = 4;

	protected static final int TODO_STATE = 1;
	protected static final int DONE_STATE = 2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		EventBus.getDefault().register(this);

		this.inflater = inflater;
		activity = getActivity();
		ac = AppContext.getAc();

		View view = inflater.inflate(R.layout.frag_lv_hotel, container, false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// 指示这个Fragment应该作为可选菜单的添加项（否则，这个Fragment不接受对onCreateOptionsMenu()方法的调用）
		if (SmartBarUtils.hasSmartBar()) {
			setHasOptionsMenu(true);

		}

		this.view = view;

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
				reqLastMapMoveLocData();
			}
		});

		initView();

	}

	private void initView() {

		View topBar = view.findViewById(R.id.topBarMain);
		ImageView btnLeft = (ImageView) topBar.findViewById(R.id.btnLeft);
		tvTitle = (TextView) topBar.findViewById(R.id.tvTitle);

		if (ac.lastChooseCity != null) {
			tvTitle.setText(ac.lastChooseCity.cityName);
		} else if (ac.lastLocCity != null) {
			tvTitle.setText(ac.lastLocCity.cityName);
		} else {
			tvTitle.setText("选择城市");
		}

		tvTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				Intent intent = new Intent(activity, CityChooseActivity.class);
				// activity.startActivity(intent);
				startActivityForResult(intent,
						Constants.REQUEST_CODE_CHOOSE_CITY);

				// UIHelper.showCityChoose(activity);
			}
		});

		btnLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				activity.finish();
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

						if (isSearching) {
							initDataByKeywords(ac.lastReqLatitude,
									ac.lastReqLongitude, ac.searchKeyword,
									mPageNum);
						} else {
							initData(ac.lastReqLatitude, ac.lastReqLongitude,
									mPageNum);
						}

					}
				});

		lv = (ListView) mPullRefreshListView.getRefreshableView();

		lv.setVerticalFadingEdgeEnabled(false);
		lv.setItemsCanFocus(false);

		adapter = new LvAdapter(new ArrayList<Item>());
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Log.d(TAG, "======onListItemClick======position=" + position);
				clickItem(v, position);
			}
		});

		// initData(ac.lastReqLatitude, ac.lastReqLongitude, mPageNum);

		View layoutKeyword = view.findViewById(R.id.layoutKeyword);
		final View ivExitSearch = layoutKeyword.findViewById(R.id.imageView2);

		ivExitSearch.setVisibility(View.INVISIBLE);

		tvKeyword = (TextView) layoutKeyword.findViewById(R.id.tvKeyword);
		tvKeyword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				Intent intent = new Intent(activity, SearchActivity.class);
				startActivityForResult(intent, Constants.REQUEST_CODE_SEARCH);
			}
		});

		tvKeyword.addTextChangedListener(new TextWatcher() {

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
					ivExitSearch.setVisibility(View.INVISIBLE);
				} else {
					ivExitSearch.setVisibility(View.VISIBLE);
				}
			}
		});

		ivExitSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				exitSearch();
			}
		});

	}

	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
			com.actionbarsherlock.view.MenuInflater inflater) {

		UIHelper.makeEmptyMenu(inflater, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "======onActivityResult====== requestCode=" + requestCode);

		switch (requestCode) {
		case Constants.REQUEST_CODE_SEARCH:
			isActivityResult4Search = true;
			if (Constants.RESULT_CODE_SEARCH_EXIT == resultCode) {

				exitSearch();

			} else if (Constants.RESULT_CODE_SEARCH_RESULT == resultCode) {
				isSearching = true;
				tvKeyword.setText(ac.searchKeyword);
				keyWordSearch();

				mPullRefreshListView.setMode(Mode.PULL_FROM_END);

			} else if (Constants.RESULT_CODE_SEARCH_RESULT_ONE == resultCode) {
				isSearching = true;
				String hotelId = data.getStringExtra(Keys.ID);
				selectOneHotel(hotelId);

				mPullRefreshListView.setMode(Mode.DISABLED);
			}
			break;
		case Constants.REQUEST_CODE_CHOOSE_CITY:
			isActivityResult4ChooseCity = true;
			if (resultCode == Constants.REQUEST_CODE_CHOOSE_CITY) {

				cityChoose();

				// Log.d(TAG, "======onActivityResult====== city=" +
				// city.cityName);
			}

			break;

		default:
			break;
		}

	}

	private void cityChoose() {
		Log.d(TAG, "======cityChoose======");
		// if (ac.lastChooseCity.cityId == null) {
		// setCityId();
		// }

		adapter.data.clear();
		hotelMap.clear();
		mPageNum = 0;

		tvTitle.setText(ac.lastChooseCity.cityName);
		initData(ac.lastChooseCity.cityLat, ac.lastChooseCity.cityLng, mPageNum);
	}

	private void exitSearch() {
		isSearching = false;
		tvKeyword.setText("");
		reqLastMapMoveLocData();
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
	}

	private void selectOneHotel(String hotelId) {
		final Item hotel = MyDbHelper.getInstance(activity).queryHotelById(
				hotelId, ac.lastReqLatitude, ac.lastReqLongitude);

		adapter.data.clear();
		hotelMap.clear();
		mPageNum = 0;
		adapter.data.add(hotel);

		adapter.notifyDataSetChanged();

		updateHotelPrice(adapter.data);
	}

	private void keyWordSearch() {
		adapter.data.clear();
		hotelMap.clear();
		mPageNum = 0;
		initDataByKeywords(ac.lastReqLatitude, ac.lastReqLongitude,
				ac.searchKeyword, mPageNum);
	}

	private void reqLastMapMoveLocData() {
		adapter.data.clear();
		hotelMap.clear();
		mPageNum = 0;
		initData(ac.lastReqLatitude, ac.lastReqLongitude, mPageNum);
	}

	@Override
	public void onResume() {
		Log.d(TAG, "======onResume======");
		super.onResume();

		if (isActivityResult4ChooseCity || isActivityResult4Search) {
			isActivityResult4ChooseCity = false;
			isActivityResult4Search = false;
			return;
		}

		if (adapter != null && adapter.data.size() == 0) {
			reqLastMapMoveLocData();
		}
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private MyHandler myHandler = new MyHandler(this);
	private HashMap<String, Item> hotelMap = new HashMap<String, Item>();;;
	protected static final int WHAT_UPDATE_HOTEL_PRICE = 2;

	static class MyHandler extends Handler {
		private WeakReference<LvHotelFragment> mWr;

		public MyHandler(LvHotelFragment frag) {
			super();
			this.mWr = new WeakReference<LvHotelFragment>(frag);
		}

		@Override
		public void handleMessage(Message msg) {

			try {
				LvHotelFragment frag = mWr.get();

				if (frag == null || !frag.isAdded()) {
					return;
				}

				switch (msg.what) {
				case WHAT_INIT_HOTEL_PAGE_DATA:
					// frag.pb.setVisibility(View.GONE);
					frag.mPullRefreshListView.onRefreshComplete();
					ItemPage hotelPage = (ItemPage) msg.obj;

					if (hotelPage.rspMsg.OK()) {

						if (hotelPage.list.size() > 0) {
							frag.mPageNum++;
						}

						int lastSelect = frag.lv.getSelectedItemPosition();
						if (frag.adapter.data.size() == 0) {
							// 初次加载、重新加载
							if (hotelPage.list.size() == 0) {
								frag.ivNoData
										.setImageResource(R.drawable.img_no_data_tip_hotel);
								frag.layoutNoDataTip
										.setVisibility(View.VISIBLE);
								frag.mPullRefreshListView
										.setVisibility(View.GONE);
							} else {
								frag.layoutNoDataTip.setVisibility(View.GONE);
								frag.mPullRefreshListView
										.setVisibility(View.VISIBLE);

								frag.adapter.data = hotelPage.list;
								frag.adapter.notifyDataSetChanged();
							}
						} else {
							frag.layoutNoDataTip.setVisibility(View.GONE);
							// 分页加载更多
							if (hotelPage.list.size() == 0) {
								AppContext.toastShow("没有更多数据了");
							} else {
								frag.adapter.data.addAll(hotelPage.list);
								frag.adapter.notifyDataSetChanged();
								frag.lv.setSelection(lastSelect);
							}
						}

					} else {
						AppContext.toastShow(hotelPage.rspMsg.message);
					}
					break;
				case -WHAT_INIT_HOTEL_PAGE_DATA:
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
				case WHAT_UPDATE_HOTEL_PRICE:
					ItemPage hotelPricePage = (ItemPage) msg.obj;

					if (hotelPricePage.rspMsg.OK()) {

						int lastSelect = frag.lv.getSelectedItemPosition();

						frag.adapter.notifyDataSetChanged();

						frag.lv.setSelection(lastSelect);

						Log.d(TAG,
								"======WHAT_UPDATE_HOTEL_PRICE====== frag.adapter.data.size="
										+ frag.adapter.data.size());
					} else {
						AppContext.toastShowException(R.string.pleaseRetry);

						try {
							frag.adapter.notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					break;

				case -WHAT_UPDATE_HOTEL_PRICE:
					((AppException) msg.obj).makeToast(frag.ac);

					try {
						frag.adapter.notifyDataSetChanged();
					} catch (Exception e) {
						e.printStackTrace();
					}

					break;

				case WHAT_EXCEPTION:
					frag.pb.setVisibility(View.GONE);
					frag.mPullRefreshListView.onRefreshComplete();
					((AppException) msg.obj).makeToast(frag.ac);
					break;

				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void initData(final double latitude, final double longitude,
			int pageNum) {

		ac.lastReqLatitude = latitude;
		ac.lastReqLongitude = longitude;

		final ItemPage cacheData;
		if (ac.lastChooseCity != null && ac.lastChooseCity.cityId != null) {

			Log.d(TAG, "======initData====== cityId="
					+ ac.lastChooseCity.cityId);

			cacheData = MyDbHelper.getInstance(activity)
					.queryHotelPageInCityByLatLng(latitude, longitude,
							ac.lastChooseCity.cityId, pageNum);
		} else {
			cacheData = MyDbHelper.getInstance(activity)
					.queryHotelPage20KmByLatLng(latitude, longitude, pageNum);

		}

		for (int i = 0; i < cacheData.list.size(); i++) {
			Item item = cacheData.list.get(i);
			hotelMap.put(item.hotelsId, item);
		}

		Message msg = new Message();
		msg.what = WHAT_INIT_HOTEL_PAGE_DATA;
		msg.obj = cacheData;

		myHandler.sendMessage(msg);

		updateHotelPrice(cacheData.list);
	}

	public void hideEmptyPriceHotel(String hotelIds) {

		if (!StringUtils.isEmpty(hotelIds)) {
			String[] split = hotelIds.split(",");

			for (int i = 0; i < split.length; i++) {
				String hotelsId = split[i];
				Item item = hotelMap.get(hotelsId);

				if (Room.isSellWyf()) {

					if (StringUtils.isEmpty(item.dayroomPrice)) {
						// 午夜房更新价格为空时，移除此酒店
						adapter.data.remove(hotelMap.remove(hotelsId));
					}

				} else if (Room.isSellLsf()) {

					if (StringUtils.isEmpty(item.nightSalePrice)) {
						// 零时房更新价格为空时，移除此酒店
						adapter.data.remove(hotelMap.remove(hotelsId));
					}
				}
			}
		}

	}

	private void initDataByKeywords(double lastReqLatitude,
			double lastReqLongitude, String searchKeyword, int pageNum) {

		ItemPage cacheData = null;
		if (ac.lastChooseCity != null && ac.lastChooseCity.cityId != null) {
			cacheData = MyDbHelper.getInstance(activity)
					.queryHotelPageInCityByKeyword(ac.lastReqLatitude,
							ac.lastReqLongitude, ac.searchKeyword,
							ac.lastChooseCity.cityId, pageNum);
		} else {
			cacheData = MyDbHelper.getInstance(activity)
					.queryHotelPage20KmByKeyword(ac.lastReqLatitude,
							ac.lastReqLongitude, ac.searchKeyword,
							ac.lastChooseCity.cityId, pageNum);
		}

		for (int i = 0; i < cacheData.list.size(); i++) {
			Item item = cacheData.list.get(i);
			hotelMap.put(item.hotelsId, item);
		}

		Message msg = new Message();
		msg.what = WHAT_INIT_HOTEL_PAGE_DATA;
		msg.arg1 = 0;
		msg.obj = cacheData;

		myHandler.sendMessage(msg);

		updateHotelPrice(cacheData.list);
	}

	/**
	 * 更新酒店价格，以及销售房间类型
	 * 
	 * @param list
	 */
	private void updateHotelPrice(final List<Item> list) {
		if (!ac.isNetworkConnected()) {
			AppContext.toastShow(R.string.pleaseCheckNet);
			return;
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < list.size(); i++) {
					sb.append(list.get(i).hotelsId).append(",");
				}

				if (sb.length() == 0) {
					return;
				}

				String hotelIds = sb.deleteCharAt(sb.length() - 1).toString();
				try {

					ItemPage q = ac.getHotelsPrice(hotelIds);

					// 更新价格更新价格
					if (!q.rspMsg.OK()) {
						hideEmptyPriceHotel(hotelIds);
						return;
					}

					int size = q.list.size();
					for (int i = 0; i < size; i++) {
						Item itemUpdate = q.list.get(i);

						String hotelsId = itemUpdate.hotelsId;
						Item item = hotelMap.get(hotelsId);

						if (item == null) {
							continue;
						}

						item.isHour = itemUpdate.isHour;
						item.isSpecial = itemUpdate.isSpecial;
						item.isMidNight = itemUpdate.isMidNight;
						item.hasTuan = itemUpdate.hasTuan;
						item.zdfDurationType = itemUpdate.zdfDurationType;

						if (Room.isSellZdf()) {
							if (!StringUtils.isEmpty(itemUpdate.hourroomPrice)) {
								item.hourroomPrice = itemUpdate.hourroomPrice;

								// TEST
								// if (i % 2 == 0) {
								// // item.hourroomPrice = "70";
								// adapter.data.remove(hotelMap
								// .remove(hotelsId));
								// }

							}

							if (!StringUtils.isEmpty(itemUpdate.hours)) {
								item.hours = itemUpdate.hours;
							}
						} else if (Room.isSellWyf()) {
							if (!StringUtils.isEmpty(itemUpdate.dayroomPrice)) {
								item.dayroomPrice = itemUpdate.dayroomPrice;

								if (!StringUtils
										.isEmpty(itemUpdate.daySalePrice)) {
									item.daySalePrice = itemUpdate.daySalePrice;
								}

							} else {
								// 午夜房更新价格为空时，移除此酒店
								adapter.data.remove(hotelMap.remove(hotelsId));
							}

						} else if (Room.isSellLsf()) {
							if (!StringUtils.isEmpty(itemUpdate.nightroomPrice)) {
								item.nightroomPrice = itemUpdate.nightroomPrice;

								if (!StringUtils
										.isEmpty(itemUpdate.nightSalePrice)) {
									item.nightSalePrice = itemUpdate.nightSalePrice;
								}
							} else {
								// 零时房更新价格为空时，移除此酒店
								adapter.data.remove(hotelMap.remove(hotelsId));
							}
						}
					}

					msg.what = WHAT_UPDATE_HOTEL_PRICE;
					msg.obj = q;

					// 更新数据库酒店价格
					if (q.rspMsg.OK()) {
						MyDbHelper.getInstance(activity).updateHotelPrice(
								q.list);
					}
				} catch (AppException e) {
					hideEmptyPriceHotel(hotelIds);

					e.printStackTrace();
					msg.what = -WHAT_UPDATE_HOTEL_PRICE;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			}
		}.start();

	}

	public void onEventMainThread(Event e) {
		Log.d(TAG, "======onEventMainThread======"
				+ e.getClass().getSimpleName());

		// if (e instanceof ExitSearchEvent) {
		// ExitSearchEvent ev = (ExitSearchEvent) e;
		// if (ev.isExit) {
		// tvKeyword.setText("");
		// }
		// }

		// if (e instanceof SearchResultEvent) {
		// SearchResultEvent ev = (SearchResultEvent) e;
		// if (ev.hasResult) {
		// tvKeyword.setText(ac.searchKeyword);
		// }
		// }

		// if (e instanceof SelectOneHotelEvent) {
		// // if (activity.isShowHotelMap) {
		// // return;
		// // }
		//
		// // SelectOneHotelEvent ev = (SelectOneHotelEvent) e;
		// // if (ev.hotel != null) {
		// // notifyAdapterData(ac.mHotelList);
		// // }
		// }

		// if (e instanceof PanelExpandedEvent) {
		// PanelExpandedEvent ev = (PanelExpandedEvent) e;
		// if (ev.isExpanded) {
		// // if (activity.isSearch) {
		// // mPullRefreshListView
		// // .setMode(PullToRefreshBase.Mode.PULL_FROM_END);
		// // } else {
		// // mPullRefreshListView
		// // .setMode(PullToRefreshBase.Mode.DISABLED);
		// // }
		// }
		// }

		// if (e instanceof PanelCollapsedEvent) {
		// PanelCollapsedEvent ev = (PanelCollapsedEvent) e;
		// if (ev.isCollapsed) {
		// if (SmartBarUtils.hasSmartBar()) {
		// lv.setSelection(0);
		// } else {
		// lv.smoothScrollToPosition(0);
		// }
		// }
		// }

		// if (e instanceof HotelMarkerPriceUpdateEvent) {
		// // if (activity.isShowHotelMap) {
		// // return;
		// // }
		//
		// HotelMarkerPriceUpdateEvent ev = (HotelMarkerPriceUpdateEvent) e;
		// if (ev.isUpdate) {
		// notifyAdapterData(ac.mHotelList);
		//
		// // if (activity.isSearch) {
		// // mPullRefreshListView
		// // .setMode(PullToRefreshBase.Mode.PULL_FROM_END);
		// // } else {
		// // mPullRefreshListView
		// // .setMode(PullToRefreshBase.Mode.DISABLED);
		// // }
		// }
		// }

		// if (e instanceof InitHotelPageDataEvent) {
		// // if (activity.isShowHotelMap) {
		// // return;
		// // }
		//
		// InitHotelPageDataEvent ev = (InitHotelPageDataEvent) e;
		// if (ev.isInit) {
		// notifyAdapterData(ac.mHotelList);
		// }
		// }

	}

	private void notifyAdapterData(List<Item> list) {
		adapter.data = list;
		// 按当前选择的排序方式进行排序
		HotelUtil.listSortBy(ac.curtOrderBy, adapter.data, ac.saleType);
		adapter.notifyDataSetChanged();
	}

	private class LvAdapter extends BaseAdapter<Item> {

		public LvAdapter(List<Item> data) {
			super(data);
		}

		private class ViewHolder {
			public TextView tvTitle;
			public TextView tvAddr;
			public TextView tvDist;
			public TextView tvPrice;
			public ImageView ivLogo;
			public View tvPricePre;
			public View layoutPrice;
			public ImageView ivFavorable;
			public View layoutService;
			public ImageView ivServiceWifi;
			public ImageView ivServicePark;
			public ImageView ivHours4;
			public ImageView ivHours6;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.hotel_lv_item, parent,
						false);

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

				holder.layoutService = convertView
						.findViewById(R.id.layoutService);
				holder.ivServiceWifi = (ImageView) holder.layoutService
						.findViewById(R.id.ivWifi);
				holder.ivServicePark = (ImageView) holder.layoutService
						.findViewById(R.id.ivPark);
				holder.ivHours4 = (ImageView) holder.layoutService
						.findViewById(R.id.ivHours4);
				holder.ivHours6 = (ImageView) holder.layoutService
						.findViewById(R.id.ivHours6);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Item i = getItem(position);

			// int bg[] = new int[] { R.drawable.lv_item_white_trans_click,
			// R.drawable.lv_item_white_gray_trans_click };
			// int p = position % 2;
			//
			// convertView.setBackgroundResource(bg[p]);

			holder.tvTitle.setText(i.hotelsName);
			holder.tvAddr.setText(i.hotelsAddr);
			holder.tvDist.setText(i.dist + "km");

			// 酒店提供服务设施，具体参考：酒店提供设施对照.txt
			// 酒店所提供服务
			if (i.facilitysIds != null) {
				if (i.facilitysIds.contains("136")) {
					holder.ivServiceWifi
							.setImageResource(R.drawable.ic_service_wifi_1_true);
				} else {
					holder.ivServiceWifi
							.setImageResource(R.drawable.ic_service_wifi_1);
				}
				if (i.facilitysIds.contains("137")) {
					holder.ivServicePark
							.setImageResource(R.drawable.ic_service_park_1_true);
				} else {
					holder.ivServicePark
							.setImageResource(R.drawable.ic_service_park_1);
				}
			} else {
				holder.ivServiceWifi
						.setImageResource(R.drawable.ic_service_wifi_1);
				holder.ivServicePark
						.setImageResource(R.drawable.ic_service_park_1);
			}
			if (i.zdfDurationType != null) {
				if (i.zdfDurationType.contains("4")) {
					holder.ivHours4.setImageResource(R.drawable.ic_hour_4_true);
				} else {
					holder.ivHours4.setImageResource(R.drawable.ic_hour_4);
				}
				if (i.zdfDurationType.contains("6")) {
					holder.ivHours6.setImageResource(R.drawable.ic_hour_6_true);
				} else {
					holder.ivHours6.setImageResource(R.drawable.ic_hour_6);
				}
			} else {
				holder.ivHours4.setImageResource(R.drawable.ic_hour_4);
				holder.ivHours6.setImageResource(R.drawable.ic_hour_6);
			}

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
	}

	private void clickItem(View v, int position) {
		if (UIHelper.isFastDoubleClick(v)) {
			return;
		}
		Item item = adapter.getItem(position - 1);
		Log.d(TAG, "======clickItem======" + item.hotelsName);

		UIHelper.showHotelActivity(activity, item);
	}

	// @Override
	// public void onListItemClick(ListView l, View v, int position, long id) {
	// Log.d(TAG, "======onListItemClick======position=" + position);
	// clickItem(v, position);
	// }

}
package cn.op.zdf.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.op.common.AppException;
import cn.op.common.BaseAdapter;
import cn.op.common.UIHelper;
import cn.op.common.domain.URLs;
import cn.op.common.util.DateUtil;
import cn.op.common.util.Log;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.ItemPage;
import cn.op.zdf.domain.Room;
import cn.op.zdf.event.Event;

import com.meizu.smartbar.SmartBarUtils;
import com.viewpagerindicator.CirclePageIndicator;

import de.greenrobot.event.EventBus;

/**
 * 优惠活动列表
 * 
 * @author lufei
 * 
 */
public class LvGiftFragment extends Fragment {

	private static final String TAG = Log.makeLogTag(LvGiftFragment.class);
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
	private ImageView ivMask;
	private VpAdapter vpAdapter;
	private CirclePageIndicator mIndicator;
	private View ivTag;
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
		Log.d(TAG, "======onCreateView======");
		EventBus.getDefault().register(this);

		this.inflater = inflater;
		activity = (MainActivity) getActivity();
		ac = AppContext.getAc();

		tag = this.getTag();

		View view = inflater.inflate(R.layout.frag_lv_gift, container, false);
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

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "======onViewCreated======");

		this.view = view;

		pb = view.findViewById(R.id.pb);
		pb.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		
		lv = (ListView) view.findViewById(R.id.listView1);
		tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);

		ivMask = (ImageView) view.findViewById(R.id.sreach_bg);

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

	private void initView() {

		lv.setVerticalFadingEdgeEnabled(false);
		lv.setItemsCanFocus(false);

		View layoutVp = inflater.inflate(R.layout.layout_gift_vp, lv, false);

		ViewPager vp = (ViewPager) layoutVp.findViewById(R.id.viewPager1);
		ivTag = layoutVp.findViewById(R.id.ivTag);
		vpAdapter = new VpAdapter(new ArrayList<Item>());
		vp.setAdapter(vpAdapter);

		// vp.setLayoutParams(new AbsListView.LayoutParams(
		// AbsListView.LayoutParams.MATCH_PARENT, getResources()
		// .getDimensionPixelSize(R.dimen.height_gift_vp)));

		mIndicator = (CirclePageIndicator) layoutVp
				.findViewById(R.id.indicator);
		mIndicator.setViewPager(vp);

		lv.addHeaderView(layoutVp);

		TextView tv = new TextView(activity);
		tv.setLayoutParams(new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT,
				AbsListView.LayoutParams.WRAP_CONTENT));
		tv.setBackgroundResource(R.drawable.img_gift_pre_bg);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setText("过往活动");
		tv.setTextColor(getResources().getColor(R.color.gray_light));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setPadding(
				getResources().getDimensionPixelSize(R.dimen.padding_medium),
				0, 0, 0);
		lv.addHeaderView(tv);

		List<Item> list = new ArrayList<Item>();
		adapter = new LvAdapter(list);
		lv.setAdapter(adapter);

		initData();
	}

	private ArrayList<String> checkedIds = new ArrayList<String>();;
	private MyHandler myHandler = new MyHandler(this);

	static class MyHandler extends Handler {
		private WeakReference<LvGiftFragment> mWr;

		public MyHandler(LvGiftFragment frag) {
			super();
			this.mWr = new WeakReference<LvGiftFragment>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			LvGiftFragment frag = mWr.get();

			switch (msg.what) {
			case WHAT_INIT_DATA:
				frag.pb.setVisibility(View.GONE);
				ItemPage q = (ItemPage) msg.obj;

				if (q.rspMsg.OK()) {
					if (q.list.size() == 0) {
						// AppContext.toastShow(R.string.noData);
						frag.tvEmpty.setVisibility(View.VISIBLE);
						frag.tvEmpty.setText("还没有活动哦");

						frag.lv.setVisibility(View.GONE);

						frag.adapter.data = q.list;
						frag.adapter.notifyDataSetChanged();
					} else {
						frag.tvEmpty.setVisibility(View.GONE);

						frag.listAll = q.list;

						Calendar calendarCurt = Calendar.getInstance();

						ArrayList<Item> listPast = new ArrayList<Item>();
						ArrayList<Item> listCurt = new ArrayList<Item>();
						for (int i = 0; i < q.list.size(); i++) {
							Item item = q.list.get(i);

							Calendar end = Calendar.getInstance();
							end.setTime(DateUtil.str2Date(item.endDate,
									DateUtil.DATE_FORMAT_1));

							if (end.before(calendarCurt)) {
								listPast.add(item);
							} else {
								listCurt.add(item);
							}

						}

						if (listCurt.size() > 0) {
							frag.ivTag.setVisibility(View.VISIBLE);

							frag.vpAdapter.data = listCurt;
							frag.vpAdapter.notifyDataSetChanged();
							frag.mIndicator.notifyDataSetChanged();
						}

						frag.adapter.data = listPast;
						frag.adapter.notifyDataSetChanged();
					}
				} else {
					AppContext.toastShow(q.rspMsg.message);
				}
				break;

			case WHAT_EXCEPTION:
				frag.pb.setVisibility(View.GONE);
				frag.tvEmpty.setVisibility(View.VISIBLE);
				frag.tvEmpty.setText("抱歉，请稍后再试");
				frag.lv.setVisibility(View.GONE);

				((AppException) msg.obj).makeToast(frag.ac);

				break;

			default:
				break;
			}
		}
	}

	class VpAdapter extends PagerAdapter {

		ArrayList<Item> data = new ArrayList<Item>();

		public VpAdapter(ArrayList<Item> data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public View instantiateItem(ViewGroup container, final int position) {
			View view = inflater.inflate(R.layout.gift_vp_item, null);

			ImageView ivPic = (ImageView) view.findViewById(R.id.imageView1);

			final Item gift = data.get(position);

			ac.mImageLoader.displayImage(URLs.URL_ZDF_API + gift.filePath,
					ivPic, ac.optionsLogo);

			ivPic.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (UIHelper.isFastDoubleClick(v)) {
						return;
					}

					UIHelper.showGiftDetail(activity, gift);
				}
			});

			container.addView(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			return view;
		}

	}

	private void initData() {

//		pb.setVisibility(View.VISIBLE);
//		new Thread() {
//			public void run() {
//				Message msg = new Message();
//				try {
//					ItemPage q = null;
//
//					q = ac.getGiftPage();
//
//					msg.what = WHAT_INIT_DATA;
//					msg.obj = q;
//				} catch (AppException e) {
//					e.printStackTrace();
//					msg.what = WHAT_EXCEPTION;
//					msg.obj = e;
//				}
//				myHandler.sendMessage(msg);
//			};
//		}.start();
	}

	public void onEventMainThread(Event e) {
		Log.d(TAG, "======onEventMainThread======"
				+ e.getClass().getSimpleName());

	}

	private void notifyAdapterData(List<Item> list) {
		adapter.data = list;
		// 按当前选择的排序方式进行排序
		listSortBy(ac.curtOrderBy);
		adapter.notifyDataSetChanged();

	}

	private void listSortBy(final int orderBy) {
		Collections.sort(adapter.data, new Comparator<Item>() {
			@Override
			public int compare(Item lhs, Item rhs) {
				switch (orderBy) {
				case Item.ORDER_BY_PRICE:

					int price1 = 0;
					int price2 = 0;
					if (ac.saleType == Room.SALE_TYPE_ZDF) {
						price1 = StringUtils.toInt(lhs.hourroomPrice);
						price2 = StringUtils.toInt(rhs.hourroomPrice);

					} else if (ac.saleType == Room.SALE_TYPE_WYF) {
						price1 = StringUtils.toInt(lhs.dayroomPrice);
						price2 = StringUtils.toInt(rhs.dayroomPrice);

					}

					return price1 - price2;
				case Item.ORDER_BY_DISTANCE:
					float dist1 = StringUtils.toFloat(lhs.dist);
					float dist2 = StringUtils.toFloat(rhs.dist);

					return Float.compare(dist1, dist2);
					// TODO case Item. ORDER_BY_COMMENT:
				}
				return 0;
			}
		});

		adapter.notifyDataSetChanged();
	}

	private class LvAdapter extends BaseAdapter<Item> {

		public LvAdapter(List<Item> data) {
			super(data);
		}

		private class ViewHolder {
			public TextView tvTitle;
			public TextView tvAddr;
			public TextView tvBrief;
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
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = inflater.inflate(R.layout.gift_lv_item, null);

				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.textView1);
				holder.tvBrief = (TextView) convertView
						.findViewById(R.id.textView3);
				holder.ivLogo = (ImageView) convertView
						.findViewById(R.id.imageView1);
				holder.ivSelected = convertView.findViewById(R.id.imageView3);
				holder.layoutFront = convertView.findViewById(R.id.front);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Item i = getItem(position);

			holder.tvTitle.setText(i.eventTitle);
			holder.tvBrief.setText(i.remark);

			ac.mImageLoader.displayImage(URLs.URL_ZDF_API + i.filePath,
					holder.ivLogo, ac.optionsLogo);

			holder.layoutFront.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (UIHelper.isFastDoubleClick(v)) {
						return;
					}

					UIHelper.showGiftDetail(activity, i);
				}
			});

			return convertView;
		}

	}

}
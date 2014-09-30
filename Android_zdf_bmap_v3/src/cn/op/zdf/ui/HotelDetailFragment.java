package cn.op.zdf.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import cn.op.common.AppConfig;
import cn.op.common.AppException;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.constant.Tags;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.URLs;
import cn.op.common.domain.UserInfo;
import cn.op.common.util.AnimationUtil;
import cn.op.common.util.Constants;
import cn.op.common.util.Log;
import cn.op.common.util.RegUtil;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.Room;
import cn.op.zdf.domain.RoomPage;
import cn.op.zdf.event.Event;
import cn.op.zdf.event.HideHotelMapEvent;
import cn.op.zdf.event.HideReserveEvent;
import cn.op.zdf.event.LoginEvent;
import cn.op.zdf.event.OrderChangeEvent;
import cn.op.zdf.event.PaySuccessEvent;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.cyrilmottier.android.translucentactionbar.HotelDetailNotifyingScrollView.OnScrollChangedListener;
import com.meizu.smartbar.SmartBarUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tendcloud.tenddata.TCAgent;

import de.greenrobot.event.EventBus;

/**
 * 酒店详情
 * 
 * @author lufei
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HotelDetailFragment extends SherlockFragment {

	protected static final String TAG = Log
			.makeLogTag(HotelDetailFragment.class);

	protected static final int WHAT_EXCEPTION = -1;
	protected static final int WHAT_INIT_ROOM_VIEW = 2;
	protected static final int WHAT_INIT_HOTEL_VIEW = 3;
	protected static final int WHAT_VP_AUTO_TURN = 4;
	protected static final int WHAT_PUT_COLLECT = 5;
	protected static final int WHAT_SUBMIT_ORDER = 6;
	protected static final int WHAT_INIT_ROOM_VIEW_XIECHENG = 7;
	protected static final int WHAT_CHECK_USER_EXIST = 8;

	private HotelDetailActivity activity;
	private View pb;
	private AppContext ac;
	private String hotelId;
	// private TextView tvReserveArriveTime;
	private LayoutInflater inflater;
	private Gallery mViewPager;
	private View view;
	/**
	 * 支付方式-是否是在线支付
	 */
	protected boolean isPayOnline = false;
	protected Fragment fragmentHotelMap;
	private Item item;
	private View layoutTopbar;
	// private View layoutPay;
	// private View tvTip;
	private ViewSwitcher viewSwitcher;
	private TextView tvTopTitle;
	// private TextView tvReserveArriveTimeTitle;
	private EditText tvReserveLiveMan;
	private TextView tvPayWay;
	private View dialogCancel;
	private View ivMask;
	private ImageView btnLeft;
	private ImageView btnRight;

	/**
	 * 是否收藏
	 */
	protected boolean isCollection = false;
	private Room orderRoom;
	// private View ivTopBarBg;
	// private View layoutZdf;
	// private View layoutTjf;
	private View btnSubmit;
	private TextView tvSaleTimeTip;

	private SamplePagerAdapter pagerAdapter;
	private LinearLayout layoutContentReserve;
	private EditText tvReserveLiveManPhone;
	public List<Room> listRoom = new ArrayList<Room>();
	private TextView tvCancelDialogTip;

	private View layoutCallDialog;
	private TextView tvLiveTip;
	protected int mHeightVp;
	private ImageView ivTopBarBg;
	private TextView tvHourEndTip;
	private TextView tvPicCount;

	private ImageView ivServiceWifi;

	private ImageView ivServicePark;

	private ImageView ivHours4;

	private ImageView ivHours6;

	private View layoutNoDataTip;

	private ImageView ivNoData;

	private View btnReload;

	private TextView tvAddr;

	private TextView tvTel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		EventBus.getDefault().register(this);

		this.inflater = inflater;

		View view = inflater.inflate(R.layout.hotel, container, false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		return view;
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		activity = (HotelDetailActivity) getActivity();
		ac = AppContext.getAc();
		this.view = view;

		// 指示这个Fragment应该作为可选菜单的添加项（否则，这个Fragment不接受对onCreateOptionsMenu()方法的调用）
		if (SmartBarUtils.hasSmartBar()) {
			setHasOptionsMenu(true);

		}

		item = null;
		Bundle arg = getArguments();
		if (arg != null) {
			item = (Item) arg.getSerializable(Keys.ITEM);
		}

		if (item == null) {
			return;
		}

		hotelId = item.hotelsId;

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
				initRoomData();
			}
		});

		mViewPager = (Gallery) view.findViewById(R.id.svHeader);
		ScrollView sv = (ScrollView) view.findViewById(R.id.scrollView1);

		layoutTopbar = view.findViewById(R.id.topBar);

		tvTopTitle = (TextView) layoutTopbar.findViewById(R.id.tvTitle);
		btnLeft = (ImageView) layoutTopbar.findViewById(R.id.btnLeft);
		btnRight = (ImageView) layoutTopbar.findViewById(R.id.btnRight);

		btnRight.setVisibility(View.INVISIBLE);

		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				if (ac.isReserve) {
					// showDialogCancel();
					hideReserve();
				} else {
					activity.finish();
				}
			}
		});

		btnRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				UIHelper.showPathNav(activity, item);

			}
		});

		layoutContentReserve = (LinearLayout) view
				.findViewById(R.id.layout_content_reserve);
		ivMask = view.findViewById(R.id.ivMask);
		dialogCancel = view.findViewById(R.id.dialogCancel);
		layoutCallDialog = view.findViewById(R.id.layoutCall);

		View btnCall400 = layoutCallDialog.findViewById(R.id.button1);
		Button btnCallHotel = (Button) layoutCallDialog
				.findViewById(R.id.button2);
		View btnCancel = layoutCallDialog.findViewById(R.id.button3);

		btnCall400.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				hideDialogCall();

				UIHelper.call(activity, Constants.TEL_JUJIA);
			}
		});

		if (!StringUtils.isEmpty(item.hotelsTel)
				&& !item.hotelsTel.trim().equals("0")) {
			btnCallHotel.setText("拨至酒店 " + item.hotelsTel);
		} else {
			btnCallHotel.setText("酒店电话");
		}

		btnCallHotel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				if (!StringUtils.isEmpty(item.hotelsTel)
						&& !item.hotelsTel.trim().equals("0")) {

					if (layoutCallDialog.getVisibility() == View.VISIBLE) {
						AnimationUtil.animationShowSifbHideSotbSpecial(
								activity, false, layoutCallDialog);
						AnimationUtil.animationShowHideAlphaSpecial(activity,
								false, ivMask);
					}

					UIHelper.call(activity, item.hotelsTel);
				} else {
					return;
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				if (layoutCallDialog.getVisibility() == View.VISIBLE) {
					AnimationUtil.animationShowSifbHideSotbSpecial(activity,
							false, layoutCallDialog);
					AnimationUtil.animationShowHideAlphaSpecial(activity,
							false, ivMask);
				}
			}
		});

		ivMask.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				if (layoutCallDialog.getVisibility() == View.VISIBLE) {
					AnimationUtil.animationShowSifbHideSotbSpecial(activity,
							false, layoutCallDialog);
					AnimationUtil.animationShowHideAlphaSpecial(activity,
							false, ivMask);

					return true;
				} else {
					return true;
				}

			}
		});

		View btnSureDialog = dialogCancel.findViewById(R.id.button1);
		View btnCancelDialog = dialogCancel.findViewById(R.id.button2);
		tvCancelDialogTip = (TextView) dialogCancel.findViewById(R.id.tvMsg);
		TextView tvDistance = (TextView) view.findViewById(R.id.tvDist);
		View layoutAddr = view.findViewById(R.id.layoutAddr);
		View layoutTel = view.findViewById(R.id.layoutTel);
		tvAddr = (TextView) view.findViewById(R.id.tvAddr);
		tvTel = (TextView) view.findViewById(R.id.tvTel);
		TextView tvHotelName = (TextView) view.findViewById(R.id.tvHotelName);

		ImageView ivHotelLogo = (ImageView) view.findViewById(R.id.ivHotelLogo);
		tvPicCount = (TextView) view.findViewById(R.id.tvPicCount);

		// 酒店提供服务设施，具体参考：酒店提供设施对照.txt
		View layoutService = view.findViewById(R.id.layoutService);
		ivServiceWifi = (ImageView) layoutService.findViewById(R.id.ivWifi);
		ivServicePark = (ImageView) layoutService.findViewById(R.id.ivPark);
		ivHours4 = (ImageView) layoutService.findViewById(R.id.ivHours4);
		ivHours6 = (ImageView) layoutService.findViewById(R.id.ivHours6);

		if (item.zdfDurationType != null) {
			if (item.zdfDurationType.contains("4")) {
				ivHours4.setImageResource(R.drawable.ic_hour_4_true);
			}
			if (item.zdfDurationType.contains("6")) {
				ivHours6.setImageResource(R.drawable.ic_hour_6_true);
			}
		}

		tvSaleTimeTip = (TextView) view.findViewById(R.id.tvSaleTimeTip);
		tvLiveTip = (TextView) view.findViewById(R.id.tvLiveTip);
		tvHourEndTip = (TextView) view.findViewById(R.id.tvHourEndTip);

		tvTel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				showDialogCall();

			}
		});

		layoutTel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvTel.performClick();
			}
		});

		tvAddr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				UIHelper.showPathNav(activity, item);
			}
		});

		layoutAddr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvAddr.performClick();
			}
		});

		if (!StringUtils.isEmpty(item.hotelsName)) {
			tvHotelName.setText(item.hotelsName);
		}
		if (!StringUtils.isEmpty(item.hotelsAddr)) {
			tvAddr.setText(item.hotelsAddr);
		}
		if (!StringUtils.isEmpty(item.hotelsTel)) {
			tvTel.setText(item.hotelsTel);
		}
		tvDistance.setText(item.dist + "km");

		ac.mImageLoader.displayImage(URLs.URL_ZDF_API + item.logopath,
				ivHotelLogo, ac.optionsLogo);

		// layoutTopbar.setBackgroundResource(R.color.transparent);
		// layoutTopbar.setAlpha(0f);
		ivTopBarBg = (ImageView) view.findViewById(R.id.ivTopBarBg);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ivTopBarBg.setAlpha(0f);
		} else {
			ivTopBarBg.setVisibility(View.GONE);
		}

		mViewPager.post(new Runnable() {
			@Override
			public void run() {
				mHeightVp = mViewPager.getHeight();
				Log.d(TAG, "======post mHeightVp======" + mHeightVp);
			}
		});

		// sv.setOnScrollChangedListener(new OnScrollChangedListener() {
		// @Override
		// public void onScrollChanged(ScrollView who, int l, int t, int oldl,
		// int oldt) {
		//
		// int tChange = t - oldt;
		//
		// float scale = (float) t / (float) mHeightVp;
		//
		// Log.d(TAG, "======onScrollChanged====== l=" + l + ", t=" + t
		// + ", oldl=" + oldl + ", oldt=" + oldt + ", tChange="
		// + tChange + " ,scale=" + scale);
		//
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// ivTopBarBg.setAlpha(scale);
		// } else {
		// if (scale >= 1) {
		// ivTopBarBg.setVisibility(View.VISIBLE);
		// } else {
		// ivTopBarBg.setVisibility(View.GONE);
		// }
		// }
		//
		// }
		// });

		btnSureDialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;

				}

				AnimationUtil.animationShowSifbHideSotbSpecial(activity, false,
						dialogCancel);
				AnimationUtil.animationShowHideAlphaSpecial(activity, false,
						ivMask);

				hideReserve();
			}
		});
		btnCancelDialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				AnimationUtil.animationShowSifbHideSotbSpecial(activity, false,
						dialogCancel);
				AnimationUtil.animationShowHideAlphaSpecial(activity, false,
						ivMask);
			}
		});

		tvAddr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				UIHelper.showPathNav(activity, item);
			}
		});

		initRoomData();
		initHotelData();
	}

	protected void hideDialogCall() {
		if (layoutCallDialog.getVisibility() == View.VISIBLE) {
			AnimationUtil.animationShowSifbHideSotbSpecial(activity, false,
					layoutCallDialog);
			AnimationUtil
					.animationShowHideAlphaSpecial(activity, false, ivMask);
		}
	}

	protected void filterViewBySaleType(int roomSaleType) {
		boolean hasRoom = false;
		int count = layoutContentReserve.getChildCount();
		for (int i = 0; i < count; i++) {
			View vRoom = layoutContentReserve.getChildAt(i);
			Integer saleType = (Integer) vRoom.getTag();
			if (saleType == roomSaleType) {
				vRoom.setVisibility(View.VISIBLE);
				hasRoom = true;
			} else {
				vRoom.setVisibility(View.GONE);
			}
		}
	}

	// protected void putCollect() {
	// pb.setVisibility(View.VISIBLE);
	// new Thread() {
	// Message msg = new Message();
	//
	// public void run() {
	// try {
	//
	// boolean collect;
	// if (!isCollection) {
	// collect = true;
	// } else {
	// collect = false;
	// }
	//
	// RspMsg r = null;
	// if (ac.isLogin()) {
	// int saleType = Room.SALE_TYPE_ZDF;
	// if (!item.isHour) {
	// saleType = Room.SALE_TYPE_WYF;
	// }
	// r = ac.putCollect(hotelId, ac.user.id, collect,
	// saleType);
	// }
	//
	// msg.what = WHAT_PUT_COLLECT;
	// msg.obj = r;
	// } catch (AppException e) {
	// e.printStackTrace();
	// msg.what = -1;
	// msg.obj = e;
	// }
	// myHandler.sendMessage(msg);
	// };
	// }.start();
	// }

	private void initHotelData() {
		pb.setVisibility(View.VISIBLE);
		new Thread() {
			Message msg = new Message();

			public void run() {
				try {
					Item r;

					r = ac.getHotel(hotelId);

					msg.what = WHAT_INIT_HOTEL_VIEW;
					msg.obj = r;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			};
		}.start();
	}

	// private void animation1(final boolean isChecked, View v) {
	// int i = 0;
	// if (isChecked) {
	// i = R.anim.toshow;
	// v.setVisibility(View.VISIBLE);
	// } else {
	// i = R.anim.todispear;
	// v.setVisibility(View.GONE);
	// }
	// AnimationSet ta = (AnimationSet) AnimationUtils.loadAnimation(activity,
	// i);
	// ta.setDuration(500);
	// v.startAnimation(ta);
	//
	// }
	//
	// private Animation animation(final boolean isChecked, View v) {
	// int i = 0;
	// if (isChecked) {
	// i = R.anim.slide_in_from_top;
	// v.setVisibility(View.VISIBLE);
	// } else {
	// i = R.anim.slide_out_to_top;
	// v.setVisibility(View.GONE);
	// }
	// Animation ta = AnimationUtils.loadAnimation(activity, i);
	// ta.setDuration(500);
	// v.startAnimation(ta);
	// return ta;
	// }

	public void hideHotelMap() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_from_left,
				R.anim.slide_out_to_left, R.anim.slide_in_from_right,
				R.anim.slide_out_to_right);
		// ft.setCustomAnimations(R.anim.slide_in_obj_from_left,
		// R.anim.slide_out_obj_to_left, R.anim.slide_in_obj_from_right,
		// R.anim.slide_out_obj_to_right);

		ft.hide(fragmentHotelMap);

		ft.commit();
	}

	public void onEventMainThread(Event e) {
		Log.d(TAG, "======onEventMainThread======"
				+ e.getClass().getSimpleName());

		if (e instanceof HideHotelMapEvent) {
			hideHotelMap();
		}
		if (e instanceof PaySuccessEvent) {
			hideReserve();
		}
		// if (e instanceof PayArriveCommitedEvent) {
		// hideReserve();
		// }
		if (e instanceof HideReserveEvent) {
			HideReserveEvent event = (HideReserveEvent) e;
			if (event.hide) {

				if (layoutCallDialog.getVisibility() == View.VISIBLE) {
					hideDialogCall();
				} else {
					// showDialogCancel();
					hideReserve();
				}

				// hideReserve();
			}
		}
		if (e instanceof LoginEvent) {
			LoginEvent event = (LoginEvent) e;
			if (event.isSpecial) {
				if (event.success) {
					Log.d(TAG, "======submitOrder  register special======");
					submitOrder();
				}
				// else {
				// // TODO 如果本地保存有之前登录的账号与此此账号相同，是否自动登录
				// AppContext.toastShow("此入住人手机号已注册过，请进行登录");
				//
				// Bundle arg = new Bundle();
				// arg.putString(Keys.PHONE, tvReserveLiveManPhone.getText()
				// .toString());
				//
				// Fragment fragment = Fragment.instantiate(activity,
				// LoginFragment.class.getName());
				//
				// activity.fragmentUtil.addToBackStack(R.id.root_hotel,
				// fragment, Tags.LOGIN_SPECIAL,
				// R.anim.slide_in_from_right,
				// R.anim.slide_out_to_right,
				// R.anim.slide_in_from_right,
				// R.anim.slide_out_to_right);
				// }
			}
		}
	}

	private MyHandler myHandler = new MyHandler(this);
	private View ivReserveLiveManChoose;

	protected Room curtClickRoom;
	private RadioButton rbtnPayWayArrive;
	private RadioButton rbtnPayWayOnline;
	private View layoutPayWayOnline;
	private View layoutPayWayArrive;
	private ImageView ivPayWayArrow;
	private View layoutPayWayAnim;

	private TextView tvPriceOrder;

	private TextView tvPricePay;

	private TextView tvMoneyDiscount;

	private TextView tvPriceSubmit;

	private View layoutPayWayName;

	private TextView tvPayMoneyPre;

	private View layoutMoneyDiscount;

	static class MyHandler extends Handler {
		// 注意下面的“<T>”类是MyHandler类所在的外部类
		private WeakReference<HotelDetailFragment> mWr;

		public MyHandler(HotelDetailFragment frag) {
			mWr = new WeakReference<HotelDetailFragment>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			HotelDetailFragment frag = mWr.get();

			// 解决bug:Fragment被销毁了导致一些空指针异常、或者fragment not attached to
			// activity异常；原因是Fragment被销毁或者deatach后，线程才返回结果；最好的解决办法应该是，在Fragment被销毁时结束线程请求
			if (frag == null || !frag.isAdded()) {
				return;
			}

			switch (msg.what) {
			case WHAT_INIT_ROOM_VIEW:
				frag.pb.setVisibility(View.GONE);
				RoomPage q = (RoomPage) msg.obj;

				if (q.rspMsg.OK()) {
					if (q.list.size() == 0) {

						if (Room.isSellZdf()) {
							frag.layoutNoDataTip.setVisibility(View.GONE);

							frag.listRoom = q.list;
							frag.initRoomView(q.list);
						} else {
							frag.ivNoData
									.setImageResource(R.drawable.img_no_data_tip_fail);
							frag.btnReload.setVisibility(View.VISIBLE);
							frag.layoutNoDataTip.setVisibility(View.VISIBLE);
						}
					} else {
						frag.layoutNoDataTip.setVisibility(View.GONE);

						if (frag.tvSaleTimeTip.getVisibility() == View.GONE) {
							frag.tvSaleTimeTip.setVisibility(View.VISIBLE);
							if (Room.isSellZdf()) {
								frag.tvSaleTimeTip.setText("钟点房");
							} else {
								frag.tvSaleTimeTip.setText("午夜房");
							}
						}

						frag.listRoom = q.list;
						frag.initRoomView(q.list);
					}
				} else {
					AppContext.toastShowException(q.rspMsg.message);
				}

				break;

			case -WHAT_INIT_ROOM_VIEW:
				frag.pb.setVisibility(View.GONE);
				((AppException) msg.obj).makeToast(frag.ac);

				// 点击重试
				frag.ivNoData.setImageResource(R.drawable.img_no_data_tip_fail);
				frag.btnReload.setVisibility(View.VISIBLE);
				frag.layoutNoDataTip.setVisibility(View.VISIBLE);

				break;

			case WHAT_INIT_HOTEL_VIEW:
				Item item = (Item) msg.obj;
				if (item.rspMsg.OK()) {
					// 详情、图片ViewPager
					frag.setViewPager(item.hotelsImgs);
					frag.tvPicCount.setText("共" + item.hotelsImgs.length + "张");
					frag.item.discountOnline = item.discountOnline;

					// 酒店所提供服务
					if (item.facilitysIds != null) {
						if (item.facilitysIds.contains("136")) {
							frag.ivServiceWifi
									.setImageResource(R.drawable.ic_service_wifi_1_true);
						}
						if (item.facilitysIds.contains("137")) {
							frag.ivServicePark
									.setImageResource(R.drawable.ic_service_park_1_true);
						}
					}

					if (Room.isSellZdf()) {

						frag.tvSaleTimeTip.setVisibility(View.VISIBLE);

						String tip = "钟点房";

						// if (!StringUtils.isEmpty(item.getHourStartTime())
						// && !StringUtils.isEmpty(item.getHourEndTime())) {
						// tip = "钟点房（ " + item.getHourStartTime()
						// + "前接受预订，最晚退房时间" + item.getHourEndTime()
						// + "）";
						// } else {
						// tip = "钟点房";
						// }

						frag.tvSaleTimeTip.setText(tip);
					} else {
						frag.tvSaleTimeTip.setVisibility(View.VISIBLE);
						frag.tvSaleTimeTip.setText("午夜房");
					}
				} else {
					AppContext.toastShowException(item.rspMsg.message);
				}
				break;

			// case WHAT_PUT_COLLECT:
			// frag.pb.setVisibility(View.GONE);
			// RspMsg rspMsg = (RspMsg) msg.obj;
			// if (rspMsg.OK()) {
			// if (!frag.isCollection) {
			// frag.btnRight.setImageResource(R.drawable.ic_like_red);
			// AppContext.toastShow("已添加到“我的收藏”");
			// frag.isCollection = true;
			// } else {
			// frag.btnRight.setImageResource(R.drawable.ic_like);
			// AppContext.toastShow("已取消");
			// frag.isCollection = false;
			// }
			//
			// CollectionChangeEvent event = new CollectionChangeEvent();
			// event.change = true;
			// EventBus.getDefault().post(event);
			//
			// } else {
			// AppContext.toastShowException(rspMsg.message);
			// }
			// break;

			case WHAT_SUBMIT_ORDER:
				frag.pb.setVisibility(View.GONE);
				Item itemOrder = (Item) msg.obj;
				itemOrder.hotelsLatitude = frag.item.hotelsLatitude;
				itemOrder.hotelsLongitude = frag.item.hotelsLongitude;
				itemOrder.hotelsTel = frag.item.hotelsTel;
				itemOrder.hotelsAddr = frag.item.hotelsAddr;

				if (itemOrder.rspMsg.OK()) {
					frag.hideReserve();

					Intent intent = new Intent(frag.activity,
							PayOnlineActivity.class);
					intent.putExtra(Keys.ORDER, itemOrder);
					intent.putExtra(Keys.ORDER_IS_DETAIL, true);
					frag.activity.startActivity(intent);
					frag.activity.overridePendingTransition(
							R.anim.slide_in_from_right,
							R.anim.slide_out_to_left);

					OrderChangeEvent event = new OrderChangeEvent();
					event.change = true;
					EventBus.getDefault().post(event);
				} else if (itemOrder.rspMsg.code
						.equals(RspMsg.CODE_SUBMIT_ORDER_NO_ROOM)) {
					AppContext.toastShow("抱歉，暂时无法预订，请拨打客服电话直接预订");
				} else if (itemOrder.rspMsg.code
						.equals(RspMsg.CODE_SUBMIT_ORDER_ONLY_FOR_PAY_ARRIVE)) {
					AppContext.toastShow(itemOrder.rspMsg.message);
				} else if (itemOrder.rspMsg.code
						.equals(RspMsg.CODE_SUBMIT_ORDER_FREQUENT)) {
					AppContext.toastShow(itemOrder.rspMsg.message);
				} else {
					AppContext.toastShow(itemOrder.rspMsg.message);
					// AppContext.toastShow("抱歉，暂时无法预订，请拨打客服电话直接预订");
				}
				break;

			case -WHAT_SUBMIT_ORDER:
				frag.pb.setVisibility(View.GONE);
				AppContext.toastShow("请重试");
				((AppException) msg.obj).makeToast(frag.ac);
				break;

			case WHAT_VP_AUTO_TURN:
				// 自动翻页
				if (frag.mViewPager != null) {
					// frag.mViewPager.setCurrentItem(msg.arg1);
					frag.mViewPager.setSelection(msg.arg1);
				}
				break;

			case WHAT_CHECK_USER_EXIST:
				frag.pb.setVisibility(View.GONE);
				RspMsg rsp = (RspMsg) msg.obj;

				if (rsp.OK()
						|| RspMsg.CODE_CHECK_USER_NOT_EXIST.equals(rsp.code)) {
					// 用户已注册过，引导用户进行登录
					// AppContext.toastShow("您已经是会员啦，请登录");

					Bundle arg = new Bundle();
					arg.putBoolean(Keys.LOGIN_RESERVE, true);
					arg.putString(Keys.PHONE, frag.tvReserveLiveManPhone
							.getText().toString());
					arg.putString(Keys.NAME, frag.item.hotelsName);
					arg.putString(Keys.ADDR, frag.item.hotelsAddr);
					arg.putString(Keys.LOGO, frag.item.logopath);
					arg.putString(Keys.PRICE, frag.curtClickRoom.roomPrice);
					arg.putString(Keys.HOURS, frag.curtClickRoom.hourDuration);
					arg.putString(Keys.ROOM_TYPE,
							frag.curtClickRoom.roomTypeName);
					arg.putInt(Keys.SALE_TYPE, frag.curtClickRoom.sellType);

					frag.showSpecialLogin(arg);

				} else if (RspMsg.CODE_CHECK_USER_NOT_EXIST.equals(rsp.code)) {
					// 用户未注册过，引导用户进行无帐号预订

					// Bundle arg = new Bundle();
					// arg.putString(Keys.PHONE, frag.tvReserveLiveManPhone
					// .getText().toString());
					// arg.putBoolean(Keys.RESERVE_REGISTER, true);
					//
					// frag.showSpecialLogin(arg);

				} else {
					AppContext.toastShowException(rsp.message);
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

	private void initRoomData() {
		pb.setVisibility(View.VISIBLE);
		new Thread() {
			Message msg = new Message();

			public void run() {
				try {
					RoomPage r = ac.getRoomPage(hotelId, item.brandId);

					if (r.list != null) {
						r.list = sortBySaleType(r.list);
					}

					msg.what = WHAT_INIT_ROOM_VIEW;
					msg.obj = r;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_INIT_ROOM_VIEW;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			};
		}.start();
	}

	public void showSpecialLogin(Bundle arg) {
		Fragment fragment = Fragment.instantiate(activity,
				LoginFragment.class.getName(), arg);

		activity.fragmentUtil.addToBackStack(R.id.root_hotel, fragment,
				Tags.RESERVE_LOGIN, R.anim.slide_in_from_right,
				R.anim.slide_out_to_right, R.anim.slide_in_from_right,
				R.anim.slide_out_to_right);
	}

	protected List<Room> sortBySaleType(List<Room> list) {
		List<Room> listSort = new ArrayList<Room>();

		int size = list.size();

		ArrayList<Room> listZdf = new ArrayList<Room>();
		ArrayList<Room> listWyf = new ArrayList<Room>();
		ArrayList<Room> listLsf = new ArrayList<Room>();

		for (int i = 0; i < size; i++) {
			Room room = list.get(i);

			switch (room.sellType) {
			case Room.SALE_TYPE_ZDF:
				listZdf.add(room);
				break;
			case Room.SALE_TYPE_WYF:
				listWyf.add(room);
				break;
			case Room.SALE_TYPE_LSF:
				listLsf.add(room);
				break;

			default:
				AppContext.toastShow("未知房间销售类型 " + room.sellType);
				break;
			}
		}

		if (Room.isSellWyf() || Room.isSellLsf()) {
			listSort.addAll(listWyf);
			listSort.addAll(listZdf);
			listSort.addAll(listLsf);

		} else if (Room.isSellLsf()) {
			listSort.addAll(listLsf);
			listSort.addAll(listWyf);
			listSort.addAll(listZdf);
		} else if (Room.isSellZdf()) {
			listSort.addAll(listZdf);
			listSort.addAll(listLsf);
			listSort.addAll(listWyf);
		}

		return listSort;
	}

	private void initRoomView(List<Room> list) {

		viewSwitcher = (ViewSwitcher) view.findViewById(R.id.viewSwitcher1);

		// 入住信息
		View layoutReverseInfo1 = view.findViewById(R.id.layout_reserve_info1);
		tvReserveLiveMan = (EditText) layoutReverseInfo1
				.findViewById(R.id.tvLiveMan);
		ivReserveLiveManChoose = layoutReverseInfo1
				.findViewById(R.id.ivContact);
		tvReserveLiveManPhone = (EditText) layoutReverseInfo1
				.findViewById(R.id.LiveManPhone);

		UIHelper.limitChineseEditTextInput(tvReserveLiveMan);
		UIHelper.limitPhoneEditTextInput(tvReserveLiveManPhone);
		ivReserveLiveManChoose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent,
						Constants.REQUEST_CODE_PICK_LIVE_MAN_NAME_PHONE);
			}
		});

		// 提交按钮
		View layoutBottomSubmit = view.findViewById(R.id.layoutBottomSubmit);
		btnSubmit = layoutBottomSubmit.findViewById(R.id.btnSubmit);

//		if (SmartBarUtils.hasSmartBar()) {
//			UIHelper.setMeizuBtn(getResources(), layoutBottomSubmit,
//					(Button) btnSubmit);
//		}

		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				UIHelper.hideSoftInput(activity, v);
				submitOrder();
			}
		});

		// 添加房间
		addRoomViews(list);
	}

	private void addRoomViews(List<Room> list) {
		if (list.size() == 0 && Room.isSellZdf()) {

			addStubZdfFullRoom();

			return;
		}

		int lastSellType = 0;

		for (int i = 0; i < list.size(); i++) {
			final Room r = list.get(i);

			// 销售类型分割线
			// if (i == 0) {
			// View layoutBreakup = null;
			// if (r.sellType == Room.SALE_TYPE_ZDF) {
			// layoutBreakup = inflater.inflate(
			// R.layout.hotel_room_item_breakup_zdf,
			// layoutContentReserve, false);
			// } else if (r.sellType == Room.SALE_TYPE_WYF
			// || r.sellType == Room.SALE_TYPE_LSF) {
			// layoutBreakup = inflater.inflate(
			// R.layout.hotel_room_item_breakup_wyf,
			// layoutContentReserve, false);
			// } else if (r.sellType == Room.SALE_TYPE_LSF) {
			// layoutBreakup = inflater.inflate(
			// R.layout.hotel_room_item_breakup_lsf,
			// layoutContentReserve, false);
			// }
			// if (layoutBreakup != null) {
			// layoutContentReserve.addView(layoutBreakup);
			// }
			// } else if (r.sellType != lastSellType) {
			// View layoutBreakup = null;
			// if (r.sellType == Room.SALE_TYPE_ZDF) {
			// layoutBreakup = inflater.inflate(
			// R.layout.hotel_room_item_breakup_zdf,
			// layoutContentReserve, false);
			// } else if (r.sellType == Room.SALE_TYPE_WYF
			// || r.sellType == Room.SALE_TYPE_LSF) {
			// layoutBreakup = inflater.inflate(
			// R.layout.hotel_room_item_breakup_wyf,
			// layoutContentReserve, false);
			// } else if (r.sellType == Room.SALE_TYPE_LSF) {
			// layoutBreakup = inflater.inflate(
			// R.layout.hotel_room_item_breakup_lsf,
			// layoutContentReserve, false);
			// }
			// if (layoutBreakup != null) {
			// layoutContentReserve.addView(layoutBreakup);
			// }
			// }
			//
			// lastSellType = r.sellType;

			View layoutItem = null;
			if (r.sellType == Room.SALE_TYPE_ZDF) {
				if (r.roomCount == 0) {
					layoutItem = inflater.inflate(
							R.layout.hotel_room_item_zdf_0,
							layoutContentReserve, false);
				} else {
					layoutItem = inflater.inflate(R.layout.hotel_room_item_zdf,
							layoutContentReserve, false);
				}
			} else if (r.sellType == Room.SALE_TYPE_WYF
					|| r.sellType == Room.SALE_TYPE_LSF) {
				if (r.roomCount == 0) {
					layoutItem = inflater.inflate(
							R.layout.hotel_room_item_zdf_0,
							layoutContentReserve, false);
				} else {
					layoutItem = inflater.inflate(R.layout.hotel_room_item_zdf,
							layoutContentReserve, false);
				}

			}

			TextView tvHours = (TextView) layoutItem.findViewById(R.id.tvHours);
			TextView tvRoomType = (TextView) layoutItem
					.findViewById(R.id.tvRoomType);
			TextView tvPrice = (TextView) layoutItem.findViewById(R.id.tvPrice);
			View btnShowReserve = layoutItem.findViewById(R.id.btnShowReserve);
			ImageView ivPayWayOnline = (ImageView) layoutItem
					.findViewById(R.id.ivPayWayOnline);
			ImageView ivPayWayArrive = (ImageView) layoutItem
					.findViewById(R.id.ivPayWayArrive);

			tvPrice.setText(r.roomPrice);

			// 房间时段、房型
			String roomTypeName = "";
			if (r.sellType == Room.SALE_TYPE_ZDF) {

				tvHours.setText(r.hourDuration);

				roomTypeName = "小时";

				// 当前需求钟点房不显示房型
				// if (!StringUtils.isEmpty(r.roomTypeName)) {
				// roomTypeName += r.roomTypeName;
				// }
			} else if (r.sellType == Room.SALE_TYPE_WYF
					|| r.sellType == Room.SALE_TYPE_LSF) {

				roomTypeName = r.roomTypeName;
			}

			tvRoomType.setText(roomTypeName);

			// 支持的支付方式
			if (Room.SUPPORT_PAY_WAY_ONLINE == r.isPrepaid) {
				// 只支持在线
				ivPayWayOnline.setVisibility(View.VISIBLE);
				ivPayWayArrive.setVisibility(View.GONE);

			} else if (Room.SUPPORT_PAY_WAY_ARRIVE == r.isPrepaid) {
				// 只支持到店
				ivPayWayOnline.setVisibility(View.GONE);
				ivPayWayArrive.setVisibility(View.VISIBLE);
			} else if (Room.SUPPORT_PAY_WAY_ALL == r.isPrepaid) {
				// 支持在线、到店
				ivPayWayOnline.setVisibility(View.VISIBLE);
				ivPayWayArrive.setVisibility(View.VISIBLE);
			} else {
				// 默认 只支持到店
				ivPayWayOnline.setVisibility(View.GONE);
				ivPayWayArrive.setVisibility(View.VISIBLE);
			}

			// 订单
			btnShowReserve.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (UIHelper.isFastDoubleClick(v)) {
						return;
					}

					curtClickRoom = r;

					// sv.smoothScrollTo(0, 0);

					if (Room.isSellZdf()) {

						if (r.business_type == null) {

							// checkIsTuan(r.saleId, Room.SALE_TYPE_ZDF);
							showReserve(r);
						} else {
							showReserve(r);
						}

					} else {
						if (r.roomCount == 0) {
							// 午夜房，房间为0，则只能拨打电话预订
							showDialogCall();
						} else {

							showReserve(r);
						}
					}

				}
			});

			// if (r.hotelsNum == 0) {
			// btnReserve.setBackgroundResource(R.drawable.btn_red);
			// btnReserve.setText("满");
			// // layoutItemProvide.setVisibility(View.VISIBLE);
			// }

			layoutItem.setTag(r.sellType);

			layoutContentReserve.addView(layoutItem);
		}
	}

	// /**
	// * 检查此房间是否属于团购，团购房间只支持在线支付
	// *
	// * @param saleId
	// * @param saleType
	// */
	// protected void checkIsTuan(final String saleId, final int saleType) {
	//
	// pb.setVisibility(View.VISIBLE);
	// new Thread() {
	// Message msg = new Message();
	//
	// public void run() {
	// try {
	//
	// RspMsg r = ac.checkeIsTuan(saleId, saleType);
	//
	// msg.what = WHAT_CHECKE_IS_TUAN;
	// msg.obj = r;
	// } catch (AppException e) {
	// e.printStackTrace();
	// msg.what = WHAT_EXCEPTION_CHECKE_IS_TUAN;
	// msg.obj = e;
	// }
	// myHandler.sendMessage(msg);
	// };
	// }.start();
	//
	// }

	private void addStubZdfFullRoom() {
		// View layoutBreakup = inflater.inflate(
		// R.layout.hotel_room_item_breakup_zdf, layoutContentReserve,
		// false);
		//
		// layoutContentReserve.addView(layoutBreakup);

		View layoutItem = inflater.inflate(R.layout.hotel_room_item_zdf_null,
				layoutContentReserve, false);

		layoutContentReserve.addView(layoutItem);

		TextView tvRoomType = (TextView) layoutItem
				.findViewById(R.id.tvRoomType);
		TextView tvPrice = (TextView) layoutItem.findViewById(R.id.tvPrice);

		if (!StringUtils.isEmpty(item.hourroomPrice)) {
			tvPrice.setText(item.hourroomPrice);
		} else {
			tvPrice.setText(Item.DEFAULT_ZDF_PRICE);
		}

		String roomTypeName;
		if (!StringUtils.isEmpty(item.hours)) {
			roomTypeName = item.hours + "小时" + "钟点房";
		} else {
			roomTypeName = Item.DEFAULT_ZDF_HOUR + "小时" + "钟点房";
		}

		tvRoomType.setText(roomTypeName);

		layoutItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				// UIHelper.call(activity, Constants.TEL_JUJIA);

				// if (!StringUtils.isEmpty(item.hotelsPhoneno)
				// && !item.hotelsPhoneno.trim().equals("0")) {
				//
				// UIHelper.call(activity, item.hotelsPhoneno);
				// } else {
				// UIHelper.call(activity, Constants.TEL_JUJIA);
				// }

				showDialogCall();

			}
		});
	}

	protected void showDialogCall() {
		if (layoutCallDialog.getVisibility() == View.VISIBLE) {
			return;
		}

		AnimationUtil.animationShowSifbHideSotbSpecial(activity, true,
				layoutCallDialog);
		AnimationUtil.animationShowHideAlphaSpecial(activity, true, ivMask);
	}

	private void setViewPager(String[] hotelsImgs) {
		pagerAdapter = new SamplePagerAdapter(hotelsImgs);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setSpacing(0);
		mViewPager.setUnselectedAlpha(1.0f);
		mPageSize = pagerAdapter.getCount();
		mViewPager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}

		});
	}

	protected void showDialogCancel() {
		UIHelper.hideSoftInput(activity, pb);

		// tvTip.setVisibility(View.GONE);
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// ivTopBarBg.setAlpha(1f);
		// }

		if (dialogCancel.getVisibility() == View.INVISIBLE) {
			String cancelDialogTip[] = new String[] { "想要换一家看看吗？",
					"马上就可以入住啦，怎么要离开呢？", "您刚填完订单，真的要返回么？", "亲爱的别走~就要预订成功了呢~" };
			Random rand = new Random();
			int nextInt = rand.nextInt(4);

			tvCancelDialogTip.setText(cancelDialogTip[nextInt]);

			// dialogCancel.setVisibility(View.VISIBLE);
			AnimationUtil.animationShowSifbHideSotbSpecial(activity, true,
					dialogCancel);
			AnimationUtil.animationShowHideAlphaSpecial(activity, true, ivMask);
		} else if (dialogCancel.getVisibility() == View.VISIBLE) {
			AnimationUtil.animationShowSifbHideSotbSpecial(activity, false,
					dialogCancel);
			AnimationUtil
					.animationShowHideAlphaSpecial(activity, false, ivMask);
		}
	}

	protected void submitOrder() {
		// String dateOnly = DateUtil.getDateOnly();
		// dateOnly = dateOnly.replaceAll("-", "");
		// Calendar calendar = Calendar.getInstance();
		// calendar.add(Calendar.MINUTE, 30);
		// String hour = DateUtil.getHourStr(calendar);
		// String minute = DateUtil.getMinuteStr(calendar);

		// final String dateArrive = dateOnly + hour + minute;

		final int payWay;
		// if (true) {
		if (isPayOnline) {
			payWay = Item.PAY_WAY_ONLINE;
		} else {
			payWay = Item.PAY_WAY_ARRIVE;
		}

		final String liveMan = tvReserveLiveMan.getText().toString();
		final String liveManPhone = tvReserveLiveManPhone.getText().toString();

		// 当前业务：联系人取入住人
		final String contact = liveMan;
		final String contactPhone = liveManPhone;

		if (StringUtils.isEmpty(liveMan)) {
			AppContext.toastShow("亲，忘填入住人啦~");
			return;
		}

		if (liveMan.length() > UIHelper.mMaxLenth) {
			AppContext.toastShow("亲，入住人最多只能是5个汉字");
			return;
		}

		if (!StringUtils.isOnlyChinese(liveMan)) {
			AppContext.toastShow("亲，入住人只能是汉字");
			return;
		}

		if (!RegUtil.isMobileNO(liveManPhone)) {
			AppContext.toastShow("手机号好像不对耶~");
			return;
		}

		saveSubmitInfo2Loc(liveMan, liveManPhone);

		if (!ac.isLogin()) {
			String username = liveManPhone;
			String realname = liveMan;

			Bundle arg = new Bundle();
			arg.putBoolean(Keys.LOGIN_RESERVE, true);
			arg.putString(Keys.PHONE, liveManPhone);
			arg.putString(Keys.NAME, item.hotelsName);
			arg.putString(Keys.ADDR, item.hotelsAddr);
			arg.putString(Keys.LOGO, item.logopath);
			arg.putString(Keys.PRICE, curtClickRoom.roomPrice);
			arg.putString(Keys.HOURS, curtClickRoom.hourDuration);
			arg.putString(Keys.ROOM_TYPE, curtClickRoom.roomTypeName);
			arg.putInt(Keys.SALE_TYPE, curtClickRoom.sellType);

			showSpecialLogin(arg);

			// 未登录，先检查填写的联系人手机号是否注册过，以引导用户进行登录或无帐号预订
			// checkUserExist(username);

			return;
		} else {

			pb.setVisibility(View.VISIBLE);
			new Thread() {
				Message msg = new Message();

				public void run() {
					try {
						Item r = null;

						String roomCount = "1";
						if (orderRoom.roomCount == 0) {
							roomCount = "0";
						}

						String dateArrive = null;
						r = ac.submitOrder(hotelId, ac.getLoginUserId(),
								orderRoom.saleId, orderRoom.roomType,
								orderRoom.sellType, dateArrive, liveMan,
								liveManPhone, contact, contactPhone, roomCount,
								payWay, orderRoom.hotelCode,
								orderRoom.roomPlanId, orderRoom.roomPrice,
								item.brandId, item.hotelsName);

						msg.what = WHAT_SUBMIT_ORDER;
						msg.obj = r;
					} catch (AppException e) {
						e.printStackTrace();
						msg.what = -WHAT_SUBMIT_ORDER;
						msg.obj = e;
					}
					myHandler.sendMessage(msg);
				};
			}.start();
		}

	}

	/**
	 * 检查填写的联系人手机号是否注册过
	 * 
	 * @param username
	 */
	private void checkUserExist(final String username) {
		pb.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			public void run() {

				Message msg = new Message();

				try {
					RspMsg userInfo = ac.checkUserExist(username);

					msg.what = WHAT_CHECK_USER_EXIST;
					msg.obj = userInfo;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = WHAT_EXCEPTION;
					msg.obj = e;
				}

				myHandler.sendMessage(msg);
			}
		}).start();
	}

	private void saveSubmitInfo2Loc(String liveMan, String liveManPhone) {
		AppConfig config = AppConfig.getAppConfig(ac);
		Properties props = config.get();
		props.setProperty(Keys.LAST_FILE_LIVE_MAN, liveMan);
		props.setProperty(Keys.LAST_FILE_LIVE_MAN_PHONE, liveManPhone);
		config.setProps(props);
	}

	protected void hideReserve() {
		ac.isReserve = false;

		final String liveMan = tvReserveLiveMan.getText().toString();
		final String liveManPhone = tvReserveLiveManPhone.getText().toString();

		if (!StringUtils.isEmpty(liveMan) && !StringUtils.isEmpty(liveManPhone)) {
			// taikingdata

			final int payWay;
			// if (true) {
			if (isPayOnline) {
				payWay = Item.PAY_WAY_ONLINE;
			} else {
				payWay = Item.PAY_WAY_ARRIVE;
			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("下单时间", StringUtils.getNow());
			if (ac.isLogin()) {
				map.put("用户id", ac.getLoginUserId());
			} else {
				map.put("用户id", "未登录");
			}
			map.put("酒店名称", item.hotelsName);
			map.put("支付方式", payWay);
			map.put("品牌id", item.brandId);

			TCAgent.onEvent(ac, "预填订单统计", "预填订单统计", map);
		}

		// layoutTopbar.setBackgroundResource(R.color.transparent);

		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// ivTopBarBg.setAlpha(0f);
		// }
		// ivTopBarBg.setVisibility(View.GONE);

		viewSwitcher.setInAnimation(ac, R.anim.slide_in_from_left);
		viewSwitcher.setOutAnimation(ac, R.anim.slide_out_to_right);
		viewSwitcher.showPrevious();

		tvTopTitle.setText("酒店信息");
		// tvTip.setVisibility(View.GONE);

		if (SmartBarUtils.hasSmartBar()) {
			activity.supportInvalidateOptionsMenu();
		}
		// layoutPay.setVisibility(View.GONE);
		// btnRight.setVisibility(View.INVISIBLE);
		// btnRight.setImageResource(R.drawable.ic_path_nav);
		// btnRight.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (UIHelper.isFastDoubleClick(v)) {
		// return;
		// }
		// UIHelper.showPathNav(activity, item);
		// }
		// });
	}

	/**
	 * 预订页面
	 * 
	 * @param r
	 */
	protected void showReserve(final Room r) {
		ac.isReserve = true;

		orderRoom = r;

		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// ivTopBarBg.setAlpha(1f);
		// }
		// ivTopBarBg.setVisibility(View.VISIBLE);
		tvTopTitle.setText("填写订单");

		// ===预订价格信息
		tvPriceOrder = (TextView) view.findViewById(R.id.tvPriceOrder);
		tvPricePay = (TextView) view.findViewById(R.id.tvPricePay);
		final View layoutMoneyPayOnline = view
				.findViewById(R.id.layoutMoneyPayOnline);
		layoutMoneyDiscount = view.findViewById(R.id.layoutMoneyDiscount);
		tvMoneyDiscount = (TextView) view.findViewById(R.id.tvMoneyDiscount);
		tvPriceSubmit = (TextView) view.findViewById(R.id.tvPriceSubmit);
		tvPayMoneyPre = (TextView) view.findViewById(R.id.tvPayMoneyPre);

		final int priceDiscount = item.discountOnline;
		final int roomPrice = StringUtils.toInt(r.roomPrice);
		setDiscount(priceDiscount, roomPrice, false);

		// ===支付方式
		View layoutPayWay = view.findViewById(R.id.layoutPayWay);
		layoutPayWayArrive = layoutPayWay.findViewById(R.id.layoutPayWayArrive);
		layoutPayWayOnline = layoutPayWay.findViewById(R.id.layoutPayWayOnline);
		rbtnPayWayArrive = (RadioButton) layoutPayWay
				.findViewById(R.id.rbtnPayWayArrive);
		rbtnPayWayOnline = (RadioButton) layoutPayWay
				.findViewById(R.id.rbtnPayWayOnline);
		layoutPayWayAnim = layoutPayWay.findViewById(R.id.layoutPayWayAnim);
		layoutPayWayName = layoutPayWay.findViewById(R.id.layoutPayWayName);
		tvPayWay = (TextView) layoutPayWay.findViewById(R.id.tvPayWay);
		ivPayWayArrow = (ImageView) layoutPayWay
				.findViewById(R.id.ivPayWayArrow);

		layoutPayWayName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Room.SUPPORT_PAY_WAY_ALL != r.isPrepaid) {
					return;
				}

				if (layoutPayWayAnim.getVisibility() == View.VISIBLE) {
					layoutPayWayAnim.setVisibility(View.GONE);
					ivPayWayArrow.setImageResource(R.drawable.ic_arrow_down);
				} else {
					layoutPayWayAnim.setVisibility(View.VISIBLE);
					ivPayWayArrow.setImageResource(R.drawable.ic_arrow_up);
				}
			}
		});

		rbtnPayWayArrive
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						if (isChecked) {
							isPayOnline = false;
							tvPayWay.setText("到店付");
							tvPayMoneyPre.setText("到店支付：");
							rbtnPayWayOnline.setChecked(false);

							layoutMoneyDiscount.setVisibility(View.GONE);
							layoutMoneyPayOnline.setVisibility(View.GONE);
							setDiscount(priceDiscount, roomPrice, false);

							if (orderRoom == null) {
								return;
							}

							if (orderRoom.sellType == Room.SALE_TYPE_ZDF) {

							} else if (orderRoom.sellType == Room.SALE_TYPE_WYF
									|| orderRoom.sellType == Room.SALE_TYPE_LSF) {

							}

							layoutPayWayName.performClick();
						}
					}
				});
		rbtnPayWayOnline
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						if (isChecked) {
							isPayOnline = true;
							tvPayWay.setText("预付");
							tvPayMoneyPre.setText("在线支付：");
							rbtnPayWayArrive.setChecked(false);

							if (orderRoom == null) {
								return;
							}

							if (orderRoom.sellType == Room.SALE_TYPE_ZDF) {

								layoutMoneyDiscount.setVisibility(View.VISIBLE);
								layoutMoneyPayOnline
										.setVisibility(View.VISIBLE);
								setDiscount(priceDiscount, roomPrice, true);

							} else if (orderRoom.sellType == Room.SALE_TYPE_WYF
									|| orderRoom.sellType == Room.SALE_TYPE_LSF) {

							}

							layoutPayWayName.performClick();
						}

					}
				});

		layoutPayWayArrive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rbtnPayWayArrive.performClick();
			}
		});
		layoutPayWayOnline.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rbtnPayWayOnline.performClick();
			}
		});

		rbtnPayWayArrive.setChecked(true);

		ivPayWayArrow.setImageResource(R.drawable.ic_arrow_down);
		layoutPayWayAnim.setVisibility(View.GONE);

		// if (isPayOnline) {
		// // 默认支付方式置为到店支付
		// sbPayType.performClick();
		// }

		// 支持的支付方式
		if (Room.SUPPORT_PAY_WAY_ONLINE == r.isPrepaid) {
			// 只支持在线
			rbtnPayWayOnline.setChecked(true);

			ivPayWayArrow.setVisibility(View.GONE);

		} else if (Room.SUPPORT_PAY_WAY_ARRIVE == r.isPrepaid) {
			// 只支持到店
			rbtnPayWayArrive.setChecked(true);

			ivPayWayArrow.setVisibility(View.GONE);

		} else if (Room.SUPPORT_PAY_WAY_ALL == r.isPrepaid) {

			// 都支持
			rbtnPayWayArrive.setChecked(true);

			ivPayWayArrow.setVisibility(View.VISIBLE);

		} else {
			// 默认 只支持到店
			rbtnPayWayArrive.setChecked(true);

			ivPayWayArrow.setVisibility(View.GONE);
		}

		// ===预订酒店房间信息
		View layoutReverseRoom = view.findViewById(R.id.layoutReserveRoom);
		TextView tvHotelNameOrder = (TextView) layoutReverseRoom
				.findViewById(R.id.tvHotelNameOrder);
		TextView tvAddrOrder = (TextView) layoutReverseRoom
				.findViewById(R.id.tvAddrOrder);
		ImageView ivHotelLogoOrder = (ImageView) layoutReverseRoom
				.findViewById(R.id.ivHotelLogoOrder);
		TextView tvHoursOrder = (TextView) layoutReverseRoom
				.findViewById(R.id.tvHoursOrder);
		TextView tvRoomTypeName = (TextView) layoutReverseRoom
				.findViewById(R.id.tvRoomTypeOrder);
		TextView tvRoomPrice = (TextView) layoutReverseRoom
				.findViewById(R.id.textView5);

		tvHotelNameOrder.setText(item.hotelsName);
		tvAddrOrder.setText(item.hotelsAddr);
		ac.mImageLoader.displayImage(URLs.URL_ZDF_API + item.logopath,
				ivHotelLogoOrder, ac.optionsLogo);

		tvRoomPrice.setText(r.roomPrice);
		if (r.sellType == Room.SALE_TYPE_ZDF) {

			tvHoursOrder.setText(r.hourDuration);

			String roomTypeName = "小时";

			if (!StringUtils.isEmpty(r.roomTypeName)) {
				roomTypeName += "  " + r.roomTypeName;
			}

			tvRoomTypeName.setText(roomTypeName);

		} else if (r.sellType == Room.SALE_TYPE_WYF
				|| r.sellType == Room.SALE_TYPE_LSF) {

			if (!StringUtils.isEmpty(r.roomTypeName)) {
				tvRoomTypeName.setText(r.roomTypeName);
			} else {
				tvRoomTypeName.setText("午夜房");
			}
		}

		View layoutBtn = view.findViewById(R.id.layoutBtn);
		View layoutTelHotelOrder = layoutBtn
				.findViewById(R.id.layoutTelHotelOrder);
		View layoutNavOrder = layoutBtn.findViewById(R.id.layoutNavOrder);
		final View btnPhone = layoutBtn.findViewById(R.id.btnTelHotelOrder);
		final View btnNav = layoutBtn.findViewById(R.id.btnNavOrder);

		btnPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// if (UIHelper.isFastDoubleClick(v)) {
				// return;
				// }
				//
				// if (StringUtils.isEmpty(itemOrder.hotelsPhoneno)) {
				// UIHelper.call(activity, Constants.TEL_JUJIA);
				// } else {
				// UIHelper.call(activity, itemOrder.hotelsPhoneno);
				// }

				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				if (layoutCallDialog.getVisibility() == View.VISIBLE) {
					return;
				}

				AnimationUtil.animationShowSifbHideSotbSpecial(activity, true,
						layoutCallDialog);
				AnimationUtil.animationShowHideAlphaSpecial(activity, true,
						ivMask);

			}
		});

		btnNav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				UIHelper.showPathNav(activity, item);
			}
		});

		layoutTelHotelOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnPhone.performClick();
			}
		});

		layoutNavOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnNav.performClick();
			}
		});

		// ===入住人信息
		if (ac.isLogin()) {
			String realname;
			String phone;

			if (UserInfo.USER_TYPE_NORMAL.equals(ac.user.userType)) {
				realname = ac.user.realname;
				phone = ac.user.username;
			} else {
				realname = ac.user.realname;
				phone = ac.user.userPhone;
			}

			tvReserveLiveMan.setText(realname);
			tvReserveLiveManPhone.setText(phone);
		}

		// 如果有上次提交订单所填写的用户信息，则使用上次提交信息
		AppConfig config = AppConfig.getAppConfig(ac);
		Properties prop = config.get();
		String lastFileLiveMan = prop.getProperty(Keys.LAST_FILE_LIVE_MAN);
		String lastFileLiveManPhone = prop
				.getProperty(Keys.LAST_FILE_LIVE_MAN_PHONE);
		if (lastFileLiveMan != null) {
			tvReserveLiveMan.setText(lastFileLiveMan);
		}
		if (lastFileLiveManPhone != null) {
			tvReserveLiveManPhone.setText(lastFileLiveManPhone);
		}

		// ===提示信息

		if (orderRoom.sellType == Room.SALE_TYPE_ZDF) {
			tvHourEndTip.setVisibility(View.VISIBLE);
			tvHourEndTip.setText("退房时间：满时退房，或根据酒店要求协商具体退房时间。");
			tvLiveTip
					.setText("入住须知：钟点房预订成功后，客房将为您保留30分钟，如不能在规定时间到店入住，请联系酒店或有间房客服4008-521-002。");

		} else if (orderRoom.sellType == Room.SALE_TYPE_WYF
				|| orderRoom.sellType == Room.SALE_TYPE_LSF) {
			tvHourEndTip.setVisibility(View.GONE);
			tvLiveTip.setText("入住须知：仅限预订当日酒店，次日中午12点前退房，预订成功后，需在约定时间入住，支持到店付款");
		}

		// ===切换动画
		Animation inAnimation = AnimationUtils.loadAnimation(activity,
				R.anim.slide_in_from_right);
		Animation outAnimation = AnimationUtils.loadAnimation(activity,
				R.anim.slide_out_to_left);

		viewSwitcher.setInAnimation(inAnimation);
		viewSwitcher.setOutAnimation(outAnimation);
		viewSwitcher.showNext();

		if (SmartBarUtils.hasSmartBar()) {
			activity.supportInvalidateOptionsMenu();
		}

	}

	/**
	 * 设置优惠金额
	 * 
	 * @param discountOnline
	 * @param roomPrice
	 * @param isDiscount
	 */
	private void setDiscount(int discountOnline, int roomPrice,
			boolean isDiscount) {

		int pricePay;
		if (isDiscount) {
			pricePay = roomPrice - discountOnline;
		} else {
			pricePay = roomPrice;
		}

		if (discountOnline <= 0) {
			layoutMoneyDiscount.setVisibility(View.GONE);
		} else {
			layoutMoneyDiscount.setVisibility(View.VISIBLE);
			tvMoneyDiscount.setText("￥-" + discountOnline);
		}

		tvPriceOrder.setText("￥" + pricePay);
		tvPricePay.setText("￥" + pricePay);
		tvPriceSubmit.setText("" + pricePay);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
		if (resultCode == Activity.RESULT_OK) {
			String[] nameAndPhone;
			switch (requestCode) {
			case Constants.REQUEST_CODE_PICK_CONTACT_NAME_PHONE:
				nameAndPhone = this.getContactNameAndPhone(data);

				break;
			case Constants.REQUEST_CODE_PICK_LIVE_MAN_NAME_PHONE:
				nameAndPhone = this.getContactNameAndPhone(data);

				tvReserveLiveMan.setText(nameAndPhone[0]);
				tvReserveLiveManPhone.setText(nameAndPhone[1]);

				break;
			}
		}
	}

	/**
	 * 获取联系人姓名和手机号
	 * 
	 * @param data
	 * @return name=nameAndPhone[0] , phone=nameAndPhone[1]
	 */
	private String[] getContactNameAndPhone(Intent data) {
		String phoneResult = "";
		String phoneMobile = "";
		String[] nameAndPhone = new String[2];

		Uri contactData = data.getData();
		Cursor cursor = activity.getContentResolver().query(contactData, null,
				null, null, null);
		cursor.moveToFirst();

		int phoneColumn = cursor
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

		int phoneNum = 0;
		try {
			phoneNum = cursor.getInt(phoneColumn);
		} catch (Exception e) {
			AppContext.toastShow("是否拒绝了联系人权限？");
		}

		// System.out.print(phoneNum);
		if (phoneNum > 0) {
			// 获得联系人的ID号
			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			String contactId = cursor.getString(idColumn);

			int displayNameColumn = cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			nameAndPhone[0] = cursor.getString(displayNameColumn);

			// 获得联系人的电话号码的cursor;
			Cursor phones = activity.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			// int phoneCount = phones.getCount();
			// allPhoneNum = new ArrayList<String>(phoneCount);

			// 遍历所有的电话号码
			// if (phones.moveToFirst()) {
			// for (; !phones.isAfterLast(); phones.moveToNext()) {
			// int index = phones
			// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			// int typeindex = phones
			// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
			// int phone_type = phones.getInt(typeindex);
			// String phoneNumber = phones.getString(index);
			//
			// if (phone_type ==
			// ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
			// phoneMobile = phoneNumber;
			// }
			// phoneResult = phoneNumber;
			//
			// // allPhoneNum.add(phoneNumber);
			// }
			// if (!phones.isClosed()) {
			// phones.close();
			// }
			// }

			// 遍历所有的电话号码
			while (phones.moveToNext()) {
				int index = phones
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
				int typeindex = phones
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
				int phone_type = phones.getInt(typeindex);
				String phoneNumber = phones.getString(index);

				if (phone_type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
					phoneMobile = phoneNumber;
				}
				phoneResult = phoneNumber;

				// allPhoneNum.add(phoneNumber);
			}
			phones.close();
		}
		if (!"".equals(phoneMobile)) {
			nameAndPhone[1] = phoneMobile;
		} else {
			nameAndPhone[1] = phoneResult;
		}

		return nameAndPhone;
	}

	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
			com.actionbarsherlock.view.MenuInflater inflater) {

		if (ac.isReserve) {

			UIHelper.makeEmptyMenu(inflater, menu);
		} else {
			inflater.inflate(R.menu.hotel_detail_menu, menu);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			btnLeft.performClick();
			return true;
		case R.id.menu_nav:
			 tvAddr.performClick();
			return true;
		case R.id.menu_phone:
			 tvTel.performClick();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private class SamplePagerAdapter extends BaseAdapter {

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.pic_hotel_img_loading)
				.showImageForEmptyUri(R.drawable.pic_hotel_img_loading)
				.resetViewBeforeLoading(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.showImageOnFail(R.drawable.pic_hotel_img_loading)
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		private String[] data = new String[] {};

		public SamplePagerAdapter(String[] data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			public ImageView ivPic;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.hotel_detail_vp_item,
						null);
				convertView.setTag(holder);

				holder.ivPic = (ImageView) convertView
						.findViewById(R.id.imageView1);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ac.mImageLoader.displayImage(data[position], holder.ivPic, options);
			// ac.mImageLoader.displayImage(URLs.URL_ZDF_API + data[position],
			// ivPic, ac.options);

			holder.ivPic.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (UIHelper.isFastDoubleClick(v)) {
						return;
					}

					Intent intent = new Intent(activity,
							PictureVpActivity.class);
					intent.putExtra(Keys.INDEX, position);
					intent.putExtra(Keys.IMGS, data);
					// intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

					startActivity(intent);
				}
			});

			return convertView;
		}

	}

	// Handler handler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	//
	// // 自动翻页
	// if (mViewPager != null) {
	// mViewPager.setCurrentItem(msg.arg1);
	// }
	// }
	// };
	private Timer mTimer;
	protected int curtPage = 0;
	protected int mPageSize;

	@Override
	public void onResume() {
		// 定时器，自动翻页
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				int num = loop();

				Message message = new Message();
				message.what = WHAT_VP_AUTO_TURN;
				message.arg1 = num;
				myHandler.sendMessage(message);
			}

			private int loop() {
				int num = curtPage;
				curtPage++;
				if (curtPage > mPageSize - 1) {
					curtPage = 0;
				}
				Log.d(TAG, "Timer loop " + num);
				return num;
			}
		}, 0, 3000);

		super.onResume();
	}

	@Override
	public void onPause() {
		// 注意：必须关闭定时器，并释放定时器资源，因为定时器是一个单独的线程，当activity关闭后，Timer线程仍在运行，
		// 而定时器中引用了activity中的一个handler，而handler中又引用了ViewPager，ViewPager中有图片的引用，这就导致了Bitmap资源无法被回收
		mTimer.cancel();
		mTimer = null;
		super.onPause();
	}

}

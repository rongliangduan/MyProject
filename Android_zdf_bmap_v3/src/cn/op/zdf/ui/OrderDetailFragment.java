package cn.op.zdf.ui;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.op.common.AppException;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.domain.RspMsg;
import cn.op.common.domain.URLs;
import cn.op.common.util.AnimationUtil;
import cn.op.common.util.Constants;
import cn.op.common.util.DateUtil;
import cn.op.common.util.Log;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.dao.MyDbHelper;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.Recharge;
import cn.op.zdf.domain.Room;
import cn.op.zdf.event.AddCouponEvent;
import cn.op.zdf.event.CouponSelectEvent;
import cn.op.zdf.event.Event;
import cn.op.zdf.event.OrderChangeEvent;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mapapi.model.LatLng;
import com.meizu.smartbar.SmartBarUtils;

import de.greenrobot.event.EventBus;

/**
 * 订单详情
 * 
 * @author lufei
 * 
 */
public class OrderDetailFragment extends SherlockFragment {
	private static final String TAG = Log.makeLogTag(OrderDetailFragment.class);
	private static final int WHAT_EXCEPTION = -1;
	protected static final int WHAT_ORDER_DETAIL = 2;
	protected static final int WHAT_ORDER_DONE = 3;
	protected static final int WHAT_BALANCE_PAY_SUCCESS = 4;
	protected static final int WHAT_QUERY_BALANCE_BEFORE_PAY = 7;
	protected static final int WHAT_CANCEL_ORDER = 8;

	private PayOnlineActivity activity;
	private AppContext ac;
	LayoutInflater inflater;
	ImageView phone;
	private ImageView dialogbg;
	private View pb;
	private String orderId;
	// private TextView tvDateSubmit;
	private TextView tvOrderNum;
	private TextView tvPriceOrder;
	private TextView tvPayWay;
	private TextView tvHotelName;
	private TextView tvAddr;
	private TextView tvRoomType;
	private TextView tvDateArrive;
	// private TextView tvRoomCount;
	private TextView tvLiveMan;
	// private TextView tvContact;
	// private TextView tvContactPhone;
	// private TextView tvDataArriveLast;
	private Item itemOrder;
	private TextView tvState;
	private TextView tvLiveManPhone;
	private ImageView ivOrderState;
	// private View layoutBottomTab;
	private View layouDateArrive;
	private TextView tvDateArrivePre;
	// private View btnPhone;
	private View ivMask;
	private View layoutCallDialog;
	// private View btnNav;
	private ImageView btnLeft;
	private View layouHourEndTip;
	private TextView tvHourEndTip;
	private ViewGroup layoutContenOrder;
	private View view;
	/**
	 * 仅使用余额、余额+优惠券支付（未使用支付平台）
	 */
	private boolean isUseBalanceEnought = false;
	/**
	 * 使用的余额
	 */
	private int useBalance;
	private TextView tvPayMoney;
	private TextView tvBalance;
	private View layoutPayPlatform;
	private View layoutBottomToPay;
	private View layoutTip;
	private TextView tvOrderState2;
	private View btnCancelOrder;
	private TextView tvUseCoupon;
	private ViewGroup layoutPayWayContainer;
	private TextView tvRoomPrice;
	private TextView tvLiveTip;
	private View layoutNoDataTip;
	private ImageView ivNoData;
	private View btnReload;
	private View layoutOrderDetail;
	private TextView tvPayMoneyPre;
	private Button btnCallHotel;
	private View btnPhone;
	private View btnNav;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		EventBus.getDefault().register(this);

		ac = AppContext.getAc();
		activity = (PayOnlineActivity) getActivity();
		this.inflater = inflater;

		view = inflater.inflate(R.layout.frag_order_detail, container, false);

		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		Log.d(TAG, "======onResume======");
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		Log.d(TAG, "======onDestroyView======");
		EventBus.getDefault().unregister(this);
		super.onDestroyView();
	}

	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
			com.actionbarsherlock.view.MenuInflater inflater) {

		inflater.inflate(R.menu.order_detail, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			btnLeft.performClick();
			return true;
		case R.id.menu_nav:
			btnNav.performClick();
			return true;
		case R.id.menu_phone:
			btnPhone.performClick();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "======onViewCreated======");
		super.onViewCreated(view, savedInstanceState);

		// 指示这个Fragment应该作为可选菜单的添加项（否则，这个Fragment不接受对onCreateOptionsMenu()方法的调用）
		if (SmartBarUtils.hasSmartBar()) {
			setHasOptionsMenu(true);
		}

		Bundle arg = getArguments();
		if (arg != null) {
			itemOrder = (Item) arg.getSerializable(Keys.ORDER);
		}

		if (itemOrder == null) {
			return;
		}

		orderId = itemOrder.booksId;
		boolean orderIsDetail = arg.getBoolean(Keys.ORDER_IS_DETAIL, false);

		initView(view);

		if (orderIsDetail) {
			Message msg = new Message();
			msg.what = WHAT_ORDER_DETAIL;
			msg.obj = itemOrder;
			myHandler.sendMessage(msg);
		} else {
			initData();
		}
	}

	private void initView(View view) {

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
				initData();
			}
		});

		View layoutTopbar = view.findViewById(R.id.topBar);
		layoutContenOrder = (ViewGroup) view
				.findViewById(R.id.layoutContenOrder);
		layoutPayWayContainer = (ViewGroup) view
				.findViewById(R.id.layoutPayWayContainer);
		layoutOrderDetail = view.findViewById(R.id.layoutOrderDetail);
		View layoutState1 = view.findViewById(R.id.layoutOrderState);
		ivOrderState = (ImageView) view.findViewById(R.id.ivOrderState);
		tvState = (TextView) layoutState1.findViewById(R.id.tvState);
		tvOrderNum = (TextView) view.findViewById(R.id.tvOrderNum);
		tvOrderState2 = (TextView) view.findViewById(R.id.tvOrderState2);
		// tvDateSubmit = (TextView) view.findViewById(R.id.tvSubmitDate);
		tvPriceOrder = (TextView) view.findViewById(R.id.tvPriceOrder);
		tvPayWay = (TextView) view.findViewById(R.id.tvPayWay);
		View layoutReserveRoom = view.findViewById(R.id.layoutReserveRoom);
		tvHotelName = (TextView) view.findViewById(R.id.tvHotelNameOrder);
		ImageView ivHotelLogoOrder = (ImageView) view
				.findViewById(R.id.ivHotelLogoOrder);
		tvAddr = (TextView) view.findViewById(R.id.tvAddrOrder);
		tvRoomType = (TextView) view.findViewById(R.id.tvRoomTypeOrder);
		tvRoomPrice = (TextView) view.findViewById(R.id.textView5);
		layouDateArrive = view.findViewById(R.id.layoutDateArrive);
		tvDateArrivePre = (TextView) view.findViewById(R.id.tvDateArrivePre);
		tvDateArrive = (TextView) view.findViewById(R.id.tvDateArrive);
		// tvDataArriveLast = (TextView)
		// view.findViewById(R.id.tvDataArriveLast);
		// tvRoomCount = (TextView) view.findViewById(R.id.tvRoomCount);
		tvLiveMan = (TextView) view.findViewById(R.id.tvLiveMan);
		tvLiveManPhone = (TextView) view.findViewById(R.id.tvLiveManPhone);
		// tvContact = (TextView) view.findViewById(R.id.tvContact);
		// tvContactPhone = (TextView) view.findViewById(R.id.tvContactPhone);
		layoutTip = view.findViewById(R.id.layoutTip);
		layouHourEndTip = view.findViewById(R.id.layouHourEndTip);
		tvLiveTip = (TextView) view.findViewById(R.id.tvLiveTip);
		tvHourEndTip = (TextView) view.findViewById(R.id.tvHourEndTip);
		btnCancelOrder = view.findViewById(R.id.btnCancelOrder);

		// call_relative = view.findViewById(R.id.order_relative);
		// callVisible = call_relative.findViewById(R.id.view_register);
		// btnCallHotel = call_relative.findViewById(R.id.button1);

		// ======拨打电话对话框
		ivMask = view.findViewById(R.id.ivMask);
		layoutCallDialog = view.findViewById(R.id.layoutCall);
		View btnCall400 = layoutCallDialog.findViewById(R.id.button1);
		btnCallHotel = (Button) layoutCallDialog.findViewById(R.id.button2);
		View btnCancel = layoutCallDialog.findViewById(R.id.button3);

		btnCall400.setOnClickListener(new OnClickListener() {
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
				UIHelper.call(activity, Constants.TEL_JUJIA);
			}
		});

		if (!StringUtils.isEmpty(itemOrder.hotelsTel)
				&& !itemOrder.hotelsTel.trim().equals("0")) {
			btnCallHotel.setText("拨至酒店 " + itemOrder.hotelsTel);
		} else {
			btnCallHotel.setText("酒店电话");
		}

		btnCallHotel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				if (!StringUtils.isEmpty(itemOrder.hotelsTel)
						&& !itemOrder.hotelsTel.trim().equals("0")) {

					if (layoutCallDialog.getVisibility() == View.VISIBLE) {
						AnimationUtil.animationShowSifbHideSotbSpecial(
								activity, false, layoutCallDialog);
						AnimationUtil.animationShowHideAlphaSpecial(activity,
								false, ivMask);
					}

					UIHelper.call(activity, itemOrder.hotelsTel);
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

		tvHotelName.setText(itemOrder.hotelsName);
		tvPriceOrder.setText(itemOrder.priceOrder);
		// TODO 这里的价格应该是原价吗？
		tvRoomPrice.setText(itemOrder.priceOrder);

		// tvDataArriveLast.setText(itemOrder.getRoomUseDate());

		if (itemOrder.sellType == Room.SALE_TYPE_ZDF) {
			tvRoomType.setText("钟点房");
		} else if (itemOrder.sellType == Room.SALE_TYPE_WYF
				|| itemOrder.sellType == Room.SALE_TYPE_LSF) {
			tvRoomType.setText("午夜房");
		} else if (itemOrder.sellType == Room.SALE_TYPE_LSF) {
			tvRoomType.setText("零时房");
		}

		tvHotelName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				LatLng locData = ac.getMyLocation();

				Item hotel = MyDbHelper.getInstance(activity)
						.queryHotelById(itemOrder.hotelsId, locData.latitude,
								locData.longitude);
				if (hotel != null) {
					UIHelper.showHotelActivity(activity, hotel);
				}
			}
		});

		layoutReserveRoom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				tvHotelName.performClick();
			}
		});

		ac.mImageLoader.displayImage(URLs.URL_ZDF_API + itemOrder.logopath,
				ivHotelLogoOrder, ac.optionsLogo);

		final ImageView ivReserveInfoArrow = (ImageView) view
				.findViewById(R.id.ivReserveInfoArrow);
		final View layoutReserveInfo = view
				.findViewById(R.id.layoutReserveInfo);

		ivReserveInfoArrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (layoutReserveInfo.getVisibility() == View.VISIBLE) {
					layoutReserveInfo.setVisibility(View.GONE);
					ivReserveInfoArrow
							.setImageResource(R.drawable.ic_arrow_down);
				} else {
					layoutReserveInfo.setVisibility(View.VISIBLE);
					ivReserveInfoArrow.setImageResource(R.drawable.ic_arrow_up);
				}
			}
		});

		layouDateArrive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ivReserveInfoArrow.performClick();
			}
		});

		View layoutBtn = view.findViewById(R.id.layoutBtn);
		View layoutTelHotelOrder = layoutBtn
				.findViewById(R.id.layoutTelHotelOrder);
		View layoutNavOrder = layoutBtn.findViewById(R.id.layoutNavOrder);
		btnPhone = layoutBtn.findViewById(R.id.btnTelHotelOrder);
		btnNav = layoutBtn.findViewById(R.id.btnNavOrder);

		btnPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				showCallDialog();
			}
		});

		btnNav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				UIHelper.showPathNav(activity, itemOrder);
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

		((TextView) view.findViewById(R.id.tvTitle)).setText("订单详情");
		btnLeft = (ImageView) view.findViewById(R.id.btnLeft);
		ImageView btnRight = (ImageView) view.findViewById(R.id.btnRight);
		dialogbg = (ImageView) view.findViewById(R.id.sreach_bg);

		btnLeft.setVisibility(View.VISIBLE);
		btnRight.setVisibility(View.INVISIBLE);

		if (SmartBarUtils.hasSmartBar()) {
			btnLeft.setVisibility(View.VISIBLE);
		} else {
			btnLeft.setVisibility(View.VISIBLE);
		}

		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// activity.finish();
				activity.onBackPressed();
			}
		});

		btnCancelOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 取消订单

				cancelOrder(itemOrder.booksId);
			}
		});

		btnCancelOrder.setVisibility(View.GONE);

		// btnRight.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (UIHelper.isFastDoubleClick(v)) {
		// return;
		// }
		//
		// UIHelper.call(activity, Constants.TEL_JUJIA);
		//
		// // if (call_relative.getVisibility() == View.GONE) {
		// // animation(true, call_relative);
		// // call_relative.setVisibility(View.VISIBLE);
		// // animation1(true, dialogbg);
		// // dialogbg.setVisibility(View.VISIBLE);
		// // } else {
		// // animation(false, call_relative);
		// // call_relative.setVisibility(View.GONE);
		// // animation1(false, dialogbg);
		// // dialogbg.setVisibility(View.GONE);
		// // }
		// }
		// });

		// callVisible.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View arg0, MotionEvent arg1) {
		// return true;
		// }
		// });

		// dialogbg.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (UIHelper.isFastDoubleClick(v)) {
		// return;
		// }
		//
		// if (call_relative.getVisibility() == View.VISIBLE) {
		// animation(false, call_relative);
		// call_relative.setVisibility(View.GONE);
		// animation1(false, dialogbg);
		// dialogbg.setVisibility(View.GONE);
		// }
		// }
		// });

		// btnCallHotel.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (UIHelper.isFastDoubleClick(v)) {
		// return;
		// }
		// UIHelper.call(activity, Constants.TEL_JUJIA);
		// }
		// });
	}

	protected void showCallDialog() {
		if (layoutCallDialog.getVisibility() == View.VISIBLE) {
			return;
		}

		AnimationUtil.animationShowSifbHideSotbSpecial(activity, true,
				layoutCallDialog);
		AnimationUtil.animationShowHideAlphaSpecial(activity, true, ivMask);
	}

	protected void cancelOrder(final String orderId) {
		pb.setVisibility(View.VISIBLE);
		new Thread() {
			Message msg = new Message();

			public void run() {
				try {
					Item r = ac.cancelOrder(ac.getLoginUserId(), orderId);

					msg.what = WHAT_CANCEL_ORDER;
					msg.obj = r;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_CANCEL_ORDER;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			};
		}.start();
	}

	public void onEventMainThread(Event e) {
		Log.d(TAG, "======onEventMainThread======"
				+ e.getClass().getSimpleName());

		if (e instanceof CouponSelectEvent) {
			CouponSelectEvent ev = (CouponSelectEvent) e;

			activity.mCoupon = ev.coupon;

			initPayInfo();
		}

	}

	private void initViewPayOrder() {

		View layoutPayWay = inflater.inflate(R.layout.layout_pay_way,
				layoutPayWayContainer);

		layoutBottomToPay = view.findViewById(R.id.layoutBottomToPay);
		View btnToPay = layoutBottomToPay.findViewById(R.id.btnToPay);
		layoutBottomToPay.setVisibility(View.VISIBLE);

		// tvOrderPrice = (TextView)
		// layoutPayWay.findViewById(R.id.tvOrderPrice);
		tvPayMoneyPre = (TextView) view.findViewById(R.id.tvPayMoneyPre);
		tvPayMoney = (TextView) view.findViewById(R.id.tvMoneyPay);
		tvBalance = (TextView) layoutPayWay.findViewById(R.id.tvBalance);

		View layoutUseCoupon = layoutPayWay.findViewById(R.id.layoutUseCoupon);
		tvUseCoupon = (TextView) layoutPayWay.findViewById(R.id.tvUseCoupon);

		layoutPayPlatform = layoutPayWay.findViewById(R.id.layoutPayPlatform);
		activity.initViewPayPlatform(layoutPayPlatform);

		layoutUseCoupon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				Intent intent = new Intent(activity, SimpleFragActivity.class);
				intent.putExtra(Keys.FRAG_NAME,
						LvCouponFragment.class.getName());

				Bundle extras = new Bundle();
				extras.putSerializable(Keys.ITEM, activity.mCoupon);
				extras.putString(Keys.ORDER_ID, orderId);
				intent.putExtras(extras);

				activity.startActivity(intent);
			}
		});

		// View btnChoosePayWay = layoutBottomPay.findViewById(R.id.btnPay);

		// if (itemOrder.booksStatus == Item.ORDER_STATE_WAIT_PAY_ONLINE) {
		//
		// boolean isTimeOut4Pay = isTimeOut4Pay(itemOrder);
		//
		// if (isTimeOut4Pay) {
		// layoutBottomPay.setVisibility(View.GONE);
		// itemOrder.booksStatus = Item.ORDER_STATE_PAY_ONLINE_TIME_OVER;
		//
		// OrderChangeEvent event = new OrderChangeEvent();
		// event.change = true;
		// EventBus.getDefault().post(event);
		//
		// } else {
		// layoutBottomPay.setVisibility(View.VISIBLE);
		// // btnChoosePayWay.setOnClickListener(new OnClickListener() {
		// // @Override
		// // public void onClick(View v) {
		// // if (isTimeOut4Pay(itemOrder)) {
		// // layoutBottomPay.setVisibility(View.GONE);
		// // AppContext.toastShow("离订单提交成功已超过15分钟，请重新下单");
		// // } else {
		// //
		// // checkBalanceBeforePay();
		// //
		// // }
		// // }
		// //
		// // });
		// }
		// } else {
		// layoutBottomPay.setVisibility(View.GONE);
		// }

		activity.mCoupon = null;
		initPayInfo();

		checkBalanceBeforePay();

		btnToPay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				// TODO 客户端计算是否过期
				// if (activity.isTimeOut4Pay(itemOrder)) {
				// // layoutBottomToPay.setVisibility(View.GONE);
				// AppContext.toastShow("离订单提交成功已超过15分钟，请重新下单");
				// return;
				// }

				// if (isTimeOut4Pay(itemOrder)) {
				// layoutBottomPay.setVisibility(View.GONE);
				// AppContext.toastShow("离订单提交成功已超过15分钟，请重新下单");
				// } else {
				// checkBalanceBeforePay();
				// }

				if (isUseBalanceEnought) {

					useBlancePay();

				} else {

					if (PayOnlineActivity.PAY_PLATFORM_ALIPAY
							.equals(activity.payPlatform)) {

						String subject = "";
						if (itemOrder.sellType == Room.SALE_TYPE_ZDF) {
							if (!StringUtils.isEmpty(itemOrder.hours)) {
								subject = itemOrder.hours + "小时钟点房";
							} else {
								subject = "钟点房";
							}
						} else if (itemOrder.sellType == Room.SALE_TYPE_WYF
								|| itemOrder.sellType == Room.SALE_TYPE_LSF) {
							subject = "午夜房";
						} else if (itemOrder.sellType == Room.SALE_TYPE_LSF) {
							subject = "零时房";
						}

						if (!StringUtils.isEmpty(itemOrder.brandName)) {
							subject = "有间房-" + itemOrder.brandName + "-"
									+ subject;
						} else {
							subject = "有间房-" + subject;
						}

						String body = subject;

						activity.topayAlipay(subject, body, ""
								+ activity.payMoney);

					} else if (PayOnlineActivity.PAY_PLATFORM_UPPAY
							.equals(activity.payPlatform)) {

						String couponId = "0";
						if (activity.mCoupon != null) {
							couponId = activity.mCoupon.couponId;
						}

						activity.getUppayTn(itemOrder.booksId,
								activity.mPayType, "" + activity.payMoney,
								couponId);
					}
				}
			}
		});
	}

	private void initPayInfo() {
		int orderPrice = StringUtils.toInt(itemOrder.priceOrder);
		activity.payMoney = orderPrice;

		if (activity.mCoupon != null
				&& (activity.mCoupon.couponPrice - orderPrice) >= 0) {
			// 优惠券金额足以支付订单，归入余额支付
			// <优惠券>
			isUseBalanceEnought = true;
			layoutPayPlatform.setVisibility(View.GONE);

			useBalance = 0;
			activity.payMoney = 0;

			tvBalance.setText("￥" + useBalance);
			tvPayMoneyPre.setText("余额支付：");
			tvPayMoney.setText("" + useBalance);

			tvUseCoupon.setText(activity.mCoupon.couponName);

			return;
		}

		if (ac.user.balance == 0) {
			// 没有余额
			isUseBalanceEnought = false;
			layoutPayPlatform.setVisibility(View.VISIBLE);

			if (activity.mCoupon != null) {
				// <在线+优惠券>
				activity.mPayType = PayOnlineActivity.PAY_TYPE_USE_COUPON;
				activity.payMoney = orderPrice - activity.mCoupon.couponPrice;
			} else {
				// <在线>
				activity.mPayType = PayOnlineActivity.PAY_TYPE_NOT_USE_BALANCE;
				activity.payMoney = orderPrice;
			}

			useBalance = 0;

		} else if (ac.user.balance >= orderPrice) {
			// 余额充足，足以支付
			isUseBalanceEnought = true;
			layoutPayPlatform.setVisibility(View.GONE);

			if (activity.mCoupon != null) {
				// <余额+优惠券>
				activity.payMoney = 0;
				useBalance = orderPrice - activity.mCoupon.couponPrice;

			} else {
				// <余额>
				activity.payMoney = 0;
				useBalance = orderPrice;
			}

		} else {
			// 有余额,但不足以支付
			if (activity.mCoupon != null) {
				// <余额+优惠券> 足以支付
				if ((ac.user.balance + activity.mCoupon.couponPrice
						- orderPrice >= 0)) {
					isUseBalanceEnought = true;
					layoutPayPlatform.setVisibility(View.GONE);

					activity.payMoney = 0;
					useBalance = orderPrice - activity.mCoupon.couponPrice;

				} else {
					// <在线+余额+优惠券>
					isUseBalanceEnought = false;
					layoutPayPlatform.setVisibility(View.VISIBLE);

					activity.mPayType = PayOnlineActivity.PAY_TYPE_USE_BALANCE_COUPON;
					useBalance = (int) ac.user.balance;
					activity.payMoney = (int) (orderPrice - ac.user.balance - activity.mCoupon.couponPrice);
				}
			} else {
				// <在线 + 余额支付>
				isUseBalanceEnought = false;
				layoutPayPlatform.setVisibility(View.VISIBLE);

				activity.mPayType = PayOnlineActivity.PAY_TYPE_USE_BALANCE_HALF;
				useBalance = (int) ac.user.balance;
				activity.payMoney = (int) (orderPrice - ac.user.balance);
			}
		}

		tvBalance.setText("￥" + useBalance);

		if (isUseBalanceEnought) {
			tvPayMoneyPre.setText("余额支付：");
			tvPayMoney.setText("" + useBalance);
		} else {
			tvPayMoneyPre.setText("支付金额：");
			tvPayMoney.setText("" + activity.payMoney);
		}

		if (activity.mCoupon != null) {
			tvUseCoupon.setText(activity.mCoupon.couponName);
		} else {
			tvUseCoupon.setText("使用代金券");
		}
	}

	/**
	 * 全部使用余额支付
	 * 
	 * @param payPsw
	 */
	protected void useBlancePay() {
		pb.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {

					String hotelsId = itemOrder.hotelsId;
					String booksId = itemOrder.booksId;

					String couponId = "0";
					if (activity.mCoupon != null) {
						couponId = activity.mCoupon.couponId;
					}

					String payPsw = null;

					Item order = ac.balancePay(ac.getLoginUserId(), booksId,
							useBalance, payPsw, hotelsId, itemOrder.sellType,
							couponId);

					msg.what = WHAT_BALANCE_PAY_SUCCESS;
					msg.obj = order;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_BALANCE_PAY_SUCCESS;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			}
		}.start();
	}

	private void checkBalanceBeforePay() {
		pb.setVisibility(View.VISIBLE);

		new Thread() {
			Message msg = new Message();

			public void run() {
				try {
					Recharge r = null;

					r = ac.queryBalance(ac.getLoginUserId());

					if (r.rspMsg.OK()) {
						ac.user.balance = StringUtils.toFloat(r.balance);
					}

					msg.what = WHAT_QUERY_BALANCE_BEFORE_PAY;
					msg.obj = r;

				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_QUERY_BALANCE_BEFORE_PAY;
					msg.obj = e;

				}

				myHandler.sendMessage(msg);
			};
		}.start();
	}

//	protected void orderDone() {
//		pb.setVisibility(View.VISIBLE);
//		new Thread() {
//			Message msg = new Message();
//
//			public void run() {
//				try {
//					RspMsg r = null;
//					r = ac.orderDone(orderId);
//
//					msg.what = WHAT_ORDER_DONE;
//					msg.obj = r;
//				} catch (AppException e) {
//					e.printStackTrace();
//					msg.what = -1;
//					msg.obj = e;
//				}
//				myHandler.sendMessage(msg);
//			};
//		}.start();
//	}

	void initData() {
		pb.setVisibility(View.VISIBLE);
		new Thread() {
			Message msg = new Message();

			public void run() {
				try {
					Item r = null;

					r = ac.getOrderDetail(orderId, itemOrder.sellType);

					msg.what = WHAT_ORDER_DETAIL;
					msg.obj = r;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -WHAT_ORDER_DETAIL;
					msg.obj = e;
				}
				myHandler.sendMessage(msg);
			};
		}.start();
	}

	private MyHandler myHandler = new MyHandler(this);

	static class MyHandler extends Handler {
		// 注意下面的“<T>”类是MyHandler类所在的外部类
		private WeakReference<OrderDetailFragment> mWr;

		public MyHandler(OrderDetailFragment frag) {
			mWr = new WeakReference<OrderDetailFragment>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			final OrderDetailFragment frag = mWr.get();

			// 解决bug:Fragment被销毁了导致一些空指针异常、或者fragment not attached to
			// activity异常；原因是Fragment被销毁或者deatach后，线程才返回结果；最好的解决办法应该是，在Fragment被销毁时结束线程请求
			if (frag == null || !frag.isAdded()) {
				return;
			}

			frag.pb.setVisibility(View.GONE);

			switch (msg.what) {
			case WHAT_ORDER_DETAIL:

				Item order = (Item) msg.obj;

				if (order.rspMsg.OK()) {
					frag.layoutNoDataTip.setVisibility(View.GONE);
					frag.layoutOrderDetail.setVisibility(View.VISIBLE);

					frag.showOrderDetail(order);

				} else {
					frag.ivNoData
							.setImageResource(R.drawable.img_no_data_tip_fail);
					frag.btnReload.setVisibility(View.VISIBLE);
					frag.layoutNoDataTip.setVisibility(View.VISIBLE);
					frag.layoutOrderDetail.setVisibility(View.GONE);
					AppContext.toastShow(order.rspMsg.message);
				}
				break;
			case -WHAT_ORDER_DETAIL:
				frag.pb.setVisibility(View.GONE);

				// 点击重试
				frag.ivNoData.setImageResource(R.drawable.img_no_data_tip_fail);
				frag.btnReload.setVisibility(View.VISIBLE);
				frag.layoutNoDataTip.setVisibility(View.VISIBLE);
				frag.layoutOrderDetail.setVisibility(View.GONE);
				((AppException) msg.obj).makeToast(frag.ac);

				break;
			case WHAT_BALANCE_PAY_SUCCESS:
				frag.pb.setVisibility(View.GONE);

				Item rsp = (Item) msg.obj;

				if (rsp.rspMsg.OK()
						|| RspMsg.CODE_BALANCE_PAY_SUCCESS
								.equals(rsp.rspMsg.code)) {

					rsp.rspMsg.code = RspMsg.CODE_SUCCESS;
					frag.activity.itemOrder = rsp;
					frag.activity.paySuccess();
				} else {
					AppContext.toastShow(rsp.rspMsg.message);
				}

				break;
			case -WHAT_BALANCE_PAY_SUCCESS:
				frag.pb.setVisibility(View.GONE);

				AppContext.toastShow(R.string.pleaseRetry);
				((AppException) msg.obj).makeToast(frag.ac);

				break;
			case WHAT_CANCEL_ORDER:
				frag.pb.setVisibility(View.GONE);

				order = (Item) msg.obj;

				if (order.rspMsg.OK()) {
					frag.showOrderDetail(order);

				} else {
					AppContext.toastShow(order.rspMsg.message);
				}

				break;
			case -WHAT_CANCEL_ORDER:
				frag.pb.setVisibility(View.GONE);
				AppContext.toastShow(R.string.pleaseRetry);
				((AppException) msg.obj).makeToast(frag.ac);
				break;

			case WHAT_QUERY_BALANCE_BEFORE_PAY:
				frag.pb.setVisibility(View.GONE);
				frag.initPayInfo();
				break;
			case -WHAT_QUERY_BALANCE_BEFORE_PAY:
				frag.pb.setVisibility(View.GONE);
				((AppException) msg.obj).makeToast(frag.ac);
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

	public void showOrderDetail(Item order) {
		if (itemOrder.booksStatus != order.booksStatus) {
			// 列表传入的订单与详情页获取的订单状态不同
			OrderChangeEvent event = new OrderChangeEvent();
			event.change = true;
			EventBus.getDefault().post(event);
		}

		if (itemOrder.booksStatus == Item.ORDER_STATE_PAY_ONLINE_TIME_OVER) {
			order.booksStatus = Item.ORDER_STATE_PAY_ONLINE_TIME_OVER;
		}

		// TODO 临时测试
		// order.booksStatus = Item.ORDER_STATE_WAIT_PAY_ONLINE;

		if (!StringUtils.isEmpty(order.booksNum)) {
			tvOrderNum.setText(order.booksNum);
		} else {
			tvOrderNum.setText(order.booksId);
		}

		if (order.sellType == Room.SALE_TYPE_ZDF) {
			tvHourEndTip.setVisibility(View.VISIBLE);
			tvHourEndTip.setText("退房时间：满时退房，或根据酒店要求协商具体退房时间。");
			tvLiveTip
					.setText("入住须知：钟点房预订成功后，客房将为您保留30分钟，如不能在规定时间到店入住，请联系酒店或有间房客服4008-521-002。");

		} else if (order.sellType == Room.SALE_TYPE_WYF
				|| order.sellType == Room.SALE_TYPE_LSF) {
			tvHourEndTip.setVisibility(View.GONE);
			tvLiveTip.setText("入住须知：仅限预订当日酒店，次日中午12点前退房，预订成功后，需在约定时间入住，支持到店付款");
		}

		if (order.payWay == Item.PAY_WAY_ONLINE) {
			tvPayWay.setText("在线支付");

		} else if (order.payWay == Item.PAY_WAY_ARRIVE) {
			tvPayWay.setText("到店支付");
		}

		String roomUseDate = order.getRoomUseDate();
		String roomUseYMD = "";
		String roomUseHour = "";
		String bookEndDate = order.getBookEndDate();
		String bookEndYMD = "";
		String bookEndHour = "";

		int roomUseYear = 0;
		int roomUseMonth = 0;
		int roomUseDay = 0;

		int bookEndYear = 0;
		int bookEndMonth = 0;
		int bookEndDay = 0;

		Calendar calendarCurt = Calendar.getInstance();
		Calendar calendarRoomUse = Calendar.getInstance();
		Calendar calendarbookEnd = Calendar.getInstance();

		if (!StringUtils.isEmpty(roomUseDate)) {
			String[] split = roomUseDate.split(" ");
			String[] splitDate = split[0].split("-");
			roomUseYMD = split[0];
			roomUseYear = StringUtils.toInt(splitDate[0]);
			roomUseMonth = StringUtils.toInt(splitDate[1]);
			roomUseDay = StringUtils.toInt(splitDate[2]);
			roomUseHour = split[1];

			calendarRoomUse.set(roomUseYear, roomUseMonth - 1, roomUseDay,
					StringUtils.toInt(roomUseHour.split(":")[0]),
					StringUtils.toInt(roomUseHour.split(":")[1]));

		}
		if (!StringUtils.isEmpty(bookEndDate)) {

			String[] split = bookEndDate.split(" ");
			String[] splitDate = split[0].split("-");
			bookEndYMD = split[0];
			bookEndYear = StringUtils.toInt(splitDate[0]);
			bookEndMonth = StringUtils.toInt(splitDate[1]);
			bookEndDay = StringUtils.toInt(splitDate[2]);
			bookEndHour = split[1];

			calendarbookEnd.set(bookEndYear, bookEndMonth - 1, bookEndDay,
					StringUtils.toInt(bookEndHour.split(":")[0]),
					StringUtils.toInt(bookEndHour.split(":")[1]));

		}

		ivOrderState.setVisibility(View.VISIBLE);

		if (order.sellType == Room.SALE_TYPE_ZDF) {
			tvDateArrivePre.setText("到店时间");
			if (!StringUtils.isEmpty(roomUseHour)) {
				if (DateUtil.isToday(calendarRoomUse)) {
					tvDateArrive.setText("当日 " + roomUseHour);
				} else if (roomUseDay == bookEndDay) {
					tvDateArrive.setText(roomUseYMD + " " + roomUseHour);
				} else {
					tvDateArrive.setText(roomUseYMD + " " + roomUseHour);
				}
			} else {
				tvDateArrive.setText("未确认");
			}

		} else if (order.sellType == Room.SALE_TYPE_WYF
				|| order.sellType == Room.SALE_TYPE_LSF) {

			tvDateArrivePre.setText("入住时段");
			if (!StringUtils.isEmpty(roomUseHour)) {
				if (DateUtil.isToday(calendarRoomUse)) {
					tvDateArrive.setText("当日 " + roomUseHour + "—次日 "
							+ bookEndHour);
				} else if (roomUseDay == bookEndDay) {
					tvDateArrive.setText(roomUseYMD + " " + roomUseHour + "—"
							+ bookEndHour);
				} else {
					tvDateArrive.setText(roomUseYMD + " " + roomUseHour + "—次日"
							+ bookEndHour);
				}
			} else {
				tvDateArrive.setText("未确认");
			}
		}

		switch (order.booksStatus) {
		case Item.ORDER_STATE_WAIT_RESPONSE:
			// 提交成功，等待确认
		case Item.ORDER_STATE_PAY_ONLINE_SUCCESS_WAIT_RESPONSE:
			// 支付成功，等待确认
			ivOrderState
					.setImageResource(R.drawable.img_booking_waiting_for_confirm);
			tvState.setText("等待确认");
			break;

		case Item.ORDER_STATE_WAIT_PAY_ONLINE:
			// 预订成功，等待支付
			ivOrderState
					.setImageResource(R.drawable.img_booking_waiting_payment);
			tvState.setText("等待支付");

			layoutTip.setVisibility(View.GONE);

			initViewPayOrder();

			break;

		case Item.ORDER_STATE_PAY_ONLINE_SUCCESS_WAIT_LIVE:
			// 支付成功，等待入住
		case Item.ORDER_STATE_WAIT_ARRIVE_PAY_LIVE:
			// 等待到店支付
			ivOrderState
					.setImageResource(R.drawable.img_booking_waiting_for_live);
			tvState.setText("等待入住");

			break;

		case Item.ORDER_STATE_CANCEL:
			// 订单取消
			ivOrderState.setImageResource(R.drawable.img_booking_canceled);
			tvState.setText("订单取消");

			break;

		case Item.ORDER_STATE_PAY_ONLINE_TIME_OVER:
			// 订单过期
			ivOrderState.setImageResource(R.drawable.img_booking_timeout);
			tvState.setText("订单过期");
			break;

		case Item.ORDER_STATE_CANCEL_WAIT_RESPONSE:
			// 取消中，待审核
			ivOrderState
					.setImageResource(R.drawable.img_booking_waiting_for_live);
			tvState.setText("取消确认中");
			break;

		case Item.ORDER_STATE_NOT_ARRIVE:
			// 未入住
		case Item.ORDER_STATE_ARRIVED:
			// 已入住
		case Item.ORDER_STATE_LEAVED:
			// 已离店
		case Item.ORDER_STATE_DONE:
			// 订单完成
			ivOrderState.setImageResource(R.drawable.img_booking_finished);
			tvState.setText("订单完成");
			break;

		default:
			ivOrderState.setVisibility(View.INVISIBLE);
			tvState.setText("未知状态：" + order.booksStatus);
			break;
		}

		tvOrderState2.setText(tvState.getText());

		// if (Item.ORDER_STATE_PAY_ONLINE_TIME_OVER == order.booksStatus) {
		// ivOrderState.setImageResource(R.drawable.img_booking_timeout);
		// tvState.setText("订单过期");
		// }

		if (order.booksStatus == Item.ORDER_STATE_CANCEL
				|| order.booksStatus == Item.ORDER_STATE_PAY_ONLINE_TIME_OVER
				|| order.booksStatus == Item.ORDER_STATE_CANCEL_WAIT_RESPONSE) {
			btnCancelOrder.setVisibility(View.GONE);
			if (layoutBottomToPay != null) {
				layoutBottomToPay.setVisibility(View.GONE);
			}
		} else {
			btnCancelOrder.setVisibility(View.VISIBLE);
		}

		tvAddr.setText(order.hotelsAddr);

		String price;
		if (order.sellType == Room.SALE_TYPE_ZDF) {
			if (!StringUtils.isEmpty(order.priceOrder)) {
				price = "￥" + order.priceOrder;
				// if (!StringUtils.isEmpty(order.price)) {
				// price = price + "（原价￥" + order.price + "）";
				// }
				tvPriceOrder.setText(price);
			}

			if (!StringUtils.isEmpty(order.hours)) {
				tvRoomType.setText(order.hours + "小时钟点房");
			}
		} else {
			if (!StringUtils.isEmpty(order.priceOrder)) {
				price = "￥" + order.priceOrder;
				// if (!StringUtils.isEmpty(order.price)) {
				// price = price + "（原价￥" + order.price + "）";
				// }
				tvPriceOrder.setText(price);
			}

			if (!StringUtils.isEmpty(order.roomTypeName)) {
				tvRoomType.setText(order.roomTypeName);
			}
		}
		tvLiveManPhone.setText(order.roomUserMobile);
		tvLiveMan.setText(order.roomUserName);

		// tvRoomCount.setText(order.roomCount);
		tvHotelName.setText(order.hotelsName);
		tvRoomPrice.setText(itemOrder.priceOrder);

		itemOrder.hotelsTel = order.hotelsTel;
		itemOrder.hotelsLatitude = order.hotelsLatitude;
		itemOrder.hotelsLongitude = order.hotelsLongitude;
		itemOrder.brandName = order.brandName;

		if (!StringUtils.isEmpty(itemOrder.hotelsTel)
				&& !itemOrder.hotelsTel.trim().equals("0")) {
			btnCallHotel.setText("拨至酒店 " + itemOrder.hotelsTel);
		} else {
			btnCallHotel.setText("酒店电话");
		}

		// itemOrder.brandId = order.brandId;
		// itemOrder.brandName = MyDbHelper.getInstance(activity).querBrandName(
		// order.brandId);
	}

	// public void showLayoutBottom() {
	// if (SmartBarUtils.hasSmartBar()) {
	// layoutBottomTab.setVisibility(View.GONE);
	// } else {
	// layoutBottomTab.setVisibility(View.VISIBLE);
	// }
	// }
	//
	// public void hideLayoutBottom() {
	// layoutBottomTab.setVisibility(View.GONE);
	// }

}
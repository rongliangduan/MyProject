package cn.op.zdf.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import cn.op.common.UIHelper;
import cn.op.common.constant.Tags;
import cn.op.common.util.DisplayUtil;
import cn.op.common.util.Log;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.domain.City;
import cn.op.zdf.event.CityChooseEvent;
import cn.op.zdf.event.DecodeCityNameByLatLngEvent;
import cn.op.zdf.event.Event;
import cn.op.zdf.event.LoginEvent;
import cn.op.zdf.event.MenuSelectEvent;

import com.readystatesoftware.viewbadger.BadgeView;

import de.greenrobot.event.EventBus;

/**
 * @deprecated 左侧菜单 not use ，现在的控制直接放在了MainActivity中
 * @author lufei
 * 
 */
public class MenuLeftFragment extends Fragment {

	private AppContext ac;
	private MainActivity activity;
	public CheckBox[] menus;
	protected CheckBox toSelectMenu;
	private TextView tvCity;
	protected CheckBox lastCheckedMenu;
	protected int curtCheckedMenuIndex;
	private CheckBox tv1HotelShow;

	protected static final int WHAT_INIT = 1;
	private static final String TAG = Log.makeLogTag(MenuLeftFragment.class);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		activity = (MainActivity) getActivity();
		ac = (AppContext) activity.getApplication();
		EventBus.getDefault().register(this);

		View view = inflater.inflate(R.layout.layout_menu_left, null);
		tvCity = (TextView) view.findViewById(R.id.textView6);
		tv1HotelShow = (CheckBox) view.findViewById(R.id.textView1);
		CheckBox tv2 = (CheckBox) view.findViewById(R.id.textView2);
		CheckBox tv3 = (CheckBox) view.findViewById(R.id.textView3);
		CheckBox tv4 = (CheckBox) view.findViewById(R.id.textView4);
		CheckBox tv5 = (CheckBox) view.findViewById(R.id.textView5);

		lastCheckedMenu = tv1HotelShow;

		// BadgeView badge = getBadge(ac, tv1);
		// badge.setText("2");
		// badge.show();

		menus = new CheckBox[] { tv1HotelShow, tv2, tv3, tv4, tv5 };

		String[] fragTags = new String[] { "temp", Tags.TAG_ORDER,
				Tags.TAG_COLLECT, Tags.TAG_ACCOUNT, Tags.TAG_ABOUT_US };

		Class[] fragClasses = new Class[] { Fragment.class,
				LvOrderFragment.class, LvOrderFragment.class,
				AccountFragment.class, AboutUsFragment.class };

		// String[] fragTags = new String[] { null, Tags.TAG_ORDER,
		// Tags.TAG_COLLECT, Tags.TAG_ACCOUNT, Tags.TAG_SETTING };
		//
		// Class[] fragClasses = new Class[] { null, LvFragment.class,
		// LvFragment.class, AccountFragment.class, SettingFragment.class };

		for (int i = 0; i < menus.length; i++) {
			menus[i].setOnClickListener(onItemClickListener);
			menus[i].setTag(new TabListener<Fragment>(activity, fragTags[i],
					fragClasses[i], null));
		}

		tvCity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				// Fragment fragHotelDetail = Fragment.instantiate(activity,
				// CityChooseFragment.class.getName());
				// ((MainActivity) activity).fragmentUtil.addToBackStack(
				// R.id.slidingmenumain, fragHotelDetail,
				// Tags.CHOOSE_CITY, R.anim.slide_in_obj_from_right,
				// R.anim.slide_out_obj_to_right,
				// R.anim.slide_in_obj_from_right,
				// R.anim.slide_out_obj_to_right);
			}
		});

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			curtCheckedMenuIndex = savedInstanceState
					.getInt("curtCheckedMenuIndex");
			menus[curtCheckedMenuIndex].setChecked(true);
			lastCheckedMenu = menus[curtCheckedMenuIndex];

			FragmentTransaction ft = activity.fm.beginTransaction();
			for (int i = 1; i < menus.length; i++) {
				TabListener menuListener = (TabListener) menus[i].getTag();
				if (curtCheckedMenuIndex == i) {
					menuListener.onTabSelected(menus[i], ft);
				} else {
					menuListener.onTabUnselected(menus[i], ft);
				}
			}
			ft.commitAllowingStateLoss();

		}

	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	public void onEventMainThread(Event event) {
		Log.d(TAG, "======onEventMainThread====== "
				+ event.getClass().getSimpleName());

		if (event instanceof LoginEvent) {
			if (toSelectMenu != null) {
				toSelectMenu.performClick();
				toSelectMenu = null;
			}
		}

		if (event instanceof DecodeCityNameByLatLngEvent) {
			DecodeCityNameByLatLngEvent ev = (DecodeCityNameByLatLngEvent) event;
			City city = ev.city;

			tvCity.setText(city.cityName);
		}

	}

	/**
	 * Callback interface invoked when a tab is focused, unfocused, added, or
	 * removed.
	 */
	private class TabListener<T extends Fragment> {
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		private final Bundle mArgs;
		private Fragment mFragment;

		public TabListener(Activity activity, String tag, Class<T> clz) {
			this(activity, tag, clz, null);
		}

		public TabListener(Activity activity, String tag, Class<T> clz,
				Bundle args) {
			super();
			this.mActivity = activity;
			this.mTag = tag;
			this.mClass = clz;
			this.mArgs = args;
		}

		/**
		 * Called when a tab enters the selected state.
		 * 
		 * @param tab
		 *            The tab that was selected
		 * @param ft
		 *            A {@link FragmentTransaction} for queuing fragment
		 *            operations to execute during a tab switch. The previous
		 *            tab's unselect and this tab's select will be executed in a
		 *            single transaction. This FragmentTransaction does not
		 *            support being added to the back stack.
		 */
		public void onTabSelected(View tab, FragmentTransaction ft) {
			if (mFragment == null) {
				Fragment fragment = activity.fm.findFragmentByTag(mTag);
				if (fragment != null) {
					mFragment = fragment;
					// ft.attach(mFragment);
					ft.show(mFragment);
				} else {
					mFragment = Fragment.instantiate(mActivity,
							mClass.getName(), mArgs);
					ft.add(R.id.content_frame, mFragment, mTag);
				}
			} else {
				// ft.attach(mFragment);
				ft.show(mFragment);
			}
		}

		public void onTabUnselected(View tab, FragmentTransaction ft) {
			if (mFragment == null) {
				Fragment fragment = activity.fm.findFragmentByTag(mTag);
				if (fragment != null) {
					mFragment = fragment;
					// ft.attach(mFragment);
					ft.hide(mFragment);
				}
			} else {
				// ft.detach(mFragment);
				ft.hide(mFragment);
			}
		}
	}

	private BadgeView getBadge(Context context, View tv1) {
		BadgeView badge = new BadgeView(context, tv1);
		badge.setBadgePosition(BadgeView.POSITION_CENTER_RIGHT);
		badge.setBadgeMargin(
				DisplayUtil.dip2px(ac,
						getResources().getDimension(R.dimen.margin_large)), 5);

		badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		badge.setBackgroundResource(R.color.red_xyxs);

		badge.setPadding(DisplayUtil.dip2px(ac, 8), 0,
				DisplayUtil.dip2px(ac, 8), 0);
		return badge;
	}

	private OnClickListener onItemClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (UIHelper.isFastDoubleClick(v)) {
				return;
			}

			if (v == menus[3] || v == menus[1]) {
				if (!ac.isNetworkConnected()) {
					((CheckBox) v).setChecked(false);
					AppContext.toastShow(R.string.pleaseCheckNet);
					return;
				}

				if (!ac.isLogin()) {
					toSelectMenu = (CheckBox) v;

					((CheckBox) v).setChecked(false);
					Fragment fragment = Fragment.instantiate(activity,
							LoginFragment.class.getName());

					// ((MainActivity) activity).fragmentUtil.addToBackStack(
					// R.id.slidingmenumain, fragment, "login",
					// R.anim.slide_in_obj_from_right,
					// R.anim.slide_out_obj_to_right,
					// R.anim.slide_in_obj_from_right,
					// R.anim.slide_out_obj_to_right);

					return;
				}
			}

			for (int i = 0; i < menus.length; i++) {
				if (menus[i] == v) {
					curtCheckedMenuIndex = i;
				}
			}
			EventBus.getDefault().post(
					new MenuSelectEvent(curtCheckedMenuIndex));

			if (lastCheckedMenu == v) {
				((CheckBox) v).setChecked(true);
				// activity.mDrawerLayout.showContent();
			} else {
				FragmentTransaction ft = activity.fm.beginTransaction();
				if (v == tv1HotelShow) {
					lastCheckedMenu.setChecked(false);
					((TabListener<?>) lastCheckedMenu.getTag())
							.onTabUnselected(lastCheckedMenu, ft);

					((CheckBox) v).setChecked(true);
					lastCheckedMenu = (CheckBox) v;
				} else {
					lastCheckedMenu.setChecked(false);

					if (lastCheckedMenu != tv1HotelShow) {
						((TabListener<?>) lastCheckedMenu.getTag())
								.onTabUnselected(lastCheckedMenu, ft);
					}

					((CheckBox) v).setChecked(true);
					((TabListener<?>) v.getTag()).onTabSelected(v, ft);
					lastCheckedMenu = (CheckBox) v;
				}

				ft.commitAllowingStateLoss();
				// activity.mDrawerLayout.showContent();

			}
		}
	};

	public void onSaveInstanceState(Bundle outState) {

		outState.putInt("curtCheckedMenuIndex", curtCheckedMenuIndex);

		super.onSaveInstanceState(outState);
	};
}
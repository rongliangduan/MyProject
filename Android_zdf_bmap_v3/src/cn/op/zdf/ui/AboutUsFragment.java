package cn.op.zdf.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import cn.op.common.AppConfig;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.domain.URLs;
import cn.op.common.util.PhoneUtil;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;

import com.meizu.smartbar.SmartBarUtils;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * 关于我们
 * 
 * @author lufei
 * 
 */
public class AboutUsFragment extends Fragment {
	private AppContext ac;
	MainActivity activity;
	private View pb;
	private View ivNewVersion;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ac = AppContext.getAc();
		activity = (MainActivity) getActivity();

		View view = inflater.inflate(R.layout.frag_about_us, container, false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
//		if (SmartBarUtils.hasSmartBar()) {
//			view.setPadding(0,
//					getResources().getDimensionPixelSize(R.dimen.marginTopAbs),
//					0,
//					getResources().getDimensionPixelSize(R.dimen.marginTopAbs));
//		}

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		pb = view.findViewById(R.id.pb);
		pb.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});

		View layoutRateUs = view.findViewById(R.id.layoutRateUs);
		View layoutFeedback = view.findViewById(R.id.layoutFeedback);
		View layoutStatement = view.findViewById(R.id.layoutStatement);
		View layoutCheckUpdate = view.findViewById(R.id.layoutCheckUpdate);
		ivNewVersion = view.findViewById(R.id.ivNewVersion);

		// TODO debug用
//		View layoutHostDebug = view.findViewById(R.id.layoutHostDebug);
//		final TextView tvCurtHost = (TextView) layoutHostDebug
//				.findViewById(R.id.tvCurtHost);
//		final EditText etTargetHost = (EditText) layoutHostDebug
//				.findViewById(R.id.etTargetHost);
//		View btnChangeHost = layoutHostDebug.findViewById(R.id.btnChangeHost);
//
//		tvCurtHost.setText("当前主机：" + URLs.URL_API_HOST);
//		btnChangeHost.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (UIHelper.isFastDoubleClick(v)) {
//					return;
//				}
//				String host = etTargetHost.getText().toString();
//
//				if (StringUtils.isEmpty(host)) {
//					return;
//				}
//
//				URLs.URL_API_HOST = URLs.HTTP + host + ":7001" + "/";
//				tvCurtHost.setText("当前主机：" + URLs.URL_API_HOST);
//			}
//		});

		layoutRateUs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				UIHelper.showInMarket(activity);
			}
		});

		layoutCheckUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				// TODO
				checkUpdate();
			}
		});

		layoutFeedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				FeedbackAgent agent = new FeedbackAgent(activity);
				agent.startFeedbackActivity();

				// showFragment(FeedbackFragment.class);
			}
		});

		// tvFaq.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (UIHelper.isFastDoubleClick(v)) {
		// return;
		// }
		// showFragment(FaqFragment.class);
		// }
		// });
		layoutStatement.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				showFragment(StatementFragment.class);
			}
		});

		AppConfig appConfig = AppConfig.getAppConfig(activity);
		String versionName = appConfig.get(Keys.VERSION_NEWEST);

		if (!StringUtils.isEmpty(versionName)
				&& !versionName.equals(PhoneUtil.getVersionName(activity))) {
			ivNewVersion.setVisibility(View.VISIBLE);
		}

		// tvGuide.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (UIHelper.isFastDoubleClick(v)) {
		// return;
		// }
		// showFragment(GuideVpFragment.class);
		//
		// }
		// });
		// ivAboutUs.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (UIHelper.isFastDoubleClick(v)) {
		// return;
		// }
		// showFragment(AboutUsFragment.class);
		// }
		// });
		// ivJujiaServices.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (UIHelper.isFastDoubleClick(v)) {
		// return;
		// }
		//
		// UIHelper.call(activity, Constants.TEL_JUJIA);
		// }
		// });

		// btnClear.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (UIHelper.isFastDoubleClick(v)) {
		// return;
		// }
		//
		// activity.showDialogClearCache();
		// }
		// });

	}

	protected void checkUpdate() {
		pb.setVisibility(View.VISIBLE);

		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {

				pb.setVisibility(View.GONE);

				switch (updateStatus) {
				case UpdateStatus.Yes: // has update

					UmengUpdateAgent.showUpdateDialog(activity, updateInfo);
					break;
				case UpdateStatus.No: // has no update

					AppContext.toastShow("当前已是最新版本");
					break;
				case UpdateStatus.NoneWifi: // none wifi

					AppContext.toastShow("没有wifi连接， 只在wifi下更新");
					break;
				case UpdateStatus.Timeout: // time out

					AppContext.toastShow("请重试。");
					break;
				}
			}
		});

		UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {

			@Override
			public void onClick(int status) {
				pb.setVisibility(View.GONE);

				switch (status) {
				case UpdateStatus.Update:

					break;
				case UpdateStatus.Ignore:

					break;
				case UpdateStatus.NotNow:

					break;
				}
			}
		});

		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(activity);
	}

	protected void showFragment(Class<? extends Fragment> clazz) {

		UIHelper.showSimpleFragActivity(activity, clazz);

		if (clazz.getName().equals(GuideVpFragment.class.getName())) {
			activity.isToShowGuideFragment = true;
		} else {
			activity.isToShowGuideFragment = false;
		}

	}
}
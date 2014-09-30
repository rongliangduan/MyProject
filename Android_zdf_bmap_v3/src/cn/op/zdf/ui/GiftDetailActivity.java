package cn.op.zdf.ui;

import java.lang.ref.WeakReference;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import cn.op.common.AppException;
import cn.op.common.UIHelper;
import cn.op.common.constant.Keys;
import cn.op.common.domain.URLs;
import cn.op.common.util.Constants;
import cn.op.common.util.Log;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.domain.Item;
import cn.op.zdf.event.GotoHotelEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.meizu.smartbar.SmartBarUtils;

import de.greenrobot.event.EventBus;

/**
 * 活动详情
 * 
 * @author lufei
 * 
 */
public class GiftDetailActivity extends SherlockFragmentActivity {

	private static final String TAG = Log.makeLogTag(GiftDetailActivity.class);
	private WebView webView;
	private View pb;
	private AppContext ac;
	private Item gift;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
	protected void onCreate(Bundle savedInstanceState) {
		if (SmartBarUtils.hasSmartBar()) {
			getSherlock().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);

			setTheme(R.style.Holo_Theme_CustomAbsOverlay);

			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), true);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gift_detail);

		ac = AppContext.getAc();
		gift = (Item) getIntent().getSerializableExtra(Keys.ITEM);

		if (gift == null) {
			Log.d(TAG, "======未传入活动======");
			AppContext.toastShowException("未传入活动");
		} else {
			initView();
			initData();
		}

	}

	protected static final int WHAT_INIT_DATA = 1;
	protected static final int WHAT_EXCEPTION = -1;

	private void initData() {
		// webView.loadUrl(webUrl);

//		pb.setVisibility(View.VISIBLE);
//		new Thread() {
//			public void run() {
//				Message msg = new Message();
//				try {
//					Item q = null;
//
//					q = ac.getGiftDetail(gift.eventId);
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

	private MyHandler myHandler = new MyHandler(this);
	private TextView tvPbTitle;

	static class MyHandler extends Handler {
		private WeakReference<GiftDetailActivity> mWr;

		public MyHandler(GiftDetailActivity frag) {
			super();
			this.mWr = new WeakReference<GiftDetailActivity>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			GiftDetailActivity frag = mWr.get();

			switch (msg.what) {
			case WHAT_INIT_DATA:
				frag.pb.setVisibility(View.GONE);
				Item q = (Item) msg.obj;

				if (q.rspMsg.OK()) {

					frag.webView.loadDataWithBaseURL(URLs.URL_ZDF_API,
							q.content, "text/html", Constants.CHAR_SET_UTF_8,
							null);

					// frag.webView
					// .loadUrl("file:///android_asset/giftDetail2.html");

				} else {
					AppContext.toastShowException(q.rspMsg.message);
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

	@TargetApi(Build.VERSION_CODES.KITKAT) private void initView() {
		pb = findViewById(R.id.pb);
		tvPbTitle = (TextView) pb.findViewById(R.id.tvPbTitle);
		webView = (WebView) findViewById(R.id.webView1);

		View btnLeft = findViewById(R.id.btnLeft);
		View btnRight = findViewById(R.id.btnRight);
		TextView tvTopBarTitle = (TextView) findViewById(R.id.tvTitle);

		btnRight.setVisibility(View.INVISIBLE);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		if (gift != null) {
			tvTopBarTitle.setText(gift.eventTitle);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(true);
		}

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webSettings.setBuiltInZoomControls(false);
		// webSettings.setDefaultFontSize(8);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setDefaultTextEncodingName(Constants.CHAR_SET_UTF_8);

		webView.addJavascriptInterface(new JavaScriptInterface(this),
				"JSInterface");

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				pb.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				tvPbTitle.setText("努力加载网页……");
				pb.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				System.out.println(url);
				view.loadUrl(url); // 在当前的webview中跳转到新的url
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater supportMenuInflater = getSupportMenuInflater();

		if (SmartBarUtils.hasSmartBar()) {
			UIHelper.makeEmptyMenu(supportMenuInflater, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				finish();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * js调用android代码
	 * 
	 * @author lufei
	 * 
	 */
	private class JavaScriptInterface {

		private Activity activity;

		public JavaScriptInterface(Activity activiy) {
			this.activity = activiy;
		}

		@JavascriptInterface
		public void goToHotel() {
			GotoHotelEvent event = new GotoHotelEvent();
			event.isGoTo = true;
			EventBus.getDefault().post(event);

			activity.finish();
		}
	}
}

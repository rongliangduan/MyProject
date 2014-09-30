package cn.op.zdf.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import cn.op.common.util.Log;
import cn.op.zdf.R;

import com.tendcloud.tenddata.TCAgent;

/**
 * 新手引导
 * 
 * @author lufei
 * 
 */
public class GuideActivity extends FragmentActivity {

	private static final String TAG = Log.makeLogTag(GuideActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

	}

	@Override
	protected void onResume() {
		Log.d(TAG, "======onResume======");
		super.onResume();
		TCAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "======onResume======");
		super.onPause();
		TCAgent.onPause(this);
	}

	@Override
	public void finish() {
		Log.d(TAG, "======finish======");
		super.finish();
		overridePendingTransition(0,
				R.anim.slide_out_to_top);

	}

}

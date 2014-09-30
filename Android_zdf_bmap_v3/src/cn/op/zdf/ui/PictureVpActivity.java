package cn.op.zdf.ui;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import cn.op.common.constant.Keys;
import cn.op.common.util.Log;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;

import com.tendcloud.tenddata.TCAgent;

/**
 * 酒店图片Vp大图显示
 * 
 * @author lufei
 * 
 */
public class PictureVpActivity extends Activity {

	private static final String TAG = Log.makeLogTag(PictureVpActivity.class);
	private ViewPager mViewPager;
	private LayoutInflater inflater;
	private AppContext ac;
	private TextView tvPicNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_vp);
		inflater = LayoutInflater.from(getApplicationContext());
		ac = AppContext.getAc();

		int index = getIntent().getIntExtra(Keys.INDEX, 0);
		String[] imgs = getIntent().getStringArrayExtra(Keys.IMGS);

		tvPicNum = (TextView) findViewById(R.id.tvPicNum);
		mViewPager = (ViewPager) findViewById(R.id.viewPager1);
		final SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(imgs);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(index);

		tvPicNum.setText("" + (index + 1) + "/" + pagerAdapter.getCount());

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
				tvPicNum.setText("" + (index + 1) + "/"
						+ pagerAdapter.getCount());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

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

	private class SamplePagerAdapter extends PagerAdapter {

		private String[] data = {};

		public SamplePagerAdapter(String[] data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.length;
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
		public View instantiateItem(ViewGroup container, int position) {
			View view = inflater.inflate(R.layout.picture_vp_item, null);

			PhotoView ivPic = (PhotoView) view.findViewById(R.id.imageView1);

			ac.mImageLoader.displayImage(data[position], ivPic,
					ac.options_largePic);
			// ac.mImageLoader.displayImage(URLs.URL_ZDF_API+data[position],
			// ivPic, ac.options);

			ivPic.setOnViewTapListener(new OnViewTapListener() {
				@Override
				public void onViewTap(View arg0, float arg1, float arg2) {
					finish();
				}
			});

			container.addView(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			return view;
		}

	}

}

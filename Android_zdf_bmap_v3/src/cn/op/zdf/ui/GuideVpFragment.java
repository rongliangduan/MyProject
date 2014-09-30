package cn.op.zdf.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import cn.op.common.util.Log;
import cn.op.zdf.R;

import com.viewpagerindicator.CirclePageIndicator;

/**
 * 新手引导，ViewPager形式
 * 
 * @author lufei
 * 
 */
public class GuideVpFragment extends Fragment {

	protected static final String TAG = Log.makeLogTag(GuideVpFragment.class);
	private LayoutInflater inflater;
	private ViewPager vp;
	private FragmentActivity activity;
	private CirclePageIndicator mIndicator;
	// private int[] pageColor;
	private int[] pageImgBg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.inflater = inflater;
		View view = inflater.inflate(R.layout.frag_guide_vp, container, false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		return view;
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		activity = getActivity();

		vp = (ViewPager) view.findViewById(R.id.viewPager1);

		pageImgBg = new int[] { R.drawable.pic_guide_bg_0,
				R.drawable.pic_guide_bg_1, R.drawable.pic_guide_bg_2,
				R.drawable.pic_guide_bg_3 };

		int[] pageImg = new int[] { R.drawable.pic_guide_0,
				R.drawable.pic_guide_1, R.drawable.pic_guide_2,
				R.drawable.pic_guide_3 };

		// Resources resources = getResources();

		// pageColor = new int[] { resources.getColor(R.color.guide_bg0),
		// resources.getColor(R.color.guide_bg1),
		// resources.getColor(R.color.guide_bg2) };

		vp.setAdapter(new SamplePagerAdapter(pageImg));

		mIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
		mIndicator.setViewPager(vp);

	};

	private class SamplePagerAdapter extends PagerAdapter {

		private int[] data = new int[] {};

		public SamplePagerAdapter(int[] data) {
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
		public View instantiateItem(ViewGroup container, final int position) {
			View view = inflater.inflate(R.layout.guide_vp_item, null);

			// view.setBackgroundColor(pageColor[position]);

			ImageView ivPicBg = (ImageView) view.findViewById(R.id.imageView1);
			ImageView ivPic = (ImageView) view.findViewById(R.id.imageView2);
			View btnEnter = view.findViewById(R.id.btnEnter);

			ivPic.setImageResource(data[position]);
			ivPicBg.setImageResource(pageImgBg[position]);

			if (position == data.length - 1) {
				btnEnter.setVisibility(View.VISIBLE);
				btnEnter.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (activity instanceof GuideActivity) {
							Intent intent = new Intent(activity
									.getApplicationContext(),
									MainActivity.class);
							startActivity(intent);
							activity.finish();
						} else {
							activity.finish();
						}
					}
				});
			} else {
				btnEnter.setVisibility(View.GONE);
			}

			container.addView(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			return view;
		}

	}
}

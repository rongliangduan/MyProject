package cn.op.zdf.ui;

import java.util.List;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.op.common.BaseAdapter;
import cn.op.common.UIHelper;
import cn.op.common.util.Log;
import cn.op.zdf.R;
import cn.op.zdf.domain.Faq;

/**
 * 常见问题
 * 
 * @author lufei
 * 
 */
public class FaqFragment extends BaseFragment {
	protected static final String TAG = Log.makeLogTag(FaqFragment.class);

	private FragmentActivity activity;
	private LayoutInflater inflater;
	private ListView lv;

	private Point size;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.inflater = inflater;
		View view = inflater.inflate(R.layout.frag_faq, container, false);
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

		tvTopBarTitle.setText("常见问题");

		btnRight.setVisibility(View.INVISIBLE);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				activity.finish();
			}
		});

		lv = (ListView) view.findViewById(R.id.listView1);

		LvAdapter adapter = new LvAdapter(Faq.parseDemo());
		lv.setAdapter(adapter);
		lv.setVerticalFadingEdgeEnabled(false);
	};

	private class LvAdapter extends BaseAdapter<Faq> {

		public LvAdapter(List<Faq> data) {
			super(data);
		}

		class ViewHolder {

			public TextView tvQuestion;
			public TextView tvAnswer;
			public ImageView ivIconArrow;
			public View layoutAnswer;

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.faq_lv_item, null);
				convertView.setTag(holder);

				holder.tvQuestion = (TextView) convertView
						.findViewById(R.id.textView1);
				holder.ivIconArrow = (ImageView) convertView
						.findViewById(R.id.imageView1);
				holder.layoutAnswer = convertView.findViewById(R.id.layout2);
				holder.tvAnswer = (TextView) convertView
						.findViewById(R.id.textView2);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Faq faq = getItem(position);

			holder.tvQuestion.setText(faq.question);
			holder.tvAnswer.setText(faq.answer);


			if (faq.isAnswerShow) {
				holder.layoutAnswer.setVisibility(View.VISIBLE);
				holder.ivIconArrow
						.setImageResource(R.drawable.ic_arrow_down_setting);
			} else {
				holder.layoutAnswer.setVisibility(View.GONE);
				holder.ivIconArrow
						.setImageResource(R.drawable.ic_arrow_right_setting);
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (holder.layoutAnswer.getVisibility() == View.VISIBLE) {
						faq.isAnswerShow = false;

						holder.layoutAnswer.setVisibility(View.GONE);
						holder.ivIconArrow
								.setImageResource(R.drawable.ic_arrow_right_setting);
					} else {
						faq.isAnswerShow = true;

						holder.layoutAnswer.setVisibility(View.VISIBLE);
						holder.ivIconArrow
								.setImageResource(R.drawable.ic_arrow_down_setting);

						holder.layoutAnswer.post(new Runnable() {

							@Override
							public void run() {

								Log.d(TAG, "======Height======"
										+ holder.layoutAnswer.getHeight());

								lv.smoothScrollToPosition(position + 1);
								// lv.scrollBy(0,
								// holder.layoutAnswer.getHeight());
							}
						});

						// lv.smoothScrollToPosition(position+1);

					}
				}
			});

			return convertView;
		}
	}
}

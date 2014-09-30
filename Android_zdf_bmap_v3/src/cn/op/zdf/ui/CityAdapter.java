package cn.op.zdf.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.op.common.BaseAdapter;
import cn.op.common.util.DisplayUtil;
import cn.op.zdf.R;
import cn.op.zdf.domain.City;

/**
 * 城市选择列表，包含了按字母分类功能
 * 
 * @author lufei
 * 
 */
public class CityAdapter extends BaseAdapter<City> implements SectionIndexer {
	private Context context;
	private int tvSizeFirstHead;
	private int tvSizeHead;
	private int tvColorFirstHead;
	private int tvColorHead;

	public CityAdapter(Context _context, ArrayList<City> list) {
		super(list);
		context = _context;

		Resources resources = context.getResources();

		float dimension = resources.getDimension(R.dimen.textSize_4);

		tvSizeFirstHead = resources.getDimensionPixelSize(R.dimen.textSize_4);
		tvSizeHead = resources.getDimensionPixelSize(R.dimen.textSize_0);

		tvColorFirstHead = resources.getColor(R.color.gray_order_pay_tv2);
		tvColorHead = resources.getColor(R.color.black_tv_zdf);
	}

	class ViewHolder {

		public LinearLayout header;
		public TextView nameText;
		public TextView headerText;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			LayoutInflater inflate = LayoutInflater.from(context);
			convertView = (View) inflate.inflate(R.layout.city_lv_item, null);

			holder.header = (LinearLayout) convertView
					.findViewById(R.id.section);
			holder.nameText = (TextView) convertView.findViewById(R.id.tv_name);
			holder.headerText = (TextView) convertView
					.findViewById(R.id.tv_header);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		City item = getItem(position);

		String name = item.cityName;
		String label = item.cityNamePy;

		if (label != null) {
			char firstChar = label.toUpperCase().charAt(0);
			if (position == 0) {
				holder.headerText.setTextColor(tvColorFirstHead);
				holder.headerText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						tvSizeFirstHead);

				if ("热".equals("" + firstChar) || "常".equals("" + firstChar)) {
					holder.headerText.setText("" + label);
				} else {
					holder.headerText.setText("" + firstChar);
				}
				holder.header.setVisibility(View.VISIBLE);

			} else {
				String preLabel = data.get(position - 1).cityNamePy;
				char preFirstChar = preLabel.toUpperCase().charAt(0);
				if (firstChar != preFirstChar) {
					holder.headerText.setTextColor(tvColorHead);
					holder.headerText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
							tvSizeHead);

					if ("热".equals("" + firstChar)
							|| "常".equals("" + firstChar)) {
						holder.headerText.setText("" + label);
					} else {
						holder.headerText.setText("" + firstChar);
					}
					holder.header.setVisibility(View.VISIBLE);
				} else {
					holder.header.setVisibility(View.GONE);
				}
			}
		}

		holder.nameText.setText(name);
		return convertView;
	}

	public int getPositionForSection(int section) {
		if (section == 35) {
			return 0;
		}
		for (int i = 0; i < data.size(); i++) {
			String lnamePy = data.get(i).cityNamePy;
			if (lnamePy != null) {
				char firstChar = lnamePy.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
		}
		return -1;
	}

	public int getSectionForPosition(int arg0) {
		return 0;
	}

	public Object[] getSections() {
		return null;
	}
}
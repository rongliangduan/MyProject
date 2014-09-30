package cn.op.zdf;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.op.common.util.StringUtils;
import cn.op.zdf.domain.Item;
import cn.op.zdf.domain.Room;

public class HotelUtil {

	public static void listSortBy(final int orderBy, List<Item> list,
			final int saleType) {
		Collections.sort(list, new Comparator<Item>() {
			@Override
			public int compare(Item lhs, Item rhs) {
				switch (orderBy) {
				case Item.ORDER_BY_PRICE:

					int price1 = 0;
					int price2 = 0;
					if (saleType == Room.SALE_TYPE_ZDF) {
						price1 = StringUtils.toInt(lhs.hourroomPrice);
						price2 = StringUtils.toInt(rhs.hourroomPrice);

					} else if (saleType == Room.SALE_TYPE_WYF) {
						price1 = StringUtils.toInt(lhs.dayroomPrice);
						price2 = StringUtils.toInt(rhs.dayroomPrice);
					}

					return price1 - price2;
				case Item.ORDER_BY_DISTANCE:
					float dist1 = StringUtils.toFloat(lhs.dist);
					float dist2 = StringUtils.toFloat(rhs.dist);

					return Float.compare(dist1, dist2);
				}
				return 0;
			}
		});
	}

}

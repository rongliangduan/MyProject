package cn.op.zdf.event;

/**
 * 搜索结果过滤
 * 
 * @author lufei
 * 
 */
public class FilteSearchHotelEvent extends Event {

	public float distMin;
	public float distMax;
	public int priceMin;
	public int priceMax;
	/**
	 * 是否执行过滤搜索
	 */
	public boolean isFilterSearch;
	/**
	 * 是否在进行重置搜索
	 */
	public boolean isResetFilterSearch;

}

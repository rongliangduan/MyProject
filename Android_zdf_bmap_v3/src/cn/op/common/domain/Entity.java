package cn.op.common.domain;

import java.text.SimpleDateFormat;

/**
 * 实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public abstract class Entity extends Base {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static SimpleDateFormat SDF_IN = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat SDF_OUT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public RspMsg rspMsg = new RspMsg();

	protected String cacheKey;

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public RspMsg getRspMsg() {
		return rspMsg;
	}

	public void setRspMsg(RspMsg rspMsg) {
		this.rspMsg = rspMsg;
	}
}

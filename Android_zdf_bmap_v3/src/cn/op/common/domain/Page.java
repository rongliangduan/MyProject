package cn.op.common.domain;

import java.util.ArrayList;
import java.util.List;

public class Page<T> extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<T> list = new ArrayList<T>();

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}

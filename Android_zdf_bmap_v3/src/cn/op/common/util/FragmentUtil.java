package cn.op.common.util;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentUtil {
	private FragmentManager fm;
	private ArrayList<Fragment> framentList = new ArrayList<Fragment>();

	public FragmentUtil(FragmentManager fm) {
		this.fm = fm;

	}

	/**
	 * 添加要管理的fragment，如果已经添加过，则不再添加
	 * 
	 * @param id
	 * @param fragment
	 */
	public void addFragment(int id, Fragment fragment,String tag) {
		addFragment(id, fragment, tag,null, null);
	}

	/**
	 * 添加要管理的fragment并显示，如果已经添加过，则不再添加
	 * 
	 * @param id
	 * @param fragment
	 * @param tag
	 * @param animIn
	 * @param animOut
	 */
	public void addFragment(int id, Fragment fragment, String tag,
			Integer animIn, Integer animOut) {
		boolean detached = fragment.isDetached();
		if (!fragment.isAdded()) {
			FragmentTransaction ft = fm.beginTransaction();
			if (animIn != null && animOut != null) {
				ft.setCustomAnimations(animIn, animOut);
			}
			ft.add(id, fragment, tag);
			hideOther(fragment, ft);

			ft.commit();
			fm.executePendingTransactions();

			framentList.add(fragment);
		} else {
			showAndHideOther(fragment, animIn, animOut);
		}
	}

	/**
	 * 添加要管理的fragment并显示，如果已经添加过，则不再添加
	 * 
	 * @param id
	 * @param fragment
	 * @param animIn
	 * @param animOut
	 */
	public void replaceFragment(int id, Fragment fragment, Integer animIn,
			Integer animOut) {

		replaceFragment(id, fragment, null, animIn, animOut);
	}

	/**
	 * 添加要管理的fragment并显示，如果已经添加过，则不再添加
	 * 
	 * @param id
	 * @param fragment
	 * @param tag
	 * @param animIn
	 * @param animOut
	 */
	public void replaceFragment(int id, Fragment fragment, String tag,
			Integer animIn, Integer animOut) {
		FragmentTransaction ft = fm.beginTransaction();
		if (animIn != null && animOut != null) {
			ft.setCustomAnimations(animIn, animOut);
		}
		ft.replace(id, fragment, tag);
		ft.commit();
	}

	/**
	 * 显示fragment，在此之前确保调用了addFragmentsInActivity(int id, Fragment fragment)
	 * 
	 * @param fragment
	 */
	public void show(Fragment fragment) {
		showAndHideOther(fragment, null, null);
	}

	public void showOnly(Fragment fragment) {
		showOnly(fragment, null, null);
	}

	/**
	 * 显示fragment，在此之前确保调用了addFragmentsInActivity()
	 * 
	 * @param fragment
	 * @param animIn
	 * @param animOut
	 */
	public void showOnly(Fragment fragment, Integer animIn, Integer animOut) {
		FragmentTransaction ft = fm.beginTransaction();
		// 动画
		if (animIn != null && animOut != null) {
			ft.setCustomAnimations(animIn, animOut);
		}
		ft.show(fragment);

		ft.commit();
	}

	/**
	 * 显示fragment，在此之前确保调用了addFragmentsInActivity()
	 * 
	 * @param fragment
	 * @param animIn
	 * @param animOut
	 */
	private void showAndHideOther(Fragment fragment, Integer animIn,
			Integer animOut) {
		FragmentTransaction ft = fm.beginTransaction();
		// 动画
		if (animIn != null && animOut != null) {
			ft.setCustomAnimations(animIn, animOut);
		}
		ft.show(fragment);
		hideOther(fragment, ft);

		ft.commit();
	}

	private void hideOther(Fragment fragment, FragmentTransaction ft) {
		for (int i = 0; i < framentList.size(); i++) {
			if (framentList.get(i) != fragment) {
				ft.hide(framentList.get(i));
			}
		}
	}

	public void hide(Fragment fragment) {
		hide(fragment, null, null);
	}

	public void hide(Fragment fragment, Integer animIn, Integer animOut) {
		FragmentTransaction ft = fm.beginTransaction();
		// 动画
		if (animIn != null && animOut != null) {
			ft.setCustomAnimations(animIn, animOut);
		}
		ft.hide(fragment);
		ft.commit();
	}

	/**
	 * 移除，会触发fragment的onPause生命周期
	 * 
	 * @param fragment
	 * @param animIn
	 * @param animOut
	 */
	public void remove(Fragment fragment, Integer animIn, Integer animOut) {
		FragmentTransaction ft = fm.beginTransaction();
		// 动画
		if (animIn != null && animOut != null) {
			ft.setCustomAnimations(animIn, animOut);
		}
		ft.remove(fragment);
		ft.commit();

		framentList.remove(fragment);
	}

	public void removeAll(Integer animIn, Integer animOut) {
		FragmentTransaction ft = fm.beginTransaction();
		// 动画
		if (animIn != null && animOut != null) {
			ft.setCustomAnimations(animIn, animOut);
		}
		for (int i = 0; i < framentList.size(); i++) {
			ft.remove(framentList.get(i));
		}
		ft.commit();
		framentList.clear();
	}

	public void addToBackStack(int containId, Fragment fragment, String tag,
			int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
		FragmentTransaction ft = fm.beginTransaction();

		ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);

		// ft.replace(containId, fragment, tag);
		ft.add(containId, fragment, tag);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(tag);
		ft.commit();
	}
}
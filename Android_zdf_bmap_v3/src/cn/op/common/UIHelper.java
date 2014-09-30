package cn.op.common;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.op.common.constant.Keys;
import cn.op.common.domain.RspMsg;
import cn.op.common.util.AnimationUtil;
import cn.op.common.util.Constants;
import cn.op.common.util.Log;
import cn.op.common.util.PhoneUtil;
import cn.op.common.util.StringUtils;
import cn.op.zdf.AppContext;
import cn.op.zdf.R;
import cn.op.zdf.UserCenter;
import cn.op.zdf.domain.Item;
import cn.op.zdf.ui.BMapRouteActivity;
import cn.op.zdf.ui.CityChooseActivity;
import cn.op.zdf.ui.GiftDetailActivity;
import cn.op.zdf.ui.HotelDetailActivity;
import cn.op.zdf.ui.LoginActivity;
import cn.op.zdf.ui.LvHotelActivity;
import cn.op.zdf.ui.MainActivity;
import cn.op.zdf.ui.PayOnlineActivity;
import cn.op.zdf.ui.SimpleFragActivity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.SearchAutoComplete;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class UIHelper {

	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;
	public static final int LISTVIEW_ACTION_DEFAULT = LISTVIEW_ACTION_REFRESH;

	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;

	public final static int REQUEST_CODE_FOR_RESULT = 0x01;
	public final static int REQUEST_CODE_FOR_REPLY = 0x02;

	public static final int REQUEST_CODE_PUB_REMARK = 1;
	private static final String TAG = Log.makeLogTag(UIHelper.class);

	private static long lastClickTime;
	private static Object lastClickView;

	/**
	 * 是否快速连续点击
	 * 
	 * @return true 连续两次点击间隔小于300ms
	 */
	public static boolean isFastDoubleClick(Object v) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		Log.d(TAG, "timeD = " + timeD);
		if (0 < timeD && timeD < 300 && lastClickView == v) {
			return true;
		}
		lastClickTime = time;
		lastClickView = v;
		return false;
	}

	/**
	 * 退出程序
	 * 
	 * @param cont
	 */
	public static void Exit(final Activity cont) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_menu_surelogout);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.show();
	}

	/**
	 * 退出程序，同时执行响应操作
	 * 
	 * @param cont
	 */
	public static void ExitWhitHandler(final Activity cont) {
		final AppContext ac = AppContext.getAc();

		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_menu_surelogout);

		String[] itemsTemp;
		boolean[] checkedItemsTemp;
		// if (ac.isLogin()) {
		// itemsTemp = new String[] { "清除用户记录", "彻底删除账户" };
		// checkedItemsTemp = new boolean[] { false, false };
		// } else {
		// itemsTemp = new String[] { "清除用户记录" };
		// checkedItemsTemp = new boolean[] { false };
		// }

		itemsTemp = new String[] { "清除用户记录" };
		checkedItemsTemp = new boolean[] { false };

		CharSequence[] items = itemsTemp;
		final boolean[] checkedItems = checkedItemsTemp;

		builder.setMultiChoiceItems(items, checkedItems,
				new OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						checkedItems[which] = isChecked;

						if (which == 1 && isChecked) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									cont);
							builder.setIcon(android.R.drawable.ic_dialog_alert);
							builder.setTitle("警告");
							builder.setMessage("彻底删除账户将清除服务器上包括订单记录在内的所有此用户信息，并且不可恢复");

							builder.setPositiveButton(R.string.sure,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});
							builder.show();
						}
					}
				});

		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// if (ac.isLogin()) {
						// String userId = ac.user.id;
						//
						// if (checkedItems[0]) {
						// UserCenter.clearUserRecord(cont);
						// }
						//
						// if (checkedItems[1]) {
						// // 彻底删除账户
						// UserCenter.clearUserRecord(cont);
						// AppConfig.getAppConfig(cont).remove(
						// Keys.IS_FIRST_ENTER);
						//
						// deleteUser(dialog, userId);
						// } else {
						// dialog.dismiss();
						// // 退出应用
						// AppManager.getAppManager().AppExit(cont);
						// }
						// } else {
						// if (checkedItems[0]) {
						// UserCenter.clearUserRecord(cont);
						// }
						//
						// dialog.dismiss();
						// // 退出应用
						// AppManager.getAppManager().AppExit(cont);
						// }

						if (checkedItems[0]) {
							UserCenter.clearUserRecord(cont);
						}

						dialog.dismiss();
						// 退出应用
						AppManager.getAppManager().AppExit(cont);
					}

					/**
					 * 请求服务器彻底删除账户
					 * 
					 * @param dialog
					 * @param userId
					 */
					private void deleteUser(final DialogInterface dialog,
							final String userId) {
						final ProgressDialog pb = ProgressDialog.show(cont,
								"请稍等", "正在删除用户");

						final Handler myHandler = new Handler() {
							public void handleMessage(Message msg) {
								switch (msg.what) {
								case 1:
									pb.dismiss();

									RspMsg rsp = (RspMsg) msg.obj;
									if (rsp.OK()) {
										dialog.dismiss();
										// 退出应用
										AppManager.getAppManager()
												.AppExit(cont);
									} else {
										AppContext
												.toastShowException(rsp.message);
									}

									break;
								case -1:
									pb.dismiss();
									((AppException) msg.obj).makeToast(ac);
									break;
								}
							};
						};

						new Thread() {
							Message msg = new Message();

							@Override
							public void run() {
								try {
									// 彻底删除账户
									RspMsg r = ac.deleteUser(userId);

									msg.what = 1;
									msg.obj = r;
								} catch (AppException e) {
									e.printStackTrace();
									msg.what = -1;
									msg.obj = e;
								}
								myHandler.sendMessage(msg);
							}
						}.start();
					}
				});

		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.show();

	}

	/**
	 * 调用系统安装了的应用分享
	 * 
	 * @param context
	 * @param title
	 *            文本
	 * @param url
	 *            图片url
	 * @param imgPath
	 *            图片本地路径
	 */
	public static void showShareMore(Activity context, final String title,
			final String url, String imgPath) {

		String content = title;
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);

		if (!(imgPath == null) && !"".equals(imgPath)) { // 有本地图片，怎分享图片
			intent.putExtra(Intent.EXTRA_STREAM,
					Uri.fromFile(new File(imgPath)));
		} else { // 无本地图片，则将图片url添到文本后面
			content = title + " " + url;
		}

		intent.putExtra(Intent.EXTRA_TEXT, content);

		context.startActivity(Intent.createChooser(intent, "选择分享"));
	}

	/**
	 * 打电话
	 * 
	 * @param tel
	 *            纯数字号码
	 */
	public static void call(Activity context, String tel) {
		// 先打开拨号界面
		Uri uri = Uri.parse("tel:" + tel);
		Intent intent = new Intent(Intent.ACTION_DIAL, uri);
		context.startActivity(intent);

		// 直接拨打
		// Uri uri = Uri.parse("tel:"+ "4006311234");
		// intent = new Intent(Intent.ACTION_CALL,uri);
		// startActivity(intent);
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param context
	 * @param elseView
	 *            任意view
	 * @return 隐藏软键盘前软键盘是否激活
	 */
	public static void hideSoftInput(Context context, View elseView) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (elseView != null) {
			imm.hideSoftInputFromWindow(elseView.getWindowToken(), 0);
		}
	}

	/**
	 * 未实现，目前采用在xml中配置属性实现
	 * </br>android:digits="abcdefghijklmnopqrstuvwxyz1234567890" 只能输入这些字符
	 * </br>android:maxLength="20"
	 * 
	 * @param etPsw
	 */
	public static void limitPswEditTextInput(final EditText etPsw) {
		etPsw.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable edt) {
				String str = etPsw.getText().toString();
				if (str.length() >= 6) {
					etPsw.setImeActionLabel("登录", KeyEvent.KEYCODE_ENTER);
					etPsw.setImeOptions(EditorInfo.IME_ACTION_DONE);
					etPsw.setOnEditorActionListener(new OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {

							return false;
						}
					});
				}
			}
		});
	}

	/**
	 * 对EditText输入手机号做限制，只能输入11位数字，不能输入特殊符号，第一位必须是1
	 * 
	 * @param etPhone
	 *            用来输入手机号的EditText，最好在xml中为其配置了android:inputType="phone"
	 */
	public static void limitPhoneEditTextInput(final EditText etPhone) {
		etPhone.addTextChangedListener(new TextWatcher() {
			int mMaxLenth = 11;
			private int cou = 0;
			int selectionEnd = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				cou = before + count;
				String editable = etPhone.getText().toString();
				String str = StringUtils.stringFilter(editable);

				if (!editable.equals(str)) {
					etPhone.setText(str);
				}

				etPhone.setSelection(etPhone.length());
				cou = etPhone.length();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (cou == 1) {
					if (!etPhone.getText().toString().equals("1")) {
						selectionEnd = etPhone.getSelectionEnd();
						s.delete(0, selectionEnd);
					}
				}

				if (cou > mMaxLenth) {
					selectionEnd = etPhone.getSelectionEnd();
					s.delete(mMaxLenth, selectionEnd);

					// if (Build.VERSION.SDK_INT >=
					// Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					etPhone.setText(s.toString());
					// }
				}
			}
		});
	}

	public static int mMaxLenth = 5;

	/**
	 * 限制只能输入最多5个中文汉字
	 * 
	 * @param tvReserveLiveMan
	 */
	public static void limitChineseEditTextInput(final EditText etChinese) {

		etChinese.addTextChangedListener(new TextWatcher() {

			private int cou = 0;
			int selectionEnd = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				Log.d(TAG, "======onTextChanged======");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.d(TAG, "======beforeTextChanged======");
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.d(TAG, "======afterTextChanged======");

				String editable = s.toString();
				if (editable.matches("[a-zA-Z]*")) {
					// 有的输入法在输入汉字时，会先将拼音传入，此时不处理
					return;
				}

				// String editable = etChinese.getText().toString();
				cou = editable.length();

				String str = StringUtils.stringFilterChinese(editable);

				if (!editable.equals(str)) {
					etChinese.setText(str);
				}

				etChinese.setSelection(etChinese.length());
				cou = etChinese.length();

				if (cou > mMaxLenth) {

					selectionEnd = etChinese.getSelectionEnd();
					s.delete(mMaxLenth, selectionEnd);

					// if (Build.VERSION.SDK_INT >=
					// Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					etChinese.setText(s.toString());
					// }
				}
			}
		});

		etChinese.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean arg1) {

				String string = etChinese.getText().toString();

				if (!StringUtils.isEmpty(string)) {
					string = StringUtils.stringFilterChinese(string);

					if (string.length() > mMaxLenth) {
						string = string.substring(0, mMaxLenth);
					}
				}

				etChinese.setText(string);
			}
		});

		etChinese.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				switch (actionId) {
				case EditorInfo.IME_ACTION_DONE:
					Log.d(TAG, "======onEditorAction====== IME_ACTION_DONE");

					break;
				case EditorInfo.IME_ACTION_GO:
					Log.d(TAG, "======onEditorAction====== IME_ACTION_GO");
					break;
				case EditorInfo.IME_ACTION_NEXT:
					Log.d(TAG, "======onEditorAction====== IME_ACTION_NEXT");

					String string = etChinese.getText().toString();

					if (!StringUtils.isEmpty(string)) {
						string = StringUtils.stringFilterChinese(string);

						if (string.length() > 4) {
							string = string.substring(0, 4);
						}
					}

					etChinese.setText(string);

					break;
				case EditorInfo.IME_ACTION_NONE:
					Log.d(TAG, "======onEditorAction====== IME_ACTION_NONE");
					break;
				case EditorInfo.IME_ACTION_PREVIOUS:
					Log.d(TAG, "======onEditorAction====== IME_ACTION_PREVIOUS");
					break;
				case EditorInfo.IME_ACTION_SEARCH:
					Log.d(TAG, "======onEditorAction====== IME_ACTION_SEARCH");
					break;
				case EditorInfo.IME_ACTION_SEND:
					Log.d(TAG, "======onEditorAction====== IME_ACTION_SEND");
					break;
				case EditorInfo.IME_ACTION_UNSPECIFIED:
					Log.d(TAG,
							"======onEditorAction====== IME_ACTION_UNSPECIFIED");
					break;

				default:
					Log.d(TAG, "======onEditorAction====== ");
					break;
				}

				return false;
			}
		});
	}

	public static void showLoginActivity(Activity activity) {
		Intent intent = new Intent(activity, LoginActivity.class);
		activity.startActivity(intent);
	}

	public static void showHotelActivity(Activity activity, Item item) {
		Intent intent = new Intent(activity, HotelDetailActivity.class);
		intent.putExtra(Keys.ITEM, item);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.slide_in_from_right,
				R.anim.slide_out_to_left);
	}

	public static void showLvHotel(MainActivity activity) {
		Intent intent = new Intent(activity, LvHotelActivity.class);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.slide_in_from_right,
				R.anim.slide_out_to_left);
	}

	public static void closeSearchView(SearchView mSearchView,
			SearchAutoComplete mSearchViewTextView) {
		if (mSearchView != null && !mSearchView.isIconified()) {
			mSearchViewTextView.setText("");
			mSearchView.setIconified(true);
		}
	}

	public static void openSearchView(SearchView mSearchView,
			SearchAutoComplete mSearchViewTextView) {
		if (mSearchView != null && mSearchView.isIconified()) {
			mSearchViewTextView.setText("");
			mSearchView.setIconified(false);
		}
	}

	/**
	 * 显示酒店地图导航
	 * 
	 * @param activity
	 * @param hotel
	 *            酒店，需要至少包含经纬度、名称、地址
	 */
	public static void showPathNav(Activity activity, Item hotel) {
		// TODO 不提供跨市导航
		// AppContext ac = AppContext.getAc();

		// if (ac.lastLocCity != null
		// && ac.lastChooseCity != null
		// && !ac.lastLocCity.cityName
		// .endsWith(ac.lastChooseCity.cityName)) {
		//
		// AppContext.toastShow("抱歉，赞不支持跨市导航");
		// return;
		// }

		Intent intent = new Intent(activity, BMapRouteActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		intent.putExtra(Keys.ITEM, hotel);
		activity.startActivity(intent);
		// activity.overridePendingTransition(R.anim.slide_in_from_right,
		// R.anim.slide_out_to_left);
	}

	public static void showDialogExit(final MainActivity cont,
			final View layoutDialogExit, final View ivMask) {

		if (layoutDialogExit.getVisibility() == View.VISIBLE) {
			AnimationUtil.animationShowHideAlpha(cont, false, ivMask);
			AnimationUtil.animationShowSifbHideSotb(cont, false,
					layoutDialogExit);
			return;
		} else {
			AnimationUtil.animationShowHideAlpha(cont, true, ivMask);
			AnimationUtil.animationShowSifbHideSotb(cont, true,
					layoutDialogExit);
		}

		final CheckBox cbClearCache = (CheckBox) layoutDialogExit
				.findViewById(R.id.checkBox1);
		View btnOk = layoutDialogExit.findViewById(R.id.button2);
		View btnCancel = layoutDialogExit.findViewById(R.id.button1);

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				if (cbClearCache.isChecked()) {
					UserCenter.clearUserRecord(cont);
				}

				// 退出应用
				AppManager.getAppManager().AppExit(cont);

			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				AnimationUtil.animationShowHideAlpha(cont, false, ivMask);
				AnimationUtil.animationShowSifbHideSotb(cont, false,
						layoutDialogExit);
				cont.supportInvalidateOptionsMenu();
			}
		});

	}

	public static void showDialogDeleteUser(final MainActivity activity,
			final View layoutDialogDeleteUser, final View ivMask) {
		if (layoutDialogDeleteUser.getVisibility() == View.VISIBLE) {
			AnimationUtil.animationShowHideAlpha(activity, false, ivMask);
			AnimationUtil.animationShowSifbHideSotb(activity, false,
					layoutDialogDeleteUser);
			return;
		} else {
			AnimationUtil.animationShowHideAlpha(activity, true, ivMask);
			AnimationUtil.animationShowSifbHideSotb(activity, true,
					layoutDialogDeleteUser);
		}

		View btnOk = layoutDialogDeleteUser.findViewById(R.id.button2);
		View btnCancel = layoutDialogDeleteUser.findViewById(R.id.button1);

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				deleteUser();
			}

			/**
			 * 请求服务器彻底删除账户
			 * 
			 * @param dialog
			 * @param userId
			 */
			private void deleteUser() {
				final String userId = activity.ac.getLoginUserId();
				final AppContext ac = activity.ac;

				final ProgressDialog pb = ProgressDialog.show(activity, "请稍等",
						"正在删除用户资料");

				final Handler myHandler = new Handler() {
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case 1:
							pb.dismiss();

							RspMsg rsp = (RspMsg) msg.obj;
							if (rsp.OK()) {
								// 彻底删除账户
								UserCenter.clearUserRecord(activity);
								AppConfig.getAppConfig(activity).remove(
										Keys.IS_FIRST_ENTER);

								AnimationUtil.animationShowHideAlpha(activity,
										false, ivMask);
								AnimationUtil
										.animationShowSifbHideSotb(activity,
												false, layoutDialogDeleteUser);

								activity.menus[0].performClick();
								activity.tv8IsLogin.setText("未登录");
							} else {
								AppContext.toastShowException(rsp.message);
							}

							break;
						case -1:
							pb.dismiss();
							((AppException) msg.obj).makeToast(ac);
							break;
						}
					};
				};

				new Thread() {
					Message msg = new Message();

					@Override
					public void run() {
						try {
							// 彻底删除账户
							RspMsg r = ac.deleteUser(userId);

							msg.what = 1;
							msg.obj = r;
						} catch (AppException e) {
							e.printStackTrace();
							msg.what = -1;
							msg.obj = e;
						}
						myHandler.sendMessage(msg);
					}
				}.start();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				AnimationUtil.animationShowHideAlpha(activity, false, ivMask);
				AnimationUtil.animationShowSifbHideSotb(activity, false,
						layoutDialogDeleteUser);

			}
		});
	}

	public static void showDialogClearCache(final MainActivity cont,
			final View layoutDialogClearCache, final View ivMask) {
		if (layoutDialogClearCache.getVisibility() == View.VISIBLE) {
			AnimationUtil.animationShowHideAlpha(cont, false, ivMask);
			AnimationUtil.animationShowSifbHideSotb(cont, false,
					layoutDialogClearCache);
			return;
		} else {
			AnimationUtil.animationShowHideAlpha(cont, true, ivMask);
			AnimationUtil.animationShowSifbHideSotb(cont, true,
					layoutDialogClearCache);
		}

		View btnOk = layoutDialogClearCache.findViewById(R.id.button2);
		View btnCancel = layoutDialogClearCache.findViewById(R.id.button1);

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				UserCenter.clearBrowseRecord(cont.ac);

				AnimationUtil.animationShowHideAlpha(cont, false, ivMask);
				AnimationUtil.animationShowSifbHideSotb(cont, false,
						layoutDialogClearCache);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				AnimationUtil.animationShowHideAlpha(cont, false, ivMask);
				AnimationUtil.animationShowSifbHideSotb(cont, false,
						layoutDialogClearCache);

			}
		});
	}

	public static void showDialogGetCash(final MainActivity activity,
			final View layoutDialogGetCash, final View ivMask) {
		if (layoutDialogGetCash.getVisibility() == View.VISIBLE) {
			AnimationUtil.animationShowHideAlpha(activity, false, ivMask);
			AnimationUtil.animationShowHideAlpha(activity, false,
					layoutDialogGetCash);
			return;
		} else {
			AnimationUtil.animationShowHideAlpha(activity, true, ivMask);
			AnimationUtil.animationShowHideAlpha(activity, true,
					layoutDialogGetCash);
		}

		View btnCancel = layoutDialogGetCash.findViewById(R.id.button1);
		View btnOk = layoutDialogGetCash.findViewById(R.id.button2);

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				layoutDialogGetCash.setVisibility(View.GONE);
				ivMask.setVisibility(View.GONE);

				UIHelper.call(activity, Constants.TEL_JUJIA);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				AnimationUtil.animationShowHideAlpha(activity, false, ivMask);
				AnimationUtil.animationShowHideAlpha(activity, false,
						layoutDialogGetCash);

			}
		});
	}

	public static void showDialogLouout(final MainActivity activity,
			final View layoutDialogLogout, final View ivMask) {
		if (layoutDialogLogout.getVisibility() == View.VISIBLE) {
			AnimationUtil.animationShowHideAlpha(activity, false, ivMask);
			AnimationUtil.animationShowHideAlpha(activity, false,
					layoutDialogLogout);
			return;
		} else {
			AnimationUtil.animationShowHideAlpha(activity, true, ivMask);
			AnimationUtil.animationShowHideAlpha(activity, true,
					layoutDialogLogout);
		}

		// final CheckBox cbClearCache = (CheckBox) layoutDialogLogout
		// .findViewById(R.id.checkBox1);
		View btnOk = layoutDialogLogout.findViewById(R.id.button2);
		View btnCancel = layoutDialogLogout.findViewById(R.id.button1);

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}

				UserCenter.loginOut(activity);

				// if (cbClearCache.isChecked()) {
				// UserCenter.clearUserRecord(activity);
				// }

				layoutDialogLogout.setVisibility(View.GONE);
				ivMask.setVisibility(View.GONE);

				activity.menus[0].performClick();
//				activity.menus[3].performClick();
//				activity.tv8IsLogin.setText("未登录");
//				activity.tv8IsLogin.setTextColor(activity.colorGray);

			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UIHelper.isFastDoubleClick(v)) {
					return;
				}
				AnimationUtil.animationShowHideAlpha(activity, false, ivMask);
				AnimationUtil.animationShowHideAlpha(activity, false,
						layoutDialogLogout);

			}
		});
	}

	public static void showGiftDetail(MainActivity activity, Item gift) {
		Intent intent = new Intent(activity, GiftDetailActivity.class);
		intent.putExtra(Keys.ITEM, gift);
		activity.startActivity(intent);
	}

	public static void showRecharge(Activity activity) {
		Intent intent = new Intent(activity, PayOnlineActivity.class);
		intent.putExtra(Keys.PAY_TYPE, PayOnlineActivity.PAY_TYPE_RECHARGE);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.slide_in_from_right,
				R.anim.slide_out_to_left);
	}

	public static void installAPK(Context context, File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 生成空的用来占位的menu，在menu中不可见
	 * 
	 * @param supportMenuInflater
	 * @param menu
	 */
	public static Button makeEmptyMenu(MenuInflater supportMenuInflater,
			Menu menu) {
		supportMenuInflater.inflate(R.menu.empty_menu, menu);
		Button menuEmpty = (Button) menu.findItem(R.id.menu_empty)
				.getActionView();
		menuEmpty.setText("");
		menuEmpty.setBackgroundResource(R.drawable.transparent);

		return menuEmpty;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void makeEmptyMenu(android.view.MenuInflater menuInflater,
			android.view.Menu menu) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			menuInflater.inflate(R.menu.empty_menu, menu);
			Button menuEmpty = (Button) menu.findItem(R.id.menu_empty)
					.getActionView();
			menuEmpty.setText("");
			menuEmpty.setBackgroundResource(R.drawable.transparent);
		}

	}

	public static void setMeizuBtn(Resources resources, View layoutBottom,
			Button btn) {
		layoutBottom.findViewById(R.id.line_h).setVisibility(View.GONE);
		layoutBottom.setPadding(0, 0, 0,
				resources.getDimensionPixelSize(R.dimen.padding_large));
		btn.setBackgroundResource(R.drawable.btn_meizu_click);
		btn.getLayoutParams().width = resources
				.getDimensionPixelSize(R.dimen.width_meizu_btn);
		btn.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				resources.getDimensionPixelSize(R.dimen.textSize_0));
	}

	public static void showSimpleFragActivity(Activity activity,
			Class<? extends Fragment> fragClass) {
		Intent intent = new Intent(activity, SimpleFragActivity.class);
		intent.putExtra(Keys.FRAG_NAME, fragClass.getName());
		activity.startActivity(intent);
	}

	public static void showCityChoose(Activity activity) {
		Intent intent = new Intent(activity, CityChooseActivity.class);
		// activity.startActivity(intent);
		activity.startActivityForResult(intent,
				Constants.REQUEST_CODE_CHOOSE_CITY);
	}

	/**
	 * 在市场中打开
	 * 
	 * @param activity
	 */
	public static void showInMarket(Activity activity) {
		Uri uri = Uri.parse("market://details?id="
				+ PhoneUtil.getPackageInfo(activity).packageName);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(intent);
	}

}
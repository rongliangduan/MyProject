package cn.op.zdf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.op.common.util.DateUtil;
import cn.op.common.util.Log;
import cn.op.zdf.event.AlarmEvent;
import de.greenrobot.event.EventBus;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = Log.makeLogTag(AlarmReceiver.class);

	public static final String ACTION_ALARM_TIME = "cn.op.zdf.AlarmReceiver.ACTION_ALARM_TIME";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "======onReceive===== action=" + intent.getAction());

		if (ACTION_ALARM_TIME.equals(intent.getAction())) {
			Log.d(TAG,
					"======ACTION_ALARM_TIME====== time= " + DateUtil.getDate());

			AlarmEvent event = new AlarmEvent();
			event.isAlarm = true;
			EventBus.getDefault().post(event);

			// AppContext.toastShow(DateUtil.getDate());
		}
	}
	
}

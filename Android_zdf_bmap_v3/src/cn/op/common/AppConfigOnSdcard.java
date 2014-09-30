package cn.op.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.content.Context;
import cn.op.common.util.FileUtils;

/**
 * 应用程序配置类：用于保存用户相关信息及设置, 保存在sd卡上；若sd卡不存在，则保存在手机内部
 * 
 * @author lufei
 * 
 */
public class AppConfigOnSdcard extends AppConfig {

	private static final String DIR_ON_SD = "zdf";
	private static AppConfigOnSdcard appConfig;

	public static AppConfigOnSdcard getAppConfig(Context context) {
		if (appConfig == null) {
			appConfig = new AppConfigOnSdcard();
			appConfig.mContext = context;
		}
		return appConfig;
	}

	public Properties get() {
		FileInputStream fis = null;
		Properties props = new Properties();
		try {

			if (!FileUtils.checkSaveLocationExists()) {
				return super.get();
			}

			// dir: /mnt/sdcard/zdf/config
			File dirConf = FileUtils.getDirOnExtStore(File.separator
					+ DIR_ON_SD + File.separator + APP_CONFIG);
			// file: /mnt/sdcard/zdf/config/config
			File conf = new File(dirConf, APP_CONFIG);
			if (!conf.exists()) {
				conf.createNewFile();
			}

			fis = new FileInputStream(conf);

			props.load(fis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return props;
	}

	public void setProps(Properties p) {
		FileOutputStream fos = null;
		try {

			if (!FileUtils.checkSaveLocationExists()) {
				super.setProps(p);
				return;
			}

			// dir: /mnt/sdcard/zdf/config
			File dirConf = FileUtils.getDirOnExtStore(File.separator
					+ DIR_ON_SD + File.separator + APP_CONFIG);
			// file: /mnt/sdcard/zdf/config/config
			File conf = new File(dirConf, APP_CONFIG);
			if (!conf.exists()) {
				conf.createNewFile();
			}
			fos = new FileOutputStream(conf);

			p.store(fos, null);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

}

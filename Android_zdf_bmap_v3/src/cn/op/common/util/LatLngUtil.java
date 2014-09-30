package cn.op.common.util;

public class LatLngUtil {
	private static final double EARTH_RADIUS = 6378.137 * 1000;

	public static final double POINT_20_KM = 0.139;
	public static final double POINT_10_KM = 0.070;
	public static final double POINT_5_KM = 0.035;

	// public static final double POINT_2_KM = 0.017;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 计算两个经纬度之间的距离,单位：米
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static double getDistance(double lat1, double lng1, double lat2,
			double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

	/**
	 * 火星坐标系GCJ-02 转 百度坐标系BD-09
	 * 
	 * @param gg_lat
	 * @param gg_lon
	 * @param bd_lat
	 * @param bd_lon
	 */
	public static double[] fromGcjToBaidu(double gg_lat, double gg_lon) {
		double[] bdLoc = new double[2];

		double x = gg_lon, y = gg_lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		double bd_lat = z * Math.sin(theta) + 0.006;
		double bd_lon = z * Math.cos(theta) + 0.0065;

		bdLoc[0] = bd_lat;
		bdLoc[1] = bd_lon;

		return bdLoc;
	}

	/**
	 * BD-09 转 火星坐标系GCJ-02
	 * 
	 * @param bd_lat
	 * @param bd_lon
	 * @return gcjLatLng
	 */
	public static double[] fromBaiduToGcj(double bd_lat, double bd_lon) {
		double[] gcjLatLng = new double[2];

		double x = bd_lon - 0.0065, y = bd_lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		double gg_lat = z * Math.sin(theta);
		double gg_lon = z * Math.cos(theta);

		gcjLatLng[0] = gg_lat;
		gcjLatLng[1] = gg_lon;

		return gcjLatLng;
	}

}

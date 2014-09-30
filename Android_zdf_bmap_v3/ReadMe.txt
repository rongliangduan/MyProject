1. 全局容器Application对象：cn.op.zdf.AppContext

2. 公共包：cn.op.common
	包含了各种通用工具类，以及可复用公共基础类
	
3. 网络层：cn.op.zdf.ApiClient
	所有网络请求都由此类负责，相关的url控制在 cn.op.common.domain.URLs
	
4. 数据解析：cn.op.zdf.domain
	数据解析统一由各自的domain负责
	
5. Dao层：cn.op.zdf.dao
	具体参考：https://github.com/jgilfelt/android-sqlite-asset-helper
	初始化数据库文件在/assets/databases/zdf.zip，zip文件中包含sqlite数据库文件
	数据库更新由/assets/databases/zdf_upgrade_4-5.sql控制, 以及在cn.op.zdf.dao.MyDbHelper中设置DATABASE_VERSION=5
	
6. EventBus事件通知：cn.op.zdf.event
	具体参考：https://github.com/greenrobot/EventBus
	注意：事件被post发出之后，相应的订阅者会立即收到响应，当涉及到两个Activity之间通信时要注意Activity的生命周期影响；
	当前Activity发出事件后，后台的Activity会立即响应，当后台的Activity回到前台时，其生命周期方法会执行，要注意此时周期方法
	对之前事件响应结果的影响。
	例如：在地图页面BMapFragment已有数据显示，此时开启选择城市CityChooseActivity，选择城市后发出CityChooseEvent，地图页面立即做出了响应，清除了
	之前显示的数据，并显示了新的数据，但当选择城市页面finish后，地图页面回到前台，执行了onResume生命周期方法，恢复了在开启选择城市页面前的数据，此时
	的解决办法就是在onResume中重新绘制一次地图数据（选择城市后获取到的数据）
	
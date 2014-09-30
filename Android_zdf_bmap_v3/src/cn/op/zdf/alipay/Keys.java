/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package cn.op.zdf.alipay;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

    // 合作商户ID，用签约支付宝账号登录www.alipay.com后，在商家服务页面中获取。
	public static final String DEFAULT_PARTNER = "2088111220362925";

    // 商户收款的支付宝账号jujia20130926@163.com
	public static final String DEFAULT_SELLER = "2088111220362925";

    // 商户（RSA）私钥
	public static final String PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANjH2PzUfHuYHLF5jfOFYr5hia52ylpj1Vxzn/2RaWepO6pnAawgydjybwneHSHzyd/BiX+S6lTFjMzPGxK8tlStxLrvB5h/acN2ounMWERK/0uXJLqMMSwlLJX1UTuVRiVAXJtV+KhNS1FEbPgAcntnI6hexUlM/S0iyyKi2FtHAgMBAAECgYBQQZTE7cn0W/lrfywdlylagHPfol0CH+nS43+8+cPDO/U5/g6xciYoaoDPb2rrsfzwGQ/VqkeVANpCzP4h7rm1azWRg5k/tmY3mc64e8AlppbVWeUwruZxaGCyMlUuHHRVQh0PzJE5zh79YGI/lbDZOgWHdLRq1DX9v76jxSKZ6QJBAPlTesZhaL3dnWWh3jSk2bTZzDYFefnQpEiZIbESNVpdRh55o3BXGUVRZGHR8luWu/UmG0DdEizJc7y6u1E3/W0CQQDelVlCCZhflO1asyIf6H6Srj1f+hMmCaEJzqZnpBaOcrla295Qi2h8CjiwxDFLVricDKp08anXNjmo9B8RUw8DAkAL18xIKg5YnnNAhZzRcVcqAQJzOnVdyWxUSnnQOGYyYaXYAr0hs/FuWnxjLR7O/Oz2qxW2Ugnv+K23tEL7i6ZNAkAGeuqiVY0uGzPd1CFGNIqa0p/Oz4UHMniRxIZrtgdCFmz3DDeouKZOFi4YV13oB5pQUL4VsYN0VIvRM2pEItrpAkBrStTjp2lIMovCY+u0ToTVPIIijyF5QGeu8Dof+J5CTlUgB07A/8J2hXOrQhbA6FGBf4nNxrJvvHzG+yTEStpq";

    // 支付宝（RSA）公钥
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNVl7vZzysnZEb1O/xZQ2xWp1nK96ZG1l4zLs+ IinWXd3Ocs0psyWZlCig+sm0h3DLGxpb4GHbgTPe+H+aAjIZkAgMUKHamzVhv6wvdOt3y50TZTcL NrEHIrSGvy3azpNzq1JpnM67UAlRyfOSjJN8Drxb4ypZkub36EJxV6/BfQIDAQAB";

}

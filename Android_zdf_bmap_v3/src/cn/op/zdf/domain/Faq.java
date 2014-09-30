package cn.op.zdf.domain;

import java.util.ArrayList;
import java.util.List;

public class Faq {

	public String question;
	public String answer;
	public boolean isAnswerShow;

	public Faq() {
		super();
	}

	public Faq(String question, String answer) {
		super();
		this.question = question;
		this.answer = answer;
	}

	public static List<Faq> parseDemo() {
		ArrayList<Faq> list = new ArrayList<Faq>();
		list.add(new Faq("【有间房】预订有保障吗？",
				"当然有保障！【有间房】预订不收取您任何费用。只要您预订之后接收到预订成功短信，酒店就会给您保留房间。免得直接到酒店无房的尴尬。"));
		list.add(new Faq("【有间房】预订有什么好处？",
				"1、预订完全免费；\n2、享受比酒店前台更低的价格；\n3、优先订到房间，避免因找房而耽误您的行程。\n"));
		list.add(new Faq(
				"如何在【有间房】预订？",
				"【有间房】是纯互联网预订，不需要打电话。您只需在网站上找到适合您的酒店后，点击相应房型后的预订按钮，然后填写个人资料等信息，提交后您将收到一条订单正在处理中的短信(当网络堵塞时可能会延迟)，说明我们的工作人员正在为您处理，一般30分钟内会发送预订结果短信给您。如果收到含有“订单已确认”或“预订成功”字样的短信，说明您已经预订成功，按时到酒店入住即可；如果收到“满房”的短信，建议您再选择周边其它酒店预订。"));
		list.add(new Faq("为什么【有间房】预订价格要比酒店前台便宜？",
				"因为我们和酒店有协议，每月会给酒店送去大量的客人，所以我们能拿到更低的协议价。同时酒店为了保证我们给他输送客人，所以给我们价格要比酒店前台价格还要低。"));
		list.add(new Faq("预订需要注册吗？", "预订不需要注册就可直接预订，但需要填写您的手机号，方便接收预订结果短信。"));
		list.add(new Faq("预订时为什么要填写手机号？",
				"填写手机号是为了您方便接收订单处理结果短信用，我们不会透露给任何无关的第三方，也不会因此扣您任何费用。"));
		list.add(new Faq(
				"我到酒店时酒店前台说没有我的预订时怎么办？",
				"如果您已经通过我们预订了此家酒店，并得到我方的确认，酒店前台就会有您的订单，您可以用订单上的名字查一下，如果还是没有，您可以与我们的预订中心联系，电话：4008-521-002，由我们与酒店联系为您解决此事。"));
		list.add(new Faq("我行程有变化，要更改/取消已预订的酒店怎么办？",
				"如果需要更改/取消您的预订，请致电我们预订中心，电话：4008-521-002，由我们客服人员给您处理。"));
		list.add(new Faq("我想在酒店再多住几天怎么办？",
				"您可以打电话通知我们的预订中心，由我们的客服人员帮您解决， 电话：4008-521-002。"));
		list.add(new Faq("入住酒店时应注意的问题!",
				"当您预订酒店时应以实际入住人的名字做预订。入住时您应以预订时登记的客人名字来办理入住手续，也就是说办理入住手续的客人姓名要与预订时登记的姓名一致。"));
		list.add(new Faq("到达酒店后怎样得到预订的房间？",
				"当您到达酒店后，在总台报您在预订单中填写的姓名，并说明：我有预订。酒店确认后会给您提供所预订的房间。"));
		list.add(new Faq("服务时间",
				"1、 我们为您提供24小时全天服务，节假日照常服务。 \n2、我们只接受通过互联网提交的预订单，不接受其它形式的订单。"));
		list.add(new Faq("修改及取消订单",
				"如果预订成功或处理中，当您的行程有变动需更改/取消预订时，可以致电4008-521-002，由我们工作人员给您处理。"));
		list.add(new Faq(
				"订单提交后确认时间",
				"在您提交订单后，我们一般会在10分钟内以短信形式回复您，如遇特殊情况（如酒店忙、系统出错、短信延迟等）会延时，如果40分钟内仍没有收到短信，请致电4008-521-002查询，我们会尽力给您解决问题；大部分订单会在1—2分钟内确认。"));
		list.add(new Faq(
				"价格和房费",
				"1、 【有间房】所公布的价格均为和各宾馆签定的协议价，它是宾馆给予我们的优惠价，此价格比酒店前台当日挂牌价要低。\n2、【有间房】公布的【有间房】预订价已包含服务费，不包含酒店其他费用及税收。如果您没有其它特殊要求，到酒店您只需支付预订时系统计算出的总房费即可，不需要再支付其它费用。"));
		list.add(new Faq("入住、离店时间",
				"按国际惯例，宾馆的入住时间为14：00，离店时间为次日正午12：00。如提前入住或推迟离店，根据酒店不同要求，某些酒店须酌情加收一定的费用"));
		list.add(new Faq(
				"保留时间",
				"1、如在订单上无特别注明，一般情况下宾馆将保留至入住当日的18：00，过时将不予保留。如果晚于18：00到达酒店，请在预订时的特殊要求内注册具体什么时间能到达，火车或航班班次。这种情况某些酒店可能不予确认或需担保。\n2、如果因火车晚点或其它特殊情况不能按时到达酒店，请根据确认短信提供的号码致电酒店前台说明情况，并说明大致到达时间，这样酒店还会给您延迟保留。因超过最晚到达时间到达且没有通知酒店的，需自行承担后果。"));
		list.add(new Faq(
				"预付房款",
				"1、当节假日期间或举办大型会展期间、旅游旺季等酒店房间紧张的情况下，部分酒店需要您将房款提前预付至酒店，以保证您的预订能够顺利的获得确定。\n2、需要预付的酒店，订单一经确认将不可取消或修改。因此请在填写担保订单前慎重考虑您是否会修改行程，以免给您造成不必要的损失。如您未按订单上约定的时间或房间数量入住，所付房款不作退回。"));
		list.add(new Faq("遇航班/天气等不可抗力因素，造成我无法顺利入住酒店，是否仍会扣款？",
				"如遇到此类特殊情况，请您及时致电4008-521-002告知相关情况。由客服人员为您联系酒店争取协调处理，并请您保留相关凭证。"));
		list.add(new Faq("既然需要在酒店前台支付，为什么信用卡担保，会收到银行短信提示我已经消费？",
				"受银行短信服务系统的限制，此时您收到的短信是担保款冻结的通知，并非实际消费扣款。如有疑问可直接查询发卡银行。"));
		list.add(new Faq(
				"我提供信用卡担保，在酒店前台办理入住后，何时能将担保款释放？",
				"您入住结帐后，我们将及时与酒店核实您的入住信息，确认后，我们会及时向银行提交释放担保款的申请，银行一般会在3-5个工作内将担保款释放；如您使用的是外卡，银行则需要20个工作日将担保款释放；如果超过上述周期，您的担保款仍然没有释放，请及时联系，我们会尽快为您处理。"));

		return list;
	}

}

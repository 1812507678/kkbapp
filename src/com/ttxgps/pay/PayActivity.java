package com.ttxgps.pay;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ttxgps.bean.ResultVo;
import com.ttxgps.bean.ServicePriceBean;
import com.ttxgps.entity.User;
import com.ttxgps.gpslocation.BaseActivity;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class PayActivity extends BaseActivity implements OnClickListener{

	private static final String TAG = "com.ttxgps.pay.PayActivity";

	// 商户PID
	public static final String PARTNER = "2088121406390945";
	// 商户收款账号
	public static final String SELLER = "chenjie007@ctyon.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOjWQg4Vgj3971hujr0TezXswUHA+UiVmmSEltCtGYT+qdkHWWrRvIq1O73wCGABT8Up9bcNbUc1iK1BqymK+ehfLiNw8nxtDgJLdPRzBgEqr4HmsAcDP5roAmcx/d7FDx0yDXhnZqWByT+PpmxttaueS77I8Vr+ECIGRDy29DxxAgMBAAECgYBT850RkvPJAlxgWEyjI/kS8nCkEr5PS5HFmIEAcMl4yJHZLOsytrjyknIeBAQhCZgHkR+FhxPVzxmezsS2T+PXExYHJ2AXuFpV5bLCC6Ra1O+yybVGId8J5lWTeh0QY+VBp1rMJ4RvEEixtcNmJYOKBmUgbctACfodrSYjK6AZeQJBAPfjZjgzv+wZtW9RgOrrONY3rRyRrMu6cwjgVE9q1uZTjYdKb5sEi6bNxZRM/V0/GvWgETZuJDykXcYbYgCRQq8CQQDwdMVwp7Eqt7GibmE9dMieiwgPmut79GYFOuKMa0QCHorqTq+XI92y8ZMW4GagNTilj0jq2pmnYAbSHO8PiLrfAkBtZCuC18t1LG81vbjvcng1iIJbXinsCc6j9yMdnN9S60JEluOjfCEIrjvn0wXoNWX3SPjvlTFmlX5QY1sfNvs/AkAk5UYGazS8IUOYcOdqzqiZ6ytpeZP4iR++XbK1aSAeUyFtonU+87no4zh2oigdHQ18GehOYQyCqjFhvB7Se2BxAkEAtM7zQtZY6lo/75QoKDM4a1NHwKHfLgEXNk3n5P+3i5szrEJ1sZK8X+QA1+IO7f1t08MjKEGsvDwtjc7fE3pIfQ==";

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;


	private IWXAPI api;

	// APP_ID 替换为你的应用从官方网站申请到的合法appId
	public static final String APP_ID = "wx866d7de24b271832";


	/** 商家向财付通申请的商家id */
	public static final String PARTNER_ID = "1303597401";

	//  API密钥，在商户平台设置
	public static final  String API_KEY="34h5htkht34945u9hkkSDF34werKJSDH";//yum



	private String TradeNo;

	private RadioGroup RadioGroup_Price,RadioGroup_PayType;
	private RadioButton Radio_test,Radio_one,Radio_three,Radio_six,Radio_year,Radio_forever,Radio_wx,Radio_zfb;
	private TextView Price_test,Price_one,Price_three,Price_six,Price_year,Price_forever;
	private Button btn_pay;
	private List<ServicePriceBean> servicePriceBeans = new ArrayList<ServicePriceBean>();
	private final List<RadioButton> radioButtons = new ArrayList<RadioButton>();
	private final List<TextView> textViews = new ArrayList<TextView>();
	private int Price_type = 0;
	StringBuffer sb;
	Map<String,String> resultunifiedorder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		initTitle();
		initView();
		GetServicePrice();
		api = WXAPIFactory.createWXAPI(this, null);
		api.registerApp(APP_ID);
		req = new PayReq();
		sb=new StringBuffer();
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText("续费服务");
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		RadioGroup_Price = (RadioGroup)findViewById(R.id.radioGroup_price);
		RadioGroup_PayType = (RadioGroup)findViewById(R.id.radioGroup_payType);
		Radio_test = (RadioButton)findViewById(R.id.test_radio);
		Radio_one = (RadioButton)findViewById(R.id.One_month_radio);
		Radio_three = (RadioButton)findViewById(R.id.Three_months_radio);
		Radio_six = (RadioButton)findViewById(R.id.Six_months_radio);
		Radio_year = (RadioButton)findViewById(R.id.A_year_radio);
		Radio_forever = (RadioButton)findViewById(R.id.forever_radio);
		Radio_wx = (RadioButton)findViewById(R.id.radio_wx);
		Radio_zfb = (RadioButton)findViewById(R.id.radio_zfb);

		radioButtons.add(Radio_test);
		radioButtons.add(Radio_one);
		radioButtons.add(Radio_three);
		radioButtons.add(Radio_six);
		radioButtons.add(Radio_year);
		radioButtons.add(Radio_forever);

		Price_test = (TextView)findViewById(R.id.test_price);
		Price_one = (TextView)findViewById(R.id.One_month_price);
		Price_three = (TextView)findViewById(R.id.Three_months_price);
		Price_six = (TextView)findViewById(R.id.Six_months_price);
		Price_year = (TextView)findViewById(R.id.A_year_price);
		Price_forever = (TextView)findViewById(R.id.forever_price);

		textViews.add(Price_test);
		textViews.add(Price_one);
		textViews.add(Price_three);
		textViews.add(Price_six);
		textViews.add(Price_year);
		textViews.add(Price_forever);

		btn_pay = (Button)findViewById(R.id.pay_btn);
		btn_pay.setOnClickListener(this);


		RadioGroup_Price.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.test_radio:
					Price_type = 0;
					break;
				case R.id.One_month_radio:
					Price_type = 1;
					break;
				case R.id.Three_months_radio:
					Price_type = 2;
					break;
				case R.id.Six_months_radio:
					Price_type = 3;
					break;
				case R.id.A_year_radio:
					Price_type = 4;
					break;
				case R.id.forever_radio:
					Price_type = 5;
					break;


				default:
					break;
				}
			}
		});

	}

	private void initPrice(){
		int len  = servicePriceBeans.size();
		for (int i = 0; i < len; i++) {
			radioButtons.get(i).setText(servicePriceBeans.get(i).getTypeName());
			textViews.get(i).setText(servicePriceBeans.get(i).getPrice());
			radioButtons.get(i).setVisibility(View.VISIBLE);
			textViews.get(i).setVisibility(View.VISIBLE);
		}
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.pay_btn:
			if(servicePriceBeans.size()<0){
				return;
			}
			if(RadioGroup_PayType.getCheckedRadioButtonId() == R.id.radio_wx){
				boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
				if(!isPaySupported){
					Toast.makeText(PayActivity.this, "很抱歉，你的手机未安装微信或者微信版本低，请安装最新版本",Toast.LENGTH_SHORT).show();
					return;
				}
				getWXTradeNo();
			}else{
				getTradeNo();
			}
			break;

		default:
			break;
		}

	}

	private void GetServicePrice(){
		CommonUtils.showProgress(PayActivity.this, "正在加载...",null);
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		WebServiceTask wsk = new WebServiceTask("GetServicePrice", linkedlist, WebService.URL_OTHER,getBaseContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				CommonUtils.closeProgress();
				String msg;
				if(result!=null){//错误信息
					msg=result;
				}
				else{//正确信息
					try {
						JSONObject jsonObject = new JSONObject(data);
						String str=(String) jsonObject.get("Msg");
						msg=str;
						if (jsonObject.has(Constants.STATUS) && jsonObject.getString(Constants.STATUS).equals("0")) {
							String ServiceStr = jsonObject.optString("Service");
							Gson gson = new Gson();
							servicePriceBeans = gson.fromJson(ServiceStr.toString(), new TypeToken<List<ServicePriceBean>>() {
							}.getType());
							if(servicePriceBeans.size()>0){
								initPrice();
							}
						}else{
							Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
						Toast.makeText(getBaseContext(), "订单号获取失败", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		wsk.execute("GetServicePriceResult");
	}


	private void getTradeNo(){
		CommonUtils.showProgress(PayActivity.this, "正在获取订单号...",null);
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID",User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("UserID",User.id));
		linkedlist.add(new WebServiceProperty("ServiceCode ",servicePriceBeans.get(Price_type).getServiceCode()));
		WebServiceTask wsk = new WebServiceTask("GetOrderCode", linkedlist, WebService.URL_OTHER,getBaseContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				CommonUtils.closeProgress();
				String msg;
				if(result!=null){//错误信息
					msg=result;
				}
				else{//正确信息
					try {
						JSONObject jsonObject = new JSONObject(data);
						String str=(String) jsonObject.get("Msg");
						msg=str;
						if (jsonObject.has(Constants.STATUS) && jsonObject.getString(Constants.STATUS).equals("0")) {
							pay(servicePriceBeans.get(Price_type).getPrice(),jsonObject.optString("OrderCode"));
						}else{
							Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
						Toast.makeText(getBaseContext(), "订单号获取失败", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		wsk.execute("GetOrderCodeResult");
	}

	private void getWXTradeNo(){
		CommonUtils.showProgress(PayActivity.this, "正在获取订单信息...",null);
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("UserId",User.id));
		linkedlist.add(new WebServiceProperty("DeviceID",User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("ServiceCode ",servicePriceBeans.get(Price_type).getServiceCode()));
		WebServiceTask wsk = new WebServiceTask("UnifiedOrder", linkedlist, WebService.URL_WX,getBaseContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				CommonUtils.closeProgress();
				String msg;
				if(result!=null){//错误信息
					msg=result;
					Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
				}
				else{//正确信息
					try {
						JSONObject jsonObject = new JSONObject(data);
						//						String str=(String) jsonObject.get("Msg");
						//						msg=str;
						if (jsonObject.has(Constants.STATUS) && jsonObject.getString(Constants.STATUS).equals("0")) {
							SendReq(data);
							//							GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
							//							getPrepayId.execute();
						}else{
							Toast.makeText(getBaseContext(), "订单信息获取失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
						Toast.makeText(getBaseContext(), "订单信息获取失败", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		wsk.execute("UnifiedOrderResult");
	}



	/**------------------------------------------------------------
	 * 支付宝支付开始
	 */

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(PayActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
					sendBroadcast(new Intent(Constants.ACTION_UPDATE_BABYDETAIL));
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(PayActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(PayActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(PayActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};


	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(String Price,String OrderNo) {
		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
				|| TextUtils.isEmpty(SELLER)) {
			return;
		}
		String combo = servicePriceBeans.get(Price_type).getTypeName();
		String yuan = servicePriceBeans.get(Price_type).getPrice();
		// 订单
		String orderInfo = getOrderInfo("酷酷宝续约服务", "开通"+combo+"套餐，需要支付费用"+yuan+"元", Price,OrderNo);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(PayActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price, String OrderNo) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + OrderNo + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://api.kokobao.com:8001/Zhifubao/ResultNotifyPage.aspx"
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
	/**--------------------------------------------------
	 * 支付宝结束
	 */



	/**----------------------------------------------------
	 * 微信支付开始
	 */

	private void SendReq(String str){
		JSONObject json;
		try {
			json = new JSONObject(str);
			if(null != json && !json.has("retcode") ){
				PayReq req = new PayReq();
				//req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
				req.appId			= json.getString("appId");
				req.partnerId		= json.getString("partnerId");
				req.prepayId		= json.getString("prepayId");
				req.nonceStr		= json.getString("nonceStr");
				req.timeStamp		= json.getString("timestamp");
				req.packageValue	= json.getString("package");
				req.sign			= json.getString("sign");
				req.extData			= "app data"; // optional
				Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
				// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
				api.sendReq(req);
			}else{
				Log.d("PAY_GET", "返回错误"+json.getString("retmsg"));
				Toast.makeText(PayActivity.this, "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(API_KEY);


		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion",packageSign);
		return packageSign;
	}
	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(API_KEY);

		this.sb.append("sign str\n"+sb.toString()+"\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes());
		Log.e("orion",appSign);
		return appSign;
	}
	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<"+params.get(i).getName()+">");


			sb.append(params.get(i).getValue());
			sb.append("</"+params.get(i).getName()+">");
		}
		sb.append("</xml>");

		Log.e("orion",sb.toString());
		return sb.toString();
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,String>> {

		private ProgressDialog dialog;


		@Override
		protected void onPreExecute() {
			//			dialog = ProgressDialog.show(PayActivity.this, getString(R.string.app_tip), getString(R.string.getting_prepayid));
		}

		@Override
		protected void onPostExecute(Map<String,String> result) {
			//			if (dialog != null) {
			//				dialog.dismiss();
			//			}
			sb.append("prepay_id\n"+result.get("prepay_id")+"\n\n");
			//			show.setText(sb.toString());

			resultunifiedorder=result;
			genPayReq();

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String,String>  doInBackground(Void... params) {

			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();

			Log.e("orion",entity);

			byte[] buf = Util.httpPost(url, entity);

			String content = new String(buf);
			Log.e("orion", content);
			Map<String,String> xml=decodeXml(content);

			return xml;
		}
	}



	public Map<String,String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName=parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if("xml".equals(nodeName)==false){
						//实例化student对象
						xml.put(nodeName,parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion",e.toString());
		}
		return null;

	}


	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}



	private String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}


	//
	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();

		try {
			String	nonceStr = genNonceStr();


			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", APP_ID));
			packageParams.add(new BasicNameValuePair("body", "APP pay test"));
			packageParams.add(new BasicNameValuePair("mch_id", PARTNER_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", "http://weixin.qq.com"));
			packageParams.add(new BasicNameValuePair("out_trade_no",genOutTradNo()));
			packageParams.add(new BasicNameValuePair("spbill_create_ip", "196.168.1.1"));
			packageParams.add(new BasicNameValuePair("total_fee", "1"));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));


			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));


			String xmlstring =toXml(packageParams);

			return xmlstring;

		} catch (Exception e) {
			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}


	}
	private void genPayReq() {

		req.appId = APP_ID;
		req.partnerId = PARTNER_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "prepay_id="+resultunifiedorder.get("prepay_id");
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());


		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n"+req.sign+"\n\n");


		Log.e("orion", signParams.toString());
		sendPayReq();

	}

	PayReq req;
	private void sendPayReq() {


		api.registerApp(APP_ID);
		api.sendReq(req);
	}


}

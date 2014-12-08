package com.example.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private BroadcastReceiver smsReceiver;
	private IntentFilter filter2;
	private Handler handler;
	private EditText editCaptcha;
	private EditText editPhone;
	private Button buttonLogin;
	private Button buttonCaptcha;
	private String strContent;
	private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editCaptcha = (EditText) findViewById(R.id.login_editcaptcha);
		editPhone = (EditText) findViewById(R.id.login_editphone);
		buttonLogin = (Button) findViewById(R.id.login_loginbutton);
		buttonCaptcha = (Button) findViewById(R.id.login_buttoncaptcha);
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				editCaptcha.setText(strContent);
			};
		};
		filter2 = new IntentFilter();
		filter2.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter2.setPriority(Integer.MAX_VALUE);
		smsReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Object[] objs = (Object[]) intent.getExtras().get("pdus");
				for (Object obj : objs) {
					byte[] pdu = (byte[]) obj;
					SmsMessage sms = SmsMessage.createFromPdu(pdu);
					// 短信的内容
					String message = sms.getMessageBody();
					Log.d("logo", "message     " + message);
					// 短息的手机号。。+86开头？
					String from = sms.getOriginatingAddress();
					Toast.makeText(getApplicationContext(), "from: "+from, Toast.LENGTH_LONG).show();
					Log.d("logo", "from     " + from);
					if (!TextUtils.isEmpty(from)) {
						String code = patternCode(message);
						if (!TextUtils.isEmpty(code)) {
//							if(!from.equals("+8613556156106")){
							strContent = code;
//							strContent = sms.getMessageBody();
							handler.sendEmptyMessage(1);
//							}
						}
					}
				}
			}
		};
		registerReceiver(smsReceiver, filter2);
		
		buttonCaptcha.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (true) {
					Toast.makeText(getApplicationContext(), "获取验证码", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		buttonLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(true){
					Toast.makeText(getApplicationContext(), "登录", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(smsReceiver);
	}
	
	/**
	 * 匹配短信中间的6个数字（验证码等）
	 * 
	 * @param patternContent
	 * @return
	 */
	private String patternCode(String patternContent) {
		if (TextUtils.isEmpty(patternContent)) {
			return null;
		}
		Pattern p = Pattern.compile(patternCoder);
		Matcher matcher = p.matcher(patternContent);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
}

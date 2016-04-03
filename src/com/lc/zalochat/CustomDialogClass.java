package com.lc.zalochat;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogClass extends android.app.Dialog implements android.view.View.OnClickListener {

	public Activity c;
	public Dialog d;
	public Button yes, no;
	public int flag;
	public String msg;
	private TextView tvMsg;

	public CustomDialogClass(Activity pa,String pmsg,int pflag) {
		super(pa);
		// TODO Auto-generated constructor stub
		this.c = pa;
		this.flag = pflag;
		this.msg = pmsg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_dialog);
		yes = (Button) findViewById(R.id.btnYes);
		no = (Button) findViewById(R.id.btnNo);
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		
		tvMsg = (TextView) findViewById(R.id.tvMsg);
		tvMsg.setTextColor(c.getResources().getColor(R.color.title));
		tvMsg.setText("" + msg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnYes:
			c.finish();
			
			break;
		case R.id.btnNo:
			dismiss();
			break;
		default:
			break;
		}
		dismiss();
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	

}
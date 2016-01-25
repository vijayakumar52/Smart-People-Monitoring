package org.opencv.samples.facedetect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.adsonik.smartpeoplemonitoring.R;

public class Front_Page extends Activity implements OnCheckedChangeListener {
	RadioGroup rg;
	String name;
	Button btn1;
    TextView t1,t2,t3,t4,t5;
    RadioButton rb1,rb2,rb3;
    EditText ed;
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.front_page);
		Typeface tf=Typeface.createFromAsset(this.getAssets(), "title.TTF");
		t1=(TextView)findViewById(R.id.textView1);
		t2=(TextView)findViewById(R.id.textView2);
		t3=(TextView)findViewById(R.id.textView3);
		t4=(TextView)findViewById(R.id.textView4);
		t5=(TextView)findViewById(R.id.textView5);
		t1.setTypeface(tf);
		t2.setTypeface(tf);
		t3.setTypeface(tf);
		t4.setTypeface(tf);
		t5.setTypeface(tf);
		rg=(RadioGroup)findViewById(R.id.radioGroup1);
		rb1=(RadioButton)findViewById(R.id.radio0);
		rb2=(RadioButton)findViewById(R.id.radio1);
		rb3=(RadioButton)findViewById(R.id.radio2);
		ed=(EditText)findViewById(R.id.editText1);
		
		rb1.setTypeface(tf);
		rb2.setTypeface(tf);
		rb3.setTypeface(tf);
		rg.setOnCheckedChangeListener(this);
		btn1=(Button)findViewById(R.id.button1);
		btn1.setTypeface(tf);
		btn1.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String cc=ed.getText().toString();
				int id=rg.getCheckedRadioButtonId();
				switch(id){
				case R.id.radio0:
					name="first";
					break;
				case R.id.radio1:
					name="second";
					break;
				case R.id.radio2:
					name="third";
					break;
				}
				Intent i=new Intent(Front_Page.this,FdActivity.class);
				i.putExtra("radio", name);
				i.putExtra("count", cc);
				if(ed.getText().toString().trim().length()==0){
					Toast.makeText(Front_Page.this, "Please Enter the Threshold value", Toast.LENGTH_SHORT).show();
				}else{
				startActivity(i);
				}
			}
		});
	    

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch(checkedId){
	
		case R.id.radio0:
			name="first";
			break;
		case R.id.radio1:
			name="second";
			break;
		case R.id.radio2:
			name="third";
			break;
		}
	}
	

}

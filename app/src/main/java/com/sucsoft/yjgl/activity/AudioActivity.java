package com.sucsoft.yjgl.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.gqt.bean.CallType;
import com.gqt.helper.CallEngine;
import com.gqt.helper.GQTHelper;
import com.sucsoft.yjgl.R;

public class AudioActivity extends BaseActivity implements View.OnClickListener {

    private CallEngine callEngine;

    private String number;
    private Button hangup_button, silence_button, loud_button;
    private TextView text;
    private Chronometer elapsedTime;

    private boolean isLoud = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        getSupportActionBar().setTitle(R.string.audio_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        number = getIntent().getStringExtra("number");

        hangup_button = findViewById(R.id.audio_hangup);
        silence_button = findViewById(R.id.audio_silence);
        loud_button = findViewById(R.id.audio_loud);
        text = findViewById(R.id.audio_textview);
        elapsedTime = findViewById(R.id.elapsedTime);

        hangup_button.setOnClickListener(this);
        silence_button.setOnClickListener(this);
        loud_button.setOnClickListener(this);
        text.setText(number);

        elapsedTime.start();

        callEngine = GQTHelper.getInstance().getCallEngine();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.audio_hangup:
                elapsedTime.stop();
                callEngine.hangupCall(CallType.VOICECALL, number);
                break;
            case R.id.audio_silence:
                boolean isSilence = callEngine.mute();
                silence_button.setActivated(isSilence);
                break;
            case R.id.audio_loud:
                Log.i("M4", "audio_loud: " + isLoud);
                if(isLoud){
                    callEngine.speaker(false);
                    loud_button.setActivated(false);
                    isLoud = false;
                }else{
                    callEngine.speaker(true);
                    loud_button.setActivated(true);
                    isLoud = true;
                }
                break;
        }
    }
}

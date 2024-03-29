package com.sucsoft.yjgl.activity;

import android.content.Context;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gqt.bean.CallType;
import com.gqt.bean.GrpMember;
import com.gqt.bean.PttGroup;
import com.gqt.customgroup.GroupInfoItem;
import com.gqt.helper.GQTHelper;
import com.gqt.video.VideoManagerService;
import com.sucsoft.yjgl.R;

import java.util.HashMap;
import java.util.List;

public class CallActivity extends BaseActivity implements View.OnClickListener{

    public static final HashMap<Character, Integer> mToneMap = new HashMap<Character, Integer>();

    private EditText numTxt = null;
    private ImageButton btnone = null;
    private ImageButton btntwo = null;
    private ImageButton btnthree = null;
    private ImageButton btnfour = null;
    private ImageButton btnfive = null;
    private ImageButton btnsix = null;
    private ImageButton btnseven = null;
    private ImageButton btnenight = null;
    private ImageButton btnnine = null;
    private ImageButton btn0 = null;
    private ImageButton btnmi = null;
    private ImageButton btnjing = null;
    private ImageButton btndialy = null;
    private ImageButton btndel = null;

    private ImageButton videoCall;
    private View keyboardView;
    private ListView call_txl_list;
    protected boolean numTxtCursor;

    ImageView keyboard_img;
    boolean isKeyboard = false;

    private String numberString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        getSupportActionBar().setTitle("拨号");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         //初始化布局
        InitCallScreen();
        initKeyBoard();
        initMenuViews();
        call_txl_list = findViewById(R.id.call_txl_list);
        //获取当前组
       PttGroup grop= GQTHelper.getInstance().getGroupEngine().getCurGrp();
        final List<GrpMember> memberList = GQTHelper.getInstance().getGroupEngine().getGrpMembers(grop);
        TxlAdapter adapter = new TxlAdapter(this,memberList);
        Log.i("CallActivity___",memberList.size()+"---");
        call_txl_list.setAdapter(adapter);
        call_txl_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                numTxt.setText(memberList.get(i).getMemberNum());
            }
        });
        keyboard_img = findViewById(R.id.keyboard_img);
        keyboard_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView pic = (ImageView) view;
                if (isKeyboard) {
                    isKeyboard = false;
                    pic.setImageResource(R.drawable.keyboardup_release);
                    keyboardView.setVisibility(View.INVISIBLE);
                } else {
                    isKeyboard = true;
                    pic.setImageResource(R.drawable.keyboarddown_release);
                    keyboardView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void InitCallScreen() {
        mToneMap.put('1', ToneGenerator.TONE_DTMF_1);
        mToneMap.put('2', ToneGenerator.TONE_DTMF_2);
        mToneMap.put('3', ToneGenerator.TONE_DTMF_3);
        mToneMap.put('4', ToneGenerator.TONE_DTMF_4);
        mToneMap.put('5', ToneGenerator.TONE_DTMF_5);
        mToneMap.put('6', ToneGenerator.TONE_DTMF_6);
        mToneMap.put('7', ToneGenerator.TONE_DTMF_7);
        mToneMap.put('8', ToneGenerator.TONE_DTMF_8);
        mToneMap.put('9', ToneGenerator.TONE_DTMF_9);
        mToneMap.put('0', ToneGenerator.TONE_DTMF_0);
        mToneMap.put('#', ToneGenerator.TONE_DTMF_P);
        mToneMap.put('*', ToneGenerator.TONE_DTMF_S);
        mToneMap.put('d', ToneGenerator.TONE_DTMF_A);
    }

    private void initKeyBoard() {
        numTxt = findViewById(R.id.p_digits);
        numTxt.setText("");
        numTxt.setEnabled(false);
        numTxt.setInputType(InputType.TYPE_NULL);
        numTxt.setCursorVisible(false);
        numTxtCursor = false;
        numTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 设置光标为可见状态
                numTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(numTxt.getWindowToken(), 0);
                numTxt.setCursorVisible(true);
                numTxtCursor = true;
            }
        });

        // 设置文本框输入字数上限；
        numTxt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(1000) });
        numTxt.setDrawingCacheEnabled(true);
        //
        btnjing = findViewById(R.id.pjing);
        btnjing.setOnClickListener(this);
        btnone = findViewById(R.id.pone);
        btnone.setOnClickListener(this);
        //
        btntwo = findViewById(R.id.ptwo);
        btntwo.setOnClickListener(this);
        //
        btnthree = findViewById(R.id.pthree);
        btnthree.setOnClickListener(this);
        //
        btnfour = findViewById(R.id.pfour);
        btnfour.setOnClickListener(this);
        //
        btnfive = findViewById(R.id.pfive);
        btnfive.setOnClickListener(this);
        //
        btnsix = findViewById(R.id.psix);
        btnsix.setOnClickListener(this);
        //
        btnseven = findViewById(R.id.pseven);
        btnseven.setOnClickListener(this);
        //
        btnenight = findViewById(R.id.penight);
        btnenight.setOnClickListener(this);
        //
        btnnine = findViewById(R.id.pnine);
        btnnine.setOnClickListener(this);
        //
        btn0 = findViewById(R.id.p0);
        btn0.setOnClickListener(this);
        //
        btnmi = findViewById(R.id.pmi);
        btnmi.setOnClickListener(this);
    }

    private void initMenuViews() {
        btndialy = findViewById(R.id.pphone);
        btndialy.setOnClickListener(this);
        // 拨打
        videoCall = findViewById(R.id.video_call);
        videoCall.setOnClickListener(this);
        btndel = findViewById(R.id.pdel);
        btndel.setOnClickListener(this);
        keyboardView = findViewById(R.id.call_keyboard);
        keyboardView.setOnClickListener(this);
//        keyboardView.setVisibility(View.INVISIBLE);

    }

    // 按钮事件触发手动调用此方法
    public void downKey(String key) {
        numTxt.setGravity(Gravity.CENTER);//GQT英文版 2014-8-28
        if (!numTxtCursor) {
            numTxt.setCursorVisible(true);
            numTxtCursor = true;
        }
        // 设置一个变量判断是否有光标
        if (numTxtCursor == true) {
            // 获得光标的位置
            int index = numTxt.getSelectionStart();
            // 将字符串转换为StringBuffer
            StringBuffer sb = new StringBuffer(numTxt.getText().toString()
                    .trim());
            // 将字符插入光标所在的位置
            sb = sb.insert(index, key);
            numTxt.setText(sb.toString());
            // 设置光标的位置保持不变
            Selection.setSelection(numTxt.getText(), index + 1);
        } else {
            numTxt.setText(numTxt.getText().toString().trim() + key);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 显示键盘
            case R.id.pone:
                downKey("1");
                break;
            case R.id.ptwo:
                downKey("2");
                break;
            case R.id.pthree:
                downKey("3");
                break;
            case R.id.pfour:
                downKey("4");
                break;
            case R.id.pfive:
                downKey("5");
                break;
            case R.id.psix:
                downKey("6");
                break;
            case R.id.pseven:
                downKey("7");
                break;
            case R.id.penight:
                downKey("8");
                break;
            case R.id.pnine:
                downKey("9");
                break;
            case R.id.p0:
                downKey("0");
                break;
            case R.id.pmi:
                downKey("*");
                break;
            case R.id.pjing:
                downKey("#");
                numberString = numTxt.getText().toString().trim();
                break;
            case R.id.video_call:
                numberString = numTxt.getText().toString().trim();
                if(numberString != null && !numberString.equals("")){
                    GQTHelper.getInstance().getCallEngine().makeCall(CallType.VIDEOCALL, numberString);
                }
                break;
            case R.id.pphone:// 移动电话
                numberString = numTxt.getText().toString().trim();
                GQTHelper.getInstance().getCallEngine().makeCall(CallType.VOICECALL, numberString);
                break;
            case R.id.pdel:
                delete();
                break;
            default:
                break;
        }
    }

    private void delete() {
        StringBuffer sb = new StringBuffer(numTxt.getText().toString().trim());
        int index = 0;
        if (numTxtCursor == true) {
            index = numTxt.getSelectionStart();
            if (index > 0) {
                sb = sb.delete(index - 1, index);
            }
        } else {
            index = numTxt.length();
            if (index > 0) {
                sb = sb.delete(index - 1, index);
            }
        }
        numTxt.setText(sb.toString());
        if (index > 0) {
            Selection.setSelection(numTxt.getText(), index - 1);
        }
        if (numTxt.getText().toString().trim().length() <= 0) {
            numTxt.setCursorVisible(false);
            numTxtCursor = false;
            numTxt.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);//GQT英文版 2014-8-28
        }
    }

    class TxlAdapter extends BaseAdapter{
        private List<GrpMember> list;
        private Context context;
        private  LayoutInflater mlayoutInflater;

        public TxlAdapter (Context context,  List<GrpMember> list) {
            this.context = context;
            this.list = list;
            //利用LayoutInflate把控件所在的布局文件加载到当前类中
            mlayoutInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = mlayoutInflater.inflate(R.layout.txl_list_item, null);
                holder = new ViewHolder();
                holder.textView_name = view.findViewById(R.id.txl_item_name);
                holder.textView_number = view.findViewById(R.id.txl_item_number);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.textView_name.setText(list.get(i).getMemberName());
            holder.textView_number.setText(list.get(i).getMemberNum());
            return view;
        }
        //写一个静态的class,把layout_grid_item的控件转移过来使用
        class ViewHolder {
            TextView textView_name;
            TextView textView_number;
        }
    }
}

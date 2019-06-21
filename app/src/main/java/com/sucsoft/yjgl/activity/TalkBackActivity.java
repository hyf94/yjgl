package com.sucsoft.yjgl.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gqt.addressbook.Member;
import com.gqt.bean.GroupCallListener;
import com.gqt.bean.GrpMember;
import com.gqt.bean.PttGroup;
import com.gqt.customgroup.CustomGroupResult;
import com.gqt.helper.Constant;
import com.gqt.helper.GQTHelper;
import com.gqt.helper.GroupEngine;
import com.sucsoft.yjgl.R;
import com.sucsoft.yjgl.core.AppManager;
import com.sucsoft.yjgl.gqt.GQTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TalkBackActivity extends BaseActivity implements GroupCallListener {

    private static GroupEngine groupEngine = null;

    TextView talkback_cur_talk;
    TextView tb_cur_grp;
    ImageView talkback_btn_ptt;
    GridView person_gridview;
    ListView grp_listview;
    ArrayList<GrpMember> personList;
    ArrayList<PttGroup> grpList;

    private ListViewAdapter listAdapter;

    private static final int GroupStatusChanged = 1;
    private static final int GroupChanged = 2;
    private static final int GroupListChanged = 3;
    private static final int GroupMemChanged = 4;
    private static final int GroupIncoming = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_talkback);
        super.onCreate(savedInstanceState);
        groupEngine = GQTHelper.getInstance().getGroupEngine();    //获取对讲模块实例
        getSupportActionBar().setTitle("对讲");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initView() {
        tb_cur_grp = findViewById(R.id.tb_cur_grp);
        talkback_cur_talk = findViewById(R.id.tb_cur_talk);
        grp_listview = findViewById(R.id.tb_grp_listview);
        person_gridview = findViewById(R.id.tb_person_gridview);
        talkback_btn_ptt = findViewById(R.id.tb_btn_ptt);
        talkback_btn_ptt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("_________TalkActivity", "按下");
                        if (!(GQTUtils.isNetworkAvailable(TalkBackActivity.this))) {
                            break;
                        }
                        talkback_btn_ptt.setImageResource(R.drawable.group_list_ptt_down);
                        groupEngine.makeGroupCall(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("_________TalkActivity", "放开");
                        if (!(GQTUtils.isNetworkAvailable(TalkBackActivity.this))) {
                            break;
                        }
                        talkback_btn_ptt.setImageResource(R.drawable.group_list_ptt_up);
                        groupEngine.makeGroupCall(false);
                        break;
                }
                return true;
            }
        });

        personList = new ArrayList<>();
        grpList = new ArrayList<>();
        listAdapter = new ListViewAdapter(this, grpList);
        final GridViewAdapter adapter = new GridViewAdapter(this, personList);

        grp_listview.setAdapter(listAdapter);
        person_gridview.setAdapter(adapter);

        //获取当前组
        PttGroup curGrp = groupEngine.getCurGrp();
        mHandler.sendMessage(mHandler.obtainMessage(GroupStatusChanged, groupEngine.getCurGrp()));
        tb_cur_grp.setText(curGrp.getGrpName());

        //获取所有组并加入adapter
        final List<PttGroup> allPttGrps = groupEngine.getAllPttGrps();
        grpList.addAll(allPttGrps);
        listAdapter.setData(grpList);
        listAdapter.notifyDataSetChanged();
        //获取组成员并加入adapter
        List<GrpMember> grpMembers = groupEngine.getGrpMembers(curGrp);
        personList.addAll(grpMembers);
        adapter.setData(personList);
        adapter.notifyDataSetChanged();

        //对讲组点击事件
        grp_listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listAdapter.setSelectedPosition(i);
                listAdapter.notifyDataSetChanged();
                Log.i("listview点击", i + "点击了");
                PttGroup pttGroup = allPttGrps.get(i);
                if (pttGroup!=null){
                    groupEngine.setCurGrp(pttGroup, true);
                }
                ArrayList<GrpMember> grpMembers = (ArrayList<GrpMember>) groupEngine.getGrpMembers(pttGroup);
                adapter.setData(grpMembers);
                adapter.notifyDataSetChanged();
            }
        });
    }


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GroupStatusChanged:
                    PttGroup grp = (PttGroup) (msg.obj);
                    tb_cur_grp.setText(grp.getGrpName());
//                    Log.d("_________TalkActivity", "当前讲话:"+grp.getCurSpeakerName());
                    talkback_cur_talk
                            .setText(ShowSpeakerStatus(grp.getCurSpeakerName(), grp.getCurSpeakerNum()));

                    break;
                case GroupMemChanged:
//                    ArrayList list = (ArrayList<GrpMember>)msg.obj;

                    break;
                case GroupChanged:
                    PttGroup grp2 = (PttGroup) (msg.obj);
                    if (grp2 == null) {
                        talkback_cur_talk.setText("无");
                    } else {
                        talkback_cur_talk.setText(ShowSpeakerStatus(grp2.getCurSpeakerName(),
                                grp2.getCurSpeakerNum()));
                    }

                    break;
                case GroupListChanged:{
                    ArrayList<PttGroup> groups = (ArrayList<PttGroup>) msg.obj;
                    if (listAdapter != null) {
                        listAdapter.setData(groups);
                        listAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
//            Log.d("_________TalkActivity", msg.what + "");
            return false;
        }
    });

    public String ShowSpeakerStatus(String strName, String userNum) {
        if (TextUtils.isEmpty(strName)) {
            return "无";
        } else if (userNum.equals(Constant.userName)/* &&isPttPressing */) {
            return "自己";
        } else {
            return "讲话人" + "（"
                    + strName + "）";
        }
    }

    boolean isPaused = false;

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        groupEngine.makeGroupCall(false);
        isPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        groupEngine.regGroupEngineListener(this);
    }

    @Override
    public void groupStateChanged(PttGroup pttGroup) {
        PttGroup curGrp = groupEngine.getCurGrp();
        if (curGrp == null) {
            //do nothing
        } else {
            if (!curGrp.equals(pttGroup)) return;
            mHandler.sendMessage(mHandler.obtainMessage(GroupStatusChanged, pttGroup));
        }
    }

    @Override
    public void onPttRequestSuccess() {
        Log.d("_________TalkActivity", "话权请求成功通知");
    }

    @Override
    public void onPttRequestFailed(String reason) {
        Log.d("_________TalkActivity", "话权请求失败通知");
        mHandler.sendMessage(mHandler.obtainMessage(8, reason));
    }

    @Override
    public void onPttReleaseSuccess() {
        Log.d("_________TalkActivity", "话权释放成功通知");
    }

    @Override
    public void onGroupCallInComing(PttGroup pttGroup) {
        Log.d("_________TalkActivity", "其他组来电通知");
        mHandler.sendMessage(mHandler.obtainMessage(GroupIncoming, pttGroup));
    }

    @Override
    public void onGrpChanged(PttGroup pttGroup) {
        Log.d("_________TalkActivity", pttGroup.getGrpName()+"组改变通知，切换组将回调此方法");
        mHandler.sendMessage(mHandler.obtainMessage(GroupChanged, pttGroup));
        mHandler.sendMessage(mHandler.obtainMessage(GroupMemChanged, groupEngine.getGrpMembers(pttGroup)));
    }

    @Override
    public void onAllGrpsChanged(List<PttGroup> groups) {
        Log.i("GroupCallActivity","----组列表改变通知，包括组名称改变");
        if (groups.size() == 0) {
            mHandler.sendMessage(mHandler.obtainMessage(GroupChanged, null));
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(GroupListChanged, groups));
        }
    }

    @Override
    public void onCurGrpMemberChanged(PttGroup pttGroup, List<GrpMember> members) {
        if (!pttGroup.equals(groupEngine.getCurGrp())) {
            mHandler.sendMessage(mHandler.obtainMessage(GroupMemChanged, groupEngine.getGrpMembers(groupEngine.getCurGrp())));
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(GroupMemChanged, members));
        }
    }

    @Override
    public void showCurrentVolume(int i) {

    }

    @Override
    public void onAddressBook(boolean b) {
        List<Map<String, String>> list = GQTHelper.getInstance().getGroupEngine().getMembers("mtype = '" + Member.UserType.SVP.convert() + "'");
        if (list.size() > 0) {
            String s = "SVP: ";
            for (Map<String, String> m : list) {
                s += "名称：" + m.get("mname") + " 号码：" + m.get("number");
                Log.i("Members", "name----: " + m.get("mname"));
            }

//			myHandler.obtainMessage(8, s).sendToTarget();
        }
    }

    @Override
    public void onAddressBookUpdateVersion(String s) {

    }

    @Override
    public void onTempGroupCallState(int i) {

    }

    @Override
    public void onTempGrpMemberChanged(List<String> list) {

    }

    @Override
    public void onTempGroupCallInComing(String s, List<String> list) {

    }

    @Override
    public void onCustomGroupResultState(CustomGroupResult customGroupResult, int i, String s, List<GrpMember> list) {

    }


    class GridViewAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater mlayoutInflater;
        private ArrayList<GrpMember> list;

        public GridViewAdapter(Context context, ArrayList<GrpMember> list) {
            this.context = context;
            this.list = list;
            //利用LayoutInflate把控件所在的布局文件加载到当前类中
            mlayoutInflater = LayoutInflater.from(context);
        }

        public void setData(ArrayList<GrpMember> list) {
            this.list = list;
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

        //写一个静态的class,把layout_grid_item的控件转移过来使用
        class ViewHolder {
            public TextView grid_textview;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = mlayoutInflater.inflate(R.layout.grid_item, null);
                holder = new GridViewAdapter.ViewHolder();
                holder.grid_textview = view.findViewById(R.id.grid_item_text);
                view.setTag(holder);
            } else {
                holder = (GridViewAdapter.ViewHolder) view.getTag();
            }

            holder.grid_textview.setText(list.get(i).getMemberName());
            if (list.get(i).getState() == 1) {
                holder.grid_textview.setTextColor(Color.parseColor("#008200"));
            }
            return view;
        }
    }

    class ListViewAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater mlayoutInflater;
        private ArrayList<PttGroup> list;

        private int selectedPosition = -1;// 选中的位置

        public ListViewAdapter(Context context, ArrayList<PttGroup> list) {
            this.context = context;
            this.list = list;
            //利用LayoutInflate把控件所在的布局文件加载到当前类中
            mlayoutInflater = LayoutInflater.from(context);
        }

        public void setData(ArrayList<PttGroup> list) {
            this.list = list;
        }

        public void setSelectedPosition(int position) {
            Log.i("setSelectedPosition",""+position);
            selectedPosition = position;
            notifyDataSetChanged();
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

        //写一个静态的class,把layout_grid_item的控件转移过来使用
        class ViewHolder {
            TextView list_textview;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ListViewAdapter.ViewHolder holder = null;
            if (view == null) {
                view = mlayoutInflater.inflate(R.layout.list_item, null);
                holder = new ListViewAdapter.ViewHolder();
                holder.list_textview = view.findViewById(R.id.list_item_text);
                Log.i("setSelectedPosition","selectedPosition:"+selectedPosition+"position"+i);
//                if (selectedPosition == i) {
//                    view.setBackgroundResource(R.color.font_color);
//                    holder.list_textview.setTextColor(getResources().getColor(R.color.dilog_title_red));
//                }else {
//                    view.setBackgroundResource(R.color.font_color2);
//                    holder.list_textview.setTextColor(getResources().getColor(R.color.white));
//                }
                view.setTag(holder);
            } else {
                holder = (ListViewAdapter.ViewHolder) view.getTag();
            }

            holder.list_textview.setText(list.get(i).getGrpName());
            return view;
        }
    }

}

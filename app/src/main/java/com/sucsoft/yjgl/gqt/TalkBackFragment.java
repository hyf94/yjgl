package com.sucsoft.yjgl.gqt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gqt.addressbook.Member;
import com.gqt.bean.GroupCallListener;
import com.gqt.bean.GroupState;
import com.gqt.bean.GrpMember;
import com.gqt.bean.PttGroup;
import com.gqt.customgroup.CustomGroupResult;
import com.gqt.helper.Constant;
import com.gqt.helper.GQTHelper;
import com.gqt.helper.GroupEngine;
import com.gqt.utils.LoadingAnimation;
import com.sucsoft.yjgl.R;
import com.sucsoft.yjgl.activity.GQTActivity;
import com.sucsoft.yjgl.activity.SelectPersonsActivity;
import com.sucsoft.yjgl.core.AppManager;

import java.lang.reflect.Field;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TalkBackFragment extends Fragment implements GroupCallListener {


    private static GroupEngine groupEngine = GQTHelper.getInstance().getGroupEngine();    //获取对讲模块实例

    View view;
    Context context;

    TextView talkback_cur_talk;
    ImageView talkback_btn_ptt;
    GridView person_gridview;
    ListView grp_listview;
    ArrayList<GrpMember> personList;
    ArrayList<PttGroup> grpList;

    private static final int GroupStatusChanged = 1;
    private static final int GroupChanged = 2;
    private static final int GroupListChanged = 3;
    private static final int GroupMemChanged = 4;
    private static final int GroupIncoming = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_talk_back, container, false);
        context = getContext();
        initView();

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        talkback_cur_talk = view.findViewById(R.id.talkback_cur_talk);
        grp_listview = view.findViewById(R.id.grp_listview);
        person_gridview = view.findViewById(R.id.person_gridview);
        talkback_btn_ptt = view.findViewById(R.id.talkback_btn_ptt);
        talkback_btn_ptt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("______TalkBackFragment", "按下");
                        if (!(GQTUtils.isNetworkAvailable(context))) {
                            break;
                        }
                        talkback_btn_ptt.setImageResource(R.drawable.group_list_ptt_down);
                        groupEngine.makeGroupCall(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("______TalkBackFragment", "放开");
                        if (!(GQTUtils.isNetworkAvailable(context))) {
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
        final ListViewAdapter listAdapter = new ListViewAdapter(getActivity(),grpList);
        final GridViewAdapter adapter = new GridViewAdapter(getActivity(),personList);


        grp_listview.setAdapter(listAdapter);
        person_gridview.setAdapter(adapter);

        //获取当前组
        PttGroup curGrp = groupEngine.getCurGrp();
        mHandler.sendMessage(mHandler.obtainMessage(GroupStatusChanged, groupEngine.getCurGrp()));
        Toast.makeText(AppManager.getAppManager().currentActivity(),"GrpName2: -----"+curGrp.getGrpName(),Toast.LENGTH_SHORT).show();

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
                listAdapter.notifyDataSetInvalidated();
                adapter.notifyDataSetChanged();
                Log.i("listview点击",i+"点击了");
                PttGroup pttGroup = allPttGrps.get(i);
                groupEngine.setCurGrp(pttGroup,false);
//                adapter.setData(groupEngine.getGrpMembers(curGrp));
//                adapter.notifyDataSetChanged();
            }
        });

        Toast.makeText(getActivity(),"-----aaaaaa",Toast.LENGTH_SHORT).show();
    }


    /**
     * 打开临时对讲对话框
     */
    public void makeTempGrpCall() {
        final EditText et_name = new EditText(getActivity());
        et_name.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.MATCH_PARENT));
        //et_name.setHint(R.string.temp_group_call_name);
        //设置弹出提示框时默认会有临时对讲名称  jibingeng 2015-09-23
        et_name.setText(getTemporaryName());
        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.make_temp_group_call)
                .setIcon(R.drawable.logo)
                .setView(et_name)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (!TextUtils.isEmpty(et_name.getText().toString().trim())) {
                                    setDialogClosable(dialog, true);
                                    Intent intent = new Intent();
                                    intent.putExtra("tempGroupName",
                                            et_name.getText().toString().trim());
                                    intent.setClass(context,
                                            SelectPersonsActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(context, R.string.input_tmpgrp_tip, Toast.LENGTH_SHORT).show();
                                    setDialogClosable(dialog, false);
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                setDialogClosable(dialog, true);
                            }
                        }).create();
        dialog.show();
    }

    /**
     * 获取临时对讲名称
     */
    private String getTemporaryName(){

        return getResources().getString(R.string.ptt_grp)+getTime();

    }
    /**
     * 获取当前时间格式HH:mm:ss
     */
    public String getTime() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(" HHmmss ");
            Date curDate = new Date(System.currentTimeMillis());
            String strTime = formatter.format(curDate);
            return strTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 通过反射机制设置Dialog是否可关闭
     * @param dialog .
     * @param isClosable .
     */
    private void setDialogClosable(DialogInterface dialog, boolean isClosable){
        try {
            Field field = dialog.getClass()
                    .getSuperclass()
                    .getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, isClosable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case GroupStatusChanged: {
                    PttGroup grp = (PttGroup) (msg.obj);
                    Toast.makeText(getActivity(),"GroupStatusChanged"+grp.getCurSpeakerName(),Toast.LENGTH_SHORT).show();
                    talkback_cur_talk
                            .setText(ShowSpeakerStatus(grp.getCurSpeakerName(), grp.getCurSpeakerNum()));
//                    talkback_cur_talk.setText(mStatus
//                            + ShowPttStatus(((PttGroup)(msg.obj)).getCurState()));
                }
                    break;
                case GroupMemChanged:
//                    ArrayList list = (ArrayList<GrpMember>)msg.obj;

                    break;
                case GroupChanged:
                {
                    PttGroup grp = (PttGroup)(msg.obj);
                    if(grp == null){
                        talkback_cur_talk.setText("无");
                    }else{
                        talkback_cur_talk.setText(ShowSpeakerStatus(grp.getCurSpeakerName(),
                                grp.getCurSpeakerNum()));
                    }
                }
                break;
            }
            Log.d("____TalkBackFragment",msg.what+"");
            return false;
        }
    });

    public String ShowSpeakerStatus(String strName, String userNum) {
        Log.i("____TalkBackFragment",strName+userNum);
        if (TextUtils.isEmpty(strName)) {
//            mBaseVisualizerView.setTimes(-1);
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
    public void onPause() {
        // TODO Auto-generated method stub
        groupEngine.makeGroupCall(false);
        isPaused = true;
        super.onPause();
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        groupEngine.regGroupEngineListener(this);
    }

    @Override
    public void groupStateChanged(PttGroup pttGroup) {
        PttGroup curGrp = groupEngine.getCurGrp();
        if(curGrp == null){
            Log.d("____TalkBackFragment","curGrp不存在");
            //do nothing
        }else{
            Log.d("____TalkBackFragment","curGrp存在");
            if(!curGrp.equals(pttGroup)) return;
            mHandler.sendMessage(mHandler.obtainMessage(GroupStatusChanged, pttGroup));
        }
    }

    @Override
    public void onPttRequestSuccess() {
        Log.d("____TalkBackFragment","话权请求成功通知");
    }

    @Override
    public void onPttRequestFailed(String reason) {
        Log.d("____TalkBackFragment","话权请求失败通知");
        mHandler.sendMessage(mHandler.obtainMessage(8, reason));
    }

    @Override
    public void onPttReleaseSuccess() {
        Log.d("____TalkBackFragment","话权释放成功通知");
    }

    @Override
    public void onGroupCallInComing(PttGroup pttGroup) {
        Log.d("____TalkBackFragment","其他组来电通知");
        mHandler.sendMessage(mHandler.obtainMessage(GroupIncoming, pttGroup));
    }

    @Override
    public void onGrpChanged(PttGroup pttGroup) {
        Log.d("____TalkBackFragment",pttGroup.getGrpName()+"组改变通知，切换组将回调此方法");
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
        Log.i("GroupCallActivity","----组列表改变通知");
        if(!pttGroup.equals(groupEngine.getCurGrp())){
            mHandler.sendMessage(mHandler.obtainMessage(GroupMemChanged, groupEngine.getGrpMembers(groupEngine.getCurGrp())));
        }else{
            mHandler.sendMessage(mHandler.obtainMessage(GroupMemChanged, members));
        }
    }

    @Override
    public void showCurrentVolume(int i) {

    }

    @Override
    public void onAddressBook(boolean b) {
        List<Map<String, String>> list = GQTHelper.getInstance().getGroupEngine().getMembers("mtype = '"+ Member.UserType.SVP.convert()+"'");
        if(list.size()>0){
            String s="SVP: ";
            for(Map<String,String> m:list){
                s+="名称："+m.get("mname")+" 号码："+m.get("number");
                Log.i("Members", "name----: "+m.get("mname"));
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

    class GridViewAdapter extends BaseAdapter{
        private Context context;
        private LayoutInflater mlayoutInflater;
        private ArrayList<GrpMember> list;

        public GridViewAdapter(Context context,ArrayList<GrpMember> list){
            this.context = context;
            this.list = list;
            //利用LayoutInflate把控件所在的布局文件加载到当前类中
            mlayoutInflater=LayoutInflater.from(context);
        }

        public void setData(ArrayList<GrpMember> list){
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
        class ViewHolder{
            public TextView grid_textview;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null){
                view = mlayoutInflater.inflate(R.layout.grid_item,null);

                holder = new ViewHolder();

                holder.grid_textview = view.findViewById(R.id.grid_item_text);
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }

            holder.grid_textview.setText(list.get(i).getMemberName());
            if (list.get(i).getState() == 1){
                holder.grid_textview.setTextColor(Color.parseColor("#008200"));
            }
            return view;
        }
    }

    class ListViewAdapter extends BaseAdapter{
        private Context context;
        private LayoutInflater mlayoutInflater;
        private ArrayList<PttGroup> list;

        private int selectedPosition = -1;// 选中的位置

        public ListViewAdapter(Context context,ArrayList<PttGroup> list){
            this.context = context;
            this.list = list;
            //利用LayoutInflate把控件所在的布局文件加载到当前类中
            mlayoutInflater=LayoutInflater.from(context);
        }

        public void setData(ArrayList<PttGroup> list){
            this.list = list;
        }
        public void setSelectedPosition(int position) {
            selectedPosition = position;
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
        class ViewHolder{
            public TextView list_textview;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null){
                view = mlayoutInflater.inflate(R.layout.list_item,null);

                holder = new ViewHolder();

                holder.list_textview = view.findViewById(R.id.list_item_text);
                if (selectedPosition == i){
                    holder.list_textview.setTextColor(Color.RED);
                }
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }

            holder.list_textview.setText(list.get(i).getGrpName());
            return view;
        }
    }
}

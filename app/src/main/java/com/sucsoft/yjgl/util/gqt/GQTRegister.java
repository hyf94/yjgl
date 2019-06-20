package com.sucsoft.yjgl.util.gqt;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.gqt.addressbook.Member;
import com.gqt.bean.GroupCallListener;
import com.gqt.bean.GrpMember;
import com.gqt.bean.PttGroup;
import com.gqt.customgroup.CustomGroupResult;
import com.gqt.helper.Constant;
import com.gqt.helper.GQTHelper;
import com.gqt.helper.GroupEngine;
import com.gqt.helper.RegisterEngine;
import com.gqt.sipua.welcome.IAutoConfigListener;
import com.sucsoft.yjgl.gqt.GQTUtils;
import com.sucsoft.yjgl.gqt.RegisterCatcher;

import java.util.List;
import java.util.Map;

public class GQTRegister implements IAutoConfigListener,GroupCallListener {
    private Context context;
    private GroupEngine groupEngine = null;
    private RegisterEngine registerEngine = null;
    private RegisterCatcher rcatcher;

    private static final int GroupStatusChanged = 1;
    private static final int GroupChanged = 2;
    private static final int GroupListChanged = 3;
    private static final int GroupMemChanged = 4;
    private static final int GroupIncoming = 5;

    private Toast mToast = null;


    public GQTRegister(Context context){
        this.context = context;
    }

    public void init(String username,String password,String ip, int port){
        //获取GQT实例
        registerEngine = GQTHelper.getInstance().getRegisterEngine();
        rcatcher = new RegisterCatcher(handler);
        if (registerEngine.isRegister()){
            showToast("已登录集群通");
        }
        if (!(GQTUtils.isNetworkAvailable(context))) {
            showToast("网络不可用");
        } else {
            int result = registerEngine.initRegisterInfo("800011", "800011", "39.106.217.160", 7080, "");
            handler.obtainMessage(2, result, 0).sendToTarget();
        }
    }


    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            mToast.show();
        }else {
            mToast.setText(msg);
            mToast.show();
        }
    }

    private void login(Context context) {
        registerEngine.register(context, rcatcher);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showToast("register ok!  " + Constant.displayname);
                    break;
                case 1:
                    String reason = "";
                    switch (Integer.valueOf((String) msg.obj)) {
                        case 401:
                            reason = "密码错误";
                            break;
                        case 403:
                            reason = "用户不存在";
                            break;
                        case 450:
                            reason = "已登录";
                            break;
                        case 480:
                            reason = "服务器不可用";
                            break;
                        case 44:
                            reason = "无网络";
                            break;
                        case 45:
                            reason = "注册超时,请检查服务器地址和端口是否正确";
                            break;
                        default:
                            break;
                    }
                    showToast("register failed!   " + reason);
                    break;
                case 2:
                    switch (msg.arg1) {
                        case 1:
                            showToast("参数为空");
                            break;
                        case 2:
                            showToast("用户名过长");
                            break;
                        case 3:
                            showToast("用户名包含非法字符");
                            break;
                        case 4:
                            showToast("密码过长");
                            break;
                        case 5:
                            showToast("密码包含非法字符");
                            break;
                        case 6:
                            showToast("端口不正确");
                            break;
                        case 0:
                            login(context);
                            break;
                    }
                    break;
                case 3:
//                    progressDialog.dismiss();
                    switch (msg.arg1) {
                        case 0:
                            showToast("连接服务器超时");
                            break;
                        case 1:
                            showToast("用户不存在");
                            break;
                        case 2:
                            showToast("解析服务器数据错误");
                            break;
                        case 3:
                            showToast("账号已被停用");
                            break;
                    }
                    break;
            }
            return false;
        }
    });


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case GroupStatusChanged:
                    PttGroup grp = (PttGroup)(msg.obj);
                    List<PttGroup> allPttGrps = groupEngine.getAllPttGrps();
                    List<GrpMember> list = groupEngine.getGrpMembers(grp);
                    for (int i = 0; i < allPttGrps.size(); i++) {
                        PttGroup pgrp = allPttGrps.get(i);
                        Log.i("getGrpName_list", "-----"+pgrp.getGrpName());
                    }
                    for (int i = 0; i < list.size(); i++) {
                        GrpMember member = (GrpMember) list.get(i);
                        Log.i("GrpMember", "GrpMember: -----"+member.getMemberName());
                    }
                    Log.i("getGrpName----",grp.getGrpName());
                    break;
                case GroupMemChanged:
//                    ArrayList list = (ArrayList<GrpMember>)msg.obj;

                    break;
            }
            Log.d("_________TalkActivity",msg.what+"");
            return false;
        }
    });

    @Override
    public void TimeOut() {
        // TODO Auto-generated method stub
        handler.obtainMessage(3, 0, 0).sendToTarget();
    }

    @Override
    public void FetchConfigFailed() {
        // TODO Auto-generated method stub
        handler.obtainMessage(3, 1, 0).sendToTarget();
    }

    @Override
    public void ParseConfigOK() {
        int result = registerEngine.initRegisterInfo("800011", "800011", "39.106.217.160", 7080, "");
        handler.obtainMessage(2, result, 0).sendToTarget();
    }

    @Override
    public void parseFailed() {
        handler.obtainMessage(3, 2, 0).sendToTarget();
    }

    @Override
    public void AccountDisabled() {
        handler.obtainMessage(3, 3, 0).sendToTarget();
    }

    @Override
    public void groupStateChanged(PttGroup pttGroup) {
        PttGroup curGrp = groupEngine.getCurGrp();
        if(curGrp == null){
            //do nothing
        }else{
            if(!curGrp.equals(pttGroup)) return;
            mHandler.sendMessage(mHandler.obtainMessage(GroupStatusChanged, pttGroup));
        }
    }

    @Override
    public void onPttRequestSuccess() {
        Log.d("_________TalkActivity","onPttRequestSuccess");
    }

    @Override
    public void onPttRequestFailed(String reason) {
        Log.d("_________TalkActivity","onPttRequestFailed");
        mHandler.sendMessage(mHandler.obtainMessage(8, reason));
    }

    @Override
    public void onPttReleaseSuccess() {
        Log.d("_________TalkActivity","onPttReleaseSuccess");
    }

    @Override
    public void onGroupCallInComing(PttGroup pttGroup) {
        Log.d("_________TalkActivity","onGroupCallInComing");
        mHandler.sendMessage(mHandler.obtainMessage(GroupIncoming, pttGroup));
    }

    @Override
    public void onGrpChanged(PttGroup pttGroup) {
        Log.d("_________TalkActivity","onGroupCallInComing");
        mHandler.sendMessage(mHandler.obtainMessage(GroupChanged, pttGroup));
        mHandler.sendMessage(mHandler.obtainMessage(GroupMemChanged, groupEngine.getGrpMembers(pttGroup)));
    }

    @Override
    public void onAllGrpsChanged(List<PttGroup> groups) {
        if (groups.size() == 0) {
            mHandler.sendMessage(mHandler.obtainMessage(GroupChanged, null));
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(GroupListChanged, groups));
        }
    }

    @Override
    public void onCurGrpMemberChanged(PttGroup pttGroup, List<GrpMember> members) {
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
}

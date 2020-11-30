package com.rescue.hc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.personal.framework.utils.StringUtil;
import com.rescue.hc.R;
import com.rescue.hc.bean.command.GroupInfo;
import com.rescue.hc.ui.adapter.TagAdapter;
import com.rescue.hc.ui.view.tag.FlowTagLayout;
import com.rescue.hc.ui.view.tag.OnTagSelectListener;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.rescue.hc.lib.component.ReccoTag.DEFAULT_GROUP_NAME;

public class GroupInfoDialog extends Dialog implements View.OnClickListener {

    private OnCloseListener listener;
    private List<GroupInfo> mGroupInfos;
    private NiceSpinner mNiceSpinner;
    private FlowTagLayout mTagLayout;
    private TextView tvCancel, tvSubmit;

    private TagAdapter<GroupInfo> mTagAdapter;
    private Context mContext;

    private HashMap<String,List<GroupInfo>> mGroupHashMap=new HashMap<>();

    private List<String> mListGroupInfo=new ArrayList<>();

    private List<GroupInfo> mGroupInfosNew =new ArrayList<>();

    private String mGroupNum="1";


    public GroupInfoDialog(@NonNull Context context) {
        super(context);
    }

    public GroupInfoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected GroupInfoDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public GroupInfoDialog(Context context, int themeResId, List<GroupInfo> groupInfos, OnCloseListener listener)
    {
        super(context, themeResId);
        this.mContext=context;
        this.listener = listener;
        this.mGroupInfos = groupInfos;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_info_layout);
        setCanceledOnTouchOutside(true);
        initView();
        initData();
        initTag();
    }

    private void initView() {
        mNiceSpinner=findViewById(R.id.group_spinner);
        mTagLayout=findViewById(R.id.tag_flow_layout);
        tvCancel=findViewById(R.id.tv_info_cancel);
        tvSubmit=findViewById(R.id.tv_info_submit);

        tvCancel.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
    }

    private void initData()
    {
        List<String> list=new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        if(mNiceSpinner!=null)
        {
            mNiceSpinner.attachDataSource(list);

            mNiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    mGroupNum=String.valueOf(adapterView.getAdapter().getItem((int) l));
                    mTagLayout.clearAllOption();
                    mTagAdapter.setGroupNum(mGroupNum);
                    List<GroupInfo> groupInfos = getGroupInfo(mGroupNum);
                    //mTagAdapter.setCheckState(groupInfos);
                    mTagAdapter.notifyDataSetChanged();

                    if(!mGroupHashMap.containsKey(mGroupNum))
                    {
                        mGroupHashMap.put(mGroupNum,groupInfos);
                    }

//                    mListGroupInfo = getGroupInfo(num);
//                    if(mListGroupInfo!=null)
//                    {
//                        mTagAdapter.notifyDataSetChanged();
//                    }
//                    else
//                    {
//                        Toast.makeText(mContext,"未分组",Toast.LENGTH_SHORT).show();
//                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            mNiceSpinner.setSelectedIndex(0);
            //int selectedIndex = mNiceSpinner.getSelectedIndex();
            //System.out.println(selectedIndex);

        }



    }

    private List<GroupInfo> getGroupInfo(String num)
    {
        List<GroupInfo> groupInfoList=new ArrayList<>();

        if(mGroupInfos!=null && mGroupInfos.size()>0)
        {
            for (int i=0,m=mGroupInfos.size();i<m;i++)
            {
                GroupInfo groupInfo = mGroupInfos.get(i);
                if(groupInfo!=null)
                {
                    if(groupInfo.getGroupNum().equals(num))
                    {
                        groupInfoList.add(groupInfo);
                        mTagAdapter.isSelectedPosition(i);
                    }
                }
            }

            return groupInfoList;
        }
        return null;
    }

    int count=0;
    private boolean isChooseEmpty=false;
    private void initTag()
    {

        if(mTagLayout!=null)
        {
            mTagAdapter=new TagAdapter<>(mContext);
            mTagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI);
            mTagLayout.setAdapter(mTagAdapter);
            mTagLayout.setOnTagSelectListener(new OnTagSelectListener() {
                @Override
                public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                    if (selectedList != null && selectedList.size() > 0) {
                        mGroupInfosNew.clear();
                        for (int i : selectedList) {
                            GroupInfo groupInfo=(GroupInfo) parent.getAdapter().getItem(i);
                            mGroupInfosNew.add(groupInfo);
                        }

                    }else{
                        //一个不选会进到这里来
                        //Toast.makeText(mContext,"未选中",Toast.LENGTH_SHORT).show();
                        isChooseEmpty=true;
                    }
                }
            });


            if(mGroupInfos!=null && mGroupInfos.size()>0)
            {
//                for (GroupInfo groupInfo:mGroupInfos
//                     ) {
//                    mListGroupInfo.add(groupInfo.getPersonName()+"-"+groupInfo.getDevEui());
//                }
                //mTagAdapter.onlyAddAll(mListGroupInfo);

                mTagAdapter.isFirst=true;
                //mTagAdapter.setGroupNum(mGroupNum);
                mTagAdapter.onlyAddAll(mGroupInfos);
                mTagAdapter.isFirst=false;


            }




        }
    }

    @Override
    public void show() {
        super.show();
        mTagAdapter.setGroupNum(mGroupNum);
        List<GroupInfo> infoList = getGroupInfo(mGroupNum);
        if(!mGroupHashMap.containsKey(mGroupNum))
        {
            mGroupHashMap.put(mGroupNum,infoList);
        }
        mTagAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_info_cancel:
                if (listener != null) {
                    //List<GroupInfo> groupInfos=new ArrayList<>();
                    listener.onClick(this,null,null, false);
                }
                break;
            case R.id.tv_info_submit:
                if (listener != null) {
                    List<GroupInfo> groupInfos=new ArrayList<>();
                    if(StringUtil.isEmpty(mGroupNum))
                    {
                        return;
                    }

                    if(mGroupHashMap!=null)
                    {
                        if(!mGroupHashMap.containsKey(mGroupNum))
                        {
                            mGroupHashMap.put(mGroupNum,getGroupInfo(mGroupNum));
                        }

                        List<GroupInfo> groupInfosOriginal = mGroupHashMap.get(mGroupNum);
                        if(groupInfosOriginal!=null && groupInfosOriginal.size()>0)
                        {
                            if(isChooseEmpty && mGroupInfosNew.size()==0)
                            {
                                for (GroupInfo original:groupInfosOriginal
                                     ) {
                                    original.setGroupNum(DEFAULT_GROUP_NAME);
                                    groupInfos.add(original);
                                }
                                isChooseEmpty=false;
                            }
                            else
                            {
                                if(mGroupInfosNew !=null && mGroupInfosNew.size()>0)
                                {
                                    //groupInfos.addAll(mGroupInfosNew);
//                                    for (GroupInfo info:mGroupInfosNew) {
//
//                                        info.setGroupNum(mGroupNum);
//                                        groupInfos.add(info);
//                                    }
                                    byte cnt=0;

                                    for (GroupInfo info:mGroupInfosNew) {

                                        for (GroupInfo original:groupInfosOriginal) {

                                            if(info.getDevEui().equals(original.getDevEui()))
                                            {
                                               cnt+=1;
                                            }
                                        }
                                    }

                                    if(cnt>0)
                                    {
                                        for (GroupInfo info:mGroupInfosNew
                                        ) {
                                            for (int i=0,m=groupInfosOriginal.size();i<m;i++)
                                            {
                                                GroupInfo gp= groupInfosOriginal.get(i);
                                                if(gp.getDevEui().equals(info.getDevEui()))
                                                {
                                                    groupInfos.add(gp);
                                                }
                                                else
                                                {
                                                    info.setGroupNum(mGroupNum);
                                                    groupInfos.add(info);
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        for (GroupInfo info:mGroupInfosNew
                                        ) {
                                            for (int i=0,m=groupInfosOriginal.size();i<m;i++)
                                            {
                                                GroupInfo gp= groupInfosOriginal.get(i);
                                                if(gp.getDevEui().equals(info.getDevEui()))
                                                {
                                                    gp.setGroupNum(DEFAULT_GROUP_NAME);
                                                    groupInfos.add(gp);
                                                }
                                                else
                                                {
                                                    info.setGroupNum(mGroupNum);
                                                    groupInfos.add(info);
                                                }
                                            }
                                        }
                                    }




                                }
                            }
                        }
                        else //初始没有值
                        {
                            if(mGroupInfosNew !=null && mGroupInfosNew.size()>0)
                            {
                                for (GroupInfo info:mGroupInfosNew) {
                                    info.setGroupNum(mGroupNum);
                                    groupInfos.add(info);
                                }
                            }
                        }
                    }
                    listener.onClick(this,groupInfos,mGroupNum,true);
                }
                break;
            default:
                break;
        }
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog,List<GroupInfo> groupInfos,String groupNum, boolean confirm);
    }


}

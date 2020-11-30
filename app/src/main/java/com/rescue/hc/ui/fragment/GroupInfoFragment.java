package com.rescue.hc.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.personal.framework.utils.CommonUtil;
import com.personal.framework.utils.GsonUtils;
import com.personal.framework.utils.PreferenceUtils;
import com.rescue.hc.R;
import com.rescue.hc.bean.ReccoData;
import com.rescue.hc.bean.command.GroupInfo;
import com.rescue.hc.bean.command.GroupInfoVo;
import com.rescue.hc.event.EventMessage;
import com.rescue.hc.lib.component.EventTag;
import com.rescue.hc.ui.adapter.GroupInfoAdapter;
import com.rescue.hc.ui.dialog.GroupInfoDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;

import static com.rescue.hc.lib.component.EventTag.BUS_ON_LINE_RECCO;
import static com.rescue.hc.lib.component.EventTag.GET_SP_DATA;
import static com.rescue.hc.lib.component.ReccoTag.DEFAULT_FIREMAN_NAME;
import static com.rescue.hc.lib.component.ReccoTag.DEFAULT_GROUP_NAME;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/11/07
 * @descibe none
 * </pre>
 */
public class GroupInfoFragment extends BaseFragment {
	@BindView(R.id.rv_list_group)
	RecyclerView recyclerView;
	@BindView(R.id.fab_menu_function)
	FloatingActionMenu mFabMenu;

	private Unbinder unbinder;
	private List<GroupInfo> mGroupInfoList;
	private GroupInfoAdapter mGroupInfoAdapter;
	//private List<List<TaskEditInfo>> lists;
	private List<GroupInfoVo> mGroupInfoVos;

	private Context mContext;



	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=getActivity();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_group_info, null);
		unbinder = ButterKnife.bind(this, view);
		initData();
		initView();
		return view;
	}

	private void initData() {
		mGroupInfoList = new ArrayList<>();
		mGroupInfoVos=new ArrayList<>();
		//lists = new ArrayList<>();
		//testEditInfo();
		Map<String, ?> stringMap = PreferenceUtils.getAll(mContext);
		if(stringMap!=null && stringMap.size()>0)
		{
			for (String key:stringMap.keySet())
			{
				String value=(String) stringMap.get(key);
				if(Objects.requireNonNull(value).contains(","))
				{
					String[] strings = value.split(",");

				}
			}
		}

		//mGroupInfoVos=testData();
	}

	private void initView() {
		initFab();
		initRecyclerView();
	}
	private void initFab() {
		mFabMenu.setClosedOnTouchOutside(true);
	}

	private void initRecyclerView() {
		LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(gridLayoutManager);
		DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
		itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_normal_list));
		recyclerView.addItemDecoration(itemDecoration);
		// TODO 非测试时将其放入到Module
		mGroupInfoAdapter = new GroupInfoAdapter(R.layout.group_info_list, mGroupInfoVos);
		recyclerView.setAdapter(mGroupInfoAdapter);
		mGroupInfoAdapter.setEmptyView(LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null));

		mGroupInfoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
				Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
			}
		});

//		mGroupInfoAdapter.setOnItemChildClickListener((adapter, view, position) -> {
//			ToastUtils.error(String.valueOf(position),false);
//		});
	}



	@OnClick({R.id.fab_add_group, R.id.fab_edit_group})
	public void onViewClicked(View view) {
		if (CommonUtil.isFastDoubleClick()) {
			return;
		}
		switch (view.getId()) {
			case R.id.fab_add_group:
				mGroupInfoList.clear();
				if(mGroupInfoVos!=null && mGroupInfoVos.size()>0)
				{
					for (GroupInfoVo gr:mGroupInfoVos) {
						GroupInfo groupInfo=new GroupInfo();
						groupInfo.setGroupNum(gr.getGroupNum());
						groupInfo.setPersonName(gr.getPersonName());
						groupInfo.setDevEui(gr.getDevEui());
						mGroupInfoList.add(groupInfo);
					}
				}

				if(mGroupInfoList!=null && mGroupInfoList.size()>0)
				{

					GroupInfoDialog groupInfoDialog=new GroupInfoDialog(getActivity(), R.style.TranPersonDialog, mGroupInfoList, new GroupInfoDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, List<GroupInfo> groupInfos,String groupNum, boolean confirm) {
                            if(confirm) {
                                if (groupInfos != null && groupInfos.size() > 0) {
                                	//String groupStr="";
                                    for (GroupInfo groupInfo : groupInfos) {
                                        String devEui = groupInfo.getDevEui();
                                        for (int i = 0, m = mGroupInfoList.size(); i < m; i++) {
                                            GroupInfoVo groupInfoVo = mGroupInfoVos.get(i);
                                            if (groupInfoVo.getDevEui().equals(devEui)) {
                                                groupInfoVo.setGroupNum(groupInfo.getGroupNum());
                                                mGroupInfoVos.set(i, groupInfoVo);
												//groupStr+=groupInfo.getDevEui()+",";
                                                mGroupInfoAdapter.notifyItemChanged(i);
                                            }
                                        }
                                    }
                                    // TODO: 2020/04/02
                                    String json = GsonUtils.toJson(groupInfos);
                                    PreferenceUtils.put(mContext,groupNum,json);//groupStr.substring(0,groupStr.length()-1)
                                    EventBus.getDefault().post(new EventMessage(groupInfos, EventTag.BUS_ON_LINE_RECCO));
                                    /*
                                    Object o = PreferenceUtils.get(mContext, "1", null);
                                    Type type=new TypeToken<List<GroupInfo>>(){}.getType();

                                    List<GroupInfo> groupInfoList=new ArrayList<>();
                                    groupInfoList=GsonUtils.fromJson(json,type);
                                    if(groupInfoList!=null && groupInfoList.size()>0)
                                    {

                                    }
                                    */
                                }
                            }
                            dialog.dismiss();
                        }
                    });
                    Window win = groupInfoDialog.getWindow();
                    win.setGravity(Gravity.CENTER);
//                    WindowManager.LayoutParams  params = new WindowManager.LayoutParams();
//                    params.x = 0;//设置x坐标
//                    params.y = 0;//设置y坐标
//                    Objects.requireNonNull(win).setAttributes(params);
                    groupInfoDialog.setCanceledOnTouchOutside(true);
					groupInfoDialog.show();
				}


				break;
			case R.id.fab_edit_group:
				//new SingleSendDialog(getActivity(), R.style.bulkDialog, (dialog, confirm) -> dialog.dismiss()).show();
				break;
			default:
				break;
		}
		// 关闭
		mFabMenu.toggle(false);
	}

	private void showTaskDetail(int position) {
		//new TaskDetailDialog(getActivity(), R.style.personInfoDialog, taskInfoList.get(position)).show();
	}


	private List<GroupInfoVo> testData() {

		List<GroupInfoVo> groupInfoVos=new ArrayList<>();
		GroupInfoVo groupInfoVo=new GroupInfoVo();
		groupInfoVo.setGroupNum(DEFAULT_GROUP_NAME);
		groupInfoVo.setPersonName(DEFAULT_FIREMAN_NAME);
		groupInfoVo.setDevEui("000001");
		ReccoData reccoData=new ReccoData();
		reccoData.setDevEui("000001");
		reccoData.setReccoId("1");
		reccoData.setAlarmState((byte)0);
		groupInfoVo.setReccoData(reccoData);
		groupInfoVos.add(groupInfoVo);


		groupInfoVo=new GroupInfoVo();
		groupInfoVo.setGroupNum(DEFAULT_GROUP_NAME);
		groupInfoVo.setPersonName(DEFAULT_FIREMAN_NAME);
		groupInfoVo.setDevEui("000002");
		reccoData=new ReccoData();
		reccoData.setDevEui("000002");
		reccoData.setReccoId("2");
		reccoData.setAlarmState((byte)0);
		groupInfoVo.setReccoData(reccoData);
		groupInfoVos.add(groupInfoVo);


		groupInfoVo=new GroupInfoVo();
		groupInfoVo.setGroupNum(DEFAULT_GROUP_NAME);
		groupInfoVo.setPersonName(DEFAULT_FIREMAN_NAME);
		groupInfoVo.setDevEui("000003");
		reccoData=new ReccoData();
		reccoData.setDevEui("000003");
		reccoData.setReccoId("3");
		reccoData.setAlarmState((byte)0);
		groupInfoVo.setReccoData(reccoData);
		groupInfoVos.add(groupInfoVo);

		return groupInfoVos;
	}

	private void testEditInfo() {
		/*
		for (int i = 0; i < 7; i++) {
			List<TaskEditInfo> taskEditInfoList = new ArrayList<>();
			TaskEditInfo taskEditInfo = new TaskEditInfo();
			taskEditInfo.setPersonName(0);
			taskEditInfoList.add(taskEditInfo);
			TaskEditInfo taskEditInfo1 = new TaskEditInfo();
			taskEditInfo1.setPersonName(1);
			taskEditInfoList.add(taskEditInfo1);
			TaskEditInfo taskEditInfo2 = new TaskEditInfo();
			taskEditInfo2.setPersonName(2);
			taskEditInfoList.add(taskEditInfo2);
			TaskEditInfo taskEditInfo3 = new TaskEditInfo();
			taskEditInfo3.setPersonName(3);
			taskEditInfoList.add(taskEditInfo3);
			TaskEditInfo taskEditInfo4 = new TaskEditInfo();
			taskEditInfo4.setPersonName(4);
			taskEditInfoList.add(taskEditInfo4);
			TaskEditInfo taskEditInfo5 = new TaskEditInfo();
			taskEditInfo5.setPersonName(5);
			taskEditInfoList.add(taskEditInfo5);
			TaskEditInfo taskEditInfo6 = new TaskEditInfo();
			taskEditInfo6.setPersonName(6);
			taskEditInfoList.add(taskEditInfo6);
			lists.add(taskEditInfoList);
		}

		 */
	}


	@Override
	public void onReceiveEvent(EventMessage event) {
		switch (event.getCode()) {
			case BUS_ON_LINE_RECCO:
				GroupInfoVo groupInfoVo=(GroupInfoVo) event.getData();
				if(groupInfoVo!=null)
				{
					if(!mGroupInfoList.contains(groupInfoVo))
					{
						mGroupInfoVos.add(groupInfoVo);
						mGroupInfoAdapter.notifyDataSetChanged();
					}
				}
				break;
			case GET_SP_DATA:
				ReccoData reccoData=(ReccoData) event.getData();
				if(reccoData!=null)
				{
					for(int i=0,m=mGroupInfoVos.size();i<m;i++)
					{
						GroupInfoVo infoVo = mGroupInfoVos.get(i);
						if(infoVo!=null)
						{
							if(infoVo.getDevEui().equals(reccoData.getDevEui()))
							{
								infoVo.setReccoData(reccoData);
								mGroupInfoVos.set(i,infoVo);
								mGroupInfoAdapter.notifyItemChanged(i);
								break;
							}
						}
					}
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (unbinder != null) {
			unbinder.unbind();
		}

	}
}

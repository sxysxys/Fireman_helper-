package com.rescue.hc.ui.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.reflect.TypeToken;
import com.personal.framework.utils.CommonUtil;
import com.personal.framework.utils.GsonUtils;
import com.personal.framework.utils.PreferenceUtils;
import com.rescue.hc.R;
import com.rescue.hc.bean.ReccoData;
import com.rescue.hc.bean.command.Fireman;
import com.rescue.hc.bean.command.FiremanReccoVo;
import com.rescue.hc.bean.command.GroupInfo;
import com.rescue.hc.bean.command.GroupInfoVo;
import com.rescue.hc.bean.command.ReccoCmdStatusInfo;
import com.rescue.hc.bean.util.ToastInfo;
import com.rescue.hc.enums.ReccoCmdEnum;
import com.rescue.hc.event.EventMessage;
import com.rescue.hc.lib.component.EventTag;
import com.rescue.hc.lib.util.ToastUtils;
import com.rescue.hc.presenter.home.HomeContract;
import com.rescue.hc.presenter.home.HomePresenter;
import com.rescue.hc.service.CommunicationService;
import com.rescue.hc.ui.adapter.FiremanReccoAdapter;
import com.rescue.hc.ui.dialog.BulkSendDialog;
import com.rescue.hc.ui.dialog.PersonInformationDialog;
import com.rescue.hc.ui.dialog.SingleReccoCmdDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

import static com.rescue.hc.lib.component.EventTag.BUS_UPDATE_GROUP_INFO;
import static com.rescue.hc.lib.component.EventTag.GET_DATA_TEST;
import static com.rescue.hc.lib.component.EventTag.GET_SP_DATA;
import static com.rescue.hc.lib.component.EventTag.NFC_INFO;
import static com.rescue.hc.lib.component.EventTag.NFC_UPDATE_FIREMAN;
import static com.rescue.hc.lib.component.EventTag.SET_NFC_STATUS;
import static com.rescue.hc.lib.component.EventTag.TOAST_INFO;
import static com.rescue.hc.lib.component.EventTag.UPDATE_HOME_ALL_DATA;
import static com.rescue.hc.lib.component.EventTag.UPDATE_HOME_SINGLE_DATA;
import static com.rescue.hc.lib.component.ReccoTag.DEFAULT_FIREMAN_NAME;
import static com.rescue.hc.lib.component.ReccoTag.DEFAULT_GROUP_NAME;
import static com.rescue.hc.lib.component.UartTag.connectCounter;

/**
 * @author szc
 * @date 2018/11/07
 * @describe 添加描述
 */
public class HomeFragment extends BaseFragment implements HomeContract.View {
	@BindView(R.id.rv_fireman_list_home)
	RecyclerView mRecyclerView;
	@BindView(R.id.fab_menu_function)
    FloatingActionMenu mFabMenu;
	@BindView(R.id.relative)
	RelativeLayout relative;

	private Unbinder unbinder;

	private CommunicationService communicationService;
	private boolean isBound;

	public static FiremanReccoAdapter mFiremanReccoAdapter;

	//private FiremanWatchAdapter mFiremanWatchAdapter;

	private   List<GroupInfo> mGroupInfoList;
	public static List<FiremanReccoVo> mFiremanReccoVoList;


	private PopupWindow popupWindow;
	private TextView tvInfo;
	private HomePresenter homePresenter;
	private ReccoCmdStatusInfo bulkCmdStatus;
	//private NfcInfoSetDialog nfcInfoSetDialog;
	//private MessageDialog joinLeaveDialog;

	private HashMap<String, Integer> mDevEuiOnLine=new HashMap<String, Integer>();



	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, null);
		unbinder = ButterKnife.bind(this, view);
		initData();
		initView();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	private void initFiremanReccoVoList()
	{
		FiremanReccoVo firemanReccoVo=new FiremanReccoVo();

		Fireman fireman=new Fireman();
		fireman.setSerialNumber("00000001");
		fireman.setName("李一");
		firemanReccoVo.setFireman(fireman);

		ReccoData reccoData=new ReccoData();
		reccoData.setDevEui("00000001");
		reccoData.setReccoId("1");
		reccoData.setGesture((byte)0);
		reccoData.setTemperature(18.0f);
		reccoData.setAlarmState((byte) 1);
		reccoData.setElectric((byte) 18);
		reccoData.setLatitude((double) 32.4456f);
		reccoData.setLongitude((double) 137.4455f);
		firemanReccoVo.setReccoData(reccoData);
		firemanReccoVo.setCmdStatusInfo(new ReccoCmdStatusInfo(ReccoCmdEnum.NONE_CMD.getCmd()));
		firemanReccoVo.setQueryCounter(connectCounter);
		mFiremanReccoVoList.add(firemanReccoVo);

		firemanReccoVo=new FiremanReccoVo();
		fireman=new Fireman();
		fireman.setSerialNumber("00000002");
		fireman.setName("李二");
		firemanReccoVo.setFireman(fireman);
		reccoData=new ReccoData();
		reccoData.setDevEui("00000002");
		reccoData.setReccoId("2");
		reccoData.setGesture((byte)0);
		reccoData.setTemperature(18.0f);
		reccoData.setElectric((byte) 28);
		reccoData.setAlarmState((byte) 2);
		reccoData.setLatitude((double) 32.4456f);
		reccoData.setLongitude((double) 137.4455f);
		firemanReccoVo.setReccoData(reccoData);
		firemanReccoVo.setCmdStatusInfo(new ReccoCmdStatusInfo(ReccoCmdEnum.NONE_CMD.getCmd()));
		firemanReccoVo.setQueryCounter(connectCounter);
		mFiremanReccoVoList.add(firemanReccoVo);

		firemanReccoVo=new FiremanReccoVo();
		fireman=new Fireman();
		fireman.setName("李三");
		fireman.setSerialNumber("00000003");
		firemanReccoVo.setFireman(fireman);
		reccoData=new ReccoData();
		reccoData.setDevEui("00000003");
		reccoData.setReccoId("3");
		reccoData.setGesture((byte)0);
		reccoData.setTemperature(18.0f);
		reccoData.setElectric((byte) 38);
		reccoData.setAlarmState((byte) 4);
		reccoData.setLatitude((double) 32.4456f);
		reccoData.setLongitude((double) 137.4455f);
		firemanReccoVo.setReccoData(reccoData);
		firemanReccoVo.setQueryCounter(connectCounter);
		firemanReccoVo.setCmdStatusInfo(new ReccoCmdStatusInfo(ReccoCmdEnum.NONE_CMD.getCmd()));
		mFiremanReccoVoList.add(firemanReccoVo);

        firemanReccoVo=new FiremanReccoVo();
        fireman=new Fireman();
        fireman.setName("李四");
        fireman.setSerialNumber("00000004");
        firemanReccoVo.setFireman(fireman);
        reccoData=new ReccoData();
        reccoData.setDevEui("00000003");
        reccoData.setReccoId("4");
		reccoData.setGesture((byte)0);
        reccoData.setTemperature(18.0f);
		reccoData.setElectric((byte) 48);
        reccoData.setAlarmState((byte) 0);
        reccoData.setLatitude((double) 32.4456f);
        reccoData.setLongitude((double) 137.4455f);
        firemanReccoVo.setReccoData(reccoData);
		firemanReccoVo.setCmdStatusInfo(new ReccoCmdStatusInfo(ReccoCmdEnum.NONE_CMD.getCmd()));
		firemanReccoVo.setQueryCounter(connectCounter);
        mFiremanReccoVoList.add(firemanReccoVo);


	}

	private void initData() {

		mGroupInfoList=new ArrayList<>();
		mFiremanReccoVoList = new ArrayList<>();
		homePresenter = new HomePresenter(this, this);
		bulkCmdStatus = new ReccoCmdStatusInfo(ReccoCmdEnum.NONE_CMD.getCmd());

//		nfcInfoSetDialog = new NfcInfoSetDialog(getActivity(), R.style.personInfoDialog, new NfcInfoSetDialog.OnCloseListener() {
//			@Override
//			public void onClick(Dialog dialog, boolean confirm, Fireman fireman) {
//				if (confirm) {
//					if (!nfcInfoSetDialog.getDisplayStatus()) {
//						return;
//					}
//					nfcInfoSetDialog.setTvSubmit();
//					EventBus.getDefault().post(new EventMessage(fireman, EventTag.SET_NFC_SUBMIT));
//				} else {
//					EventBus.getDefault().post(new EventMessage(false, EventTag.SET_NFC_CANCEL));
//					nfcInfoSetDialog.clearInfoMeg();
//					nfcInfoSetDialog.dismiss();
//				}
//			}
//		});

//		joinLeaveDialog = new MessageDialog(getActivity(), R.style.baseDialog, new MessageDialog.onMessageClickListener() {
//			@Override
//			public void onClick(Dialog dialog, boolean confirm, Fireman fireman, int id) {
//				if (confirm) {
//					if (id == -1) {
//						dealJoinConfirm(fireman);
//					} else {
//						dealLeaveDialog(fireman, id);
//					}
//				}
//				dialog.dismiss();
//			}
//		});

		//initFiremanReccoVoList();
		mGroupInfoList=getOriginGroupInfo();

	}

	private List<GroupInfo> getOriginGroupInfo()
	{
		Context context = Objects.requireNonNull(getActivity()).getApplicationContext();
		List<GroupInfo> list=new ArrayList<>();
		Type type=new TypeToken<List<GroupInfo>>(){}.getType();
		if(context!=null)
		{
			Map<String, ?> all = PreferenceUtils.getAll(context);
			if(all==null || all.size()==0)
			{
				list=null;
			}
			else
			{
				for (String key:all.keySet())
				{
					String string = (String) all.get(key);
					List<GroupInfo> groupInfoList=new ArrayList<>();
					groupInfoList= GsonUtils.fromJson(string,type);
					if(groupInfoList!=null && groupInfoList.size()>0)
					{
						list.addAll(groupInfoList);
					}
				}
			}
		}
		return list;

	}
	private void initView() {
		initFab();
		initRecyclerView();
		initTip();
		if (communicationService == null) {
			bindCommunicationService();
		}
	}






	private void initTip() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		View view = getLayoutInflater().inflate(R.layout.tip, null, false);
		tvInfo = view.findViewById(R.id.tv_info);
		popupWindow = new PopupWindow(view, display.getWidth() / 5, display.getHeight() / 4, true);
		popupWindow.setAnimationStyle(R.style.tipAnimation);
	}

	private void initFab() {
		mFabMenu.setClosedOnTouchOutside(true);
	}

	private void initRecyclerView() {
		GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
		mRecyclerView.setLayoutManager(gridLayoutManager);

		// TODO 非测试时将其放入到Module
		mFiremanReccoAdapter = new FiremanReccoAdapter(R.layout.item_fireman_recco, mFiremanReccoVoList);
		((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
		mRecyclerView.setAdapter(mFiremanReccoAdapter);
		mFiremanReccoAdapter.setEmptyView(LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null));
		mFiremanReccoAdapter.setOnItemChildClickListener((adapter, view, position) -> {
			switch (view.getId()) {
				case R.id.miv_close_fireman:
					//showDisconnectDialog(position);
					break;
				case R.id.ib_fireman_status:
					if (CommonUtil.isFastDoubleClick()) {
						return;
					}
					//ToastUtils.info(mFiremanReccoVoList.get(position).getFireman().getSerialNumber(),false);
                    new SingleReccoCmdDialog(getActivity(), mFiremanReccoVoList.get(position).getCmdStatusInfo(), R.style.singleDialog, (dialog, cmdStatusInfo) -> {
                        mFiremanReccoVoList.get(position).setCmdStatusInfo(cmdStatusInfo);
                        mFiremanReccoVoList.get(position).setAnimationState((byte) 1);
                        mFiremanReccoAdapter.notifyItemChanged(position);
                        if (communicationService != null) {
                            //ToastUtils.info(String.valueOf(cmdStatusInfo.getCmd()),false);
                            communicationService.sendSingleCmdFunction(mFiremanReccoVoList.get(position).getReccoData().getDevEui(), cmdStatusInfo);

                        }
                        dialog.dismiss();
                    }).show();
					break;
				case R.id.cd_fireman:
					if (CommonUtil.isFastDoubleClick()) {
						return;
					}
					new SingleReccoCmdDialog(getActivity(), mFiremanReccoVoList.get(position).getCmdStatusInfo(), R.style.singleDialog, (dialog, cmdStatusInfo) -> {

						//cmdStatusInfo.setCmd(1);
						mFiremanReccoVoList.get(position).setCmdStatusInfo(cmdStatusInfo);
						//mFiremanReccoVoList.get(position).getReccoData().setAlarmState((byte) 1);
						//mFiremanReccoVoList.get(position).getReccoData().setGesture((byte) 2);
						mFiremanReccoVoList.get(position).setAnimationState((byte) 1);
						mFiremanReccoAdapter.notifyItemChanged(position);
						if (communicationService != null) {
						    //ToastUtils.info(String.valueOf(cmdStatusInfo.getCmd()),false);
							communicationService.sendSingleCmdFunction(mFiremanReccoVoList.get(position).getReccoData().getDevEui(), cmdStatusInfo);

						}
						dialog.dismiss();
					}).show();
					break;
				case R.id.ll_location:
//					if (CommonUtil.isFastDoubleClick()) {
//						return;
//					}
//					EventBus.getDefault().post(new EventMessage(mFiremanReccoVoList.get(position).getFireman().getSerialNumber() + "/" +
//							mFiremanReccoVoList.get(position).getFireman().getName(), EventTag.LOCATION_ID));
//					if (getActivity() != null) {
//						((MainActivity) getActivity()).switchFragment();
//					}
					break;
				default:
					break;
			}
		});
		mFiremanReccoAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
			@Override
			public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
				switch (view.getId()) {
					case R.id.ib_fireman_status:
						// 显示消防员的详细个人信息
						Fireman fireman = ((FiremanReccoVo) adapter.getData().get(position)).getFireman();
						showFiremanDialog(fireman,position);
						break;
					default:
						break;
				}
				return false;
			}
		});
	}

	@OnClick({R.id.fab_all_send, R.id.fab_single_send, R.id.fab_connect_setting})
	public void onViewClicked(View view) {
		if (CommonUtil.isFastDoubleClick()) {
			return;
		}
		switch (view.getId()) {
			case R.id.fab_all_send:
				new BulkSendDialog(getActivity(), bulkCmdStatus, R.style.bulkDialog, (dialog, cmdStatusInfo) -> {
					bulkCmdStatus = cmdStatusInfo;
                    for (int i = 0; i < mFiremanReccoVoList.size(); i++) {
                        mFiremanReccoVoList.get(i).setCmdStatusInfo(new ReccoCmdStatusInfo(cmdStatusInfo.getCmd()));
                        mFiremanReccoVoList.get(i).setAnimationState((byte) 1);
//                        mFiremanReccoAdapter.notifyItemChanged(i);
                    }
                    mFiremanReccoAdapter.notifyDataSetChanged();
                    //ToastUtils.success("群体" + cmdStatusInfo.getCmd(), false);
					if (communicationService != null) {
						communicationService.sendBulkCmdFunction(cmdStatusInfo);
						//communicationService.sendSingleCmdFunction();

					}
					dialog.dismiss();
				}).show();
				break;
			case R.id.fab_single_send:
				//new SingleSendDialog(getActivity(), R.style.bulkDialog, (dialog, confirm) -> dialog.dismiss()).show();
				break;
			case R.id.fab_connect_setting:
//				if (!nfcInfoSetDialog.isShowing()) {
//					nfcInfoSetDialog.show();
//				}
				//showAlarmMessage("预报警");
                //AudioUtils.getInstance().speak("请求紧急救援");
//                if(communicationService!=null)
//                {
//					communicationService.sendSingleCmdFunction("8D00000000000001",new ReccoCmdStatusInfo((byte)1));
//                }
				break;
			default:
				break;
		}
		// 关闭
		mFabMenu.toggle(false);
	}

	private void showAlarmMessage(String msg) {
		showTip(msg);
		homePresenter.delayTipShow();
	}

	// 显示消防员个人信息的对话框
	@SuppressLint("SetTextI18n")
	private void showFiremanDialog(Fireman fireman,int position) {
		if(position<0)
		{
			return;
		}
		new PersonInformationDialog(getActivity(), R.style.personInfoDialog, fireman, (dialog, man, confirm) -> {
			if(confirm)
			{
				//ToastUtils.info(man.getName(),false);
				mFiremanReccoVoList.get(position).setFireman(man);
				mFiremanReccoAdapter.notifyItemChanged(position);
				if(mGroupInfoList!=null && mGroupInfoList.size()>0)
                {
                    for(int i=0,m=mGroupInfoList.size();i<m;i++)
                    {
                        if(mFiremanReccoVoList.get(position).getFireman().getSerialNumber().equals(mGroupInfoList.get(i).getDevEui()))
                        {
                            mGroupInfoList.get(i).setPersonName(man.getName());
                            break;
                        }
                    }

                }
			}
			dialog.dismiss();

		}).show();
	}

	// 当点击断开按钮时弹出断开连接对话框
	private void showDisconnectDialog(int position) {
		String serialNumberStr = mFiremanReccoVoList.get(position).getFireman().getSerialNumber();
		MaterialDialog mUnbindDialog = new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
				.iconRes(R.mipmap.ic_warning)
				.limitIconToDefaultSize()
				.title("警告")
				.content("确定断开与编号为： " + serialNumberStr + " 的连接?")
				.contentGravity(GravityEnum.CENTER)
				.positiveText(R.string.confirm)
				.positiveColorRes(R.color.positive_color)
				.negativeText(R.string.cancel)
				.negativeColorRes(R.color.negative_color)
				.buttonRippleColorRes(R.color.button_ripple_color)
				.onPositive((dialog, which) -> {
					if (communicationService != null) {
//							showWaitingDlg("解绑中...");

						//communicationService.addUnbindId(mFiremanWatchVoList.get(position).getFireman().getSerialNumber());
						//communicationService.leaveBattle(Integer.parseInt(mFiremanWatchVoList.get(position).getFireman().getSerialNumber()));
					}
					EventBus.getDefault().post(new EventMessage(mFiremanReccoVoList.get(position).getFireman().getSerialNumber() + "-"
							+ mFiremanReccoVoList.get(position).getFireman().getName(), EventTag.LEAVE_FIREMAN));
					mFiremanReccoVoList.remove(position);
					mFiremanReccoAdapter.notifyDataSetChanged();
				}).build();
		mUnbindDialog.show();
	}

	public void showTip(String msg) {
		tvInfo.setText(msg);
		popupWindow.showAtLocation(relative, Gravity.START | Gravity.BOTTOM, 0, 0);
	}

	@Override
	public void hideTip() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	private void showNfcSetInfo(Fireman fireman) {
		//nfcInfoSetDialog.setInfoMeg(fireman);
	}

	private void receiveNFCInfo(Fireman fireman) {
//		if (TextUtils.isEmpty(fireman.getSerialNumber())) {
//			ToastUtils.error("编号为空，加入失败", false);
//			return;
//		}
//		if (nfcInfoSetDialog.isShowing()) {
//			showNfcSetInfo(fireman);
//			return;
//		}
//		for (int i = 0; i < mFiremanReccoVoList.size(); i++) {
//			if (fireman.getSerialNumber().equals(mFiremanReccoVoList.get(i).getFireman().getSerialNumber())) {
//				showLeaveDialog(fireman, i);
//				return;
//			}
//		}
//		showJoinDialog(fireman);
	}

	private void showJoinDialog(Fireman fireman) {
//		if (joinLeaveDialog.isShowing()) {
//			return;
//		}
//		joinLeaveDialog.setContent(fireman.getName() + "请求加入作战？")
//				.setFireman(fireman)
//				.setId(-1)
//				.show();
	}

	/**
	 * 根据devEui获取分组信息和姓名
	 * @param devEui
	 */
	private Map<String,String> getGroupAndName(String devEui)
	{
		Map<String,String> map=new HashMap<>();
		if(mGroupInfoList!=null && mGroupInfoList.size()>0)
		{
			for (GroupInfo groupInfo:mGroupInfoList)
			{
				if(groupInfo.getDevEui().equals(devEui))
				{
					map.put("groupNum",groupInfo.getGroupNum());
					map.put("name",groupInfo.getPersonName());
					break;
				}
			}
		}
		return null;
	}

	private void dealJoinConfirm(ReccoData reccoData) {


		FiremanReccoVo firemanReccoVo = new FiremanReccoVo();
		Fireman fireman=new Fireman();
		String devEui = reccoData.getDevEui();
		fireman.setSerialNumber(devEui);

		Map<String, String> groupAndName = getGroupAndName(devEui);
		if(groupAndName!=null && groupAndName.size()>0)
		{
			fireman.setName(groupAndName.get("name"));
			fireman.setGroupNumber(groupAndName.get("groupNum"));
		}
		else
		{
			fireman.setName(DEFAULT_FIREMAN_NAME);
			fireman.setGroupNumber(DEFAULT_GROUP_NAME);
		}

		ReccoCmdStatusInfo cmdStatusInfo = new ReccoCmdStatusInfo(ReccoCmdEnum.NONE_CMD.getCmd());
		firemanReccoVo.setFireman(fireman);
		firemanReccoVo.setReccoData(reccoData);
		firemanReccoVo.setCmdStatusInfo(cmdStatusInfo);
//		firemanReccoVo.setQueryCounter(0);
//		firemanReccoVo.setQueryCounter(connectCounter + 1);
		mFiremanReccoVoList.add(firemanReccoVo);
		if (communicationService != null) {
			communicationService.addBindId(devEui);
			communicationService.joinBattle(devEui);

		}
		//EventBus.getDefault().post(new EventMessage(fireman.getSerialNumber() + "-" + fireman.getName(), EventTag.JOIN_FIREMAN));
		mFiremanReccoAdapter.notifyDataSetChanged();
	}

	private void dealLeaveDialog(Fireman fireman, int id) {
		mFiremanReccoVoList.remove(id);
		if (communicationService != null) {
			communicationService.leaveBattle(fireman.getSerialNumber());

		}
		EventBus.getDefault().post(new EventMessage(fireman.getSerialNumber() + "-" + fireman.getName(), EventTag.LEAVE_FIREMAN));
		mFiremanReccoAdapter.notifyDataSetChanged();
	}

	private void showLeaveDialog(Fireman fireman, int id) {

//		if (joinLeaveDialog.isShowing()) {
//			return;
//		}
//		joinLeaveDialog.setContent(fireman.getName() + "请求取消作战？")
//				.setFireman(fireman)
//				.setId(id)
//				.show();
	}

	private ServiceConnection communicationConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			communicationService = ((CommunicationService.CommunicationBinder) binder).getService();
			communicationService.setActivity(getActivity());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			communicationService = null;
		}
	};

	private void bindCommunicationService() {
		Intent bindingIntent = new Intent(getActivity(), CommunicationService.class);
		if (getActivity() != null) {
			isBound = getActivity().bindService(bindingIntent, communicationConnection, Context.BIND_AUTO_CREATE);
		}
	}

	private void analysisSpData(EventMessage event) {
		ReccoData reccoData= (ReccoData) event.getData();
		if(reccoData==null)
		{
			return;
		}

		String devEui = reccoData.getDevEui();
		if(mDevEuiOnLine!=null)
		{
			if(!mDevEuiOnLine.containsKey(devEui))
			{
				mDevEuiOnLine.put(devEui,Integer.parseInt(reccoData.getReccoId()));
				dealJoinConfirm(reccoData);
				// TODO: 2020/04/04 需要修改

				GroupInfoVo groupInfoVo=new GroupInfoVo();
				groupInfoVo.setDevEui(devEui);
				groupInfoVo.setGroupNum(DEFAULT_GROUP_NAME);
				groupInfoVo.setPersonName(DEFAULT_FIREMAN_NAME);
				groupInfoVo.setReccoData(reccoData);
				EventBus.getDefault().post(new EventMessage(groupInfoVo, EventTag.BUS_ON_LINE_RECCO));

			}
			else
			{
				for (int i = 0; i < mFiremanReccoVoList.size(); i++) {
					if (mFiremanReccoVoList.get(i).getFireman().getSerialNumber().equals(devEui)) {
						Timber.d(String.valueOf(mFiremanReccoVoList.get(i).getReccoData().getAlarmState()));
						mFiremanReccoVoList.get(i).setReccoData(reccoData);
						mFiremanReccoVoList.get(i).setQueryCounter(0);
						mFiremanReccoAdapter.notifyItemChanged(i);
						break;
					}
				}
			}
		}



	}

	@Override
	public void onReceiveEvent(EventMessage event) {
		switch (event.getCode()) {
			case GET_DATA_TEST:
//				byte[] data = (byte[]) event.getData();
//				if(data!=null)
//				{
//					System.out.println(StringUtil.bytesToHexString(data));
//				}
				break;
			case NFC_INFO:
				//receiveNFCInfo((Fireman) event.getData());
				break;
			case GET_SP_DATA:
				analysisSpData(event);
				break;
			case UPDATE_HOME_ALL_DATA:
//				WatchInfo watchInfo1 = (WatchInfo) event.getData();
//				if (watchInfo1 == null || watchInfo1.getList() == null) {
//					return;
//				}
//				List<WatchCmdInfo> cmdInfoList1 = watchInfo1.getList();
//				for (WatchCmdInfo watchCmdInfo : cmdInfoList1) {
//					for (int i = 0; i < mFiremanReccoVoList.size(); i++) {
//						if (mFiremanReccoVoList.get(i).getFireman().getSerialNumber().equals(String.valueOf(watchCmdInfo.getWatchId()))) {
//							mFiremanReccoVoList.get(i).setQueryCounter(watchCmdInfo.getQueryCounter());
//							break;
//						}
//					}
//				}
//				mFiremanReccoAdapter.notifyDataSetChanged();
				break;
			case UPDATE_HOME_SINGLE_DATA:
//				WatchCmdInfo watchCmdInfo = (WatchCmdInfo) event.getData();
//				if (watchCmdInfo == null) {
//					return;
//				}
//				for (int i = 0; i < mFiremanReccoVoList.size(); i++) {
//					if (mFiremanReccoVoList.get(i).getFireman().getSerialNumber().equals(String.valueOf(watchCmdInfo.getWatchId()))) {
//						if (mFiremanReccoVoList.get(i).getQueryCounter() > connectCounter) {
//							return;
//						}
//						mFiremanReccoVoList.get(i).setQueryCounter(watchCmdInfo.getQueryCounter());
//						mFiremanReccoAdapter.notifyItemChanged(i);
//						break;
//					}
//				}
//				break;

				if (event.getData() == null) {
					return;
				}
				List<String> watchIdList = (List<String>) event.getData();
				if (watchIdList == null) {
					return;
				}
				for (int i = 0; i < watchIdList.size(); i++) {
					for (int j = 0; j < mFiremanReccoVoList.size(); j++) {
						if (watchIdList.get(i).equals(mFiremanReccoVoList.get(j).getFireman().getSerialNumber())) {
							mFiremanReccoVoList.get(j).setQueryCounter(CommunicationService.maxCounter);
							mFiremanReccoAdapter.notifyItemChanged(j);
							break;
						}
					}
				}
				break;
			case TOAST_INFO:
				showToast(event);
				break;
			case SET_NFC_STATUS:
				//nfcInfoSetDialog.clearInfoMeg();
				//nfcInfoSetDialog.dismiss();
				break;
			case NFC_UPDATE_FIREMAN:
//				Fireman fireman = (Fireman) event.getData();
//				if (fireman == null) {
//					return;
//				}
//				for (int i = 0; i < mFiremanReccoVoList.size(); i++) {
//					if (mFiremanReccoVoList.get(i).getFireman().getSerialNumber().equals(fireman.getSerialNumber())) {
//						mFiremanReccoVoList.get(i).setFireman(fireman);
//						break;
//					}
//				}
			case BUS_UPDATE_GROUP_INFO://更新分组信息
				List<GroupInfo> groupInfos=(List<GroupInfo>)event.getData();
				if(groupInfos!=null && groupInfos.size()>0)
				{
					for (GroupInfo groupInfo:groupInfos
						 ) {
						for (int i = 0; i < mFiremanReccoVoList.size(); i++) {
							Fireman fireman = mFiremanReccoVoList.get(i).getFireman();

							if (fireman.getSerialNumber().equals(groupInfo.getDevEui())) {
								fireman.setGroupNumber(groupInfo.getGroupNum());
								mFiremanReccoVoList.get(i).setFireman(fireman);
								mFiremanReccoAdapter.notifyItemChanged(i);
							}
						}
					}
				}
				break;
			default:
				break;
		}
	}


	private void showToast(EventMessage event) {
		ToastInfo toastInfo = (ToastInfo) event.getData();
		if (toastInfo == null) {
			return;
		}
		if (toastInfo.getType() == 1) {
			ToastUtils.success(toastInfo.getMsg(), toastInfo.isWithIcon());
		} else if (toastInfo.getType() == 2) {
			ToastUtils.error(toastInfo.getMsg(), toastInfo.isWithIcon());
		} else if (toastInfo.getType() == 3) {
			ToastUtils.info(toastInfo.getMsg(), toastInfo.isWithIcon());
		}
	}

    @Override
	public void onDestroyView() {
		super.onDestroyView();


		if (unbinder != null) {
			unbinder.unbind();
		}
		if (homePresenter != null) {
			homePresenter = null;
		}
		if (communicationService != null) {
			communicationService.closeService();
			if (isBound) {
				if (getActivity() != null) {
					getActivity().unbindService(communicationConnection);
				}
				isBound = false;
			}
			communicationService = null;
		}
//        if(mGroupInfoList!=null && mGroupInfoList.size()>0)
//        {
//            final Map<String, List<GroupInfo>> collect;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                collect = mGroupInfoList.stream().collect(Collectors.groupingBy(GroupInfo::getGroupNum));
//                if(collect!=null && collect.size()>0)
//                {
//                    for(Map.Entry<String, List<GroupInfo>> entry : collect.entrySet()){
//                        String json = GsonUtils.toJson(entry.getValue());
//                        PreferenceUtils.put(Objects.requireNonNull(getActivity()).getApplicationContext(),entry.getKey(),json);
//                    }
//                }
//            }
//        }

//		if (joinLeaveDialog != null) {
//			joinLeaveDialog = null;
//		}
	}

	public boolean getCounter() {
		return mFiremanReccoVoList.size() != 0;
	}

	public List<String> getFiremanReccoVoList() {
		List<String> list = new ArrayList<>();
		for (FiremanReccoVo reccoVo : mFiremanReccoVoList) {
			list.add(reccoVo.getFireman().getSerialNumber() + "-" + reccoVo.getFireman().getName());
		}
		return list;
	}

	public String getFiremanWatchName(String watchId) {
		String name = "";
		if (TextUtils.isEmpty(watchId)) {
			return name;
		}
		for (FiremanReccoVo reccoVo : mFiremanReccoVoList) {
			if (reccoVo.getFireman().getSerialNumber().equals(watchId)) {
				return reccoVo.getFireman().getName();
			}
		}
		return name;
	}
}

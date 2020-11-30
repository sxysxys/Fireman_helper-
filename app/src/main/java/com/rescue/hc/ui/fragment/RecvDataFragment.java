package com.rescue.hc.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.personal.framework.utils.StringUtil;
import com.rescue.hc.R;
import com.rescue.hc.event.EventMessage;
import com.rescue.hc.lib.component.EventTag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class RecvDataFragment extends BaseFragment {

    private Unbinder unbinder;


    @BindView(R.id.consoleText)
    TextView mDumpTextView;
    @BindView(R.id.demoScroller)
    ScrollView mScrollView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recv_data, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEventMainThread(EventMessage message) throws InterruptedException {
        switch (message.getCode()) {
            case EventTag.GET_DATA_TEST:
            case EventTag.BUS_SEND_CMD_DATA:
                String str= (String) message.getData();
                if(!StringUtil.isEmpty(str))
                {
                    mDumpTextView.append(str);
                    mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
                    //System.out.println(str);
                }
                break;
            default:
                break;
        }

    }






}

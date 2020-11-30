package com.rescue.hc.ui.adapter;


import android.graphics.Color;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.rescue.hc.R;
import com.rescue.hc.bean.command.FiremanReccoVo;
import com.rescue.hc.lib.util.AnimationUtils;
import com.rescue.hc.lib.util.ConvertUtils;
import com.rescue.hc.service.CommunicationService;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;

import static com.rescue.hc.lib.component.UartTag.connectCounter;

/**
 * @author DevCheng
 * @description
 * @date 2018/07/10
 */

public class FiremanReccoAdapter extends BaseQuickAdapter<FiremanReccoVo, BaseViewHolder> {
//	private Animation animationIn = AnimationUtils.getAlphaAnimationIn(1000);
//	private Animation animationOut = AnimationUtils.getAlphaAnimationOut(1000);

//	private ScaleAnimation animation=null;

    public FiremanReccoAdapter(int layoutResId, @Nullable List<FiremanReccoVo> data) {
        super(layoutResId, data);
//		animation=initAnim();
    }


    @Override
    protected void convert(BaseViewHolder helper, final FiremanReccoVo item) {
        MaterialIconView mivConnectStatus = helper.getView(R.id.miv_connect_status);

        ImageButton mImageButton = helper.getView(R.id.ib_fireman_status);
        // TODO 闪烁效果无效，记得找出原因修改
//        monitorConnect(mivConnectStatus, item.getQueryCounter(), mImageButton);
		boolean b = item.getQueryCounter() >= CommunicationService.maxCounter;
		monitorConnect(mivConnectStatus, b);
		if (b) {
		    mImageButton.setBackgroundColor(Color.GRAY);
        }
        // 每次都创建一个新的对象
        ScaleAnimation animation = initAnim();
        if (mImageButton != null && animation != null) {
            mImageButton.startAnimation(animation);
        }


        if (item.getFireman() != null) {
            helper.setText(R.id.tv_serial_number, item.getFireman().getSerialNumber());
            if (item.getFireman().getName().isEmpty()) {
                helper.setText(R.id.tv_name, "(无)");
            } else {
                helper.setText(R.id.tv_name, "(" + item.getFireman().getName() + ")");
            }
            if (item.getFireman().getGroupNumber().isEmpty()) {
                helper.setText(R.id.tv_steps, "未分组");
            } else {
                helper.setText(R.id.tv_steps, item.getFireman().getGroupNumber());
            }
        }

        if (item.getReccoData() != null) {
            helper.setText(R.id.tv_temperate, isEmptyStr(item.getReccoData().getTemperature()));
            helper.setText(R.id.tv_electric, isEmptyStr(item.getReccoData().getElectric()));
            if (item.getReccoData().getLongitude() == null || item.getReccoData().getLatitude() == null) {
                helper.setText(R.id.tv_longitude, "");
            } else {
                helper.setText(R.id.tv_longitude, "(" + String.format(Locale.CHINA, "%.6f", item.getReccoData().getLongitude()) + "," +
                        String.format(Locale.CHINA, "%.6f", item.getReccoData().getLatitude()) + ")");
            }
            byte alarmState = item.getReccoData().getAlarmState();

            byte bit = GetBit(alarmState, 7);
            if (bit == 1) {
                helper.setText(R.id.tv_message, ConvertUtils.getReccoAlarmState(5));
            } else {
                helper.setText(R.id.tv_message, ConvertUtils.getReccoAlarmState((alarmState & 0x70) >> 4));
            }


            setFiremanReccoState(item.getReccoData().getGesture(), alarmState, mImageButton);
            if (item.getAnimationState() == 1) {
                animation.start();
            } else {
                animation.cancel();
            }
        }
        helper.addOnClickListener(R.id.miv_close_fireman);
        helper.addOnClickListener(R.id.ib_fireman_status).addOnLongClickListener(R.id.ib_fireman_status);
        helper.addOnClickListener(R.id.cd_fireman);
        helper.addOnClickListener(R.id.ll_location);
    }

    private void monitorConnect(final MaterialIconView materialIconView, boolean b) {
        Animation animationIn = AnimationUtils.getAlphaAnimationIn(1000);
        Animation animationOut = AnimationUtils.getAlphaAnimationOut(1000);
        animationIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            	if (!b)
                	materialIconView.startAnimation(animationOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            	if (!b)
                	materialIconView.startAnimation(animationIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // 将图像置成灰色
        if (b) {
            setDisconnected(materialIconView);
        } else {
            setConnected(materialIconView);
        }
		materialIconView.startAnimation(animationIn);
    }

    private byte GetBit(byte b, int index) {
        return (byte) (((b & (1 << index)) > 0) ? 1 : 0);
    }

    // 设置状态显示图片为连接
    private void setConnected(MaterialIconView materialIconView) {
        materialIconView.setColorResource(R.color.fireman_connected_color);
        materialIconView.setIcon(MaterialDrawableBuilder.IconValue.CIRCLE);

    }

    private void setDisconnected(MaterialIconView materialIconView) {
        materialIconView.setColorResource(R.color.fireman_disconnect_color);
        materialIconView.setIcon(MaterialDrawableBuilder.IconValue.ALERT_CIRCLE);
    }

    private String isEmptyStr(Object str) {
        if (str == null) {
            return "";
        } else if (str instanceof Float) {
            return String.format(Locale.CHINA, "%.0f", (Float) str);
        } else {
            return String.valueOf(str);
        }
    }

    private void setFiremanReccoState(int index, byte type, final ImageButton imageButton) {
        if (type != 0)//有报警
        {
            byte bit = GetBit(type, 7);
            if (bit == 1) {
                Glide
                        .with(mContext)
                        .load(R.drawable.dr_strong_alarm)
                        .into(imageButton);
            } else {
                byte alarmType = (byte) (type & 0x70);
                switch (alarmType) {
                    case 0x10://预报警
                        Glide
                                .with(mContext)
                                .load(R.drawable.dr_pre_alram)
                                .into(imageButton);
                        break;
                    case 0x20://自动强报警
                    case 0x30://手动强报警
                        Glide
                                .with(mContext)
                                .load(R.drawable.dr_strong_alarm)
                                .into(imageButton);
                        break;
//                    case 5://高温报警
//                        Glide
//                                .with(mContext)
//                                .load(R.drawable.dr_strong_alarm)
//                                .into(imageButton);
//                        break;
                    case 0x40://低压报警
                        Glide
                                .with(mContext)
                                .load(R.drawable.dr_strong_alarm)
                                .into(imageButton);
                        break;
                    default:
                        break;
                }
            }

        } else//无报警
        {
            switch (index) {
                case 0:
                    Glide
                            .with(mContext)
                            .load(R.drawable.dr_stand_on)
                            .into(imageButton);
                    break;
                case 1:
                    Glide
                            .with(mContext)
                            .load(R.drawable.dr_walk)
                            .into(imageButton);
                    break;
                case 2:
                    Glide
                            .with(mContext)
                            .load(R.drawable.dr_zuotang)
                            .into(imageButton);
                    break;
                case 3:
                    Glide
                            .with(mContext)
                            .load(R.drawable.dr_youtang)
                            .into(imageButton);
                    break;
                case 4:
                    Glide
                            .with(mContext)
                            .load(R.drawable.dr_fuwo)
                            .into(imageButton);
                    break;
                case 5:
                    Glide
                            .with(mContext)
                            .load(R.drawable.dr_yangwo)
                            .into(imageButton);
                    break;
                case 6:
                    Glide
                            .with(mContext)
                            .load(R.drawable.dr_run)
                            .into(imageButton);
                    break;
                default:
                    break;
            }
        }

    }

    private ScaleAnimation initAnim() {
        /** 设置缩放动画 */
        ScaleAnimation animation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);//设置动画持续时间
        /** 常用方法 */
        animation.setRepeatCount(Animation.INFINITE);//设置重复次数
        animation.setFillAfter(false);//动画执行完后是否停留在执行完的状态
        //animation.setStartOffset(long startOffset);//执行前的等待时间

        return animation;
    }

}

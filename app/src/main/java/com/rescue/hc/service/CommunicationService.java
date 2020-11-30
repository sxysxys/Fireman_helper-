package com.rescue.hc.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.rescue.hc.bean.ReccoData;
import com.rescue.hc.bean.command.FiremanReccoVo;
import com.rescue.hc.bean.command.ReccoCmdInfo;
import com.rescue.hc.bean.command.ReccoCmdStatusInfo;
import com.rescue.hc.bean.util.ToastInfo;
import com.rescue.hc.enums.ReccoCmdEnum;
import com.rescue.hc.event.EventMessage;
import com.rescue.hc.lib.component.EventTag;
import com.rescue.hc.lib.util.CrcUtil;
import com.rescue.hc.lib.util.StringUtil;
import com.rescue.hc.ui.fragment.HomeFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import me.zhouzhuo810.okusb.USB;
import timber.log.Timber;

import static com.blankj.utilcode.util.ConvertUtils.bytes2HexString;
import static com.rescue.hc.lib.component.EventTag.GET_DATA_TEST;
import static com.rescue.hc.lib.component.EventTag.GET_SP_DATA;
import static com.rescue.hc.lib.component.EventTag.INIT_USB_PORT;
import static com.rescue.hc.lib.component.EventTag.UPDATE_HOME_SINGLE_DATA;
import static com.rescue.hc.lib.component.UartTag.baudRate;
import static com.rescue.hc.lib.component.UartTag.capatity;
import static com.rescue.hc.lib.component.UartTag.dataBits;
import static com.rescue.hc.lib.component.UartTag.dataFrameHeader;
import static com.rescue.hc.lib.component.UartTag.dataFrameTail;
import static com.rescue.hc.lib.component.UartTag.maxReadBytes;
import static com.rescue.hc.lib.component.UartTag.readDuration;
import static com.rescue.hc.lib.component.UartTag.writeFrameAddr;
import static com.rescue.hc.lib.component.UartTag.writeFrameHeader;
import static com.rescue.hc.lib.component.UartTag.writeTimeoutMills;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/10/31
 * @descibe none
 * </pre>
 */
public class CommunicationService extends Service {
    public static final int maxCounter = 30;
    private static final String TAG = "CommunicationService";

    private Activity mContext;
    private USB usb;

    private UsbManager mUsbManager;
    public static UsbSerialPort sPort = null;

    //private List<WatchCmdInfo> watchCmdInfoList;
    private List<ReccoCmdInfo> mReccoCmdInfoList;


//    private ReccoData reccoData = new ReccoData();
//    private EventMessage eventMessageSp;
    private List<String> bindList;

    public static int sendCount = 0;
    private byte reccoAlarm1 = 0;//呼救器报警标志1-8
    private byte reccoAlarm2 = 0;//呼救器报警标志9-16
    private byte reccoAlarm3 = 0;//呼救器报警标志17-24
    private byte reccoAlarm4 = 0;//呼救器报警标志25-32

    private ScheduledExecutorService executorService;
    private volatile boolean sysRunToHome = false;

    private List<String> lostIdList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        registerEventBus();
    }

    private List<UsbSerialPort> mEntries = new ArrayList<UsbSerialPort>();

    private void initData() {
        //mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //initUsb();
        //initSerialPort();
        //sysRunToSp = true;
        sysRunToHome = true;
        bindList = new ArrayList<>();
        //unbindList = new ArrayList<>();
        mReccoCmdInfoList = new ArrayList<>();

//        eventMessageSp = new EventMessage(reccoData, GET_SP_DATA);

        openTimer();
    }

    /**
     * 开启定时器
     */
    private void openTimer() {
        ThreadFactory namedThreadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "thread_pool_updateHome");
            }
        };
        /**
         * 开定时器 2s发一次数据到服务器
         */
        executorService = new ScheduledThreadPoolExecutor(1, namedThreadFactory);
        executorService.scheduleAtFixedRate(() -> {
            if (sysRunToHome) {
                lostIdList.clear();
                // 计算此时超时的个数
                List<FiremanReccoVo> mFiremanReccoVoList = HomeFragment.mFiremanReccoVoList;
                for (int i = 0; i < mFiremanReccoVoList.size(); i++) {
                    mFiremanReccoVoList.get(i).setQueryCounter(mFiremanReccoVoList.get(i).getQueryCounter() + 2);
                    if (mFiremanReccoVoList.get(i).getQueryCounter() > maxCounter) {
                        lostIdList.add(mFiremanReccoVoList.get(i).getFireman().getSerialNumber());
                    }
                }
                if (lostIdList.size() != 0) {
					EventMessage<List<String>> listEventMessage = new EventMessage<>();
					listEventMessage.setData(lostIdList);
					listEventMessage.setCode(UPDATE_HOME_SINGLE_DATA);
                    EventBus.getDefault().post(listEventMessage);
				}
            }
        }, 1000, 2000, TimeUnit.MILLISECONDS);
    }

    private void initUsb() {
        usb = new USB.USBBuilder(mContext)
                .setBaudRate(baudRate)
                .setDataBits(dataBits)
                .setStopBits(UsbSerialPort.STOPBITS_1)
                .setParity(UsbSerialPort.PARITY_NONE)
                .setMaxReadBytes(maxReadBytes)
                .setReadDuration(readDuration)
                .setDTR(true)
                .setRTS(true)
                .build();
        usb.setOnUsbChangeListener(new USB.OnUsbChangeListener() {
            @Override
            public void onUsbConnect() {
                sendMessage(new ToastInfo(1, "connect success", false));
            }

            @Override
            public void onUsbDisconnect() {
                sendMessage(new ToastInfo(1, "disconnected", false));
            }

            @Override
            public void onDataReceive(byte[] data) {
                //analyzeCmdFromRecco(data);
                EventMessage message = new EventMessage();
                message.setCode(GET_DATA_TEST);
                message.setData(data);
                EventBus.getDefault().post(message);
            }

            @Override
            public void onUsbConnectFailed() {
                sendMessage(new ToastInfo(2, "connect fail", false));
            }

            @Override
            public void onPermissionGranted() {
                sendMessage(new ToastInfo(1, "permission ok", false));
            }

            @Override
            public void onPermissionRefused() {
                sendMessage(new ToastInfo(2, "permission fail", false));
            }

            @Override
            public void onDriverNotSupport() {
                sendMessage(new ToastInfo(3, "no driver", false));
            }

            @Override
            public void onWriteDataFailed(String error) {
                //sendMessage(new ToastInfo(2, "write fail", false));
            }

            @Override
            public void onWriteSuccess(int num) {
//				sendMessage("write ok ");
            }
        });
        usb.requestPermission();
    }


    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    //sendMessage(new ToastInfo(3, e.getMessage(), false));
                    Log.v("CommunicationService", e.getMessage());
                }

                @Override
                public void onNewData(final byte[] data) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateReceivedData(data);
                        }
                    });
//					updateReceivedData(data);
                }
            };


    private void initSerialPort() {
        if (sPort == null) {
            sendMessage(new ToastInfo(3, "No serial device", false));
        } else {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            if (connection == null) {
                sendMessage(new ToastInfo(3, "Opening device failed", false));
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);


            } catch (IOException e) {
                sendMessage(new ToastInfo(3, "Error opening device: " + e.getMessage(), false));
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
            sendMessage(new ToastInfo(1, "Serial device: " + sPort.getClass().getSimpleName(), false));

        }

        onDeviceStateChange();
    }


    ArrayList<Byte> mRecvBuf = new ArrayList<>();


    private void updateReceivedData(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }

        for (byte by : data) {
            mRecvBuf.add((byte) (by & (byte) 0xFF));
        }

        while (mRecvBuf.size() > 8) {
            if (mRecvBuf.get(0) == (byte) 0xFE && mRecvBuf.get(3) == 0x40) {

                int len = ((mRecvBuf.get(5) & 0xFF) * 256) + (mRecvBuf.get(4) & 0xFF);
                if (mRecvBuf.size() < len + 8) //数据区尚未接收完整
                {
                    break;
                }

                byte[] ReceiveBytes = new byte[len + 8];


                Byte[] aaa = mRecvBuf.toArray(new Byte[0]);
                //得到完整的数据，复制到ReceiveBytes中进行校验
                System.arraycopy(CrcUtil.toPrimitives(aaa), 0, ReceiveBytes, 0, len + 8);

                byte crc = CrcUtil.ChechSumNew(ReceiveBytes, ReceiveBytes.length - 2);
                if (crc != ReceiveBytes[ReceiveBytes.length - 2]) //校验失败，最后一个字节是校验位
                {
                    //mRecvBuf.removeAll(Collections.singleton(ReceiveBytes));
                    mRecvBuf.clear();
                    continue;
                }

                //mRecvBuf.removeAll(Collections.singleton(ReceiveBytes));

                mRecvBuf.clear();
                /////执行其他代码，对数据进行处理。
                analyzeCmdFromRecco(ReceiveBytes);

                final String message = bytes2HexString(ReceiveBytes) + "\r\n";
                EventMessage eventMessage = new EventMessage(message, GET_DATA_TEST);
                EventBus.getDefault().post(eventMessage);

                //System.out.println(message);

            } else {
                mRecvBuf.remove(0);
            }
        }
    }


    private void stopIoManager() {
        if (mSerialIoManager != null) {
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }


    private void sendMessage(ToastInfo msg) {
        EventBus.getDefault().post(new EventMessage(msg, EventTag.TOAST_INFO));
    }

    public class CommunicationBinder extends Binder {
        public CommunicationService getService() {
            return CommunicationService.this;
        }
    }

    private CommunicationBinder communicationBinder = new CommunicationBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return communicationBinder;
    }

    public void setActivity(Activity activity) {
        this.mContext = activity;
        initData();
    }

    /**
     * 发送普通命令到呼救器
     *
     * @param devEui
     * @param order
     */
    private void sendCmdToRecco(String devEui, int order) throws Exception {
        Timber.d("发送dev_eui:" + devEui + "," + "cmd:" + order);
        byte[] cmd = new byte[24];
        byte sum = 0;

        cmd[0] = dataFrameHeader;
        if (sendCount > 65535) {
            sendCount = 0;
        }
        cmd[1] = (byte) sendCount;
        cmd[2] = (byte) (sendCount >> 8);

        cmd[3] = writeFrameAddr;

        cmd[4] = 0x10;
        cmd[5] = 0x00;

        byte[] temp = StringUtil.toByteArray(devEui);
        System.arraycopy(temp, 0, cmd, 6, 8);


        cmd[14] = 0x07;

        cmd[15] = writeFrameHeader;
        cmd[16] = (byte) order;

        cmd[17] = 0x00;
        cmd[18] = 0x00;
        cmd[19] = 0x00;
        cmd[20] = 0x00;

        byte[] databuf = new byte[7];
        System.arraycopy(cmd, 15, databuf, 0, 7);
        cmd[21] = CheckSumCrc8(databuf, 6);

        for (int i = 1; i < cmd.length - 2; i++) {
            sum += cmd[i];
        }
        cmd[22] = sum;

        cmd[23] = dataFrameTail;

        writeDataNew(cmd);
        sendCount++;
//        System.out.println(String.format("发送命令：%s", bytes2HexString(cmd)));
        //EventMessage message=new EventMessage(String.format("发送命令：%s",bytes2HexString(cmd)),BUS_SEND_CMD_DATA);
        //EventBus.getDefault().post(message);
    }


    /**
     * 群发求救命令到呼救器
     *
     * @param devEui
     * @param reccoAlarm1
     * @param reccoAlarm2
     * @param reccoAlarm3
     * @param reccoAlarm4
     */
    private void sendCmdToRecco(String devEui, int order, final byte reccoAlarm1, final byte reccoAlarm2, final byte reccoAlarm3, final byte reccoAlarm4) throws Exception {
        byte[] cmd = new byte[24];
        byte sum = 0;

        cmd[0] = dataFrameHeader;
        if (sendCount > 65535) {
            sendCount = 0;
        }
        cmd[1] = (byte) sendCount;
        cmd[2] = (byte) (sendCount >> 8);

        cmd[3] = writeFrameAddr;

        cmd[4] = 0x10;
        cmd[5] = 0x00;

        byte[] temp = StringUtil.toByteArray(devEui);
        System.arraycopy(temp, 0, cmd, 6, 8);


        cmd[14] = 0x07;

        cmd[15] = writeFrameHeader;
        cmd[16] = (byte) order;

        cmd[17] = reccoAlarm1;
        cmd[18] = reccoAlarm2;
        cmd[19] = reccoAlarm3;
        cmd[20] = reccoAlarm4;

        byte[] databuf = new byte[7];
        System.arraycopy(cmd, 15, databuf, 0, 7);
        cmd[21] = CheckSumCrc8(databuf, 6);

        for (int i = 1; i < cmd.length - 2; i++) {
            sum += cmd[i];
        }
        cmd[22] = sum;

        cmd[23] = dataFrameTail;

        writeDataNew(cmd);
        //EventMessage message=new EventMessage(String.format("发送命令：%s",bytes2HexString(cmd)),BUS_SEND_CMD_DATA);
        //EventBus.getDefault().post(message);
    }

    private void writeData(byte[] data) {
        if (usb != null) {
            usb.writeData(data, writeTimeoutMills);
        }
    }

    private void writeDataNew(byte[] data) throws Exception {
        if (sPort != null) {
            sPort.write(data, writeTimeoutMills);
        }
    }

    /**
     * 处理校验后的数据结果
     *
     * @param data
     */
    private void analyzeCmdFromRecco(byte[] data) {

        String dev_eui = "";

        byte[] devEuiBuf = new byte[8];
        System.arraycopy(data, 6, devEuiBuf, 0, 8);
        dev_eui = StringUtil.bytesToHexString(devEuiBuf);
        int length = data[14];

        byte[] recvBuf = new byte[length];
        System.arraycopy(data, 15, recvBuf, 0, length);

        for (int i = 0; i < length; i++) {
            recvBuf[i] &= 0xFF;
        }
        byte crc8 = CheckSumCrc8(recvBuf, length - 1);
        if (crc8 != recvBuf[length - 1]) {
            return;
        }

        ReccoData reccoData = new ReccoData();
        reccoData.setDevEui(dev_eui);
        reccoData.setReccoId(dev_eui.substring(dev_eui.length() - 8));


        byte command = recvBuf[16];//是否收到命令


        if (HomeFragment.mFiremanReccoVoList != null && HomeFragment.mFiremanReccoVoList.size() > 0) {
            for (int i = 0, m = HomeFragment.mFiremanReccoVoList.size(); i < m; i++) {
                FiremanReccoVo firemanReccoVo = HomeFragment.mFiremanReccoVoList.get(i);
                if (firemanReccoVo != null && firemanReccoVo.getReccoData().getDevEui().equals(dev_eui)) {
                    int cmd = firemanReccoVo.getCmdStatusInfo().getCmd();
                    // 先将上次的拿到
                    reccoData.setCommand(firemanReccoVo.getReccoData().getCommand());
//					if(cmd!=0)
//                    {
//                        if(cmd==command)
//                        {
//                            firemanReccoVo.setAnimationState((byte) 0);
//                            firemanReccoVo.getCmdStatusInfo().setCmd((byte)0);
//                            HomeFragment.mFiremanReccoAdapter.notifyItemChanged(i);
//
//                        }
//                        else
//                        {
//                            try {
//                                sendCmdToRecco(dev_eui,cmd);
//                                System.out.println(String.format("单发：向%s发送命令%d",dev_eui,cmd));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//
//                            }
//                        }
//                    }
                    byte preCmd = (byte) ((firemanReccoVo.getReccoData().getCommand()) & 0xFF);
                    Timber.d("dev_eui:" + dev_eui + "," + "command:" + command + "," + "precommand:" + preCmd);
                    if (preCmd != command) {
                        // 此时确定是在平板上发了数据
                        if (cmd != 0) {
                            // 此时呼救器收到了数据返回不为0
                            if (command == 0) {
                                try {
                                    sendCmdToRecco(dev_eui, cmd);
//                                    System.out.println(String.format("单发：向%s发送命令%d", dev_eui, cmd));
                                } catch (Exception e) {
                                    e.printStackTrace();

                                }
                            } else {
                                firemanReccoVo.setAnimationState((byte) 0);
                                // 清空待发送
                                firemanReccoVo.getCmdStatusInfo().setCmd((byte) 0);
                                // 将上一次pre设置为此次的
                                reccoData.setCommand(command);
                            }

                        }

                    } else {
                        if (cmd != 0) {
                            try {
                                sendCmdToRecco(dev_eui, cmd);
                                System.out.println(String.format("单发：向%s发送命令%d", dev_eui, cmd));
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }

                    }
                }
            }
        }


        float temperature = (float) (recvBuf[1]);//温度
        reccoData.setTemperature(temperature);

        byte gesture = (byte) (recvBuf[2] & (0x0F));//姿态
        reccoData.setGesture(gesture);

        //byte highTemperatureAlarm= GetBit(recvBuf[2],7);//高温报警

        byte alarmState = (byte) (recvBuf[2] & (byte) (0xF0));//其他报警

        reccoData.setAlarmState(alarmState);

        reccoData.setElectric(recvBuf[11]);//电量

        // TODO: 2020/03/26 需要修改
        double longitude = recvBuf[7] * 1.0f + ((recvBuf[8] * 10000.0f) + (recvBuf[9] * 100.0f) + (recvBuf[10])) / 1000000.0f;
        reccoData.setLongitude(longitude);//经度
        double latitude = recvBuf[3] * 1.0f + ((recvBuf[4] * 10000.0f) + (recvBuf[5] * 100.0f) + (recvBuf[6])) / 1000000.0f;
        reccoData.setLatitude(latitude);//纬度


        byte alarm1 = recvBuf[12];
        byte alarm2 = recvBuf[13];
        byte alarm3 = recvBuf[14];
        byte alarm4 = recvBuf[15];

        int number = Integer.parseInt(reccoData.getReccoId(), 16);
        int index = (number % capatity);

        if (index < 0 || index > capatity) {
            return;
        }

        int strongAlarm = (alarmState & 0x70) >> 4;
        if ((strongAlarm == 2) || (strongAlarm == 3)) {

            byte m = 0;
            if (index >= 1 && index <= 8) {
                m = (byte) (index - 1);
                reccoAlarm1 |= (byte) (1 << m);
            } else if (index >= 9 && index <= 16) {
                m = (byte) (index - 9);
                reccoAlarm2 |= (byte) (1 << m);
            } else if (index >= 17 && index <= 24) {
                m = (byte) (index - 17);
                reccoAlarm3 |= (byte) (1 << m);
            } else if (index >= 25 && index <= 31) {
                m = (byte) (index - 25);
                reccoAlarm4 |= (byte) (1 << m);
            } else {
                reccoAlarm4 |= (byte) (1 << 7);
            }


        } else if (alarmState == 0) {
            byte m = 0;
            if (index >= 1 && index <= 8) {
                m = (byte) (index - 1);
                reccoAlarm1 &= (byte) (~(1 << m));
            } else if (index >= 9 && index <= 16) {
                m = (byte) (index - 9);
                reccoAlarm2 &= (byte) (~(1 << m));
            } else if (index >= 17 && index <= 24) {
                m = (byte) (index - 17);
                reccoAlarm3 &= (byte) (~(1 << m));
            } else if (index >= 25 && index <= 31) {
                m = (byte) (index - 25);
                reccoAlarm4 &= (byte) (~(1 << m));
            } else {
                reccoAlarm4 &= (byte) (~(1 << 7));
            }
        }


        if ((alarm1 != reccoAlarm1) || (alarm2 != reccoAlarm2) || (alarm3 != reccoAlarm3) || (alarm4 != reccoAlarm4)) {
            try {

                sendCmdToRecco(dev_eui, 0x00, reccoAlarm1, reccoAlarm2, reccoAlarm3, reccoAlarm4);
                System.out.println(String.format("群发：向%s求救命令%d", dev_eui, reccoAlarm1));
            } catch (Exception e) {
                e.printStackTrace();

            }
        }


        Timber.d("接受：" + "////" + "id:" + reccoData.getDevEui() + "/" + StringUtil.bytesToHexString(recvBuf));
        EventMessage<ReccoData> eventMessageSp = new EventMessage<>();
        eventMessageSp.setCode(GET_SP_DATA);
        eventMessageSp.setData(reccoData);
        EventBus.getDefault().post(eventMessageSp);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onReceiveEvent(EventMessage event) {
        switch (event.getCode()) {
            case INIT_USB_PORT:
                sPort = (UsbSerialPort) event.getData();
                initSerialPort();
                break;
            default:
                break;
        }
    }

    private byte GetBit(byte b, int index) {
        return (byte) (((b & (1 << index)) > 0) ? 1 : 0);
    }

    public static byte CheckSumCrc8(byte[] buffer, int leng) {
        byte crc = 0;
        byte[] ch = new byte[8];
        byte ch1, i, j, k = 0;
        crc = (byte) (0xff);
        for (i = 0; i < leng; i++) {
            ch1 = (byte) (buffer[i] & (0xFF));
            for (j = 0; j < 8; j++) {
                ch[j] = (byte) (ch1 & 0x01);
                ch1 >>= 1;
            }
            for (k = 0; k < 8; k++) {
                ch[7 - k] <<= 7;
                if (((crc ^ ch[7 - k]) & 0x80) == 0x80) {
                    crc = (byte) ((crc << 1) ^ 0x1d);
                } else {
                    crc <<= 1;
                }
            }

        }
        crc ^= 0xff;
        return crc;
    }

    /**
     * 关闭服务 释放资源
     */
    public void closeService() {
        //sysRunToSp = false;
        //sysRunToHome = false;
        //cancelTimer();
//		if (usb != null) {
//			usb.destroy();
//		}

		sysRunToHome = false;
		cancelTimer();
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }


    }

	private void cancelTimer() {
		if (executorService != null) {
			try {
				executorService.shutdown();
				Timber.d("shutdown");
				if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
					executorService.shutdownNow();
					Timber.d("shutdownNow");
				}
			} catch (InterruptedException e) {
				Timber.d(e.toString());
				executorService.shutdownNow();
			}
			executorService = null;
		}
	}


    /**
     * 新加入成员
     *
     * @param watchId
     */
    public void joinBattle(String watchId) {
        ReccoCmdInfo watchCmdInfo = new ReccoCmdInfo();
        watchCmdInfo.setDevEui(watchId);
        watchCmdInfo.setCmdStatusInfo(new ReccoCmdStatusInfo(ReccoCmdEnum.NONE_CMD.getCmd()));
        mReccoCmdInfoList.add(watchCmdInfo);
    }

    /**
     * 离开成员
     *
     * @param watchId
     */
    public void leaveBattle(String watchId) {
        int index = getListIndex(watchId);
        if (index == -1) {
            return;
        }
        mReccoCmdInfoList.remove(index);
    }


    /**
     * 根据watchId获取index
     *
     * @param devEui
     * @return
     */
    private int getListIndex(String devEui) {
        int index = -1;
        for (int i = 0; i < mReccoCmdInfoList.size(); i++) {
            if (mReccoCmdInfoList.get(i).getDevEui().equals(devEui)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * @param devEui
     * @param cmdStatusInfo 命令（清命令，呼叫，撤退）
     */
    public void sendSingleCmdFunction(String devEui, ReccoCmdStatusInfo cmdStatusInfo) {
        int index = getListIndex(devEui);
        if (index == -1) {
            return;
        }
        mReccoCmdInfoList.get(index).setCmdStatusInfo(cmdStatusInfo);
        try {
            sendCmdToRecco(devEui, cmdStatusInfo.getCmd());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * @param cmdStatusInfo
     */
    public void sendBulkCmdFunction(ReccoCmdStatusInfo cmdStatusInfo) {
        if (mReccoCmdInfoList != null && mReccoCmdInfoList.size() > 0) {
            for (int i = 0; i < mReccoCmdInfoList.size(); i++) {
                sendSingleCmdFunction(mReccoCmdInfoList.get(i).getDevEui(), cmdStatusInfo);
            }
        }
    }


    /**
     * 添加待绑定的成员id
     *
     * @param devEui
     */
    public void addBindId(String devEui) {
        if (bindList != null) {
            if (!bindList.contains(devEui)) {
                bindList.add(devEui);
            }

        }
    }


    /**
     * 获取id
     *
     * @param selectList
     * @return
     */
    public String getSelectIds(List<String> selectList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String info : selectList) {
            stringBuilder.append(info);
            stringBuilder.append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }


    private void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }


    }


}

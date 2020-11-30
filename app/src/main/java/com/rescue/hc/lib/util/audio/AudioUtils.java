package com.rescue.hc.lib.util.audio;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.rescue.hc.app.AppApplication;

import java.io.IOException;

/**
 * <pre>
 * @author Created by szc
 * @date on 2019-01-22
 * @descibe none
 * </pre>
 */
public class AudioUtils {
	private static AudioUtils audioUtils;
	private SpeechSynthesizer mSpeechSynthesizer;
	/**
	 * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
	 * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
	 */
	private String appId = "19092284";

	private String appKey = "XPmxwRqUVVlcMx1nWkKW3btr";

	private String secretKey = "XLadrfreIMLHOj2YemkgG80p3eSPRLzZ";

	// TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
	private TtsMode ttsMode = TtsMode.MIX;
	// 离线发音选择，VOICE_FEMALE即为离线女声发音。
	// assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
	// assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
	private String offlineVoice = OfflineResource.VOICE_MALE;

	private AudioUtils() {
	}

	public static AudioUtils getInstance() {
		if (audioUtils == null) {
			synchronized (AudioUtils.class) {
				if (audioUtils == null) {
					audioUtils = new AudioUtils();
				}
			}
		}
		return audioUtils;
	}

	/**
	 * 注意此处为了说明流程，故意在UI线程中调用。
	 * 实际集成中，该方法一定在新线程中调用，并且该线程不能结束。具体可以参考NonBlockSyntherizer的写法
	 */
	public void initTTs(Context context) {
		boolean isMix = ttsMode.equals(TtsMode.MIX);
		boolean isSuccess;
		mSpeechSynthesizer = SpeechSynthesizer.getInstance();
		mSpeechSynthesizer.setContext(context);
		int result = mSpeechSynthesizer.setAppId(appId);
		checkResult(result, "setAppId");
		result = mSpeechSynthesizer.setApiKey(appKey, secretKey);
		checkResult(result, "setApiKey");
		if (isMix) {
			// 检查离线授权文件是否下载成功，离线授权文件联网时SDK自动下载管理，有效期3年，3年后的最后一个月自动更新。
//			if (!LoginInfoModel.isAuthVoice()) {
//				isSuccess = checkAuth();
//				if (!isSuccess) {
//					mSpeechSynthesizer.release();
//					mSpeechSynthesizer = null;
//					ToastUtils.info("离线语音授权失败", false);
//					return;
//				} else {
//					LoginInfoModel.setAuthVoice(true);
//				}
//			}
			// 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
			OfflineResource offlineResource = createOfflineResource(context, offlineVoice);
			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename()); // 文本模型文件路径 (离线引擎使用)
			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, offlineResource.getModelFilename()); // 声学模型文件路径 (离线引擎使用)
		}

		// 以下setParam 参数选填。不填写则默认值生效
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "1"); // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9"); // 设置合成的音量，0-9 ，默认 5
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");// 设置合成的语速，0-9 ，默认 5
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");// 设置合成的语调，0-9 ，默认 5

		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
		// 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
		// MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
		// MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
		// MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
		// MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

		mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);
		result = mSpeechSynthesizer.initTts(ttsMode);
		checkResult(result, "initTts");
	}

	private OfflineResource createOfflineResource(Context context, String voiceType) {
		OfflineResource offlineResource = null;
		try {
			offlineResource = new OfflineResource(context, voiceType);
		} catch (IOException e) {
			// IO 错误自行处理
			e.printStackTrace();
		}
		return offlineResource;
	}

	/**
	 * 检查appId ak sk 是否填写正确，另外检查官网应用内设置的包名是否与运行时的包名一致。本demo的包名定义在build.gradle文件中
	 *
	 * @return
	 */
	public boolean checkAuth() {
		AuthInfo authInfo = mSpeechSynthesizer.auth(ttsMode);
		if (!authInfo.isSuccess()) {
			// 离线授权需要网站上的应用填写包名。本demo的包名是com.baidu.tts.sample，定义在build.gradle中
			String errorMsg = authInfo.getTtsError().getDetailMessage();
			Log.e("msg", "【error】鉴权失败 errorMsg=" + errorMsg);
			return false;
		} else {
			Log.e("msg", "验证通过，离线正式授权文件存在。");
			return true;
		}
	}

	private void checkResult(int result, String method) {
		if (result != 0) {
			Log.e("msg", "error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
		}
	}

	public int speak(String text) {
		if (mSpeechSynthesizer == null) {
			initTTs(AppApplication.getApp().getApplicationContext());
		}
		int result = mSpeechSynthesizer.speak(text);
		checkResult(result, "speak");
		return result;
	}

	public void stop() {
		int result = mSpeechSynthesizer.stop();
		checkResult(result, "stop");
	}

	/**
	 * 暂停播放。仅调用speak后生效
	 */
	public void pause() {
		int result = mSpeechSynthesizer.pause();
		checkResult(result, "pause");
	}

	/**
	 * 继续播放。仅调用speak后生效，调用pause生效
	 */
	public void resume() {
		int result = mSpeechSynthesizer.resume();
		checkResult(result, "resume");
	}

	public void destroy() {
		if (mSpeechSynthesizer != null) {
			mSpeechSynthesizer.release();
			mSpeechSynthesizer = null;
		}
	}
}

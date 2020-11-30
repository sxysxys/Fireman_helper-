package com.rescue.hc.nfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.text.TextUtils;

import com.rescue.hc.bean.command.Fireman;
import com.rescue.hc.lib.util.ToastUtils;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/11/04
 * @descibe none
 * </pre>
 */
public class NfcNdef {
	/**
	 * 读取NFC标签文本数据
	 */
	public static String readNfcTag(Intent intent) {
		String info = "";
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
					NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage msgs[] = null;
			int contentSize = 0;
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
					contentSize += msgs[i].toByteArray().length;
				}
			}
			try {
				if (msgs != null) {
					NdefRecord record = msgs[0].getRecords()[0];
					info = parseTextRecord(record);
				}
			} catch (Exception e) {
				ToastUtils.error("NFC读取失败", true);
			}
		}
		return info;
	}

	/**
	 * 解析NDEF文本数据，从第三个字节开始，后面的文本数据
	 *
	 * @param ndefRecord
	 * @return
	 */
	private static String parseTextRecord(NdefRecord ndefRecord) {
		/**
		 * 判断数据是否为NDEF格式
		 */
		//判断TNF
		if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
			return null;
		}
		//判断可变的长度的类型
		if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
			return null;
		}
		try {
			//获得字节数组，然后进行分析
			byte[] payload = ndefRecord.getPayload();
			//下面开始NDEF文本数据第一个字节，状态字节
			//判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
			//其他位都是0，所以进行"位与"运算后就会保留最高位
			String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
			//3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
			int languageCodeLength = payload[0] & 0x3f;
			//下面开始NDEF文本数据第二个字节，语言编码
			//获得语言编码
			String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
			//下面开始NDEF文本数据后面的字节，解析出文本
			String textRecord = new String(payload, languageCodeLength + 1,
					payload.length - languageCodeLength - 1, textEncoding);
			return textRecord;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public static boolean writeNfcTag(Intent intent, String info) {
		Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		NdefMessage ndefMessage = new NdefMessage(
				new NdefRecord[]{createTextRecord(info)});
		return writeTag(ndefMessage, detectedTag);
	}

	/**
	 * 创建NDEF文本数据
	 *
	 * @param text
	 * @return
	 */
	public static NdefRecord createTextRecord(String text) {
		byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
		Charset utfEncoding = Charset.forName("UTF-8");
		//将文本转换为UTF-8格式
		byte[] textBytes = text.getBytes(utfEncoding);
		//设置状态字节编码最高位数为0
		int utfBit = 0;
		//定义状态字节
		char status = (char) (utfBit + langBytes.length);
		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		//设置第一个状态字节，先将状态码转换成字节
		data[0] = (byte) status;
		//设置语言编码，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1到langBytes.length的位置
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		//设置文本字节，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1 + langBytes.length
		//到textBytes.length的位置
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
		//通过字节传入NdefRecord对象
		//NdefRecord.RTD_TEXT：传入类型 读写
		NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_TEXT, new byte[0], data);
		return ndefRecord;
	}

	/**
	 * 写数据
	 *
	 * @param ndefMessage 创建好的NDEF文本数据
	 * @param tag         标签
	 * @return
	 */
	public static boolean writeTag(NdefMessage ndefMessage, Tag tag) {
		try {
			Ndef ndef = Ndef.get(tag);
			ndef.connect();
			ndef.writeNdefMessage(ndefMessage);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 检测工作,判断设备的NFC支持情况
	 *
	 * @return
	 */
	public static Boolean ifNFCUse(NfcAdapter mNfcAdapter) {
		if (mNfcAdapter == null) {
			ToastUtils.warning("设备不支持NFC！", true);
			return false;
		}
		if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
			ToastUtils.warning("请在系统设置中先启用NFC功能！", true);
			return false;
		}
		return true;
	}

	public static Fireman getFiremanInfo(String str) {
		if (TextUtils.isEmpty(str)) {
			ToastUtils.info("未读到数据", true);
			return null;
		}
		String[] info = str.split(",");
		if (info.length != 10) {
			ToastUtils.error("读取错误", true);
			return null;
		}
		Fireman fireman = new Fireman();
		fireman.setSerialNumber(info[0]);
		fireman.setName(info[1]);
		fireman.setGender(info[2]);
		fireman.setBirth(info[3]);
		fireman.setHeight(info[4]);
		fireman.setWeight(info[5]);
		fireman.setBloodType(info[6]);
		fireman.setEnlistTime(info[7]);
		fireman.setGrade(info[8]);
		fireman.setPosition(info[9]);
		return fireman;
	}
}

package com.zhc.eid.nfc;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zhc.eid.util.ByteUtil;
import com.zhc.eid.util.StringUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class EIDCardReader implements NfcAdapter.ReaderCallback {

    private static String TAG = "EIDCardReader";

    private final int UPDATE_SUCCESS = 0x01;
    private final int UPDATE_DATARECEIVE = 0x02;
    private final int UPDATE_FAIURE = 0x03;

    public static int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V;
    private WeakReference<EIDCallback> mEIDCallback;

    public EIDCardReader(EIDCallback callback) {
        mEIDCallback = new WeakReference<EIDCallback>(callback);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.e("NFC", "NFC  EID  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        try {
            IsoDep isodep = IsoDep.get(tag);
            isodep.connect();

            // 选择ADF_EID
            byte[] cmd1 = isodep.transceive(ByteUtil.hexStringToBytes("00A404000E315041592E5359532E4444463031"));
            Log.e(TAG, "选择ADF_EID:" + ByteUtil.byteArr2HexStr(cmd1));

            // 选择容器索引文件
            byte[] cmd2 = isodep.transceive(ByteUtil.hexStringToBytes("00A4000002FFFD"));
            Log.e(TAG, "选择容器索引文件:" + ByteUtil.byteArr2HexStr(cmd2));

            // 读取容器索引文件
            byte[] cmd3 = isodep.transceive(ByteUtil.hexStringToBytes("00B6000002"));
            Log.e(TAG, "读取容器索引文件:" + ByteUtil.byteArr2HexStr(cmd3));

            // 选择文件索引文件
            byte[] cmd4 = isodep.transceive(ByteUtil.hexStringToBytes("00A4000002FFFE"));
            Log.e(TAG, "选择文件索引文件:" + ByteUtil.byteArr2HexStr(cmd4));

            // 读取文件索引文件
            byte[] cmd5 = isodep.transceive(ByteUtil.hexStringToBytes("00B6000018"));
            Log.e(TAG, "读取文件索引文件:" + ByteUtil.byteArr2HexStr(cmd5));

            // 选择容器信息文件
            byte[] cmd6 = isodep.transceive(ByteUtil.hexStringToBytes("00A4000002FFFF"));
            Log.e(TAG, "选择容器信息文件:" + ByteUtil.byteArr2HexStr(cmd6));

            // 读取容器信息文件
            byte[] cmd7 = isodep.transceive(ByteUtil.hexStringToBytes("00B60000FA"));
            Log.e(TAG, "读取容器信息文件:" + ByteUtil.byteArr2HexStr(cmd7));

            // 读取容器信息文件
            byte[] cmd8 = isodep.transceive(ByteUtil.hexStringToBytes("00B600FA6E"));
            Log.e(TAG, "读取容器信息文件:" + ByteUtil.byteArr2HexStr(cmd8));

            // 选择载体信息文件
            byte[] cmd9 = isodep.transceive(ByteUtil.hexStringToBytes("00A40000024001"));
            Log.e(TAG, "选择载体信息文件:" + ByteUtil.byteArr2HexStr(cmd9));

            // 读取载体信息文件
            byte[] cmd10 = isodep.transceive(ByteUtil.hexStringToBytes("00B6000040"));
            Log.e(TAG, "读取载体信息文件:" + ByteUtil.byteArr2HexStr(cmd10));

            // 选择载体能力文件
            byte[] cmd11 = isodep.transceive(ByteUtil.hexStringToBytes("00A40000024000"));
            Log.e(TAG, "选择载体能力文件:" + ByteUtil.byteArr2HexStr(cmd11));

            // 读取载体能力文件
            byte[] cmd12 = isodep.transceive(ByteUtil.hexStringToBytes("00B6000040"));
            Log.e(TAG, "读取载体能力文件:" + ByteUtil.byteArr2HexStr(cmd12));

            // 选择EID MICX RSA2048公钥文件
            byte[] cmd13 = isodep.transceive(ByteUtil.hexStringToBytes("00A400000221D3"));
            Log.e(TAG, "选择EID MICX RSA2048公钥文件:" + ByteUtil.byteArr2HexStr(cmd13));

            // 读取EID MICX RSA2048公钥文件
            byte[] cmd14 = isodep.transceive(ByteUtil.hexStringToBytes("00B60000FF"));
            Log.e(TAG, "读取EID MICX RSA2048公钥文件:" + ByteUtil.byteArr2HexStr(cmd14));

            // 读取EID MICX RSA2048公钥文件
            byte[] cmd15 = isodep.transceive(ByteUtil.hexStringToBytes("00B600FF0D"));
            Log.e(TAG, "读取EID MICX RSA2048公钥文件:" + ByteUtil.byteArr2HexStr(cmd15));

            // 选择EID MICX SM2公钥文件
            byte[] cmd16 = isodep.transceive(ByteUtil.hexStringToBytes("00A400000222F3"));
            Log.e(TAG, "选择EID MICX SM2公钥文件:" + ByteUtil.byteArr2HexStr(cmd16));

            // 读取EID MICX SM2公钥文件
            byte[] cmd17 = isodep.transceive(ByteUtil.hexStringToBytes("00B6000042"));
            Log.e(TAG, "读取EID MICX SM2公钥文件:" + ByteUtil.byteArr2HexStr(cmd17));

            // //////////////////////////////////////////////

            // 选择ADF_PPSE
            byte[] cmd201 = isodep.transceive(ByteUtil.hexStringToBytes("00A404000E315041592E5359532E4444463031"));
            Log.e(TAG, "选择ADF_PPSE:" + ByteUtil.byteArr2HexStr(cmd201));

            // 选择借记账户
            byte[] cmd202 = isodep.transceive(ByteUtil.hexStringToBytes("00A4040007A0000003330101"));
            Log.e(TAG, "选择借记账户:" + ByteUtil.byteArr2HexStr(cmd202));

            // 读卡片账号预读
            byte[] cmd203 = isodep.transceive(ByteUtil.hexStringToBytes("00B2011C00"));
            Log.e(TAG, "读卡片账号预读:" + ByteUtil.byteArr2HexStr(cmd203));

            // 读卡片账号
            byte[] cmd204 = isodep.transceive(ByteUtil.hexStringToBytes("00B2011C"));
            String accountNoStr = ByteUtil.byteArr2HexStr(cmd204);
            Log.e(TAG, "读卡片账号:" + accountNoStr);

            // 读取电子现金余额 805C000204
            byte[] cmd205 = isodep.transceive(ByteUtil.hexStringToBytes("80CA9F7900"));
            String balanceStr = ByteUtil.byteArr2HexStr(cmd205);
            Log.e(TAG, "读取电子现金余额:" + balanceStr);

            isodep.close();

            HashMap<String, String> map = new HashMap<String, String>();

            // EID
            map.put("RQSY", "3F00");
            map.put("WJSY", "010003000300000000000100010001000300000001000100");
            map.put("RQXX", "7b 34 41 37 41 32 36 42 31 2d 41 42 41 35 2d 34 38 65 66 2d 38 42 36 41 2d 32 34 41 34 42 41 34 32 45 37 38 37 7d 00 00 00 01 00 00 00 00 00 00 11 10 12 10 13 10 00 00 00 00 00 00 7b 39 42 42 41 33 36 41 34 2d 34 45 35 44 2d 34 34 65 33 2d 38 38 33 33 2d 43 43 34 44 42 46 45 41 30 38 38 36 7d 00 00 00 20 00 00 00 00 00 00 00 00 12 13 13 13 00 00 00 00 00 00 7b 43 42 42 35 30 33 43 33 2d 45 30 43 36 2d 34 61 65 39 2d 39 35 43 39 2d 32 45 37 44 33 39 42 45 42 43 46 34 7d 00 00 00 02 00 00 00 00 00 00 00 00 00 00 d3 21 00 00 00 00 00 00 7b 32 32 39 39 39 32 44 38 2d 34 30 37 42 2d 34 64 37 35 2d 41 45 44 37 2d 30 46 34 44 33 34 41 45 35 46 38 38 7d 00 00 00 01 00 00 00 00 00 00 00 00 f2 a0 f3 a0 00 00 00 00 00 00 7b 41 36 37 37 44 38 32 42 2d 33 35 46 35 2d 34 30 39 34 2d 42 43 34 31 2d 38 38 33 45 43 30 46 36 37 44 37 39 7d 00 00 00 04 00 00 00 00 00 00 11 12 12 12 13 12 00 00 00 00 00 00 7b 44 41 38 46 38 43 44 39 2d 30 32 43 36 2d 34 39 31 32 2d 39 41 34 39 2d 37 38 43 32 31 46 36 38 37 41 42 46 7d 00 00 00 04 00 00 00 00 00 00 00 00 00 00 f3 22 00 00 00 00 00 00".replace(" ", ""));
            map.put("ZTXX", "000000000000");
            map.put("ZTNL", "A0FF98010003000300000000000100010001000300000001000100");
            map.put("MICX", "805C000204010003000300");
            map.put("MICXSM2", "6F00A404000E315041592E5359532E4444463031");

            // 账号
            int off = accountNoStr.indexOf("5A");
            String lengthStr = accountNoStr.substring(off + 2, off + 4);
            int accountLen = Integer.parseInt(lengthStr, 16);
            String account = accountNoStr.substring(off + 4, off + 4 + accountLen * 2).replace("F", "");
            map.put("ACCOUNT", account);

            // 余额
            String balance = StringUtil.String2SymbolAmount(balanceStr.substring(6, 18));
            map.put("BALANCE", balance);

            if (!ByteUtil.byteArr2HexStr(cmd2).equalsIgnoreCase("6A86")) { // 不是EID卡
                Message msg = new Message();
                msg.what = UPDATE_FAIURE;
                msg.arg1 = 100;
                mHandler.sendMessage(msg);

            } else {
                Message msg1 = new Message();
                msg1.what = UPDATE_SUCCESS;
                mHandler.sendMessage(msg1);

                Message msg2 = new Message();
                msg2.obj = map;
                msg2.what = UPDATE_DATARECEIVE;
                mHandler.sendMessage(msg2);
            }

        } catch (Exception e) {
            e.printStackTrace();

            Message msg = new Message();
            msg.what = UPDATE_FAIURE;
            msg.arg1 = 0;
            mHandler.sendMessage(msg);
        }

    }

    public void enableReaderMode(Activity activity, EIDCardReader cardReader) {
        Log.i("NFC", "Enabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.enableReaderMode(activity, cardReader, EIDCardReader.READER_FLAGS, null);
        }
    }

    public void disableReaderMode(Activity activity) {
        Log.i("NFC", "Disabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.disableReaderMode(activity);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SUCCESS:
                    mEIDCallback.get().onSuccess();
                    break;

                case UPDATE_DATARECEIVE:
                    mEIDCallback.get().onDataReceived((HashMap<String, String>) msg.obj);
                    break;

                case UPDATE_FAIURE:
                    mEIDCallback.get().onFailure(msg.arg1);
                    break;

                default:
                    break;
            }
        }
    };


    public interface EIDCallback {
        public void onDataReceived(HashMap<String, String> map);

        public void onSuccess();

        public void onFailure(int code);

    }

}

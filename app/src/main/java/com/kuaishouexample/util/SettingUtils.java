package com.kuaishouexample.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.honeywell.barcode.ActiveCamera;
import com.honeywell.barcode.HSMDecoder;
import com.honeywell.barcode.Symbology;
import com.kuaishouexample.R;


public class SettingUtils {
    private HSMDecoder hsmDecoder;
    private SharedPreferencesUitl preferencesUitl;
    private Context context;

    public SettingUtils(Context context, HSMDecoder hsmDecoder, SharedPreferencesUitl preferencesUitl) {
        this.context = context;
        this.hsmDecoder = hsmDecoder;
        this.preferencesUitl = preferencesUitl;
    }

    private final String[] items = {"UPCA", "UPCA_2CHAR_ADDENDA", "UPCA_5CHAR_ADDENDA", "UPCE0", "UPCE1",
            "UPCE_EXPAND", "UPCE_2CHAR_ADDENDA", "UPCE_5CHAR_ADDENDA", "EAN8", "EAN8_2CHAR_ADDENDA",
            "EAN8_5CHAR_ADDENDA", "EAN13", "EAN13_2CHAR_ADDENDA", "EAN13_5CHAR_ADDENDA", "EAN13_ISBN",
            "CODE128", "GS1_128", "C128_ISBT", "CODE39", "COUPON_CODE", "TRIOPTIC", "I25", "S25", "IATA25",
            "M25", "CODE93", "CODE11", "CODABAR", "TELEPEN", "MSI", "RSS_14", "RSS_LIMITED", "RSS_EXPANDED",
            "CODABLOCK_F", "PDF417", "MICROPDF", "COMPOSITE", "COMPOSITE_WITH_UPC", "AZTEC", "MAXICODE",
            "DATAMATRIX", "DATAMATRIX_RECTANGLE", "QR", "HANXIN", "HK25", "KOREA_POST", "OCR", "前置/后置"};
    /**
     * 多选
     */
    private boolean[] isCheck = new boolean[items.length];

    public void dialogMoreChoice() {
        for (int i = 0; i < items.length; i++) {
            isCheck[i] = preferencesUitl.read("decodeType" + i, false);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("扫描设置");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMultiChoiceItems(items, isCheck, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which,
                                boolean isChecked) {

                if (which == 47) {
                    if (isChecked) {
                        preferencesUitl.write("decodeType" + which, isChecked);
                        hsmDecoder.setActiveCamera(ActiveCamera.FRONT_FACING);//前置 摄像头

                    } else {
                        preferencesUitl.write("decodeType" + which, isChecked);
                        hsmDecoder.setActiveCamera(ActiveCamera.REAR_FACING);//后置 摄像头
                    }
                } else {
                    if (!isChecked) {
                        hsmDecoder.disableSymbology(Symbology.SYMS[which]);
                        preferencesUitl.write("decodeType" + which, isChecked);
                    } else {
                        hsmDecoder.enableSymbology(Symbology.SYMS[which]);
                        preferencesUitl.write("decodeType" + which, isChecked);

                    }
                }
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(context, "确定", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        builder.create().show();
    }
}

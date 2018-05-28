package com.kuaishouexample;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.serialport.DeviceControl;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.barcode.ActiveCamera;
import com.honeywell.barcode.HSMDecodeComponent;
import com.honeywell.barcode.HSMDecodeResult;
import com.honeywell.barcode.HSMDecoder;
import com.honeywell.barcode.Symbology;
import com.kuaishouexample.base.BaseAct;
import com.kuaishouexample.db.KuaiShouDatas;
import com.kuaishouexample.util.DBUitl;
import com.kuaishouexample.util.SettingUtils;
import com.kuaishouexample.util.SharedPreferencesUitl;
import com.kuaishouexample.view.CustomToolBar;
import com.kuaishoulibrary.KuaishouInterface.VolumeInterface;
import com.kuaishoulibrary.KuaishouInterface.WeightInterface;
import com.kuaishoulibrary.VolumeManage;
import com.kuaishoulibrary.WeightManage;
import com.kuaishoulibrary.utils.PlaySound;
import com.sc100.HuoniManage;
import com.sc100.Huoniinterface.HuoniScan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseAct implements WeightInterface.DisplayWeightDatasListener, VolumeInterface.DisplayVolumeListener, View.OnClickListener, HuoniScan.DisplayBarcodeDataListener, CustomToolBar.BtnClickListener, HuoniScan.HuoniscanListener {

    private DecimalFormat df;
    private SurfaceView mSurfaceview;
    private HSMDecodeComponent mHsmDecodeComponent;
    /**
     * 保证条码在预览框内
     */
    private TextView mTvShowmsg;
    private ImageView mImage;
    /**
     * 条码
     */
    private TextView mSuccess;
    /**
     * 空
     */
    private TextView mCode;
    private LinearLayout mLayoutBarcode;
    /**
     * 0.00KG
     */
    private TextView mWeight;
    private LinearLayout mLayoutWeight;
    private TextView mVolume;
    private TextView mIsPass;
    private LinearLayout mFloatId;
    private int dbCount = 0;
    private boolean weightState = false;
    public SharedPreferencesUitl preferencesUitl;
    public DBUitl dbUitl;
    private String weightResult;
    private WeightInterface weightInterface;
    private VolumeInterface volumeInterface;
    private SettingUtils settingUtils;
    private HuoniScan huoniScan;
    private HSMDecoder hsmDecoder;
    private com.honeywell.camera.CameraManager cameraManager;
    private boolean[] bl = new boolean[48];
    private DeviceControl deviceControl;
    /**
     * 条码设置
     */
    private Button mBtnCodeSetting;
    public CustomToolBar customToolBar;
    private String barcode = "";

    private Queue<String> BarCodeQueue = new ArrayDeque<>();//条码队列
    private Button btnTakePicture;//测试专用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kuaishou_layout);
        btnTakePicture = findViewById(R.id.btn_takepicture);

        initView();
        initLibrary();
    }

    private void initView() {
        customToolBar = findViewById(R.id.title_bar_layout);
        customToolBar.setCameraState("相机：已连接");
        customToolBar.setTvExportVisable(false);
        customToolBar.setSetingBackground(R.drawable.check_db);
        customToolBar.setTitleBarListener(this);
        mSurfaceview = findViewById(R.id.surfaceview);
        mHsmDecodeComponent = findViewById(R.id.hsm_decodeComponent);
        mTvShowmsg = findViewById(R.id.tv_showmsg);
        mImage = findViewById(R.id.image);
        mSuccess = findViewById(R.id.success);
        mCode = findViewById(R.id.code);
        mLayoutBarcode = findViewById(R.id.layout_barcode);
        mWeight = findViewById(R.id.weight);
        mLayoutWeight = findViewById(R.id.layout_weight);
        mVolume = findViewById(R.id.volume);
        mIsPass = findViewById(R.id.is_pass);
        mFloatId = findViewById(R.id.float_id);
        mBtnCodeSetting = findViewById(R.id.btn_code_setting);
        mBtnCodeSetting.setOnClickListener(this);

        try {
            deviceControl = new DeviceControl(DeviceControl.PowerType.MAIN);
            deviceControl.MainPowerOn(93);//体积激光
            deviceControl.MainPowerOn(98);//扫码灯光
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLibrary() {
        PlaySound.initSoundPool(this);
        dbUitl = new DBUitl();//初始化数据库util
        preferencesUitl = SharedPreferencesUitl.getInstance(this, "decoeBar");
        df = new DecimalFormat("######0.00");
        //初始化霍尼扫描解码
        huoniScan = HuoniManage.getKuaishouIntance();
        huoniScan.intScanDecode(MainActivity.this);
        huoniScan.setdisplayBarcodeData(this);
        huoniScan.setHuoniScanLibraryState(this);
        hsmDecoder = huoniScan.getHuoniHsmDecoder();
        cameraManager = huoniScan.getHuoniCameraManager(MainActivity.this);
        settingUtils = new SettingUtils(MainActivity.this, huoniScan.getHuoniHsmDecoder(), preferencesUitl);
        //初始化称重
        weightInterface = WeightManage.getKuaishouIntance();
        weightInterface.setWeightStatas(this);

        //初始化提及测量
        volumeInterface = VolumeManage.getVolumeIntance();
        volumeInterface.initVolumeCamera(MainActivity.this, mSurfaceview);
        volumeInterface.setDisplayVolumeListener(this);
    }

    private long startTime = 0;

    @Override
    protected void onResume() {
        weightInterface.initWeight();
        customToolBar.setCameraState("相机：" + preferencesUitl.read("hsmDecoder", ""));
        barcode = "";
        mTvShowmsg.setText("保证条码在预览框内");
        //查询缓存的快件条码
        BarCodeQueue = preferencesUitl.readQueue("queue");
        Object[] oo = BarCodeQueue.toArray();
        for (int i = 0; i < oo.length; i++) {
            Log.i("BarCodeQueue", "onResume: " + oo[i]);
        }
        starterTimer();//检测所有状态
        for (int i = 0; i < 48; i++) {
            bl[i] = preferencesUitl.read("decodeType" + i, false);
        }
        for (int i = 0; i < bl.length; i++) {
            if (i == 47) {
                if (bl[47]) {
                    huoniScan.getHuoniHsmDecoder().setActiveCamera(ActiveCamera.FRONT_FACING);
                } else {
                    huoniScan.getHuoniHsmDecoder().setActiveCamera(ActiveCamera.REAR_FACING);
                }
            } else {
                if (bl[i]) {
                    huoniScan.getHuoniHsmDecoder().enableSymbology(Symbology.SYMS[i]);
                } else {
                    huoniScan.getHuoniHsmDecoder().disableSymbology(Symbology.SYMS[i]);
                }
            }
        }
        hsmDecoder.enableSound(false);
        cameraManager.reopenCamera();
        startTime = SystemClock.currentThreadTimeMillis();
        camera1 = cameraManager.getCamera();
        parameters1 = camera1.getParameters();
        parameters1.setExposureCompensation(-1);
        parameters1.setAutoWhiteBalanceLock(true);
        parameters1.setColorEffect(Camera.Parameters.EFFECT_MONO);
        parameters1.setPreviewSize(1920, 1080);
        camera1.setParameters(parameters1);
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = hsmDecoder.getLastImage();
                //输出流保存数据
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream("/mnt/sdcard/DCIM/camera/" + System.currentTimeMillis() + ".png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//                    camera.stopPreview();
//                    camera.startPreview();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
//                camera1.takePicture(null, null, new Camera.PictureCallback() {
//                    @Override
//                    public void onPictureTaken(byte[] bytes, Camera camera) {
//                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//
//                    }
//                });
            }
        });
        setCameraParams();
        startTimer();
        super.onResume();

    }

    @SuppressWarnings("unchecked")
    private void setCameraParams() {
        Camera.Parameters parameters = cameraManager.getCamera().getParameters();
        try {
            //获取支持的参数
            Method parametersSetEdgeMode = Camera.Parameters.class
                    .getMethod("setEdgeMode", String.class);
            Method parametersSetBrightnessMode = Camera.Parameters.class
                    .getMethod("setBrightnessMode", String.class);
            Method parametersSetContrastMode = Camera.Parameters.class
                    .getMethod("setContrastMode", String.class);

            //锐度 亮度 对比度
            parametersSetEdgeMode.invoke(parameters, "high");
            parametersSetBrightnessMode.invoke(parameters, "high");
            parametersSetContrastMode.invoke(parameters, "high");
            Method parametersGetEdgeMode = Camera.Parameters.class
                    .getMethod("getEdgeMode");
            Method parametersGetBrightnessMode = Camera.Parameters.class
                    .getMethod("getBrightnessMode");
            Method parametersGetContrastMode = Camera.Parameters.class
                    .getMethod("getContrastMode");

            //锐度亮度对比度 是否设置成功
            String ruidu = (String) parametersGetEdgeMode.invoke(parameters);
            String liangdu = (String) parametersGetBrightnessMode.invoke(parameters);
            String duibidu = (String) parametersGetContrastMode.invoke(parameters);

            Log.d("cameraSetting", "mlist is" + ruidu + "-----" + liangdu + "-----" + duibidu);


        } catch (Exception e) {
            Log.d("cameraSetting", "error is::" + Log.getStackTraceString(e));
        }

    }

    private Timer timer = null;

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                parameters1.setAutoExposureLock(true);
                camera1.setParameters(parameters1);
            }
        }, 4000);
    }

    private int a = 0;

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {//调整扫码焦距
//            if (a > 10) {
//                a = 0;
//            }
//            switch (a) {
//                case 0:
//                    parameters1.setZoom(0);
//                    break;
//                case 1:
//                    parameters1.setZoom(1);
//                    break;
//                case 2:
//                    parameters1.setZoom(2);
//                    break;
//                case 3:
//                    parameters1.setZoom(3);
//                    break;
//                case 4:
//                    parameters1.setZoom(4);
//                    break;
//                case 5:
//                    parameters1.setZoom(5);
//                    break;
//                case 6:
//                    parameters1.setZoom(6);
//                    break;
//                case 7:
//                    parameters1.setZoom(7);
//                    break;
//                case 8:
//                    parameters1.setZoom(8);
//                    break;
//                case 9:
//                    parameters1.setZoom(9);
//                    break;
//                case 10:
//                    parameters1.setZoom(10);
//                    break;
//            }
//            camera1.setParameters(parameters1);
//            a++;
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    int b = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_F2) {
            if (b == 0) {
                b = 1;
                //背景
                volumeInterface.volumePreviewPicture(true);
            } else {
                b = 0;
                //处理体积
                volumeInterface.volumePreviewPicture(false);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        huoniScan.release();
        volumeInterface.releaseVolumeCamera();
        weightInterface.releaseWeightDev();
        try {
            deviceControl.MainPowerOff(93);
            deviceControl.MainPowerOff(98);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    double[] V = (double[]) msg.obj;
                    if (V[3] == 1) {
                        mTvShowmsg.setText("您的摆放不正确");
                    } else if (V[3] == 2) {
                        mTvShowmsg.setText("线缺失");
                    } else if (V[3] == 3) {
                        mTvShowmsg.setText("请往中心位置摆放");
                    } else if (V[3] == 4) {
                        mTvShowmsg.setText("请往中心位置摆放");
                    } else if (V[3] == 0) {
                        mVolume.setText("长" + V[0] + "宽" + V[1] + "高" + V[2]);
                    }
                    break;
                case 2:
                    mImage.setImageBitmap((Bitmap) msg.obj);
                    break;
                case 10:
                    dbUitl.insertDtata(new KuaiShouDatas((String) msg.obj, weightResult + "kg", "10L", testTime(System.currentTimeMillis())));
                    mTvShowmsg.setTextColor(getResources().getColor(R.color.green));
                    mTvShowmsg.setText("PASS\n请扫面下一件物品");
                    dbCount++;
                    customToolBar.setCount("本次保存：" + dbCount);
                    break;
                case 11:
                    mLayoutWeight.setBackgroundColor(getResources().getColor(R.color.red));
                    mTvShowmsg.setTextColor(getResources().getColor(R.color.white));
                    mTvShowmsg.setText((String) msg.obj);
                    mVolume.setText("");
                    break;
                case 12:
                    mLayoutBarcode.setBackgroundColor(getResources().getColor(R.color.green));
                    mCode.setText((String) msg.obj);
                    break;
            }
        }
    };

    public String testTime(long l) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date curDate = new Date(l);//获取当前时间
        String Times = formatter.format(curDate);
        return Times;
    }

    /**
     * 计时器监听全局重量与条码状态
     */
    private void starterTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (barcode.equals("")) {
                    return;
                }
                if (!BarCodeQueue.contains(barcode) && weightState) {//查解码队列里是否存在
                    BarCodeQueue.offer(barcode);
                    if (BarCodeQueue.size() > 4) {
                        Object[] s = BarCodeQueue.toArray();
                        for (int i = 0; i < s.length; i++) {
                            Log.i("BarCodeQueue", "timer: " + s[i]);
                        }
                        BarCodeQueue.poll();
                    }
                    preferencesUitl.writeQueue("queue", BarCodeQueue);
                    PlaySound.play(PlaySound.PASS_SCAN, PlaySound.NO_CYCLE);
                    handler.sendMessage(handler.obtainMessage(10, barcode));
                } else {

                }
            }
        }, 0, 60);
    }

    @Override
    public void WeightStatas(final int i, final double v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (i) {
                    case 0:
                        mWeight.setText("0.00KG");
                        customToolBar.setWeightState("电子秤：断开");
                        handler.sendMessage(handler.obtainMessage(11, "保证条码在预览框内"));
                        Toast.makeText(MainActivity.this, "请检查电子秤链接", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        customToolBar.setWeightState("电子秤：已连接");
                        mLayoutWeight.setBackgroundColor(getResources().getColor(R.color.green));
                        weightResult = df.format(v);
                        mWeight.setText(df.format(v));
                        weightState = true;
                        break;
                    case 2:
                        customToolBar.setWeightState("电子秤：已连接");
                        mWeight.setText(df.format(v));
                        if (v == 0) {
                            weightState = false;
                            handler.sendMessage(handler.obtainMessage(11, "保证条码在预览框内"));
                        }
                        break;
                    case 3:
                        handler.sendMessage(handler.obtainMessage(11, "保证条码在预览框内"));
                        mWeight.setText("0.00KG");
                        customToolBar.setWeightState("电子秤：断开");
                        break;
                }
            }
        });

    }

    @Override
    public void displayBarcodeData(String s, long l, HSMDecodeResult[] hsmDecodeResults) {
//************************
//        hsmDecoder.enableSound(true);
//        StringBuilder result = new StringBuilder();
//        String[] codeBytes = new String[hsmDecodeResults.length];
//        for (int i = 0; i < hsmDecodeResults.length; i++) {
//            codeBytes[i] = hsmDecodeResults[i].getBarcodeData();
//            String bar = hsmDecodeResults[i].getBarcodeData();
//            result.append("码" + i + ":" + bar + "\n");
//        }
//        mSuccess.setText("条码" + codeBytes.length + "个");
//        mCode.setTextSize(20);
//        handler.sendMessage(handler.obtainMessage(12, result.toString()));
        //***********************
        String decoderBar = s;
        hsmDecoder.enableSound(true);
        handler.sendMessage(handler.obtainMessage(12, decoderBar));
        if (BarCodeQueue.contains(decoderBar)) {
            if (!decoderBar.equals(barcode)) {
                PlaySound.play(PlaySound.REPETITION, PlaySound.NO_CYCLE);
                handler.sendMessage(handler.obtainMessage(11, "重复扫描\n请扫面下一件物品"));
                Log.i("db", "播放声音");
            }
        }
//        saveImage(hsmDecoder.getLastBarcodeImage(hsmDecodeResults[0].getBarcodeBounds()));
        barcode = decoderBar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_code_setting:
                //跳转到数据库查看界面
                settingUtils.dialogMoreChoice();
                break;
        }
    }

    @Override
    public void getVolumeDatas(double[] bytes, Bitmap bitmap) {
        // TODO: 2018/4/10    体积计算结果
        handler.sendMessage(handler.obtainMessage(2, bitmap));
        handler.sendMessage(handler.obtainMessage(1, bytes));
    }

    @Override
    public void exportClick() {

    }

    @Override
    public void settingClick() {
        intentAct(DbShowAct.class);
    }

    @Override
    public void huoniLibraryState(String s) {
        preferencesUitl.write("hsmDecoder", s);
        customToolBar.setCameraState("相机：" + s);
    }

    /**
     * 保存扫描后的条码图片
     *
     * @param bmp
     */
    public void saveImage(final Bitmap bmp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                String fileName = System.currentTimeMillis() + ".jpg";
                File file = new File(appDir, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 其次把文件插入到系统图库
                try {
                    MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(),
                            file.getAbsolutePath(), fileName, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // 最后通知图库更新
//                MainActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
            }
        }).start();

    }
}

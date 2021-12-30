package com.BUAARSE.faultdetector;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MyRecorder extends AppCompatActivity implements Runnable{
    private AudioRecord mAudioRecord;
    //音频源
    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    //采样率
    private int mSampleRate;
    //编码大小
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //选择声道
    private int mAudioChannel = AudioFormat.CHANNEL_IN_MONO;
    //缓冲区
    private int mBufferSize;
    //文件储存地址
    private String filePath;
    private File myRecordFile;
    //是否在录音
    private boolean isRecording = false;
    private Thread mThread;
    private DataOutputStream mDataOutputStream;
    private float dataMax = 0;


    //构造函数
    public MyRecorder(int sampleRate, String filePath){
        this.mSampleRate = sampleRate;
        //获取最小缓冲区
        this.mBufferSize = AudioRecord.getMinBufferSize(mSampleRate
                ,mAudioChannel,mAudioFormat);
        //创建AudioRecord
        this.mAudioRecord = new AudioRecord(mAudioSource,
                mSampleRate,mAudioChannel,mAudioFormat,mBufferSize);
        //新建文件
        this.filePath = filePath;
        myRecordFile = new File(filePath+"/Record.pcm");
        if(!myRecordFile.exists()){
            try {
                myRecordFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("文件错误：", "无法创建文件");
            }
        }
    }

    //开始录音函数
    public void startMyRecord(){
        if(AudioRecord.ERROR_BAD_VALUE == mBufferSize||AudioRecord.ERROR == mBufferSize){
            Log.i("录音：","硬件不支持");
        }else{
            destroyThread();
            isRecording = true;
            if(mThread == null){
                mThread = new Thread(this);
                mThread.start();//开启线程
            }
        }
    }

    //停止录音
    public void stopRecord() {
        isRecording = false;
        //停止录音，回收AudioRecord对象，释放内存
        if (mAudioRecord != null) {
            if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {//初始化成功
                mAudioRecord.stop();
            }
            if (mAudioRecord  !=null ) {
                mAudioRecord.release();
            }
        }
        if(myRecordFile.exists()){
//            System.out.println("文件保存，地址是："+myRecordFile.getAbsolutePath());
        }
    }

    //销毁线程
    private void destroyThread() {
        try {
            isRecording = false;
            if (null != mThread && Thread.State.RUNNABLE == mThread.getState()) {
                try {
                    Thread.sleep(20);
                    mThread.interrupt();
                } catch (Exception e) {
                    mThread = null;
                }
            }
            mThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mThread = null;
        }
    }


    @Override
    public void run() {
        //标记为开始采集状态
        isRecording = true;
        try {
            //获取到文件的数据流
            mDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(myRecordFile)));
            byte[] buffer = new byte[mBufferSize];
            mAudioRecord.startRecording();//开始录音
            //getRecordingState获取当前AudioReroding是否正在采集数据的状态
//            int j=0;
            while (isRecording && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                int bufferReadResult = mAudioRecord.read(buffer,0,mBufferSize);
                long data = 0;

//                j++;
                for (int i = 0; i < bufferReadResult; i++)
                {

                    mDataOutputStream.write(buffer[i]);
                    if(i%2==0){
                        data+=Math.abs(buffer[i]);
                    }
                    else{
                        data+=Math.abs(buffer[i]<<8);
                    }
                }

                this.dataMax=(float)2*data/bufferReadResult;
            }
//            System.out.println("一共有"+j);
            mDataOutputStream.close();
        } catch (Throwable t) {
            Log.e("录音：", "Recording Failed");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyThread();
        stopRecord();
    }

    //开始画波形图
    private Timer timer;
    private TimerTask timerTask;

    public void showWaveData(final ShowWaveView swv) {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(isRecording){
                    double db = 0;
                    if(dataMax!=0){
                        db = 20*Math.log10(dataMax);
                    }
                    swv.showLine((float)db-30);
                }
            }
        };
        timer.schedule(timerTask,0, 20);
    }
    public void stopWave(final ShowWaveView swv) {
        swv.cleanPaint();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}


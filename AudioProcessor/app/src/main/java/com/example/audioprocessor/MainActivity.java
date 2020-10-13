package com.example.audioprocessor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ViewUtils;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // Audio Record
    Button StartRecord, StopRecord, GenerateBtn;
    File file;
    int SamplingRate = 48000; //48K采样率
    int channelConfiguration = AudioFormat.CHANNEL_IN_STEREO; //格式：双声道
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT; //16Bit
    boolean isRecording = false; //是否在录制
    int bufferSize = 0; //每次从audiorecord输入流中获取到的buffer的大小

    // Audio Play
    private SeekBar seekBar;
    Button btn_play;
    private MediaPlayer mediaPlayer;

    Handler handler;

    static final int REQUEST_AUDIO_GET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Audio Record
        this.GetPermission();
        StartRecord = (Button)findViewById(R.id.start_record);
        StopRecord = (Button)findViewById(R.id.stop_record);
        GenerateBtn = (Button)findViewById(R.id.generate);
        handler = new Handler(Looper.getMainLooper());

        //在录音键被按下之前，不允许按停止键
        StopRecord.setEnabled(false);

        StartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SamplingRate = 44000;
                EditText sam = (EditText)findViewById(R.id.edit_rate);

                try {
                    SamplingRate = Integer.parseInt(sam.getText().toString());
                }
                catch (Exception e) {
                    handler.post(
                            new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Toast.makeText(getApplicationContext(), "Please provide sampling rate!", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    return;
                }

                //恢复停止录音按钮，并禁用开始录音按钮
                StopRecord.setEnabled(true);
                StartRecord.setEnabled(false);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //设置用于临时保存录音原始数据的文件的名字
                        String name = Environment.getExternalStorageDirectory().getAbsolutePath()+"/raw.wav";
                        //调用开始录音函数，并把原始数据保存在指定的文件中
                        StartRecord(name);
                        //获取此刻的时间
                        Date now = Calendar.getInstance().getTime();
                        //用此刻时间为最终的录音wav文件命名
                        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + now.toString() + ".wav";
                        System.out.println("Save: " + filepath);
                        //把录到的原始数据写入到wav文件中。
                        copyWaveFile(name, filepath);
                    }
                });
                //开启线程
                thread.start();
            }
        });

        StopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //停止录音
                isRecording = false;
                //恢复开始录音按钮，并禁用停止录音按钮
                StopRecord.setEnabled(false);
                StartRecord.setEnabled(true);
            }
        });


        GenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String name = Environment.getExternalStorageDirectory().getAbsolutePath()+"/raw.wav";
//
                        int frequency = 440;
                        double phase = 0;
                        int duration = 10;
                        int samplingRate = 44000;

                        EditText fre = (EditText)findViewById(R.id.edit_frequency);
                        EditText pha = (EditText)findViewById(R.id.edit_phase);
                        EditText dur = (EditText)findViewById(R.id.edit_duration);
                        EditText sam = (EditText)findViewById(R.id.edit_samplingRate);

                        try {
                            frequency = Integer.parseInt(fre.getText().toString());
                            phase = Float.parseFloat(pha.getText().toString());
                            duration = Integer.parseInt(dur.getText().toString());
                            samplingRate = Integer.parseInt(sam.getText().toString());
                        }
                        catch (Exception e) {
                            handler.post(
                                    new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Toast.makeText(getApplicationContext(), "Please fill all blank!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                            return;
                        }

                        int channels = 1; //单声道
                        int bitWidth = 16; //位宽
                        int bufferSize = 16; //缓冲大小

                        handler.post(
                                new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        Toast.makeText(getApplicationContext(), "Generating...Wait please!", Toast.LENGTH_LONG).show();
                                    }
                                }
                        );

                        generateWave(name, frequency, phase, duration, samplingRate);   //生成一段正弦波
                        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/generate.wav";
                        generateWaveFile(name, filepath, samplingRate, channels, bitWidth, bufferSize); //加入文件头
                        handler.post(
                                new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        Toast.makeText(getApplicationContext(), "Generate successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                });
                //开启线程
                thread.start();
            }
        });

        // Audio Play
        btn_play = (Button) findViewById(R.id.start_play);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPlayOrPause(view);
            }
        });

        seekBar= (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //获取拖动结束之后的位置
                int progress=seekBar.getProgress();
                //跳转到某个位置播放
                mediaPlayer.seekTo(progress);

            }
        });

    }

    private void GetPermission() {

        /*在此处插入运行时权限获取的代码*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!=
                PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED
        )
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    public void StartRecord(String name) {

        //生成原始数据文件
        file = new File(name);
        //如果文件已经存在，就先删除再创建
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("未能创建" + file.toString());
        }
        try {
            //文件输出流
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);
            //获取在当前采样和信道参数下，每次读取到的数据buffer的大小
            bufferSize = AudioRecord.getMinBufferSize(SamplingRate, channelConfiguration, audioEncoding);
            //建立audioRecord实例
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SamplingRate, channelConfiguration, audioEncoding, bufferSize);

            //设置用来承接从audiorecord实例中获取的原始数据的数组
            byte[] buffer = new byte[bufferSize];
            //启动audioRecord
            audioRecord.startRecording();
            //设置正在录音的参数isRecording为true
            isRecording = true;
            //只要isRecording为true就一直从audioRecord读出数据，并写入文件输出流。
            //当停止按钮被按下，isRecording会变为false，循环停止
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                for (int i = 0; i < bufferReadResult; i++) {
                    dos.write(buffer[i]);
                }
            }
            //停止audioRecord，关闭输出流
            audioRecord.stop();
            dos.close();
        } catch (Throwable t) {
            Log.e("MainActivity", "录音失败");
        }
    }


    private void copyWaveFile(String inFileName, String outFileName)
    {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        //wav文件比原始数据文件多出了44个字节，除去表头和文件大小的8个字节剩余文件长度比原始数据多36个字节
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = SamplingRate;
        int channels = 2;
        //每分钟录到的数据的字节数
        long byteRate = 16 * SamplingRate * channels / 8;

        byte[] data = new byte[bufferSize];
        try
        {
            in = new FileInputStream(inFileName);
            out = new FileOutputStream(outFileName);
            //获取真实的原始数据长度
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            //为wav文件写文件头
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            //把原始数据写入到wav文件中。
            while(in.read(data) != -1)
            {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // WAV type format = 1
        header[21] = 0;
        header[22] = (byte) channels; //指示是单声道还是双声道
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff); //采样频率
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff); //每分钟录到的字节数
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff); //真实数据的长度
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        //把header写入wav文件
        out.write(header, 0, 44);
    }

    public void isPlayOrPause(View view){
        if(mediaPlayer==null){
            Context context = getApplicationContext();
            CharSequence text = "Please choose audio file!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }else if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            btn_play.setText(R.string.text_play);
        }else{
            mediaPlayer.start();
            btn_play.setText(R.string.text_pause);
        }

    }

    class MyThread extends  Thread{

        @Override
        public void run() {
            super.run();
            while (seekBar.getProgress()<=seekBar.getMax()){
                //获取当前音乐播放的位置
                int currentPosition=mediaPlayer.getCurrentPosition();
                //让进度条动起来
                seekBar.setProgress(currentPosition);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUDIO_GET && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            try {
                assert uri != null;
                mediaPlayer=new MediaPlayer();
                mediaPlayer.setDataSource(this, uri);

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        btn_play.setText(R.string.text_pause);

                        int duration=mediaPlayer.getDuration();
                        seekBar.setMax(duration);

                        new MyThread().start();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void selectWav(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_AUDIO_GET);
        }
    }

    public void generateWave(String name, int frequency, double phase, int duration, int samplingRate) {

        //生成原始数据文件
        File file = new File(name);
        //如果文件已经存在，就先删除再创建
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("未能创建" + file.toString());
        }
        try {
            //文件输出流
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);

            //位宽16bit, 单声道.
            byte[] buffer = new byte[duration * samplingRate * 2]; // 存储生成的正弦音频.
            SinWave sinWave = new SinWave();
            sinWave.setWave(frequency, phase);

            int sampleLength = sinWave.sample(samplingRate, duration, buffer);
            for (int i = 0; i < sampleLength; i++) {
                dos.write(buffer[i]);
            }

            dos.close();
        } catch (Throwable t) {
            Log.e("MainActivity", "生成正弦波失败");
        }
    }


    private void generateWaveFile(String inFileName, String outFileName,
                                  int samplingRate, int channels,
                                  int bitWidth,
                                  int bufferSize)
    {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen, totalDataLen;
        long longSampleRate = samplingRate;

        //每分钟录到的数据的字节数
        long byteRate = samplingRate * channels * bitWidth / 8;

        byte[] data = new byte[bufferSize];
        try
        {
            in = new FileInputStream(inFileName);
            out = new FileOutputStream(outFileName);
            //获取真实的原始数据长度
            totalAudioLen = in.getChannel().size();
            //wav文件比原始数据文件多出了44个字节，除去表头和文件大小的8个字节剩余文件长度比原始数据多36个字节
            totalDataLen = totalAudioLen + 36;
            //为wav文件写文件头
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            //把原始数据写入到wav文件中。
            while(in.read(data) != -1)
            {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
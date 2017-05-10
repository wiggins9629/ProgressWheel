package com.wiggins.progresswheel;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.wiggins.progresswheel.base.BaseActivity;
import com.wiggins.progresswheel.utils.UIUtils;
import com.wiggins.progresswheel.widget.ProgressWheel;
import com.wiggins.progresswheel.widget.RingProgressBar;
import com.wiggins.progresswheel.widget.RoundProgressBar;
import com.wiggins.progresswheel.widget.TitleView;

import java.util.Random;

/**
 * @Description 自定义圆形进度条
 * @Author 一花一世界
 */
public class MainActivity extends BaseActivity {

    private TitleView titleView;
    private Button mBtnRandom;
    private SeekBar mSeekBar;
    private ProgressWheel mProgressWheel;
    private RoundProgressBar mRoundProgressBar;
    private RingProgressBar mRingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setListener();
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.titleView);
        titleView.setAppTitle(UIUtils.getString(R.string.title));
        titleView.setLeftImageVisibility(View.GONE);
        mBtnRandom = (Button) findViewById(R.id.btn_random);
        mSeekBar = (SeekBar) findViewById(R.id.mSeekBar);
        mProgressWheel = (ProgressWheel) findViewById(R.id.mProgressWheel);
        mRoundProgressBar = (RoundProgressBar) findViewById(R.id.mRoundProgressBar);
        mRingProgressBar = (RingProgressBar) findViewById(R.id.mRingProgressBar);
    }

    private void setListener() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double pg = 360.0 * (seekBar.getProgress() / 100.0);
                mProgressWheel.setProgress((int) pg);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBtnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomBg(mProgressWheel);
                mRingProgressBar.setCurrentProgress(63);
                mRoundProgressBar.setProgress(new float[]{getProgress(56), getProgress(90),
                                getProgress(120), getProgress(88), getProgress(80)},
                        new String[]{"#A0DD2A", "#FFAF8B", "#36D9F1", "#FFD71C", "#A89AFF"});
            }
        });
    }

    private float getProgress(float progress) {
        return (Math.round((progress / 360) * 100));
    }

    private static void randomBg(ProgressWheel wheel) {
        Random random = new Random();
        int firstColour = random.nextInt();
        int secondColour = random.nextInt();
        int patternSize = (1 + random.nextInt(3)) * 8;
        int patternChange = (1 + random.nextInt(3)) * 8;
        int[] pixels = new int[patternSize];
        for (int i = 0; i < patternSize; i++) {
            pixels[i] = (i > patternChange) ? firstColour : secondColour;
        }
        wheel.setRimShader(new BitmapShader(
                Bitmap.createBitmap(pixels, patternSize, 1, Bitmap.Config.ARGB_8888),
                Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT));
        wheel.resetView();
    }
}

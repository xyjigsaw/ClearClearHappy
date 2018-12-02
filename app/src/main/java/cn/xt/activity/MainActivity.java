package cn.xt.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.xt.interfaces.MainViewInterface;
import cn.xt.view.MainView;

public class MainActivity extends AppCompatActivity implements MainViewInterface {

    private MainView mainView;
    private MediaPlayer media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除标题栏
        getSupportActionBar().hide();
        // 取消状态栏
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mainView = (MainView) findViewById(R.id.myMainView);
        mainView.setMainViewInterface(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        playMusic();
    }

    @Override
    public void startGame() {
        Intent intent = new Intent();
        intent.setClass(this.getBaseContext(), GameActivity.class);
        startActivity(intent);
        stopMusic();
    }

    public void startGameMode2() {
        Intent intent = new Intent();
        intent.setClass(this.getBaseContext(), GameActivity.class);
        startActivity(intent);
        stopMusic();
    }


    private void playMusic() {
        media = MediaPlayer.create(getBaseContext(), R.raw.bg_main);
        media.start();

        // 循环播放监听
        media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                media.start();
                // media.setLooping(true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    private void stopMusic() {
        if (media.isPlaying()) {
            media.stop();
        }
    }
}

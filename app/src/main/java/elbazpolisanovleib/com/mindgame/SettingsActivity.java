package elbazpolisanovleib.com.mindgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    SeekBar seekBar;
    int value = 0;
    int volumeCheck = 0;
    TextView backButton;
    ImageView volumeIcon;
    MediaPlayer bgm;
    int volumeVal = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        volumeIcon = findViewById(R.id.volumeIcon);

        seekBar = findViewById(R.id.sb);

        bgm = MediaPlayer.create(this, R.raw.bgm);

        // keep playing the background music in loop
        bgm.setLooping(true);

        if (volumeCheck == 0) {
            // start playing the background music
            SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
            volumeVal = msharedPreferences1.getInt("volumeVal", 10);

            AudioManager mAudioManager = (AudioManager)SettingsActivity.this.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10 * volumeVal / 10, 0);
            bgm.start();
            // set the icon to turn off
            volumeIcon.setImageResource(R.drawable.ic_mute);
        } else {
            // set the icon to turn on
            volumeIcon.setImageResource(R.drawable.ic_volume);
        }

        backButton = findViewById(R.id.settingsbackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences msharedPreferences12 = getSharedPreferences("volumeValue", MODE_PRIVATE);
        value = msharedPreferences12.getInt("volumeVal", 10);
        SharedPreferences msharedPreferences = getSharedPreferences("volumeCheck", MODE_PRIVATE);
        volumeCheck = msharedPreferences.getInt("volume", 0);
        if (volumeCheck == 0) {
            volumeIcon.setImageResource(R.drawable.ic_mute);
        }
        else {
            volumeIcon.setImageResource(R.drawable.ic_volume);
        }

        volumeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOffMusic();
            }
        });
        seekBar.setProgress(value);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                AudioManager mAudioManager = (AudioManager)SettingsActivity.this.getSystemService(Context.AUDIO_SERVICE);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10 * value / 10, 0);
                SharedPreferences sharedPreferences = getSharedPreferences("volumeValue", MODE_PRIVATE);
                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                mSharedEditor.putInt("volumeVal", value);
                mSharedEditor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                AudioManager mAudioManager = (AudioManager)SettingsActivity.this.getSystemService(Context.AUDIO_SERVICE);
//                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10 * value / 10, 0);
//                SharedPreferences sharedPreferences = getSharedPreferences("volumeValue", MODE_PRIVATE);
//                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
//                mSharedEditor.putInt("volumeVal", value);
//                mSharedEditor.commit();
            }
        });
    }

    public void onOffMusic()
    {
        // check what is the previous value for volumecheck in the sharedpreferences
        SharedPreferences msharedPreferences = getSharedPreferences("volumeCheck", MODE_PRIVATE);
        volumeCheck = msharedPreferences.getInt("volume", 0);

        // if volume is on pause it
        if (volumeCheck == 0) {
            volumeCheck = 1;
            volumeIcon.setImageResource(R.drawable.ic_volume);
            SharedPreferences sharedPreferences = getSharedPreferences("volumeCheck", MODE_PRIVATE);
            SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
            mSharedEditor.putInt("volume", 1);
            mSharedEditor.commit();
            bgm.pause();

            Toast.makeText(this, "Music has been turned off!", Toast.LENGTH_SHORT).show();
        } else {

            // strt playing the music and update it's value in shared preferences
            volumeCheck = 0;
            volumeIcon.setImageResource(R.drawable.ic_mute);
            SharedPreferences sharedPreferences = getSharedPreferences("volumeCheck", MODE_PRIVATE);
            SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
            mSharedEditor.putInt("volume", 0);
            mSharedEditor.commit();
            bgm.start();
            Toast.makeText(this, "Music has been turned on!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        if (volumeCheck == 0) {
            // start playing the background music
            SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
            volumeVal = msharedPreferences1.getInt("volumeVal", 10);

            AudioManager mAudioManager = (AudioManager)SettingsActivity.this.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10 * volumeVal / 10, 0);
            bgm.start();
            // set the icon to turn off
            volumeIcon.setImageResource(R.drawable.ic_mute);
        } else {
            // set the icon to turn on
            volumeIcon.setImageResource(R.drawable.ic_volume);
        }
        super.onStart();
    }

    @Override
    protected void onRestart() {
        if (volumeCheck == 0) {
            // start playing the background music
            SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
            volumeVal = msharedPreferences1.getInt("volumeVal", 10);

            AudioManager mAudioManager = (AudioManager)SettingsActivity.this.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10 * volumeVal / 10, 0);
            bgm.start();
            // set the icon to turn off
            volumeIcon.setImageResource(R.drawable.ic_mute);
        } else {
            // set the icon to turn on
            volumeIcon.setImageResource(R.drawable.ic_volume);
        }
        super.onRestart();
    }

    @Override
    protected void onResume() {
        if (volumeCheck == 0) {
            // start playing the background music
            SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
            volumeVal = msharedPreferences1.getInt("volumeVal", 10);

            AudioManager mAudioManager = (AudioManager)SettingsActivity.this.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10 * volumeVal / 10, 0);
            bgm.start();
            // set the icon to turn off
            volumeIcon.setImageResource(R.drawable.ic_mute);
        } else {
            // set the icon to turn on
            volumeIcon.setImageResource(R.drawable.ic_volume);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        bgm.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        bgm.pause();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

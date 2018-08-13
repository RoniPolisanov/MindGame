package elbazpolisanovleib.com.mindgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    // Firebase Analytics Object
    private FirebaseAnalytics mFirebaseAnalytics;

    // Tutorial Button object
    TextView tutorial;

    // Temporary view objects to keep track which image/slide user has pressed
    View prev = null, next = null;

    // Temporary variables to show stars at the bottom of each stage when the game is started
    // These will be used as Leader board score variables.
    int level1Stars, level2Stars, level3Stars, level4Stars, level5Stars, level6Stars;

    // Number of slides/images matched
    int SCORE = 0;

    // Number of stars user has earned for current stage that he/she is playing
    int STARS = 0;

    // Back button shown while playing the game
    TextView backButton;

    // The user won dialog will be shown in this text view
    TextView userWon;

    // the playbutton on the main screen
    ImageView playButton;

    // share button on the main screen, first it was settings then changed to share button
    ImageView settingsButton;

    // When stages are shown each star would be shown after verifying from shared preferences
    ImageView beginnerStageStar1, beginnerStageStar2, beginnerStageStar3, easyStageStar1, easyStageStar2, easyStageStar3, mediumStage1, mediumStage2, mediumStage3, hardStage1, hardStage2, hardStage3, hardestStage1, hardestStage2, hardestStage3, masterStage1, masterStage2, masterStage3;

    // When stages are shown these are textivews to show the beginner, easy, medium, hard, hardest and master stage textviews
    TextView beginner, easy, medium, hard, hardest, master;

    // This is the blue background views for each stage..
    // These are loaded when the playbutton is pressed and stages are shown.
    View beginnerBackground, easyBackground, mediumBackground, hardBackground, hardestBackground, masterBackground;

    // keep track of state of the game
    // 0 means user is at the start screen, where there is logo and other buttons
    // 1 means user has pressed playbutton and at the stages are shown to the user
    // 2 means the user is playing the game and is at a level
    int state = 0;

    // Logo of the game will be shown when state is 0
    TextView logo;

    // These are all the backs of animal images in the stages, number of images are shown according to stage
    // Only required images are shown others are hidden
    ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10, image11, image12, image13, image14, image15, image16;

    // The timer textview in the stage
    TextView timer;

    // These are placeholders for images, where the animal images are shown during the play
    ImageView animalImage1, animalImage2, animalImage3, animalImage4, animalImage5, animalImage6, animalImage7, animalImage8, animalImage9, animalImage10, animalImage11, animalImage12, animalImage13, animalImage14, animalImage15, animalImage16;

    // variable to check which stage is being played at current time
    int stageImageCheck = 0;

    // These stars are shown according to number of stars user has scored.
    // 1 star is given if user completes game after the time has run out
    // 2 stars are given if user completes the game before time
    // 3 stars are given if user completes the game 10 seconds before time.
    ImageView winStar1, winStar2, winStar3;

    // this is click sound, this is played when user clicks on a button
    MediaPlayer click;

    // Sound played when user selects same images
    MediaPlayer matched;

    // MediaPlayer object for background music
    MediaPlayer bgm;

    // Pause button, displayed when user is playing the game..
    // The text in pause button becomes resume game when game is paused
    TextView pauseButton;

    // Variable to keep check if game is paused or not
    int pauseCheck = 0;

    // Variable to keep check if the sound is turned off or not
    int volumeCheck = 0;

    // On off button for sound
    ImageView soundButton;

    //This is where the icon will be changed
    // If user has selected volume off then off icon will be shown, otherwise on icon will be shown
    ImageView volumeIcon;

    // About button in main menu
    TextView about;
    int volumeVal = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization of fabric
        Fabric.with(this, new Crashlytics());

        // Making the activity a full screen activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // binding the tutorial button with xml view
        tutorial = findViewById(R.id.tutorial);

        // on click listener for tutorial button
        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Take the user to tutorial activity
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Logging the analytics event
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "APP Opened Just Now");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
        volumeIcon = findViewById(R.id.volumeIcon);
        about = findViewById(R.id.about);

        // About button on click listener
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Take user to another activity
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        // Checking from shared preferences if user has turned off volume or it is on
        SharedPreferences msharedPreferences = getSharedPreferences("volumeCheck", MODE_PRIVATE);
        volumeCheck = msharedPreferences.getInt("volume", 0);

        // initializing the media player with background music
        bgm = MediaPlayer.create(this, R.raw.bgm);

        // keep playing the background music in loop
        bgm.setLooping(true);

        // if volume is not turned off, start the music and set a turning off image for volume button
        if (volumeCheck == 0) {
            // start playing the background music
            SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
            volumeVal = msharedPreferences1.getInt("volumeVal", 10);
            AudioManager mAudioManager = (AudioManager)MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10 * volumeVal / 10, 0);
            bgm.start();
            // set the icon to turn off
            volumeIcon.setImageResource(R.drawable.ic_settings);
        } else {
            // set the icon to turn on
            volumeIcon.setImageResource(R.drawable.ic_settings);
        }

        soundButton = findViewById(R.id.volumeButton);

        // on click listener for sound button
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // binding pause button with it's xml view
        pauseButton = findViewById(R.id.pauseGame);

        // make the pause button disappear because game hasn't started yet
        pauseButton.setVisibility(View.GONE);

        // initializing the mediaplayer object with click sound
        click = MediaPlayer.create(this, R.raw.click);

        // initializing the mediaplayer object with matched sound
        matched = MediaPlayer.create(this, R.raw.matched);

        // binding the winstar view
        winStar1 = findViewById(R.id.winStar1);

        // binding the winstar view
        winStar2 = findViewById(R.id.winStar2);

        // binding the winstar view
        winStar3 = findViewById(R.id.winStar3);

        // binding the userwon view
        userWon = findViewById(R.id.userwon);

        // making the userwon view disappear because user hasn't won yet
        userWon.setVisibility(View.GONE);

        // binding the backbutton view
        backButton = findViewById(R.id.backbutton);

        // making the backbutton disappear because game hasn't started yet
        backButton.setVisibility(View.GONE);

        // binding the logo view
        logo = findViewById(R.id.logo);

        // Making the logo visible because game has just started and it's first/main screen
        logo.setVisibility(View.VISIBLE);

        // binding the playbutton with xml view
        playButton = findViewById(R.id.playButton);

        // binding timer view with xml
        timer = findViewById(R.id.timer);

        // image binding
        // these are the back sides of the images shown in stages
        // these will appear only when a stage is started
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        image6 = findViewById(R.id.image6);
        image7 = findViewById(R.id.image7);
        image8 = findViewById(R.id.image8);
        image9 = findViewById(R.id.image9);
        image10 = findViewById(R.id.image10);
        image11 = findViewById(R.id.image11);
        image12 = findViewById(R.id.image12);
        image13 = findViewById(R.id.image13);
        image14 = findViewById(R.id.image14);
        image15 = findViewById(R.id.image15);
        image16 = findViewById(R.id.image16);

        // these are the animal images
        // these are binded with their xml views
        // these will only appear when game has started and a user clicks on the back of these images
        animalImage1 = findViewById(R.id.animalImage1);
        animalImage2 = findViewById(R.id.animalImage2);
        animalImage3 = findViewById(R.id.animalImage3);
        animalImage4 = findViewById(R.id.animalImage4);
        animalImage5 = findViewById(R.id.animalImage5);
        animalImage6 = findViewById(R.id.animalImage6);
        animalImage7 = findViewById(R.id.animalImage7);
        animalImage8 = findViewById(R.id.animalImage8);
        animalImage9 = findViewById(R.id.animalImage9);
        animalImage10 = findViewById(R.id.animalImage10);
        animalImage11 = findViewById(R.id.animalImage11);
        animalImage12 = findViewById(R.id.animalImage12);
        animalImage13 = findViewById(R.id.animalImage13);
        animalImage14 = findViewById(R.id.animalImage14);
        animalImage15 = findViewById(R.id.animalImage15);
        animalImage16 = findViewById(R.id.animalImage16);

        // binding the settings button with xml view
        settingsButton = findViewById(R.id.settingsButton);

        // the stars below the stage name
        // these stars will only appear when user has played the game and scored at least one star..
        // if user has scored maximum 1 score in stage 1 then in stage 1 only 1 star will appear.
        // the number of stars appearing under the stage name will appear according to the high scores scored in the game
        beginnerStageStar1 = findViewById(R.id.beginnerStageStar1);
        beginnerStageStar2 = findViewById(R.id.beginnerStageStar2);
        beginnerStageStar3 = findViewById(R.id.beginnerStageStar3);
        easyStageStar1 = findViewById(R.id.easyStageStar1);
        easyStageStar2 = findViewById(R.id.easyStageStar2);
        easyStageStar3 = findViewById(R.id.easyStageStar3);
        mediumStage1 = findViewById(R.id.mediumStageStar1);
        mediumStage2 = findViewById(R.id.mediumStageStar2);
        mediumStage3 = findViewById(R.id.mediumStageStar3);
        hardStage1 = findViewById(R.id.hardStageStar1);
        hardStage2 = findViewById(R.id.hardStageStar2);
        hardStage3 = findViewById(R.id.hardStageStar3);
        hardestStage1 = findViewById(R.id.hardestStageStar1);
        hardestStage2 = findViewById(R.id.hardestStageStar2);
        hardestStage3 = findViewById(R.id.hardestStageStar3);
        masterStage1 = findViewById(R.id.masterStageStar1);
        masterStage2 = findViewById(R.id.masterStageStar2);
        masterStage3 = findViewById(R.id.masterStageStar3);

        // these are names of the stages
        beginner = findViewById(R.id.beginnerTV);
        easy = findViewById(R.id.easyTV);
        medium = findViewById(R.id.mediumTV);
        hard = findViewById(R.id.hardTV);
        hardest = findViewById(R.id.hardestTV);
        master = findViewById(R.id.masterTV);

        // these are the blue backgrounds for each stage
        beginnerBackground = findViewById(R.id.beginnerStage);
        easyBackground = findViewById(R.id.easyStage);
        mediumBackground = findViewById(R.id.mediumStage);
        hardBackground = findViewById(R.id.hardStage);
        hardestBackground = findViewById(R.id.hardestStage);
        masterBackground = findViewById(R.id.masterStage);

        // setting on click listener for back button
        // back button will only appear when a user has started a stage
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set the pause value to default i.e game is not paused
                // set the time of stage to default i.e game will start from initial
                pauseCheck = 0;
                checktime = 0;

                // Make the pause button disappear
                pauseButton.setText("Pause Game");
                pauseButton.setVisibility(View.GONE);

                // if user has turned on volume, make the sound of button
                if (volumeCheck == 0) {
                    SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
                    volumeVal = msharedPreferences1.getInt("volumeVal", 10);
                    AudioManager mAudioManager = (AudioManager)MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15 * volumeVal / 10, 0);
                    click.start();
                }

                // make the stars that appear on win disappear.
                winStar1.setVisibility(View.GONE);
                winStar2.setVisibility(View.GONE);
                winStar3.setVisibility(View.GONE);

                // hide the user has won message
                userWon.setVisibility(View.GONE);

                // set the score to default i.e 0
                SCORE = 0;

                // set the stars scored to 0 i.e. default value
                STARS = 0;

                // set the prev and next to null
                // these are temporary views to keep track which images user has clicked on
                prev = null;
                next = null;

                // these are for backside of the images
                prevView = null;
                nextView = null;

                // make the state 1, means user is on the stages area
                state = 1;

                // make the timer disappear because game isn't started
                timer.setVisibility(View.GONE);

                // make the playbutton disappear because it's stages area
                playButton.setVisibility(View.INVISIBLE);

                //  make the play about disappear because it's stages area
                about.setVisibility(View.GONE);

                //make the tutorial button disappear because it's stages area
                tutorial.setVisibility(View.GONE);

                // make the settings button appear because on stages area settings button and
                // volume control is shown
                settingsButton.setVisibility(View.VISIBLE);
                soundButton.setVisibility(View.VISIBLE);

                // hide the stage
                hideStage();

                // go to the one state, means show the images for stages
                one();

                // make the back button disappear because on stages area there is no back button
                backButton.setVisibility(View.GONE);

                // turn the timer off
                countDownTimer.cancel();
            }
        });

        // on click listener for playbutton
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if volume is turned on make the sound of button click
                if (volumeCheck == 0) {
                    SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
                    volumeVal = msharedPreferences1.getInt("volumeVal", 10);
                    AudioManager mAudioManager = (AudioManager)MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15 * volumeVal / 10, 0);
                    click.start();
                }

                // if we are at state 0 means main page that opens up on game opening
                // then make the about, tutorial, logo, and playbutton disappear
                // because we are going to the stages area
                if (state == 0) {
                    about.setVisibility(View.GONE);
                    tutorial.setVisibility(View.GONE);

                    // load the stages area
                    one();

                    // make the logo disappear
                    logo.animate().alpha(0.0f);

                    // make the playbutton disappear by fireworks.
                    hideViewByAnimation(playButton);

                    // hide the logo as the stages area is being shown
                    logo.setVisibility(View.GONE);
                }
            }
        });

        // background for each stage, when user will click on beginner stage, this code will execute
        beginnerBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // As the user has started stage 1
                // make the pause button visible
                pauseButton.setVisibility(View.VISIBLE);

                // hide the stages area as user has selected stage 1
                hideStageArea();

                // make the timer visible because game has started
                timer.setVisibility(View.VISIBLE);

                //hide the settings, play, about and tutorial button because game has started and we are not in main
                // menu or stages area
                settingsButton.setVisibility(View.GONE);
                soundButton.setVisibility(View.GONE);
                playButton.setVisibility(View.GONE);
                about.setVisibility(View.GONE);
                tutorial.setVisibility(View.GONE);

                // load stage 1 and make the back button visible
                // because user is in a stage and he/she should be able to go back
                loadStage1();
                backButton.setVisibility(View.VISIBLE);
            }
        });

        // onclick listener for stage2/ easy stage
        easyBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide settings, sound, play, about, tutorial buttons
                // show pause, timer, back buttons
                // load stage 2
                pauseButton.setVisibility(View.VISIBLE);
                hideStageArea();
                settingsButton.setVisibility(View.GONE);
                soundButton.setVisibility(View.GONE);
                playButton.setVisibility(View.GONE);
                about.setVisibility(View.GONE);
                tutorial.setVisibility(View.GONE);
                timer.setVisibility(View.VISIBLE);
                loadStage2();
                backButton.setVisibility(View.VISIBLE);
            }
        });

        mediumBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide stages area
                // hide settings, sound, play, about, tutorial buttons
                // show pause, timer, back buttons
                // load stage 3
                pauseButton.setVisibility(View.VISIBLE);
                hideStageArea();
                settingsButton.setVisibility(View.GONE);
                soundButton.setVisibility(View.GONE);
                playButton.setVisibility(View.GONE);
                about.setVisibility(View.GONE);
                tutorial.setVisibility(View.GONE);
                timer.setVisibility(View.VISIBLE);
                loadStage3();
                backButton.setVisibility(View.VISIBLE);
            }
        });

        hardBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide stages area
                // hide settings, sound, play, about, tutorial buttons
                // show pause, timer, back buttons
                // load stage 4
                pauseButton.setVisibility(View.VISIBLE);
                hideStageArea();
                settingsButton.setVisibility(View.GONE);
                soundButton.setVisibility(View.GONE);
                playButton.setVisibility(View.GONE);
                about.setVisibility(View.GONE);
                tutorial.setVisibility(View.GONE);
                timer.setVisibility(View.VISIBLE);
                loadStage4();
                backButton.setVisibility(View.VISIBLE);
            }
        });

        hardestBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide stages area
                // hide settings, sound, play, about, tutorial buttons
                // show pause, timer, back buttons
                // load stage 5
                pauseButton.setVisibility(View.VISIBLE);
                hideStageArea();
                settingsButton.setVisibility(View.GONE);
                soundButton.setVisibility(View.GONE);
                playButton.setVisibility(View.GONE);
                about.setVisibility(View.GONE);
                tutorial.setVisibility(View.GONE);
                timer.setVisibility(View.VISIBLE);
                loadStage5();
                backButton.setVisibility(View.VISIBLE);
            }
        });

        masterBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide stages area
                // hide settings, sound, play, about, tutorial buttons
                // show pause, timer, back buttons
                // load stage 6
                pauseButton.setVisibility(View.VISIBLE);
                hideStageArea();
                settingsButton.setVisibility(View.GONE);
                soundButton.setVisibility(View.GONE);
                playButton.setVisibility(View.GONE);
                about.setVisibility(View.GONE);
                tutorial.setVisibility(View.GONE);
                timer.setVisibility(View.VISIBLE);
                loadStage6();
                backButton.setVisibility(View.VISIBLE);
            }
        });

        // user will be able to share the game using this share button
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // allow the user to share the app, through any means he/she wants
                Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
                txtIntent.setType("text/plain");
                txtIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Mind GAME");
                txtIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hi, Check this amazing Mind Game, it's awesome! Download from below link.\nhttps://play.google.com/store/apps/details?id=elbazpolisanovleib.com.mindgame");
                startActivity(Intent.createChooser(txtIntent, "Share"));
            }
        });

        // game will be paused if user clicks on pause button
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pauseCheck = 0 means game is not paused
                // pauseCheck = 1 means game is paused
                if (pauseCheck == 0) {
                    pauseButton.setText("Resume Game");
                    pauseCheck = 1;
                } else {
                    pauseCheck = 0;
                    pauseButton.setText("Pause Game");
                }
            }
        });
    }

    // this function will load the stages area
    public void one() {
        // state = 1 means we are in the stages area
        // state = 0 means we are in the main menu
        // state = 2 means we are playing the game on any of the stages
        state = 1;

        // as we are going to load the stages area, so there are maximum number of stars each
        // user has scored, to keep track of stars we use shared preferences
        // so getting the maximum number of stars scored by user from the shared preferences
        SharedPreferences msharedPreferences = getSharedPreferences("stars", MODE_PRIVATE);

        // these integer variables hold maximum number of stars scored by user on each stage
        level1Stars = msharedPreferences.getInt("stage1star", 0);
        level2Stars = msharedPreferences.getInt("stage2star", 0);
        level3Stars = msharedPreferences.getInt("stage3star", 0);
        level4Stars = msharedPreferences.getInt("stage4star", 0);
        level5Stars = msharedPreferences.getInt("stage5star", 0);
        level6Stars = msharedPreferences.getInt("stage6star", 0);

        // if user has scored 1 in level 1 show 1 stars
        // if maximum stars is 2 show 2 stars, if 3 then show 3 stars
        // and stars are shown by animation
        switch (level1Stars) {
            case 1:
                showViewByAnimation(beginnerStageStar1);
                break;
            case 2:
                showViewByAnimation(beginnerStageStar1);
                showViewByAnimation(beginnerStageStar2);
                break;
            case 3:
                showViewByAnimation(beginnerStageStar1);
                showViewByAnimation(beginnerStageStar2);
                showViewByAnimation(beginnerStageStar3);
                break;
        }

        // if user has scored 1 in level 2 show 1 stars
        // if maximum stars is 2 show 2 stars, if 3 then show 3 stars
        // and stars are shown by animation
        switch (level2Stars) {
            case 1:
                showViewByAnimation(easyStageStar1);
                break;
            case 2:
                showViewByAnimation(easyStageStar1);
                showViewByAnimation(easyStageStar2);
                break;
            case 3:
                showViewByAnimation(easyStageStar1);
                showViewByAnimation(easyStageStar2);
                showViewByAnimation(easyStageStar3);
                break;
        }

        // if user has scored 1 in level 3 show 1 stars
        // if maximum stars is 2 show 2 stars, if 3 then show 3 stars
        // and stars are shown by animation
        switch (level3Stars) {
            case 1:
                showViewByAnimation(mediumStage1);
                break;
            case 2:
                showViewByAnimation(mediumStage1);
                showViewByAnimation(mediumStage2);
                break;
            case 3:
                showViewByAnimation(mediumStage1);
                showViewByAnimation(mediumStage2);
                showViewByAnimation(mediumStage3);
                break;
        }

        // if user has scored 1 in level 4 show 1 stars
        // if maximum stars is 2 show 2 stars, if 3 then show 3 stars
        // and stars are shown by animation
        switch (level5Stars) {
            case 1:
                showViewByAnimation(hardStage1);
                break;
            case 2:
                showViewByAnimation(hardStage1);
                showViewByAnimation(hardStage2);
                break;
            case 3:
                showViewByAnimation(hardStage1);
                showViewByAnimation(hardStage2);
                showViewByAnimation(hardStage3);
                break;
        }

        // if user has scored 1 in level 4 show 1 stars
        // if maximum stars is 2 show 2 stars, if 3 then show 3 stars
        // and stars are shown by animation
        switch (level5Stars) {
            case 1:
                showViewByAnimation(hardestStage1);
                break;
            case 2:
                showViewByAnimation(hardestStage1);
                showViewByAnimation(hardestStage2);
                break;
            case 3:
                showViewByAnimation(hardestStage1);
                showViewByAnimation(hardestStage2);
                showViewByAnimation(hardestStage3);
                break;
        }

        // if user has scored 1 in level 5 show 1 stars
        // if maximum stars is 2 show 2 stars, if 3 then show 3 stars
        // and stars are shown by animation
        switch (level6Stars) {
            case 1:
                showViewByAnimation(masterStage1);
                break;
            case 2:
                showViewByAnimation(masterStage1);
                showViewByAnimation(masterStage2);
                break;
            case 3:
                showViewByAnimation(masterStage1);
                showViewByAnimation(masterStage2);
                showViewByAnimation(masterStage3);
                break;
        }

        // showing the name of each stage by animation
        showViewByAnimation(beginner);
        showViewByAnimation(easy);
        showViewByAnimation(medium);
        showViewByAnimation(hard);
        showViewByAnimation(hardest);
        showViewByAnimation(master);

        // showing the blue background of each stage by animation
        showViewByAnimation(beginnerBackground);
        showViewByAnimation(easyBackground);
        showViewByAnimation(mediumBackground);
        showViewByAnimation(hardBackground);
        showViewByAnimation(hardestBackground);
        showViewByAnimation(masterBackground);
    }

    // This function is to go back to main menu
    public void goToOne() {
        // state 0 means main menu
        // state 1 means stages area
        // state 2 means playing a game
        state = 0;

        // make the logo visible because we are in the main menu
        logo.setVisibility(View.VISIBLE);

        // make the logo visible by animation and the animation will take 1500
        logo.animate().alpha(1.0f).setDuration(1500);

        // hiding the stages area

        // hide stars for stage 1
        hideViewByAnimation(beginnerStageStar1);
        hideViewByAnimation(beginnerStageStar2);
        hideViewByAnimation(beginnerStageStar3);

        // hide stars for stage 2
        hideViewByAnimation(easyStageStar1);
        hideViewByAnimation(easyStageStar2);
        hideViewByAnimation(easyStageStar3);

        // hide stars for stage 3
        hideViewByAnimation(mediumStage1);
        hideViewByAnimation(mediumStage2);
        hideViewByAnimation(mediumStage3);

        // hide stars for stage 4
        hideViewByAnimation(hardStage1);
        hideViewByAnimation(hardStage2);
        hideViewByAnimation(hardStage3);

        // hide stars for stage 5
        hideViewByAnimation(hardestStage1);
        hideViewByAnimation(hardestStage2);
        hideViewByAnimation(hardestStage3);

        // hide stars for stage 6
        hideViewByAnimation(masterStage1);
        hideViewByAnimation(masterStage2);
        hideViewByAnimation(masterStage3);

        // hide name of stage 1 by animation
        hideViewByAnimation(beginner);

        // hide name of stage 3 by animation
        hideViewByAnimation(easy);

        // hide name of stage 3 by animation
        hideViewByAnimation(medium);

        // hide name of stage 4 by animation
        hideViewByAnimation(hard);

        // hide name of stage 5 by animation
        hideViewByAnimation(hardest);

        // hide name of stage 6 by animation
        hideViewByAnimation(master);

        // hide the blue background for stage 1
        hideViewByAnimation(beginnerBackground);

        // hide the blue background for stage 2
        hideViewByAnimation(easyBackground);

        // hide the blue background for stage 3
        hideViewByAnimation(mediumBackground);

        // hide the blue background for stage 4
        hideViewByAnimation(hardBackground);

        // hide the blue background for stage 5
        hideViewByAnimation(hardestBackground);

        // hide the blue background for stage 6
        hideViewByAnimation(masterBackground);
    }

    // This method is for showing the view by animation
    // whatever view is passed to this method is shown by animation
    public void showViewByAnimation(View view) {
        // if view is not null
        if (view != null) {
            // View.VISIBLE means view is present on the layout and visible
            // View.INVISIBLE means view is present but not visible
            // View.GONE means view is not present on the layout

            // Make the view present and visible on the layout
            view.setVisibility(View.VISIBLE);

            // make view present and invisible
            // so that it can be shown by animation
            view.setAlpha(0.0f);

            // Rotate the view 270 degrees along Y axis
            view.setRotationY(270);

            // now the view would be shown by animation, the view would rotate from 270 to 360 degrees
            // in one second
            view.animate()
                    .alpha(1.0f).rotationY(360)
                    .setDuration(1000)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });
        }
    }

    // Temporary variable to store the view which is to be hidden
    View viewForHiding;

    // Method for hiding views by animation
    public void hideViewByAnimation(View view) {
        // temporary variable used to save the views that are to be hidden
        viewForHiding = view;
        // if no stage is being played then hide the playbutton for stages screen
        if (stageImageCheck == 0) {
            if (view == playButton) {
                // rotate the view 180 and hide it in 1 second
                view.animate().rotationY(180)
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                            }
                        });
            } else {
                // if view is not playbutton hide the view
                view.animate().rotationY(180)
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                viewForHiding.setVisibility(View.GONE);
                            }
                        });
            }
        } else {
            // if any stage is being played make the view visible
            // and let it be there on the layout.
            view.animate().rotationY(180)
                    .alpha(0.0f)
                    .setDuration(1000)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            viewForHiding.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }

    // Load stage 1
    // This function will be called when user clicks on beginner stage
    public void loadStage1() {
        // hiding the stages area
        goToOne();

        // making the logo disappear
        logo.setVisibility(View.GONE);

        // state = 1 means user is in the main menu
        // state = 2 means user is in the stages area
        // state = 3 means user is playing a stage
        state = 3;

        // As this is third stage so before starting a stage all of the images in a stage are
        // hidden from the layout, even they are not present on the layout
        // Making the images visible that are to be shown in stage 1
        image1.setVisibility(View.VISIBLE);
        image2.setVisibility(View.VISIBLE);
        image3.setVisibility(View.VISIBLE);

        // Showing the images using animation
        rotateImage(image1);
        rotateImage(image2);
        rotateImage(image3);

        // Make the other images not required on stage 1 disappear
        image4.setVisibility(View.GONE);
        image5.setVisibility(View.GONE);
        image6.setVisibility(View.GONE);

        // second row of images shown on  stage 1
        // showing second row of images
        image7.setVisibility(View.VISIBLE);
        image8.setVisibility(View.VISIBLE);
        image9.setVisibility(View.VISIBLE);

        // rotating the images, showing them by animation
        rotateImage(image7);
        rotateImage(image8);
        rotateImage(image9);

        // Hiding the images not required in stage 1 second row and third row
        image10.setVisibility(View.GONE);
        image11.setVisibility(View.GONE);
        image12.setVisibility(View.GONE);
        image13.setVisibility(View.GONE);
        image14.setVisibility(View.GONE);
        image15.setVisibility(View.GONE);
        image16.setVisibility(View.GONE);

        // Let the user interact with images shown on stage 1
        playStage1();

        // set the images in pairs for stage 1
        loadAnimalImagesForStage1();
    }

    // Method for images to be shown with animation
    // this is only used in stages
    public void rotateImage(View view) {
        // make the image appear
        // ALPHA 0 means image is there but not visible
        // ALPHA 1 means image is there and it's visible
        // ALPHA 0.5 means image is half visible
        view.setAlpha(1.0f);

        // rotate the image 180 degrees around the Y axis.
        view.setRotationY(180);

        // Make the image rotate around Y axis
        // And add a dely of 1 second
        view.animate().rotationY(360)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                });
    }

    // This function will be called to load stage 2
    public void loadStage2() {
        // Load Stage 2
        // Hide the stages area
        goToOne();

        // Make the logo gone because it's not the main screen
        logo.setVisibility(View.GONE);

        // set state to 3 which means the user is playing game
        state = 3;

        // Show Only four images in the first row
        // because total 8 images are included in the stage2
        image1.setVisibility(View.VISIBLE);
        image2.setVisibility(View.VISIBLE);
        image3.setVisibility(View.VISIBLE);
        image4.setVisibility(View.VISIBLE);

        // rotate the four images in stage 2
        rotateImage(image1);
        rotateImage(image2);
        rotateImage(image3);
        rotateImage(image4);

        // hide image 5 and image 6 on stage 2 because these aren't required
        image5.setVisibility(View.GONE);
        image6.setVisibility(View.GONE);

        // show the images in second row
        image7.setVisibility(View.VISIBLE);
        image8.setVisibility(View.VISIBLE);
        image9.setVisibility(View.VISIBLE);
        image10.setVisibility(View.VISIBLE);

        // rotate the images in second row
        rotateImage(image7);
        rotateImage(image8);
        rotateImage(image9);
        rotateImage(image10);

        // hide remaining images because we have to show only 8 images in second stage
        image11.setVisibility(View.GONE);
        image12.setVisibility(View.GONE);
        image13.setVisibility(View.GONE);
        image14.setVisibility(View.GONE);
        image15.setVisibility(View.GONE);
        image16.setVisibility(View.GONE);

        // let the user interact/play stage 2
        playStage2();

        // set the images for stage 2, which of the images will be same
        loadAnimalImagesForStage2();
    }

    public void loadStage3() {
        // Load Stage 2
        // Hide the stages area
        // Make the logo gone because it's not the main screen
        goToOne();
        logo.setVisibility(View.GONE);
        // set state to 3 which means the user is playing game
        state = 3;

        // Show Only five images in the first row
        // because total 10 images are included in the stage3
        image1.setVisibility(View.VISIBLE);
        image2.setVisibility(View.VISIBLE);
        image3.setVisibility(View.VISIBLE);
        image4.setVisibility(View.VISIBLE);
        image5.setVisibility(View.VISIBLE);

        // rotate and show the five images in stage 3
        rotateImage(image1);
        rotateImage(image2);
        rotateImage(image3);
        rotateImage(image4);
        rotateImage(image5);

        // hide image 6 on stage 3 because these aren't required
        image6.setVisibility(View.GONE);

        // show the images in second row
        image7.setVisibility(View.VISIBLE);
        image8.setVisibility(View.VISIBLE);
        image9.setVisibility(View.VISIBLE);
        image10.setVisibility(View.VISIBLE);
        image11.setVisibility(View.VISIBLE);

        // rotate the images in second row
        rotateImage(image7);
        rotateImage(image8);
        rotateImage(image9);
        rotateImage(image10);
        rotateImage(image11);

        // hide remaining images because we have to show only 10 images in second stage
        image12.setVisibility(View.GONE);
        image13.setVisibility(View.GONE);
        image14.setVisibility(View.GONE);
        image15.setVisibility(View.GONE);
        image16.setVisibility(View.GONE);

        // let the user interact/play stage
        playStage3();

        // set the images for stage 3, which of the images will be same
        loadAnimalImagesForStage3();
    }

    public void loadStage4() {
        // Load Stage 2
        // Hide the stages area
        // Make the logo gone because it's not the main screen
        goToOne();
        logo.setVisibility(View.GONE);

        // set state to 3 which means the user is playing game
        state = 3;

        // Show Only 4 images in the first row
        // because total 12 images are included in the stage4
        image1.setVisibility(View.VISIBLE);
        image2.setVisibility(View.VISIBLE);
        image3.setVisibility(View.VISIBLE);
        image4.setVisibility(View.VISIBLE);

        // rotate the 6 images in stage 4
        rotateImage(image1);
        rotateImage(image2);
        rotateImage(image3);
        rotateImage(image4);

        // hide image 5 and image 6 on stage 4 because these aren't required
        image5.setVisibility(View.GONE);
        image6.setVisibility(View.GONE);

        // Show Only 4 images in the second row
        // because total 12 images are included in the stage4
        image7.setVisibility(View.VISIBLE);
        image8.setVisibility(View.VISIBLE);
        image9.setVisibility(View.VISIBLE);
        image10.setVisibility(View.VISIBLE);

        // rotate the four images in stage 4
        rotateImage(image7);
        rotateImage(image8);
        rotateImage(image9);
        rotateImage(image10);

        // hide image 11 and image 12 on stage 4 because these aren't required
        image11.setVisibility(View.GONE);
        image12.setVisibility(View.GONE);

        // Show Only 4 images in the third row
        // because total 12 images are included in the stage4
        image13.setVisibility(View.VISIBLE);
        image14.setVisibility(View.VISIBLE);
        image15.setVisibility(View.VISIBLE);
        image16.setVisibility(View.VISIBLE);

        // rotate the four images in stage 4
        rotateImage(image13);
        rotateImage(image14);
        rotateImage(image15);
        rotateImage(image16);

        // let the user interact/play stage 2
        // set the images for stage 2, which of the images will be same
        playStage4();
        loadAnimalImagesForStage4();
    }

    public void loadStage5() {
        // Load Stage 5
        // Hide the stages area
        goToOne();

        // Make the logo gone because it's not the main screen
        logo.setVisibility(View.GONE);
        // set state to 3 which means the user is playing game
        state = 3;

        // Show Only five images in the first row
        // because total 14 images are included in the stage 5
        image1.setVisibility(View.VISIBLE);
        image2.setVisibility(View.VISIBLE);
        image3.setVisibility(View.VISIBLE);
        image4.setVisibility(View.VISIBLE);
        image5.setVisibility(View.VISIBLE);

        // rotate the five images in stage 2
        rotateImage(image1);
        rotateImage(image2);
        rotateImage(image3);
        rotateImage(image4);
        rotateImage(image5);

        // hide image 6 on stage 5 because these aren't required
        image6.setVisibility(View.GONE);

        // show the images in second row
        image7.setVisibility(View.VISIBLE);
        image8.setVisibility(View.VISIBLE);
        image9.setVisibility(View.VISIBLE);
        image10.setVisibility(View.VISIBLE);
        image11.setVisibility(View.VISIBLE);

        // rotate the images in second row
        rotateImage(image7);
        rotateImage(image8);
        rotateImage(image9);
        rotateImage(image10);
        rotateImage(image11);

        // hide remaining images because we have to show only 14 images in fifth stage
        image12.setVisibility(View.GONE);

        // show the images in third row
        image13.setVisibility(View.VISIBLE);
        image14.setVisibility(View.VISIBLE);
        image15.setVisibility(View.VISIBLE);
        image16.setVisibility(View.VISIBLE);

        // rotate the images in third row
        rotateImage(image13);
        rotateImage(image14);
        rotateImage(image15);
        rotateImage(image16);

        // let the user interact/play stage 5
        playStage5();

        // set the images for stage 5, which of the images will be same
        loadAnimalImagesForStage5();
    }

    public void loadStage6() {
        // Load Stage 6
        // Hide the stages area
        goToOne();
        logo.setVisibility(View.GONE);

        // set state to 3 which means the user is playing game
        state = 3;

        // Showing six images in the first row
        // because total 16 images are included in the stage6
        image1.setVisibility(View.VISIBLE);
        image2.setVisibility(View.VISIBLE);
        image3.setVisibility(View.VISIBLE);
        image4.setVisibility(View.VISIBLE);
        image5.setVisibility(View.VISIBLE);
        image6.setVisibility(View.VISIBLE);

        // rotate the six images in stage
        rotateImage(image1);
        rotateImage(image2);
        rotateImage(image3);
        rotateImage(image4);
        rotateImage(image5);
        rotateImage(image6);

        // show the images in second row.
        image7.setVisibility(View.VISIBLE);
        image8.setVisibility(View.VISIBLE);
        image9.setVisibility(View.VISIBLE);
        image10.setVisibility(View.VISIBLE);
        image11.setVisibility(View.VISIBLE);
        image12.setVisibility(View.VISIBLE);

        // rotate the images in second row
        rotateImage(image7);
        rotateImage(image8);
        rotateImage(image9);
        rotateImage(image10);
        rotateImage(image11);
        rotateImage(image12);

        // rotate the images in third row
        image13.setVisibility(View.VISIBLE);
        image14.setVisibility(View.VISIBLE);
        image15.setVisibility(View.VISIBLE);
        image16.setVisibility(View.VISIBLE);

        // rotate the images in third row
        rotateImage(image13);
        rotateImage(image14);
        rotateImage(image15);
        rotateImage(image16);

        // Let the user interact/play with images
        playStage6();

        // decide which images will be same in level 6
        loadAnimalImagesForStage6();
    }

    // This function will be called when the user would press back button
    public void hideStage() {
        // stageImageCheck = 0 means there is no stage being played currently
        stageImageCheck = 0;

        // Hide all of the images on screen used to play
        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);
        image4.setVisibility(View.GONE);
        image5.setVisibility(View.GONE);
        image6.setVisibility(View.GONE);
        image7.setVisibility(View.GONE);
        image8.setVisibility(View.GONE);
        image9.setVisibility(View.GONE);
        image10.setVisibility(View.GONE);
        image11.setVisibility(View.GONE);
        image12.setVisibility(View.GONE);
        image13.setVisibility(View.GONE);
        image14.setVisibility(View.GONE);
        image15.setVisibility(View.GONE);
        image16.setVisibility(View.GONE);

        // hide the animal characters
        animalImage1.setVisibility(View.GONE);
        animalImage2.setVisibility(View.GONE);
        animalImage3.setVisibility(View.GONE);
        animalImage4.setVisibility(View.GONE);
        animalImage5.setVisibility(View.GONE);
        animalImage6.setVisibility(View.GONE);
        animalImage7.setVisibility(View.GONE);
        animalImage8.setVisibility(View.GONE);
        animalImage9.setVisibility(View.GONE);
        animalImage10.setVisibility(View.GONE);
        animalImage11.setVisibility(View.GONE);
        animalImage12.setVisibility(View.GONE);
        animalImage13.setVisibility(View.GONE);
        animalImage14.setVisibility(View.GONE);
        animalImage15.setVisibility(View.GONE);
        animalImage16.setVisibility(View.GONE);
    }

    // prevView means the backside of image selected second last
    View prevView = null;

    // nextView means the backside of image selected last
    View nextView = null;

    //clickCount is used to track how many images have been selected currently
    int clickCount = 0;

    /*
    This is the main logic of the playStage functions
    All of the playStage functions have same logic
    Every stage has it's own playStage function, for example stage 1 has playStage1 function, in the same way all of the other stages have their own playStage function
    In every playStage function, there is an onclick listener for every image, background.
    We do all of the logic parts in the onclick listeners.
    Only the same images are changed for every stage
    For example if on stage 1 image 1 and image 8 are same then it would be different for stage 2, stage 3, stage 3 etc.
    When game is started prev is null, next is null, prevView is null, nextView is null and clickCount = 0;
    When user clicks on an image (let's say the image is imageX) clickCount = 1, prev = animalImageX, next = null, prevView = imageX and nextView = null
    Then when second image is clicked, if second clicked image is not the same image clicked previously then
    prev = animalImageX, next = animalImageY, prevView = imageX, nextView = imageY and then we check if prev and next both contain same cartoon/animal
    if prev and next contain same image then SCORE++, and we check if the user has won or not, in case of stage 1 we check if (SCORE = 3), for stage 2 if (SCORE == 4)
    for stage 3 if (SCORE = 5) for stage 4 if (SCORE == 6) for stage 5 if(SCORE == 7) for stage 6 if (SCORE == 8), if the condition matches then we show
    the win status, if not, then we do prev = null, next = null, prevView = null, nextView = null, and then user has to select the images of their choice again.
    This continues until user completes the game
    If user wins, then we check if checkTim > 11, then we give 3 stars to user, if (checkTime > 0  && checkTime < 11) then we give 2 stars, and if (checkTime == 0 || checkTime < 0)
    then we give only 1 star.
    This continues for every stage.
    As playStage is same for all of the stages and the same conditions are executed, except just the same images sequence is changed for all of the stages
    so we don't need to comment same text above every onclick Listener.

    whenever two slides match we play a sound
    */

    // playStage1() is called in the end of loadImages1
    public void playStage1() {
        // stageImageCheck = 1 means currently stage 1 is being played
        stageImageCheck = 1;

        // startTimer is a function where the time for the current stage is tracked
        // startTimer will start the count down for the stage
        startTimer();

        // this is the code that will be executed when user will select image 1
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // clickCount = 2 means user has already selected two images
                // no need to allow user to select an image/card if
                // user has already selected two images.
                if (clickCount != 2 && pauseCheck == 0) {
                    //if user has selected 1 or no images
                    // show the image and hide the back of the image
                    hideViewByAnimation(image1);
                    showAnimalImage(animalImage1);
                    // if there is no image showed already (means all of the animal images are hidden)
                    if (prev == null) {
                        // set prev to animalimage1
                        // set preview to image1
                        prev = animalImage1;
                        prevView = image1;
                        // clickcount++ because user has selected an image
                        clickCount++;
                    } else {
                        // make sure the user is not selecting the same image selected previously
                        if (prev != animalImage1) {
                            // user has clicked second image
                            // next is now second image means user has already selected an image
                            next = animalImage1;
                            // nextview is image 1 means there is already a preview
                            nextView = image1;
                            // clickcount is 2 now, means 2 images are shown
                            clickCount++;
                            // as two images are shown, checking if both the images are same
                            if (prev == animalImage9) {
                                // this code will run if both the images are same
                                // the mobile will wait for 1.5 seconds before incrementing to the score, and before hiding the values
                                // meanwhile the click on the images will be blocked
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        // 1.5 seconds have passed now click count is again 0 and now user can select any two images
                                        clickCount = 0;
                                        bugCheckImage1 = 1;
                                        bugCheckImage9 = 1;

                                        // score is updated
                                        SCORE++;

                                        // hiding both the images now
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);

                                        // setting the temporary flags to default values
                                        prevView = null;
                                        prev = null;
                                        next = null;

                                        // checking if user has won or not
                                        checkWin();

                                        // if user has turned the volume on then the matching sound will be played.
                                        if (volumeCheck == 0) {
                                            if (volumeCheck == 0) {
                                                SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
                                                volumeVal = msharedPreferences1.getInt("volumeVal", 10);
                                                AudioManager mAudioManager = (AudioManager)MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
                                                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15 * volumeVal / 10, 0);
                                                matched.start();
                                            }
                                        }
                                    }
                                }.start();
                            } else {
                                // prev and next images were not same
                                // now wait 1.5 seconds before hiding the images.
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        // clickCount = 0 means now user can click any two images
                                        clickCount = 0;

                                        // hide animal images
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);

                                        // show the backside of the images
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);

                                        // set the temporary values to default so that they can be used with other clicks as well.
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image2);
                    showAnimalImage(animalImage2);
                    if (prev == null) {
                        prev = animalImage2;
                        prevView = image2;
                        clickCount++;
                    } else {
                        if (prev != animalImage2) {
                            next = animalImage2;
                            nextView = image2;
                            clickCount++;
                            if (prev == animalImage7) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        bugCheckImage2 = 1;
                                        bugCheckImage7 = 1;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
                                            volumeVal = msharedPreferences1.getInt("volumeVal", 10);
                                            AudioManager mAudioManager = (AudioManager)MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
                                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15 * volumeVal / 10, 0);
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
                                            volumeVal = msharedPreferences1.getInt("volumeVal", 10);
                                            AudioManager mAudioManager = (AudioManager)MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
                                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15 * volumeVal / 10, 0);
                                            matched.start();
                                        }
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image3);
                    showAnimalImage(animalImage3);
                    if (prev == null) {
                        prev = animalImage3;
                        prevView = image3;
                        clickCount++;
                    } else {
                        if (prev != animalImage3) {
                            next = animalImage3;
                            nextView = image3;
                            clickCount++;
                            if (prev == animalImage8) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        bugCheckImage3 = 1;
                                        bugCheckImage8 = 1;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image7);
                    showAnimalImage(animalImage7);
                    if (prev == null) {
                        prev = animalImage7;
                        prevView = image7;
                        clickCount++;
                    } else {
                        if (prev != animalImage7) {
                            next = animalImage7;
                            nextView = image7;
                            clickCount++;
                            if (prev == animalImage2) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        bugCheckImage7 = 1;
                                        bugCheckImage2 = 1;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image8);
                    showAnimalImage(animalImage8);
                    if (prev == null) {
                        prev = animalImage8;
                        prevView = image8;
                        clickCount++;
                    } else {
                        if (prev != animalImage8) {
                            next = animalImage8;
                            nextView = image8;
                            clickCount++;
                            if (prev == animalImage3) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        bugCheckImage8 = 1;
                                        bugCheckImage3 = 1;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image9);
                    showAnimalImage(animalImage9);
                    if (prev == null) {
                        prev = animalImage9;
                        prevView = image9;
                        clickCount++;
                    } else {
                        if (prev != animalImage9) {
                            next = animalImage9;
                            nextView = image9;
                            clickCount++;
                            if (prev == animalImage1) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        bugCheckImage1 = 1;
                                        bugCheckImage9 = 1;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });
    }

    public void playStage2() {
        stageImageCheck = 2;
        startTimer();
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image1);
                    showAnimalImage(animalImage1);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage1;
                        prevView = image1;
                    } else {
                        if (prev != animalImage1) {
                            clickCount++;
                            next = animalImage1;
                            nextView = image1;
                            if (prev == animalImage8) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image2);
                    showAnimalImage(animalImage2);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage2;
                        prevView = image2;
                    } else {
                        if (prev != animalImage2) {
                            clickCount++;
                            next = animalImage2;
                            nextView = image2;
                            if (prev == animalImage4) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image3);
                    showAnimalImage(animalImage3);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage3;
                        prevView = image3;
                    } else {
                        if (prev != animalImage3) {
                            clickCount++;
                            next = animalImage3;
                            nextView = image3;
                            if (prev == animalImage9) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image4);
                    showAnimalImage(animalImage4);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage4;
                        prevView = image4;
                    } else {
                        if (prev != animalImage4) {
                            clickCount++;
                            next = animalImage4;
                            nextView = image4;
                            if (prev == animalImage2) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image7);
                    showAnimalImage(animalImage7);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage7;
                        prevView = image7;
                    } else {
                        if (prev != animalImage7) {
                            clickCount++;
                            next = animalImage7;
                            nextView = image7;
                            if (prev == animalImage10) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image8);
                    showAnimalImage(animalImage8);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage8;
                        prevView = image8;
                    } else {
                        if (prev != animalImage8) {
                            clickCount++;
                            next = animalImage8;
                            nextView = image8;
                            if (prev == animalImage1) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image9);
                    showAnimalImage(animalImage9);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage9;
                        prevView = image9;
                    } else {
                        if (prev != animalImage9) {
                            clickCount++;
                            next = animalImage9;
                            nextView = image9;
                            if (prev == animalImage3) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image10);
                    showAnimalImage(animalImage10);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage10;
                        prevView = image10;
                    } else {
                        if (prev != animalImage10)
                            clickCount++;
                        {
                            next = animalImage10;
                            nextView = image10;
                            if (prev == animalImage7) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });
    }

    public void playStage3() {
        stageImageCheck = 3;
        startTimer();
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image1);
                    showAnimalImage(animalImage1);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage1;
                        prevView = image1;
                    } else {
                        if (prev != animalImage1) {
                            next = animalImage1;
                            nextView = image1;
                            clickCount++;
                            if (prev == animalImage4) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image2);
                    showAnimalImage(animalImage2);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage2;
                        prevView = image2;
                    } else {
                        if (prev != animalImage2) {
                            next = animalImage2;
                            nextView = image2;
                            clickCount++;
                            if (prev == animalImage9) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image3);
                    showAnimalImage(animalImage3);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage3;
                        prevView = image3;
                    } else {
                        if (prev != animalImage3) {
                            next = animalImage3;
                            nextView = image3;
                            clickCount++;
                            if (prev == animalImage11) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image4);
                    showAnimalImage(animalImage4);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage4;
                        prevView = image4;
                    } else {
                        if (prev != animalImage4) {
                            next = animalImage4;
                            nextView = image4;
                            clickCount++;
                            if (prev == animalImage1) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image5);
                    showAnimalImage(animalImage5);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage5;
                        prevView = image5;
                    } else {
                        if (prev != animalImage5) {
                            next = animalImage5;
                            nextView = image5;
                            clickCount++;
                            if (prev == animalImage8) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image7);
                    showAnimalImage(animalImage7);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage7;
                        prevView = image7;
                    } else {
                        if (prev != animalImage7) {
                            next = animalImage7;
                            nextView = image7;
                            clickCount++;
                            if (prev == animalImage10) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image8);
                    showAnimalImage(animalImage8);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage8;
                        prevView = image8;
                    } else {
                        if (prev != animalImage8) {
                            next = animalImage8;
                            nextView = image8;
                            clickCount++;
                            if (prev == animalImage5) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image9);
                    showAnimalImage(animalImage9);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage9;
                        prevView = image9;
                    } else {
                        if (prev != animalImage9) {
                            next = animalImage9;
                            nextView = image9;
                            clickCount++;
                            if (prev == animalImage2) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image10);
                    showAnimalImage(animalImage10);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage10;
                        prevView = image10;
                    } else {
                        if (prev != animalImage10) {
                            next = animalImage10;
                            nextView = image10;
                            clickCount++;
                            if (prev == animalImage7) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image11);
                    showAnimalImage(animalImage11);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage11;
                        prevView = image11;
                    } else {
                        if (prev != animalImage11) {
                            next = animalImage11;
                            nextView = image11;
                            clickCount++;
                            if (prev == animalImage3) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milli seconds is total time, 1000 milli seconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });
    }

    public void playStage4() {
        stageImageCheck = 4;
        startTimer();
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image1);
                    showAnimalImage(animalImage1);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage1;
                        prevView = image1;
                    } else {
                        if (prev != animalImage1) {
                            clickCount++;
                            next = animalImage1;
                            nextView = image1;
                            if (prev == animalImage9) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image2);
                    showAnimalImage(animalImage2);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage2;
                        prevView = image2;
                    } else {
                        if (prev != animalImage2) {
                            clickCount++;
                            next = animalImage2;
                            nextView = image2;
                            if (prev == animalImage13) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image3);
                    showAnimalImage(animalImage3);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage3;
                        prevView = image3;
                    } else {
                        if (prev != animalImage3) {
                            clickCount++;
                            next = animalImage3;
                            nextView = image3;
                            if (prev == animalImage16) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image4);
                    showAnimalImage(animalImage4);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage4;
                        prevView = image4;
                    } else {
                        if (prev != animalImage4) {
                            clickCount++;
                            next = animalImage4;
                            nextView = image4;
                            if (prev == animalImage14) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });


        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image7);
                    showAnimalImage(animalImage7);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage7;
                        prevView = image7;
                    } else {
                        if (prev != animalImage7) {
                            clickCount++;
                            next = animalImage7;
                            nextView = image7;
                            if (prev == animalImage10) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image8);
                    showAnimalImage(animalImage8);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage8;
                        prevView = image8;
                    } else {
                        if (prev != animalImage8) {
                            clickCount++;
                            next = animalImage8;
                            nextView = image8;
                            if (prev == animalImage15) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image9);
                    showAnimalImage(animalImage9);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage9;
                        prevView = image9;
                    } else {
                        if (prev != animalImage9) {
                            clickCount++;
                            next = animalImage9;
                            nextView = image9;
                            if (prev == animalImage1) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image10);
                    showAnimalImage(animalImage10);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage10;
                        prevView = image10;
                    } else {
                        if (prev != animalImage10) {
                            clickCount++;
                            next = animalImage10;
                            nextView = image10;
                            if (prev == animalImage7) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image13);
                    showAnimalImage(animalImage13);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage13;
                        prevView = image13;
                    } else {
                        if (prev != animalImage13) {
                            clickCount++;
                            next = animalImage13;
                            nextView = image13;
                            if (prev == animalImage2) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image14);
                    showAnimalImage(animalImage14);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage14;
                        prevView = image14;
                    } else {
                        if (prev != animalImage14) {
                            clickCount++;
                            next = animalImage14;
                            nextView = image14;
                            if (prev == animalImage4) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image15);
                    showAnimalImage(animalImage15);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage15;
                        prevView = image15;
                    } else {
                        if (prev != animalImage15) {
                            clickCount++;
                            next = animalImage15;
                            nextView = image15;
                            if (prev == animalImage8) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image16);
                    showAnimalImage(animalImage16);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage16;
                        prevView = image16;
                    } else {
                        if (prev != animalImage16) {
                            clickCount++;
                            next = animalImage16;
                            nextView = image16;
                            if (prev == animalImage3) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();

                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });
    }

    public void playStage5() {
        stageImageCheck = 5;
        startTimer();
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image1);
                    showAnimalImage(animalImage1);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage1;
                        prevView = image1;
                    } else {
                        if (prev != animalImage1) {
                            clickCount++;
                            next = animalImage1;
                            nextView = image1;
                            if (prev == animalImage14) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image2);
                    showAnimalImage(animalImage2);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage2;
                        prevView = image2;
                    } else {
                        if (prev != animalImage2) {
                            clickCount++;
                            next = animalImage2;
                            nextView = image2;
                            if (prev == animalImage11) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image3);
                    showAnimalImage(animalImage3);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage3;
                        prevView = image3;
                    } else {
                        if (prev != animalImage3) {
                            clickCount++;
                            next = animalImage3;
                            nextView = image3;
                            if (prev == animalImage16) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image4);
                    showAnimalImage(animalImage4);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage4;
                        prevView = image4;
                    } else {
                        if (prev != animalImage4) {
                            clickCount++;
                            next = animalImage4;
                            nextView = image4;
                            if (prev == animalImage13) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image5);
                    showAnimalImage(animalImage5);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage5;
                        prevView = image5;
                    } else {
                        if (prev != animalImage5) {
                            clickCount++;
                            next = animalImage5;
                            nextView = image5;
                            if (prev == animalImage9) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image7);
                    showAnimalImage(animalImage7);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage7;
                        prevView = image7;
                    } else {
                        if (prev != animalImage7) {
                            clickCount++;
                            next = animalImage7;
                            nextView = image7;
                            if (prev == animalImage10) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image8);
                    showAnimalImage(animalImage8);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage8;
                        prevView = image8;
                    } else {
                        if (prev != animalImage8) {
                            clickCount++;
                            next = animalImage8;
                            nextView = image8;
                            if (prev == animalImage15) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image9);
                    showAnimalImage(animalImage9);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage9;
                        prevView = image9;
                    } else {
                        if (prev != animalImage9) {
                            clickCount++;
                            next = animalImage9;
                            nextView = image9;
                            if (prev == animalImage5) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image10);
                    showAnimalImage(animalImage10);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage10;
                        prevView = image10;
                    } else {
                        if (prev != animalImage10) {
                            clickCount++;
                            next = animalImage10;
                            nextView = image10;
                            if (prev == animalImage7) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image11);
                    showAnimalImage(animalImage11);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage11;
                        prevView = image11;
                    } else {
                        if (prev != animalImage11) {
                            clickCount++;
                            next = animalImage11;
                            nextView = image11;
                            if (prev == animalImage2) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();

                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image13);
                    showAnimalImage(animalImage13);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage13;
                        prevView = image13;
                    } else {
                        if (prev != animalImage13) {
                            clickCount++;
                            next = animalImage13;
                            nextView = image13;
                            if (prev == animalImage4) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image14);
                    showAnimalImage(animalImage14);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage14;
                        prevView = image14;
                    } else {
                        if (prev != animalImage14) {
                            clickCount++;
                            next = animalImage14;
                            nextView = image14;
                            if (prev == animalImage1) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image15);
                    showAnimalImage(animalImage15);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage15;
                        prevView = image15;
                    } else {
                        if (prev != animalImage15) {
                            clickCount++;
                            next = animalImage15;
                            nextView = image15;
                            if (prev == animalImage8) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image16);
                    showAnimalImage(animalImage16);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage16;
                        prevView = image16;
                    } else {
                        if (prev != animalImage16) {
                            clickCount++;
                            next = animalImage16;
                            nextView = image16;
                            if (prev == animalImage3) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });
    }

    public void playStage6() {
        stageImageCheck = 6;
        startTimer();
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image1);
                    showAnimalImage(animalImage1);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage1;
                        prevView = image1;
                    } else {
                        if (prev != animalImage1) {
                            next = animalImage1;
                            nextView = image1;
                            if (prev == animalImage10) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image2);
                    showAnimalImage(animalImage2);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage2;
                        prevView = image2;
                    } else {
                        if (prev != animalImage2) {
                            clickCount++;
                            next = animalImage2;
                            nextView = image2;
                            if (prev == animalImage11) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image3);
                    showAnimalImage(animalImage3);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage3;
                        prevView = image3;
                    } else {
                        if (prev != animalImage3) {
                            clickCount++;
                            next = animalImage3;
                            nextView = image3;
                            if (prev == animalImage15) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image4);
                    showAnimalImage(animalImage4);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage4;
                        prevView = image4;
                    } else {
                        if (prev != animalImage4) {
                            clickCount++;
                            next = animalImage4;
                            nextView = image4;
                            if (prev == animalImage8) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image5);
                    showAnimalImage(animalImage5);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage5;
                        prevView = image5;
                    } else {
                        if (prev != animalImage5) {
                            clickCount++;
                            next = animalImage5;
                            nextView = image5;
                            if (prev == animalImage9) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image6);
                    showAnimalImage(animalImage6);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage6;
                        prevView = image6;
                    } else {
                        if (prev != animalImage6) {
                            clickCount++;
                            next = animalImage6;
                            nextView = image6;
                            if (prev == animalImage13) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image7);
                    showAnimalImage(animalImage7);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage7;
                        prevView = image7;
                    } else {
                        if (prev != animalImage7) {
                            clickCount++;
                            next = animalImage7;
                            nextView = image7;
                            if (prev == animalImage14) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image8);
                    showAnimalImage(animalImage8);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage8;
                        prevView = image8;
                    } else {
                        if (prev != animalImage8) {
                            clickCount++;
                            next = animalImage8;
                            nextView = image8;
                            if (prev == animalImage4) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image9);
                    showAnimalImage(animalImage9);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage9;
                        prevView = image9;
                    } else {
                        if (prev != animalImage9) {
                            clickCount++;
                            next = animalImage9;
                            nextView = image9;
                            if (prev == animalImage5) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image10);
                    showAnimalImage(animalImage10);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage10;
                        prevView = image10;
                    } else {
                        if (prev != animalImage10) {
                            clickCount++;
                            next = animalImage10;
                            nextView = image10;
                            if (prev == animalImage1) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image11);
                    showAnimalImage(animalImage11);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage11;
                        prevView = image11;
                    } else {
                        if (prev != animalImage11) {
                            clickCount++;
                            next = animalImage11;
                            nextView = image11;
                            if (prev == animalImage2) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image12);
                    showAnimalImage(animalImage12);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage12;
                        prevView = image12;
                    } else {
                        if (prev != animalImage12) {
                            clickCount++;
                            next = animalImage12;
                            nextView = image12;
                            if (prev == animalImage16) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image13);
                    showAnimalImage(animalImage13);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage13;
                        prevView = image13;
                    } else {
                        if (prev != animalImage13) {
                            clickCount++;
                            next = animalImage13;
                            nextView = image13;
                            if (prev == animalImage6) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image14);
                    showAnimalImage(animalImage14);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage14;
                        prevView = image14;
                    } else {
                        if (prev != animalImage14) {
                            clickCount++;
                            next = animalImage14;
                            nextView = image14;
                            if (prev == animalImage7) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image15);
                    showAnimalImage(animalImage15);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage15;
                        prevView = image15;
                    } else {
                        if (prev != animalImage15) {
                            clickCount++;
                            next = animalImage15;
                            nextView = image15;
                            if (prev == animalImage3) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        image16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCount != 2 && pauseCheck == 0) {
                    hideViewByAnimation(image16);
                    showAnimalImage(animalImage16);
                    if (prev == null) {
                        clickCount++;
                        prev = animalImage16;
                        prevView = image16;
                    } else {
                        if (prev != animalImage16) {
                            clickCount++;
                            next = animalImage16;
                            nextView = image16;
                            if (prev == animalImage12) {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        SCORE++;
                                        hideAnimalImage(next);
                                        hideAnimalImage(prev);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                        checkWin();
                                        if (volumeCheck == 0) {
                                            matched.start();
                                        }
                                    }
                                }.start();
                            } else {
                                new CountDownTimer(1500, 750) { //30000 milliseconds is total time, 1000 milliseconds is time interval
                                    public void onTick(long millisUntilFinished) {}
                                    public void onFinish() {
                                        clickCount = 0;
                                        hideAnimalImage(prev);
                                        hideAnimalImage(next);
                                        showViewByAnimation(nextView);
                                        showViewByAnimation(prevView);
                                        prevView = null;
                                        prev = null;
                                        next = null;
                                    }
                                }.start();
                            }
                        }
                    }
                }

            }
        });
    }

    //This is for showing the images and animate them
    public void showAnimalImage(ImageView view) {
        view.setBackgroundResource(R.drawable.imagebackground);
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0.0f);
        view.setRotationY(180);
        view.animate()
                .alpha(1.0f).rotationY(360)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                });
    }

    // hiding the images and animating them, same  as previously done in other hiding function
    public void hideAnimalImage(final View view) {
        if (view != null) {
            view.animate()
                    .alpha(0.0f).rotationY(180)
                    .setDuration(1000)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }

    // Assigning the images to the images views.
    // the images assigned will be different but the code is same.
    public void loadAnimalImagesForStage1() {
        // making the image placeholder invisible as by default it would be hidden
        // assigning the image view an image
        animalImage1.setVisibility(View.INVISIBLE);
        animalImage1.setImageResource(R.drawable.animal1);
        // making the image placeholder invisible as by default it would be hidden
        // assigning the image view an image
        animalImage9.setVisibility(View.INVISIBLE);
        animalImage9.setImageResource(R.drawable.animal1);

        // making the image placeholder invisible as by default it would be hidden
        // assigning the image view an image
        animalImage2.setVisibility(View.INVISIBLE);
        animalImage2.setImageResource(R.drawable.animal2);
        // making the image placeholder invisible as by default it would be hidden
        // assigning the image view an image
        animalImage7.setVisibility(View.INVISIBLE);
        animalImage7.setImageResource(R.drawable.animal2);

        // making the image placeholder invisible as by default it would be hidden
        // assigning the image view an image
        animalImage3.setVisibility(View.INVISIBLE);
        animalImage3.setImageResource(R.drawable.animal3);
        // making the image placeholder invisible as by default it would be hidden
        // assigning the image view an image
        animalImage8.setVisibility(View.INVISIBLE);
        animalImage8.setImageResource(R.drawable.animal3);

        // it is also same in all of the images
    }

    public void loadAnimalImagesForStage2() {
        animalImage1.setVisibility(View.INVISIBLE);
        animalImage2.setVisibility(View.INVISIBLE);
        animalImage3.setVisibility(View.INVISIBLE);
        animalImage4.setVisibility(View.INVISIBLE);
        animalImage7.setVisibility(View.INVISIBLE);
        animalImage8.setVisibility(View.INVISIBLE);
        animalImage9.setVisibility(View.INVISIBLE);
        animalImage10.setVisibility(View.INVISIBLE);

        animalImage1.setImageResource(R.drawable.animal4);
        animalImage2.setImageResource(R.drawable.animal5);
        animalImage3.setImageResource(R.drawable.animal7);
        animalImage4.setImageResource(R.drawable.animal5);
        animalImage7.setImageResource(R.drawable.animal6);
        animalImage8.setImageResource(R.drawable.animal4);
        animalImage9.setImageResource(R.drawable.animal7);
        animalImage10.setImageResource(R.drawable.animal6);
    }

    public void loadAnimalImagesForStage3() {
        animalImage1.setVisibility(View.INVISIBLE);
        animalImage2.setVisibility(View.INVISIBLE);
        animalImage3.setVisibility(View.INVISIBLE);
        animalImage4.setVisibility(View.INVISIBLE);
        animalImage5.setVisibility(View.INVISIBLE);
        animalImage7.setVisibility(View.INVISIBLE);
        animalImage8.setVisibility(View.INVISIBLE);
        animalImage9.setVisibility(View.INVISIBLE);
        animalImage10.setVisibility(View.INVISIBLE);
        animalImage11.setVisibility(View.INVISIBLE);

        animalImage1.setImageResource(R.drawable.animal8);
        animalImage2.setImageResource(R.drawable.animal1);
        animalImage3.setImageResource(R.drawable.animal3);
        animalImage4.setImageResource(R.drawable.animal8);
        animalImage5.setImageResource(R.drawable.animal5);
        animalImage7.setImageResource(R.drawable.animal7);
        animalImage8.setImageResource(R.drawable.animal5);
        animalImage9.setImageResource(R.drawable.animal1);
        animalImage10.setImageResource(R.drawable.animal7);
        animalImage11.setImageResource(R.drawable.animal3);
    }

    public void loadAnimalImagesForStage4() {
        animalImage1.setVisibility(View.INVISIBLE);
        animalImage2.setVisibility(View.INVISIBLE);
        animalImage3.setVisibility(View.INVISIBLE);
        animalImage4.setVisibility(View.INVISIBLE);
        animalImage7.setVisibility(View.INVISIBLE);
        animalImage8.setVisibility(View.INVISIBLE);
        animalImage9.setVisibility(View.INVISIBLE);
        animalImage10.setVisibility(View.INVISIBLE);
        animalImage13.setVisibility(View.INVISIBLE);
        animalImage14.setVisibility(View.INVISIBLE);
        animalImage15.setVisibility(View.INVISIBLE);
        animalImage16.setVisibility(View.INVISIBLE);

        animalImage1.setImageResource(R.drawable.animal2);
        animalImage2.setImageResource(R.drawable.animal4);
        animalImage3.setImageResource(R.drawable.animal6);
        animalImage4.setImageResource(R.drawable.animal8);
        animalImage7.setImageResource(R.drawable.animal1);
        animalImage8.setImageResource(R.drawable.animal5);
        animalImage9.setImageResource(R.drawable.animal2);
        animalImage10.setImageResource(R.drawable.animal1);
        animalImage13.setImageResource(R.drawable.animal4);
        animalImage14.setImageResource(R.drawable.animal8);
        animalImage15.setImageResource(R.drawable.animal5);
        animalImage16.setImageResource(R.drawable.animal6);
    }

    public void loadAnimalImagesForStage5() {
        animalImage1.setVisibility(View.INVISIBLE);
        animalImage2.setVisibility(View.INVISIBLE);
        animalImage3.setVisibility(View.INVISIBLE);
        animalImage4.setVisibility(View.INVISIBLE);
        animalImage5.setVisibility(View.INVISIBLE);
        animalImage7.setVisibility(View.INVISIBLE);
        animalImage8.setVisibility(View.INVISIBLE);
        animalImage9.setVisibility(View.INVISIBLE);
        animalImage10.setVisibility(View.INVISIBLE);
        animalImage11.setVisibility(View.INVISIBLE);
        animalImage13.setVisibility(View.INVISIBLE);
        animalImage14.setVisibility(View.INVISIBLE);
        animalImage15.setVisibility(View.INVISIBLE);
        animalImage16.setVisibility(View.INVISIBLE);

        animalImage1.setImageResource(R.drawable.animal7);
        animalImage2.setImageResource(R.drawable.animal4);
        animalImage3.setImageResource(R.drawable.animal2);
        animalImage4.setImageResource(R.drawable.animal3);
        animalImage5.setImageResource(R.drawable.animal1);
        animalImage7.setImageResource(R.drawable.animal5);
        animalImage8.setImageResource(R.drawable.animal6);
        animalImage9.setImageResource(R.drawable.animal1);
        animalImage10.setImageResource(R.drawable.animal5);
        animalImage11.setImageResource(R.drawable.animal4);
        animalImage13.setImageResource(R.drawable.animal3);
        animalImage14.setImageResource(R.drawable.animal7);
        animalImage15.setImageResource(R.drawable.animal6);
        animalImage16.setImageResource(R.drawable.animal2);
    }

    public void loadAnimalImagesForStage6() {
        animalImage1.setVisibility(View.INVISIBLE);
        animalImage2.setVisibility(View.INVISIBLE);
        animalImage3.setVisibility(View.INVISIBLE);
        animalImage4.setVisibility(View.INVISIBLE);
        animalImage5.setVisibility(View.INVISIBLE);
        animalImage6.setVisibility(View.INVISIBLE);
        animalImage7.setVisibility(View.INVISIBLE);
        animalImage8.setVisibility(View.INVISIBLE);
        animalImage9.setVisibility(View.INVISIBLE);
        animalImage10.setVisibility(View.INVISIBLE);
        animalImage11.setVisibility(View.INVISIBLE);
        animalImage12.setVisibility(View.INVISIBLE);
        animalImage13.setVisibility(View.INVISIBLE);
        animalImage14.setVisibility(View.INVISIBLE);
        animalImage15.setVisibility(View.INVISIBLE);
        animalImage16.setVisibility(View.INVISIBLE);

        animalImage1.setImageResource(R.drawable.animal5);
        animalImage2.setImageResource(R.drawable.animal2);
        animalImage3.setImageResource(R.drawable.animal1);
        animalImage4.setImageResource(R.drawable.animal6);
        animalImage5.setImageResource(R.drawable.animal4);
        animalImage6.setImageResource(R.drawable.animal3);
        animalImage7.setImageResource(R.drawable.animal7);
        animalImage8.setImageResource(R.drawable.animal6);
        animalImage9.setImageResource(R.drawable.animal4);
        animalImage10.setImageResource(R.drawable.animal5);
        animalImage11.setImageResource(R.drawable.animal2);
        animalImage12.setImageResource(R.drawable.animal8);
        animalImage13.setImageResource(R.drawable.animal3);
        animalImage14.setImageResource(R.drawable.animal7);
        animalImage15.setImageResource(R.drawable.animal1);
        animalImage16.setImageResource(R.drawable.animal8);
    }

    // time is temporary variable used to hold values of time at different places
    int time = 0;

    //checktime is used to track time of the game
    int checktime = 0;

    // countdowntimer is the timer object that runs the timer, it could be stoped paused or cancelled
    CountDownTimer countDownTimer;

    public void startTimer() {
        switch (stageImageCheck) {
            // give 61 seconds for stage 1
            case 1:
                time = 61;
                break;
            // give 91 seconds for stage 2
            case 2:
                time = 91;
                break;
            // give 121 seconds for stage 3
            case 3:
                time = 121;
                break;
            // give 151 seconds for stage 4
            case 4:
                time = 151;
                break;
            // give 181 seconds for stage 5
            case 5:
                time = 181;
                break;
            // give 211 seconds for stage 6
            case 6:
                time = 211;
                break;
            default:
                time = 61;
                break;
        }

        //giving an hour to timer so the game keeps running even after the time for the game has passed
        // and assigning the stage time to checktime
        checktime = time;
        time = 36000 * 1000;

        //starting a timer with infinite time
        countDownTimer = new CountDownTimer(time, 1000) { //30000 milliseconds is total time, 1000 milliseconds is time interval
            public void onTick(long millisUntilFinished) {
                // if user has paused the game don't count time, and don't update time in textview
                if (pauseCheck == 0) {
                    checktime--;
                    String temp = String.valueOf(checktime);
                    timer.setText(temp);
                }
            }

            public void onFinish() {
                //if the time assigned to timer is passed, just restart the timer again
                countDownTimer.start();
            }
        };
        countDownTimer.start();
    }

    /*
    This is how we determine if user has won or not
    In stage 1 we have 3 images
    In stage 2 we have 4 images
    In stage 3 we have 5 images
    In stage 4 we have 6 images
    In stage 5 we have 7 images
    In stage 6 we have 8 images
    So we first check for
    stageImageCheck if it is 1, stage is 1
    stageImageCheck if it is 2, stage is 2
    stageImageCheck if it is 3, stage is 3
    stageImageCheck if it is 4, stage is 4
    stageImageCheck if it is 5, stage is 5
    stageImageCheck if it is 6, stage is 6
    and then we check for scores
    if stage 1 has 3 score
    if stage 2 has 4 score
    if stage 3 has 5 score
    if stage 4 has 6 score
    if stage 5 has 7 score
    if stage 6 has 8 score
    then we declare that user has won the game
    And then we assign stars to the user by checking the checktime
    if checktime is > 11 user gets 3 stars
    if checktime is < 11 && checktime > 0 user gets 2 stars
    if checktime == 0 || checktime < 0 user gets 1 star
    And then before updating in leader board we check if the current stars are greater than the previously stored stars
    */

    // this function will keep track if user has won or not
    public void checkWin() {
        // shared preferences to check what is the previous highest score
        SharedPreferences msharedPreferences = getSharedPreferences("stars", MODE_PRIVATE);

        // these variables will contain highest scores for each previous stage
        level1Stars = msharedPreferences.getInt("stage1star", 0);
        level2Stars = msharedPreferences.getInt("stage2star", 0);
        level3Stars = msharedPreferences.getInt("stage3star", 0);
        level4Stars = msharedPreferences.getInt("stage4star", 0);
        level5Stars = msharedPreferences.getInt("stage5star", 0);
        level6Stars = msharedPreferences.getInt("stage6star", 0);
        SharedPreferences sharedPreferences = getSharedPreferences("stars", MODE_PRIVATE);

        switch (stageImageCheck) {
            case 1:
                // if user has matched all of the images
                if (SCORE == 3) {
                    countDownTimer.cancel();
                    // show user won dialog/textview
                    userWon.setVisibility(View.VISIBLE);
                    // if user's remaining time is greater than 11, give them 3 stars
                    if (checktime >= 11) {
                        STARS = 3;
                        //Updating the stars values in sharedpreferences
                        SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                        mSharedEditor.putInt("stage1star", 3);
                        mSharedEditor.commit();
                        // User has won 3 stars, make 3 stars visible
                        winStar1.setVisibility(View.VISIBLE);
                        winStar2.setVisibility(View.VISIBLE);
                        winStar3.setVisibility(View.VISIBLE);
                        // Logging the firebase analytics event
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.SCORE, "3_STARS");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 1 High Score");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                    } else {
                        if (checktime < 11 && checktime > 0) {
                            STARS = 2;
                            if (STARS >= level1Stars) {
                                //Updating the stars values in sharedpreferences
                                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                                mSharedEditor.putInt("stage1star", 2);
                                mSharedEditor.commit();
                                winStar1.setVisibility(View.VISIBLE);
                                winStar2.setVisibility(View.VISIBLE);
                                // Logging the firebase analytics event
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.SCORE, "2_STARS");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 1 High Score");
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                            }
                        } else {
                            STARS = 1;
                            if (STARS >= level1Stars) {
                                //Updating the stars values in sharedpreferences
                                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                                mSharedEditor.putInt("stage1star", 1);
                                mSharedEditor.commit();
                                winStar2.setVisibility(View.VISIBLE);
                                // Logging the firebase analytics event
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.SCORE, "1_STAR");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 1 High Score");
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                            }
                        }
                    }
                }
                break;
            case 2:
                // if user has matched all of the images
                if (SCORE == 4) {
                    countDownTimer.cancel();
                    // show user won dialog/textview
                    userWon.setVisibility(View.VISIBLE);

                    // if user's remaining time is greater than 11, give them 3 stars
                    if (checktime >= 11) {
                        STARS = 3;
                        //Updating the stars values in sharedpreferences
                        SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                        mSharedEditor.putInt("stage2star", 3);
                        mSharedEditor.commit();
                        // User has won 3 stars, make 3 stars visible
                        winStar1.setVisibility(View.VISIBLE);
                        winStar2.setVisibility(View.VISIBLE);
                        winStar3.setVisibility(View.VISIBLE);
                        // Logging the firebase analytics event
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.SCORE, "3_STAR");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 2 High Score");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                    } else {
                        if (checktime < 11 && checktime > 0) {
                            STARS = 2;
                            if (STARS >= level2Stars) {
                                //Updating the stars values in sharedpreferences
                                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                                mSharedEditor.putInt("stage2star", 2);
                                mSharedEditor.commit();
                                winStar1.setVisibility(View.VISIBLE);
                                winStar2.setVisibility(View.VISIBLE);
                                // Logging the firebase analytics event
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.SCORE, "2_STAR");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 2 High Score");
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                            }
                        } else {
                            STARS = 1;
                            if (STARS >= level2Stars) {
                                //Updating the stars values in sharedpreferences
                                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                                mSharedEditor.putInt("stage2star", 1);
                                mSharedEditor.commit();
                                winStar2.setVisibility(View.VISIBLE);
                                // Logging the firebase analytics event
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.SCORE, "1_STAR");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 2 High Score");
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                            }
                        }
                    }
                }
                break;
            case 3:
                // if user has matched all of the images
                if (SCORE == 5) {
                    countDownTimer.cancel();
                    // show user won dialog/textview
                    userWon.setVisibility(View.VISIBLE);
                    // if user's remaining time is greater than 11, give them 3 stars
                    if (checktime >= 11) {
                        STARS = 3;
                        //Updating the stars values in sharedpreferences
                        SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                        mSharedEditor.putInt("stage3star", 3);
                        mSharedEditor.commit();
                        // User has won 3 stars, make 3 stars visible
                        winStar1.setVisibility(View.VISIBLE);
                        winStar2.setVisibility(View.VISIBLE);
                        winStar3.setVisibility(View.VISIBLE);
                        // Logging the firebase analytics event
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.SCORE, "3_STAR");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 3 High Score");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                    } else {
                        if (checktime < 11 && checktime > 0) {
                            STARS = 2;
                            if (STARS >= level3Stars) {
                                //Updating the stars values in sharedpreferences
                                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                                mSharedEditor.putInt("stage3star", 2);
                                mSharedEditor.commit();
                                winStar1.setVisibility(View.VISIBLE);
                                winStar2.setVisibility(View.VISIBLE);
                                // Logging the firebase analytics event
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.SCORE, "2_STAR");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 3 High Score");
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                            }
                        } else {
                            STARS = 1;
                            if (STARS >= level3Stars) {
                                //Updating the stars values in sharedpreferences
                                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                                mSharedEditor.putInt("stage3star", 1);
                                mSharedEditor.commit();
                                winStar2.setVisibility(View.VISIBLE);
                                // Logging the firebase analytics event
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.SCORE, "1_STAR");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 3 High Score");
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                            }
                        }
                    }
                }
                break;
            case 4:
                // if user has matched all of the images
                if (SCORE == 6) {
                    countDownTimer.cancel();
                    // show user won dialog/textview
                    userWon.setVisibility(View.VISIBLE);
                    // if user's remaining time is greater than 11, give them 3 stars
                    if (checktime >= 11) {
                        STARS = 3;
                        //Updating the stars values in sharedpreferences
                        SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                        mSharedEditor.putInt("stage4star", STARS);
                        mSharedEditor.commit();
                        // User has won 3 stars, make 3 stars visible
                        winStar1.setVisibility(View.VISIBLE);
                        winStar2.setVisibility(View.VISIBLE);
                        winStar3.setVisibility(View.VISIBLE);
                        // Logging the firebase analytics event
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.SCORE, "3_STAR");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 4 High Score");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                    } else {
                        if (checktime < 11 && checktime > 0) {
                            STARS = 2;
                            if (STARS >= level4Stars) {
                                //Updating the stars values in sharedpreferences
                                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                                mSharedEditor.putInt("stage4star", STARS);
                                mSharedEditor.commit();
                                winStar1.setVisibility(View.VISIBLE);
                                winStar2.setVisibility(View.VISIBLE);
                                // Logging the firebase analytics event
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.SCORE, "2_STAR");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 4 High Score");
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                            }
                        } else {
                            STARS = 1;
                            if (STARS >= level4Stars) {
                                //Updating the stars values in sharedpreferences
                                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                                mSharedEditor.putInt("stage4star", STARS);
                                mSharedEditor.commit();
                                winStar2.setVisibility(View.VISIBLE);
                                // Logging the firebase analytics event
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.SCORE, "1_STAR");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 4 High Score");
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                            }
                        }
                    }
                }
                break;
            case 5:
                // if user has matched all of the images
                if (SCORE == 7) {
                    countDownTimer.cancel();
                    // show user won dialog/textview
                    userWon.setVisibility(View.VISIBLE);
                    // if user's remaining time is greater than 11, give them 3 stars
                    if (checktime >= 11) {
                        STARS = 3;
                        //Updating the stars values in sharedpreferences
                        SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                        mSharedEditor.putInt("stage5star", STARS);
                        mSharedEditor.commit();
                        // User has won 3 stars, make 3 stars visible
                        winStar1.setVisibility(View.VISIBLE);
                        winStar2.setVisibility(View.VISIBLE);
                        winStar3.setVisibility(View.VISIBLE);
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.SCORE, "3_STAR");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 5 High Score");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                    } else {
                        if (checktime < 11 && checktime > 0) {
                            STARS = 2;
                            if (STARS >= level5Stars) {
                                //Updating the stars values in sharedpreferences
                                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                                mSharedEditor.putInt("stage5star", STARS);
                                mSharedEditor.commit();
                                winStar1.setVisibility(View.VISIBLE);
                                winStar2.setVisibility(View.VISIBLE);
                                // Logging the firebase analytics event
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.SCORE, "2_STAR");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 5 High Score");
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                            }
                        } else {
                            STARS = 1;
                            if (STARS >= level5Stars) {
                                //Updating the stars values in sharedpreferences
                                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                                mSharedEditor.putInt("stage5star", STARS);
                                mSharedEditor.commit();
                                winStar2.setVisibility(View.VISIBLE);
                                // Logging the firebase analytics event
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.SCORE, "1_STAR");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 5 High Score");
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                            }
                        }
                    }
                }
                break;
            case 6:
                // if user has matched all of the images
                if (SCORE == 8) {
                    countDownTimer.cancel();
                    // show user won dialog/textview
                    userWon.setVisibility(View.VISIBLE);
                    // if user's remaining time is greater than 11, give them 3 stars
                    if (checktime >= 11) {
                        STARS = 3;
                        //Updating the stars values in sharedpreferences
                        SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                        mSharedEditor.putInt("stage6star", STARS);
                        mSharedEditor.commit();
                        // User has won 3 stars, make 3 stars visible
                        winStar1.setVisibility(View.VISIBLE);
                        winStar2.setVisibility(View.VISIBLE);
                        winStar3.setVisibility(View.VISIBLE);
                        // Logging the firebase analytics event
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.SCORE, "3_STAR");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 6 High Score");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                    } else {
                        if (checktime < 11 && checktime > 0) {
                            STARS = 2;
                            if (STARS >= level6Stars) {
                                //Updating the stars values in sharedpreferences
                                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                                mSharedEditor.putInt("stage6star", STARS);
                                mSharedEditor.commit();
                                winStar1.setVisibility(View.VISIBLE);
                                winStar2.setVisibility(View.VISIBLE);
                                // Logging the firebase analytics event
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.SCORE, "2_STAR");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 6 High Score");
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                            }
                        } else {
                            STARS = 1;
                            if (STARS >= level6Stars) {
                                //Updating the stars values in sharedpreferences
                                SharedPreferences.Editor mSharedEditor = sharedPreferences.edit();
                                mSharedEditor.putInt("stage6star", STARS);
                                mSharedEditor.commit();
                                winStar2.setVisibility(View.VISIBLE);
                                // Logging the firebase analytics event
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.SCORE, "1_STAR");
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Level 6 High Score");
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
                            }
                        }
                    }
                }
                break;
        }
    }

    // this will hide the stage area
    // stage name and stars are automatically hidden if background is hidden because their constraints are aligned with constraints of backgrounds
    // so hiding the backgrounds only will do the work
    public void hideStageArea() {
        // hiding the backgrounds
        beginnerBackground.setVisibility(View.GONE);
        easyBackground.setVisibility(View.GONE);
        mediumBackground.setVisibility(View.GONE);
        hardBackground.setVisibility(View.GONE);
        hardestBackground.setVisibility(View.GONE);
        masterBackground.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        // when game is in background pause the time, time should not run when game is put in background
        pauseCheck = 1;
        super.onPause();
        bgm.pause();
    }

    @Override
    protected void onStart() {
        pauseCheck = 0;
        // check if user has paused the background music then keep it paused, if not resume the music.
        SharedPreferences msharedPreferences = getSharedPreferences("volumeCheck", MODE_PRIVATE);
        volumeCheck = msharedPreferences.getInt("volume", 0);

        if (volumeCheck == 0) {
            SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
            volumeVal = msharedPreferences1.getInt("volumeVal", 10);
            AudioManager mAudioManager = (AudioManager)MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15 * volumeVal / 10, 0);
            bgm.start();
            volumeIcon.setImageResource(R.drawable.ic_settings);
        } else {
            volumeIcon.setImageResource(R.drawable.ic_settings);
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        // game is brought to front again so resume the timers
        pauseCheck = 0;

        // check if user has paused the background music then keep it paused, if not resume the music.
        SharedPreferences msharedPreferences = getSharedPreferences("volumeCheck", MODE_PRIVATE);
        volumeCheck = msharedPreferences.getInt("volume", 0);

        if (volumeCheck == 0) {
            SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
            volumeVal = msharedPreferences1.getInt("volumeVal", 10);

            AudioManager mAudioManager = (AudioManager)MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15 * volumeVal / 10, 0);
            bgm.start();
            volumeIcon.setImageResource(R.drawable.ic_settings);
        } else {
            volumeIcon.setImageResource(R.drawable.ic_settings);
        }
        super.onResume();
    }

    @Override
    protected void onRestart() {
        pauseCheck = 0;
        // check if user has paused the background music then keep it paused, if not resume the music.
        SharedPreferences msharedPreferences = getSharedPreferences("volumeCheck", MODE_PRIVATE);
        volumeCheck = msharedPreferences.getInt("volume", 0);

        if (volumeCheck == 0) {
            SharedPreferences msharedPreferences1 = getSharedPreferences("volumeValue", MODE_PRIVATE);
            volumeVal = msharedPreferences1.getInt("volumeVal", 10);
            AudioManager mAudioManager = (AudioManager)MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15 * volumeVal / 10, 0);
            bgm.start();
            volumeIcon.setImageResource(R.drawable.ic_settings);
        } else {
            volumeIcon.setImageResource(R.drawable.ic_settings);
        }
        super.onRestart();
    }

    // when game user presses the back button this function will be executed.
    // this function has 3 checks
    // when state = 0, state = 1, state = 2
    // when state = 2 and back button is pressed, state = 1 and stages area is loaded
    // when stage = 1 and back button is pressed state = 0 and main menu is loaded
    // when stage = 0 and user presses back button, game is exit
    @Override
    public void onBackPressed() {
        if (state == 0) {
            super.onBackPressed();
            // simply exit the app
        } else {
            if (state == 1) {
                // go back to main menu
                showViewByAnimation(playButton);
                //show the about button
                about.setVisibility(View.VISIBLE);
                // show the tutorial button
                tutorial.setVisibility(View.VISIBLE);
                // go to main menu
                goToOne();
                // show the settings button (share button)
                settingsButton.setVisibility(View.VISIBLE);
                // make the sound button visible
                soundButton.setVisibility(View.VISIBLE);
            } else {
                if (state == 3) {
                    // user is playing game and has pressed the back button
                    // hide the pause button
                    // set pausecheck to default
                    // set checktime to default
                    // hide winstar1, winstar2, winstar3
                    // make score = 0
                    // make stars = 0
                    // make prev = null
                    // make next = null
                    // make prevView = null
                    // make nextView = null
                    // hide the timer view
                    pauseButton.setVisibility(View.GONE);
                    pauseCheck = 0;
                    checktime = 0;
                    winStar1.setVisibility(View.GONE);
                    winStar2.setVisibility(View.GONE);
                    winStar3.setVisibility(View.GONE);
                    userWon.setVisibility(View.GONE);
                    SCORE = 0;
                    STARS = 0;
                    prev = null;
                    next = null;
                    prevView = null;
                    nextView = null;
                    // hide the stage
                    hideStage();
                    // show the stages area
                    one();
                    // hide the timer
                    timer.setVisibility(View.GONE);
                    // show the settings button
                    settingsButton.setVisibility(View.VISIBLE);
                    // show the sound button
                    soundButton.setVisibility(View.VISIBLE);
                    // hide the playbutton
                    playButton.setVisibility(View.INVISIBLE);
                    // hide the about button
                    about.setVisibility(View.GONE);
                    // hide the tutorial button
                    tutorial.setVisibility(View.GONE);
                    // hide the pause button
                    // hide the back button
                    pauseButton.setText("Pause Game");
                    backButton.setVisibility(View.GONE);
                }
            }
        }
    }
    int bugCheckImage1 = 0, bugCheckImage2 = 0, bugCheckImage3 = 0, bugCheckImage4 = 0, bugCheckImage5 = 0, bugCheckImage6 = 0, bugCheckImage7 = 0, bugCheckImage8 = 0, bugCheckImage9 = 0, bugCheckImage10 = 0, bugCheckImage11 = 0, bugCheckImage12 = 0, bugCheckImage13 = 0, bugCheckImage14 = 0, bugCheckImage15 = 0, bugCheckImage16 = 0;
}

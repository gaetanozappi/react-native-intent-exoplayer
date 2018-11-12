package com.zappi.ui.exoplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.regex.Pattern;

public class SimpleVideoStream extends AppCompatActivity implements ExoPlayer.EventListener {
    public static final int PERMISSIONS_REQUEST_CODE = 0;

    SimpleExoPlayerView playerView;
    SimpleExoPlayer player;
    DataSource.Factory dataSourceFactory;
    MediaSource videoSource;
    BottomSheetMenuDialog menu, subtitle, time;

    TextView video_title;

    String title, url, sub;
    Boolean showTitle = true, showSub = false;
    int timeId = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle b = getIntent().getExtras();
        title = b.getString("title", "");
        url = b.getString("url", "");
        sub = b.getString("sub", "");

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        playerView = (SimpleExoPlayerView) findViewById(R.id.exo_player_view);
        video_title = (TextView) findViewById(R.id.video_title);
        video_title.setText(title);

        if (title.equals("")) {
            showTitle = false;
            video_title.setVisibility(View.INVISIBLE);
        }

        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        playerView.setRewindIncrementMs(5 * 1000);
        playerView.setFastForwardIncrementMs(5 * 1000);

        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayer"));

        // Produces Extractor instances for parsing the media data.
        final ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        videoSource = new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);

        // Prepare the player with the source.
        player.addListener(this);
        player.prepare(videoSource);
        playerView.requestFocus();
        player.setPlayWhenReady(true);// to play video when ready. Use false to pause a video

        if (!sub.equals("") && b.getBoolean("subShow")) addSub(sub);

        final ImageView option = (ImageView) findViewById(R.id.exo_option);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false); //to pause a video because now our video player is not in focus
        }
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                //You can use progress dialog to show user that video is preparing or buffering so please wait
                break;
            case ExoPlayer.STATE_IDLE:
                //idle state
                break;
            case ExoPlayer.STATE_READY:
                // dismiss your dialog here because our video is ready to play now
                break;
            case ExoPlayer.STATE_ENDED:
                // do your processing after ending of video
                break;
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        // show user that something went wrong. I am showing dialog but you can use your way
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Could not able to stream video");
        adb.setMessage("It seems that something is going wrong.\nPlease try again.");
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish(); // take out user from this activity. you can skip this
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void Menu() {
        String sub = showSub ? "Show" : "Hide";
        String time = String.valueOf(timeId * 5);
        String titleCheck = showTitle ? "Show" : "Hide";
        if (!title.equals("")) {
            menu = new BottomSheetBuilder(this)
                    .setMode(BottomSheetBuilder.MODE_LIST)
                    .setItemTextColor(Color.parseColor("#000000"))
                    .setBackgroundColor(Color.parseColor("#ffffff"))
                    .addItem(0, "Network flow", R.drawable.ic_info_outline_24dp)
                    .addItem(1, "Subtitle • " + sub, R.drawable.ic_subtitles_24dp)
                    .addItem(2, "Change time • " + time, R.drawable.ic_timelapse_24dp)
                    .addItem(3, "Title • " + titleCheck, R.drawable.ic_title_24dp)
                    .expandOnStart(true)
                    .setItemClickListener(new BottomSheetItemClickListener() {
                        @Override
                        public void onBottomSheetItemClick(MenuItem item) {
                            if (item.getItemId() == 0) {
                                NetworkFlow();
                            } else if (item.getItemId() == 1) {
                                Subtitle();
                            } else if (item.getItemId() == 2) {
                                Time();
                            } else if (item.getItemId() == 3) {
                                if (!title.equals("")) {
                                    showTitle = !showTitle;
                                    video_title.setVisibility((video_title.getVisibility() == View.VISIBLE) ? View.INVISIBLE : View.VISIBLE);
                                }
                            }
                        }
                    }).createDialog();
        } else {
            menu = new BottomSheetBuilder(this)
                    .setMode(BottomSheetBuilder.MODE_LIST)
                    .setItemTextColor(Color.parseColor("#000000"))
                    .setBackgroundColor(Color.parseColor("#ffffff"))
                    .addItem(0, "Network flow", R.drawable.ic_info_outline_24dp)
                    .addItem(1, "Subtitle • " + sub, R.drawable.ic_subtitles_24dp)
                    .addItem(2, "Change time • " + time, R.drawable.ic_timelapse_24dp)
                    .expandOnStart(true)
                    .setItemClickListener(new BottomSheetItemClickListener() {
                        @Override
                        public void onBottomSheetItemClick(MenuItem item) {
                            if (item.getItemId() == 0) {
                                NetworkFlow();
                            } else if (item.getItemId() == 1) {
                                Subtitle();
                            } else if (item.getItemId() == 2) {
                                Time();
                            }
                        }
                    }).createDialog();
        }
        menu.show();
    }

    public void Subtitle() {
        subtitle = new BottomSheetBuilder(this)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setItemTextColor(Color.parseColor("#000000"))
                .setBackgroundColor(Color.parseColor("#ffffff"))
                .addItem(0, "Add Url", null)
                .addItem(1, "Add file", null)
                .addItem(2, "Subtitle Size, Font and Color", null)
                .addItem(3, "Remove subtitle", null)
                .expandOnStart(true)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        if (item.getItemId() == 0) DialogSubtitle();
                        if (item.getItemId() == 1) {
                            checkPermissionsAndOpenFilePicker();
                        }
                        if (item.getItemId() == 2) {
                            Intent intent = new Intent(Settings.ACTION_CAPTIONING_SETTINGS);
                            startActivity(intent);
                        }
                        if (item.getItemId() == 3) removeSub();
                    }
                }).createDialog();
        subtitle.show();
    }

    public void DialogSubtitle() {
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.custom_alertdialog, null);
        final EditText url_ele = (EditText) layout.findViewById(R.id.url);
        url_ele.setText(sub);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Subtitle");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String subUrl = url_ele.getText().toString();
                        if (!subUrl.equals("") && isValidURL(subUrl)) {
                            addSub(subUrl);
                        }
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setView(layout);
        alert.show();
    }

    public void Time() {
        Drawable[] array = new Drawable[13];
        for (int i = 0; i < 13; i++)
            array[i] = timeId == i ? getResources().getDrawable(R.drawable.ic_check_24dp) : null;
        time = new BottomSheetBuilder(this)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setItemTextColor(Color.parseColor("#000000"))
                .setBackgroundColor(Color.parseColor("#ffffff"))
                .addItem(0, "0", array[0])
                .addItem(1, "5", array[1])
                .addItem(2, "10", array[2])
                .addItem(3, "15", array[3])
                .addItem(4, "20", array[4])
                .addItem(5, "25", array[5])
                .addItem(6, "30", array[6])
                .addItem(7, "35", array[7])
                .addItem(8, "40", array[8])
                .addItem(9, "45", array[9])
                .addItem(10, "50", array[10])
                .addItem(11, "55", array[11])
                .addItem(12, "60", array[12])
                .expandOnStart(true)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        Log.v("ok", " click3-" + item.getTitle());
                        timeId = item.getItemId();
                        int t = Integer.parseInt(String.valueOf(item.getTitle()));
                        playerView.setRewindIncrementMs(t * 1000);
                        playerView.setFastForwardIncrementMs(t * 1000);
                    }
                }).createDialog();
        time.show();
    }

    public void NetworkFlow() {
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.custom_alertdialog, null);
        final EditText url_ele = (EditText) layout.findViewById(R.id.url);
        url_ele.setText(url);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Network flow");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String newUrl = url_ele.getText().toString();
                        if (!newUrl.equals("") && isValidURL(newUrl)) {
                            final ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                            videoSource = new ExtractorMediaSource(Uri.parse(newUrl), dataSourceFactory, extractorsFactory, null, null);
                            player.addListener(SimpleVideoStream.this);
                            player.prepare(videoSource);
                            playerView.requestFocus();
                            player.setPlayWhenReady(true);
                        }
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setView(layout);
        alert.show();
    }

    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showError();
            } else {*/
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            //}
        } else {
            openFilePicker();
        }
    }

    private void showError() {
        Toast.makeText(this, "Allow external storage reading.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFilePicker();
                } else {
                    showError();
                }
            }
        }
    }

    private void openFilePicker() {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(PERMISSIONS_REQUEST_CODE)
                .withFilter(Pattern.compile(".*\\.srt$"))
                .withHiddenFiles(true)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSIONS_REQUEST_CODE && resultCode == RESULT_OK) {
            String sub = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            addSub(sub);
        }
    }

    private void removeSub() {
        showSub = false;
        final ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        videoSource = new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
        player.prepare(videoSource, false, false);
    }

    private void addSub(String sub) {
        removeSub();
        showSub = true;
        Format textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, null, Format.NO_VALUE, Format.NO_VALUE, "en", null, Format.OFFSET_SAMPLE_RELATIVE);
        MediaSource textMediaSource = new SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(sub), textFormat, C.TIME_UNSET);
        videoSource = new MergingMediaSource(videoSource, textMediaSource);
        player.prepare(videoSource, false, false);
    }

    boolean isValidURL(String path) {
        return URLUtil.isValidUrl(path);
    }

}
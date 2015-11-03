package com.casasmap.ribbit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    ParseUser currentUser = ParseUser.getCurrentUser();
    //adding constants
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;
    public static final int FILE_SIZE_LIMIT = 1024*1024*10;
    protected Uri mMediaUri;


    // creating the listener when item on camera icon gets clicked.
    protected DialogInterface.OnClickListener mDialogListener =
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0: //take a pic
                            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            if(mMediaUri == null){
                                Toast.makeText(MainActivity.this, R.string.error_access_sd, Toast.LENGTH_LONG).show();
                            }else {
                                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                            }
                            break;
                        case 1: // choose a pic
                            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                            if(mMediaUri == null){
                                Toast.makeText(MainActivity.this, R.string.error_access_sd, Toast.LENGTH_LONG).show();
                            }else {
                                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                                videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // 0 is the lowest quality
                                startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                            }
                            break;
                        case 2://make movie
                            Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            choosePhotoIntent.setType("image/*");
                            startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                            break;
                        case 3: //choose movie
                            Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            chooseVideoIntent.setType("video/*");
                            Toast.makeText(MainActivity.this, getString(R.string.video_limit), Toast.LENGTH_LONG).show();
                            startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                            break;
                    }

                }

                private Uri getOutputMediaFileUri(int mediaType) {
                        if(isExternalStorageAvailable()){
                            //success. thre an SC or something
                            //get Uri
                            //1. Get the exteranl storage directory
                            String appName = MainActivity.this.getString(R.string.app_name);
                            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
                            //2. create our subdirectory
                            if(!mediaStorageDir.exists()){
                                if(! mediaStorageDir.mkdirs()){
                                    Toast.makeText(MainActivity.this, R.string.couldnt_craete, Toast.LENGTH_LONG).show();
                                    return null;
                                }
                            }
                            //3. Create a file name
                            //4. create the file itself
                            File mediaFile;
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                            //create folder
                            String path = mediaStorageDir.getPath() + File.separator;
                            if(mediaType == MEDIA_TYPE_IMAGE){
                                mediaFile = new File(path + "IMG"+ timeStamp + ".jpg");
                            }else if(mediaType == MEDIA_TYPE_VIDEO){
                                mediaFile = new File((path + "VID"+timeStamp+".mp4"));
                            }else{
                                return null;
                            }
                            //5. return the file's uri
                            return Uri.fromFile(mediaFile);
                        }
                    else{
                            return null;

                        }
                }
                private boolean isExternalStorageAvailable(){
                        String state = Environment.getExternalStorageState();
                        if(state.equals(Environment.MEDIA_MOUNTED)){
                            return true;
                        }
                    else{
                            return false;
                        }

                }
            };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //adding processing symbol on mainactivity
        //deprecated
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        //accessing login activity
        if (currentUser == null) {
            goToLogin();
        } else {
            Toast.makeText(this, "Welcome! " + currentUser.getUsername(), Toast.LENGTH_SHORT).show();
        }

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }
//here we can manage the result after using the camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            //save to sd
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST){
                if(data == null){
                    Toast.makeText(this, "error carajo", Toast.LENGTH_LONG).show();
                }else{
                    mMediaUri = data.getData();
                }
                //checkign video size
                if(requestCode == PICK_VIDEO_REQUEST){
                    //make sure it is less than 10MB
                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, "error carajo", Toast.LENGTH_LONG).show();
                        return;
                    } catch (IOException e){
                        Toast.makeText(this, "error carajo", Toast.LENGTH_LONG).show();
                        return;
                    }finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            Toast.makeText(this, "error carajo", Toast.LENGTH_LONG).show();
                        }
                    }
                    if (fileSize >= FILE_SIZE_LIMIT){
                        Toast.makeText(this, "your file is larger than 10MB", Toast.LENGTH_LONG).show();
                        return;
                    }

                }
            }else{
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }



        }else if(resultCode != RESULT_CANCELED){
            Toast.makeText(this, R.string.coudnt_save, Toast.LENGTH_LONG);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                ParseUser.logOut();
                currentUser = ParseUser.getCurrentUser();
                goToLogin();
            case R.id.action_edit_friends:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        return super.onOptionsItemSelected(item);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        //next two lines say: start a new activity and omit or skip previous one. ie, will delete the previous one from cache.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


}

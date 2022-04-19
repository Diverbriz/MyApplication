package com.example.myapplication;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.sql.StatementEvent;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private File mPrivateRootDir;
    // Путь до каталога "images"
    private File mImagesDir;
    // Массив файлов из директории images
    File[] mImageFiles;
    // Массив имен файлов, соответствующих mImageFiles
    String[] mImageFilenames;
    // Инициализация явления
    // Инициализация явления

    private static final int PICK_CONTACT_REQUEST = 1;
    Button btnAdd, btnRead, btnClear;

    EditText etName, etEmail, etMessage;

    DBHelper dbHelper;
    Intent mResultIntent;

    // Инициализация явления
    ImageView imgV;
    private ShareActionProvider mShareActionProvider;

    int imgID[] = {R.drawable.android_24, R.drawable.ic_baseline};
    int count = -1;
   // private AdapterView<Adapter> mFileListView;
   // private URI[] mImageFilename;
   // private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        imgV = (ImageView) findViewById(R.id.imageView);

        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etMessage = (EditText) findViewById(R.id.edit_message);

        Button btnDate = (Button)findViewById(R.id.button_date);
        Button btnTime = (Button)findViewById(R.id.button_time);

        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        dbHelper = new DBHelper(this);

        mResultIntent =
                new Intent("com.example.myapplication.ACTION_RETURN_FILE");

        mPrivateRootDir = getFilesDir();
        mImagesDir = new File(mPrivateRootDir, "images");
        mImageFiles = mImagesDir.listFiles();
        setResult(Activity.RESULT_CANCELED, null);


    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    ActivityResultLauncher<Intent> sActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        imgV.setImageURI(uri);
                    }
                }
            }
    );

    public void openFileDialog(View v){
        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        data.setType("*/*");
        data = Intent.createChooser(data, "Choose a file");
        sActivityResultLauncher.launch(data);
    }

    @Override
    public void onClick(View v) {

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();


        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, "This is my text to send.");
        sendIntent.setType("text/plain");

        ArrayList<Uri> imageUris = new ArrayList<Uri>();

        Intent shareIntent = new Intent();

        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        shareIntent.setType("image/*");


        Intent intent;
        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "123456789"));
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        Uri location = Uri.parse("geo: 0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");

        Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, location);

        Intent intentSub = new Intent(this, Date.class);
        intentSub.putExtra("name", etMessage.getText().toString());



        switch (v.getId()) {

            case R.id.btnAdd:
                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_MAIL, email);

                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                break;

            case R.id.btnRead:
                Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

                if (cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                    int emailIndex = cursor.getColumnIndex(DBHelper.KEY_MAIL);
                    do {
                        Log.d("mLog", "ID = " + cursor.getInt(idIndex) +
                                ", name = " + cursor.getString(nameIndex) +
                                ", email = " + cursor.getString(emailIndex));
                    } while (cursor.moveToNext());
                } else
                    Log.d("mLog","0 rows");

                cursor.close();
                break;

            case R.id.btnClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                break;
            case R.id.button_date:
               // startActivity(Intent.createChooser(shareIntent, null));
                openFileDialog(v);
                break;
            case R.id.button_time:
                intent = new Intent("info.android.intent.action.time");
                startActivity(intent);
                break;
        }
        dbHelper.close();
    }
}
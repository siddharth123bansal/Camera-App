package com.example.camera;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContentInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
public class MainActivity extends AppCompatActivity {
    private static final int CAPTURE_CODE = 1001;
    Button btn;
    ImageView img;
    FusedLocationProviderClient loc;
    Calendar calendar;
    TextView txt;
    String  finald,date;
    public final static int REQUEST_CODE = 1034;
    Uri image_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=findViewById(R.id.Click);
        txt=findViewById(R.id.WaterMark);
        calendar=Calendar.getInstance();
        calendar=Calendar.getInstance();
        date=new SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(new Date());
        img=findViewById(R.id.IMG);

        loc= LocationServices.getFusedLocationProviderClient(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                   if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED||
                           checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED ||
                           checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_DENIED)
                   {
                       String[] per={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                               Manifest.permission.ACCESS_COARSE_LOCATION};
                       requestPermissions(per,REQUEST_CODE);
                   }else{
                       openCamera();
                       loc.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                           @Override
                           public void onSuccess(Location location) {
                                if(location!=null){
                                    Geocoder geo=new Geocoder(MainActivity.this, Locale.getDefault());
                                    try {
                                        List<Address> address =geo.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                        String latitude= String.valueOf(address.get(0).getLatitude());
                                        String longitude= String.valueOf(address.get(0).getLongitude());
                                        String Country=String.valueOf(address.get(0).getCountryName());
                                        String city=String.valueOf(address.get(0).getLocality());
                                        finald= latitude+" "+longitude+"\n"+Country+" "+city+"\n"+date+"\n";

                                        txt.setText(finald);
                                        Toast.makeText(MainActivity.this, finald, Toast.LENGTH_LONG).show();

                                    } catch (IOException e) {
                                        Toast.makeText(MainActivity.this, "Cant get The Location", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(MainActivity.this, "Cant get The Location", Toast.LENGTH_SHORT).show();
                                }
                           }
                       });
                   }
               }else{
                   openCamera();
               }

            }
        });
    }
    private void openCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"new Image");
        values.put(MediaStore.Images.Media.DESCRIPTION,finald);
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,CAPTURE_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode)
        {
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    openCamera();
                }
                else
                {
                    Toast.makeText(this, "Permissions Required", Toast.LENGTH_SHORT).show();
                }
        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            img.setImageURI(image_uri);
        }
    }
}
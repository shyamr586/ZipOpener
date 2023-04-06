package com.example.zipopener;

import org.qtproject.qt.android.bindings.QtActivity;;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends QtActivity {

    public final int REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("On create is running? ", "yes");
    }

    public void openFileDialog() {

        //filePickPointer = filePicker;
        //Log.d(tag, filePicker+"");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        //intent.putExtra("pointer", filePicker);
        startActivityForResult(intent, REQUEST_CODE);

    }


//            try {
//        File cacheDir = getCacheDir();
//        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath));
//        ZipEntry zipEntry;
//        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
//            File file = new File(cacheDir, zipEntry.getName());
//            if (zipEntry.isDirectory()) {
//                file.mkdirs();
//            } else {
//                FileOutputStream fileOutputStream = new FileOutputStream(file);
//                byte[] buffer = new byte[1024];
//                int length;
//                while ((length = zipInputStream.read(buffer)) > 0) {
//                    fileOutputStream.write(buffer, 0, length);
//                }
//                fileOutputStream.close();
//            }
//        }
//        zipInputStream.close();
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {

        if (requestcode == REQUEST_CODE && resultcode == Activity.RESULT_OK) {

            if (data != null) {
                Uri uri = data.getData();
                Log.d("The uri from java is", uri.getPath());

                InputStream input = null;

                try {
                    input = getContentResolver().openInputStream(uri);
                    ZipInputStream zippedInputStream = new ZipInputStream(input);
                    ZipEntry zipEntry;
                    //List<String> fileList = new ArrayList<>();
                    while ((zipEntry = zippedInputStream.getNextEntry()) != null) {
                        Path currPath = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            currPath = Paths.get(zipEntry.getName());
                        }
                        assert currPath != null;
                        Log.d("The name of the file is ",currPath.toString());
                        //fileList.add(currPath.toString());
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(getCacheDir(), zipEntry.getName()));
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zippedInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                        fileOutputStream.close();

                    }

                    Log.d("No exception was found in ", input.toString());
                } catch (FileNotFoundException e) {
                    Log.d("Exception found while opening input", e.toString());
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    Log.d("Exception, zippedinputstream is not recognized: ", e.toString());
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
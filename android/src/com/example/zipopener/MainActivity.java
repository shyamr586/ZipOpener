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
    public long pointer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("On create is running? ", "yes");
    }

    public native void fileListReceived(ArrayList<String> files, long pointer);

    public void openFileDialog(long FilePickerPointer) {
        pointer = FilePickerPointer;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        startActivityForResult(intent, REQUEST_CODE);
    }

    //https://stackoverflow.com/questions/40050270/java-unzip-and-progress-bar
    //check this ^ to get an idea.

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
                    ArrayList<String> fileList = new ArrayList<>();
                    while ((zipEntry = zippedInputStream.getNextEntry()) != null) {
                        Path currPath = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            currPath = Paths.get(zipEntry.getName());
                        }
                        assert currPath != null;
                        Log.d("The name of the file is ",currPath.toString());
                        fileList.add(currPath.toString());
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(getCacheDir(), zipEntry.getName()));
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zippedInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                        fileOutputStream.close();

                    }

                    Log.d("No exception was found in ", input.toString());
                    Log.d("ArrayList size is: ", fileList.size()+"");

                    fileListReceived(fileList, pointer);


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
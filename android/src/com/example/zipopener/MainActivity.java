package com.example.zipopener;

import org.qtproject.qt.android.bindings.QtActivity;;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

    public class ParameterClass {
        Uri uri;
        ZipInputStream zippedInputStream;
        int totalFiles;

        ParameterClass(Uri uri, ZipInputStream zippedInputStream, int totalFiles) {
            this.uri = uri;
            this.zippedInputStream = zippedInputStream;
            this.totalFiles = totalFiles;
        }
    }

    //use lambdas to get callback -- closures
    public class BackgroundThreadClass extends AsyncTask<InputStream, Void, Void> {
        @Override
        public Void doInBackground(InputStream... params) {
            InputStream input = params[0];
            Log.d("Running statement ", "1");
            ZipInputStream zippedInputStream = new ZipInputStream(input);
            Log.d("Running statement ", "2");
            ZipEntry zipEntry;
            int totalFiles = 0;

            while (true) {
                try {
                    if ((zipEntry = zippedInputStream.getNextEntry()) == null) break;
                } catch (IOException e) {
                    Log.d("The IO exception is running?", "yes");
                    throw new RuntimeException(e);
                }
                if (!zipEntry.isDirectory()) {
                    totalFiles++;
                }
            }

            Log.d("The total files found is: ", totalFiles + "");
            input = params[0];
            Log.d("Running statement ", "3");
            zippedInputStream = new ZipInputStream(input);
            Log.d("Running statement ", "4");
//            Log.d("Running statement ", "5");
//            Uri uri = params[0].uri;
//            ZipInputStream zippedInputStream = params[0].zippedInputStream;
//            Log.d("Running statement ", "6");
//            ZipEntry zipEntry = null;
//            int totalFiles = params[0].totalFiles;
            zipEntry = null;
            ArrayList<String> fileList = new ArrayList<>();
            int fileNumber = 0;
            while (true) {
                try {
                    if ((zipEntry = zippedInputStream.getNextEntry()) == null) break;
                    else {Log.d("Infinite loop?", String.valueOf(zippedInputStream.getNextEntry() == null));};
                } catch (IOException e) {
                    Log.d("Run time exception found ", "in 1" + e.toString());
                    throw new RuntimeException(e);
                }
                fileNumber += 1;
                Path currPath = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    currPath = Paths.get(zipEntry.getName());
                }
                float percentage = (float) fileNumber / totalFiles * 100;
                Log.d("The percentage done is: ", percentage + "");
                assert currPath != null;
                Log.d("The name of the file is ", currPath.toString());
                fileList.add(currPath.toString());
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(new File(getCacheDir(), zipEntry.getName()));
                } catch (FileNotFoundException e) {
                    Log.d("Run time exception found ", "in 2" + e.toString());
                    throw new RuntimeException(e);
                }
                byte[] buffer = new byte[1024];
                int length;
                while (true) {
                    try {
                        if (!((length = zippedInputStream.read(buffer)) > 0)) break;
                    } catch (IOException e) {
                        Log.d("Run time exception found ", "in 3" + e.toString());
                        throw new RuntimeException(e);
                    }
                    try {
                        fileOutputStream.write(buffer, 0, length);
                    } catch (IOException e) {
                        Log.d("Run time exception found ", "in 4" + e.toString());
                        throw new RuntimeException(e);
                    }
                }
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.d("Run time exception found ", "in 5" + e.toString());
                    throw new RuntimeException(e);
                }
            }
            fileListReceived(fileList, pointer);
            return null;
        }
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
//                try {
//                    input = getContentResolver().openInputStream(uri);
//                    Log.d("Running statement ", "1");
//                    ZipInputStream zippedInputStream = new ZipInputStream(input);
//                    Log.d("Running statement ", "2");
//                    ZipEntry zipEntry;
//                    int totalFiles = 0;
//
//                    while ((zipEntry = zippedInputStream.getNextEntry()) != null) {
//                        if (!zipEntry.isDirectory()) {
//                            totalFiles++;
//                        }
//                    }
//
//                    Log.d("The total files found is: ",totalFiles+"");
//                    input = getContentResolver().openInputStream(uri);
//                    Log.d("Running statement ", "3");
//                    zippedInputStream = new ZipInputStream(input);
//                    Log.d("Running statement ", "4");

//                    ArrayList<String> fileList = new ArrayList<>();
//                    int fileNumber = 0;
                //MainActivity.ParameterClass params = new ParameterClass(uri, zippedInputStream,  totalFiles);

                try {
                    input = getContentResolver().openInputStream(uri);
                    BackgroundThreadClass bgThread = new BackgroundThreadClass();
                    bgThread.execute(input);

//                    while ((zipEntry = zippedInputStream.getNextEntry()) != null) {
//                        fileNumber+=1;
//                        Path currPath = null;
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                            currPath = Paths.get(zipEntry.getName());
//                        }
//                        Log.d("The percentage done is: ", (fileNumber+1/totalFiles)*100+"");
//                        assert currPath != null;
//                        Log.d("The name of the file is ",currPath.toString());
//                        fileList.add(currPath.toString());
//                        FileOutputStream fileOutputStream = new FileOutputStream(new File(getCacheDir(), zipEntry.getName()));
//                        byte[] buffer = new byte[1024];
//                        int length;
//                        while ((length = zippedInputStream.read(buffer)) > 0) {
//                            fileOutputStream.write(buffer, 0, length);
//                        }
//                        fileOutputStream.close();
//
//                    }
//
//                    Log.d("No exception was found in ", input.toString());
//                    Log.d("ArrayList size is: ", fileList.size()+"");
//
//                    fileListReceived(fileList, pointer);


//                } catch (FileNotFoundException e) {
//                    Log.d("Exception found while opening input", e.toString());
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    Log.d("Exception, zippedinputstream is not recognized: ", e.toString());
//                    throw new RuntimeException(e);
//                }
//            }
                } catch (FileNotFoundException e) {
                    Log.d("Exception, file not found ", e.toString());
                } catch(NullPointerException e){
                    Log.d("Exception, null pointer ", e.toString());
                } catch (Exception e) {
                    Log.d("Exception ",e.toString());
                }
            }
        }
    }
}
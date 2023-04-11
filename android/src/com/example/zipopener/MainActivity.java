package com.example.zipopener;

import org.qtproject.qt.android.bindings.QtActivity;;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class MainActivity extends QtActivity {

    public final int REQUEST_CODE = 1;
    public long pointer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("On create is running? ", "yes");
    }

    public class ParameterClass{
        InputStream input;
        Uri uri;

        public ParameterClass(InputStream input, Uri uri){
            this.input = input;
            this.uri = uri;
        }
    }

    public native void progressUpdated(int percentage, long pointer);

    public class BackgroundThreadClass extends AsyncTask<ParameterClass, Integer, Void> {
        public long compressedBytesRead;
        public long compressedBytesProcessed = 0; // Add this line
        @Override
        public Void doInBackground(ParameterClass... params) {

            InputStream input2 = params[0].input;
            ZipInputStream zis = new ZipInputStream(input2);
            ArrayList<String> fileList = new ArrayList<>();
            ZipEntry zipEntry2;
            int fileNumber = 0;
            int totalBytesWritten = 0;
            while (true) {

                try {
                    zipEntry2 = zis.getNextEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if ((zipEntry2) == null) break;
                fileNumber += 1;
                Path currPath = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currPath = Paths.get(zipEntry2.getName());
                    Log.d("Get size is: ",zipEntry2.getSize()+"");
                    Log.d("The name of the files is ", (currPath!=null)? currPath.toString():"");
                }

                fileList.add(currPath.toString());
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(new File(getCacheDir(), zipEntry2.getName()));
                    //Log.d("GET SIZE IS ",zipEntry2.getSize()+"");
                } catch (FileNotFoundException e) {
                    Log.d("Run time exception found ", "in 2" + e.toString());
                    throw new RuntimeException(e);
                }
                byte[] buffer = new byte[1024];
                int length;

                //int progress = 0;

                while (true) {
                    try {
                        if (!((length = zis.read(buffer)) > 0)) break;
                    } catch (IOException e) {
                        Log.d("Run time exception found ", "in 3" + e.toString());
                        throw new RuntimeException(e);
                    }
                    try {
                        fileOutputStream.write(buffer, 0, length);

                        totalBytesWritten+=length;
                        //compressedBytesProcessed += length;
                        if (totalBytesWritten%10000 == 0){
                            publishProgress(totalBytesWritten);
                        }

                    } catch (IOException e) {
                        Log.d("Run time exception found ", "in 4" + e.toString());
                        throw new RuntimeException(e);
                    }
                }

                //compressedBytesRead += zipEntry2.getCompressedSize();
                //Log.d("Compressed bytes processed is: ",compressedBytesProcessed+"");
                //int progress = Math.round(((float) fileNumber / totalFiles) * 100);
                publishProgress(totalBytesWritten);
                Log.d("Running statement ", "8");
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
        @Override
        public void onProgressUpdate(Integer ...params){
            Log.d("Java value for progress is: ",params[0]+"");
            progressUpdated(params[0], pointer);
        }
    }

    public InputStream getInputStream(Uri uri) throws FileNotFoundException {
        InputStream input = getContentResolver().openInputStream(uri);
        return input;
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
                    InputStream input2 = getContentResolver().openInputStream(uri);

                    BackgroundThreadClass bgThread = new BackgroundThreadClass();

                    ParameterClass parameterObj = new ParameterClass(input, uri);
                    bgThread.execute(parameterObj);

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
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
    
    //use lambdas to get callback -- closures
    public class BackgroundThreadClass extends AsyncTask<ParameterClass, Integer, Void> {
        public long compressedBytesRead;
        public long compressedBytesProcessed = 0; // Add this line
        @Override
        public Void doInBackground(ParameterClass... params) {
            InputStream input = params[0].input;
            ZipInputStream zippedInputStream = new ZipInputStream(input);
            long totalUncompressedSize = 0;
            ZipEntry zipEntry = null;
//            while (true) {
//                try {
//                    zipEntry = zippedInputStream.getNextEntry();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                if ((zipEntry) == null) break;
//                totalUncompressedSize+=zipEntry.getExtra().length;;
//            }

            Log.d("Total bytes is: ",totalUncompressedSize+"");

            //Log.d("The total files found is: ", totalFiles + "");

            InputStream input2 = params[0].input;
            ZipInputStream zis = new ZipInputStream(input2);
            ArrayList<String> fileList = new ArrayList<>();
            ZipEntry zipEntry2;
            int fileNumber = 0;
            float totalBytesWritten = 0;
            Log.d("Running statement ", "5");
//            try {
//                //String absolutePath = getContentResolver().
//
//                File normal_file = new File(params[0].uri.getPath());
//                Log.d("URI TO STRING IS: ", params[0].uri.toString());
//                Log.d("FILE IS: ", new File(new URI(params[0].uri.toString()).getPath()).toString());
//                Log.d("URI GET PATH IS: ", params[0].uri.getPath());
//                ZipFile file = new ZipFile(params[0].uri.toString());
//
//                try {
//                    final Enumeration<? extends ZipEntry> entries = file.entries();
//                    while (entries.hasMoreElements()) {
//                        final ZipEntry entry = entries.nextElement();
//                        Log.d("Get size ",entry.getSize()+"");
//                        //extractFile(entry, file.getInputStream(entry));
//                    }
//                    //System.out.printf("Zip file %s extracted successfully.", SOURCE_FILE);
//                }
//                finally {
//                    file.close();
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (URISyntaxException e) {
//                throw new RuntimeException(e);
//            }
            while (true) {

                try {
                    zipEntry2 = zis.getNextEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //Log.d("zipped input get entry is", zis.getNextEntry()+"");
                if ((zipEntry2) == null) break;
                fileNumber += 1;
                Path currPath = null;
                //Log.d("Get size is: ",zipEntry2.getSize()+"");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currPath = Paths.get(zipEntry2.getName());
                    Log.d("Get size is: ",zipEntry2.getSize()+"");
                    Log.d("The name of the files is ", (currPath!=null)? currPath.toString():"");
                }

//                Log.d("Running statement ", "6");
//                float percentage = (float) fileNumber / totalFiles * 100;
//                Log.d("The percentage done is: ", percentage + "");
//                int roundedPercentage = (int)Math.floor(percentage);
//                publishProgress(roundedPercentage);
                //assert currPath != null;

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
                float totalAvailableBytes = 0;
                int compressedBytesReadThisEntry = 0;

                while (true) {
                    try {
                        if (!((length = zis.read(buffer)) > 0)) break;
                        try {
                            Log.d("Read is: ",zis.read(buffer)+"");
                        } catch (IOException e) {
                            Log.d("Exception found in read",e.toString());
                            throw new RuntimeException(e);
                        }
                    } catch (IOException e) {
                        Log.d("Run time exception found ", "in 3" + e.toString());
                        throw new RuntimeException(e);
                    }
                    try {
                        fileOutputStream.write(buffer, 0, length);
                        //totalAvailableBytes += zis.available();
                        //Log.d("The available value is: ",zis.available()+"");
                        //Log.d("Get size is", zipEntry2.getSize()+"");
                        totalBytesWritten+=length;
                        compressedBytesProcessed += length;

                        if (totalBytesWritten%10000 == 0){
                            //Log.d("Total bytes read is ", totalBytesRead+"");
                            //int progress = Math.round((totalBytesWritten/fileSize)*100);
                            //Log.d("Progress is ", progress+"");
                            //publishProgress(progress);
                        }

//                        if (totalAvailableBytes%10000 == 0){
//                            Log.d("Total available value is: ",totalAvailableBytes+"");
//                        }


                    } catch (IOException e) {
                        Log.d("Run time exception found ", "in 4" + e.toString());
                        throw new RuntimeException(e);
                    }
                }
//                Log.d("Get compressed size: ", zipEntry2.getCompressedSize()+"");
//                Log.d("Min compressed size is: ", Math.min(compressedBytesReadThisEntry, zipEntry2.getCompressedSize())+"");
                compressedBytesRead += zipEntry2.getCompressedSize();
                Log.d("Compressed bytes processed is: ",compressedBytesProcessed+"");
                int progress = Math.round(((float) compressedBytesProcessed / totalUncompressedSize) * 100);
                publishProgress(progress);
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
                    InputStream input2 = getContentResolver().openInputStream(uri);

                    BackgroundThreadClass bgThread = new BackgroundThreadClass();

                    ParameterClass parameterObj = new ParameterClass(input, uri);
                    bgThread.execute(parameterObj);

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
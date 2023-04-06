package com.example.zipopener;

import org.qtproject.qt.android.bindings.QtActivity;;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends QtActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("On create is running? ", "yes");
    }

    public void printHello(){
        Log.d("The consoled is: ", "Hello");
    }
}
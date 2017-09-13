package com.example.saurabh.map_final;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Created by saurabh on 8/22/17.
 */

public class login extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

    }

    public void onButtonClick(View v)
    {
        if(v.getId() == R.id.go_next)
        {
            //Firebase Authentication for login
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
        }
    }
}

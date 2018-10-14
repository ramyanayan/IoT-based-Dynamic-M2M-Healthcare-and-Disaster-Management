package com.example.nimeneze.lifeline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText ET;
    String IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ET =(EditText) findViewById(R.id.editText);
    }

    public void sendIP(View v){
        IP=ET.getText().toString();
        Intent i=new Intent(MainActivity.this,Main2Activity.class);
        i.putExtra("IP",IP);
        startActivity(i);
    }

}

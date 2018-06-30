package com.example.jokecontainer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class JokeActivity extends AppCompatActivity {

    public static final String JOKE = "intent_joke";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);

        TextView jokeTextView = (TextView) findViewById(R.id.joke_text_view);

        Intent intent = getIntent();

        if(intent != null){
            String joke = intent.getStringExtra(JOKE);
            jokeTextView.setText(joke);
        }
    }
}

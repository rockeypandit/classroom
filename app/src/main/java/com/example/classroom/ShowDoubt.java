package com.example.classroom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ShowDoubt extends AppCompatActivity {
    private DoubtModel d;
    private TextView questionText;
    private TextView answerText;
    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_doubt);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        questionText = findViewById(R.id.questionTextView);
        answerText = findViewById(R.id.answerTextView);

        Bundle extraInfo = getIntent().getExtras();
        if (extraInfo != null) {
            this.d = (DoubtModel) extraInfo.get("doubt");
        }
        questionText.setText(d.getQuestion());
        if (d.getAnswer() != null) {
            answerText.setText(d.getAnswer());
        } else {
            answerText.setText("This question has not been answered yet.");
        }
    }
}
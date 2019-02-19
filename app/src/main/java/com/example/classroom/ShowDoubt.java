package com.example.classroom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowDoubt extends AppCompatActivity {
    private DoubtModel d;
    private TextView questionText;
    private TextView answerText;
    private LinearLayout showDoubtLayout;
    private Button downloadAttachmentButton;
    private DownloadManager dm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_doubt);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        questionText = findViewById(R.id.questionTextView);
        answerText = findViewById(R.id.answerTextView);
        downloadAttachmentButton = findViewById(R.id.dnldAttachButton);
        Bundle extraInfo = getIntent().getExtras();
        if (extraInfo != null) {
            this.d = (DoubtModel) extraInfo.get("doubt");
            Log.d("DOUBT OBJECT", d.toString());
        }

        questionText.setText(d.getQuestion());

        if (d.getAnswer() != null) {
            answerText.setText(d.getAnswer());
        } else {
            answerText.setText("This question has not been answered yet.");
        }

        if (d.getAttachmentLink() != null) {
            dm = new DownloadManager(d.getAttachmentLink(), "attachment");
            Log.d("DOWNLOAD LINK", d.getAttachmentLink());
            downloadAttachmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dm.execute();
                    Snackbar.make(v, "Download Started", Snackbar.LENGTH_SHORT).show();
                }
            });
        } else {
            downloadAttachmentButton.setEnabled(false);
        }
    }
}
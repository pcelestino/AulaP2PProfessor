package br.edu.ffb.pedro.aulap2pprofessor.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import br.edu.ffb.pedro.aulap2pprofessor.R;
import br.edu.ffb.pedro.aulap2pprofessor.model.Questionnaire;
import br.edu.ffb.pedro.aulap2pprofessor.model.Quiz;
import br.edu.ffb.pedro.bullyelectionp2p.BullyElectionP2p;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        LinearLayout llStudentReportContainer = (LinearLayout) findViewById(R.id.llStudentReportContainer);

        Quiz quiz = new Quiz(this);
        String questionnaireJson = getIntent().getStringExtra(Questionnaire.TAG);
        try {
            Questionnaire questionnaire = LoganSquare.parse(questionnaireJson, Questionnaire.class);
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(questionnaire.getStudentName());
            }
            quiz.addQuizReport(llStudentReportContainer, questionnaire);
        } catch (IOException e) {
            Log.e(BullyElectionP2p.TAG, "Falha ao efetuar o parse do questionário", e);
        }
    }
}

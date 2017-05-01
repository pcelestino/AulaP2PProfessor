package br.edu.ffb.pedro.aulap2pprofessor.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.List;

import br.edu.ffb.pedro.aulap2pprofessor.R;
import br.edu.ffb.pedro.aulap2pprofessor.activity.MainActivity;
import br.edu.ffb.pedro.aulap2pprofessor.activity.ReportActivity;
import br.edu.ffb.pedro.aulap2pprofessor.adapters.holders.StudentsReportViewHolder;
import br.edu.ffb.pedro.aulap2pprofessor.model.Questionnaire;
import br.edu.ffb.pedro.bullyelectionp2p.BullyElectionP2p;

public class StudentsReportAdapter extends RecyclerView.Adapter {

    private List<Questionnaire> studentReports;
    private Context context;

    public StudentsReportAdapter(List<Questionnaire> studentReports, Context context) {
        this.studentReports = studentReports;
        this.context = context;
    }

    public void swap(List<Questionnaire> studentReports){
        this.studentReports.clear();
        this.studentReports.addAll(studentReports);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_questionnaire_report, parent, false);
        return new StudentsReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StudentsReportViewHolder studentsReportViewHolder = (StudentsReportViewHolder) holder;
        final Questionnaire studentReport = studentReports.get(position);

        studentsReportViewHolder.tvStudentNameReport.setText(studentReport.getStudentName());

        float questionnaireOverallScore = studentReport.getOverallScore();
        studentsReportViewHolder.tvStudentReportScoreValue.setText(String.valueOf(studentReport.getOverallScore()));
        if (questionnaireOverallScore >= 7.0) {
            studentsReportViewHolder.tvStudentReportScoreValue.setTextColor(ContextCompat.getColor(context, R.color.green_dark));
        } else {
            studentsReportViewHolder.tvStudentReportScoreValue.setTextColor(ContextCompat.getColor(context, R.color.red_dark));
        }

        studentsReportViewHolder.vStudentReportContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String studentReportJson = LoganSquare.serialize(studentReport);
                    Intent intent = new Intent(context, ReportActivity.class);
                    intent.putExtra(Questionnaire.TAG, studentReportJson);
                    context.startActivity(intent);
                } catch (IOException e) {
                    Log.e(BullyElectionP2p.TAG, "Falha ao serializar o questionário", e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentReports.size();
    }
}
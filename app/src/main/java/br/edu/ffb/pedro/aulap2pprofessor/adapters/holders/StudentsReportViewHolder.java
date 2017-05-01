package br.edu.ffb.pedro.aulap2pprofessor.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.edu.ffb.pedro.aulap2pprofessor.R;

public class StudentsReportViewHolder extends RecyclerView.ViewHolder {

    public TextView tvStudentNameReport;
    public TextView tvStudentReportScoreValue;
    public View vStudentReportContainer;

    public StudentsReportViewHolder(View itemView) {
        super(itemView);

        tvStudentNameReport = (TextView) itemView.findViewById(R.id.tvStudentNameReport);
        tvStudentReportScoreValue = (TextView) itemView.findViewById(R.id.tvStudentReportScoreValue);
        vStudentReportContainer = itemView.findViewById(R.id.rlStudentReportContainer);
    }
}

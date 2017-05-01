package br.edu.ffb.pedro.aulap2pprofessor.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import br.edu.ffb.pedro.aulap2pprofessor.R;

/**
 * Created by Pedro on 03/04/2017.
 */

public class StudentsListViewHolder extends RecyclerView.ViewHolder {

    public TextView studentDeviceReadableName;
    public ImageView studentDeviceLeaderIcon;

    public StudentsListViewHolder(View itemView) {
        super(itemView);

        studentDeviceReadableName = (TextView) itemView.findViewById(R.id.tvStudentNameReport);
        studentDeviceLeaderIcon = (ImageView) itemView.findViewById(R.id.ivStudentReportIcon);
    }
}

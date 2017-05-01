package br.edu.ffb.pedro.aulap2pprofessor.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import br.edu.ffb.pedro.aulap2pprofessor.R;
import br.edu.ffb.pedro.aulap2pprofessor.adapters.holders.StudentsListViewHolder;
import br.edu.ffb.pedro.bullyelectionp2p.BullyElectionP2pDevice;

public class StudentsListAdapter extends RecyclerView.Adapter {

    private ArrayList<BullyElectionP2pDevice> studentDevices;
    private Context context;

    public StudentsListAdapter(ArrayList<BullyElectionP2pDevice> studentDevices, Context context) {
        this.studentDevices = studentDevices;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new StudentsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StudentsListViewHolder studentsListViewHolder = (StudentsListViewHolder) holder;
        BullyElectionP2pDevice easyP2pDevice = studentDevices.get(position);
        studentsListViewHolder.studentDeviceReadableName.setText(easyP2pDevice.readableName);

        if (easyP2pDevice.isLeader) {
            studentsListViewHolder.studentDeviceLeaderIcon.setImageResource(R.drawable.ic_sheriff_enabled);
        } else {
            studentsListViewHolder.studentDeviceLeaderIcon.setImageResource(R.drawable.ic_sheriff_disabled);
        }
    }

    @Override
    public int getItemCount() {
        return studentDevices.size();
    }
}

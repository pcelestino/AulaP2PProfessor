package br.edu.ffb.pedro.aulap2pprofessor.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import br.edu.ffb.pedro.aulap2pprofessor.R;
import br.edu.ffb.pedro.aulap2pprofessor.activity.MainActivity;
import br.edu.ffb.pedro.aulap2pprofessor.adapters.StudentsListAdapter;
import br.edu.ffb.pedro.aulap2pprofessor.event.QuizDataEvent;
import br.edu.ffb.pedro.aulap2pprofessor.model.QuizData;
import br.edu.ffb.pedro.bullyelectionp2p.BullyElectionP2p;
import br.edu.ffb.pedro.bullyelectionp2p.event.BullyElectionEvent;
import br.edu.ffb.pedro.bullyelectionp2p.event.ServerEvent;

public class StudentsFragment extends Fragment {

    private RecyclerView studentsList;
    private View mEmptyView;
    private StudentsListAdapter studentsListAdapter;
    private MainActivity mainActivity;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.students_list));
        setupStudentsList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_students, container, false);
        studentsList = (RecyclerView) view.findViewById(R.id.studentsList);
        mEmptyView = view.findViewById(R.id.emptyView);
        mainActivity = (MainActivity) getActivity();
        return view;
    }

    private void checkAdapterIsEmpty() {
        if (studentsListAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            studentsList.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            studentsList.setVisibility(View.VISIBLE);
        }
    }

    private void setupStudentsList() {
        studentsListAdapter = new StudentsListAdapter(mainActivity.bullyElectionP2p.registeredClients, mainActivity);

        studentsListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });

        LinearLayoutManager studentsListLayoutManager = new LinearLayoutManager(mainActivity,
                LinearLayoutManager.VERTICAL, false);

        studentsList.setLayoutManager(studentsListLayoutManager);
        studentsList.setHasFixedSize(true);
        studentsList.setAdapter(studentsListAdapter);
        checkAdapterIsEmpty();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerEvent(ServerEvent serverEvent) {
        switch (serverEvent.event) {
            case ServerEvent.DEVICE_REGISTERED_WITH_HOST:
                Log.d(BullyElectionP2p.TAG, "Dispositivo registrado no servidor local\n"
                        + serverEvent.device.toString());
                studentsListAdapter.notifyDataSetChanged();
                break;
            case ServerEvent.DEVICE_UNREGISTERED_WITH_HOST:
                Log.d(BullyElectionP2p.TAG, "Dispositivo removido do servidor local\n"
                        + serverEvent.device.toString());
                studentsListAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBullyElectionEvent(BullyElectionEvent bullyElectionEvent) {
        switch (bullyElectionEvent.event) {
            case BullyElectionEvent.ELECTED_LEADER:
                studentsListAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(StudentsFragment.this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(StudentsFragment.this);
        super.onStop();
    }
}

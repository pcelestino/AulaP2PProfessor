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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import br.edu.ffb.pedro.aulap2pprofessor.AulaP2PProfessorApp;
import br.edu.ffb.pedro.aulap2pprofessor.R;
import br.edu.ffb.pedro.aulap2pprofessor.activity.MainActivity;
import br.edu.ffb.pedro.aulap2pprofessor.adapters.StudentsReportAdapter;
import br.edu.ffb.pedro.aulap2pprofessor.event.QuizDataEvent;
import br.edu.ffb.pedro.aulap2pprofessor.model.DaoSession;
import br.edu.ffb.pedro.aulap2pprofessor.model.Questionnaire;
import br.edu.ffb.pedro.aulap2pprofessor.model.QuestionnaireDao;
import br.edu.ffb.pedro.bullyelectionp2p.BullyElectionP2p;
import br.edu.ffb.pedro.bullyelectionp2p.payload.bully.BullyElection;

public class ReportFragment extends Fragment {

    private RecyclerView studentsReport;
    private View mEmptyView;
    private StudentsReportAdapter studentsReportAdapter;
    private MainActivity mainActivity;
    private LinearLayout llQuestionnaireReportContainer;
    private TextView tvQuestionnaireTitle;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.report));
        setupStudentsReportList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_report, container, false);
        studentsReport = (RecyclerView) view.findViewById(R.id.rvStudentsQuestionnaireReport);
        llQuestionnaireReportContainer = (LinearLayout) view.findViewById(R.id.llQuestionnaireReportContainer);
        tvQuestionnaireTitle = (TextView) view.findViewById(R.id.questionnaireTitle);
        mEmptyView = view.findViewById(R.id.emptyView);
        mainActivity = (MainActivity) getActivity();
        return view;
    }

    private void setupStudentsReportList() {

        List<Questionnaire> questionnaires = getAppDaoSession().getQuestionnaireDao().queryBuilder()
                .where(QuestionnaireDao.Properties.Id.notEq(Questionnaire.DEFAULT_QUESTIONNAIRE)).list();

        studentsReportAdapter = new StudentsReportAdapter(questionnaires, mainActivity);

        studentsReportAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });

        LinearLayoutManager studentsListLayoutManager = new LinearLayoutManager(mainActivity,
                LinearLayoutManager.VERTICAL, false);

        studentsReport.setLayoutManager(studentsListLayoutManager);
        studentsReport.setHasFixedSize(true);
        studentsReport.setAdapter(studentsReportAdapter);
        checkAdapterIsEmpty();
    }

    private void checkAdapterIsEmpty() {
        if (studentsReportAdapter.getItemCount() == 0) {
            Log.d(BullyElectionP2p.TAG, "Escondendo LISTA");
            hideQuestionnaireTitle();
            mEmptyView.setVisibility(View.VISIBLE);
            llQuestionnaireReportContainer.setVisibility(View.GONE);
        } else {
            Log.d(BullyElectionP2p.TAG, "Exibindo LISTA");
            showQuestionnaireTitle();
            mEmptyView.setVisibility(View.GONE);
            llQuestionnaireReportContainer.setVisibility(View.VISIBLE);
        }
    }

    private void showQuestionnaireTitle() {
        Questionnaire defaultQuestionnaire =
                getAppDaoSession().getQuestionnaireDao().load(Questionnaire.DEFAULT_QUESTIONNAIRE);
        if (defaultQuestionnaire != null) {
            tvQuestionnaireTitle.setVisibility(View.VISIBLE);
            tvQuestionnaireTitle.setText(defaultQuestionnaire.getName());
        }
    }

    private void hideQuestionnaireTitle() {
        tvQuestionnaireTitle.setText("");
        tvQuestionnaireTitle.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuizDataEvent(QuizDataEvent quizDataEvent) {
        switch (quizDataEvent.event) {
            case QuizDataEvent.QUESTIONNAIRE_RECEIVED:
                Log.d(BullyElectionP2p.TAG, "Questionário recebido, atualizando lista de reports");
                List<Questionnaire> questionnaires = getAppDaoSession().getQuestionnaireDao().queryBuilder()
                        .where(QuestionnaireDao.Properties.Id.notEq(Questionnaire.DEFAULT_QUESTIONNAIRE)).list();
                studentsReportAdapter.swap(questionnaires);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(ReportFragment.this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(ReportFragment.this);
        super.onStop();
    }

    private DaoSession getAppDaoSession() {
        return ((AulaP2PProfessorApp) mainActivity.getApplication()).getDaoSession();
    }
}

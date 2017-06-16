package br.edu.ffb.pedro.aulap2pprofessor.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import br.edu.ffb.pedro.aulap2pprofessor.AulaP2PProfessorApp;
import br.edu.ffb.pedro.aulap2pprofessor.R;
import br.edu.ffb.pedro.aulap2pprofessor.activity.MainActivity;
import br.edu.ffb.pedro.aulap2pprofessor.model.DaoSession;
import br.edu.ffb.pedro.aulap2pprofessor.model.Questionnaire;
import br.edu.ffb.pedro.aulap2pprofessor.model.Quiz;
import br.edu.ffb.pedro.aulap2pprofessor.model.QuizData;
import br.edu.ffb.pedro.bullyelectionp2p.BullyElectionP2p;
import br.edu.ffb.pedro.bullyelectionp2p.event.BullyElectionEvent;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import okio.BufferedSource;
import okio.Okio;

public class QuizFragment extends Fragment {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 32;
    private LinearLayout quizContainer;
    private Quiz quiz;
    private MainActivity mainActivity;
    private View emptyView;
    private boolean isSendQuestionnaireToLeaderEvent;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle(getString(R.string.quiz));
        quiz = new Quiz(mainActivity);

        Questionnaire questionnaire = getAppDaoSession()
                .getQuestionnaireDao()
                .load(Questionnaire.DEFAULT_QUESTIONNAIRE);

        if (questionnaire != null) {
            emptyView.setVisibility(View.GONE);
            quiz.addQuiz(quizContainer, questionnaire);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        quizContainer = (LinearLayout) view.findViewById(R.id.quiz_container);
        emptyView = view.findViewById(R.id.emptyView);

        FloatingActionButton fbSendQuiz = (FloatingActionButton) view.findViewById(R.id.fb_send_quiz);
        fbSendQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(mainActivity)
                        .setTitle("Enviar Questionário?")
                        .setMessage("Será efetuada a eleição do líder e logo após, " +
                                "os questionários dos alunos serão atualizados")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                isSendQuestionnaireToLeaderEvent = true;
                                Log.d("EXISTE LIDER?", String.valueOf(mainActivity.bullyElectionP2p.registeredLeader != null));
                                if (mainActivity.bullyElectionP2p.registeredLeader != null) {
                                    sendQuestionnaire();
                                } else {
                                    mainActivity.bullyElectionP2p.bootstrapElection();
                                }
                            }
                        })
                        .setNegativeButton("Não", null)
                        .create();
                alertDialog.show();
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_quiz, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_questions:
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    FilePickerBuilder.getInstance().setMaxCount(1)
                            .setSelectedFiles(new ArrayList<String>())
                            .setActivityTheme(R.style.AppTheme)
                            .addFileSupport("QUESTIONÁRIOS", new String[]{".json"})
                            .pickFile(QuizFragment.this);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            String questionsPath = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS).get(0);
            File questionnaireFile = new File(questionsPath);
            try {
                BufferedSource questionsSource = Okio.buffer(Okio.source(questionnaireFile));
                String questionnaireJson = questionsSource.readUtf8();
                Questionnaire questionnaire = LoganSquare.parse(questionnaireJson, Questionnaire.class);
                emptyView.setVisibility(View.GONE);
                quiz.addQuiz(quizContainer, questionnaire);
            } catch (FileNotFoundException e) {
                Log.e(BullyElectionP2p.TAG, "Nenhum arquivo encontrado", e);
            } catch (IOException e) {
                Log.e(BullyElectionP2p.TAG, "Erro ao ler os dados em UTF-8", e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FilePickerBuilder.getInstance().setMaxCount(1)
                            .setSelectedFiles(new ArrayList<String>())
                            .setActivityTheme(R.style.AppTheme)
                            .addFileSupport("QUESTIONÁRIOS", new String[]{".json"})
                            .pickFile(QuizFragment.this);
                } else {
                    Toast.makeText(getContext(), "A permissão é Necessário para exibir a lista de arquivos", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBullyElectionEvent(BullyElectionEvent bullyElectionEvent) {
        switch (bullyElectionEvent.event) {
            case BullyElectionEvent.ELECTED_LEADER:
                Toast.makeText(getContext(), "Líder eleito: " +
                        bullyElectionEvent.device.readableName, Toast.LENGTH_SHORT).show();

                sendQuestionnaire();
                break;
        }
    }

    private void sendQuestionnaire() {
        if (isSendQuestionnaireToLeaderEvent) {
            Questionnaire questionnaire = getAppDaoSession()
                    .getQuestionnaireDao()
                    .load(Questionnaire.DEFAULT_QUESTIONNAIRE);

            if (questionnaire != null) {
                Log.d(BullyElectionP2p.TAG, "Enviando o questionário para o líder");
                QuizData quizData = new QuizData();
                quizData.message = QuizData.LOAD_QUIZ;
                quizData.questionnaire = questionnaire;
                mainActivity.bullyElectionP2p.sendToLeader(quizData);
            } else {
                Toast.makeText(mainActivity, "Por favor, carregue um questionário",
                        Toast.LENGTH_SHORT).show();
            }
            isSendQuestionnaireToLeaderEvent = false;
        }
    }

    private DaoSession getAppDaoSession() {
        return ((AulaP2PProfessorApp) mainActivity.getApplication()).getDaoSession();
    }
}

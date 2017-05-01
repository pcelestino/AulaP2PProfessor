package br.edu.ffb.pedro.aulap2pprofessor.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import br.edu.ffb.pedro.aulap2pprofessor.AulaP2PProfessorApp;
import br.edu.ffb.pedro.aulap2pprofessor.R;
import br.edu.ffb.pedro.aulap2pprofessor.Utils;
import br.edu.ffb.pedro.aulap2pprofessor.callback.OnProfessorDialogClickOk;
import br.edu.ffb.pedro.aulap2pprofessor.event.QuizDataEvent;
import br.edu.ffb.pedro.aulap2pprofessor.fragment.QuizFragment;
import br.edu.ffb.pedro.aulap2pprofessor.fragment.ReportFragment;
import br.edu.ffb.pedro.aulap2pprofessor.fragment.StudentsFragment;
import br.edu.ffb.pedro.aulap2pprofessor.model.DaoSession;
import br.edu.ffb.pedro.aulap2pprofessor.model.Question;
import br.edu.ffb.pedro.aulap2pprofessor.model.Questionnaire;
import br.edu.ffb.pedro.aulap2pprofessor.model.QuizData;
import br.edu.ffb.pedro.bullyelectionp2p.BullyElectionP2p;
import br.edu.ffb.pedro.bullyelectionp2p.callback.OnWifiRestarted;
import br.edu.ffb.pedro.bullyelectionp2p.event.DataTransferEvent;
import br.edu.ffb.pedro.bullyelectionp2p.event.ServerEvent;
import br.edu.ffb.pedro.bullyelectionp2p.payload.Payload;

public class MainActivity extends AppCompatActivity {

    static final String PROFESSOR_PREFERENCES = "PROFESSOR_PREFERENCES";
    static final String PROFESSOR_NAME = "PROFESSOR_NAME";

    public BullyElectionP2p bullyElectionP2p;
    private LinearLayout container;
    private String professorInputName = "";
    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private ProgressDialog restartDialog;
    private boolean isChangeNameEvent;
    private ProgressDialog finishDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        removeAllQuestionnaires();
        EventBus.getDefault().register(this);
        setupLayout();

        professorInputName = getStudentPreferences().getString(PROFESSOR_NAME, "");
        if (professorInputName.isEmpty()) {
            Utils.showProfessorInputNameDialog(MainActivity.this, new OnProfessorDialogClickOk() {
                @Override
                public void onClick(AlertDialog dialog, String professorName) {
                    professorInputName = professorName;
                    SharedPreferences.Editor editor = getStudentPreferences().edit();
                    editor.putString(PROFESSOR_NAME, professorInputName);
                    editor.apply();

                    if (professorInputName.isEmpty()) {
                        Toast.makeText(MainActivity.this, R.string.please_insert_your_name,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        toolbar.setVisibility(View.VISIBLE);
                        navigation.setVisibility(View.VISIBLE);
                        container.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.transparent));
                        setupEasyP2P(professorInputName);
                        displaySelectedScreen(R.id.nav_students_list);
                        dialog.dismiss();
                    }
                }
            });
        } else {
            toolbar.setVisibility(View.VISIBLE);
            navigation.setVisibility(View.VISIBLE);
            container.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.transparent));
            setupEasyP2P(professorInputName);
            displaySelectedScreen(R.id.nav_students_list);
        }
    }

    public SharedPreferences getStudentPreferences() {
        return getApplicationContext().getSharedPreferences(PROFESSOR_PREFERENCES, MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Deseja realmente sair?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.dismiss();
                                finishDialog = ProgressDialog.show(MainActivity.this, "Finalizando sessão...", "Finalizando");
                                bullyElectionP2p.stopNetworkService(false);
                            }
                        })
                        .setNegativeButton("Não", null)
                        .create();
                alertDialog.show();
                return true;
            case R.id.change_professor_name:
                Utils.showProfessorChangeNameDialog(MainActivity.this, professorInputName, new OnProfessorDialogClickOk() {
                    @Override
                    public void onClick(AlertDialog dialog, String professorName) {
                        professorInputName = professorName;
                        SharedPreferences.Editor editor = getStudentPreferences().edit();
                        editor.putString(PROFESSOR_NAME, professorInputName);
                        editor.apply();
                        dialog.dismiss();

                        isChangeNameEvent = true;
                        restartDialog = ProgressDialog
                                .show(MainActivity.this, "Reiniciando sessão...",
                                        "Reiniciando");
                        bullyElectionP2p.stopNetworkService(false);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);

        container = (LinearLayout) findViewById(R.id.container);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setVisibility(View.GONE);
    }

    @SuppressLint("InflateParams")
    private void showProfessorInputNameDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.professor_input_name_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText studentInputNameDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(R.string.send, null);

        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();

        alertDialogAndroid.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        professorInputName = studentInputNameDialogEditText.getText().toString();
                        if (professorInputName.isEmpty()) {
                            Toast.makeText(MainActivity.this, R.string.please_insert_your_name,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            toolbar.setVisibility(View.VISIBLE);
                            navigation.setVisibility(View.VISIBLE);
                            container.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.transparent));
                            setupEasyP2P(professorInputName);
                            displaySelectedScreen(R.id.nav_students_list);
                            alertDialogAndroid.dismiss();
                        }
                    }
                });
            }
        });

        alertDialogAndroid.show();
    }

    private void setupEasyP2P(String professorName) {
        bullyElectionP2p = new BullyElectionP2p(this, professorName);
        bullyElectionP2p.startNetworkService();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataTransferEvent(DataTransferEvent dataTransferEvent) {
        String data = dataTransferEvent.data;
        switch (dataTransferEvent.event) {
            case DataTransferEvent.DATA_RECEIVED:
                Log.d(BullyElectionP2p.TAG, "Dados recebidos:\n" + dataTransferEvent.data);

                try {
                    Payload payload = LoganSquare.parse(data, Payload.class);
                    switch (payload.type) {
                        case QuizData.TYPE:
                            QuizData quizData = LoganSquare.parse(data, QuizData.class);
                            switch (quizData.message) {
                                case QuizData.RESPONSE_QUIZ:
                                    // É necessário criar um novo objeto para que o id seja atualizado pelo autoincrement
                                    saveNewReport(quizData.questionnaire);
                                    EventBus.getDefault().post(new QuizDataEvent(QuizDataEvent.QUESTIONNAIRE_RECEIVED));
                                    Toast.makeText(MainActivity.this, "Questionário recebido de " +
                                            quizData.questionnaire.getStudentName(), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            break;
                    }
                } catch (IOException e) {
                    Log.e(BullyElectionP2p.TAG, "Falha ao serializar o arquivo JSON", e);
                }
                break;
        }
    }

    private void saveNewReport(Questionnaire questionnaire) {
        // O id do questionário também é o id do dispositivo do aluno
        getAppDaoSession().getQuestionnaireDao().insertOrReplace(questionnaire);

        List<Question> questions = questionnaire.getQuestions();
        getAppDaoSession().getQuestionDao().insertOrReplaceInTx(questions);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerEvent(ServerEvent serverEvent) {
        switch (serverEvent.event) {
            case ServerEvent.SERVER_CLOSED:
                if (isChangeNameEvent) {
                    Utils.restartWifi(MainActivity.this, new OnWifiRestarted() {
                        @Override
                        public void call() {
                            restartDialog.dismiss();
                            recreate();
                        }
                    });
                } else {
                    Utils.restartWifi(MainActivity.this, new OnWifiRestarted() {
                        @Override
                        public void call() {
                            finishDialog.dismiss();
                            finish();
                        }
                    });
                }
                break;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return displaySelectedScreen(item.getItemId());
        }
    };

    private boolean displaySelectedScreen(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.nav_students_list:
                fragment = new StudentsFragment();
                break;
            case R.id.nav_quiz:
                fragment = new QuizFragment();
                break;
            case R.id.nav_class:
                fragment = new ReportFragment();
                break;
        }
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, fragment)
                    .commit();
            return true;
        } else {
            return false;
        }
    }

    private void removeAllQuestionnaires() {
        // Remove todas as questões
        getAppDaoSession().getQuestionDao().deleteAll();
        // Remove o questionário padrão
        getAppDaoSession().getQuestionnaireDao().deleteAll();
    }

    private DaoSession getAppDaoSession() {
        return ((AulaP2PProfessorApp) getApplication()).getDaoSession();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

package br.edu.ffb.pedro.aulap2pprofessor.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import br.edu.ffb.pedro.aulap2pprofessor.AulaP2PProfessorApp;
import br.edu.ffb.pedro.aulap2pprofessor.R;
import br.edu.ffb.pedro.aulap2pprofessor.activity.MainActivity;
import br.edu.ffb.pedro.aulap2pprofessor.custom.CheckboxGroup;
import br.edu.ffb.pedro.bullyelectionp2p.BullyElectionP2p;
import br.edu.ffb.pedro.bullyelectionp2p.BullyElectionP2pDevice;

public class Quiz {

    private Activity activity;
    private LinearLayout.LayoutParams fillWidthWrappedLayoutParam;
    private LinearLayout.LayoutParams cvLayoutParams;
    private LinearLayout.LayoutParams vLastQuestionLayoutParams;

    public Quiz(Activity activity) {
        this.activity = activity;

        fillWidthWrappedLayoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        cvLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cvLayoutParams.setMargins(16, 16, 16, 16);

        vLastQuestionLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        vLastQuestionLayoutParams.setMargins(16, 16, 16, 170);
    }

    private int integerConcat(int x, int y) {
        int multiplier = 1;

        do {
            multiplier *= 10;
        } while (multiplier <= y);

        return x * multiplier + y;
    }


    private View getSingleChoiceView(Question question, boolean enableReport) {

        int questionId = question.getId().intValue();

        @SuppressLint("InflateParams")
        CardView cvSingleChoice = (CardView) activity.getLayoutInflater().inflate(R.layout.quiz_single_choice, null);
        cvSingleChoice.setId(integerConcat(questionId, 0));
        cvSingleChoice.setLayoutParams(cvLayoutParams);

        TextView tvSingleChoiceTitle = (TextView) cvSingleChoice.findViewById(R.id.tv_single_choice_title);
        tvSingleChoiceTitle.setText(question.getTitle());
        tvSingleChoiceTitle.setPadding(28, 28, 28, 0);

        RadioGroup rgSingleChoice = (RadioGroup) cvSingleChoice.findViewById(R.id.rg_single_choice);
        rgSingleChoice.setId(integerConcat(questionId, 1));
        rgSingleChoice.setPadding(20, 0, 20, 20);

        View scoreContainer = cvSingleChoice.findViewById(R.id.llScoreContainer);
        TextView tvStudentReportScoreValue = (TextView) scoreContainer.findViewById(R.id.tvStudentReportScoreValue);

        List<String> choices = question.getChoices();
        for (int i = 0; i < choices.size(); i++) {
            String choice = choices.get(i);
            RadioButton rbChoice = new RadioButton(cvSingleChoice.getContext());
            rbChoice.setId(integerConcat(questionId, i + 2));
            rbChoice.setText(choice);
            rbChoice.setPadding(5, 5, 5, 5);
            rbChoice.setLayoutParams(fillWidthWrappedLayoutParam);
            rbChoice.setEnabled(false);
            if (enableReport) {
                scoreContainer.setVisibility(View.VISIBLE);
                tvStudentReportScoreValue.setText(String.valueOf(question.getScore()));

                if (question.getRightChoices().contains(choice)) {
                    rbChoice.setTextColor(ContextCompat.getColor(activity, R.color.accent));
                }
                for (String selectedChoice : question.getSelectedChoices()) {
                    if (selectedChoice.equals(choice) && question.getRightChoices().contains(selectedChoice)) {
                        rbChoice.setChecked(true);
                        rbChoice.setTextColor(ContextCompat.getColor(activity, R.color.accent));
                    } else if (selectedChoice.equals(choice)) {
                        rbChoice.setChecked(true);
                        rbChoice.setTextColor(ContextCompat.getColor(activity, R.color.red));
                    }
                }
            } else {
                if (question.getRightChoices().contains(choice)) rbChoice.setChecked(true);
                scoreContainer.setVisibility(View.GONE);
            }
            rgSingleChoice.addView(rbChoice);
        }
        return cvSingleChoice;
    }

    private View getMultipleChoiceView(Question question, boolean enableReport) {

        int questionId = question.getId().intValue();

        @SuppressLint("InflateParams")
        CardView cvMultipleChoice = (CardView) activity.getLayoutInflater().inflate(R.layout.quiz_multiple_choice, null);
        cvMultipleChoice.setId(integerConcat(questionId, 0));
        cvMultipleChoice.setLayoutParams(cvLayoutParams);

        TextView tvMultipleChoiceTitle = (TextView) cvMultipleChoice.findViewById(R.id.tv_multiple_choice_title);
        tvMultipleChoiceTitle.setText(question.getTitle());
        tvMultipleChoiceTitle.setPadding(28, 28, 28, 0);

        CheckboxGroup cbgMultipleChoice = (CheckboxGroup) cvMultipleChoice.findViewById(R.id.cbg_multiple_choice);
        cbgMultipleChoice.setId(integerConcat(questionId, 1));
        cbgMultipleChoice.setOrientation(LinearLayout.VERTICAL);
        cbgMultipleChoice.setPadding(20, 0, 20, 20);

        View scoreContainer = cvMultipleChoice.findViewById(R.id.llScoreContainer);
        TextView tvStudentReportScoreValue = (TextView) scoreContainer.findViewById(R.id.tvStudentReportScoreValue);

        List<String> choices = question.getChoices();
        for (String choice : choices) {
            CheckBox cbChoice = new CheckBox(cvMultipleChoice.getContext());
            cbChoice.setText(choice);
            cbChoice.setLayoutParams(fillWidthWrappedLayoutParam);
            cbChoice.setEnabled(false);
            if (enableReport) {
                scoreContainer.setVisibility(View.VISIBLE);
                tvStudentReportScoreValue.setText(String.valueOf(question.getScore()));

                if (question.getRightChoices().contains(choice)) {
                    cbChoice.setTextColor(ContextCompat.getColor(activity, R.color.accent));
                }
                for (String selectedChoice : question.getSelectedChoices()) {
                    if (selectedChoice.equals(choice) && question.getRightChoices().contains(selectedChoice)) {
                        cbChoice.setChecked(true);
                        cbChoice.setTextColor(ContextCompat.getColor(activity, R.color.accent));
                    } else if (selectedChoice.equals(choice)) {
                        cbChoice.setChecked(true);
                        cbChoice.setTextColor(ContextCompat.getColor(activity, R.color.red));
                    }
                }
            } else {
                if (question.getRightChoices().contains(choice)) cbChoice.setChecked(true);
                scoreContainer.setVisibility(View.GONE);
            }
            cbgMultipleChoice.addView(cbChoice);
        }

        return cvMultipleChoice;
    }

    public void addQuiz(LinearLayout quizContainer, final Questionnaire questionnaire) {
        quizContainer.removeAllViews();
        // Inicialmente as questões estarão nulas até utilizar o método getQuestions, Lazy
        List<Question> questions = questionnaire.getQuestions();
        if (questions != null && questions.size() > 0) {
            Log.d(BullyElectionP2p.TAG, "Questionário: " + questions.toString());

            TextView questionnaireTitle = new TextView(activity);
            questionnaireTitle.setGravity(Gravity.CENTER_HORIZONTAL);
            questionnaireTitle.setLayoutParams(fillWidthWrappedLayoutParam);
            questionnaireTitle.setPadding(16, 25, 16, 25);
            questionnaireTitle.setTextSize(23);
            questionnaireTitle.setText(questionnaire.getName());

            quizContainer.addView(questionnaireTitle);

            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);

                View questionView = question.getType().equals(Question.SINGLE_CHOICE) ?
                        getSingleChoiceView(question, false) :
                        getMultipleChoiceView(question, false);

                if (i == questions.size() - 1)
                    questionView.setLayoutParams(vLastQuestionLayoutParams);

                quizContainer.addView(questionView);
            }

            saveQuestionnaire(questionnaire);

        } else {
            Toast.makeText(quizContainer.getContext(),
                    "Por favor selecione um questionário válido", Toast.LENGTH_SHORT).show();
        }
    }

    public void addQuizReport(LinearLayout quizContainer, final Questionnaire questionnaire) {
        // Inicialmente as questões estarão nulas até utilizar o método getQuestions, Lazy
        List<Question> questions = questionnaire.getQuestions();
        if (questions != null && questions.size() > 0) {

//            TextView questionnaireTitle = new TextView(activity);
//            questionnaireTitle.setGravity(Gravity.CENTER_HORIZONTAL);
//            questionnaireTitle.setLayoutParams(fillWidthWrappedLayoutParam);
//            questionnaireTitle.setPadding(16, 25, 16, 25);
//            questionnaireTitle.setTextSize(23);
//            questionnaireTitle.setText(questionnaire.getName());
//
//            quizContainer.addView(questionnaireTitle);

            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);

                View questionView = question.getType().equals(Question.SINGLE_CHOICE) ?
                        getSingleChoiceView(question, true) :
                        getMultipleChoiceView(question, true);

                if (i == questions.size() - 1)
                    questionView.setLayoutParams(vLastQuestionLayoutParams);

                quizContainer.addView(questionView);
            }
        } else {
            Toast.makeText(quizContainer.getContext(),
                    "Por favor selecione um questionário válido", Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> getSingleChoiceCheckedValue(View singleChoiceContainer, int questionId) {
        RadioGroup rgSingleChoice = (RadioGroup) singleChoiceContainer.findViewById(integerConcat(questionId, 1));
        int checkedRadioButtonId = rgSingleChoice.getCheckedRadioButtonId();
        RadioButton rbSingleChoice = (RadioButton) rgSingleChoice.findViewById(checkedRadioButtonId);
        String checkedRadioButtonChoice = rbSingleChoice != null ? rbSingleChoice.getText().toString() : "";
        return Collections.singletonList(checkedRadioButtonChoice);
    }

    private List<String> getMultipleChoiceCheckedValue(View multipleChoiceContainer, int questionId) {
        CheckboxGroup cbgMultipleChoice = (CheckboxGroup) multipleChoiceContainer.findViewById(integerConcat(questionId, 1));
        return cbgMultipleChoice.getCheckedValues();
    }

    public Questionnaire getQuizResponse(LinearLayout quizContainer, Questionnaire questionnaire) {
        List<Question> questions = questionnaire.getQuestions();
        for (Question question : questions) {
            int questionId = question.getId().intValue();
            View choiceView = quizContainer.findViewById(integerConcat(questionId, 0));
            question.setSelectedChoices(question.getType().equals(Question.SINGLE_CHOICE) ?
                    getSingleChoiceCheckedValue(choiceView, questionId) :
                    getMultipleChoiceCheckedValue(choiceView, questionId));
        }
        Log.d(BullyElectionP2p.TAG, "Questionário de resposta: " + questionnaire.toString());
        return questionnaire;
    }

    public Questionnaire getQuizResult(Questionnaire questionnaire) {
        BullyElectionP2pDevice thisDevice = getThisDevice();
        questionnaire.setId(thisDevice.id + 1);

        List<Question> questions = questionnaire.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            question.setId(questionnaire.getId() + (i + 1));
            question.setQuestionnaireId(questionnaire.getId());

            if (question.getType().equals(Question.SINGLE_CHOICE)) {
                List<String> selectedChoices = question.getSelectedChoices();
                for (String choice : selectedChoices) {
                    if (question.getRightChoices().contains(choice)) {
                        question.setScore(question.getValue());
                        questionnaire.setOverallScore(questionnaire.getOverallScore() + question.getValue());
                    } else {
                        float questionPenalty = question.getErrorPenalty();
                        question.setScore(question.getScore() - questionPenalty);
                        questionnaire.setOverallScore(questionnaire.getOverallScore() - questionPenalty);
                    }
                }
            } else {
                List<String> selectedChoices = question.getSelectedChoices();
                for (String choice : selectedChoices) {
                    if (question.getRightChoices().contains(choice)) {
                        float questionScore = question.getValue() / question.getRightChoices().size();
                        question.setScore(question.getScore() + questionScore);
                        questionnaire.setOverallScore(questionnaire.getOverallScore() + questionScore);
                    } else {
                        float questionPenalty = question.getErrorPenalty();
                        question.setScore(question.getScore() - questionPenalty);
                        questionnaire.setOverallScore(questionnaire.getOverallScore() - questionPenalty);
                    }
                }
            }
            Log.d(BullyElectionP2p.TAG, "Resultado da Questão:" + question.getTitle() +
                    "\nPontuação: " + question.getScore());
        }
        Log.d(BullyElectionP2p.TAG, "Resultado do questionário: " + questionnaire.getName() +
                "\nPontuação: " + questionnaire.getOverallScore());
        return questionnaire;
    }

    private void saveQuestionnaire(Questionnaire questionnaire) {
        removeDefaultQuestionnaire();
        getAppDaoSession().getQuestionnaireDao().insertOrReplace(questionnaire);
        getAppDaoSession().getQuestionDao().insertOrReplaceInTx(questionnaire.getQuestions(), false);
    }

    private void removeDefaultQuestionnaire() {
        // Remove todos as questões relacionadas com o questionário padrão
        getAppDaoSession().getQuestionDao().queryBuilder()
                .where(QuestionDao.Properties.QuestionnaireId.eq(Questionnaire.DEFAULT_QUESTIONNAIRE))
                .buildDelete().executeDeleteWithoutDetachingEntities();

        // Remove o questionário padrão
        getAppDaoSession().getQuestionnaireDao().queryBuilder()
                .where(QuestionnaireDao.Properties.Id.eq(Questionnaire.DEFAULT_QUESTIONNAIRE))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    private DaoSession getAppDaoSession() {
        return ((AulaP2PProfessorApp) activity.getApplication()).getDaoSession();
    }

    private BullyElectionP2pDevice getThisDevice() {
        return ((MainActivity) activity).bullyElectionP2p.thisDevice;
    }
}

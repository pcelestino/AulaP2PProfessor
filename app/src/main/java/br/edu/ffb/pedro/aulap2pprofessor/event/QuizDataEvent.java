package br.edu.ffb.pedro.aulap2pprofessor.event;

public class QuizDataEvent {
    public static final String QUESTIONNAIRE_RECEIVED = "QUESTIONNAIRE_RECEIVED";

    public final String event;

    public QuizDataEvent(String event) {
        this.event = event;
    }
}

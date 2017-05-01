package br.edu.ffb.pedro.aulap2pprofessor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arasthel.asyncjob.AsyncJob;

import br.edu.ffb.pedro.aulap2pprofessor.callback.OnProfessorDialogClickOk;
import br.edu.ffb.pedro.bullyelectionp2p.BullyElectionP2p;
import br.edu.ffb.pedro.bullyelectionp2p.callback.OnWifiRestarted;

public class Utils {

    @SuppressLint("InflateParams")
    public static void showProfessorInputNameDialog(Context context, final OnProfessorDialogClickOk onClickOk) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.professor_input_name_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
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
                        String studentName = studentInputNameDialogEditText.getText().toString();
                        onClickOk.onClick(alertDialogAndroid, studentName);
                    }
                });
            }
        });

        alertDialogAndroid.show();
    }

    @SuppressLint("InflateParams")
    public static void showProfessorChangeNameDialog(Context context, String currentProfessorName, final OnProfessorDialogClickOk onClickOk) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View dialogView = layoutInflaterAndroid.inflate(R.layout.professor_input_name_change_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(dialogView);

        Resources resources = context.getResources();
        String dialogTitle = String.format(resources.getString(
                R.string.dialog_change_professor_name_description), currentProfessorName);

        TextView tvDialogTitle = (TextView) dialogView.findViewById(R.id.dialogTitle);
        tvDialogTitle.setText(dialogTitle);

        final EditText studentInputNameDialogEditText = (EditText) dialogView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(R.string.change, null)
                .setNegativeButton(R.string.cancel, null);

        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();

        alertDialogAndroid.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String studentName = studentInputNameDialogEditText.getText().toString();
                        onClickOk.onClick(alertDialogAndroid, studentName);
                    }
                });
            }
        });

        alertDialogAndroid.show();
    }

    public static void restartWifi(final Context context, final OnWifiRestarted onWifiRestarted) {

        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
                try {
                    AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                        @Override
                        public void doInUIThread() {
                            BullyElectionP2p.disableWiFi(context);
                        }
                    });

                    Thread.sleep(1000);


                    AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                        @Override
                        public void doInUIThread() {
                            BullyElectionP2p.enableWiFi(context);
                        }
                    });

                    Thread.sleep(8000);

                    AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                        @Override
                        public void doInUIThread() {
                            onWifiRestarted.call();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

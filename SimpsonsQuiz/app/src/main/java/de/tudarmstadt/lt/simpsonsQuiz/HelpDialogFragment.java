package de.tudarmstadt.lt.simpsonsQuiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Stefan on 14.10.2015.
 */
public class HelpDialogFragment extends DialogFragment {

    public static final int CONTENT_ALL = 0;
    public static final int CONTENT_SP_GAME = 1;
    public static final int CONTENT_MP_GAME = 2;
    public static final int CONTENT_CREATE_QUESTION = 3;
    public static final int CONTENT_REVIEW = 4;
    public static final int CONTENT_HIGHSCORE = 5;
    public static final int CONTENT_QA = 6;

    public static HelpDialogFragment newInstance(int helpContent) {
        HelpDialogFragment f = new HelpDialogFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("helpContent", helpContent);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_help, null);
        int argument = (getArguments() != null) ? getArguments().getInt("helpContent", CONTENT_ALL) : CONTENT_ALL;
        switch (argument) {
            case CONTENT_CREATE_QUESTION:
                view.findViewById(R.id.help_container_question_creation).setVisibility(View.VISIBLE);
                break;
            case CONTENT_HIGHSCORE:
                view.findViewById(R.id.help_container_highscore).setVisibility(View.VISIBLE);
                break;
            case CONTENT_MP_GAME:
                view.findViewById(R.id.help_container_mp_game).setVisibility(View.VISIBLE);
                break;
            case CONTENT_QA:
                view.findViewById(R.id.help_container_qa_).setVisibility(View.VISIBLE);
                break;
            case CONTENT_REVIEW:
                view.findViewById(R.id.help_container_review).setVisibility(View.VISIBLE);
                break;
            case CONTENT_SP_GAME:
                view.findViewById(R.id.help_container_sp_game).setVisibility(View.VISIBLE);
                break;
            default:
                view.findViewById(R.id.help_container_highscore).setVisibility(View.VISIBLE);
                view.findViewById(R.id.help_container_mp_game).setVisibility(View.VISIBLE);
                view.findViewById(R.id.help_container_qa).setVisibility(View.VISIBLE);
                view.findViewById(R.id.help_container_question_creation).setVisibility(View.VISIBLE);
                view.findViewById(R.id.help_container_review).setVisibility(View.VISIBLE);
                view.findViewById(R.id.help_container_sp_game).setVisibility(View.VISIBLE);
                break;
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view).setTitle("Help")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}

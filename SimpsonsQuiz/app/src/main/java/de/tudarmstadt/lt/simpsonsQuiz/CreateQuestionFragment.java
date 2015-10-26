package de.tudarmstadt.lt.simpsonsQuiz;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class CreateQuestionFragment extends Fragment {

    View view;

    public CreateQuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_enter_question, container, false);

        view.findViewById(R.id.enter_question_generate_answers_btn).getBackground().clearColorFilter();
        return view;
    }

    public void setHint(String hint) {
        TextView hintView = ((TextView) view.findViewById(R.id.enter_question_hint_cat));
        hintView.setText(hint);
        hintView.setVisibility(View.VISIBLE);
    }
}

package org.centum.android.molescan.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.centum.android.molescan.MainActivity;
import org.centum.android.molescan.R;

/**
 * Created by Phani on 9/5/2014.
 */
public class IntroFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button proceedButton;
    private CheckBox agreeCheckBox, disagreeCheckBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro, container, false);

        proceedButton = (Button) rootView.findViewById(R.id.proceed_button);
        agreeCheckBox = (CheckBox) rootView.findViewById(R.id.agree_checkBox);
        disagreeCheckBox = (CheckBox) rootView.findViewById(R.id.disagree_checkBox);

        proceedButton.setOnClickListener(this);
        agreeCheckBox.setOnCheckedChangeListener(this);
        disagreeCheckBox.setOnCheckedChangeListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().getActionBar().hide();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            getActivity().getActionBar().show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.proceed_button:
                proceedAction();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.agree_checkBox) {
            disagreeCheckBox.setChecked(!isChecked);
        } else {
            agreeCheckBox.setChecked(!isChecked);
        }
    }

    private void proceedAction() {
        if (disagreeCheckBox.isChecked()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Disclaimer");
            builder.setMessage("Please agree to the disclaimer to use this app!");
            builder.show();
        } else if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).switchToImageFragment();
        }
    }
}

package org.centum.android.molescan.dialogs;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.centum.android.molescan.R;
import org.centum.android.molescan.network.ParseInterface;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Phani on 9/6/2014.
 */
public class CommentDialogFragment extends DialogFragment implements View.OnClickListener {

    private ParseObject comment;

    private Button okButton;
    private TextView commentsTextView;
    private TextView fromTextView;
    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_dialog, container);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCancelable(false);

        okButton = (Button) view.findViewById(R.id.ok_button);
        commentsTextView = (TextView) view.findViewById(R.id.comments_textView);
        fromTextView = (TextView) view.findViewById(R.id.name_textView);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        okButton.setOnClickListener(this);

        updateInfo();

        return view;
    }

    private void updateInfo() {
        if (comment != null) {
            new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected void onPreExecute() {
                    fromTextView.setText((String) comment.get(ParseInterface.COMMENTS_FROM_KEY));
                    commentsTextView.setText((String) comment.get(ParseInterface.COMMENTS_KEY));
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                protected Bitmap doInBackground(Void... params) {
                    ParseFile file = comment.getParseFile(ParseInterface.IMAGE_KEY);
                    try {
                        File outFile = new File(Environment.getExternalStorageDirectory(), file.getName());
                        FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                        byte[] bytes = file.getData();
                        fileOutputStream.write(bytes);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        bytes = null;

                        return BitmapFactory.decodeFile(outFile.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    progressBar.setVisibility(View.GONE);
                    imageView.setImageBitmap(result);
                }
            }.execute();
        }
    }

    public void setComment(ParseObject comment) {
        this.comment = comment;
    }

    public ParseObject getComment() {
        return comment;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ParseInterface.get().markAsComplete(comment);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}

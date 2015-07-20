package org.centum.android.molescan.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;

import org.centum.android.molescan.R;
import org.centum.android.molescan.dialogs.CommentDialogFragment;
import org.centum.android.molescan.network.ParseInterface;
import org.centum.android.molescan.processing.ProcessingResults;
import org.centum.android.molescan.processing.Processor;

import java.io.File;
import java.util.List;

/**
 * Created by Phani on 9/6/2014.
 */
public class ImageFragment extends Fragment implements View.OnClickListener {

    private static final int GALLERY_IMAGE_REQUEST = 0;
    private static final int CAMERA_IMAGE_REQUEST = 1;

    private ProgressBar uploadProgressBar;
    private ImageButton cameraImageButton;
    private ImageButton galleryImageButton;
    private ImageButton checkImageButton;
    private TextView reselectButton;
    private TextView uploadButton;
    private TextView resultsTextView;
    private ImageView imageView;
    private RelativeLayout selectorRelativeLayout;
    private RelativeLayout imageSelectedRelativeLayout;

    private ProgressBar loadingProgressBar;

    private String cameraExtraFileOutPath = null;
    private String currentImage = null;
    private Bitmap currentBitmap = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);

        cameraImageButton = (ImageButton) rootView.findViewById(R.id.camera_imageButton);
        galleryImageButton = (ImageButton) rootView.findViewById(R.id.gallery_imageButton);
        checkImageButton = (ImageButton) rootView.findViewById(R.id.check_button);
        reselectButton = (TextView) rootView.findViewById(R.id.reselect_button);
        selectorRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.selectImage_relativeLayout);
        imageSelectedRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.imageSelected_relativeLayout);
        loadingProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        uploadButton = (TextView) rootView.findViewById(R.id.upload_button);
        uploadProgressBar = (ProgressBar) rootView.findViewById(R.id.upload_progressBar);
        resultsTextView = (TextView) rootView.findViewById(R.id.results_textView);

        cameraImageButton.setOnClickListener(this);
        galleryImageButton.setOnClickListener(this);
        reselectButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        checkImageButton.setOnClickListener(this);

        if (getActivity() != null && !getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            cameraImageButton.setVisibility(View.GONE);
        }

        if (currentImage == null) {
            selectorRelativeLayout.setVisibility(View.VISIBLE);
            imageSelectedRelativeLayout.setVisibility(View.GONE);
        } else {
            onImageSelected(currentImage);
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_imageButton:
                cameraActionPerformed();
                break;
            case R.id.gallery_imageButton:
                galleryActionPerformed();
                break;
            case R.id.reselect_button:
                reselectActionPerformed();
                break;
            case R.id.upload_button:
                if (uploadButton.isEnabled())
                    uploadActionPerformed();
                break;
            case R.id.check_button:
                checkForComments();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GALLERY_IMAGE_REQUEST:
                if (data != null && data.getData() != null && resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    Cursor cursor = getActivity().getContentResolver().query(uri,
                            new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    cursor.moveToFirst();
                    final String imageFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    cursor.close();
                    if (imageFilePath != null)
                        onImageSelected(imageFilePath);
                }
                break;
            case CAMERA_IMAGE_REQUEST:
                if (resultCode == Activity.RESULT_OK && cameraExtraFileOutPath != null) {
                    onImageSelected(cameraExtraFileOutPath);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForComments();
    }

    private void uploadActionPerformed() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                uploadButton.setEnabled(false);
                uploadButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                uploadProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ParseInterface.get().uploadImage(currentImage);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                uploadButton.setEnabled(true);
                uploadButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
                uploadProgressBar.setVisibility(View.GONE);

                Toast.makeText(getActivity(), R.string.upload_complete, Toast.LENGTH_LONG).show();
                reselectActionPerformed();
            }
        }.execute();
    }

    private void onImageSelected(String filePath) {
        selectorRelativeLayout.setVisibility(View.GONE);
        imageSelectedRelativeLayout.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        currentImage = filePath;

        new AsyncTask<Void, Void, ProcessingResults>() {

            @Override
            protected ProcessingResults doInBackground(Void... params) {
                currentBitmap = BitmapFactory.decodeFile(currentImage);
                return Processor.process(currentBitmap);
            }

            @Override
            protected void onPostExecute(ProcessingResults result) {
                loadingProgressBar.setVisibility(View.GONE);
                imageView.setImageBitmap(result.getRenderedBitmap());
                if (result.isColorPass() && result.isSizePass()) {
                    resultsTextView.setText("Your mole is regular\nYour mole is not irregularly colored\n" +
                            "Consult a professional for more info");
                } else if (!result.isColorPass() && !result.isSizePass()) {
                    resultsTextView.setText("Your mole is irregularly shaped\nYour mole is irregularly colored\n" +
                            "Consult a professional for more info");
                } else if (result.isColorPass() && !result.isSizePass()) {
                    resultsTextView.setText("Your mole is irregularly shaped\nYour mole is not irregularly colored\n" +
                            "Consult a professional for more info");
                } else if (!result.isColorPass() && result.isSizePass()) {
                    resultsTextView.setText("Your mole is regular\nYour mole is irregularly colored\n" +
                            "Consult a professional for more info");
                }
            }
        }.execute();

    }

    public void checkForComments() {
        new AsyncTask<Void, Void, List<ParseObject>>() {

            @Override
            protected void onPreExecute() {
                //Toast.makeText(getActivity(), R.string.checking_for_comments, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected List<ParseObject> doInBackground(Void... params) {
                try {
                    List<ParseObject> objs = ParseInterface.get().getNewComments();
                    return objs;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onPostExecute(List<ParseObject> objs) {
                if (objs.size() > 0) {
                    CommentDialogFragment dialog = new CommentDialogFragment();
                    dialog.setComment(objs.get(0));
                    dialog.show(getActivity().getFragmentManager(), "comment_dialog");
                } else {
                    //Toast.makeText(getActivity(), R.string.no_comments, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void cameraActionPerformed() {
        File cameraDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        cameraDir.mkdir();
        File imageFile;
        int num = 0;
        while ((imageFile = new File(cameraDir, "mole_sniffer_img_" + num + ".jpg")).exists()) {
            num++;
        }
        cameraExtraFileOutPath = imageFile.getAbsolutePath();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        intent.putExtra("return-data", false);
        startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
    }

    private void galleryActionPerformed() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GALLERY_IMAGE_REQUEST);
    }

    private void reselectActionPerformed() {
        selectorRelativeLayout.setVisibility(View.VISIBLE);
        imageSelectedRelativeLayout.setVisibility(View.GONE);

        imageView.setImageBitmap(null);
        currentImage = null;
        currentBitmap = null;
        resultsTextView.setText("");
    }
}

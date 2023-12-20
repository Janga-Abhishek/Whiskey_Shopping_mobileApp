package com.example.abhishek_janga_project2;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek_janga_project2.models.Upload;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

public class MainActivityAdmin extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    private EditText mEditDescription;
    private EditText mEditCost;
    private EditText mEditType;
    private ImageView mImageView;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextFileName = findViewById(R.id.edit_text_title);
        mImageView = findViewById(R.id.image_view);
        mEditDescription=findViewById(R.id.editText_desc);
        mEditCost=findViewById(R.id.editText_cost);
        mEditType=findViewById(R.id.editText_type);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(MainActivityAdmin.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();
            }
        });
        Button backButton = findViewById(R.id.backbtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {

            String titleInput = mEditTextFileName.getText().toString().trim();
            String descriptionInput = mEditDescription.getText().toString().trim();
            String costInput = mEditCost.getText().toString().trim();
            String typeInput = mEditType.getText().toString().trim();

            // Check if description field is empty
            if (descriptionInput.isEmpty()) {
                Toast.makeText(this, "Description can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate other fields for alphanumeric characters
            String alphanumericPattern = "^[a-zA-Z0-9]+$";

            if (!titleInput.matches(alphanumericPattern)
                    || !typeInput.matches(alphanumericPattern)) {
                Toast.makeText(this, "Invalid format. Only letters and numbers allowed in title, type.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate cost format (number with up to two decimal points)
            String costPattern = "^\\d+(\\.\\d{1,2})?$";
            if (!costInput.matches(costPattern)) {
                Toast.makeText(this, "Invalid cost format. Enter a number with up to two decimal points.", Toast.LENGTH_SHORT).show();
                return;
            }
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(MainActivityAdmin.this, "Upload successful", Toast.LENGTH_LONG).show();

                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            Upload upload = new Upload(mEditTextFileName.getText().toString().trim(), uri.toString(),mEditDescription.getText().toString().trim(),mEditCost.getText().toString().trim(),mEditType.getText().toString().trim());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(MainActivityAdmin.this, e.getMessage(), Toast.LENGTH_SHORT).show());

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void openImagesActivity() {
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);
    }
}
package com.m1.iot.carpool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.m1.iot.carpool.models.User;

import java.io.IOException;
import java.util.UUID;


/**
 * Created by Marta on 10/8/17.
 */

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText FirstEditText, LastEditText;
    private EditText EmailEditText, PasswordEditText;
    private EditText phoneEditText;
    private Button mSignUpButton;

    private ImageView profileImage;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 1;

    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        profileImage = (ImageView) findViewById(R.id.profile_image);
        FirstEditText = (EditText) findViewById(R.id.first_name);
        LastEditText = (EditText) findViewById(R.id.last_name);
        EmailEditText = (EditText) findViewById(R.id.create_email);
        PasswordEditText = (EditText) findViewById(R.id.create_password);
        phoneEditText = (EditText) findViewById(R.id.phone_number);

        mSignUpButton = findViewById(R.id.save_information);

        mSignUpButton.setOnClickListener(this);

        uploadProfileImage();
    }

    public void onStart() {
        super.onStart();

        //Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void SignUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = EmailEditText.getText().toString();
        String password = PasswordEditText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    onAuthSuccess(task.getResult().getUser());
                } else {
                    Toast.makeText(SignUpActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(EmailEditText.getText().toString())) {
            EmailEditText.setError("Required");
            result = false;
        } else {
            EmailEditText.setError(null);
        }

        if (TextUtils.isEmpty(PasswordEditText.getText().toString())) {
            PasswordEditText.setError("Required");
            result = false;
        } else {
            PasswordEditText.setError(null);
        }
        return result;
    }

    private void onAuthSuccess(FirebaseUser user) {
        String first = FirstEditText.getText().toString();
        String last = LastEditText.getText().toString();

        String phone = "";
        if (phoneEditText != null) {
            phone = phoneEditText.getText().toString();
        }


        String imageURL = "";

        if (filePath != null) {
            StorageReference ref = storageReference.child("profileImages/" + UUID.randomUUID().toString());
            ref.putFile(filePath);
            imageURL = UUID.randomUUID().toString();
//            writeNewUser(user.getUid(),user.getEmail(), first, last, phone, imageURL);
        }

        writeNewUser(user.getUid(),user.getEmail(), first, last, phone, imageURL);

        //Go to MainActivity
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.putExtra("callingActivity", "SignUpActivity");
        startActivity(intent);
        finish();
    }

    //Writes user's email in users table
    private void writeNewUser(String userId, String email, String first, String last, String phone, String filepath) {
        User user = new User(email, first, last, phone, filepath);

        mDatabase.child("users").child(userId).setValue(user);
    }

//    private void getUsername(String email){
//
//    }

    private void uploadProfileImage(){

        //When ImageView profileImage is pressed, user can choose image from their device gallery
        profileImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*"); //Intent type is set to image
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), PICK_IMAGE_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_information:
                SignUp();
                break;

        }
    }
}


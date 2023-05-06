package com.example.mobileproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobileproject.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    ImageView close, profileImage;
    TextView save, tv_change;
    MaterialEditText fullName, username, bio;

    FirebaseUser firebaseUser;
    private Uri imageUri;

    private StorageTask uploadTask;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.close);
        profileImage = findViewById(R.id.profile_image);
        save = findViewById(R.id.save);
        tv_change = findViewById(R.id.tv_change);
        fullName = findViewById(R.id.fullName);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                fullName.setText(user.getFullName());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(profileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(v -> finish());
        tv_change.setOnClickListener(v -> CropImage
                .activity()
                .setAspectRatio(1, 1)
                .start(EditProfileActivity.this));

        profileImage.setOnClickListener(v -> CropImage
                .activity()
                .setAspectRatio(1, 1)
                .start(EditProfileActivity.this));

        save.setOnClickListener(v ->
                updateProfile(fullName.getText().toString(), username.getText().toString(), bio.getText().toString()));


    }

    private void updateProfile(String fullName, String username, String bio) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("fullName", fullName);
        hashMap.put("username", username);
        hashMap.put("bio", bio);
        reference.updateChildren(hashMap);
        Toast.makeText(EditProfileActivity.this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = EditProfileActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        ProgressDialog pd = new ProgressDialog(EditProfileActivity.this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();

                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        String myUri = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("imageURL", "" + myUri);

                        reference.updateChildren(hashMap);
                        Toast.makeText(EditProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        pd.dismiss();

                    } else {
                        Toast.makeText(EditProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            });
        } else {
            Toast.makeText(EditProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            uploadImage();
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


}
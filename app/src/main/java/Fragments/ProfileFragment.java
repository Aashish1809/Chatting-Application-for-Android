package Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.R;
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
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import P1.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment{
    private static final int RESULT_OK = 1;
    private CircleImageView profile_image;
    private TextView name,email;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private static final int IMAGE = 1;
    private Uri imageURL;
    private Context mcontext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mcontext = getContext();
        profile_image = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        storageReference = FirebaseStorage.getInstance().getReference("File Uploads");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isAdded()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    name.setText(user.getUsername());
                    email.setText(user.getEmail());
                    if (user.getImageURL().equals("default"))
                        profile_image.setImageResource(R.drawable.dp);
                    else
                        Glide.with(mcontext).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageOpener();
            }
        });
        return view;
    }

    private void imageOpener() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,IMAGE);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageURL=data.getData();
        if(requestCode == IMAGE && resultCode == RESULT_OK && data.getData() != null)
            Toast.makeText(getContext(),"Uploading..",Toast.LENGTH_SHORT).show();
            else
                upload();
        }

    private void upload(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Please Wait");
        pd.show();

        if(imageURL!=null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageURL));
            StorageTask<UploadTask.TaskSnapshot> storageTask = fileReference.putFile(imageURL);
            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri download = task.getResult();
                        assert download != null;
                        String finalURL = download.toString();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL",finalURL);
                        reference.updateChildren(map);
                        pd.dismiss();
                    }
                    else{
                        pd.dismiss();
                        Toast.makeText(getContext(),"Failed to Upload!",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            Toast.makeText(getContext(),"Sele",Toast.LENGTH_SHORT).show();
        }

    }
}
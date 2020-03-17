package com.rubenmimoun.beerchallenge;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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
import com.rubenmimoun.beerchallenge.Models.User;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    private CircleImageView image_profil  ;
    private TextView username_profil ;

    private DatabaseReference db ;
    private FirebaseUser user ;

    private StorageReference storageReference ;
    private static final int IMAGE_REQUEST = 1 ;
    private Uri image_uri  ;
    private StorageTask upload_task ;




    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        image_profil =view.findViewById(R.id.profil_image_profileFrag);
        username_profil = view.findViewById(R.id.username_profile);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        user = FirebaseAuth.getInstance().getCurrentUser() ;
        db = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());




        db.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(isAdded()){
                    //Load image if the fragment is currently added to its activity.
                    User user = dataSnapshot.getValue(User.class) ;

                    assert user != null;
                    username_profil.setText(user.getName());

                    if(user.getImageURL().equals("default")){
                        image_profil.setImageResource(R.drawable.unknown);
                    }else {
                        Glide.with(getContext()).load(user.getImageURL()).into(image_profil);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        image_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openImage();

            }
        });



        return view ;
    }



    private void openImage(){

        Intent intent = new Intent() ;
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT) ;
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, IMAGE_REQUEST);

    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver =  getContext().getContentResolver() ;
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton() ;
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private  void uploadImage(){

        final ProgressDialog progressDialog = new ProgressDialog(getContext()) ;

        progressDialog.setMessage("Uploading ... ");
        progressDialog.show();

        if(image_uri != null){
            final StorageReference file_reference =
                    storageReference.child(System.currentTimeMillis()
                            +"."+getFileExtension(image_uri));

            upload_task = file_reference.putFile(image_uri);

            upload_task.continueWithTask(new Continuation< UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if( !task.isSuccessful()){
                        throw  task.getException() ;
                    }

                    return  file_reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task <Uri>task) {
                    if( task.isSuccessful()){

                        Uri download_uri = task.getResult() ;
                        String mUri = download_uri.toString() ;

                        db = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()) ;

                        HashMap<String, Object> hashMap =  new HashMap<>() ;

                        hashMap.put("imageURL",mUri) ;
                        db.updateChildren(hashMap) ;

                        progressDialog.dismiss();


                    }else{
                        Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });


        }else{

            Toast.makeText(getContext(),"No image selected", Toast.LENGTH_SHORT).show();


        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode  == IMAGE_REQUEST  && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            image_uri = data.getData() ;

            if(upload_task != null  && upload_task.isInProgress()){
                Toast.makeText(getContext(),"Upload in progress", Toast.LENGTH_SHORT).show();
            }else{
                uploadImage();
            }



        }
    }

}

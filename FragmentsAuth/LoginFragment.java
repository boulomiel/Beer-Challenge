package com.rubenmimoun.beerchallenge.FragmentsAuth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.rubenmimoun.beerchallenge.MainMenu;
import com.rubenmimoun.beerchallenge.Models.User;
import com.rubenmimoun.beerchallenge.R;


public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private User user ;

    private EditText email ;
    private EditText password ;
    private Button login ;
    // lazy loading
    ProgressDialog pb ;

    private DatabaseReference db ;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_login, container, false);

        email =  v.findViewById(R.id.et_email);
        password =v.findViewById(R.id.et_password);
        login =v.findViewById(R.id.btn_login);

        pb =  new ProgressDialog(getContext());
        pb.setTitle("Logging in ...");
        pb.setMessage("Please wait ...");


        mAuth = FirebaseAuth.getInstance() ;


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String email_txt = email.getText().toString();
                String passWord_txt = password.getText().toString();


                login(email_txt, passWord_txt) ;

            }
        });




        return  v ;
    }

    private void login(final String email, final String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if( !isEmailValid()){

                            Toast.makeText(getContext(),"Insert a valid Email", Toast.LENGTH_SHORT).show();

                        }else if (!isPasswordValid()){
                            Toast.makeText(getContext(),
                                    "Insert a valid  passsword, containing at least 6 characters",Toast.LENGTH_SHORT).show();
                        }else {

                            mAuth.signInWithEmailAndPassword(email,password)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {

                                            Intent intent  = new Intent(getContext(), MainMenu.class) ;
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    showError(e.getMessage());

                                }
                            });
                        }
                    }
                });



    }


    private String getPassWord(){
        return password.getText().toString() ;
    }

    private String getEmail(){
        return  email.getText().toString();
    }

    private boolean isEmailValid(){
        boolean valid = Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches() ;

        if(!valid){
            email.setError("Invalid email adress");
            email.setTextColor(Color.RED);
        }else{
            email.setError(null);
        }

        return valid ;
    }

    private boolean isPasswordValid(){
        boolean valid = getPassWord().length() >= 6 ;

        if(!valid){
            password.setError("Invalid password");
            password.setTextColor(Color.RED);
        }else{
            password.setError(null);
        }

        return valid ;
    }


    private void toggleProgress(boolean show){

        if(pb == null){
            pb.dismiss();
        }


        if(show){
            pb.show();
        }else{
            pb.dismiss();
        }

    }

    private void showError(String message){
        new AlertDialog.Builder(getContext())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }


}

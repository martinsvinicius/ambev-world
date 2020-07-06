package com.example.ambevworlds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    //Componentes
    EditText mEmail, mSenha;
    Button btnEntrar;
    ProgressBar progressBar;
    TextView textView;

    //Firebase
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadItens();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), register.class));
            }
        });


        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmail.getText().toString().trim();
                String senha = mSenha.getText().toString().trim();


                if(TextUtils.isEmpty(email)) {
                    mEmail.setError("O campo EMAIL é obrigatório");
                    return;
                }

                if(TextUtils.isEmpty(senha)) {
                    mSenha.setError("O campo SENHA é obrigatório");
                    return;
                }

                if(senha.length() < 6) {
                    mSenha.setError("Sua senha precisa ter 6 caracteres ou mais");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


                //auth user

                auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Login.this, "Sucesso!", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        }else {
                            Toast.makeText(Login.this, "Erro ao realizar login" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mSenha.setText("");
                        }
                    }
                });

            }
        });







    }

    private void loadItens() {
        mEmail = (EditText)findViewById(R.id.email_login);
        mSenha = (EditText)findViewById(R.id.senha_login);
        btnEntrar = (Button)findViewById(R.id.btn_entrar);
        textView = (TextView)findViewById(R.id.textView2);
        progressBar = (ProgressBar)findViewById(R.id.progressBar2);

        auth = FirebaseAuth.getInstance();

    }

}
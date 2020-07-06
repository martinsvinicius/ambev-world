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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class register extends AppCompatActivity {

    EditText mNome, mEmail, mSenha;
    Button btnCadastrar;
    TextView textLoginPage;
    ProgressBar progressBar;

    //FIREBASE
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNome = (EditText)findViewById(R.id.nome_cadastro);
        mEmail = (EditText)findViewById(R.id.email_cadastro);
        mSenha = (EditText)findViewById(R.id.senha_cadastro);
        btnCadastrar = (Button)findViewById(R.id.btn_cadastrar);
        textLoginPage = (TextView)findViewById(R.id.textLoginPage);

        auth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar)findViewById(R.id.progressBar);


        if(auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        }

        inicializarFirebase();

        textLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmail.getText().toString().trim();
                String nome = mNome.getText().toString().trim();
                String senha = mSenha.getText().toString().trim();

                if(TextUtils.isEmpty(nome)) {
                    mNome.setError("O campo NOME é obrigatório");
                    return;
                }

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

                auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Cliente cliente = new Cliente();
                            cliente.setId(auth.getCurrentUser().getUid());
                            cliente.setNome(mNome.getText().toString());
                            cliente.setEmail(mEmail.getText().toString());
                            cliente.setSenha(mSenha.getText().toString());
                            databaseReference.child("Clientes").child(cliente.getId()).setValue(cliente);

                            Toast.makeText(register.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), Login.class));

                        } else {
                            Toast.makeText(register.this, "Erro ao realizar cadastro" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(register.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

}
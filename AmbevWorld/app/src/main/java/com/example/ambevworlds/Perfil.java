package com.example.ambevworlds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Perfil extends AppCompatActivity {

    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView nomeView, emailView, tampinhasView;
    Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        inicializarFirebase();

        nomeView = (TextView) findViewById(R.id.textNomeview);
        emailView = (TextView) findViewById(R.id.textEmailView);
        tampinhasView = (TextView) findViewById(R.id.textPontosView);
        btnVoltar = (Button) findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            String nameUser = user.getDisplayName();
            String emailUser = user.getEmail();

            String uid = user.getUid();
        }

        databaseReference.child("Clientes").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String email = dataSnapshot.child("email").getValue().toString();
                String nome = dataSnapshot.child("nome").getValue().toString();
                String tampinhas = dataSnapshot.child("pontos").getValue().toString();

                nomeView.setText(nome);
                emailView.setText(email);
                tampinhasView.setText(tampinhas);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                nomeView.setText("Algo deu errado");
            }
        });


    }

    private void inicializarFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}
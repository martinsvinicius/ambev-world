package com.example.ambevworlds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class ComprarAcitivity extends AppCompatActivity implements DialogCompra.DialogCompraListener {

    private DatabaseReference mDatabase;

    private ListView lojaList;

    private ArrayList<String> descontosArray = new ArrayList<>();

    private String currentDesconto, currentPreco, currentBebida, currentId;

    public FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_acitivity);

        user = FirebaseAuth.getInstance().getCurrentUser();

        lojaList = (ListView) findViewById(R.id.lojaList);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, descontosArray);
        lojaList.setAdapter(arrayAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Loja").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String desconto = dataSnapshot.getKey();
                mDatabase.child("Loja").child(desconto);
                descontosArray.add(desconto);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        lojaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = String.valueOf(adapterView.getItemAtPosition(i));
                mDatabase.child("Loja").child(selectedItem).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot != null){
                            getDescontosInfo((Map<String,Object>) dataSnapshot.getValue());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }
    private void getDescontosInfo(Map<String,Object> infos){
        for (Map.Entry<String, Object> entry : infos.entrySet()){
            if(entry.getKey().contains("Desconto")){
                currentDesconto = String.valueOf(entry.getValue());

            }
            if(entry.getKey().contains("Preco")){
                currentPreco = String.valueOf(entry.getValue());

            }
            if(entry.getKey().contains("Bebida")){
                currentBebida = String.valueOf(entry.getValue());

            }
            if(entry.getKey().contains("Id")){
                currentId = String.valueOf(entry.getValue());

            }
        }
        openDialog();
    }
    public void openDialog(){
        Bundle args = new Bundle();
        args.putString("Desconto", currentDesconto);
        args.putString("Preco", currentPreco);
        args.putString("Bebida", currentBebida);
        args.putString("Id", currentId);
        DialogCompra dialogCompra = new DialogCompra();
        dialogCompra.setArguments(args);
        dialogCompra.show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void applyInfo(String preco, String id) {
        final String preco2 = preco;
        mDatabase.child("Clientes").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pontosUser = dataSnapshot.child("pontos").getValue().toString();
                int pontosUserInt = Integer.parseInt(pontosUser);
                int precoInt = Integer.parseInt(preco2);
                int aplicarPontos = pontosUserInt;

                if(pontosUserInt >= precoInt) {

                    aplicarPontos = pontosUserInt - precoInt;
                    Toast.makeText(ComprarAcitivity.this, "COMPRA REALIZADA COM SUCESSO!", Toast.LENGTH_LONG).show();

                } else if (pontosUserInt < precoInt) {
                    Toast.makeText(ComprarAcitivity.this, "VOCÊ NÃO TEM TAMPINHAS SUFICIENTES PARA ESTA COMPRA!", Toast.LENGTH_LONG).show();
                }

                mDatabase.child("Clientes").child(user.getUid()).child("pontos").setValue(aplicarPontos).toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

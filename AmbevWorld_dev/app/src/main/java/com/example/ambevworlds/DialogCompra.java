package com.example.ambevworlds;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DialogCompra extends AppCompatDialogFragment {

    public FirebaseUser user;
    public DatabaseReference databaseReference;

    private DialogCompraListener listener;
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle args = getArguments();
        String desconto = String.valueOf(args.get("Desconto"));
        final String preco = String.valueOf(args.get("Preco"));
        String bebida = String.valueOf(args.get("Bebida"));
        final String Id = String.valueOf(args.get("Id"));
        user = FirebaseAuth.getInstance().getCurrentUser();

        builder.setTitle("Compra").setMessage("Deseja resgatar o desconto de "+desconto+"% em " + bebida+" por "+preco+" pontos?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.applyInfo(preco, Id);
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();

    }

    @Override
    public void onAttach(Context c){
        super.onAttach(c);

        try {
            listener = (DialogCompraListener) c;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }
    public interface DialogCompraListener{
        void applyInfo(String preco, String id);
    }
}

package com.example.ambevworlds;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogCodigo extends AppCompatDialogFragment {
    private EditText editCodigo;

    private DialogCodigo.DialogCodigoListener listener;
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_codigo_dialog, null);

        builder.setView(view).setTitle("Adicionar produto").setMessage("Digite o código do produto consumido para resgatar as TAMPINHAS: ")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Pronto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String codigo = editCodigo.getText().toString();
                        listener.applyCodigo(codigo);
                    }
                });

        editCodigo = view.findViewById(R.id.edt_codigo);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogCodigoListener)context;
        } catch (Exception e) {
            throw new ClassCastException();
        }
    }

    public interface DialogCodigoListener{
        void applyCodigo(String codigo);
    }

}

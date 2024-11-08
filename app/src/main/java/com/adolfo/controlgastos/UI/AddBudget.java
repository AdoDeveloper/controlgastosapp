package com.adolfo.controlgastos.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.adolfo.controlgastos.Models.Presupuesto;
import com.adolfo.controlgastos.R;
import com.adolfo.controlgastos.UI.ViewModels.BudgetVM;
import com.adolfo.controlgastos.databinding.ActivityAddBudgetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class AddBudget extends BottomSheetDialogFragment {
    private ActivityAddBudgetBinding binding;
    private BudgetVM viewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(BudgetVM.class);
        // Forzar el tema claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityAddBudgetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnGuardarPresupuesto.setOnClickListener(v -> {
            String titulo = binding.edtATitulo.getText().toString();
            String montoStr = binding.edtAMonto.getText().toString();

            if (titulo.isEmpty() || montoStr.isEmpty()) {
                Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double monto = Double.parseDouble(montoStr);
                Presupuesto mObject = new Presupuesto(titulo, monto, true);

                viewModel.addBudget(
                        mObject,
                        documentReference -> {
                            this.dismiss();
                            Toast.makeText(getContext(), "Guardado correctamente el presupuesto", Toast.LENGTH_SHORT).show();
                        },
                        e -> {
                            Toast.makeText(getContext(), "Error no se guardó el presupuesto", Toast.LENGTH_SHORT).show();
                        }
                );
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Por favor ingresa un monto válido", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
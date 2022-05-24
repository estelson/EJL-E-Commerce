package com.exemplo.ejle_commerce.activity.loja;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.databinding.ActivityLojaFormPagamentoBinding;
import com.exemplo.ejle_commerce.model.FormaPagamento;

import java.util.Locale;

public class LojaFormPagamentoActivity extends AppCompatActivity {

    private ActivityLojaFormPagamentoBinding binding;

    private FormaPagamento formaPagamento;

    private String tipoValor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaFormPagamentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();

        configClicks();
    }

    private void configClicks() {
        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });

        binding.include.btnSalvar.setOnClickListener(v -> {
            validarDados();
        });

        binding.rgValor.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.rbDesconto) {
                tipoValor = "DESC";
            } else if(checkedId == R.id.rbAcrescimo) {
                tipoValor = "ACRES";
            } else {
                tipoValor = null;
            }
        });
    }

    private void validarDados() {
        String nome = binding.edtFormaPagamento.getText().toString().trim();
        String descricao = binding.edtDescricaoPagamento.getText().toString().trim();
        double valor = (double) binding.edtValor.getRawValue() / 100;

        if(!nome.isEmpty()) {
            if(!descricao.isEmpty()) {
                ocultarTeclado();

                binding.progressBar.setVisibility(View.VISIBLE);

                if(formaPagamento == null) {
                    formaPagamento = new FormaPagamento();
                }

                formaPagamento.setNome(nome);
                formaPagamento.setDescricao(descricao);
                formaPagamento.setValor(valor);
                formaPagamento.setTipoValor(tipoValor);

                if(formaPagamento.getTipoValor() != null) {
                    formaPagamento.salvar();

                    Toast.makeText(this, "Forma de pagamento incluída com sucesso", Toast.LENGTH_SHORT).show();

                    finish();
                } else {
                    binding.progressBar.setVisibility(View.GONE);

                    Toast.makeText(this, "Selecione um tipo de valor", Toast.LENGTH_SHORT).show();
                }
            } else {
                binding.edtDescricaoPagamento.requestFocus();
                binding.edtDescricaoPagamento.setError("Informação obrigatória");
            }
        } else {
            binding.edtFormaPagamento.requestFocus();
            binding.edtFormaPagamento.setError("Informação obrigatória");
        }
    }

    private void iniciarComponentes() {
        binding.include.textTitulo.setText("Forma de pagamento");

        binding.edtValor.setLocale(new Locale("PT", "br"));
    }

    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.edtFormaPagamento.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
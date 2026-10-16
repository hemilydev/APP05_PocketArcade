package com.hemily.pixelquest;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.hemily.pixelquest.databinding.ActivityScannerBinding;

import java.util.Locale;

public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = Constantes.TAG_CICLO;
    private static final int MINIMO_CARACTERES = 3;
    private static final String VOGAIS = "aeiouáàâãéêíóôõúü";

    private ActivityScannerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "Tela 3 (ScannerActivity) - onCreate");

        binding.btnAnalisar.setOnClickListener(v -> analisar());
        binding.btnLimpar.setOnClickListener(v -> limpar());
        binding.btnVoltarScanner.setOnClickListener(v -> finish());
        binding.btnEncerrarScanner.setOnClickListener(v ->
                Navegacao.encerrarSessao(this, getString(R.string.modulo_scanner)));
    }

    private void analisar() {
        String texto = binding.etMensagem.getText() == null
                ? ""
                : binding.etMensagem.getText().toString().trim();

        // Validações com mensagens para entradas inválidas
        if (texto.isEmpty()) {
            binding.tilMensagem.setError(getString(R.string.t3_erro_vazio));
            binding.painelResultado.setVisibility(View.GONE);
            return;
        }
        if (texto.length() < MINIMO_CARACTERES) {
            binding.tilMensagem.setError(getString(R.string.t3_erro_curto));
            binding.painelResultado.setVisibility(View.GONE);
            return;
        }
        binding.tilMensagem.setError(null);
        esconderTeclado();

        // As cinco medidas
        binding.tvResCaracteres.setText(String.valueOf(contarCaracteresSemEspacos(texto)));
        binding.tvResPalavras.setText(String.valueOf(contarPalavras(texto)));
        binding.tvResVogais.setText(String.valueOf(contarVogais(texto)));
        binding.tvResInvertido.setText(inverter(texto));
        binding.tvResPalindromo.setText(ehPalindromo(texto)
                ? getString(R.string.t3_sim)
                : getString(R.string.t3_nao));

        animarResultado();
    }

    private int contarCaracteresSemEspacos(String texto) {
        return texto.replaceAll("\\s+", "").length();
    }

    private int contarPalavras(String texto) {
        return texto.split("\\s+").length;
    }

    // Conta vogais com e sem acento, ignorando maiúsculas/minúsculas
    private int contarVogais(String texto) {
        int total = 0;
        for (char c : texto.toLowerCase(Locale.ROOT).toCharArray()) {
            if (VOGAIS.indexOf(c) >= 0) {
                total++;
            }
        }
        return total;
    }

    private String inverter(String texto) {
        return new StringBuilder(texto).reverse().toString();
    }

    // Ignora espaços e diferença entre maiúsculas e minúsculas
    private boolean ehPalindromo(String texto) {
        String normalizado = texto.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
        String invertido = new StringBuilder(normalizado).reverse().toString();
        return normalizado.equals(invertido);
    }

    // ViewPropertyAnimator: o painel aparece subindo (500 ms)
    private void animarResultado() {
        binding.painelResultado.setVisibility(View.VISIBLE);
        binding.painelResultado.setAlpha(0f);
        binding.painelResultado.setTranslationY(60f);
        binding.painelResultado.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .start();
    }

    private void limpar() {
        binding.etMensagem.setText("");
        binding.tilMensagem.setError(null);
        binding.painelResultado.setVisibility(View.GONE);
        binding.etMensagem.requestFocus();
    }

    private void esconderTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.etMensagem.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Tela 3 (ScannerActivity) - onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Tela 3 (ScannerActivity) - onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Tela 3 (ScannerActivity) - onDestroy");
    }
}
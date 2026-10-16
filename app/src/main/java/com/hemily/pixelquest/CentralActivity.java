package com.hemily.pixelquest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.hemily.pixelquest.databinding.ActivityCentralBinding;

public class CentralActivity extends AppCompatActivity {

    private static final String TAG = Constantes.TAG_CICLO;
    private static final long INTERVALO_ANIMACAO = 120L;

    private ActivityCentralBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCentralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "Tela 2 (CentralActivity) - onCreate");

        mostrarSaudacao();
        animarEntrada();
        configurarBotoes();
    }

    // Validação no destino: se o apelido não chegou, usa um padrão
    private void mostrarSaudacao() {
        String apelido = getIntent().getStringExtra(Constantes.EXTRA_APELIDO);
        if (apelido == null || apelido.trim().length() < 3) {
            apelido = getString(R.string.apelido_padrao);
        }
        binding.tvSaudacao.setText(getString(R.string.t2_saudacao, apelido));
    }

    // Animação XML (fade + translate), um painel depois do outro
    private void animarEntrada() {
        View[] paineis = {
                binding.tvSaudacao,
                binding.painelScanner,
                binding.painelArena,
                binding.painelCodigo,
                binding.painelRandom
        };
        for (int i = 0; i < paineis.length; i++) {
            Animation entrada = AnimationUtils.loadAnimation(this, R.anim.entrada_painel);
            entrada.setStartOffset(i * INTERVALO_ANIMACAO);
            paineis[i].startAnimation(entrada);
        }
    }

    private void configurarBotoes() {
        binding.btnAbrirScanner.setOnClickListener(v -> abrirDesafio(ScannerActivity.class));
        binding.btnAbrirArena.setOnClickListener(v -> abrirDesafio(ArenaActivity.class));
        binding.btnAbrirCodigo.setOnClickListener(v -> abrirDesafio(CodigoActivity.class));
        binding.btnAbrirRandom.setOnClickListener(v -> abrirDesafio(RandomizadorActivity.class));
    }

    // Intent explícita. A Central NÃO chama finish(): ela fica na pilha
    // e reaparece quando o desafio chamar finish().
    private void abrirDesafio(Class<?> destino) {
        startActivity(new Intent(this, destino));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Tela 2 (CentralActivity) - onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Tela 2 (CentralActivity) - onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Tela 2 (CentralActivity) - onDestroy");
    }
}
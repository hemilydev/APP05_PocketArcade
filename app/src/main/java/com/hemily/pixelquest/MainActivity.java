package com.hemily.pixelquest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hemily.pixelquest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = Constantes.TAG_CICLO;
    private static final int APELIDO_MINIMO = 3;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        aplicarEspacamentoSeguro();
        Log.d(TAG, "Tela 1 (MainActivity) - onCreate");

        binding.btnEntrar.setOnClickListener(v -> validarEEntrar());
    }

    // Soma o espaço das barras do sistema com a margem de 24dp,
    // para nada encostar na borda nem ficar atrás da barra de status
    private void aplicarEspacamentoSeguro() {
        int espaco = getResources().getDimensionPixelSize(R.dimen.espaco_g);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left + espaco, barras.top + espaco,
                    barras.right + espaco, barras.bottom + espaco);
            return insets;
        });
    }

    private void validarEEntrar() {
        String apelido = binding.etApelido.getText() == null
                ? ""
                : binding.etApelido.getText().toString().trim();

        if (apelido.length() < APELIDO_MINIMO) {
            binding.tilApelido.setError(getString(R.string.t1_erro_apelido));
            return;
        }
        binding.tilApelido.setError(null);
        binding.btnEntrar.setEnabled(false);

        // ViewPropertyAnimator: o botão "afunda" e volta, depois abre a Central
        binding.btnEntrar.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(150)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.btnEntrar.animate().setListener(null);
                        binding.btnEntrar.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(150)
                                .start();
                        abrirCentral(apelido);
                    }
                })
                .start();
    }

    // Intent explícita levando o apelido para a Tela 2
    private void abrirCentral(String apelido) {
        Intent intent = new Intent(this, CentralActivity.class);
        intent.putExtra(Constantes.EXTRA_APELIDO, apelido);
        startActivity(intent);
    }

    // Chamado quando um desafio usa "Encerrar sessão" (CLEAR_TOP | SINGLE_TOP)
    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String modulo = intent.getStringExtra(Constantes.EXTRA_ULTIMO_MODULO);
        Log.d(TAG, "Tela 1 (MainActivity) - onNewIntent | último módulo: " + modulo);

        if (modulo != null && !modulo.isEmpty()) {
            String mensagem = getString(R.string.t1_sessao_encerrada, modulo);
            binding.tvUltimoModulo.setText(mensagem);
            binding.tvUltimoModulo.setVisibility(View.VISIBLE);
            binding.tvUltimoModulo.setAlpha(0f);
            binding.tvUltimoModulo.animate().alpha(1f).setDuration(500).start();
            Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.btnEntrar.setEnabled(true);
        Log.d(TAG, "Tela 1 (MainActivity) - onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Tela 1 (MainActivity) - onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Tela 1 (MainActivity) - onDestroy");
    }
}
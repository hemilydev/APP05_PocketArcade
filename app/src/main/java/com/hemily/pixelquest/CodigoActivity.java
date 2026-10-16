package com.hemily.pixelquest;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hemily.pixelquest.databinding.ActivityCodigoBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodigoActivity extends AppCompatActivity {

    private static final String TAG = Constantes.TAG_CICLO;
    private static final int TAMANHO_CODIGO = 4;
    private static final int MAX_TENTATIVAS = 8;

    private ActivityCodigoBinding binding;

    private String codigoSecreto;
    private int tentativasFeitas;
    private long inicioPartida;
    private boolean partidaEncerrada;
    private SpannableStringBuilder historico;

    // Sons de conclusão: criados uma vez e reutilizados
    private MediaPlayer somVitoria;
    private MediaPlayer somDerrota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCodigoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "Tela 5 (CodigoActivity) - onCreate");

        somVitoria = MediaPlayer.create(this, R.raw.som_vitoria);
        somDerrota = MediaPlayer.create(this, R.raw.som_derrota);

        binding.btnTentar.setOnClickListener(v -> tentar());
        binding.btnNovaPartidaCodigo.setOnClickListener(v -> novaPartida());
        binding.btnVoltarCodigo.setOnClickListener(v -> finish());
        binding.btnEncerrarCodigo.setOnClickListener(v ->
                Navegacao.encerrarSessao(this, getString(R.string.modulo_codigo)));

        novaPartida();
    }

    // Gera 4 dígitos SEM repetição: embaralha 0-9 e pega os 4 primeiros
    private String gerarCodigo() {
        List<Integer> digitos = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            digitos.add(i);
        }
        Collections.shuffle(digitos);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            sb.append(digitos.get(i));
        }
        return sb.toString();
    }

    private void novaPartida() {
        codigoSecreto = gerarCodigo();
        Log.d(TAG, "Código secreto gerado (para teste): " + codigoSecreto);
        tentativasFeitas = 0;
        partidaEncerrada = false;
        historico = new SpannableStringBuilder();
        inicioPartida = System.currentTimeMillis(); // início do tempo

        binding.tvMascara.setText(R.string.t5_mascara);
        binding.tvMascara.setTextColor(ContextCompat.getColor(this, R.color.neo_rosa));
        binding.tvHistorico.setText(R.string.t5_historico_vazio);
        binding.painelConclusao.setVisibility(View.GONE);
        binding.etTentativa.setText("");
        binding.etTentativa.setEnabled(true);
        binding.btnTentar.setEnabled(true);
        binding.tilTentativa.setError(null);
        atualizarContador();
    }

    private void tentar() {
        if (partidaEncerrada) {
            return;
        }
        String tentativa = binding.etTentativa.getText() == null
                ? ""
                : binding.etTentativa.getText().toString().trim();

        // Validações antes de aceitar a tentativa
        if (tentativa.isEmpty()) {
            binding.tilTentativa.setError(getString(R.string.t5_erro_vazio));
            return;
        }
        if (!tentativa.matches("\\d{4}")) {
            binding.tilTentativa.setError(getString(R.string.t5_erro_tamanho));
            return;
        }
        if (temDigitoRepetido(tentativa)) {
            binding.tilTentativa.setError(getString(R.string.t5_erro_repetido));
            return;
        }
        binding.tilTentativa.setError(null);

        tentativasFeitas++;
        registrarNoHistorico(tentativa);
        binding.etTentativa.setText("");

        if (tentativa.equals(codigoSecreto)) {
            concluir(true);
        } else if (tentativasFeitas >= MAX_TENTATIVAS) {
            concluir(false);
        } else {
            atualizarContador();
        }
    }

    private boolean temDigitoRepetido(String texto) {
        Set<Character> vistos = new HashSet<>();
        for (char c : texto.toCharArray()) {
            if (!vistos.add(c)) {
                return true;
            }
        }
        return false;
    }

    // Verde: posição certa | Amarelo: existe em outra posição | Cinza: não existe
    private void registrarNoHistorico(String tentativa) {
        if (tentativasFeitas == 1) {
            historico = new SpannableStringBuilder();
        }
        historico.append(getString(R.string.t5_linha, tentativasFeitas));

        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            char digito = tentativa.charAt(i);
            int corFundo;
            int corTexto;
            if (digito == codigoSecreto.charAt(i)) {
                corFundo = R.color.termo_verde;
                corTexto = R.color.neo_fundo;
            } else if (codigoSecreto.indexOf(digito) >= 0) {
                corFundo = R.color.termo_amarelo;
                corTexto = R.color.neo_fundo;
            } else {
                corFundo = R.color.termo_cinza;
                corTexto = R.color.white;
            }

            int inicio = historico.length();
            historico.append(" ").append(digito).append(" ");
            int fim = historico.length();
            historico.setSpan(new BackgroundColorSpan(ContextCompat.getColor(this, corFundo)),
                    inicio, fim, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            historico.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, corTexto)),
                    inicio, fim, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            historico.append(" ");
        }
        historico.append("\n");
        binding.tvHistorico.setText(historico);
    }

    private void concluir(boolean venceu) {
        partidaEncerrada = true;
        binding.etTentativa.setEnabled(false);
        binding.btnTentar.setEnabled(false);

        // Tempo: diferença entre fim e início (sem Handler, Thread ou Runnable)
        long fimPartida = System.currentTimeMillis();
        int segundos = (int) ((fimPartida - inicioPartida) / 1000);

        // Revela o código no cofre
        binding.tvMascara.setText(codigoSecreto.replace("", " ").trim());

        String mensagem;
        if (venceu) {
            mensagem = getString(R.string.t5_vitoria, codigoSecreto, tentativasFeitas, segundos);
            binding.tvMascara.setTextColor(ContextCompat.getColor(this, R.color.termo_verde));
            binding.tvConclusao.setTextColor(ContextCompat.getColor(this, R.color.termo_verde));
            tocar(somVitoria);
        } else {
            mensagem = getString(R.string.t5_derrota, codigoSecreto, tentativasFeitas, segundos);
            binding.tvMascara.setTextColor(ContextCompat.getColor(this, R.color.neo_erro));
            binding.tvConclusao.setTextColor(ContextCompat.getColor(this, R.color.neo_erro));
            tocar(somDerrota);
        }
        binding.tvConclusao.setText(mensagem);
        binding.tvTentativas.setText(getString(R.string.t5_tentativas, tentativasFeitas, MAX_TENTATIVAS));

        binding.painelConclusao.setVisibility(View.VISIBLE);
        if (venceu) {
            animarSucesso();
        } else {
            animarFracasso();
        }
    }

    // Sucesso: o painel cresce com um "quique"
    private void animarSucesso() {
        binding.painelConclusao.setAlpha(0f);
        binding.painelConclusao.setScaleX(0.6f);
        binding.painelConclusao.setScaleY(0.6f);
        binding.painelConclusao.animate()
                .alpha(1f).scaleX(1f).scaleY(1f)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(600)
                .start();
    }

    // Fracasso: o cofre "treme" e o painel aparece
    private void animarFracasso() {
        binding.tvMascara.setTranslationX(0f);
        binding.tvMascara.animate()
                .translationX(30f)
                .setInterpolator(new CycleInterpolator(3))
                .setDuration(600)
                .start();

        binding.painelConclusao.setScaleX(1f);
        binding.painelConclusao.setScaleY(1f);
        binding.painelConclusao.setAlpha(0f);
        binding.painelConclusao.animate()
                .alpha(1f)
                .setDuration(600)
                .start();
    }

    private void atualizarContador() {
        binding.tvTentativas.setText(getString(R.string.t5_tentativas, tentativasFeitas + 1, MAX_TENTATIVAS));
    }

    private void tocar(MediaPlayer player) {
        if (player == null) {
            return;
        }
        if (player.isPlaying()) {
            player.pause();
        }
        player.seekTo(0);
        player.start();
    }

    private void pausar(MediaPlayer player) {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Tela 5 (CodigoActivity) - onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausar(somVitoria);
        pausar(somDerrota);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Tela 5 (CodigoActivity) - onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (somVitoria != null) {
            somVitoria.release();
            somVitoria = null;
        }
        if (somDerrota != null) {
            somDerrota.release();
            somDerrota = null;
        }
        Log.d(TAG, "Tela 5 (CodigoActivity) - onDestroy");
    }
}
package com.hemily.pixelquest;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hemily.pixelquest.databinding.ActivityArenaBinding;

import java.util.Random;

public class ArenaActivity extends AppCompatActivity {

    private static final String TAG = Constantes.TAG_CICLO;

    private static final int PEDRA = 0;
    private static final int PAPEL = 1;
    private static final int TESOURA = 2;
    private static final int VITORIAS_PARA_VENCER = 2;
    private static final long DURACAO_ANIMACAO = 450L; // dentro da faixa de 300 a 700 ms

    private final int[] icones = {R.drawable.ic_pedra, R.drawable.ic_papel, R.drawable.ic_tesoura};
    private final int[] nomes = {R.string.t4_pedra, R.string.t4_papel, R.string.t4_tesoura};

    private ActivityArenaBinding binding;
    private final Random random = new Random();

    private int pontosJogador = 0;
    private int pontosDispositivo = 0;
    private boolean partidaEncerrada = false;

    // MediaPlayer criados UMA vez no onCreate e reutilizados
    private MediaPlayer somJogada;
    private MediaPlayer somVitoria;
    private MediaPlayer somDerrota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArenaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "Tela 4 (ArenaActivity) - onCreate");

        somJogada = MediaPlayer.create(this, R.raw.som_jogada);
        somVitoria = MediaPlayer.create(this, R.raw.som_vitoria);
        somDerrota = MediaPlayer.create(this, R.raw.som_derrota);

        binding.btnPedra.setOnClickListener(v -> jogar(PEDRA));
        binding.btnPapel.setOnClickListener(v -> jogar(PAPEL));
        binding.btnTesoura.setOnClickListener(v -> jogar(TESOURA));
        binding.btnNovaPartida.setOnClickListener(v -> novaPartida());
        binding.btnVoltarArena.setOnClickListener(v -> finish());
        binding.btnEncerrarArena.setOnClickListener(v ->
                Navegacao.encerrarSessao(this, getString(R.string.modulo_arena)));

        novaPartida();
    }

    private void jogar(int escolhaJogador) {
        if (partidaEncerrada) {
            return;
        }
        int escolhaDispositivo = random.nextInt(3);

        // Mostra as duas escolhas
        binding.ivJogador.setImageResource(icones[escolhaJogador]);
        binding.ivDispositivo.setImageResource(icones[escolhaDispositivo]);
        binding.tvNomeJogador.setText(nomes[escolhaJogador]);
        binding.tvNomeDispositivo.setText(nomes[escolhaDispositivo]);
        animarEscolhas();

        // Resultado da rodada (empate não soma ponto)
        String mensagem;
        int cor;
        if (escolhaJogador == escolhaDispositivo) {
            mensagem = getString(R.string.t4_rodada_empate);
            cor = R.color.neo_texto_suave;
        } else if (venceu(escolhaJogador, escolhaDispositivo)) {
            pontosJogador++;
            mensagem = getString(R.string.t4_rodada_vitoria);
            cor = R.color.neo_ciano;
        } else {
            pontosDispositivo++;
            mensagem = getString(R.string.t4_rodada_derrota);
            cor = R.color.neo_erro;
        }

        // Melhor de três: quem chega a 2 vitórias primeiro
        if (pontosJogador == VITORIAS_PARA_VENCER) {
            mensagem = getString(R.string.t4_final_vitoria);
            cor = R.color.termo_verde;
            encerrarPartida();
            tocar(somVitoria);
        } else if (pontosDispositivo == VITORIAS_PARA_VENCER) {
            mensagem = getString(R.string.t4_final_derrota);
            cor = R.color.neo_erro;
            encerrarPartida();
            tocar(somDerrota);
        } else {
            tocar(somJogada);
        }

        atualizarPlacar();
        binding.tvResultado.setText(mensagem);
        binding.tvResultado.setTextColor(ContextCompat.getColor(this, cor));
        animarResultado();
    }

    private boolean venceu(int jogador, int dispositivo) {
        return (jogador == PEDRA && dispositivo == TESOURA)
                || (jogador == PAPEL && dispositivo == PEDRA)
                || (jogador == TESOURA && dispositivo == PAPEL);
    }

    private void encerrarPartida() {
        partidaEncerrada = true;
        habilitarJogadas(false);
    }

    private void novaPartida() {
        pontosJogador = 0;
        pontosDispositivo = 0;
        partidaEncerrada = false;
        atualizarPlacar();
        binding.ivJogador.setImageResource(R.drawable.ic_interrogacao);
        binding.ivDispositivo.setImageResource(R.drawable.ic_interrogacao);
        binding.tvNomeJogador.setText(R.string.t4_indefinido);
        binding.tvNomeDispositivo.setText(R.string.t4_indefinido);
        binding.tvResultado.setText(R.string.t4_aguardando);
        binding.tvResultado.setTextColor(ContextCompat.getColor(this, R.color.neo_texto));
        habilitarJogadas(true);
    }

    private void habilitarJogadas(boolean habilitar) {
        binding.btnPedra.setEnabled(habilitar);
        binding.btnPapel.setEnabled(habilitar);
        binding.btnTesoura.setEnabled(habilitar);
        float alpha = habilitar ? 1f : 0.4f;
        binding.btnPedra.setAlpha(alpha);
        binding.btnPapel.setAlpha(alpha);
        binding.btnTesoura.setAlpha(alpha);
    }

    private void atualizarPlacar() {
        binding.tvPlacar.setText(getString(R.string.t4_placar, pontosJogador, pontosDispositivo));
    }

    // Jogador entra pela esquerda e dispositivo pela direita, girando
    private void animarEscolhas() {
        binding.ivJogador.setTranslationX(-200f);
        binding.ivJogador.setAlpha(0f);
        binding.ivJogador.setRotation(-180f);
        binding.ivJogador.animate()
                .translationX(0f).alpha(1f).rotation(0f)
                .setDuration(DURACAO_ANIMACAO)
                .start();

        binding.ivDispositivo.setTranslationX(200f);
        binding.ivDispositivo.setAlpha(0f);
        binding.ivDispositivo.setRotation(180f);
        binding.ivDispositivo.animate()
                .translationX(0f).alpha(1f).rotation(0f)
                .setDuration(DURACAO_ANIMACAO)
                .start();
    }

    // Mensagem "pula" na tela
    private void animarResultado() {
        binding.tvResultado.setScaleX(0.5f);
        binding.tvResultado.setScaleY(0.5f);
        binding.tvResultado.setAlpha(0f);
        binding.tvResultado.animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(DURACAO_ANIMACAO)
                .start();
    }

    // Reutiliza a mesma instância: volta ao início e toca de novo
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

    private void liberar(MediaPlayer player) {
        if (player != null) {
            player.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Tela 4 (ArenaActivity) - onResume");
    }

    // Ao sair da tela: PAUSA o áudio (nunca stop())
    @Override
    protected void onPause() {
        super.onPause();
        pausar(somJogada);
        pausar(somVitoria);
        pausar(somDerrota);
        Log.d(TAG, "Tela 4 (ArenaActivity) - onPause (áudio pausado)");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Tela 4 (ArenaActivity) - onStop");
    }

    // release(): libera os recursos e leva o player ao estado End
    @Override
    protected void onDestroy() {
        super.onDestroy();
        liberar(somJogada);
        liberar(somVitoria);
        liberar(somDerrota);
        somJogada = null;
        somVitoria = null;
        somDerrota = null;
        Log.d(TAG, "Tela 4 (ArenaActivity) - onDestroy (MediaPlayer liberado)");
    }
}
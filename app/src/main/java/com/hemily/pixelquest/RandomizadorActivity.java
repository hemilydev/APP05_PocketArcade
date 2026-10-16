package com.hemily.pixelquest;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hemily.pixelquest.databinding.ActivityRandomizadorBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomizadorActivity extends AppCompatActivity {

    private static final String TAG = Constantes.TAG_CICLO;
    private static final int COLUNAS_GRADE = 2;

    private ActivityRandomizadorBinding binding;
    private final List<ItemSorteio> itens = new ArrayList<>();
    private ItemSorteioAdapter adapter;
    private boolean emGrade = false;
    private final Random random = new Random();

    // Som do sorteio: criado uma vez e reutilizado
    private MediaPlayer somSorteio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRandomizadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "Tela 6 (RandomizadorActivity) - onCreate");

        somSorteio = MediaPlayer.create(this, R.raw.som_sorteio);

        configurarSpinner();
        configurarLista();
        carregarExemplos();

        binding.btnAdicionar.setOnClickListener(v -> adicionar());
        binding.btnSortear.setOnClickListener(v -> sortear());
        binding.btnAlternar.setOnClickListener(v -> alternarLayout());
        binding.btnVoltarRandom.setOnClickListener(v -> finish());
        binding.btnEncerrarRandom.setOnClickListener(v ->
                Navegacao.encerrarSessao(this, getString(R.string.modulo_random)));
    }

    private void configurarSpinner() {
        ArrayAdapter<CharSequence> categorias = ArrayAdapter.createFromResource(
                this, R.array.t6_categorias, android.R.layout.simple_spinner_item);
        categorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCategoria.setAdapter(categorias);
    }

    private void configurarLista() {
        adapter = new ItemSorteioAdapter(itens, this::remover);
        binding.rvItens.setLayoutManager(new LinearLayoutManager(this));
        binding.rvItens.setAdapter(adapter);
    }

    private void carregarExemplos() {
        String[] palavras = getResources().getStringArray(R.array.t6_exemplos_palavras);
        String[] categorias = getResources().getStringArray(R.array.t6_exemplos_categorias);
        for (int i = 0; i < palavras.length; i++) {
            itens.add(new ItemSorteio(palavras[i], categorias[i]));
        }
        adapter.notifyItemRangeInserted(0, palavras.length);
    }

    // Cadastro: notifyItemInserted() e rola até o novo item
    private void adicionar() {
        String palavra = binding.etPalavra.getText() == null
                ? ""
                : binding.etPalavra.getText().toString().trim();
        if (palavra.isEmpty()) {
            binding.tilPalavra.setError(getString(R.string.t6_erro_vazio));
            return;
        }
        binding.tilPalavra.setError(null);

        String categoria = binding.spCategoria.getSelectedItem().toString();
        itens.add(new ItemSorteio(palavra, categoria));
        int posicao = itens.size() - 1;
        adapter.notifyItemInserted(posicao);
        binding.rvItens.scrollToPosition(posicao);

        binding.etPalavra.setText("");
        Toast.makeText(this, getString(R.string.t6_adicionado, palavra), Toast.LENGTH_SHORT).show();
    }

    // Exclusão por clique longo: notifyItemRemoved()
    private void remover(int posicao) {
        ItemSorteio removido = itens.remove(posicao);
        adapter.notifyItemRemoved(posicao);
        Toast.makeText(this, getString(R.string.t6_removido, removido.getPalavra()),
                Toast.LENGTH_SHORT).show();
    }

    // Sorteia APENAS entre os itens da categoria selecionada no Spinner
    private void sortear() {
        String categoria = binding.spCategoria.getSelectedItem().toString();
        List<Integer> posicoesDaCategoria = new ArrayList<>();
        for (int i = 0; i < itens.size(); i++) {
            if (itens.get(i).getCategoria().equals(categoria)) {
                posicoesDaCategoria.add(i);
            }
        }

        if (posicoesDaCategoria.isEmpty()) {
            String aviso = getString(R.string.t6_sem_itens, categoria);
            binding.tvSorteado.setText(aviso);
            Toast.makeText(this, aviso, Toast.LENGTH_SHORT).show();
            return;
        }

        int posicaoSorteada = posicoesDaCategoria.get(random.nextInt(posicoesDaCategoria.size()));
        ItemSorteio sorteado = itens.get(posicaoSorteada);
        binding.tvSorteado.setText(getString(R.string.t6_sorteado_formato,
                sorteado.getPalavra(), sorteado.getCategoria()));

        animarSorteio();
        tocar(somSorteio);
        binding.rvItens.smoothScrollToPosition(posicaoSorteada);
    }

    // Alterna entre LinearLayoutManager (lista) e GridLayoutManager (grade)
    private void alternarLayout() {
        emGrade = !emGrade;
        if (emGrade) {
            binding.rvItens.setLayoutManager(new GridLayoutManager(this, COLUNAS_GRADE));
            binding.btnAlternar.setText(R.string.t6_btn_lista);
        } else {
            binding.rvItens.setLayoutManager(new LinearLayoutManager(this));
            binding.btnAlternar.setText(R.string.t6_btn_grade);
        }
    }

    // Animação do resultado: o painel gira e cresce (500 ms)
    private void animarSorteio() {
        binding.painelSorteio.setRotationX(90f);
        binding.painelSorteio.setScaleX(0.8f);
        binding.painelSorteio.setScaleY(0.8f);
        binding.painelSorteio.animate()
                .rotationX(0f).scaleX(1f).scaleY(1f)
                .setDuration(500)
                .start();
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Tela 6 (RandomizadorActivity) - onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (somSorteio != null && somSorteio.isPlaying()) {
            somSorteio.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Tela 6 (RandomizadorActivity) - onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (somSorteio != null) {
            somSorteio.release();
            somSorteio = null;
        }
        Log.d(TAG, "Tela 6 (RandomizadorActivity) - onDestroy");
    }
}
package com.hemily.pixelquest;

import android.app.Activity;
import android.content.Intent;

public final class Navegacao {

    private Navegacao() {
    }

    // Volta para a Tela 1 que já está na pilha, sem criar outra.
    // CLEAR_TOP fecha tudo que está acima dela; SINGLE_TOP faz a Tela 1
    // receber a Intent em onNewIntent() em vez de ser recriada.
    public static void encerrarSessao(Activity origem, String moduloAtual) {
        Intent intent = new Intent(origem, MainActivity.class);
        intent.putExtra(Constantes.EXTRA_ULTIMO_MODULO, moduloAtual);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        origem.startActivity(intent);
    }
}
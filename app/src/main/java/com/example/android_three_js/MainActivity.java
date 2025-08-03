package com.example.android_three_js;

import static android.webkit.WebView.RENDERER_PRIORITY_IMPORTANT;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends AppCompatActivity {

    // Classe interna para interceptar requisições da WebView e carregá-las dos assets locais
    private static class LocalContentWebViewClient extends WebViewClient {
        private final WebViewAssetLoader assetLoader;

        // Construtor que recebe o carregador de assets
        LocalContentWebViewClient(WebViewAssetLoader loader) {
            this.assetLoader = loader;
        }

        // Intercepta todas as requisições HTTP da WebView
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            // Redireciona as requisições para carregar arquivos dos assets locais
            return assetLoader.shouldInterceptRequest(request.getUrl());
        }
    }

    // Suprime o aviso de segurança sobre JavaScript habilitado
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout da activity principal
        setContentView(R.layout.activity_main);

        // Ativa o modo tela cheia
        enableFullscreen();

        // Mantém a tela sempre ligada enquanto a app estiver ativa
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Encontra a WebView no layout
        WebView frontend = findViewById(R.id.webview);
        // Define prioridade alta para o renderizador da WebView (melhor performance)
        frontend.setRendererPriorityPolicy(RENDERER_PRIORITY_IMPORTANT, false);

        // Configura as definições da WebView
        WebSettings settings = frontend.getSettings();
        settings.setJavaScriptEnabled(true); // Habilita JavaScript (necessário para Three.js)
        settings.setDomStorageEnabled(true); // Habilita armazenamento DOM (localStorage, sessionStorage)

        // Configurações de segurança - desabilita acesso a arquivos do sistema
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);

        // Permite conteúdo misto (HTTP e HTTPS) para compatibilidade
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        settings.setSafeBrowsingEnabled(true); // Habilita navegação segura
        settings.setSupportZoom(false); // Desabilita zoom por pinça (altere para true se quiser zoom)

        // Define cliente para lidar com recursos como alerts, progress, etc.
        frontend.setWebChromeClient(new WebChromeClient());

        // Cria carregador de assets para servir arquivos locais via HTTPS
        WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        // Define o cliente customizado para interceptar requisições
        frontend.setWebViewClient(new LocalContentWebViewClient(assetLoader));

        // Carrega o arquivo HTML principal dos assets usando URL HTTPS local
        frontend.loadUrl("https://appassets.androidplatform.net/assets/index.html");
    }

    // Método para ativar modo tela cheia
    private void enableFullscreen() {
        // Para Android 11 (API 30) e superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Remove decorações do sistema (status bar, navigation bar)
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                // Esconde barras do sistema
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                // Define comportamento: barras aparecem temporariamente ao deslizar
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // Para versões mais antigas do Android
            // Define flags para esconder elementos da interface do sistema
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Layout estável
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // Layout sob navegação
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Layout em tela cheia
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Esconde navegação
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // Modo tela cheia
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Modo imersivo persistente
            );
        }
    }
}
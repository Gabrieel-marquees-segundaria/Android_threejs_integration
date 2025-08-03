import { defineConfig } from 'vite';
import path from 'path';

export default defineConfig({
  // Define o caminho base como relativo para compatibilidade com WebView Android
  // Isso garante que todos os recursos sejam carregados corretamente no contexto da WebView
  base: './',

  // Configuração de alias de caminhos
  resolve: {
    alias: {
      // Cria um alias '@' que aponta para o diretório raiz do projeto
      // Permite imports como: import something from '@/components/MyComponent.js'
      '@': path.resolve(__dirname, '.')
    }
  },

  // Configuração do build/compilação
  build: {
    // Define onde os arquivos compilados serão salvos
    // '../assets' significa que vai para app/src/main/assets/ (pasta do Android)
    outDir: '../assets',

    // Desabilita sourcemaps para reduzir tamanho dos arquivos finais
    sourcemap: false,

    // Limpa o diretório de saída antes de cada build
    emptyOutDir: true,

    // Configurações específicas do Rollup (bundler usado pelo Vite)
    rollupOptions: {
      output: {
        // Nome do arquivo JavaScript principal
        entryFileNames: 'main.js',

        // Padrão de nomenclatura para chunks (pedaços) de código
        // Chunks são criados quando o código é dividido para otimização
        chunkFileNames: 'chunks/[name].js',

        // Padrão para assets (CSS, imagens, etc.)
        // [name] = nome original, [ext] = extensão
        assetFileNames: 'assets/[name].[ext]'
      }
    }
  },

  // Configuração de handling de assets/recursos
  // Define quais tipos de arquivo devem ser tratados como assets estáticos
  assetsInclude: [
    // Formatos de modelos 3D
    '**/*.gltf',    // glTF (formato JSON para modelos 3D)
    '**/*.glb',     // glTF binário (formato compactado)
    '**/*.babylon', // Formato Babylon.js
    '**/*.obj',     // Wavefront OBJ (modelo 3D)
    '**/*.mtl',     // Material Template Library (materiais para OBJ)

    // Formatos de texturas e imagens especiais
    '**/*.hdr',     // High Dynamic Range (texturas HDR)
    '**/*.tga',     // Targa (formato de imagem)

    // Formatos de dados 3D especializados
    '**/*.pcb',     // Point Cloud Binary
    '**/*.pcd',     // Point Cloud Data
    '**/*.prwm',    // Packed Raw WebGL Model
    '**/*.mat',     // Material files

    // Formatos de áudio para experiências 3D
    '**/*.mp3',     // Áudio comprimido
    '**/*.ogg'      // Áudio Ogg Vorbis
  ],

  // Configuração do servidor de desenvolvimento
  server: {
    // Porta do servidor local para desenvolvimento
    port: 3000,

    // Abre automaticamente o navegador quando iniciar o servidor
    open: true
  },

  // Define o diretório raiz do projeto
  // '.' significa o diretório atual onde está o vite.config.js
  root: '.'
});

/*
RESUMO DO FLUXO:

1. DESENVOLVIMENTO:
   - `npm run dev` → inicia servidor local na porta 3000
   - Arquivos fonte são servidos diretamente
   - Hot reload automático quando arquivos mudam

2. BUILD PARA ANDROID:
   - `npm run build` → compila todos os arquivos
   - Resultado vai para '../assets' (pasta assets do Android)
   - JavaScript é bundled em 'main.js'
   - Assets 3D são copiados para subpasta 'assets/'
   - HTML é otimizado com referências corretas

3. ESTRUTURA FINAL NO ANDROID:
   app/src/main/assets/
   ├── index.html          ← HTML otimizado
   ├── main.js             ← JavaScript bundled
   ├── chunks/             ← Código dividido (se necessário)
   └── assets/             ← Modelos 3D, texturas, etc.

4. WEBVIEW CARREGA:
   - index.html carrega main.js
   - main.js contém todo o código Three.js
   - Assets são carregados conforme necessário
*/
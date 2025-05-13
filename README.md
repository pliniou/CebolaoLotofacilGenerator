# Cebolão Lotofácil Generator

Este é um aplicativo Android desenvolvido para auxiliar na geração de combinações de números para a Lotofácil com recursos avançados de filtragem estatística.

## Funcionalidades Principais

* **Geração Inteligente de Jogos:** Cria jogos com números selecionados com base em filtros estatísticos avançados e configuráveis.
* **Filtros Estatísticos Precisos:** 
  * **Pares e Ímpares:** Controle da distribuição entre números pares e ímpares.
  * **Soma Total:** Filtragem pela soma total dos números do jogo, baseada em análises de resultados anteriores.
  * **Números Primos:** Controle da quantidade de números primos em cada jogo.
  * **Números de Fibonacci:** Controle da presença de números da sequência de Fibonacci.
  * **Miolo e Moldura:** Balanceamento entre números do miolo e da borda do volante.
  * **Múltiplos de 3:** Controle da quantidade de números que são múltiplos de 3.
* **100% Offline:** Funciona completamente offline, sem necessidade de conexão com a internet.
* **Armazenamento Local Seguro:** Salva todos seus jogos gerados para consulta posterior usando Room Database.
* **Conferência Inteligente:** Verifica automaticamente seus jogos com os resultados oficiais cadastrados.
* **Gerenciamento Completo:** Interface intuitiva para marcar jogos como favoritos, editar e excluir jogos salvos.
* **Design Moderno:** Interface de usuário limpa e intuitiva com suporte para temas claro e escuro, adaptando-se à preferência do sistema.

## Arquitetura do Projeto

O Cebolão Lotofácil Generator foi desenvolvido seguindo a arquitetura MVVM (Model-View-ViewModel) recomendada pelo Google, visando uma separação clara de responsabilidades e maior facilidade de manutenção e testes.

### Componentes Principais

#### Camada de Dados (Model)
* **Entidades:** `Jogo` e `Resultado` para armazenar jogos gerados e resultados oficiais
* **DAOs:** `JogoDao` e `ResultadoDao` para acesso ao banco de dados
* **Repositórios:** `JogoRepository` e `ResultadoRepository` para abstração do acesso aos dados
* **Banco de Dados:** Implementado com Room para persistência local

#### Camada de Lógica (ViewModel)
* **JogoViewModel:** Gerencia operações relacionadas aos jogos salvos
* **ResultadoViewModel:** Gerencia os resultados oficiais da Lotofácil
* **GeradorViewModel:** Implementa a lógica de geração de jogos com filtros
* **PreferenciasViewModel:** Gerencia as preferências e configurações do usuário

#### Utilitários
* **GeradorJogos:** Implementa algoritmos para geração com filtros estatísticos
* **VerificadorJogos:** Implementa a lógica de conferência e análise de resultados
* **PreferenciasManager:** Gerencia configurações persistentes usando DataStore

### Tecnologias Utilizadas

* **Linguagem:** Kotlin
* **Persistência:** Room Database
* **UI:** Material Design Components
* **Navegação:** Navigation Component
* **Programação Assíncrona:** Kotlin Coroutines e Flow
* **Preferências:** DataStore
* **Injeção de Dependências:** ViewModelProvider
* **Lifecycle:** LiveData e ViewModel

## Manual do Usuário

Este manual descreve como utilizar as funcionalidades do aplicativo Cebolão Lotofácil Generator.

### 1. Tela Inicial

Ao abrir o aplicativo, você verá a tela principal com as seguintes opções de geração:

*   **Gerar Números Aleatórios:** Para criar jogos com dezenas sorteadas aleatoriamente.
*   **Gerar com Números Fixos:** Para criar jogos incluindo dezenas específicas de sua escolha.
*   **Gerar com Números Excluídos:** Para criar jogos evitando dezenas específicas.

### 2. Gerar Números Aleatórios

1.  Na tela inicial, toque em "Gerar Números Aleatórios".
2.  Você será direcionado para a tela de geração.
3.  **Quantidade de Dezenas:** Selecione quantas dezenas cada jogo deve conter (entre 15 e 20).
4.  **Quantidade de Jogos:** Defina quantos jogos você deseja gerar.
5.  Toque no botão "Gerar Jogos".
6.  Os jogos gerados serão exibidos em uma lista na parte inferior da tela.

### 3. Gerar com Números Fixos

1.  Na tela inicial, toque em "Gerar com Números Fixos".
2.  Você será levado à tela de "Seleção de Números Fixos".
3.  Toque nos números (de 1 a 25) que você deseja que apareçam em todos os seus jogos. Os números selecionados ficarão destacados.
    *   *Observação:* A quantidade de números fixos deve ser menor que a quantidade de dezenas por jogo que você selecionará na próxima etapa.
4.  Após selecionar os números fixos, toque em "Avançar" (ou similar).
5.  Você será direcionado para a tela de geração, com os números fixos já considerados.
6.  **Quantidade de Dezenas:** Selecione quantas dezenas cada jogo deve conter (entre 15 e 20). Este valor deve ser maior que a quantidade de números fixos escolhidos.
7.  **Quantidade de Jogos:** Defina quantos jogos você deseja gerar.
8.  Toque no botão "Gerar Jogos".
9.  Os jogos gerados, contendo os números fixos selecionados, serão exibidos.

### 4. Gerar com Números Excluídos

1.  Na tela inicial, toque em "Gerar com Números Excluídos".
2.  Você será levado à tela de "Seleção de Números Excluídos".
3.  Toque nos números (de 1 a 25) que você NÃO deseja que apareçam em nenhum dos seus jogos. Os números selecionados ficarão destacados.
    *   *Observação:* A quantidade de números excluídos não pode ser tão grande a ponto de impossibilitar a formação de um jogo com a quantidade de dezenas desejada (ex: excluir 15 números e tentar gerar jogos de 15 dezenas).
4.  Após selecionar os números a serem excluídos, toque em "Avançar" (ou similar).
5.  Você será direcionado para a tela de geração.
6.  **Quantidade de Dezenas:** Selecione quantas dezenas cada jogo deve conter (entre 15 e 20).
7.  **Quantidade de Jogos:** Defina quantos jogos você deseja gerar.
8.  Toque no botão "Gerar Jogos".
9.  Os jogos gerados, sem os números excluídos, serão exibidos.

### 5. Visualizando os Jogos Gerados

Em todas as modalidades de geração, após clicar em "Gerar Jogos" na tela de geração, os jogos aparecerão listados. Você pode rolar a lista para ver todas as combinações geradas.

## Estrutura do Projeto

O projeto segue a arquitetura MVVM e é totalmente implementado em Kotlin, utilizando as tecnologias modernas do ecossistema Android.

```
CebolaoLotofacilGenerator/
├── app/
│   ├── src/main/java/com/example/cebolaolotofacilgenerator/
│   │   ├── data/
│   │   │   ├── model/         # Modelos de dados: Jogo.kt, Resultado.kt
│   │   │   ├── dao/           # DAOs: JogoDao.kt, ResultadoDao.kt
│   │   │   └── repository/    # Repositórios: JogoRepository.kt, ResultadoRepository.kt
│   │   ├── ui/
│   │   │   ├── adapters/      # Adapters para RecyclerView/ListAdapter
│   │   │   ├── fragments/     # Fragments da interface do usuário
│   │   │   └── screens/       # Telas em Jetpack Compose (se aplicável)
│   │   ├── viewmodel/         # ViewModels: JogoViewModel, ResultadoViewModel, GeradorViewModel, PreferenciasViewModel
│   │   └── util/              # Utilitários: GeradorJogos, VerificadorJogos, PreferenciasManager
│   └── ...
├── build.gradle.kts           # Script de build em Kotlin
├── settings.gradle.kts        # Configuração de módulos
└── README.md                  # Documentação do projeto
```

### Componentes Principais

- **Modelos:** Representam os dados persistidos (Jogo, Resultado) e são anotados para uso com Room.
- **DAOs:** Interfaces para acesso ao banco de dados local (Room).
- **Repositórios:** Abstraem o acesso aos dados, centralizando a lógica de persistência e consulta.
- **ViewModels:** Gerenciam o estado da UI e a lógica de negócio, utilizando LiveData e coroutines.
- **Fragments:** Implementam as telas e fluxos de navegação da aplicação.
- **Adapters:** Fazem o binding de listas de jogos/resultados para componentes de UI (RecyclerView/ListAdapter).
- **Utilitários:** Funções auxiliares para geração, conferência e gerenciamento de preferências.
- **Jetpack Compose:** Utilizado para telas modernas e reativas (caso aplicável).
- **DataStore:** Persistência de preferências do usuário.

### Observações
- Não há arquivos `.java` ou layouts XML clássicos: toda a lógica e UI são em Kotlin (Fragments, Compose, ViewBinding).
- O projeto é modular e facilmente extensível.
- Scripts de build são 100% Kotlin Script (`.kts`).

## Como Compilar e Executar

Para compilar e executar o projeto, siga os passos abaixo:

1.  **Requisitos:**
    *   Android Studio (versão mais recente recomendada).
    *   Android SDK configurado.
    *   Um emulador Android ou um dispositivo físico Android.

2.  **Passos:**
    *   Clone ou baixe este repositório.
    *   Abra o Android Studio.
    *   Selecione "Open an existing Android Studio project".
    *   Navegue até o diretório onde você clonou/descompactou o projeto e selecione a pasta raiz do projeto.
    *   Aguarde o Android Studio sincronizar e construir o projeto (Gradle sync).
    *   Selecione um dispositivo (emulador ou físico) na barra de ferramentas.
    *   Clique no botão "Run" (ícone de play verde) ou use o atalho `Shift + F10`.

O aplicativo será instalado e iniciado no dispositivo/emulador selecionado.
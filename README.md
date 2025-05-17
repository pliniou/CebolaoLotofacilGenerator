# Cebolão Lotofácil Generator

Aplicativo Android 100% offline para geração de jogos da Lotofácil, desenvolvido em:
Kotlin com Jetpack Compose, arquitetura MVVM, Room, DataStore e navegação moderna com transições animadas.

## Funcionalidades Principais

-   **Geração de Jogos com Filtros:** Criação de jogos da Lotofácil com base em aleatoriedade, refinada por um conjunto de filtros personalizáveis.
-   **Filtros Estatísticos Detalhados:**
    -   Números Fixos e Excluídos
    -   Quantidade de Pares e Ímpares
    -   Intervalo para Soma Total das Dezenas
    -   Quantidade de Números Primos
    -   Quantidade de Números de Fibonacci
    -   Quantidade de Dezenas do Miolo (vs. Moldura)
    -   Quantidade de Múltiplos de Três
    -   Quantidade de Dezenas Repetidas do Concurso Anterior (usuário informa as 15 dezenas do concurso anterior e define a faixa de repetição).
    *Para uma explicação detalhada de cada filtro, consulte a tela "Instruções" dentro do aplicativo.*
-   **Gerenciamento de Jogos:** Salvar, visualizar, favoritar e excluir jogos gerados.
-   **Armazenamento Local:**
    -   Room Database: Para jogos salvos e jogos favoritos.
    -   DataStore: Para preferências do usuário (tema do app) e persistência das configurações de filtros.
-   **Interface Moderna e Intuitiva:** Desenvolvida com Jetpack Compose e Material 3.
-   **Navegação Animada:** Transições suaves entre telas para uma melhor experiência do usuário.
-   **Tema Claro/Escuro:** Suporte para tema dinâmico do sistema ou seleção manual.
-   **100% Offline:** Funciona sem necessidade de conexão com a internet.
-   **Instruções de Uso Integradas:** Tela dedicada com explicações detalhadas.

## Requisitos

- Android Studio (mais recente)
- JDK 17
- Kotlin 1.9.22 (ou conforme `libs.versions.toml`)
- Gradle Wrapper (conforme `gradle-wrapper.properties`)
- Android Gradle Plugin (conforme `libs.versions.toml`)
- Compile SDK: 34
- Target SDK: 34
- Min SDK: 21

## Principais Tecnologias

- Jetpack Compose (UI moderna)
- Room (persistência local para jogos)
- DataStore (persistência para preferências e configurações)
- ViewModel + StateFlow/LiveData (arquitetura MVVM)
- Hilt (Injeção de Dependência)
- Navegação Compose com transições animadas (Accompanist)
- Kotlin Coroutines
- Material 3
- Proguard (Minificação e Ofuscação em builds de release)

## Arquitetura e Fluxo de Dados

O aplicativo segue a arquitetura MVVM (Model-View-ViewModel)

-   **View (UI Layer):** Implementada com Jetpack Compose (`ui/screens/`, `ui/components/`). As telas são Composables que observam estados expostos pelos ViewModels.
-   **ViewModel (`viewmodel/`):** Contém a lógica de apresentação e gerencia o estado da UI. Não tem conhecimento direto das Views, expondo dados via `StateFlow` ou `LiveData`. Interage com os Repositórios para obter e persistir dados.
-   **Model (Data Layer):** Composta por:
    -   **Repositories (`data/repository/`):** Abstraem as fontes de dados (Room, DataStore). Fornecem uma API limpa para os ViewModels.
    -   **DataSources:**
        -   **Room (`data/db/`, `data/dao/`):** Banco de dados local para armazenar jogos gerados/salvos.
        -   **DataStore (`data/AppDataStore.kt`, `util/PreferenciasManager.kt`):** Para salvar preferências do usuário, como tema do aplicativo e configurações de filtros.
    -   **Data Models (`data/model/`):** Classes Kotlin (geralmente `data class`) que representam os dados do aplicativo (ex: `Jogo.kt`, `ConfiguracaoFiltros.kt`).

**Fluxo de Dados Típico:**
1.  O usuário interage com um Composable na UI (ex: clica em um botão).
2.  O Composable invoca uma função no `ViewModel`.
3.  O `ViewModel` processa a lógica (ex: valida entrada, prepara dados) e, se necessário, chama uma função no `Repository` apropriado.
4.  O `Repository` busca dados do Room ou DataStore, ou envia dados para serem salvos.
5.  Os dados (ou o resultado da operação) fluem de volta para o `ViewModel`.
6.  O `ViewModel` atualiza seu estado (ex: um `StateFlow`), que é observado pela UI.
7.  A UI (Composable) reage à mudança de estado e se recompõe para exibir as novas informações.

## Estrutura do Projeto Detalhada

```
CebolaoLotofacilGenerator/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/cebolaolotofacilgenerator/
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/
│   │   │   │   │   ├── dao/
│   │   │   │   │   ├── db/
│   │   │   │   │   ├── converters/
│   │   │   │   │   └── repository/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   ├── components/
│   │   │   │   │   ├── theme/
│   │   │   │   ├── viewmodel/
│   │   │   │   ├── util/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── AppNavigation.kt
│   │   │   │   └── Navigation.kt
│   │   └── res/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
│   └── libs.versions.toml
└── README.md
```

## Navegação e UI

O aplicativo utiliza Jetpack Compose Navigation com transições animadas (via Accompanist Navigation Animation).
O menu principal (Bottom Navigation Bar) e as telas são:

-   **Tela de Boas-Vindas (`BoasVindasScreen.kt`):** Ponto de entrada do app. Apresenta uma mensagem de boas-vindas, uma breve explicação sobre o aplicativo e um botão de chamada para ação para "Gerar Jogos".
-   **Tela de Instruções (`InstrucoesScreen.kt`):** Acessível pelo menu principal, fornece orientações detalhadas sobre o conceito de geração aleatória, como usar cada filtro disponível e seu impacto.
-   **Tela de Gerar Jogos (`GeradorScreen.kt`):** Funcionalidade central para criar jogos. Permite selecionar dezenas fixas/excluídas, aplicar diversos filtros estatísticos e visualizar os jogos gerados.
-   **Tela de Favoritos (`FavoritosScreen.kt`):** Lista e gerencia jogos que o usuário marcou como favoritos.
-   **Tela de Ajustes (`SettingsScreen.kt`):** Permite acessar informações "Sobre" e outras configurações.

## Algoritmo de Geração (Visão Geral)

O processo de geração de jogos envolve:
1.  Gerar uma combinação inicial de N dezenas (usualmente 15) de forma aleatória dentro do universo de 1 a 25.
2.  Aplicar sequencialmente cada um dos filtros ativos e configurados pelo usuário:
    *   Verificar se a combinação atende aos critérios de números fixos (devem estar presentes).
    *   Verificar se a combinação atende aos critérios de números excluídos (não devem estar presentes).
    *   Verificar se a combinação satisfaz as faixas de quantidade para Pares/Ímpares, Primos, Fibonacci, Miolo, Múltiplos de 3.
    *   Verificar se a soma das dezenas está dentro do intervalo definido.
    *   Se o filtro de repetição do concurso anterior estiver ativo, verificar se a combinação satisfaz a quantidade de dezenas repetidas (com base nas 15 dezenas do concurso anterior informadas manualmente pelo usuário).
3.  Se a combinação passar por todos os filtros ativos, ela é considerada um jogo válido e é adicionada à lista de jogos gerados.
4.  O processo se repete até que a quantidade de jogos solicitada pelo usuário seja atingida ou um limite de tentativas seja alcançado para evitar loops infinitos caso os filtros sejam muito restritivos.

## Pendências e Melhorias Futuras

-   [ ] **Otimização e Remoção de Código Desnecessário:**
    -   [ ] Identificar e remover componentes, funções e recursos que não são utilizados.
    -   [ ] Verificar recursos importados mas não utilizados.
    -   [ ] Eliminar código comentado que não tem valor para desenvolvimento futuro.
    -   [ ] Revisar continuamente o código para identificar possíveis gargalos, especialmente na geração e filtragem.
-   **Testes:**
    -   [ ] Atualizar e expandir testes unitários para cobrir toda a lógica de negócios, incluindo a geração com filtros.
    -   [ ] Implementar testes de UI com Compose Testing para as principais telas e fluxos.
-   **Melhorias na UI/UX:**
    -   [ ] **Animações Sutis:** Adicionar animações para tornar a experiência mais agradável.
    -   [ ] **Feedback Visual:** Implementar feedback visual aprimorado quando filtros são aplicados.
    -   [ ] **Modo Escuro Aprimorado:** Refinar a experiência do modo escuro.
    -   [ ] **Melhorar Tela de Configurações:** Opções como seleção de temas em lista suspensa, etc.
-   **Funcionalidades Adicionais:**
    -   [ ] **Suporte a Tablets:** Otimizar layouts.
    -   [ ] **Importar/Exportar Jogos:** Permitir que o usuário faça backup/restauração de seus jogos.

## Dependências Principais (links)

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room](https://developer.android.com/jetpack/androidx/releases/room)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Accompanist Navigation Animation](https://google.github.io/accompanist/navigation-animation/)
- [Material 3](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo LICENSE para detalhes.
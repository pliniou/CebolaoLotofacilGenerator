# Cebolão Lotofácil Generator

Aplicativo Android para geração e gerenciamento de jogos da Lotofácil, desenvolvido em Kotlin com Jetpack Compose, arquitetura MVVM, Room, DataStore e navegação moderna.

## Requisitos

- **Android Studio Giraffe ou mais recente**
- **JDK 17**
- **Kotlin 1.9.22**
- **Gradle Wrapper 8.11.1** (não altere para outra versão)
- **Android Gradle Plugin 8.2.2**
- **Compile SDK:** 34
- **Target SDK:** 34
- **Min SDK:** 21

## Principais Tecnologias

- Jetpack Compose (UI moderna)
- Room (persistência local)
- DataStore (preferências)
- ViewModel + LiveData (arquitetura MVVM)
- Navegação Compose
- Kotlin Coroutines 1.7.3
- Material 3

## Como compilar e rodar

1. **Clone o repositório:**
   ```sh
   git clone <url-do-repositorio>
   cd CebolaoLotofacilGenerator
   ```
2. **Abra no Android Studio** (recomendado) ou rode pelo terminal:
   ```sh
   ./gradlew assembleDebug
   ```
3. **Execute no emulador ou dispositivo físico** (SDK 21+).

## Estrutura do Projeto

- `app/src/main/java/com/example/cebolaolotofacilgenerator/` - Código principal
  - `ui/screens/` - Telas Compose
  - `viewmodel/` - ViewModels
  - `data/` - Persistência e repositórios
  - `model/` - Modelos de dados
- `app/src/main/res/` - Recursos (layouts, strings, temas)

## Navegação e UI

O aplicativo possui uma navegação moderna baseada em Jetpack Compose Navigation, com as seguintes telas:

- **Tela Principal (Home):** Interface central do aplicativo e ponto de entrada principal. Apresenta cartões para as principais funcionalidades (geração de jogos, favoritos, resultados) e também integra informações sobre o aplicativo e como usá-lo.
- **Tela de Geração de Jogos (`GeradorScreen.kt`):** Permite ao usuário definir dezenas fixas (opcional), configurar diversos filtros estatísticos e gerar novos jogos da Lotofácil. Navega para uma tela de visualização dos jogos gerados (`JogosGeradosScreen.kt`).
- **Tela de Jogos Gerados (`JogosGeradosScreen.kt`):** (Acessada a partir da Geração de Jogos) Exibe os jogos que foram gerados com base nos filtros selecionados.
- **Tela de Favoritos (`FavoritosScreen.kt`):** Armazena e exibe os jogos salvos pelo usuário.
- **Tela de Resultados (`ResultadosScreen.kt`):** Mostra os resultados de concursos anteriores da Lotofácil.
- **Tela de Configurações (`SettingsScreen.kt`):** Permite personalizar o comportamento do aplicativo, como tema e notificações, e acessar informações sobre o app.

Cada tela possui um cabeçalho (TopAppBar) consistente com título e ações contextuais, seguindo o padrão de design Material 3.

## Fluxo de Navegação

O aplicativo inicia diretamente na Tela Principal (Home).
A partir da Tela Principal, o usuário pode navegar para as telas de Geração de Jogos, Favoritos, Resultados ou Configurações.

## Funcionalidades Principais

* **Geração Inteligente de Jogos:** Cria jogos com números selecionados com base em filtros estatísticos avançados e configuráveis.
* **Filtros Estatísticos Precisos:** 
  * **Pares e Ímpares:** Controle da distribuição entre números pares e ímpares.
  * **Soma Total:** Filtragem pela soma total dos números do jogo.
  * **Números Primos:** Controle da quantidade de números primos.
  * **Números de Fibonacci:** Controle da presença de números da sequência.
  * **Miolo e Moldura:** Balanceamento entre números do miolo e da borda.
  * **Múltiplos de 3:** Controle da quantidade de múltiplos.
* **100% Offline:** Funciona sem necessidade de conexão com a internet.
* **Armazenamento Local:** Salva jogos usando Room Database.
* **Conferência Inteligente:** Verifica jogos com resultados oficiais.
* **Design Moderno:** Interface usando Material3 e Compose.

## Arquitetura e Tecnologias

O projeto utiliza a arquitetura MVVM (Model-View-ViewModel) com as seguintes tecnologias:

### Camada de Apresentação
- **Jetpack Compose:** UI declarativa moderna
- **Material3:** Design system atual do Android
- **Navigation Component:** Navegação entre telas
- **ViewBinding:** Para views XML legadas
- **ViewModel:** Gerenciamento de estado da UI

### Camada de Dados
- **Room:** Persistência local com SQLite
- **DataStore:** Preferências do usuário
- **Kotlin Serialization:** Serialização de dados
- **Repository Pattern:** Abstração de fontes de dados

### Ferramentas e Bibliotecas
- **Kotlin Coroutines:** Programação assíncrona
- **Flow:** Streams de dados reativos
- **KSP:** Processamento de anotações
- **ViewBinding:** Binding de views XML
- **JUnit & Espresso:** Testes unitários e de UI

## Estrutura do Projeto

```
CebolaoLotofacilGenerator/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/cebolaolotofacilgenerator/
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/
│   │   │   │   │   ├── dao/
│   │   │   │   │   └── repository/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── HomeScreen.kt       # Tela principal
│   │   │   │   │   │   ├── GeradorScreen.kt    # Tela de geração de jogos e configuração de filtros
│   │   │   │   │   │   ├── JogosGeradosScreen.kt # Tela de exibição de jogos gerados
│   │   │   │   │   │   ├── FavoritosScreen.kt  # Tela de favoritos
│   │   │   │   │   │   ├── ResultadosScreen.kt # Tela de resultados
│   │   │   │   │   │   └── SettingsScreen.kt   # Tela de configurações
│   │   │   │   │   ├── components/
│   │   │   │   │   └── theme/
│   │   │   │   ├── viewmodel/
│   │   │   │   ├── MainActivity.kt             # Ponto de entrada do app
│   │   │   │   ├── AppNavigation.kt            # Lógica de navegação principal
│   │   │   │   └── Navigation.kt               # Definição de rotas (sealed class Screen)
│   │   │   └── res/
│   │   ├── test/
│   │   └── androidTest/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
│   └── libs.versions.toml
└── README.md
```

## Compilação

O projeto usa Gradle com Kotlin DSL (`.kts`). Para compilar:

```bash
./gradlew assembleDebug    # Versão de debug
./gradlew assembleRelease  # Versão de produção
```

## Testes

```bash
./gradlew test            # Testes unitários
./gradlew connectedCheck  # Testes instrumentados
```

## Solução de Problemas

- **Tela preta com apenas texto:** As telas foram atualizadas para mostrar interfaces completas com Material Design, cabeçalhos, cartões e botões.
- **Erros de compilação relacionados ao Compose:** Verifique se está usando as versões corretas das bibliotecas conforme especificado no build.gradle.kts.
- **Problemas de navegação:** Certifique-se de que o arquivo `Navigation.kt` está definindo corretamente as rotas e que o `NavHost` em `AppNavigation.kt` está configurado adequadamente.

## Próximos Passos

- Implementação da funcionalidade de conferência de jogos salvos/gerados com resultados oficiais (se ainda não integrada em Resultados/Favoritos).
- Verificação e refinamento do fluxo de navegação entre `GeradorScreen` e `JogosGeradosScreen`, especialmente a lógica de quando navegar após a geração.
- Completar a implementação dos campos detalhados para todos os filtros na `GeradorScreen.kt` (Primos, Fibonacci, Miolo/Moldura, Múltiplos de 3).
- Adicionar UI na `GeradorScreen.kt` para configurar quantidade de jogos, quantidade de números (se aplicável), e seleção visual de dezenas fixas/excluídas.
- Implementar a exibição de mensagens de erro/sucesso e status de carregamento na `GeradorScreen.kt` usando `geradorViewModel.mensagem` e `geradorViewModel.operacaoStatus`.
- Decidir sobre a necessidade de uma tela "Sobre" dedicada ou se a informação atual na `HomeScreen` e o diálogo na `SettingsScreen` são suficientes (parece que o diálogo é uma boa solução).
- Melhorias na interface de usuário e experiência geral, incluindo o design do botão "Gerar Jogos" para ser mais informativo.
- Investigar e resolver o problema do ícone do aplicativo não aparecer corretamente no dispositivo.
- Implementar a tela de Favoritos (`FavoritosScreen.kt`) para listar, salvar e remover jogos favoritos.

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.
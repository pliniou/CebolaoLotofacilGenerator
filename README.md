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

## Observações

- O projeto utiliza navegação Compose. As telas principais exibem textos de placeholder por padrão.
- Para customizar ou evoluir as telas, edite os arquivos em `ui/screens/`.
- Caso veja tela preta, verifique se há conteúdo visível nos Composables principais.

## Testes

- Testes unitários: `junit:junit:4.13.2`
- Testes instrumentados: `androidx.test.ext:junit:1.1.5`, `androidx.test.espresso:espresso-core:3.5.1`

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
│   │   │   │   │   ├── components/
│   │   │   │   │   └── theme/
│   │   │   │   ├── viewmodel/
│   │   │   │   └── util/
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

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.
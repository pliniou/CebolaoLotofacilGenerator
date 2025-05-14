# Cebolão Lotofácil Generator

Este é um aplicativo Android desenvolvido para auxiliar na geração de combinações de números para a Lotofácil com recursos avançados de filtragem estatística.

## Requisitos do Sistema

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Gradle 8.11.1
- Android SDK 34 (compilação)
- Android SDK 21+ (execução)

## Compatibilidade

- Kotlin 1.9.22
- Java 17
- Android Gradle Plugin 8.2.2
- Compose Compiler 1.5.8
- Room 2.6.1
- Material3 1.2.0
- Navigation Component 2.7.7
- Coroutines 1.7.3

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

## Configuração do Projeto

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/CebolaoLotofacilGenerator.git
```

2. Abra o projeto no Android Studio

3. Sincronize o projeto com os arquivos Gradle

4. Execute o aplicativo em um emulador ou dispositivo físico

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

## Otimizações Recentes

### Versão 1.0.0 (Fevereiro 2024)
- Migração para Java 17
- Atualização do Android Gradle Plugin para 8.2.2
- Implementação do catálogo de versões (TOML)
- Migração parcial para Jetpack Compose
- Atualização das dependências para versões mais recentes
- Remoção de recursos não utilizados
- Correção de problemas de internacionalização
- Implementação de temas dinâmicos (Material3)

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## Contato

Seu Nome - [@seutwitter](https://twitter.com/seutwitter) - email@exemplo.com

Link do Projeto: [https://github.com/seu-usuario/CebolaoLotofacilGenerator](https://github.com/seu-usuario/CebolaoLotofacilGenerator)
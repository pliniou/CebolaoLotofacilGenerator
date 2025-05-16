# Cebolão Lotofácil Generator

Aplicativo Android 100% offline para geração, conferência e gerenciamento de jogos da Lotofácil, desenvolvido em Kotlin com Jetpack Compose, arquitetura MVVM, Room, DataStore e navegação moderna com transições animadas.

## Requisitos

- **Android Studio Giraffe ou mais recente**
- **JDK 17**
- **Kotlin 1.9.22**
- **Gradle Wrapper 8.11.1**
- **Android Gradle Plugin 8.2.2**
- **Compile SDK:** 34
- **Target SDK:** 34
- **Min SDK:** 21

## Principais Tecnologias

- Jetpack Compose (UI moderna)
- Room (persistência local)
- DataStore (preferências)
- ViewModel + StateFlow (arquitetura MVVM)
- **Hilt (Injeção de Dependência)**
- Navegação Compose com transições animadas (Accompanist)
- Kotlin Coroutines 1.7.3
- Material 3
- **Proguard (Minificação e Ofuscação)**

## Estrutura do Projeto

- `app/src/main/java/com/example/cebolaolotofacilgenerator/` - Código principal
  - `ui/screens/` - Telas Compose
  - `viewmodel/` - ViewModels
  - `data/` - Persistência e repositórios
  - `model/` - Modelos de dados
  - `ui/components/` - Componentes reutilizáveis Compose
  - `ui/theme/` - Temas e estilos
- `app/src/main/res/` - Recursos (layouts, strings, temas)

## Navegação e UI

O aplicativo utiliza navegação moderna baseada em Jetpack Compose Navigation, com transições animadas entre telas (Accompanist Navigation Animation). As principais telas são:

- **Tela Principal (`PrincipalScreen.kt`):** Interface central do app, com acesso rápido às principais funções.
- **Tela de Geração de Jogos (`GeradorScreen.kt`):** Permite configurar filtros estatísticos (que são carregados/persistidos via `FiltrosScreen`), selecionar dezenas fixas/excluídas e gerar jogos.
- **Tela de Filtros (`FiltrosScreen.kt`):** Configuração detalhada e persistência de todos os filtros de geração.
- **Tela de Jogos Gerados/Salvos (`GerenciamentoJogosScreen.kt`):** Exibe os jogos gerados na sessão e os jogos salvos no banco, permitindo gerenciamento.
- **Tela de Favoritos (`FavoritosScreen.kt`):** Lista e gerencia jogos marcados como favoritos.
- **Tela de Resultados (`ResultadosScreen.kt`):** Exibe o último resultado salvo pelo usuário e permite selecionar suas dezenas para usar na geração. (Planejado: expandir para listar e gerenciar todos os resultados de concursos salvos).
- **Tela de Conferência (`ConferenciaScreen.kt`):** Permite ao usuário inserir um resultado de concurso e conferir seus jogos. (Planejado: integrar melhor com a lista de todos os resultados salvos).
- **Tela de Configurações (`SettingsScreen.kt`):** Permite escolher tema, acessar informações do app, etc.
- **Tela de Instruções (`InstrucoesScreen.kt`):** Nova tela que fornece orientações detalhadas sobre como usar o aplicativo e suas funcionalidades.

## Funcionalidades Principais

- **Geração Inteligente de Jogos:** Criação de jogos com filtros estatísticos avançados.
- **Entrada Manual de Resultados:** O usuário pode informar as dezenas sorteadas de um concurso (atualmente via `ConferenciaScreen`, planejado consolidar).
- **Filtros Estatísticos:**
  - Pares/Ímpares
  - Soma Total
  - Números Primos
  - Números de Fibonacci
  - Miolo/Moldura
  - Múltiplos de 3
  - Repetição de Dezenas do Concurso Anterior (com opção de carregar dezenas do último resultado salvo)
- **100% Offline:** Nenhuma dependência de conexão.
- **Armazenamento Local:** Room Database para jogos e resultados; DataStore para preferências e configurações de filtros.
- **Conferência de Jogos:** Verificação dos jogos com base nos resultados informados.
- **Transições Animadas:** Navegação suave entre telas.
- **Design Moderno:** Material 3, Compose.
- **Injeção de Dependência:** Usando Hilt para gerenciar dependências do projeto.
- **Instruções de Uso:** Tela dedicada que explica como usar cada funcionalidade do aplicativo.

### Implementadas Recentemente
- [x] **Tela de Instruções:** Criada nova tela com explicações detalhadas sobre como usar o aplicativo.
- [x] **Integração com Hilt:** Implementação da injeção de dependência com Hilt para melhorar a gestão de dependências.
- [x] **Melhoria na Função 'Gerar Jogos':** Corrigido para exibir jogos gerados diretamente na tela, sem necessidade de navegação.
- [x] **Correção de Warnings do Gradle:** Resolvidos avisos relacionados à configuração do catálogo de versões.

### Pendências e Melhorias Futuras
- **Funcionalidade Principal:**
    - [ ] **Aprimorar a função 'Gerar Jogos' para garantir que todos os filtros sejam aplicados corretamente.**
- **Gestão Completa de Resultados Salvos:**
    - [ ] Expandir `ResultadosScreen.kt` para listar TODOS os resultados de concursos salvos (não apenas o último).
    - [ ] Implementar CRUD completo (Adicionar, Visualizar, Editar, Excluir) para os resultados de concursos salvos.
    - [ ] Permitir que a `ConferenciaScreen` utilize qualquer resultado salvo da lista para conferência.
    - [ ] Consolidar a entrada de "novo resultado" (atualmente dispersa/implícita) em um fluxo claro, provavelmente a partir da tela de listagem de resultados.
- **Testes:**
    - [ ] Finalizar a atualização dos testes unitários para refletir todas as mudanças recentes.
    - [ ] Implementar mais testes de UI com Compose Testing para garantir a robustez da interface.
- **Documentação:**
    - [ ] Completar KDoc em todas as classes e funções públicas importantes.
- **Otimização:**
    - [ ] Revisar continuamente o código para identificar possíveis gargalos, especialmente na geração e filtragem.
- **Migração para Compose:**
    - [ ] Finalizar a migração de quaisquer componentes legados que ainda utilizem ViewBinding/XML (se houver).
- **Sugestões de Melhorias (do backlog original e novas):**
    - [ ] **Melhorar Tela de Configurações:**
        - [ ] Apresentar seleção de temas em uma lista suspensa.
        - [ ] Avaliar e adicionar mais opções de configuração.
        - [ ] Simplificar ou remover suporte a multilinguagem, focando em pt-BR.
    - [ ] **Estatísticas Avançadas:** Exibir estatísticas sobre jogos gerados e resultados históricos.
    - [ ] **Compartilhamento Social:** Facilitar o compartilhamento de jogos.
    - [ ] **Modo Escuro Aprimorado:** Refinar a experiência do modo escuro.
    - [ ] **Acessibilidade:** Continuar melhorando o suporte para tecnologias assistivas.
    - [ ] **Suporte a Tablets:** Otimizar layouts para telas maiores.
    - [ ] **Importar/Exportar Jogos:** Permitir importação/exportação de jogos do usuário.
    - [ ] **Teimosinha:** Facilidade para repetir jogos por vários concursos.
    - [ ] **Backup/Restauração de Dados:** Opção para backup de dados do app.

## Arquitetura e Tecnologias

- **MVVM:** Separação clara entre UI, lógica e dados.
- **Room:** Persistência local de jogos e resultados.
- **DataStore:** Preferências do usuário (tema, filtros, etc).
- **Hilt:** Injeção de dependência.
- **Compose + Material 3:** UI declarativa e moderna.
- **Accompanist Navigation Animation:** Transições animadas entre telas.
- **Coroutines + Flow/StateFlow:** Operações assíncronas e reatividade.
- **Repository Pattern:** Abstração de acesso a dados.

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
│   │   │   │   │   ├── adapters/ (se houver algum resquício de View)
│   │   │   │   │   └── navigation/ (se separado de AppNavigation/Navigation.kt)
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
*Nota: A estrutura acima é uma representação. Adapters e navigation podem estar dentro de `ui` ou em subpastas mais específicas.*

## Dependências Principais

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room](https://developer.android.com/jetpack/androidx/releases/room)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Accompanist Navigation Animation](https://google.github.io/accompanist/navigation-animation/)
- [Material 3](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo LICENSE para detalhes.
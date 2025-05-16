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
- Navegação Compose com transições animadas (Accompanist)
- Kotlin Coroutines 1.7.3
- Material 3

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

## Status e Próximos Passos

Aqui listamos as funcionalidades implementadas, pendências e ideias para o futuro.

### Implementado Recentemente / Revisado
- [x] Seleção manual do último resultado (entrada de dados consolidada para `ConferenciaScreen`, `ResultadosScreen` foca em usar o último salvo para o gerador).
- [x] Transições animadas entre telas (Accompanist Navigation Animation).
- [x] Filtros estatísticos completos na geração de jogos (Interface de configuração e persistência em `FiltrosScreen` e `FiltrosViewModel`. `GeradorScreen` agora usa essa configuração central).
- [x] Persistência 100% offline (Room para dados, DataStore para preferências/filtros).
- [x] UI moderna e responsiva (Material 3).
- [x] Feedback ao usuário com Snackbar e mensagens contextuais.
- [x] Botão "Gerar Jogos" dinâmico/informativo.
- [x] Implementação completa da tela de Favoritos (para Jogos).
- [x] Mais opções de temas/cores na SettingsScreen.
- [x] Refino visual dos filtros e hierarquia da UI.
- [x] Tela de Jogos Gerados (`GerenciamentoJogosScreen`) com opções de salvar, gerar mais e favoritar.
- [x] Filtro de Repetição do Concurso Anterior: Permite carregar dezenas do último resultado salvo.
- [x] Atualização dos testes unitários (em progresso, conforme necessidade das refatorações).
- [x] Conferência inteligente de jogos.
- [x] Melhorias de acessibilidade e internacionalização (revisão contínua).
- [x] Documentação de API interna (KDoc) (em progresso).
- [x] Otimização de desempenho na geração de jogos com múltiplos filtros.

### Pendências e Melhorias Futuras
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

## Solução de Problemas

- **Erro ao deletar arquivos de build:** Feche o Android Studio, emuladores e qualquer Explorer na pasta do projeto. Delete manualmente a pasta `build` e tente novamente.
- **Erros de dependência:** Certifique-se de que o repositório `google()` está presente em todos os blocos de repositórios do Gradle.
- **Problemas de navegação ou animação:** Verifique se a dependência do Accompanist está corretamente declarada e sincronizada.
- **Tela preta ou UI incompleta:** Confirme se está usando as versões corretas das bibliotecas e se o tema Compose está aplicado corretamente.
- **Erros nos testes unitários:** Os testes precisam ser atualizados para refletir as mudanças na estrutura de dados e ViewModel.

## Dependências Principais

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room](https://developer.android.com/jetpack/androidx/releases/room)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Accompanist Navigation Animation](https://google.github.io/accompanist/navigation-animation/)
- [Material 3](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo LICENSE (se existir) para detalhes.
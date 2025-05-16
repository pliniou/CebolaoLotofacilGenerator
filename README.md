# Cebolão Lotofácil Generator

Aplicativo Android 100% offline para geração, conferência e gerenciamento de jogos da Lotofácil, desenvolvido em Kotlin com Jetpack Compose, arquitetura MVVM, Room, DataStore e navegação moderna com transições animadas.

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

- **Tela Principal (Home):** Interface central do app, com acesso rápido às principais funções.
- **Tela de Geração de Jogos (`GeradorScreen.kt`):** Permite configurar filtros, selecionar dezenas fixas/excluídas e gerar jogos. Inclui seleção manual do último resultado sorteado (grade de 1 a 25).
- **Tela de Jogos Gerados:** Exibe os jogos gerados com base nos filtros.
- **Tela de Favoritos:** Lista e gerencia jogos favoritos.
- **Tela de Resultados:** Exibe resultados salvos pelo usuário (totalmente offline).
- **Tela de Configurações:** Permite escolher tema, acessar informações do app, etc.

## Funcionalidades Principais

- **Geração Inteligente de Jogos:** Criação de jogos com filtros estatísticos avançados.
- **Seleção Manual do Último Resultado:** O usuário informa as 15 dezenas sorteadas do último concurso, sem necessidade de internet.
- **Filtros Estatísticos:**
  - Pares/Ímpares
  - Soma Total
  - Números Primos
  - Números de Fibonacci
  - Miolo/Moldura
  - Múltiplos de 3
  - Repetição de Dezenas do Concurso Anterior
- **100% Offline:** Nenhuma dependência de conexão.
- **Armazenamento Local:** Room Database para jogos e resultados.
- **Conferência de Jogos:** Verificação dos jogos gerados com base nos resultados informados.
- **Transições Animadas:** Navegação suave entre telas com animações de slide.
- **Design Moderno:** Material 3, Compose, responsivo e acessível.

## Status do Projeto

### O que ainda precisa ser feito:

1. **Correção dos testes unitários**: Os testes unitários apresentam erros relacionados a métodos que não existem mais, como `getNumerosComoLista()` e referências a propriedades como `somaTotal`. Estes testes precisam ser atualizados para refletir as mudanças na estrutura de dados.

2. **Finalização da migração para Compose**: Alguns fragmentos ainda usam layouts XML e ViewBinding. A migração completa para Compose tornaria o código mais consistente.

3. **Melhorias na documentação**: Adicionar KDoc em classes e funções importantes para facilitar a manutenção.

4. **Otimização de desempenho**: Revisar o código para identificar possíveis gargalos, especialmente na geração de jogos com muitos filtros.

5. **Implementação de testes de UI**: Adicionar testes de UI usando Compose Testing para garantir que a interface funcione corretamente.

## Arquitetura e Tecnologias

- **MVVM:** Separação clara entre UI, lógica e dados.
- **Room:** Persistência local de jogos e resultados.
- **DataStore:** Preferências do usuário (tema, filtros, etc).
- **Compose + Material 3:** UI declarativa e moderna.
- **Accompanist Navigation Animation:** Transições animadas entre telas.
- **Coroutines + Flow/StateFlow:** Operações assíncronas e reatividade.
- **Repository Pattern:** Abstração de acesso a dados.

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

## Solução de Problemas

- **Erro ao deletar arquivos de build:** Feche o Android Studio, emuladores e qualquer Explorer na pasta do projeto. Delete manualmente a pasta `build` e tente novamente.
- **Erros de dependência:** Certifique-se de que o repositório `google()` está presente em todos os blocos de repositórios do Gradle.
- **Problemas de navegação ou animação:** Verifique se a dependência do Accompanist está corretamente declarada e sincronizada.
- **Tela preta ou UI incompleta:** Confirme se está usando as versões corretas das bibliotecas e se o tema Compose está aplicado corretamente.
- **Erros nos testes unitários:** Os testes precisam ser atualizados para refletir as mudanças na estrutura de dados. Métodos como `getNumerosComoLista()` foram removidos e propriedades como `somaTotal` foram renomeadas.

## Roadmap / Pendências

- [x] Seleção manual do último resultado (grade de 1 a 25, persistência local)
- [x] Transições animadas entre telas (Accompanist Navigation Animation)
- [x] Filtros estatísticos completos na geração de jogos (incluindo Repetição de Dezenas, Pares/Ímpares, Soma, Primos, Fibonacci, Miolo/Moldura, Múltiplos de 3) - **Interface de configuração e persistência implementadas em `FiltrosScreen` e `FiltrosViewModel`. Integração com `GeradorViewModel` realizada.**
- [x] Persistência 100% offline (Room)
- [x] UI moderna e responsiva (Material 3)
- [x] Feedback ao usuário com Snackbar e mensagens contextuais
- [x] Botão "Gerar Jogos" dinâmico/informativo
- [x] Implementação completa da tela de Favoritos
- [x] Mais opções de temas/cores na SettingsScreen (Claro, Escuro, Sistema, Azul, Verde, Laranja, Ciano)
- [x] Refino visual dos filtros e hierarquia da UI (filtros agrupados em Cards na GeradorScreen, melhorias na SettingsScreen)
- [x] Tela de Jogos Gerados com opções de salvar, gerar mais e favoritar
- [x] **Filtro de Repetição do Concurso Anterior:** Permitir carregar dezenas do último resultado salvo no app (atualmente entrada manual).
- [x] Atualização dos testes unitários para refletir as mudanças na estrutura de dados e ViewModel (corrigidas referências a `getNumerosComoLista` e `somaTotal`).
- [x] Conferência inteligente de jogos (UI/UX aprimorada - Exibição da faixa de prêmio para cada jogo conferido).
- [x] Melhorias de acessibilidade (uso consistente de `contentDescription` com `stringResource` verificado nas telas principais) e internacionalização (revisão de strings hardcoded nas telas principais não revelou problemas críticos).
- [x] Documentação de API interna (KDoc) iniciada (Modelos principais, ViewModels e Composables chave documentados).
- [x] Implementação de testes de UI com Compose Testing (Estrutura inicial e primeiro teste para FiltrosScreen criados).
- [x] Otimização de desempenho na geração de jogos com múltiplos filtros (Verificação de duplicidade otimizada com HashSet).

## Sugestões de Melhorias

**Modo Offline Aprimorado**: Implementar um sistema de cache mais robusto para armazenar resultados históricos.

**Compartilhamento Social**: Facilitar o compartilhamento de jogos com amigos via aplicativos de mensagem.

**Modo Escuro Aprimorado**: Refinar a experiência do modo escuro com mais opções de personalização.

**Acessibilidade**: Melhorar o suporte para leitores de tela e outras tecnologias assistivas.

**Suporte a Tablets**: Otimizar layouts para telas maiores, aproveitando melhor o espaço disponível.

## Dependências Principais

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room](https://developer.android.com/jetpack/androidx/releases/room)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Accompanist Navigation Animation](https://google.github.io/accompanist/navigation-animation/)
- [Material 3](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.
# Fintra — Personal Finance Tracker

A personal finance app for Android: track income and expenses, organize
categories, import bank CSVs and visualize where your money goes.

[English](#english) | [Português](#português)

---

## English

### What it does

- **Transactions** — add, edit and delete income/expense entries with title,
  amount, date and category
- **Categories** — create, rename and delete custom categories; renames cascade
  to all linked transactions
- **CSV import** — bulk-import transactions from bank statement exports
- **Summary dashboard** — balance hero card, income vs. expense totals and an
  income-commitment bar
- **Spending breakdown** — donut chart with per-category percentages
- **Filters** — slice the data by year, month, category or text search
- **Themes** — 6 generated palettes plus a fully custom theme editor (pick
  primary/secondary/accent/background/text colors with live preview)
- **Local-first** — all data stays on-device in a Room database; no accounts,
  no cloud, no tracking

### Install

Download the signed APK from [Releases](../../releases) and sideload it
(allow "install unknown apps" when prompted). All releases are signed with the
same key, so updates install in place without losing your data.

### Build

Requires JDK 21 and the Android SDK.

```bash
./gradlew installDebug        # debug build on a connected device/emulator
./gradlew assembleRelease     # signed release (uses keystore.properties or env vars)
./gradlew testDebugUnitTest   # unit tests
./gradlew connectedDebugAndroidTest  # UI tests on device/emulator
```

Release signing reads `keystore.properties` locally or `RELEASE_STORE_*`
environment variables in CI. See the workflow in `.github/workflows/`.

---

## Português

### O que o app faz

- **Transações** — adicione, edite e exclua entradas de renda/despesa com
  título, valor, data e categoria
- **Categorias** — crie, renomeie e exclua categorias personalizadas; renomear
  atualiza todas as transações vinculadas
- **Importação CSV** — importe transações em massa de extratos bancários
- **Resumo** — card de saldo em destaque, totais de entradas/saídas e barra de
  comprometimento da renda
- **Gastos por categoria** — gráfico de rosca com percentuais por categoria
- **Filtros** — filtre por ano, mês, categoria ou busca de texto
- **Temas** — 6 paletas geradas mais um editor de tema personalizado (escolha
  cores de primária/secundária/destaque/fundo/texto com preview ao vivo)
- **Local-first** — todos os dados ficam no aparelho, num banco Room; sem
  contas, sem nuvem, sem rastreamento

### Instalação

Baixe o APK assinado em [Releases](../../releases) e instale via sideload
(permita "instalar apps desconhecidos" quando solicitado). Todas as releases são
assinadas com a mesma chave, então updates instalam por cima sem perder seus dados.

### Build

Requer JDK 21 e o Android SDK.

```bash
./gradlew installDebug        # build debug num device/emulador conectado
./gradlew assembleRelease     # release assinada (usa keystore.properties ou env vars)
./gradlew testDebugUnitTest   # testes unitários
./gradlew connectedDebugAndroidTest  # testes de UI no device/emulador
```

A assinatura de release lê `keystore.properties` localmente ou as variáveis
`RELEASE_STORE_*` no CI. Veja o workflow em `.github/workflows/`.

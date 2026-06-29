# Changelog — Correções aplicadas ao projeto base

## Bug 1: API Key não configurada

**Arquivo:** `local.properties` (criado)

O projeto exige uma chave da API OpenWeather para funcionar. O arquivo `local.properties` foi criado na raiz do projeto com a propriedade `WEATHER_API_KEY`. Este arquivo está no `.gitignore` e não é versionado.

---

## Bug 2: Pressão hardcoded

**Arquivo:** `app/src/main/java/com/example/findinglogs/view/recyclerview/adapter/WeatherListAdapter.java`

**Antes:**
```java
String pressure_value = "Pressão: " + 1008.2 + "hPa";
```

**Depois:**
```java
String pressure_value = "Pressão: " + weather.getMain().getPressure() + " hPa";
```

**Motivo:** O valor da pressão estava fixo em vez de usar o dado real retornado pela API. O modelo `WeatherInfo` já possuía o campo `pressure` com getter, bastando referenciá-lo.

---

## Bug 3: Botão de refresh não-funcional

**Arquivos:**
- `app/src/main/java/com/example/findinglogs/view/MainActivity.java`
- `app/src/main/java/com/example/findinglogs/viewmodel/MainViewModel.java`

**Antes (MainActivity):**
```java
fetchButton.setOnClickListener(v ->
        Toast.makeText(MainActivity.this, "Not Implemenented yet",
        Toast.LENGTH_SHORT).show());
```

**Depois (MainActivity):**
```java
fetchButton.setOnClickListener(v -> mainViewModel.refresh());
```

**Depois (MainViewModel — método adicionado):**
```java
public void refresh() {
    fetchAllForecasts();
}
```

**Motivo:** O botão apenas mostrava um Toast. Agora ele dispara uma nova busca de dados na API.

---

## Bug 4: Ícone de clima quebrado

**Arquivo:** `app/src/main/res/drawable/weather_icon_03dd.png` → renomeado para `weather_icon_03d.png`

**Motivo:** A API retorna o código `03d` para nuvens dispersas (dia). O método `Utils.getDrawable()` monta o nome `weather_icon_03d`, mas o arquivo se chamava `weather_icon_03dd` (com "d" extra). Assim, o ícone não era encontrado e caía no fallback genérico.

---

## Bug 5: Dados duplicados

**Arquivo:** `app/src/main/java/com/example/findinglogs/model/repo/Repository.java`

**Antes:**
```java
localizations.put("1", "-8.05428,-34.8813");     // Recife
localizations.put("2", "-9.39416,-40.5096");     // Petrolina
localizations.put("3", "-8.284547,-35.969863");  // Caruaru
localizations.put("4", "-8.284547,-35.969863");  // duplicata da 3
localizations.put("5", "-9.39416,-40.5096");     // duplicata da 2
```

**Depois:**
```java
localizations.put("1", "-8.05428,-34.8813");     // Recife
localizations.put("2", "-9.39416,-40.5096");     // Petrolina
localizations.put("3", "-8.284547,-35.969863");  // Caruaru
```

**Motivo:** As entradas 4 e 5 repetiam coordenadas já existentes, gerando cards duplicados na tela.

---

## Bug 6: Conversão Kelvin → Celsius incorreta

**Arquivo:** `app/src/main/java/com/example/findinglogs/model/util/Utils.java`

**Antes:**
```java
float tempKelvinRef = 275.15f;
```

**Depois:**
```java
float tempKelvinRef = 273.15f;
```

**Motivo:** A constante correta para converter Kelvin em Celsius é 273.15. O valor anterior (275.15) fazia todas as temperaturas aparecerem 2°C abaixo do real.

---

## Bug 7: Problemas de concorrência e Handler duplicado no ViewModel

**Arquivo:** `app/src/main/java/com/example/findinglogs/viewmodel/MainViewModel.java`

### 7a. ArrayList não-thread-safe

**Antes:** `new ArrayList<>()` acessado por múltiplos callbacks assíncronos.

**Depois:** `Collections.synchronizedList(new ArrayList<>())` protegido para acesso concorrente.

### 7b. Handler duplicado

**Antes:** `startFetching()` agendava um `postDelayed` e o `onSuccess` agendava outro, causando múltiplos ciclos de fetch em paralelo.

**Depois:** `startFetching()` apenas chama `fetchAllForecasts()` sem agendar. O agendamento ocorre em um único lugar: dentro dos callbacks, quando todas as respostas chegaram.

### 7c. Falhas parciais tratadas

**Antes:** Se uma cidade falhasse, o contador nunca atingia `total` e a tela nunca atualizava.

**Depois:** `onFailure` também incrementa o contador. Quando todas responderam, se houver pelo menos um sucesso, a tela é atualizada com os dados disponíveis.

### 7d. `removeCallbacks` no início de `fetchAllForecasts()`

Garante que ao iniciar uma nova busca (seja automática ou por refresh), qualquer agendamento anterior é cancelado, evitando buscas duplicadas.

---

# Migração para Kotlin + Jetpack Compose

O projeto foi inteiramente reescrito de Java + XML para Kotlin + Jetpack Compose. Todos os arquivos `.java` foram removidos e substituídos por `.kt`. Os layouts XML (`activity_main.xml`, `weather_item.xml`) não são mais utilizados, e a UI é declarativa via Compose.

## Arquitetura

O app segue MVVM (Model-View-ViewModel) com a seguinte estrutura:

```
app/src/main/java/com/example/findinglogs/
├── view/
│   ├── MainActivity.kt
│   ├── navigation/
│   │   └── WeatherNavHost.kt
│   ├── screens/
│   │   ├── WeatherListScreen.kt
│   │   ├── WeatherDetailScreen.kt
│   │   └── CitiesScreen.kt
│   └── theme/
│       └── WeatherTheme.kt
├── viewmodel/
│   └── MainViewModel.kt
└── model/
    ├── model/
    │   ├── Weather.kt
    │   ├── City.kt
    │   ├── GeoResult.kt
    │   └── Forecast.kt
    ├── repo/
    │   ├── Repository.kt
    │   └── remote/
    │       ├── ConnectionManager.kt
    │       ├── WeatherManager.kt
    │       └── api/
    │           ├── WeatherService.kt
    │           └── WeatherCallback.kt
    └── util/
        └── Utils.kt
```

---

## View

### MainActivity.kt

É o ponto de entrada do app. O Android cria esta Activity ao abrir o app. Ela faz apenas duas coisas: instancia o ViewModel e chama `setContent` com o tema Material e a navegação. Não contém lógica de tela.

### WeatherNavHost.kt

É o roteador do app. Define as três rotas (`list`, `detail/{index}`, `cities`), observa os estados do ViewModel via `observeAsState`, e distribui dados e callbacks para cada tela. Ao tocar num card, navega passando o índice; ao voltar, chama `popBackStack()`.

### WeatherListScreen.kt

Tela principal. Composta por:
- TopAppBar azul com título "Weather" e ícone de gerenciar cidades (`AddLocationAlt`)
- Lista de cards (`LazyColumn`) com pull-to-refresh
- FAB de refresh
- Cada card (`WeatherCard`) exibe emoji do clima, nome da cidade, descrição, temperatura em destaque, e métricas secundárias (máx, mín, umidade, pressão) com ícones nativos do Material
- Cards usam gradiente horizontal

### WeatherDetailScreen.kt

Tela de detalhes, aberta ao tocar num card. Exibe:
- Temperatura em destaque
- Descrição do clima e sensação térmica
- Previsão das próximas horas em `LazyRow` horizontal (até 8 itens de 3h cada, vindos do endpoint `/forecast`)
- Grid com cards de métricas: High, Low, Humidity, Pressure
- Fundo em degradê vertical

### CitiesScreen.kt

Tela de gerenciamento de cidades. Contém:
- Campo de busca
- Botão "Search" que consulta a API de geocoding (`/geo/1.0/direct`)
- Resultados em cards verdes com botão de adicionar (`Add`)
- Lista de cidades salvas com numeração (posição = ordem na tela principal) e botão de remover (`Delete`)

### WeatherTheme.kt

Arquivo de constantes e funções utilitárias de UI:
- `AppColors` — cores da TopAppBar e cores de texto
- `getWeatherEmoji(iconCode)` — mapeia código da API para emoji (ex: `01d` → ☀️)
- `getCardColor(iconCode)` — mapeia código da API para cor pastel do card (ex: `01` → azul, `09` → lavanda)

---

## ViewModel

### MainViewModel.kt

É o "cérebro" do app. Herda de `AndroidViewModel` (precisa de Context para SharedPreferences). Mantém seis estados via LiveData:

| Estado | Tipo | Uso |
|--------|------|-----|
| `weatherList` | `List<Weather>` | Dados climáticos exibidos nos cards |
| `isLoading` | `Boolean` | Controla indicador de loading e pull-to-refresh |
| `cities` | `List<City>` | Cidades salvas pelo usuário |
| `searchResults` | `List<GeoResult>` | Resultados da busca por nome de cidade |
| `isSearching` | `Boolean` | Controla spinner durante busca |
| `forecastItems` | `List<ForecastItem>` | Previsão por hora da cidade selecionada |

**Busca periódica:** usa `Handler` com `postDelayed` para buscar a cada 2 minutos. `removeCallbacks` no início de cada busca evita agendamentos duplicados.

**Ordem preservada:** usa `arrayOfNulls<Weather>(size)` para inserir cada resultado na posição correta do índice, independente da ordem de chegada dos callbacks.

**Ações expostas:**
- `refresh()` — busca manual imediata
- `searchCity(query)` — busca cidades por nome
- `addCity(geoResult)` — adiciona cidade e atualiza dados
- `removeCity(city)` — remove cidade e atualiza dados
- `loadForecast(index)` — carrega previsão por hora

---

## Model

### model/Weather.kt

Três data classes que representam a resposta do endpoint `/weather`:
- `Weather`: objeto raiz (nome da cidade, info principal, detalhes)
- `WeatherInfo`: temperaturas, pressão, umidade, sensação térmica
- `WeatherDetail`: ícone e descrição textual do clima

### model/City.kt

Representa uma cidade salva: nome, latitude e longitude. É usado para persistência e para disparar as requisições.

### model/GeoResult.kt

Resultado da API de geocoding: nome, coordenadas, país e estado. É usado na tela de busca de cidades.

### model/Forecast.kt

Modelos do endpoint `/forecast`:
- `ForecastResponse`: contém a lista de itens
- `ForecastItem`: dados de um intervalo de 3h (temperatura, ícone, horário)

### repo/Repository.kt

Ponto central de acesso a dados. Responsabilidades:
- Delega chamadas de rede ao `WeatherManager`
- Persiste a lista de cidades no `SharedPreferences` como JSON (via Gson)
- Fornece cidades padrão (Recife, Petrolina, Caruaru) na primeira execução
- Impede duplicatas ao adicionar cidade (compara por coordenadas)

### repo/remote/ConnectionManager.kt

Singleton (`object`) que configura duas instâncias Retrofit:
- `weatherConnection`, para `/data/2.5/` (clima e forecast)
- `geoConnection`, para `/geo/1.0/` (busca de cidades)

Ambas usam o mesmo `OkHttpClient` com timeout de 3s e logging de headers.

### repo/remote/WeatherManager.kt

Executa as três chamadas de rede:
- `retrieveForecast(lat, lon, callback)`: clima atual
- `retrieveHourlyForecast(lat, lon, onResult, onError)`: previsão por hora
- `searchCity(query, onResult, onError)`: geocoding

### repo/remote/api/WeatherService.kt

Interfaces Retrofit que definem os endpoints HTTP:
- `WeatherService`: `GET /weather` e `GET /forecast`
- `GeoService`: `GET /direct`

O Retrofit gera a implementação automaticamente a partir das anotações `@GET` e `@Query`.

### repo/remote/api/WeatherCallback.kt

Interface de callback para o clima atual: `onSuccess(Weather)` e `onFailure(String)`. É usada pelo ViewModel para receber resultados das requisições.

### util/Utils.kt

Função utilitária `getCelsiusFromKelvin(temp)`: subtrai 273.15, arredonda para inteiro, e retorna string formatada (ex: "28ºC").

---

## Funcionalidades novas

| Funcionalidade | Descrição |
|----------------|-----------|
| Tela de gerenciamento de cidades | Busca por nome, adicionar/remover, ordem preservada |
| Previsão por hora | Endpoint `/forecast`, exibido em LazyRow na tela de detalhes |
| Pull-to-refresh | Gesto de puxar a lista para baixo atualiza os dados |
| Persistência de cidades | SharedPreferences mantém a lista entre sessões |
| Loading visual | CircularProgressIndicator e spinner no pull-to-refresh |
| Cores por clima | Cards com cor pastel e degradê baseados na condição climática |

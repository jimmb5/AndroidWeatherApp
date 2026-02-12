## WeatherApp

### Retrofit

- retrofit kirjasto hoitaa http pyynnöt. Sovelluksessa kutsutaan esim getWeather ja retrofit hoitaa kaiken taustalla. Palautus aina JSONina.

### JSON - dataluokat Gson

- Gson muuttaa JSON-vastauksen suoraan WeatherResponse dataluokaksi. Näitä käytetään UI:ssa suoraan. esim. weather.main.temp

### Coroutines

- API on merkitty suspendilla  
- viewModelScope.launch tekee pyynnön taustalla säikeessä, estäen pääsäikeen interruptaamisen  

### UI-tila

- ViewModel käyttää Result sealed classia  
- ViewModel päivittää StateFlow muuttujan esim. _weatherState  
- Compose-näkymä kuuntelee sitä collectAsState() ja päivittää sen mukaan UI:n. Loading/Success/Error

### API-avain

- API avain on tallennettu ohjeiden mukaan local.properties tiedostoon
- täältä build.gradle.kts lukee sen ja laittaa sen buildConfigiin. Repository välittää tämän retrofitille.



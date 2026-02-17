## WeatherApp

### Room

- Room tallentaa ja lukee dataa paikallisesti. entity = taulu, DAO = kyselyt tietokannasta , Appdatabase= tietokanta. Repository käyttää DAO:a ja API:a, viewmodel hakee tilan
- Rakenne noudattaa seuraavaa mallia: data/model (entity,response), data/local (DAO, Appdatabase), data/repository(weatherRepository), ui/screens/weather (viewmodel, screen).

### Datavirta

- ensimmäisen kerran hakiessa tietyltä paikkakunnalta säätä -> viewmodel kutsuu repositorya. Repository katsoo tietokannasta(roomista) onko alle 30min vanhaa dataa saatavilla. Jos on, palauttaa sen. Jos ei, haetaan uudestaan APIlla ja tallennetaan Roomiin. Viewmodel vastaa tilan päivityksestä (loading/success/error), UI kuuntelee collectAsState()lla ja renderöi sen mukaan. Käynnistyksessä viewmodel lukee viimeisimmän sään roomista ja näyttää sen.

### Välimuisti

- Viimeisin haettu sää menee Roomiin. UI näyttää aina sieltä luetun datan mikäli saatavilla, jos data yli 30 min vanhaa haetaan se uudestaan APIlla ja tallennetaan. Muuten käytetään välimuistia.

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



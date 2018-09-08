SnapShop
========

PS! Current project is deprecated (lot's of stuff could be improved, uses deprecated Camera API to support old devices etc...) 
Used backend is rewritten to microservices architecture.

Snapchatist inspiratsiooni saanud osta-müü-vaheta keskkond (peamiselt eraisikutele).
Kasutajal on väga lihtne lisada uusi tooteid - Teeb otse appist pildi, lisab pealkirja, kirjelduse, hinna ning ongi valmis.
Postitused aeguvad teatud aja jooksul, kuid neid on võimalik uuendada.

Hiljem võimalus lisada RESTful Webservice osale ka veebikeskkond.

Current REST Endpoints
----------------------

https://documenter.getpostman.com/view/3644844/snapshop/RVnPJNvP (Uuendatud)

Liikmed
-------

- Priit Käärd (prkaar)

Funktsionaalsus
---------------

**Android rakendus**

- Rakendusesisene limiteeritud funktsionaalsusega kaamera - ei võimalda lisada pilte väljaspoolt.
- Listview / Gridview toodetest avalehel.
- Toote vaade
- Ostukorv
- Checkout - Konkreetse projekti raames näiteks arvetega või maksekeskuse testkeskkonnas.
- Konto süsteem - sisselogimine, kasutaja seadete haldamine.
- Splash screen
- Android notifications uute toodete lisandumisel, kui on seadetest aktiveeritud (?)
- Seaded

**Spring Boot RESTful Webservice**

- Tooted CRUD - Loo, Loe, Uuenda, Kustuta
- Pildid - lisa, kustuta, kuva
- Kasutajad - Custom UserDetails service. Spring security Basic Authentication. Hiljem võimalik integreerida OAuth 2.0

Töö käigus ilmselt lisandub.

Ekraanivaated
-------------

**Splash screen**

Algeliseks laadimiseks animeeritud vaade.

**Esileht**

Viimati lisatud tooted custom GridView'na. 
Tooteplokid sisaldavad minimaalselt pilti ja hinda
Ostukorvi nupp
Kategooriate jaoks slideable fragment. Kategooriaid võimalik kombineerida otsingusõnaga.
Otsinguriba

**Kaamera**

Rakendusesisene kaamera.
Sisaldab vaid kaamera surfaceview'd ja nuppu pildi tegemiseks esialgses plaanis.
Keelatud on välises allikast tulevad meediafailid.

**Toote vaade**

Galerii slideshow
Pealkiri
Hind
Lühikirjeldus
Muu info
Lisa ostukorvi

**Ostukorv**

List valitud toodetest
Iga toode sisaldab minimaalselt toote pealkiri, hind, kogus.
Checkout nupp

**Seaded**

Minu tellimused
Konto seaded
Logi välja
...

Plaan
-----

- \6. nädal: Põhiline REST API valmis vähemalt toodete jaoks Spring Bootis. Androidi projekti põhi loodud.
- \8. nädal: Androidi poolel tehtud põhiline front end ilma funktsionaalsuseta.
- \10. nädal: Edasiarendus frontendile (Toote lisamine). REST APIga sidumine.
- \12. nädal: Checkout maksekeskus test keskkonnaga.
- \14. nädal: REST ja Android viimased viimistlused.
- \15. nädal: Esitamine

Punktisoov
----------

8 - 10 punkti

Tehnoloogiad
------------

- Spring Boot
- Spring JPA
- Hibernate
- Spring Security
- Android
- PostgreSQL

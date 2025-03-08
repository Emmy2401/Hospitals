# Hospitals
Preuve de concept (PoC) pour MedHead, un consortium d'institutions médicales britanniques. Le projet repose sur une architecture de microservices (gestion des utilisateurs, hôpitaux, géolocalisation) avec un front-end VueJS. Utilisation de Spring Boot, JWT, CI/CD, tests automatisés et charge avec JMeter.

# MedHeadPoc

MedHead est un POC développé en JavaSpringBoot pour le Back-end et Vue.Js pour le front end

## Prérequis:

### Backend

- Java 23
- Maven
- Base de donnée mysql



### Front End :

- Node 18

### Configuration :

Conserver les applications.properties tel quel, pour faire tourner sur les containers utiliser les ports dédiés :

- 8090 Hospital
- 8091 Users
- 8092 Distance
- 8080 Front

### Authentification :

Authentificaiton basique implémenter qui nécessite un nom de compte sous format "mail" avec un mot de passe,. Le tout permettant ensutie d'obtenir  une JwtToken pour pouvoir accèder aux routes protégés de l'application.

### Test :

- basic.cy.js (front end)
- AppUnitTests.spec.js (front end)
- Voir dossier Test pour les parties back-end commande à l'ancer : mvn test

### Endpoint disponibles

- /me (Users)
- /register (Users)
- /hospitals (Hospitals)
- /hospitals/add (Hospitals)
- /hospitals/detail/id (Hosptilas)
- /searchCriteria (Hospitals lien avec Distance pour le calcul)


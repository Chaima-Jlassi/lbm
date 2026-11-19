# Library Manager – Backend (Spring Boot)

Spring Boot 3.3 · Java 17+ · Spring Web / Data JPA / Security · JWT · PostgreSQL · Mail

## Lancer
1. Démarrer PostgreSQL. Le plus simple : `docker compose up -d` à la racine du projet (crée la base `lbm_db`,
   utilisateur `postgres` / mot de passe `postgres`). Sans Docker : `CREATE DATABASE lbm_db;` dans votre PostgreSQL.
2. Variables d'environnement (toutes optionnelles, valeurs par défaut dans `application.properties`) :
   `DB_URL` (défaut `jdbc:postgresql://localhost:5432/lbm_db`), `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (base64, ≥ 32 octets),
   `ADMIN_EMAIL`, `ADMIN_PASSWORD`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_USER`, `MAIL_PASSWORD`, `MAIL_FROM`.
3. `mvn spring-boot:run`  → http://localhost:8080 (les tables sont créées automatiquement par Hibernate).

Au premier démarrage : rôles `ADMIN` et `BIBLIOTHECAIRE` + compte admin par défaut
`admin@lbm.com` / `Admin@123` (à changer via `ADMIN_EMAIL` / `ADMIN_PASSWORD`).

## API
| Méthode | URL | Accès |
|---|---|---|
| POST | /api/auth/register | public (rôle BIBLIOTHECAIRE attribué, email envoyé à l'admin) |
| POST | /api/auth/login | public → renvoie `token` JWT |
| GET, POST | /api/auteurs, /api/livres | ADMIN, BIBLIOTHECAIRE |
| GET /{id} | /api/auteurs/{id}, /api/livres/{id} | ADMIN, BIBLIOTHECAIRE |
| PUT, DELETE | /api/auteurs/{id}, /api/livres/{id} | ADMIN |
| CRUD | /api/users, GET /api/roles | ADMIN |

En-tête à envoyer : `Authorization: Bearer <token>`.

Corps d'un livre : `{ "titre":"…", "isbn":"…", "categorie":"…", "nbPages":300, "nbExemplaires":5, "description":"…", "idAuteur":1 }`.
Corps d'un utilisateur (admin) : `{ "email":"…", "password":"…", "tel":"…", "nom":"…", "prenom":"…", "roles":["ADMIN"] }`.

## Tests Postman
Importer `LibraryManager.postman_collection.json` : lancer **Login admin** (le token est stocké automatiquement
dans la variable `token`), puis les autres requêtes. Le dossier « Sécurité » vérifie les 401/403.

## Frontend Angular
Le dossier `frontend/` contient l'application Angular 19 (Bootstrap, navbar/sidebar façon Argon, hash routing).
- Dev : `cd frontend && npm install && npm start` → http://localhost:4200 (les appels `/api` sont proxifiés vers `localhost:8080` via `proxy.conf.json`).
- Build : `ng build` puis copier `dist/frontend/browser/*` dans `backend/src/main/resources/static/` (déjà fait dans cette archive).
- Après `mvn clean package`, `java -jar target/lbm.jar` sert API + interface sur http://localhost:8080.

## Déploiement (partie 3 du CDC)
1. Builder Angular (`ng build`) et copier le contenu de `dist/.../browser` dans `src/main/resources/static/`.
2. `mvn clean package` → `target/lbm.jar`
3. Sur le VPS : `java -jar lbm.jar` (avec les variables d'environnement ci-dessus).
   Utiliser `useHash` (HashLocationStrategy) côté Angular pour éviter une règle de fallback SPA.
4. CORS : `http://localhost:4200` est autorisé pour le développement (voir `SecurityConfig`).

## Choix de conception
- Inscription : jamais de rôle ADMIN auto-attribué ; l'admin change les rôles via `/api/users`.
- ISBN unique (409 en cas de doublon) ; suppression d'un auteur ayant des livres refusée (409).
- Un échec d'envoi SMTP n'empêche pas l'inscription (loggé uniquement).

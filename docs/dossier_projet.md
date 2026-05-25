# Dossier Projet — NovaBet
> Mis à jour au Sprint 1 — 10 mai 2026

---

## 1. Présentation du projet

**Nom du projet :** NovaBet

**Concept :** Application de bureau Java/JavaFX fusionnant les marchés de prédiction (*prediction markets*) et des mécaniques de casino. L'utilisateur prédit l'issue d'événements réels (sport, politique, crypto, IA…), gagne ou perd de la monnaie fictive, et se voit proposer de convertir ses gains en crédits casino via une offre unique au moment du retrait.

**Proposition de valeur :** Un funnel de monétisation agressif — l'OTO (*One Time Offer*) — qui convertit les gains de marchés en crédits casino au moment du retrait, générant ainsi un engagement prolongé et une rétention maximale.

**Cible (ICP) :** Jeunes adultes (18-35 ans), familiers de l'écosystème Crypto/DeFi, déjà utilisateurs de plateformes de paris sportifs, de trading ou de marchés de prédiction.

---

## 2. Organisation de l'équipe

### Rôles Sprint 1

| Membre | Rôle | Responsabilités |
|--------|------|-----------------|
| Hugo | Product Owner | Vision produit, UI/UX FXML, onboarding, services métier |
| Siloe | Scrum Master | Organisation, cérémonies Scrum, coordination |
| Antonio (Jusce) | Lead Développeur | Logique métier paris, tests unitaires, UI marchés |
| Kahina | Développeuse | Modélisation SQL, DAOs JDBC |

> Les rôles tourneront à chaque sprint.

### Stack Technique

- Java 21 / JavaFX 21
- Maven
- MySQL 8 / JDBC
- BCrypt (jBCrypt 0.4) — hashage des mots de passe
- JUnit 5 — tests unitaires
- Git / GitHub (GitFlow : main, develop, feature/*)
- Gestion de projet : Trello

---

## 3. Backlog & Planification

### Sprint 1 — Cadrage + MVP

| ID | User Story | Statut | Responsable |
|----|-----------|--------|-------------|
| US-01 | En tant qu'utilisateur, je peux créer un compte | ✅ Fait | Hugo |
| US-01b | En tant qu'utilisateur, je peux me connecter | ✅ Fait | Hugo |
| US-02 | En tant qu'utilisateur, je peux déposer de la monnaie fictive | ✅ Fait | Hugo |
| US-03 | En tant qu'utilisateur, je peux voir la liste des marchés ouverts | 🔄 En cours | Antonio |
| US-04 | En tant qu'utilisateur, je peux acheter des parts YES/NO | 🔄 En cours | Antonio |

### Sprint 2 — Enrichissement (Logique Métier)

- En tant qu'utilisateur, je peux voir mon portefeuille et mes positions en cours.
- En tant qu'admin/système, je peux résoudre un marché (YES/NO) et payer les gagnants.
- En tant qu'utilisateur, je peux consulter l'historique de mes transactions.
- En tant qu'utilisateur, je peux créer un nouveau marché de pari.

### Sprint 3 — Finalisation & Funnel Casino

- En tant qu'utilisateur, au moment de demander un retrait, une offre OTO casino s'affiche.
- En tant qu'utilisateur, je peux jouer aux machines à sous avec mes crédits casino.
- Polissage de l'interface et mise à jour en temps réel des soldes.

---

## 4. Avancement Sprint 1

**Avancement global estimé : 78%**

### Ce qui fonctionne (end-to-end)

- **Onboarding complet :** création de compte → validation → dépôt → catégories
- **US-01 :** `registerUser()` insère l'utilisateur en base MySQL avec mot de passe hashé en BCrypt. `loginUser()` vérifie les identifiants via SELECT + BCrypt.checkpw().
- **US-02 :** `createDeposit()` insère une transaction et met à jour le wallet en base, en transaction atomique.
- **Design System :** interface "Void Black & Gold" complète avec validation dynamique temps réel.
- **Tests unitaires :** `BettingServiceImplTest.java` (400+ lignes) — tous les cas de paris testés, tous les tests passent.
- **DAOs JDBC :** 8 DAOs complets (users, bets, events, outcomes, transactions, wallets, games, game_sessions) livrés par Kahina.

### Ce qui est en cours (branches non mergées)

- US-03 — Liste des marchés : implémentée sur `feature/ui-markets-list` (Java pur, sans FXML)
- US-04 — Achat YES/NO : logique métier sur `feature/betting-service`, tests passent

---

## 5. Difficultés rencontrées

| Niveau | Problème | Impact |
|--------|----------|--------|
| 🔴 CRITIQUE | Conflit de nommage `com.polymarket` vs `com.novabet` entre branches | Blocage au merge |
| 🔴 CRITIQUE | Kahina a pushé sur `main` directement au lieu d'une branche `feature/` | Hors convention GitFlow |
| 🟠 MAJEUR | Travail en silos — branches non intégrées assez souvent | Pas d'intégration continue |
| 🟠 MAJEUR | Disparité technique : Hugo utilise FXML, Antonio utilise Java pur | Incohérence maintenabilité |

---

## 6. Décisions prises

| Date | Décision | Raison |
|------|----------|--------|
| Avril 2026 | Architecture MVC + Hexagonale | Isolation de la logique métier pour permettre les tests sans BDD |
| Avril 2026 | BCrypt pour le hashage | Sécurité : jamais de mot de passe en clair en base |
| Avril 2026 | Validation regex côté front | UX : erreurs immédiates sans round-trip serveur |
| Mai 2026 | Renommage com.polymarket → com.novabet | Cohérence du projet suite au rebranding |
| Sprint 2 | Décision à prendre : FXML ou Java pur | Uniformiser l'approche UI pour la maintenabilité |

---

## 7. Rétrospective Sprint 1

### Ce qui a bien marché
- Design System cohérent et professionnel dès le Sprint 1
- Architecture hexagonale sur le BettingService : tests unitaires sans BDD
- Onboarding entièrement connecté à MySQL en fin de sprint
- Bonne nomenclature des branches (GitFlow respecté côté Hugo et Antonio)

### Ce qui n'a pas marché
- Pas assez de synchronisation entre membres → branches pas mergées
- Le nommage de package a divergé entre les membres sans qu'on s'en aperçoive à temps
- Les DAOs de Kahina poussés sur `main` directement et sous l'ancien package

### Actions Sprint 2
- Merge global sur `develop` avec résolution des conflits de nommage
- Décision d'équipe : FXML obligatoire pour toutes les vues
- Intégration continue hebdomadaire (merge sur develop chaque semaine)

---

## 8. Budget Sprint 1

**Budget initial : 100 000 €**

| Rôle | Tarif | Jours | Coût Total |
|------|-------|-------|-----------|
| Product Owner | 1 000 €/j | 2 jours | 2 000 € |
| Scrum Master | 800 €/j | 1 jour | 800 € |
| Dev Front | 400 €/j | 6 jours | 2 400 € |
| Dev Full Stack | 550 €/j | 6 jours | 3 300 € |
| Dev Back | 600 €/j | 3 jours | 1 800 € |
| Testeur | 800 €/j | 1 jour | 800 € |
| **TOTAL** | | **19 jours** | **11 100 €** |

**Budget consommé Sprint 1 : 11 100 €**
**Budget restant : 88 900 €**

---

## 9. Projection Sprint 2

**Budget prévisionnel Sprint 2 :** ~15 000 € (intégration + logique métier complexe)

### Priorités

| Priorité | Tâche |
|----------|-------|
| P0 | Merge global — résolution conflits com.polymarket / com.novabet |
| P1 | Connexion Front ↔ BDD — DAOs branchés au controller |
| P2 | Portfolio utilisateur — positions en cours, historique transactions |
| P3 | Résolution de marchés — paiement automatique des gagnants |

---

## 10. UML Global

### Architecture applicative

```
┌─────────────────────────────────────────────┐
│                  VUES (JavaFX)               │
│   OnboardingView.fxml   │  MarketDetailView  │
└──────────────┬──────────────────────────────┘
               │ @FXML
┌──────────────▼──────────────────────────────┐
│              CONTROLLERS                     │
│         OnboardingController                 │
└──────────────┬──────────────────────────────┘
               │
┌──────────────▼──────────────────────────────┐
│               SERVICES                       │
│   UserService  │  TransactionService         │
│   BettingServiceImpl (architecture hexa)     │
└──────────────┬──────────────────────────────┘
               │ JDBC
┌──────────────▼──────────────────────────────┐
│            BASE DE DONNÉES                   │
│    DatabaseManager → MySQL (novabet)         │
└─────────────────────────────────────────────┘
```

---

## 11. MCD / MLD

### MCD (entités et associations)

```
USER (id, username, email, password_hash, created_at)
WALLET (id, real_balance, virtual_balance)
TRANSACTION (id, type, amount, created_at)
EVENT (id, title, description, status, resolution, created_at)
OUTCOME (id, label, odds)
BET (id, amount, potential_win, status, created_at)
GAME (id, name, type)
GAME_SESSION (id, bet_amount, result, win_amount, created_at)

POSSEDE   : USER (1,N) ←→ (1,1) WALLET
EFFECTUE  : USER (1,N) ←→ (0,N) TRANSACTION
PARIE     : USER (1,N) ←→ (0,N) BET
CONCERNE  : BET  (1,N) ←→ (1,1) OUTCOME
APPARTIENT: OUTCOME (1,N) ←→ (1,1) EVENT
JOUE      : USER (1,N) ←→ (0,N) GAME_SESSION
EST_SUR   : GAME_SESSION (1,N) ←→ (1,1) GAME
```

### MLD (tables relationnelles)

```
USER        (id PK, username, email UNIQUE, password_hash, created_at)
WALLET      (id PK, real_balance, virtual_balance, #user_id FK→USER)
TRANSACTION (id PK, type, amount, created_at, #user_id FK→USER)
EVENT       (id PK, title, description, status, resolution, created_at, #outcome_id FK→OUTCOME)
OUTCOME     (id PK, label, odds, #bet_id FK→BET)
BET         (id PK, amount, potential_win, status, created_at)
GAME        (id PK, name, type, #game_session_id FK→GAME_SESSION)
GAME_SESSION(id PK, bet_amount, result, win_amount, created_at)

EFFECTUE    (#user_id FK→USER, #transaction_id FK→TRANSACTION)
PARIE       (#user_id FK→USER, #bet_id FK→BET)
JOUE        (#user_id FK→USER, #game_session_id FK→GAME_SESSION)
```

---

## 12. Choix techniques

| Choix | Justification |
|-------|---------------|
| JavaFX + FXML | Imposé par le projet. FXML permet la séparation vue/logique et le data-binding via @FXML |
| Architecture MVC | Séparation claire des responsabilités (vue, contrôleur, service) |
| Architecture Hexagonale (BettingService) | Isolation de la logique métier — permet les tests unitaires sans base de données |
| BCrypt (jBCrypt) | Standard de sécurité pour le hashage des mots de passe. Salage automatique |
| Transaction JDBC atomique | Le dépôt insère une transaction ET met à jour le wallet en une seule opération atomique — impossible d'avoir un état incohérent |
| MySQL + JDBC | Imposé par le projet. Config externalisée dans `config.properties` |
| PreparedStatement | Protection contre les injections SQL |

---

## 13. État de la documentation

| Document | État |
|----------|------|
| `docs/dossier_projet.md` | ✅ À jour — Sprint 1 |
| `README.md` | ✅ Instructions installation et lancement |
| `bdd_novamarket.sql` | ✅ Schéma complet — 8 tables |
| Javadoc | ❌ Non généré — prévu Sprint 3 |
| Guide utilisateur | ❌ Non rédigé — prévu Sprint 3 |
| Guide d'installation | ✅ Dans README.md |

---

## 14. Déclaration d'usage de l'IA

L'intelligence artificielle a été utilisée dans ce projet de manière transparente pour les activités suivantes :

| Usage | Outil | Détail |
|-------|-------|--------|
| Brainstorming | Claude | Idéation du concept NovaBet, modèle OTO, proposition de valeur |
| Aide au code | Claude | Génération de `UserService.java`, `TransactionService.java`, modifications de `OnboardingController.java` |
| Aide à la documentation | Claude | Rédaction du présent dossier projet |
| Support de soutenance | Claude | Génération du PowerPoint Sprint 1 Review |

Tout le code généré par IA a été relu, compris et validé par les membres de l'équipe avant intégration.

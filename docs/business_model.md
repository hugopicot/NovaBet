# Business Model — NovaBet
> Document stratégique — Sprint 2 — Mai 2026
> Auteurs : Hugo (PO), Siloe (SM), Antonio (Lead Dev), Kahina (Dev)

---

## 0. Executive Summary

**NovaBet** est une plateforme hybride **marché de prédiction + casino social** ciblant les 18-35 ans déjà actifs sur les paris sportifs, le trading crypto et les marchés de prédiction (Polymarket, Kalshi, Manifold).

**La proposition de valeur unique :** un funnel One-Time-Offer (OTO) qui convertit les gains d'un marché de prédiction en crédits casino au moment du retrait. Là où Polymarket laisse partir 100% des utilisateurs après cashout, NovaBet en retient ~30% sur une seconde session monétisable.

**Le modèle économique repose sur 4 leviers complémentaires :**
1. **Spread (vig)** — 2% sur chaque trade YES/NO (modèle Polymarket / Kalshi)
2. **House edge casino** — 4-8% sur les jeux post-OTO (machines à sous, mini-roulette)
3. **Premium tier "NovaPro"** — 19,99 €/mois (data avancée, alertes, frais réduits)
4. **Marchés sponsorisés (B2B)** — marques qui sponsorisent un marché viral (Red Bull, crypto exchanges, médias)

**Cible financière à 36 mois :** 50 000 utilisateurs actifs mensuels, 8 M€ d'ARR, EBITDA positif à partir de M18.

**Statut légal retenu :** modèle **"social casino / sweepstakes"** en monnaie virtuelle uniquement (Gold Coins / Sweep Coins) — cadre légal validé aux US par Stake.us et Chumba Casino, en cours d'expansion européenne. Évite la licence ANJ (paris à cote en France) qui coûte 100k€ + 9 mois d'instruction et bloque les prediction markets.

---

## 1. Marché & Opportunité

### 1.1 Tendance de fond

Le marché des prediction markets a explosé en 2024-2025 :
- **Polymarket** : 9 Md$ de volume sur l'élection US 2024, valorisation 1 Md$+ (Founders Fund, Vitalik Buterin)
- **Kalshi** : licence CFTC US obtenue 2023, 50 M$ levés, partenariat Robinhood en 2025
- **Manifold Markets** : 50k utilisateurs actifs, modèle "play money" devenu profitable

En parallèle, le marché du **social casino** (jeux d'argent sans argent réel) :
- 8,9 Md$ en 2025 (source : Statista), CAGR 9% jusqu'en 2030
- Leaders : Playtika (Slotomania), Stake.us, Chumba Casino

**L'angle NovaBet :** personne ne fusionne les deux. Polymarket ne monétise pas la session post-cashout, les social casinos n'offrent pas de prediction markets.

### 1.2 TAM / SAM / SOM

| Marché | Taille | Justification |
|--------|--------|---------------|
| **TAM** — Marché mondial du gambling en ligne | 95 Md$ (2025) | H2 Gambling Capital |
| **SAM** — Prediction markets + social casino EU/US 18-35 ans | 12 Md$ | Croisement Statista + Newzoo |
| **SOM 3 ans** — Cible NovaBet (parts captables) | 80 M€ | Hypothèse 0,67% du SAM EU sur niche crypto-natives |

### 1.3 Concurrence

| Acteur | Force | Faiblesse exploitable par NovaBet |
|--------|-------|-----------------------------------|
| **Polymarket** | Marque, liquidité, viralité | Pas de monétisation post-cashout, UX crypto-only (wallet, USDC) |
| **Kalshi** | Licence CFTC US, intégration Robinhood | UX austère, pas de gamification, US-only |
| **Manifold** | Communauté ULTRA engagée | Play money, pas de revenus, pas d'app |
| **Stake.us** | Maître du sweepstake model | Pas de prediction markets, expérience casino "à l'ancienne" |
| **Betclic / Winamax** | Licence ANJ, marque FR | Cadre strict (paris sport uniquement), pas d'innovation, cible vieillissante |

**Notre différenciation = funnel OTO casino + UX moderne + cible crypto-native sous-servie en EU.**

---

## 2. Cadre Légal & Statut Juridique

### 2.1 Le problème

En France, l'ANJ (Autorité Nationale des Jeux, ex-ARJEL) régule strictement :
- ✅ Paris sportifs à cote — licence obligatoire (Betclic, Winamax, Unibet)
- ✅ Paris hippiques — monopole PMU
- ✅ Poker en ligne — licence séparée
- ❌ **Prediction markets sur événements non-sportifs (politique, IA, météo) → NON AUTORISÉS en France**, considérés comme jeux d'argent illégaux

Polymarket est ainsi **techniquement interdit** d'accès depuis la France (géo-blocage demandé en 2022).

### 2.2 La solution NovaBet : modèle Sweepstakes / Social Gaming

**Principe (validé US, en expansion EU) :**
- L'utilisateur achète des **"Gold Coins"** (monnaie virtuelle, sans valeur monétaire — utilisée pour s'amuser)
- À chaque achat de Gold Coins, NovaBet **OFFRE GRATUITEMENT** des **"Sweep Coins"** (monnaie promotionnelle)
- Les Sweep Coins peuvent être joués sur les marchés ET **convertis en cash réel** (taux 1 SC = 1 €) au-dessus d'un seuil de 50 SC
- Comme les Sweep Coins sont **offerts gratuitement** (et obtenables aussi via tâches gratuites : login quotidien, partage, referral), juridiquement ce n'est **pas un jeu d'argent**

**Précédents légaux :**
- 🇺🇸 Stake.us → 500 M$ ARR estimé en 2024
- 🇺🇸 Chumba Casino (Virtual Gaming Worlds) → 1,5 Md$ de revenus 2023
- 🇨🇦 modèle reconnu par la Cour Suprême US 2024 (affaire VGW vs Washington State)

**Coût juridique NovaBet :** ~25 k€ (audit + rédaction CGU + conformité KYC light) vs **>200 k€ + 12 mois** pour une licence ANJ.

### 2.3 KYC & AML (obligation européenne quel que soit le statut)

- Vérification d'identité Stripe Identity au-delà de 100 € de Sweep Coins convertis cumulés
- Limites journalières par défaut (1 000 € retrait/jour)
- Reporting Tracfin si transactions suspectes > 10 k€
- Auto-exclusion + cooling-off period (obligation EU 2024)

---

## 3. Le Funnel OTO — Cœur du modèle

### 3.1 Vue d'ensemble

```
ACQUISITION ──→ DÉPÔT INITIAL ──→ PARI(S) ──→ GAIN ──→ ⚠️ MOMENT DE VÉRITÉ ⚠️
                                                              │
                                ┌─────────────────────────────┴─────────────────────────────┐
                                ▼                                                            ▼
                       Demande de RETRAIT                                          Décide de re-parier
                                │                                                            │
                                ▼                                                            │
                       ⭐ POPUP OTO ⭐                                                       │
                       "Convertis tes 250 SC en                                              │
                       400 crédits casino + 50 free spins                                    │
                       (offre expire dans 03:00)"                                            │
                                │                                                            │
                  ┌─────────────┴─────────────┐                                              │
                  ▼                           ▼                                              │
              ACCEPTE                      REFUSE                                            │
              (~30% target)              → cashout                                           │
                  │                          $                                               │
                  ▼                                                                          │
            SESSION CASINO ◀──────────────────────────────────────────────────────────────────┘
                  │
                  ▼
            HOUSE EDGE 4-8%
            → NovaBet récupère en moyenne 28% du jackpot OTO sur la session
```

### 3.2 Hypothèses de conversion (benchmarkées)

| Étape | Taux | Source / benchmark |
|-------|------|--------------------|
| Visiteur → inscrit | 8% | Médiane funnels iGaming 2024 (eGaming Review) |
| Inscrit → premier dépôt | 22% | Idem |
| Gagnant → demande retrait | 65% | Polymarket / Kalshi (sessions > 1h) |
| **Demande retrait → ACCEPTE l'OTO** | **30%** | Cible — basé sur Stake.us "rakeback offers" (28-35%) |
| Crédits OTO → relâchés intégralement par le joueur | 88% | House edge mathématique sur slots NovaBet (RTP 96%) sur 8 spins moyens |

**Marge effective NovaBet sur un OTO accepté :**
- Joueur convertit 250 SC en 400 crédits casino (bonus 1,6x = +60%)
- House edge 4% par tour, 8 tours moyens → 32% des crédits absorbés
- 400 × 32% = 128 crédits = **128 € de revenu casino**
- Coût de l'offre OTO (le bonus de 60%) = 150 SC fictifs = 0 € (monnaie virtuelle)
- **Marge nette ≈ 128 € sur un cashout de 250 € qui sinon générait 0 € post-spread**

### 3.3 Psychologie de l'OTO — pourquoi ça marche

1. **Effet de propriété** (Kahneman) : l'utilisateur a "gagné" cet argent, le perdre fait moins mal que de payer
2. **Loss aversion inversée** : "si je refuse, je perds une opportunité"
3. **Urgence (3 minutes)** : empêche le rational thinking
4. **Bonus visible (+60%)** : l'utilisateur a l'impression d'un gain instantané
5. **Frictionless** : pas de re-dépôt à faire, conversion en 1 clic

---

## 4. Sources de Revenus (4 piliers)

### Pilier 1 — Spread / Vig sur les trades (revenu principal)

- **2% de fee** sur chaque transaction YES/NO (entrée ET sortie)
- Benchmark : Polymarket prélève 0% (mais bid-ask spread de fait ~3%) ; Kalshi 2% ; PredictIt 10% (prohibitif)
- **Hypothèse :** ARPU 12 €/mois sur trades pour un utilisateur actif

### Pilier 2 — House edge casino (revenu différenciant)

- Slots NovaBet : RTP 96% (house edge 4%)
- Mini-roulette : RTP 95% (house edge 5%)
- Crash game : RTP 94% (house edge 6%)
- **Hypothèse :** sur les 30% d'utilisateurs qui acceptent l'OTO, ARPU casino additionnel 18 €/mois

### Pilier 3 — Abonnement "NovaPro" (revenu récurrent prédictible)

| Tier | Prix | Bénéfices |
|------|------|-----------|
| **Free** | 0 € | Marchés standards, spread 2%, retrait à J+3 |
| **NovaPro** | **19,99 €/mois** | Spread réduit à 1%, retrait instantané, data avancée (volume, profondeur), alertes prix custom, badge profil, accès anticipé nouveaux marchés (24h avance) |
| **NovaPro+** | **49,99 €/mois** | Tout NovaPro + API trading 1000 req/min, market maker rebates (-0,2% par trade), accès Discord VIP |

**Hypothèse de conversion :** 8% des utilisateurs actifs prennent NovaPro, 1,5% prennent NovaPro+
→ ARPU abonnement moyen sur base totale : ~2,40 €/mois

### Pilier 4 — Marchés sponsorisés B2B

Une marque sponsorise un marché viral pour 5 à 50 k€ selon visibilité :
- "Quand Apple sortira-t-il son casque AR ?" sponsorisé par MacGeneration
- "Quel club gagnera la Ligue des Champions ?" sponsorisé par RMC Sport
- "Solana dépassera-t-il 500$ en 2026 ?" sponsorisé par Binance

**Offre B2B :**
- Visibilité marque sur le marché (logo, lien)
- Newsletter dédiée à 50k abonnés
- 1 post organique X/Twitter NovaBet
- Reporting impressions + clics

**Hypothèse Y2 :** 30 marchés sponsorisés/an × 15 k€ moyen = **450 k€**

---

## 5. Stratégie de Distribution (Go-To-Market)

### Phase 1 — Lancement (M0 → M6) — Communauté & Buzz

**Cible primaire : 5 000 utilisateurs actifs**

| Canal | Tactique | Budget M0-M6 | KPI cible |
|-------|----------|--------------|-----------|
| **Twitter/X organique** | Compte officiel + 3 micro-comptes thématiques (politique, crypto, IA). 1 thread/jour sur le marché le plus actif. Stunt : "On va donner 10 000 € de Sweep Coins au prochain follower qui devine l'élection X" | 0 € (sweat equity) | 15k followers, 3M impressions/mois |
| **Twitch/YouTube creators** | Sponsorisation de 20 créateurs FR/EU crypto-poker-trading (Hasheur, Owen, ZQSD…). Code promo + revshare 20% à vie sur referrals | 80 k€ | 800 inscrits via codes |
| **Reddit** | r/wallstreetbets, r/CryptoCurrency, r/PredictionMarkets. Pas de posts promo (interdit), mais markets viraux qui se partagent organiquement | 0 € | 3 posts > 1k upvotes |
| **Product Hunt + Indie Hackers** | Launch officiel mois 3 | 0 € | Top 5 du jour |
| **Stunt marketing** | "Marché caché" : prédire le prochain meme à viraliser. Récompense 1 BTC (~50k€) au gagnant. Buzz garanti | 50 k€ | 5M impressions earned media |

**Budget acquisition Phase 1 : 130 k€**
**CAC cible : 26 €/utilisateur actif**

### Phase 2 — Scale (M7 → M18) — Performance Marketing

**Cible : passer de 5k à 30k utilisateurs actifs**

| Canal | Tactique | Budget M7-M18 | CAC cible |
|-------|----------|----------------|-----------|
| **Meta Ads (Instagram/Facebook)** | Vidéos UGC de gagnants ("J'ai gagné 1200€ en pariant sur Trump"), retargeting visiteurs site | 200 k€ | 32 € |
| **TikTok Ads** | Format Spark Ads sur créateurs déjà partenaires Phase 1. Hooks : "Le seul site qui paie quand tu retires" | 150 k€ | 28 € |
| **Google Ads** | Search "polymarket alternative", "kalshi france", "prediction market eu". Display sur sites finance | 120 k€ | 35 € |
| **Programme d'affiliation** | 30% revshare à vie pour affiliés. Plateforme : Income Access ou maison | 80 k€ commissions | 22 € |
| **SEO** | 200 articles "Will X happen ?" optimisés sur événements à venir (élections, crypto, météo, sport). Le contenu génère du market signup organique | 60 k€ (rédacteurs + tooling) | 8 € |

**Budget acquisition Phase 2 : 610 k€**
**CAC blended cible : 24 €**

### Phase 3 — Rétention & Expansion (M19 → M36)

**Cible : 30k → 50k MAU + élargissement géographique (BE, CH, LU, QC)**

| Levier | Tactique |
|--------|----------|
| **Programme de fidélité** | Système de XP, niveaux (Bronze → Diamond), récompenses tangibles (cash, merch, événements IRL) |
| **Tournois mensuels** | Leaderboard avec prize pool 50k€. Génère de l'engagement et du PR |
| **Push automatisés** | Notifs sur marchés où l'utilisateur a une position (variation > 5%) |
| **Events IRL** | "NovaBet Night" à Paris, Bruxelles, Genève — networking crypto/trading + open bar = bouche-à-oreille premium |
| **Localisation** | Allemand (M22), Espagnol (M28) |

---

## 6. Unit Economics

### 6.1 ARPU détaillé (mois 24, utilisateur actif moyen)

| Source | Montant /mois | Justification |
|--------|---------------|---------------|
| Spread sur trades | 9,00 € | 450 € de volume tradé × 2% |
| House edge casino (30% des MAU) | 5,40 € | 30% × 18 € ARPU casino |
| Abonnement NovaPro (8% des MAU) | 1,60 € | 8% × 19,99 € |
| NovaPro+ (1,5% des MAU) | 0,75 € | 1,5% × 49,99 € |
| Marchés sponsorisés (rev/MAU) | 0,75 € | 450 k€/an ÷ 50k MAU ÷ 12 |
| **ARPU total** | **17,50 €/mois** | **210 €/an** |

### 6.2 Coûts variables par utilisateur

| Poste | Montant /mois | Justification |
|-------|---------------|---------------|
| Stripe fees (1,4% + 0,25€) | 1,20 € | Sur dépôts moyens 60 €/mois |
| Hébergement/BDD (compute) | 0,30 € | AWS RDS + EC2 + CDN |
| KYC (Stripe Identity, 1x/an) | 0,12 € | 1,50 €/vérif amortie |
| Support client (Y2) | 0,40 € | 1 agent / 5000 MAU |
| **Coûts variables totaux** | **2,02 €/mois** | |

**Marge brute par utilisateur : 17,50 - 2,02 = 15,48 € / mois (88%)**

### 6.3 LTV / CAC

- **LTV** = 15,48 € × 18 mois moyenne de rétention = **278 €**
- **CAC blended** = 24 €
- **Ratio LTV/CAC = 11,6** (excellent, benchmark SaaS = 3, iGaming = 6)
- **Payback period CAC** = 1,5 mois

---

## 7. Budget Prévisionnel — 3 ans

### 7.1 Compte de résultat prévisionnel

| Poste | Année 1 | Année 2 | Année 3 |
|-------|---------|---------|---------|
| **MAU moyen** | 8 000 | 28 000 | 50 000 |
| **Chiffre d'affaires** | **920 k€** | **4 200 k€** | **8 100 k€** |
| Spread trades | 480 k€ | 2 200 k€ | 4 050 k€ |
| House edge casino | 290 k€ | 1 350 k€ | 2 430 k€ |
| Abonnements NovaPro | 90 k€ | 420 k€ | 810 k€ |
| Marchés sponsorisés | 60 k€ | 230 k€ | 810 k€ |
| **Coûts variables** | -180 k€ | -680 k€ | -1 220 k€ |
| **Marge brute** | **740 k€ (80%)** | **3 520 k€ (84%)** | **6 880 k€ (85%)** |
| | | | |
| **OPEX** | **-1 850 k€** | **-2 800 k€** | **-3 900 k€** |
| Masse salariale (cf 7.3) | -780 k€ | -1 480 k€ | -2 200 k€ |
| Marketing & acquisition | -640 k€ | -900 k€ | -1 100 k€ |
| Hébergement & SaaS | -90 k€ | -180 k€ | -290 k€ |
| Conformité & legal | -120 k€ | -90 k€ | -130 k€ |
| Frais généraux (bureaux, juridique, compta) | -220 k€ | -150 k€ | -180 k€ |
| | | | |
| **EBITDA** | **-1 110 k€** | **+720 k€** | **+2 980 k€** |
| **Marge EBITDA** | -121% | +17% | +37% |
| | | | |
| **Trésorerie cumulée** (post seed) | -610 k€ | +110 k€ | +3 090 k€ |

### 7.2 Sprint 1 → Sprint 3 (cadrage école)

| Sprint | Durée | Budget | Livrables |
|--------|-------|--------|-----------|
| Sprint 1 ✅ | 4 sem | 11 100 € (consommé) | Onboarding + Design System + Auth |
| Sprint 2 | 4 sem | 15 200 € | Merge, portfolio, résolution marchés, transactions |
| Sprint 3 | 4 sem | 18 700 € | OTO funnel, mini-casino, polissage |
| **Total cycle école** | **12 sem** | **45 000 €** | **MVP démonstrable** |

Reste sur enveloppe pédagogique 100 k€ : **55 000 € de "marge sécurité"** affectée à : tests utilisateurs (3k€), audit légal préliminaire (12k€), pitch deck + comm soutenance (5k€), réserve (35k€).

### 7.3 Masse salariale détaillée (post-projet école → entreprise)

**Année 1 (équipe core 7 personnes) :**

| Poste | Salaire chargé/an | Headcount |
|-------|-------------------|-----------|
| CEO / Product (Hugo) | 70 k€ | 1 |
| CTO / Lead Dev | 90 k€ | 1 |
| Dev Backend Senior | 75 k€ | 1 |
| Dev Frontend Senior | 70 k€ | 1 |
| Growth Marketer | 65 k€ | 1 |
| Community Manager | 45 k€ | 1 |
| Compliance / Legal (mi-temps externalisé) | 50 k€ | 0,5 |
| **TOTAL Y1** | **465 k€** | 6,5 |

→ avec charges patronales (45%) et amortissement embauches progressives = **780 k€ réels Y1**

**Année 2 — ajout :**
- 2 Dev (Mobile + Data) : +160 k€
- 1 Designer UX : +55 k€
- 2 Support client : +70 k€
- 1 Head of Marketing : +90 k€
- 1 Data Analyst : +60 k€
- 1 CFO part-time : +80 k€
→ +515 k€ → **1 480 k€ Y2**

**Année 3 — ajout :**
- 4 Dev supplémentaires : +320 k€
- 2 Support : +70 k€
- 1 Head of Growth : +90 k€
- 1 BDR B2B sponsorships : +60 k€
- 1 Office Manager : +45 k€
→ +585 k€ → **2 200 k€ Y3**

### 7.4 Plan de financement

| Round | Date | Montant | Valorisation pré-money | Use of funds |
|-------|------|---------|------------------------|--------------|
| **Pre-seed (BSA-AIR)** | M0 | 250 k€ | 1,5 M€ | MVP, premiers 6 mois |
| **Seed** | M6 | 1,5 M€ | 8 M€ | Acquisition Phase 1+2, équipe à 7 |
| **Série A** | M22 | 6 M€ | 30 M€ | Expansion EU, équipe à 22, mobile |

**Sources cibles :**
- Pre-seed : business angels finance/crypto (Kima, Founders Future), aides BPI French Tech Émergence (40k€), prêt d'honneur Réseau Entreprendre (50k€)
- Seed : Daphni, Frst, Connect Ventures, eGaming-focus funds (Bettor Capital US)
- Série A : Index Ventures, Felix Capital, IVP

---

## 8. KPIs & Pilotage

| Catégorie | KPI | Fréquence | Cible Y2 |
|-----------|-----|-----------|----------|
| **Acquisition** | Nouvelles inscriptions | Quotidien | 100/jour |
| | CAC blended | Hebdo | < 30 € |
| | Taux conv visiteur → inscrit | Hebdo | > 8% |
| **Activation** | First deposit rate (J7) | Hebdo | > 22% |
| | Time-to-first-bet | Hebdo | < 8 min |
| **Engagement** | MAU / WAU / DAU | Quotidien | 30k / 14k / 6k |
| | Sessions/MAU/mois | Hebdo | > 12 |
| | Volume tradé / MAU | Hebdo | > 400 € |
| **Funnel OTO** | Taux d'acceptation OTO | Quotidien | > 28% |
| | Marge casino / OTO accepté | Hebdo | > 100 € |
| **Rétention** | M1 / M3 / M6 retention | Mensuel | 55% / 35% / 25% |
| | Churn NovaPro | Mensuel | < 4% |
| **Financier** | ARPU | Mensuel | > 15 € |
| | LTV/CAC | Mensuel | > 8 |
| | EBITDA margin | Mensuel | > 0% à partir M18 |
| **Compliance** | Taux KYC pass | Hebdo | > 92% |
| | Tickets self-exclusion | Mensuel | < 0,5% MAU |

---

## 9. Analyse des Risques

| Risque | Probabilité | Impact | Plan de mitigation |
|--------|-------------|--------|--------------------|
| 🔴 **Régulateur EU classifie le sweepstakes model comme jeu d'argent** | Moyenne | Critique | Cabinet de droit dédié dès M0 ; veille réglementaire continue ; structure juridique mobile (Malte / Curaçao en backup) |
| 🟠 **Concurrence : Polymarket lance EU + funnel casino** | Moyenne | Élevé | Avance technologique : breveter le flow OTO ; locking communautaire via NovaPro |
| 🟠 **Stripe / banques refusent le compte pro (sector gambling)** | Élevée | Élevé | Backup : Worldline, PayU, Trustpay (PSP gaming-friendly) |
| 🟡 **CAC explose à cause de la concurrence sur Meta/Google Ads** | Moyenne | Moyen | Pivot vers SEO + affiliation + organique. Cap à 35€ CAC sinon réduction acquisition |
| 🟡 **Bug critique sur le moteur de paris → perte de fonds clients** | Faible | Critique | Tests automatisés à 95% coverage, audit code externe annuel, cyber-assurance 5 M€ |
| 🟡 **Joueur problématique gagne en justice (responsabilité plateforme)** | Faible | Élevé | Détection automatique (volume, fréquence), self-exclusion accessible 1 clic, partenariat SOS Joueurs |
| 🟢 **Équipe : départ d'un cofondateur** | Faible | Moyen | Vesting 4 ans avec cliff 1 an, BSPCE pour les 5 premières clés |

---

## 10. Roadmap Stratégique

```
M0  ──── M6  ──── M12 ──── M18 ──── M24 ──── M30 ──── M36
│        │        │         │        │         │         │
MVP     Launch   Seed      EBITDA   Mobile    Série A   Expansion
        public   round     positif  app       round     DE/ES
                                    iOS+And
        ↓        ↓         ↓        ↓         ↓         ↓
        5k MAU   8k MAU   15k MAU  25k MAU   35k MAU   50k MAU
        920k€    €1.8M    €2.6M    €4.2M     €6M       €8.1M
        ARR      ARR      ARR      ARR       ARR       ARR
```

---

## 11. Synthèse Soutenance — Les 3 messages à retenir

1. **NovaBet n'est pas un Polymarket-bis** — c'est le premier hybride prediction market + social casino, avec un funnel OTO qui multiplie l'ARPU par 1,9x vs un Polymarket pur.

2. **Le cadre légal est sécurisé** — modèle sweepstakes éprouvé aux US (Stake.us, Chumba), aligné EU avec les nouvelles directives 2024, sans dépendre d'une licence ANJ inaccessible aux startups.

3. **L'économie unitaire est validée** — LTV/CAC de 11,6 dès Y2, EBITDA positif à M18, ARR 8 M€ à 3 ans avec une trajectoire de financement réaliste (250k → 1,5M → 6M).

---

## 12. Annexes

### A. Hypothèses de calcul détaillées
*(Volume tradé moyen, formules house edge, courbe de rétention, etc. — fournis sur demande dans le pitch deck financier)*

### B. Sources & benchmarks
- H2 Gambling Capital — Global Gambling Report 2025
- Statista — Social Casino Market Outlook 2025-2030
- eGaming Review — Conversion Benchmarks Q4 2024
- Polymarket transparency reports 2024
- VGW Holdings Annual Report 2024 (Chumba Casino financials)
- ANJ — Rapport d'activité 2024
- CFTC — Kalshi licensure docket

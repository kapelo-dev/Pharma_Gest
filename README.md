# Gestion de Pharmacie - Documentation

## Table des matières

1. [Présentation du projet](#présentation-du-projet)
2. [Architecture technique](#architecture-technique)
3. [Fonctionnalités](#fonctionnalités)
4. [Guide d'utilisation](#guide-dutilisation)
5. [Installation et configuration](#installation-et-configuration)

## Présentation du projet

L'application de Gestion de Pharmacie est un logiciel développé en Java qui permet de gérer efficacement l'inventaire et le stock d'une pharmacie. Cette solution offre une interface graphique moderne et intuitive pour gérer les produits pharmaceutiques, leur stock, et suivre les dates d'expiration.

### Objectifs principaux

- Gérer l'inventaire des produits pharmaceutiques
- Suivre les stocks et les dates d'expiration
- Alerter sur les produits proche de la date d'expiration
- Faciliter la gestion quotidienne de la pharmacie

## Architecture technique

### Technologies utilisées

- Java (JDK 17+)
- Swing pour l'interface graphique
- Architecture MVC (Modèle-Vue-Contrôleur)
- Base de données relationnelle

### Structure du projet

```
src/
├── main/
│   └── java/
│       └── com/
│           └── pharmajava/
│               ├── controller/
│               │   ├── ProduitController.java
│               │   └── StockController.java
│               ├── model/
│               │   ├── Produit.java
│               │   └── Stock.java
│               ├── view/
│               │   └── StockView.java
│               └── utils/
│                   ├── DateUtil.java
│                   └── DialogUtils.java
```

## Fonctionnalités

### 1. Gestion des stocks

#### 1.1 Vue principale des stocks

- Affichage tabulaire des stocks
- Colonnes : ID, Produit, Lot, Quantité, Date d'expiration, Jours restants
- Coloration des lignes selon la date d'expiration :
  - Rouge : Produits expirés
  - Orange : Produits expirant bientôt (< 30 jours)
  - Blanc : Produits avec date d'expiration normale

#### 1.2 Filtrage et recherche

- Filtre par état : Tous, Expiré, Expire bientôt, En stock
- Recherche dynamique de produits avec autocomplétion
- Filtrage instantané des résultats

#### 1.3 Gestion des stocks

- Ajout de nouveaux stocks
- Modification des stocks existants
- Suppression de stocks
- Validation des données saisies

### 2. Gestion des produits

- Recherche avancée de produits avec autocomplétion
- Sélection rapide des produits
- Association automatique avec les stocks

## Guide d'utilisation

### 1. Interface principale

#### 1.1 Barre supérieure

- **Titre** : "Gestion des Stocks" (gauche)
- **Filtres** : Sélection du type de vue (droite)
- **Recherche** : Champ de recherche global (droite)

#### 1.2 Tableau central

- Affiche tous les stocks avec leurs informations
- Colonnes triables
- Sélection simple de ligne pour modification/suppression

#### 1.3 Formulaire inférieur

- Zone de saisie/modification des stocks
- Champs obligatoires :
  - Produit (avec recherche autocomplétion)
  - Numéro de lot
  - Quantité
  - Date d'expiration

### 2. Opérations courantes

#### 2.1 Ajouter un stock

1. Cliquer sur le champ "Rechercher un produit..."
2. Commencer à taper le nom du produit
3. Sélectionner le produit dans la liste d'autocomplétion
4. Remplir le numéro de lot
5. Définir la quantité
6. Sélectionner la date d'expiration
7. Cliquer sur "Ajouter"

#### 2.2 Modifier un stock

1. Sélectionner le stock dans le tableau
2. Modifier les champs nécessaires dans le formulaire
3. Cliquer sur "Ajouter" pour sauvegarder les modifications

#### 2.3 Supprimer un stock

1. Sélectionner le stock dans le tableau
2. Cliquer sur "Supprimer"
3. Confirmer la suppression

#### 2.4 Rechercher des stocks

1. Utiliser le champ de recherche global
2. Ou utiliser les filtres prédéfinis
3. Les résultats se mettent à jour automatiquement

### 3. Fonctionnalités avancées

#### 3.1 Autocomplétion des produits

- La recherche commence dès la saisie
- Navigation possible avec les flèches du clavier
- Sélection avec la souris ou Entrée
- Le popup reste visible pendant la sélection

#### 3.2 Alertes visuelles

- Rouge : Produits expirés
- Orange : Produits expirant dans moins de 30 jours
- Blanc : Produits avec date d'expiration normale

## Installation et configuration

### Prérequis

- Java JDK 17 ou supérieur
- Base de données compatible
- Espace disque : 100 Mo minimum

### Installation

1. Télécharger le fichier JAR de l'application
2. Configurer la base de données
3. Lancer l'application avec :

```bash
java -jar pharmacie-gestion.jar
```

### Configuration

1. Vérifier les paramètres de connexion à la base de données
2. Ajuster les paramètres d'alerte si nécessaire
3. Configurer les sauvegardes automatiques

### Maintenance

- Sauvegarder régulièrement la base de données
- Mettre à jour l'application quand disponible
- Vérifier les logs pour d'éventuelles erreurs

## Support et contact

Pour toute question ou support technique :

- Email : support@pharmacie-gestion.com
- Documentation en ligne : https://pharmacie-gestion.com/docs
- Support technique : https://pharmacie-gestion.com/support
#   P h a r m a _ G e s t  
 
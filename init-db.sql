-- ============================================
-- SCRIPT D'INITIALISATION POSTGRESQL
-- ============================================
-- Ce script est exécuté automatiquement au premier démarrage
-- du conteneur PostgreSQL (grâce au volume monté dans docker-compose.yml)

-- Création des 4 bases de données pour les microservices
CREATE DATABASE db_patients;
CREATE DATABASE db_medecins;
CREATE DATABASE db_rendezvous;
CREATE DATABASE db_users;

-- Message de confirmation (visible dans les logs Docker)
\echo 'Bases de données créées avec succès : db_patients, db_medecins, db_rendezvous, db_users'
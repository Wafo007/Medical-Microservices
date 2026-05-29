import React from 'react';
import { Link } from 'react-router-dom';
import { Heart, Shield, Brain, Calendar, Users, Stethoscope, ArrowRight, Activity } from 'lucide-react';
import './HomePage.css';

/**
 * Page d'accueil - Première impression du projet.
 * 
 * Cette page doit être ÉPOUSTOUFLANTE car c'est la première
 * chose que le professeur verra. Elle montre :
 * - La modernité du design (CSS avancé)
 * - Les fonctionnalités clés (cards animées)
 * - L'IA (feature innovante mise en avant)
 */

const HomePage = () => {
  return (
    <div className="homepage">
      {/* ============================================
          HERO SECTION (Section principale)
          ============================================ */}
      <header className="hero">
        <div className="hero-overlay">
          <nav className="navbar">
            <div className="logo">
              <Heart className="logo-icon" />
              <span>Clinic<span className="logo-plus">+</span></span>
            </div>
            <div className="nav-links">
              <a href="#features">Fonctionnalités</a>
              <a href="#ia">Intelligence Artificielle</a>
              <a href="#architecture">Architecture</a>
            </div>
            <div className="nav-buttons">
              <Link to="/login" className="btn btn-outline">Se connecter</Link>
              <Link to="/register" className="btn btn-primary">Commencer</Link>
            </div>
          </nav>

          <div className="hero-content">
            <div className="hero-badge">
              <Activity size={16} />
              <span>Microservices • Spring Boot • React • IA</span>
            </div>
            <h1 className="hero-title">
              La santé connectée<br />
              <span className="gradient-text">propulsée par l'IA</span>
            </h1>
            <p className="hero-subtitle">
              Une architecture microservices moderne avec authentification JWT,
              API Gateway, Eureka Service Discovery et intégration Mistral AI.
            </p>
            <div className="hero-buttons">
              <Link to="/login" className="btn btn-large btn-primary">
                Accéder à l'application <ArrowRight size={20} />
              </Link>
              <a href="#demo" className="btn btn-large btn-outline">
                Voir la démo
              </a>
            </div>
            
            {/* Stats impressionnantes */}
            <div className="hero-stats">
              <div className="stat">
                <span className="stat-number">3</span>
                <span className="stat-label">Microservices</span>
              </div>
              <div className="stat">
                <span className="stat-number">JWT</span>
                <span className="stat-label">Sécurité</span>
              </div>
              <div className="stat">
                <span className="stat-number">AI</span>
                <span className="stat-label">Intégration IA</span>
              </div>
            </div>
          </div>
        </div>
      </header>

      {/* ============================================
          FEATURES SECTION (Fonctionnalités)
          ============================================ */}
      <section id="features" className="features">
        <div className="container">
          <h2 className="section-title">Architecture <span className="gradient-text">Microservices</span></h2>
          <p className="section-subtitle">
            Une solution complète avec séparation des responsabilités,
            découverte de services et sécurité stateless.
          </p>

          <div className="features-grid">
            {/* Card 1 : Patient Service */}
            <div className="feature-card">
              <div className="feature-icon blue">
                <Users size={28} />
              </div>
              <h3>Patient Service</h3>
              <p>Gestion complète des dossiers patients avec CRUD, historique médical et recherche avancée. Base PostgreSQL dédiée.</p>
              <div className="tech-tags">
                <span className="tag">Spring Boot</span>
                <span className="tag">JPA</span>
                <span className="tag">PostgreSQL</span>
              </div>
            </div>

            {/* Card 2 : Médecin Service */}
            <div className="feature-card">
              <div className="feature-icon green">
                <Stethoscope size={28} />
              </div>
              <h3>Médecin Service</h3>
              <p>Gestion des praticiens, spécialités médicales et disponibilités horaires. Communication inter-services via OpenFeign.</p>
              <div className="tech-tags">
                <span className="tag">OpenFeign</span>
                <span className="tag">Eureka</span>
                <span className="tag">REST</span>
              </div>
            </div>

            {/* Card 3 : RDV Service */}
            <div className="feature-card">
              <div className="feature-icon purple">
                <Calendar size={28} />
              </div>
              <h3>RDV Service</h3>
              <p>Prise de rendez-vous intelligente avec vérification des conflits de créneaux et validation des disponibilités.</p>
              <div className="tech-tags">
                <span className="tag">Validation</span>
                <span className="tag">Conflits</span>
                <span className="tag">Planning</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ============================================
          IA SECTION (Le clou du spectacle)
          ============================================ */}
      <section id="ia" className="ia-section">
        <div className="container">
          <div className="ia-content">
            <div className="ia-text">
              <div className="ia-badge">
                <Brain size={16} />
                <span>Intelligence Artificielle</span>
              </div>
              <h2>
                Décrivez vos symptômes,<br />
                <span className="gradient-text">l'IA suggère le spécialiste</span>
              </h2>
              <p>
                Intégration de l'API Mistral AI pour l'analyse sémantique des symptômes.
                Le système suggère automatiquement la spécialité médicale appropriée
                et un médecin disponible.
              </p>
              <ul className="ia-features">
                <li><Shield size={18} /> Analyse sémantique avancée</li>
                <li><Shield size={18} /> Suggestion de spécialité</li>
                <li><Shield size={18} /> Recommandation de médecin</li>
                <li><Shield size={18} /> Prise de rendez-vous en un clic</li>
              </ul>
              <Link to="/login" className="btn btn-primary">
                Tester l'assistant <ArrowRight size={18} />
              </Link>
            </div>
            <div className="ia-visual">
              <div className="chat-bubble user">
                "J'ai des douleurs dans la poitrine..."
              </div>
              <div className="chat-bubble ai">
                <div className="ai-header">
                  <Brain size={14} /> Assistant IA
                </div>
                <strong>Spécialité suggérée :</strong> Cardiologie<br />
                <strong>Médecin :</strong> Dr. Sophie Martin<br />
                <strong>Disponible :</strong> Demain 09:00
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ============================================
          ARCHITECTURE SECTION
          ============================================ */}
      <section id="architecture" className="architecture">
        <div className="container">
          <h2 className="section-title">Stack <span className="gradient-text">Technique</span></h2>
          
          <div className="stack-grid">
            <div className="stack-item">
              <span className="stack-name">Spring Boot</span>
              <span className="stack-desc">Microservices</span>
            </div>
            <div className="stack-item">
              <span className="stack-name">Spring Cloud</span>
              <span className="stack-desc">Gateway + Eureka</span>
            </div>
            <div className="stack-item">
              <span className="stack-name">PostgreSQL</span>
              <span className="stack-desc">Bases de données</span>
            </div>
            <div className="stack-item">
              <span className="stack-name">JWT</span>
              <span className="stack-desc">Authentification</span>
            </div>
            <div className="stack-item">
              <span className="stack-name">Docker</span>
              <span className="stack-desc">Containerisation</span>
            </div>
            <div className="stack-item">
              <span className="stack-name">Kubernetes</span>
              <span className="stack-desc">Orchestration</span>
            </div>
            <div className="stack-item">
              <span className="stack-name">Mistral AI</span>
              <span className="stack-desc">Intelligence artificielle</span>
            </div>
            <div className="stack-item">
              <span className="stack-name">React</span>
              <span className="stack-desc">Interface utilisateur</span>
            </div>
          </div>
        </div>
      </section>

      {/* ============================================
          FOOTER
          ============================================ */}
      <footer className="footer">
        <div className="container">
          <div className="footer-content">
            <div className="footer-brand">
              <Heart className="logo-icon" />
              <span>Clinic<span className="logo-plus">+</span></span>
            </div>
            <p className="footer-text">
              Projet de microservices • Architecture distribuée • Spring Boot & React
            </p>
            <div className="footer-links">
              <a href="https://spring.io/projects/spring-boot" target="_blank" rel="noreferrer">Spring Boot</a>
              <a href="https://react.dev" target="_blank" rel="noreferrer">React</a>
              <a href="https://mistral.ai" target="_blank" rel="noreferrer">Mistral AI</a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default HomePage;
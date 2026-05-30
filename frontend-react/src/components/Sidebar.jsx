import React from 'react';
import { Heart, LogOut } from 'lucide-react';
import { useNavigate, useLocation } from 'react-router-dom';

/**
 * Sidebar de navigation latérale.
 * 
 * Réutilisable pour toutes les pages protégées.
 * Affiche le logo, le menu de navigation, et les infos utilisateur.
 */

const Sidebar = ({ menuItems, userName, userRole, onLogout }) => {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <aside className="sidebar">
      {/* Logo */}
      <div className="sidebar-logo" onClick={() => navigate('/')}>
        <Heart className="logo-icon" />
        <span>Clinic<span className="logo-plus">+</span></span>
      </div>

      {/* Navigation */}
      <nav className="sidebar-nav">
        {menuItems.map((item, index) => {
          const Icon = item.icon;
          const isActive = location.pathname === item.path;
          
          return (
            <button
              key={index}
              className={`nav-item ${isActive ? 'active' : ''}`}
              onClick={() => navigate(item.path)}
            >
              <Icon size={20} />
              <span>{item.label}</span>
              {isActive && <div className="active-indicator" />}
            </button>
          );
        })}
      </nav>

      {/* Profil utilisateur */}
      <div className="sidebar-footer">
        <div className="user-info">
          <div className="user-avatar">
            {userName.charAt(0).toUpperCase()}
          </div>
          <div className="user-details">
            <span className="user-name">{userName}</span>
            <span className="user-role">{userRole}</span>
          </div>
        </div>
        <button className="logout-btn" onClick={onLogout}>
          <LogOut size={18} />
          <span>Déconnexion</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
import { useState, useEffect } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { getMyProfile, logout } from '../api'
import './Profile.css'

export default function Profile() {
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!localStorage.getItem('token')) return
    getMyProfile()
      .then(setProfile)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  const token = localStorage.getItem('token')
  if (!token) {
    return <Navigate to="/login" replace />
  }

  if (loading) {
    return (
      <div className="profile-page">
        <p>Chargement...</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="profile-page">
        <p className="error">{error}</p>
        <Link to="/movies">← Films</Link>
      </div>
    )
  }

  const created =
    profile?.createdAt != null
      ? new Date(profile.createdAt).toLocaleString()
      : '—'

  return (
    <div className="profile-page">
      <nav className="profile-nav">
        <Link to="/movies">← Films</Link>
        <button
          type="button"
          onClick={() => {
            logout()
            window.location.href = '/movies'
          }}
        >
          Déconnexion
        </button>
      </nav>

      <h1>Mon profil</h1>
      <dl className="profile-dl">
        <dt>Nom d&apos;utilisateur</dt>
        <dd>{profile?.username}</dd>
        <dt>Email</dt>
        <dd>{profile?.email}</dd>
        <dt>Inscrit le</dt>
        <dd>{created}</dd>
      </dl>
    </div>
  )
}

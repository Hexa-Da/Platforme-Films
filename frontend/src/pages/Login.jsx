import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { login, getGoogleOAuthUrl } from '../api'
import './Auth.css'

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    try {
      const token = await login(username, password)
      localStorage.setItem('token', token)
      navigate('/movies')
    } catch (err) {
      setError(err.message || 'Login failed')
    }
  }

  return (
    <div className="auth-page">
      <h1>Connexion</h1>
      <form onSubmit={handleSubmit}>
        {error && <p className="error">{error}</p>}
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Se connecter</button>
        <button
          type="button"
          onClick={() => {
            window.location.href = getGoogleOAuthUrl()
          }}
        >
          Se connecter avec Google
        </button>
      </form>
      <p>
        Pas de compte ? <Link to="/register">S'inscrire</Link>
      </p>
      <Link to="/movies">Voir les films sans se connecter</Link>
    </div>
  )
}

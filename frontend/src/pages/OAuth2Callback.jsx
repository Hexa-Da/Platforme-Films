import { useEffect } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'

export default function OAuth2Callback() {
  const location = useLocation()
  const navigate = useNavigate()

  useEffect(() => {
    const params = new URLSearchParams(location.search)
    const token = params.get('token')

    if (token) {
      localStorage.setItem('token', token)
      navigate('/movies', { replace: true })
    } else {
      navigate('/login?error=oauth2', { replace: true })
    }
  }, [location.search, navigate])

  return (
    <div className="auth-page">
      <h1>Connexion en cours...</h1>
      <p>Redirection après authentification avec Google.</p>
    </div>
  )
}


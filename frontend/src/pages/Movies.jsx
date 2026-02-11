import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { getMovies, logout } from '../api'
import './Movies.css'

export default function Movies() {
  const [movies, setMovies] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [token] = useState(() => localStorage.getItem('token'))

  useEffect(() => {
    getMovies()
      .then(setMovies)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p>Chargement...</p>
  if (error) return <p className="error">Erreur: {error}</p>

  return (
    <div className="movies-page">
      <nav>
        <Link to="/movies">Films</Link>
        {token ? (
          <button onClick={() => { logout(); window.location.reload(); }}>
            Déconnexion
          </button>
        ) : (
          <>
            <Link to="/login">Connexion</Link>
            <Link to="/register">Inscription</Link>
          </>
        )}
      </nav>
      <h1>Liste des films</h1>
      <div className="movies-grid">
        {movies.map((movie) => (
          <Link key={movie.id} to={`/movies/${movie.id}`} className="movie-card">
            <h3>{movie.title}</h3>
            <p>{movie.director} ({movie.releaseYear})</p>
            <p className="genre">{movie.genre}</p>
          </Link>
        ))}
      </div>
    </div>
  )
}

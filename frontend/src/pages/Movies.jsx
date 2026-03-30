import { useState, useEffect, useRef } from 'react'
import { Link } from 'react-router-dom'
import { getMovies, logout } from '../api'
import './Movies.css'
import ReviewPopup from '../popups/ReviewPopup'
import SearchBar from '../components/SearchBar/SearchBar'

const DEBOUNCE_MS = 350

export default function Movies() {
  const [movies, setMovies] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [token] = useState(() => localStorage.getItem('token'))
  const [selectedMovie, setSelectedMovie] = useState(null)

  const [recherche, setRecherche] = useState('')
  const [genreFilter, setGenreFilter] = useState('')
  const isFirstFetch = useRef(true)

  useEffect(() => {
    const delay = isFirstFetch.current ? 0 : DEBOUNCE_MS
    isFirstFetch.current = false

    const t = setTimeout(() => {
      setLoading(true)
      setError('')
      const title = recherche.trim()
      const genre = genreFilter.trim()
      const params = {}
      if (title) params.title = title
      else if (genre) params.genre = genre

      getMovies(params)
        .then(setMovies)
        .catch((err) => setError(err.message))
        .finally(() => setLoading(false))
    }, delay)

    return () => clearTimeout(t)
  }, [recherche, genreFilter])

  const handleReviewSubmit = async (data) => {
    try {
      const tok = localStorage.getItem('token')
      if (!selectedMovie) {
        throw new Error('Aucun film sélectionné')
      }

      const ratingRes = await fetch(`http://localhost:8080/api/v1/ratings`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${tok}` },
        body: JSON.stringify({ movieId: selectedMovie.id, score: data.score }),
      })
      if (!ratingRes.ok) {
        const errBody = await ratingRes.json().catch(() => null)
        throw new Error(errBody?.message || "Erreur lors de l'envoi de la note")
      }

      const reviewRes = await fetch(`http://localhost:8080/api/v1/reviews`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${tok}` },
        body: JSON.stringify({ movieId: selectedMovie.id, content: data.content }),
      })
      if (!reviewRes.ok) {
        const errBody = await reviewRes.json().catch(() => null)
        throw new Error(errBody?.message || "Erreur lors de l'envoi de la critique")
      }

      alert('Avis enregistré !')
      setSelectedMovie(null)
    } catch (err) {
      alert(err?.message || 'Erreur lors de l\'envoi')
    }
  }

  if (loading && movies.length === 0 && !error) {
    return <p className="movies-page">Chargement...</p>
  }
  if (error && movies.length === 0) {
    return <p className="error movies-page">Erreur: {error}</p>
  }

  return (
    <div className="movies-page">
      <nav>
        <Link to="/movies">Films</Link>
        {token && (
          <>
            <Link to="/profile">Mon profil</Link>
            <Link to="/movies/new">Ajouter un film</Link>
          </>
        )}
        {token ? (
          <button
            type="button"
            className="btn-danger"
            onClick={() => { logout(); window.location.reload() }}
          >
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

      <div className="movies-filters">
        <SearchBar onSearch={setRecherche} />
        <SearchBar
          onSearch={setGenreFilter}
          placeholder="Rechercher un genre..."
          ariaLabel="Filtrer par genre"
        />
      </div>

      {loading && <p className="loading-hint">Actualisation…</p>}

      {movies.length === 0 && !loading && (
        <p style={{ textAlign: 'center' }}>Aucun film ne correspond à votre recherche.</p>
      )}

      <div className="movies-grid">
        {movies.map((movie) => (
          <Link key={movie.id} to={`/movies/${movie.id}`} className="movie-card">
            <h3>{movie.title}</h3>
            <p>
              {movie.director} ({movie.releaseYear})
            </p>
            <p className="genre">{movie.genre}</p>

            {token && (
              <button
                type="button"
                className="rate-button"
                onClick={(e) => {
                  e.preventDefault()
                  e.stopPropagation()
                  setSelectedMovie(movie)
                }}
              >
                Noter ce film
              </button>
            )}
          </Link>
        ))}
      </div>

      {selectedMovie && (
        <ReviewPopup
          movieTitle={selectedMovie.title}
          onClose={() => setSelectedMovie(null)}
          onSubmit={handleReviewSubmit}
        />
      )}
    </div>
  )
}

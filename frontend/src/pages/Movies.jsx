import { useState, useEffect, useRef } from 'react'
import { Link } from 'react-router-dom'
import { getMovies, logout, API_BASE, getMyProfile } from '../api'
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
    const delay = isFirstFetch.current ? 0 : DEBOUNCE_MS;
    isFirstFetch.current = false;

    const t = setTimeout(() => {
      setLoading(true);
      setError('');

      getMovies({
        title: recherche.trim() || undefined,
        genre: genreFilter.trim() || undefined
      })
        .then(async (moviesList) => {
          let username = null
          if (token) {
            try {
              const profile = await getMyProfile()
              username = profile?.username || null
            } catch {
            }
          }

          const enrichedMovies = await Promise.all(
            moviesList.map(async (movie) => {
              try {
                const [avgRes, ratingsRes, reviewsRes] = await Promise.all([
                  fetch(`${API_BASE}/movies/${movie.id}/ratings/average`),
                  username ? fetch(`${API_BASE}/movies/${movie.id}/ratings`) : Promise.resolve(null),
                  username ? fetch(`${API_BASE}/movies/${movie.id}/reviews`) : Promise.resolve(null),
                ])

                const avg = avgRes.ok ? await avgRes.json() : 0
                let hasUserFeedback = false
                let userRating = null
                let userReviewContent = ''
                if (username && ratingsRes?.ok && reviewsRes?.ok) {
                  const [ratings, reviews] = await Promise.all([
                    ratingsRes.json(),
                    reviewsRes.json(),
                  ])
                  const userRatingEntry = Array.isArray(ratings) ? ratings.find((r) => r.username === username) : null
                  const userReviewEntry = Array.isArray(reviews) ? reviews.find((r) => r.username === username) : null
                  const hasUserRating = Boolean(userRatingEntry)
                  const hasUserReview = Boolean(userReviewEntry)
                  hasUserFeedback = hasUserRating || hasUserReview
                  userRating = userRatingEntry?.score ?? null
                  userReviewContent = userReviewEntry?.content ?? ''
                }

                return { ...movie, averageRating: avg, hasUserFeedback, userRating, userReviewContent };
              } catch {
                return { ...movie, averageRating: 0, hasUserFeedback: false, userRating: null, userReviewContent: '' };
              }
            })
          );
          setMovies(enrichedMovies);
        })
        .catch((err) => setError(err.message))
        .finally(() => setLoading(false));
    }, delay);

    return () => clearTimeout(t);
  }, [recherche, genreFilter, token]);


  const MovieStars = ({ rating }) => {
    const roundedRating = Math.round(rating || 0);
    return (
      <div className="movie-card-stars" title={`Note : ${rating?.toFixed(1) || 0}/5`}>
        {[...Array(5)].map((_, i) => (
          <span key={i} className={`star ${i < roundedRating ? "on" : "off"}`}>
            &#9733;
          </span>
        ))}
        {rating > 0 && <span className="rating-number">({rating.toFixed(1)})</span>}
      </div>
    );
  };

  const sessionError = 'Session expirée ou non autorisée. Reconnectez-vous.'

  const parseError = async (res, fallbackMessage) => {
    const errBody = await res.json().catch(() => null)
    if (res.status === 401 || res.status === 403) {
      localStorage.removeItem('token')
      return sessionError
    }
    return errBody?.message || fallbackMessage
  }

  const authFetch = async (url, options = {}) => {
    const tok = localStorage.getItem('token')
    if (!tok) {
      throw new Error(sessionError)
    }
    return fetch(url, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${tok}`,
        ...(options.headers || {}),
      },
    })
  }

  const handleReviewSubmit = async (data) => {
    try {
      if (!selectedMovie) {
        throw new Error('Aucun film sélectionné')
      }

      try {
        await getMyProfile()
      } catch {
        localStorage.removeItem('token')
        throw new Error('Session expirée. Veuillez vous reconnecter.')
      }

      const ratingRes = await authFetch(`${API_BASE}/movies/${selectedMovie.id}/ratings`, {
        method: 'POST',
        body: JSON.stringify({ score: data.score }),
      })
      if (ratingRes.status === 409) {
        const updateRatingRes = await authFetch(`${API_BASE}/movies/${selectedMovie.id}/ratings/mine`, {
          method: 'PUT',
          body: JSON.stringify({ score: data.score }),
        })
        if (!updateRatingRes.ok) {
          throw new Error(await parseError(updateRatingRes, "Erreur lors de la mise à jour de la note"))
        }
      } else if (!ratingRes.ok) {
        throw new Error(await parseError(ratingRes, "Erreur lors de l'envoi de la note"))
      }

      const reviewRes = await authFetch(`${API_BASE}/movies/${selectedMovie.id}/reviews`, {
        method: 'POST',
        body: JSON.stringify({ content: data.content }),
      })
      if (reviewRes.status === 409) {
        const updateRes = await authFetch(`${API_BASE}/movies/${selectedMovie.id}/reviews/mine`, {
          method: 'PUT',
          body: JSON.stringify({ content: data.content }),
        })
        if (!updateRes.ok) {
          throw new Error(await parseError(updateRes, "Erreur lors de la mise à jour de la critique"))
        }
      } else if (!reviewRes.ok) {
        throw new Error(await parseError(reviewRes, "Erreur lors de l'envoi de la critique"))
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
            onClick={() => {
              logout();
              window.location.reload();
            }}
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
            {/* Header de la carte : Titre + Étoiles */}
            <div className="movie-card-header">
              <h3>{movie.title}</h3>
              <div className="movie-card-stars">
                {[...Array(5)].map((_, i) => (
                  <span
                    key={i}
                    className={`star ${(movie.averageRating || 0) >= i + 1 ? 'on' : 'off'}`}
                  >
                    &#9733;
                  </span>
                ))}
                {movie.averageRating > 0 && (
                  <span className="rating-number">
                    ({movie.averageRating.toFixed(1)})
                  </span>
                )}
              </div>
            </div>

            {/* Infos secondaires */}
            <p>
              {movie.director} ({movie.releaseYear})
            </p>
            <p className="genre">{movie.genre}</p>

            {/* Bouton Noter : visible seulement si connecté */}
            {token && (
              <button
                type="button"
                className="rate-button"
                onClick={(e) => {
                  e.preventDefault(); // Empêche la navigation vers /movies/:id
                  e.stopPropagation(); // Empêche le Link parent de s'activer
                  setSelectedMovie(movie);
                }}
              >
                {movie.hasUserFeedback ? 'Editer ma note' : 'Noter ce film'}
              </button>
            )}
          </Link>
        ))}
      </div>

      {/* Popup de notation */}
      {selectedMovie && (
        <ReviewPopup
          movieTitle={selectedMovie.title}
          initialRating={selectedMovie.userRating}
          initialComment={selectedMovie.userReviewContent}
          onClose={() => setSelectedMovie(null)}
          onSubmit={handleReviewSubmit}
        />
      )}
    </div>
  );
}

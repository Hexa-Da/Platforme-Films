import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { deleteMovie, getMovie, getReviewsByMovie, getAverageRating } from '../api'
import StarRating from '../components/StarRating'
import './MovieDetail.css'

function formatDate(dateStr) {
  if (!dateStr) return ''
  try {
    return new Date(dateStr).toLocaleDateString('fr-FR', {
      day: 'numeric', month: 'long', year: 'numeric'
    })
  } catch {
    return ''
  }
}

export default function MovieDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [movie, setMovie] = useState(null)
  const [reviews, setReviews] = useState([])
  const [averageRating, setAverageRating] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const token = localStorage.getItem('token')

  useEffect(() => {
    Promise.all([
      getMovie(id),
      getReviewsByMovie(id).catch(() => []),
      getAverageRating(id).catch(() => 0),
    ])
      .then(([movieData, reviewsData, avgData]) => {
        setMovie(movieData)
        setReviews(reviewsData)
        setAverageRating(avgData)
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id])

  const handleDelete = async () => {
    if (!window.confirm('Supprimer ce film ?')) return
    try {
      await deleteMovie(id)
      navigate('/movies')
    } catch (err) {
      alert(err.message || 'Échec de la suppression')
    }
  }

  if (loading) return <p className="movie-detail">Chargement...</p>
  if (error) return <p className="error movie-detail">Erreur: {error}</p>
  if (!movie) return <p className="movie-detail">Film non trouvé</p>

  return (
    <div className="movie-detail">
      <nav className="movie-detail-nav">
        <Link to="/movies">← Films</Link>
      </nav>

      <article>
        <h1>{movie.title}</h1>
        <p className="meta">
          {movie.director} · {movie.releaseYear} · {movie.genre}
        </p>

        <div
          className="movie-rating"
          aria-label={
            averageRating > 0
              ? `Note moyenne ${averageRating.toFixed(1)} sur 5`
              : 'Pas encore de note'
          }
        >
          <StarRating rating={averageRating} size="lg" />
          {averageRating > 0 ? (
            <span className="movie-rating-value">{averageRating.toFixed(1)} / 5</span>
          ) : (
            <span className="movie-rating-value">Pas encore noté</span>
          )}
        </div>

        <p className="synopsis">{movie.synopsis}</p>

        {token && (
          <div className="movie-detail-actions">
            <Link to={`/movies/${id}/edit`}>Modifier</Link>
            <button type="button" onClick={handleDelete} className="btn-danger delete-button">
              Supprimer
            </button>
          </div>
        )}
      </article>

      <section className="reviews-section">
        <h2>Critiques ({reviews.length})</h2>

        {reviews.length === 0 ? (
          <p className="reviews-empty">Aucune critique pour ce film.</p>
        ) : (
          <div className="reviews-list-scroll">
            <ul className="reviews-list">
              {reviews.map((review) => (
                <li key={review.id} className="review-card">
                  <div className="review-meta">
                    <span className="review-author">{review.username}</span>
                    {review.createdAt && (
                      <time className="review-date" dateTime={review.createdAt}>
                        {formatDate(review.createdAt)}
                      </time>
                    )}
                  </div>
                  <p className="review-content">{review.content}</p>
                </li>
              ))}
            </ul>
          </div>
        )}
      </section>
    </div>
  )
}

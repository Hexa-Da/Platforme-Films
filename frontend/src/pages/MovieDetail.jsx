import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { deleteMovie, getMovie } from '../api'
import './MovieDetail.css'

export default function MovieDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [movie, setMovie] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const token = localStorage.getItem('token')

  useEffect(() => {
    getMovie(id)
      .then(setMovie)
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
        {token && <Link to="/profile">Mon profil</Link>}
        {token && <Link to="/movies/new">Ajouter un film</Link>}
      </nav>

      <article>
        <h1>{movie.title}</h1>
        <p className="meta">
          {movie.director} · {movie.releaseYear} · {movie.genre}
        </p>
        <p className="synopsis">{movie.synopsis}</p>

        {token && (
          <div className="movie-detail-actions">
            <Link to={`/movies/${id}/edit`}>Modifier</Link>
            <button type="button" onClick={handleDelete} className="delete-button">
              Supprimer
            </button>
          </div>
        )}
      </article>
    </div>
  )
}

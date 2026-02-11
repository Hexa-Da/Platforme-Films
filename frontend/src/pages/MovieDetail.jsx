import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getMovie } from '../api'
import './MovieDetail.css'

export default function MovieDetail() {
  const { id } = useParams()
  const [movie, setMovie] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getMovie(id)
      .then(setMovie)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <p>Chargement...</p>
  if (error) return <p className="error">Erreur: {error}</p>
  if (!movie) return <p>Film non trouvé</p>

  return (
    <div className="movie-detail">
      <Link to="/movies">← Retour aux films</Link>
      <article>
        <h1>{movie.title}</h1>
        <p className="meta">
          {movie.director} · {movie.releaseYear} · {movie.genre}
        </p>
        <p className="synopsis">{movie.synopsis}</p>
      </article>
    </div>
  )
}

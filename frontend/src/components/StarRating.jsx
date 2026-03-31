import './StarRating.css'

/**
 * Fraction de l’étoile d’index 0..4 remplie pour une note sur 5 (ex. 4.2 → [1,1,1,1,0.2]).
 */
export function starFillFraction(rating, index) {
  // Force un pas de 0.1 : 4.2 => 20% de la 5e étoile.
  const raw = typeof rating === 'string' ? rating.replace(',', '.') : rating
  const normalized = Math.max(0, Math.min(5, Math.round((Number(raw) || 0) * 10) / 10))
  return Math.min(1, Math.max(0, normalized - index))
}

/**
 * @param {number} rating — note sur 5 (peut être décimale)
 * @param {'sm' | 'md' | 'lg'} [size] — taille des étoiles
 * @param {string} [className] — classe sur le conteneur
 */
export default function StarRating({ rating, size = 'md', className = '' }) {
  const r = Number(rating) || 0
  const sizeClass = size === 'sm' ? 'star-rating--sm' : size === 'lg' ? 'star-rating--lg' : 'star-rating--md'

  return (
    <div className={`star-rating-row ${sizeClass} ${className}`.trim()} aria-hidden>
      {[0, 1, 2, 3, 4].map((i) => {
        const fill = starFillFraction(r, i)
        return (
          <span key={i} className="star-partial">
            <span className="star-partial-bg">&#9733;</span>
            <span
              className="star-partial-fill"
              style={{ width: `${fill * 100}%` }}
            >
              &#9733;
            </span>
          </span>
        )
      })}
    </div>
  )
}
